package org.qcri.rheem.basic.operators;

import org.apache.commons.lang3.Validate;
import org.qcri.rheem.core.api.Configuration;
import org.qcri.rheem.core.function.PredicateDescriptor;
import org.qcri.rheem.core.optimizer.OptimizationContext;
import org.qcri.rheem.core.optimizer.ProbabilisticDoubleInterval;
import org.qcri.rheem.core.optimizer.cardinality.CardinalityEstimate;
import org.qcri.rheem.core.plan.rheemplan.UnaryToUnaryOperator;
import org.qcri.rheem.core.types.DataSetType;

import java.util.Optional;


/**
 * This operator returns a new dataset after filtering by applying predicateDescriptor.
 */
public class FilterOperator<Type> extends UnaryToUnaryOperator<Type, Type> {

    /**
     * Function that this operator applies to the input elements.
     */
    protected final PredicateDescriptor<Type> predicateDescriptor;

    /**
     * Creates a new instance.
     */
    public FilterOperator(PredicateDescriptor.SerializablePredicate<Type> predicateDescriptor, Class<Type> typeClass) {
        this(new PredicateDescriptor<>(predicateDescriptor, typeClass));
    }

    /**
     * Creates a new instance.
     */
    public FilterOperator(PredicateDescriptor<Type> predicateDescriptor) {
        super(DataSetType.createDefault(predicateDescriptor.getInputType()),
                DataSetType.createDefault(predicateDescriptor.getInputType()),
                true);
        this.predicateDescriptor = predicateDescriptor;
    }

    /**
     * Creates a new instance.
     *
     * @param type type of the dataunit elements
     */
    public FilterOperator(DataSetType<Type> type, PredicateDescriptor.SerializablePredicate<Type> predicateDescriptor) {
        this(new PredicateDescriptor<>(predicateDescriptor, type.getDataUnitType().getTypeClass()), type);
    }

    /**
     * Creates a new instance.
     *
     * @param type type of the dataunit elements
     */
    public FilterOperator(PredicateDescriptor<Type> predicateDescriptor, DataSetType<Type> type) {
        super(type, type, true);
        this.predicateDescriptor = predicateDescriptor;
    }

    /**
     * Copies an instance (exclusive of broadcasts).
     *
     * @param that that should be copied
     */
    public FilterOperator(FilterOperator<Type> that) {
        super(that);
        this.predicateDescriptor = that.getPredicateDescriptor();
    }

    public PredicateDescriptor<Type> getPredicateDescriptor() {
        return this.predicateDescriptor;
    }

    public String getSelectKeyString(){
        if (this.getPredicateDescriptor().getUdfSelectivity() != null){
            return this.getPredicateDescriptor().getUdfSelectivityKeyString();
        } else {
            return "";
        }
    }

    @Override
    public Optional<org.qcri.rheem.core.optimizer.cardinality.CardinalityEstimator> createCardinalityEstimator(
            final int outputIndex,
            final Configuration configuration) {
        Validate.inclusiveBetween(0, this.getNumOutputs() - 1, outputIndex);
        return Optional.of(new FilterOperator.CardinalityEstimator(this.predicateDescriptor, configuration));
    }

    public DataSetType<Type> getType() {
        return this.getInputType();
    }

    /**
     * Custom {@link org.qcri.rheem.core.optimizer.cardinality.CardinalityEstimator} for {@link FilterOperator}s.
     */
    private class CardinalityEstimator implements org.qcri.rheem.core.optimizer.cardinality.CardinalityEstimator {

        /**
         * The expected selectivity to be applied in this instance.
         */
        private final ProbabilisticDoubleInterval selectivity;
        Configuration configuration;

        public CardinalityEstimator(PredicateDescriptor<?> predicateDescriptor, Configuration configuration) {
            this.selectivity = configuration.getUdfSelectivityProvider().provideFor(predicateDescriptor);
            this.configuration = configuration;
        }

        @Override
        public CardinalityEstimate estimate(OptimizationContext optimizationContext, CardinalityEstimate... inputEstimates) {
            Validate.isTrue(inputEstimates.length == FilterOperator.this.getNumInputs());
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
