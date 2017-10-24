package org.qcri.rheem.basic.operators;

import org.apache.commons.lang3.Validate;
import org.qcri.rheem.core.api.Configuration;
import org.qcri.rheem.core.function.DistinctPredicateDescriptor;
import org.qcri.rheem.core.function.PredicateDescriptor;
import org.qcri.rheem.core.optimizer.OptimizationContext;
import org.qcri.rheem.core.optimizer.ProbabilisticDoubleInterval;
import org.qcri.rheem.core.optimizer.cardinality.CardinalityEstimate;
import org.qcri.rheem.core.optimizer.cardinality.CardinalityEstimator;
import org.qcri.rheem.core.optimizer.cardinality.DefaultCardinalityEstimator;
import org.qcri.rheem.core.plan.rheemplan.UnaryToUnaryOperator;
import org.qcri.rheem.core.types.DataSetType;

import java.util.Optional;


/**
 * This operator returns the distinct elements in this dataset.
 */
public class DistinctOperator<Type> extends UnaryToUnaryOperator<Type, Type> {

    protected final DistinctPredicateDescriptor<Type> predicateDescriptor;


    /**
     * Creates a new instance.
     *
     * @param type type of the dataunit elements
     */
    public DistinctOperator(DataSetType<Type> type) {
        super(type, type, false);
        this.predicateDescriptor = null;
    }

    public DistinctOperator(DataSetType<Type> type, DistinctPredicateDescriptor<Type> predicateDescriptor) {
        super(type, type, false);
        this.predicateDescriptor = predicateDescriptor;
    }

    /**
     * Creates a new instance.
     *
     * @param typeClass type of the dataunit elements
     */
    public DistinctOperator(Class<Type> typeClass) {
        this(DataSetType.createDefault(typeClass));
    }

    /**
     * Copies an instance (exclusive of broadcasts).
     *
     * @param that that should be copied
     */
    public DistinctOperator(DistinctOperator<Type> that) {
        super(that);
        this.predicateDescriptor = that.getPredicateDescriptor();
    }

    public DistinctPredicateDescriptor<Type> getPredicateDescriptor() {
        return this.predicateDescriptor;
    }

    public String getSelectKeyString(){
        if (this.getPredicateDescriptor() != null && this.getPredicateDescriptor().getUdfSelectivity() != null){
            return this.getPredicateDescriptor().getUdfSelectivityKeyString();
        } else {
            return "";
        }
    }


//        // Assume with a confidence of 0.7 that 70% of the data quanta are pairwise distinct.
//        return Optional.of(new DefaultCardinalityEstimator(0.7d, 1, this.isSupportingBroadcastInputs(),
//                inputCards -> (long) (inputCards[0] * 0.7d))); // TODO JRK: Do not make baseline worse



    @Override
    public Optional<org.qcri.rheem.core.optimizer.cardinality.CardinalityEstimator> createCardinalityEstimator(
            final int outputIndex,
            final Configuration configuration) {
        Validate.inclusiveBetween(0, this.getNumOutputs() - 1, outputIndex);
        return Optional.of(new DistinctOperator.CardinalityEstimator(configuration));
    }


    private class CardinalityEstimator implements org.qcri.rheem.core.optimizer.cardinality.CardinalityEstimator {

        /**
         * The expected selectivity to be applied in this instance.
         */
        private final ProbabilisticDoubleInterval selectivity;
        Configuration configuration;

        public CardinalityEstimator(Configuration configuration) {
            this.selectivity = configuration.getUdfSelectivityProvider().provideFor(DistinctOperator.this.predicateDescriptor);
            this.configuration = configuration;
        }

        @Override
        public CardinalityEstimate estimate(OptimizationContext optimizationContext, CardinalityEstimate... inputEstimates) {
            Validate.isTrue(inputEstimates.length == DistinctOperator.this.getNumInputs());
            final CardinalityEstimate inputEstimate = inputEstimates[0];

            String mode = this.configuration.getStringProperty("rheem.optimizer.sr.mode", "best");
            if (mode.equals("best")){
                mode = this.selectivity.getBest();
            }

            if (mode.equals("lin")) {
                return new CardinalityEstimate(
                        (long) Math.max(0, ((inputEstimate.getLowerEstimate() * this.selectivity.getCoeff() + this.selectivity.getIntercept()) * inputEstimate.getLowerEstimate())),
                        (long) Math.max(0, ((inputEstimate.getUpperEstimate() * this.selectivity.getCoeff() + this.selectivity.getIntercept()) * inputEstimate.getUpperEstimate())),
                        inputEstimate.getCorrectnessProbability() * this.selectivity.getCorrectnessProbability()
                );
            } else if (mode.equals("log")) {
                return new CardinalityEstimate(
                        (long) Math.max(0, ((Math.log(inputEstimate.getLowerEstimate()) * this.selectivity.getLog_coeff() + this.selectivity.getLog_intercept()) * inputEstimate.getLowerEstimate())),
                        (long) Math.max(0, ((Math.log(inputEstimate.getUpperEstimate()) * this.selectivity.getLog_coeff() + this.selectivity.getLog_intercept()) * inputEstimate.getUpperEstimate())),
                        inputEstimate.getCorrectnessProbability() * this.selectivity.getCorrectnessProbability()
                );
            } else {
                return new CardinalityEstimate(
                        (long) Math.max(0, (inputEstimate.getLowerEstimate() * this.selectivity.getLowerEstimate())),
                        (long) Math.max(0, (inputEstimate.getUpperEstimate() * this.selectivity.getUpperEstimate())),
                        inputEstimate.getCorrectnessProbability() * this.selectivity.getCorrectnessProbability()
                );
            }
        }
    }
}
