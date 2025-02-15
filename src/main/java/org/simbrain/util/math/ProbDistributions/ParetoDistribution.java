package org.simbrain.util.math.ProbDistributions;

import org.simbrain.util.SimbrainConstants.Polarity;
import org.simbrain.util.UserParameter;
import org.simbrain.util.math.ProbabilityDistribution;

import umontreal.iro.lecuyer.randvar.ParetoGen;

public class ParetoDistribution extends ProbabilityDistribution{

    @UserParameter(
            label = "Slope (\u03B1)",
            description = "The power of the distribution.",
            order = 1)
    private double slope = 2.0;

    @UserParameter(
            label = "Minimum",
            description = "The minimum value the distribution will produce. "
                    + "Note that floor should never be lower than minimum.",
            order = 2)
    private double min = 1.0;
    
    /**
     * For all but uniform, upper bound is only used in conjunction with
     * clipping, to truncate the distribution. So if clipping is false this
     * value is not used.
     */
    @UserParameter(
            label = "Floor",
            description = "An artificial minimum value set by the user.",
            order = 3, useSetter = true)
    private double floor = 1.0; // TODO: Note that floor should never be lower than minimum.

    /**
     * For all but uniform, lower bound is only used in conjunction with
     * clipping, to truncate the distribution. So if clipping is false this
     * value is not used.
     */
    @UserParameter(
            label = "Ceiling",
            description = "An artificial minimum value set by the user.",
            order = 4, useSetter = true)
    private double ceil = Double.POSITIVE_INFINITY;

    @UserParameter(
            label = "Clipping",
            description = "When clipping is enabled, the randomizer will reject outside the floor and ceiling values.",
            order = 5)
    private boolean clipping = false;

    private Polarity polarity = Polarity.BOTH;

    /**
     * Public constructor for reflection-based creation. You are encourage to use
     * the builder pattern provided for ProbabilityDistributions.
     */
    public ParetoDistribution() {
    }

    @Override
    public double nextRand() {
        return clipping(this,
                ParetoGen.nextDouble(DEFAULT_RANDOM_STREAM, slope, min),
                floor,
                ceil
                );
    }

    @Override
    public int nextRandInt() {
        return (int) nextRand();
    }

    @Override
    public ParetoDistribution deepCopy() {
        ParetoDistribution cpy = new ParetoDistribution();
        cpy.slope = this.slope;
        cpy.min = this.min;
        cpy.floor = this.floor;
        cpy.ceil = this.ceil;
        cpy.clipping = this.clipping;
        return cpy;
    }

    public double getSlope() {
        return slope;
    }

    public void setSlope(double slope) {
        this.slope = slope;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    @Override
    public String getName() {
        return "Pareto";
    }

    @Override
    public void setClipping(boolean clipping) {
        this.clipping = clipping;
    }

    @Override
    public void setUpperBound(double ceiling) {
        if(ceiling > min)
            this.ceil = ceiling;
    }

    @Override
    public void setLowerBound(double floor) {
        if(floor >= min)
            this.floor = floor;
    }

    public void setCeil(double ceiling) {setUpperBound(ceiling);} // For APE

    public void setFloor(double floor) {setLowerBound(floor);} // For APE

    @Override
    public void setPolarity(Polarity polarity) {
        this.polarity = polarity;
    }

    @Override
    public Polarity getPolarity() {
        return this.polarity;
    }

    public static ParetoDistributionBuilder builder() {
        return new ParetoDistributionBuilder();
    }

    public static ParetoDistribution create() {
        return new ParetoDistribution();
    }

    public static class ParetoDistributionBuilder
        extends ProbabilityDistributionBuilder<
            ParetoDistributionBuilder,
            ParetoDistribution> {

        ParetoDistribution product = new ParetoDistribution();

        public ParetoDistributionBuilder slope(double slope) {
            product.setSlope(slope);
            return this;
        }

        public ParetoDistributionBuilder min(double min) {
            product.setMin(min);
            return this;
        }

        @Override
        public ParetoDistribution build() {
            return product;
        }

        @Override
        protected ParetoDistribution product() {
            return product;
        }
    }

}