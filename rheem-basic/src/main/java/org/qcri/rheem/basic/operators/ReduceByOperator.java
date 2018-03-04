package org.qcri.rheem.basic.operators;

import org.apache.commons.lang3.Validate;
import org.qcri.rheem.core.api.Configuration;
import org.qcri.rheem.core.function.FunctionDescriptor;
import org.qcri.rheem.core.function.PredicateDescriptor;
import org.qcri.rheem.core.function.ReduceDescriptor;
import org.qcri.rheem.core.function.TransformationDescriptor;
import org.qcri.rheem.core.optimizer.OptimizationContext;
import org.qcri.rheem.core.optimizer.ProbabilisticDoubleInterval;
import org.qcri.rheem.core.optimizer.cardinality.CardinalityEstimate;
import org.qcri.rheem.core.optimizer.cardinality.CardinalityEstimator;
import org.qcri.rheem.core.optimizer.cardinality.DefaultCardinalityEstimator;
import org.qcri.rheem.core.plan.rheemplan.UnaryToUnaryOperator;
import org.qcri.rheem.core.types.DataSetType;

import java.util.Optional;

/**
 * This operator groups the elements of a data set and aggregates the groups.
 */
public class ReduceByOperator<Type, Key> extends UnaryToUnaryOperator<Type, Type> {

    protected final TransformationDescriptor<Type, Key> keyDescriptor;

    protected final ReduceDescriptor<Type> reduceDescriptor;

    /**
     * Creates a new instance.
     */
    public ReduceByOperator(FunctionDescriptor.SerializableFunction<Type, Key> keyFunction,
                            FunctionDescriptor.SerializableBinaryOperator<Type> reduceDescriptor,
                            Class<Key> keyClass,
                            Class<Type> typeClass) {
        this(new TransformationDescriptor<>(keyFunction, typeClass, keyClass),
                new ReduceDescriptor<>(reduceDescriptor, typeClass));
    }

    /**
     * Creates a new instance.
     *
     * @param keyDescriptor    describes how to extract the key from data units
     * @param reduceDescriptor describes the reduction to be performed on the elements
     */
    public ReduceByOperator(TransformationDescriptor<Type, Key> keyDescriptor,
                            ReduceDescriptor<Type> reduceDescriptor) {
        this(keyDescriptor, reduceDescriptor, DataSetType.createDefault(keyDescriptor.getInputType()));
    }

    /**
     * Creates a new instance.
     *
     * @param keyDescriptor    describes how to extract the key from data units
     * @param reduceDescriptor describes the reduction to be performed on the elements
     * @param type             type of the reduce elements (i.e., type of {@link #getInput()} and {@link #getOutput()})
     */
    public ReduceByOperator(TransformationDescriptor<Type, Key> keyDescriptor,
                            ReduceDescriptor<Type> reduceDescriptor,
                            DataSetType<Type> type) {
        super(type, type, true);
        this.keyDescriptor = keyDescriptor;
        this.reduceDescriptor = reduceDescriptor;
    }

    /**
     * Copies an instance (exclusive of broadcasts).
     *
     * @param that that should be copied
     */
    public ReduceByOperator(ReduceByOperator<Type, Key> that) {
        super(that);
        this.keyDescriptor = that.getKeyDescriptor();
        this.reduceDescriptor = that.getReduceDescriptor();
    }

    public DataSetType<Type> getType() {
        return this.getInputType();
    }

    public TransformationDescriptor<Type, Key> getKeyDescriptor() {
        return this.keyDescriptor;
    }

    public ReduceDescriptor<Type> getReduceDescriptor() {
        return this.reduceDescriptor;
    }

    public String getSelectKeyString(){
        if (this.getReduceDescriptor().getUdfSelectivity() != null){
            return this.getReduceDescriptor().getUdfSelectivityKeyString();
        } else {
            return "";
        }
    }


    @Override
    public Optional<org.qcri.rheem.core.optimizer.cardinality.CardinalityEstimator> createCardinalityEstimator(
            final int outputIndex,
            final Configuration configuration) {
        Validate.inclusiveBetween(0, this.getNumOutputs() - 1, outputIndex);
        // TODO: Come up with a decent way to estimate the "distinctness" of reduction keys.
        return Optional.of(new ReduceByOperator.CardinalityEstimator(configuration));
    }


    private class CardinalityEstimator implements org.qcri.rheem.core.optimizer.cardinality.CardinalityEstimator {

        /**
         * The expected selectivity to be applied in this instance.
         */
        private final ProbabilisticDoubleInterval selectivity;
        Configuration configuration; //1

        public CardinalityEstimator(Configuration configuration) {
            this.selectivity = configuration.getUdfSelectivityProvider().provideFor(ReduceByOperator.this.reduceDescriptor);
            this.configuration = configuration; //2
        }

        @Override
        public CardinalityEstimate estimate(OptimizationContext optimizationContext, CardinalityEstimate... inputEstimates) {
            Validate.isTrue(inputEstimates.length == ReduceByOperator.this.getNumInputs());
            final CardinalityEstimate inputEstimate = inputEstimates[0];

            //3

                String mode = this.configuration.getStringProperty("rheem.optimizer.sr.mode", "best");
                if (mode.equals("best")){
                    mode = this.selectivity.getBest();
                }



            if (mode.equals("lin")) {
                return new CardinalityEstimate(
                        (long)  Math.max(0, ((inputEstimate.getLowerEstimate() * this.selectivity.getCoeff() + this.selectivity.getIntercept()) * inputEstimate.getLowerEstimate())),
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
