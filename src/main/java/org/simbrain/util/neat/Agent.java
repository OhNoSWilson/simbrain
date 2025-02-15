package org.simbrain.util.neat;

import org.simbrain.custom_sims.helper_classes.OdorWorldBuilder;
import org.simbrain.network.core.Network;
import org.simbrain.util.math.SimbrainRandomizer;
import org.simbrain.world.odorworld.OdorWorld;
import org.simbrain.world.odorworld.OdorWorldComponent;
import org.simbrain.world.odorworld.entities.EntityType;
import org.simbrain.world.odorworld.entities.OdorWorldEntity;

import static java.util.Objects.requireNonNull;

/**
 * Agent holds the Genome and the Network generated from that genome.
 * Provides a convenient abstraction for describing an agent in an environment
 * and tracking its fitness.
 *
 * The agents do not have to be embedded in a world.
 *
 * This is the phenotype expressed by a given genome / genotype.
 *
 * @author LeoYulinLi
 *
 */
public class Agent implements Comparable<Agent> {

    /**
     * Reference of the genome this instance uses
     */
    private Genome genome;

    /**
     * The network to be created from the genome
     */
    private Network net;

    // TODO: For now this is tied to odorworld but in the future the environment
    // should be abstracted

    /**
     * Reference to parent world.
     * TODO: Move this to a separate class.
     */
    private OdorWorld world = new OdorWorldBuilder(new OdorWorldComponent("Hi")).getWorld();

    /**
     * Reference to an agent in an odor world.
     */
    private OdorWorldEntity odorWorldAgent = new OdorWorldEntity(world, EntityType.MOUSE);

    /**
     * NEAT randomizer.
     */
    private SimbrainRandomizer rand;

    /**
     * The fitness to be evaluated from the evaluation method.
     */
    private Double fitness = null;

    public Agent(Genome genome, long seed) {
        this(genome);
        rand = new SimbrainRandomizer(seed);
    }

    /**
     * Create a instance and build the network from a genome.
     * @param genome A genome to build this instance
     */
    public Agent(Genome genome) {
        setGenome(genome);
        net = genome.buildNetwork();
    }

    public void setGenome(Genome genome) {
        this.genome = requireNonNull(genome);
    }

    @Override
    public String toString() {
        //TODO: If this is used, print fitness and maybe some genome info
        return net.toString();
    }

    public Genome getGenome() {
        return genome;
    }

    public Network getNet() {
        return net;
    }

    public OdorWorld getWorld() {
        world.setWrapAround(false); // TODO: move. not a place to set thing.
        return world;
    }

    public double getFitness() {
        if (fitness == null) { // TODO: remove. just to deal with a NullPointerException in worldTestingIteration
            return 0.0;
        }
        return fitness;
    }
    
    public SimbrainRandomizer getRandomizer() {
        return rand;
    }

    /**
     * Setting the fitness of this instance and the genome it uses.
     * @param fitness A fitness score
     */
    public void setFitness(double fitness) {
        this.fitness = fitness;
        genome.setFitness(fitness);
    }

    @Override
    public int compareTo(Agent o) {
        return this.fitness.compareTo(o.fitness);
    }
}
