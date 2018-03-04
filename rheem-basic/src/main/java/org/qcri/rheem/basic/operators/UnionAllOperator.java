package org.qcri.rheem.basic.operators;

import org.apache.commons.lang3.Validate;
import org.qcri.rheem.core.api.Configuration;
import org.qcri.rheem.core.function.PredicateDescriptor;
import org.qcri.rheem.core.optimizer.cardinality.CardinalityEstimator;
import org.qcri.rheem.core.optimizer.cardinality.DefaultCardinalityEstimator;
import org.qcri.rheem.core.plan.rheemplan.BinaryToUnaryOperator;
import org.qcri.rheem.core.plan.rheemplan.Operator;
import org.qcri.rheem.core.plan.rheemplan.OutputSlot;
import org.qcri.rheem.core.types.DataSetType;

import java.util.Optional;


/**
 * This {@link Operator} creates the union (bag semantics) of two .
 */
public class UnionAllOperator<Type>
        extends BinaryToUnaryOperator<Type, Type, Type> {

    protected final PredicateDescriptor<Type> predicateDescriptor;

    /**
     * Creates a new instance.
     *
     * @param type the type of the datasets to be coalesced
     */
    public UnionAllOperator(DataSetType<Type> type, PredicateDescriptor<Type> predicateDescriptor) {
        super(type, type, type, false);
        this.predicateDescriptor = predicateDescriptor;
    }

    public UnionAllOperator(DataSetType<Type> type) {
        this(type, null);
    }

    /**
     * Creates a new instance.
     *
     * @param typeClass the type of the datasets to be coalesced
     */
    public UnionAllOperator(Class<Type> typeClass) {
        this(DataSetType.createDefault(typeClass), null);
    }

    /**
     * Copies an instance (exclusive of broadcasts).
     *
     * @param that that should be copied
     */
    public UnionAllOperator(UnionAllOperator<Type> that) {
        super(that);
        this.predicateDescriptor = that.getPredicateDescriptor();
    }

    public PredicateDescriptor<Type> getPredicateDescriptor() {
        return this.predicateDescriptor;
    }

    public String getSelectKeyString(){
        if (this.getPredicateDescriptor() != null && this.getPredicateDescriptor().getUdfSelectivity() != null){
            return this.getPredicateDescriptor().getUdfSelectivityKeyString();
        } else {
            return "";
        }
    }

    @Override
    public Optional<CardinalityEstimator> createCardinalityEstimator(
            final int outputIndex,
            final Configuration configuration) {
        Validate.inclusiveBetween(0, this.getNumOutputs() - 1, outputIndex);
        return Optional.of(new DefaultCardinalityEstimator(1d, 2, this.isSupportingBroadcastInputs(),
                inputCards -> inputCards[0] + inputCards[1]));
    }

    public OutputSlot<?> getOutput() {
        return this.getOutput(0);
    }
}
