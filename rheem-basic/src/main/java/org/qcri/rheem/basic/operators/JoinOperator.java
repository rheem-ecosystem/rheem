package org.qcri.rheem.basic.operators;

import org.apache.commons.lang3.Validate;
import org.qcri.rheem.basic.data.Tuple2;
import org.qcri.rheem.core.api.Configuration;
import org.qcri.rheem.core.function.FunctionDescriptor;
import org.qcri.rheem.core.function.TransformationDescriptor;
import org.qcri.rheem.core.optimizer.OptimizationContext;
import org.qcri.rheem.core.optimizer.ProbabilisticDoubleInterval;
import org.qcri.rheem.core.optimizer.cardinality.CardinalityEstimate;
import org.qcri.rheem.core.optimizer.cardinality.CardinalityEstimator;
import org.qcri.rheem.core.optimizer.cardinality.DefaultCardinalityEstimator;
import org.qcri.rheem.core.plan.rheemplan.BinaryToUnaryOperator;
import org.qcri.rheem.core.types.DataSetType;

import java.util.Optional;


/**
 * This operator returns the cartesian product of elements of input datasets.
 */
public class JoinOperator<InputType0, InputType1, Key>
        extends BinaryToUnaryOperator<InputType0, InputType1, Tuple2<InputType0, InputType1>> {

    private static <InputType0, InputType1> DataSetType<Tuple2<InputType0, InputType1>> createOutputDataSetType() {
        return DataSetType.createDefaultUnchecked(Tuple2.class);
    }

    protected final TransformationDescriptor<InputType0, Key> keyDescriptor0;

    protected final TransformationDescriptor<InputType1, Key> keyDescriptor1;

    public JoinOperator(FunctionDescriptor.SerializableFunction<InputType0, Key> keyExtractor0,
                        FunctionDescriptor.SerializableFunction<InputType1, Key> keyExtractor1,
                        Class<InputType0> input0Class,
                        Class<InputType1> input1Class,
                        Class<Key> keyClass) {
        this(
                new TransformationDescriptor<>(keyExtractor0, input0Class, keyClass),
                new TransformationDescriptor<>(keyExtractor1, input1Class, keyClass)
        );
    }

    public JoinOperator(TransformationDescriptor<InputType0, Key> keyDescriptor0,
                        TransformationDescriptor<InputType1, Key> keyDescriptor1) {
        super(DataSetType.createDefault(keyDescriptor0.getInputType()),
                DataSetType.createDefault(keyDescriptor1.getInputType()),
                JoinOperator.createOutputDataSetType(),
                true);
        this.keyDescriptor0 = keyDescriptor0;
        this.keyDescriptor1 = keyDescriptor1;
    }
    public JoinOperator(TransformationDescriptor<InputType0, Key> keyDescriptor0,
                        TransformationDescriptor<InputType1, Key> keyDescriptor1,
                        DataSetType<InputType0> inputType0,
                        DataSetType<InputType1> inputType1) {
        super(inputType0, inputType1, JoinOperator.createOutputDataSetType(), true);
        this.keyDescriptor0 = keyDescriptor0;
        this.keyDescriptor1 = keyDescriptor1;
    }


    /**
     * Copies an instance (exclusive of broadcasts).
     *
     * @param that that should be copied
     */
    public JoinOperator(JoinOperator<InputType0, InputType1, Key> that) {
        super(that);
        this.keyDescriptor0 = that.getKeyDescriptor0();
        this.keyDescriptor1 = that.getKeyDescriptor1();
    }

    public TransformationDescriptor<InputType0, Key> getKeyDescriptor0() {
        return this.keyDescriptor0;
    }

    public String getSelectKeyString(){
        if (this.getKeyDescriptor0().getUdfSelectivity() != null){
            return this.getKeyDescriptor0().getUdfSelectivityKeyString();
        } else {
            return "";
        }
    }

    public TransformationDescriptor<InputType1, Key> getKeyDescriptor1() {
        return this.keyDescriptor1;
    }


//    @Override
//    public Optional<CardinalityEstimator> createCardinalityEstimator(
//            final int outputIndex,
//            final Configuration configuration) {
//        Validate.inclusiveBetween(0, this.getNumOutputs() - 1, outputIndex);
//        // The current idea: We assume, we have a foreign-key like join
//        // TODO: Find a better estimator.
//        return Optional.of(new DefaultCardinalityEstimator(
//                .5d, 2, this.isSupportingBroadcastInputs(),
//                inputCards -> 3 * Math.max(inputCards[0], inputCards[1])
//        ));
//    }





    @Override
    public Optional<org.qcri.rheem.core.optimizer.cardinality.CardinalityEstimator> createCardinalityEstimator(
            final int outputIndex,
            final Configuration configuration) {
        Validate.inclusiveBetween(0, this.getNumOutputs() - 1, outputIndex);
        return Optional.of(new JoinOperator.CardinalityEstimator(configuration));
    }


    private class CardinalityEstimator implements org.qcri.rheem.core.optimizer.cardinality.CardinalityEstimator {

        /**
         * The expected selectivity to be applied in this instance.
         */
        private final ProbabilisticDoubleInterval selectivity;
        Configuration configuration;

        public CardinalityEstimator(Configuration configuration) {
            this.selectivity = configuration.getUdfSelectivityProvider().provideFor(JoinOperator.this.keyDescriptor0); // TODO JRK: What about the other one?
            this.configuration = configuration;
        }

        @Override
        public CardinalityEstimate estimate(OptimizationContext optimizationContext, CardinalityEstimate... inputEstimates) {
            Validate.isTrue(inputEstimates.length == JoinOperator.this.getNumInputs());
            final CardinalityEstimate inputEstimate0 = inputEstimates[0];
            final CardinalityEstimate inputEstimate1 = inputEstimates[1];
            final double max_lower_estimate =  Math.max(inputEstimate0.getLowerEstimate(), inputEstimate1.getLowerEstimate());
            final double max_upper_estimate =  Math.max(inputEstimate0.getUpperEstimate(), inputEstimate1.getUpperEstimate());

            String mode = this.configuration.getStringProperty("rheem.optimizer.sr.mode", "best");
            if (mode.equals("best")){
                mode = this.selectivity.getBest();
            }

                if (mode.equals("lin")) {
                return new CardinalityEstimate(
                        (long) Math.max(0, ((max_lower_estimate * this.selectivity.getCoeff() + this.selectivity.getIntercept()) * max_lower_estimate)),
                        (long) Math.max(0, ((max_upper_estimate * this.selectivity.getCoeff() + this.selectivity.getIntercept()) * max_upper_estimate)),
                        inputEstimate0.getCorrectnessProbability() * this.selectivity.getCorrectnessProbability()
                );
            } else if (mode.equals("log")) {
                return new CardinalityEstimate(
                        (long) Math.max(0, ((Math.log(max_lower_estimate) * this.selectivity.getLog_coeff() + this.selectivity.getLog_intercept()) * max_lower_estimate)),
                        (long) Math.max(0, ((Math.log(max_upper_estimate) * this.selectivity.getLog_coeff() + this.selectivity.getLog_intercept()) * max_upper_estimate)),
                        inputEstimate0.getCorrectnessProbability() * this.selectivity.getCorrectnessProbability()
                );
            } else {
                return new CardinalityEstimate(
                        (long) Math.max(0, (max_lower_estimate * this.selectivity.getLowerEstimate())),
                        (long) Math.max(0, (max_upper_estimate * this.selectivity.getUpperEstimate())),
                        inputEstimate0.getCorrectnessProbability() * this.selectivity.getCorrectnessProbability()
                );
            }
        }
    }
}
