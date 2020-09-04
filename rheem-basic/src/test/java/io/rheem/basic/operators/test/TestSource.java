package io.rheem.basic.operators.test;

import io.rheem.core.plan.rheemplan.UnarySource;
import io.rheem.core.types.DataSetType;

/**
 * Dummy source for testing purposes.
 */
public class TestSource<T> extends UnarySource<T> {

    public TestSource(DataSetType outputType) {
        super(outputType);
    }

}
