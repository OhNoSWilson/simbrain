/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2005,2007 The Authors.  See http://www.simbrain.net/credits
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.simbrain.network.neuron_update_rules;

import org.simbrain.network.core.Neuron;
import org.simbrain.network.core.SpikingNeuronUpdateRule;
import org.simbrain.network.neuron_update_rules.interfaces.NoisyUpdateRule;
import org.simbrain.util.UserParameter;
import org.simbrain.util.math.ProbDistributions.UniformDistribution;
import org.simbrain.util.math.ProbabilityDistribution;


public class FitzhughNagumo extends SpikingNeuronUpdateRule implements NoisyUpdateRule {

    /**
     * W. - recovery variable
     */
    private double w;

    /**
     * V. - membrane potential
     */
    private double v;

    /**
     * Constant background current. KEEP
     */
    @UserParameter(
            label = "Background Current (nA)",
            description = "Background current to the cell.",
            increment = .1,
            order = 4)
    private double iBg = 1;

    /**
     * Threshold value to signal a spike. KEEP
     */
    @UserParameter(
            label = "Spike threshold",
            description = "Threshold value to signal a spike.",
            increment = .1,
            order = 5)
    private double threshold = 1.9;

    /**
     * Noise generator.
     */
    private ProbabilityDistribution noiseGenerator = UniformDistribution.create();

    /**
     * Add noise to the neuron.
     */
    private  boolean addNoise = false;

    /**
     * Recovery rate
     */
    @UserParameter(
            label = "A (Recovery Rate)",
            description = "Abstract measure of how much \"resource\" a cell is depleting in response to large changes in voltage.",
            increment = .1,
            order = 1)
    private double a = 0.08;

    /**
     * Recovery dependence on voltage.
     */
    @UserParameter(
            label = "B (Rec. Voltage Dependence)",
            description = "How much the recovery variable w depends on voltage.",
            increment = .1,
            order = 2)
    private double b = 1;

    /**
     * Recovery self-dependence.
     */
    @UserParameter(
            label = "C (Rec. Self Dependence)",
            description = "How quickly the recovery variable recovers to its baseline value.",
            increment = .1,
            order = 3)
    private double c = 0.8;

    @Override
    public FitzhughNagumo deepCopy() {
        FitzhughNagumo in = new FitzhughNagumo();
        in.setW(getW());
        in.setV(getV());
        in.setA(getA());
        in.setB(getB());
        in.setC(getC());
        in.setThreshold(getThreshold());
        in.setAddNoise(getAddNoise());
        in.setNoiseGenerator(noiseGenerator.deepCopy());
        return in;
    }

    @Override
    public void update(final Neuron neuron) {
        double timeStep = neuron.getNetwork().getTimeStep();
        //        final boolean refractory = getLastSpikeTime() + refractoryPeriod
        //                >= neuron.getNetwork().getTime();
        // final double activation = neuron.getActivation();
        double inputs = 0;
        inputs = neuron.getInput();
        if (addNoise) {
            inputs += noiseGenerator.getRandom();
        }
        inputs += iBg;
        v = neuron.getActivation();
        w += (timeStep * (a * (b * v + 0.7 - (c * w))));

        v += timeStep * (v - (v * v * v) / 3 - w + inputs);

        // v = activation + (timeStep * (activation - (Math.pow(activation, 3)/3) - w + inputs) );
        // You want this
        if (v >= threshold) {
            neuron.setSpkBuffer(true);
            setHasSpiked(true, neuron);
        } else {
            neuron.setSpkBuffer(false);
            setHasSpiked(false, neuron);
        }
        //till here
        neuron.setBuffer(v);
    }

    @Override
    public double getRandomValue() {
        // Equal chance of spiking or not spiking, taking on any value between
        // the resting potential and the threshold if not.
        return 2 * (threshold - c) * Math.random() + c;
    }

    public double getW() {
        return w;
    }

    public void setW(final double w) {
        this.w = w;
    }

    public double getV() {
        return v;
    }

    public void setV(final double v) {
        this.v = v;
    }

    public double getiBg() {
        return iBg;
    }

    public void setiBg(double iBg) {
        this.iBg = iBg;
    }

    public boolean getAddNoise() {
        return addNoise;
    }

    public void setAddNoise(final boolean addNoise) {
        this.addNoise = addNoise;
    }

    @Override
    public ProbabilityDistribution getNoiseGenerator() {
        return noiseGenerator;
    }

    @Override
    public void setNoiseGenerator(final ProbabilityDistribution noise) {
        this.noiseGenerator = noise;
    }

    @Override
    public String getName() {
        return "FitzhughNagumo";
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public void setA(double a) {
        this.a = a;
    }

    public void setB(double b) {
        this.b = b;
    }

    public void setC(double c) {
        this.c = c;
    }
}