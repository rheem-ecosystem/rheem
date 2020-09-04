package io.rheem.core.plan.rheemplan.test;

import io.rheem.core.api.Configuration;
import io.rheem.core.optimizer.cardinality.CardinalityEstimator;
import io.rheem.core.optimizer.cardinality.FixedSizeCardinalityEstimator;
import io.rheem.core.plan.rheemplan.UnarySource;
import io.rheem.core.types.DataSetType;
import io.rheem.core.types.DataUnitType;

import java.util.Optional;

/**
 * Dummy source for testing purposes.
 */
public class TestSource<T> extends UnarySource<T> {

    private CardinalityEstimator cardinalityEstimator = new FixedSizeCardinalityEstimator(100);

    public TestSource(DataSetType outputType) {
        super(outputType);
    }

    public TestSource(Class<?> dataQuantumClass) {
        this(DataSetType.createDefault(DataUnitType.createBasic(dataQuantumClass)));
    }

    @Override
    public Optional<CardinalityEstimator> createCardinalityEstimator(int outputIndex, Configuration configuration) {
        return Optional.ofNullable(this.cardinalityEstimator);
    }

    public void setCardinalityEstimators(CardinalityEstimator cardinalityEstimators) {
        this.cardinalityEstimator = cardinalityEstimators;
    }
}
