package org.simbrain.custom_sims.simulations.test;

import org.simbrain.custom_sims.RegisteredSimulation;
import org.simbrain.custom_sims.helper_classes.OdorWorldBuilder;
import org.simbrain.network.NetworkComponent;
import org.simbrain.network.core.Network;
import org.simbrain.network.groups.NeuronGroup;
import org.simbrain.util.geneticalgorithm.Agent;
import org.simbrain.util.geneticalgorithm.Population;
import org.simbrain.util.neat2.NetworkGenome;
import org.simbrain.workspace.gui.SimbrainDesktop;
import org.simbrain.world.odorworld.OdorWorld;
import org.simbrain.world.odorworld.OdorWorldComponent;
import org.simbrain.world.odorworld.effectors.StraightMovement;
import org.simbrain.world.odorworld.effectors.Turning;
import org.simbrain.world.odorworld.entities.EntityType;
import org.simbrain.world.odorworld.entities.OdorWorldEntity;
import org.simbrain.world.odorworld.sensors.ObjectSensor;

/**
 * Try to evolve a network that pursues cheese in an environment.
 */
public class TestEvolution extends RegisteredSimulation {

    // TODO: Create a control panel and wrap population size, maxiterations, etc. into it

    /**
     * Default population size at each generation.
     */
    public int populationSize = 100;

    /**
     * The maximum number of generation.
     */
    public int maxIterations = 100;

    /**
     * If fitness rises above this threshold before maxiterations is reached, simulation terminates.
     */
    double fitnessThreshold = 3;

    public static int maxMove = 500;


    /**
     * Population of xor networks to evolve
     */
    private Population<NetworkGenome, Network> population;


    // Odor world stuff
    OdorWorldEntity mouse;
    OdorWorldEntity cheese, flower, fish;
    OdorWorldBuilder worldBuilder;

    /**
     * Construct sim
     */
    public TestEvolution() {
        super();
    }

    /**
     * @param desktop
     */
    public TestEvolution(SimbrainDesktop desktop) {
        super(desktop);
    }

    /**
     * Run the simulation
     */
    @Override
    public void run() {

        // Evolve the pursuer
        init();
        evolve();

        // Clear workspace
        sim.getWorkspace().clearWorkspace();

        // Add winning network
        Network winner = population.getFittestAgent().getPhenotype();
        sim.addNetwork(10, 10, 491, 534, new NetworkComponent("Evolved Pursuer", winner));

        // Add odor world
        createOdorWorld();

        // Create couplings
        NeuronGroup outputs = (NeuronGroup) winner.getGroupByLabel("outputs");
        sim.couple(outputs.getNeuron(0), mouse.getEffector("Move straight"));
        sim.couple(outputs.getNeuron(1), mouse.getEffector("Turn left"));
        sim.couple(outputs.getNeuron(2), mouse.getEffector("Turn right"));
        NeuronGroup inputs = (NeuronGroup) winner.getGroupByLabel("inputs");
        sim.couple((ObjectSensor) mouse.getSensors().get(0), inputs.getNeuron(0));
        sim.couple((ObjectSensor) mouse.getSensors().get(1), inputs.getNeuron(1));
        sim.couple((ObjectSensor) mouse.getSensors().get(2), inputs.getNeuron(2));

        // TODO: When the mouse gets the cheese, respawn to a new location

    }


    private void createOdorWorld() {

        worldBuilder = sim.addOdorWorldTMX(486, 14, 472, 516, "empty.tmx");
        worldBuilder.getWorld().setObjectsBlockMovement(false);

        mouse = worldBuilder.getWorld().addAgent();
        cheese = worldBuilder.addEntity(100, 100, EntityType.SWISS);
        worldBuilder.getWorld().update();

    }


    public static Double eval(Agent<NetworkGenome, Network> agent) {

        // How many cheeses the agent eats
        double score = 0;

        OdorWorldComponent odorWorldComponent = new OdorWorldComponent("agent world");
        OdorWorld odorWorld = odorWorldComponent.getWorld();
        OdorWorldEntity mouse = odorWorld.addAgent();
        mouse.setLocation(odorWorld.getWidth() / 2, odorWorld.getHeight() / 2);
        OdorWorldEntity cheese = odorWorld.addEntity();

        double x = mouse.getCenterX() + agent.getRandomizer().nextDouble(32, 64);
        x *= agent.getRandomizer().nextBoolean() ? 1 : -1;
        if (x < 0) {
            x = 0;
        } else if (x > odorWorld.getWidth() - cheese.getEntityType().getImageWidth()) {
            x = odorWorld.getWidth() - cheese.getEntityType().getImageWidth();
        }

        double y = mouse.getCenterY() + agent.getRandomizer().nextDouble(32, 64);
        y *= agent.getRandomizer().nextBoolean() ? 1 : -1;
        if (y < 0) {
            y = 0;
        } else if (y > odorWorld.getHeight() - cheese.getEntityType().getImageHeight()) {
            y = odorWorld.getHeight() - cheese.getEntityType().getImageHeight();
        }
        cheese.setLocation(x, y);

        for (int i = 0; i < maxMove; i++) {

            // sensing
            NeuronGroup inputs = (NeuronGroup) agent.getPhenotype().getGroupByLabel("inputs");
            for (int j = 0; j < inputs.size(); j++) {
                ObjectSensor os = (ObjectSensor) mouse.getSensors().get(j);
                inputs.getNeuronList().get(j).forceSetActivation(os.getCurrentValue());
            }

            // "planning"
            agent.getPhenotype().update();

            // acting
            NeuronGroup outputs = (NeuronGroup) agent.getPhenotype().getGroupByLabel("outputs");
            ((StraightMovement) mouse.getEffector("Move Straight")).setAmount(outputs.getActivations()[0]);
            ((Turning) mouse.getEffector("Turn Left")).setAmount(outputs.getActivations()[1]);
            ((Turning) mouse.getEffector("Turn Right")).setAmount(outputs.getActivations()[2]);

            odorWorld.update();

            if (mouse.isInRadius(cheese, 28)) {
                score += 1;
                x = mouse.getCenterX() + agent.getRandomizer().nextDouble(-64, 64);
                x *= agent.getRandomizer().nextBoolean() ? 1 : -1;
                if (x < 0) {
                    x = 0;
                } else if (x > odorWorld.getWidth() - cheese.getEntityType().getImageWidth()) {
                    x = odorWorld.getWidth() - cheese.getEntityType().getImageWidth();
                }

                y = mouse.getCenterY() + agent.getRandomizer().nextDouble(-64, 64);
                y *= agent.getRandomizer().nextBoolean() ? 1 : -1;
                if (y < 0) {
                    y = 0;
                } else if (y > odorWorld.getHeight() - cheese.getEntityType().getImageHeight()) {
                    y = odorWorld.getHeight() - cheese.getEntityType().getImageHeight();
                }

                cheese.setLocation(x, y);
            }

        }
        double distanceToCheese = mouse.getRadiusTo(cheese);
        score += distanceToCheese < 48 ? (48 - distanceToCheese) / 48 : 0;

        return score;
    }

    /**
     * Initialize the population of networks.
     */
    public void init() {
        population = new Population<>(this.populationSize, System.nanoTime());
        NetworkGenome.Configuration configuration = new NetworkGenome.Configuration();
        configuration.setNumInputs(3);
        configuration.setNumOutputs(3);
        configuration.setAllowSelfConnection(false);
        configuration.setMaxNode(50);
        configuration.setNodeMaxBias(1);
        Agent<NetworkGenome, Network> prototype = new Agent<>(new NetworkGenome(configuration), TestEvolution::eval);
        population.populate(prototype);
    }

    /**
     * Run the simulation.
     */
    public void evolve() {
        for (int i = 0; i < maxIterations; i++) {
            double bestFitness = population.computeNewFitness();
            System.out.println(i + ", fitness = " + bestFitness);
            if (bestFitness > fitnessThreshold) {
                break;
            }
            population.replenish();
        }
    }


    @Override
    public String getName() {
        return "Test Evolution";
    }

    @Override
    public TestEvolution instantiate(SimbrainDesktop desktop) {
        return new TestEvolution(desktop);
    }

}
