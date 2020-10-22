package io.rheem.basic.data;

import io.rheem.core.api.exception.RheemException;

public class Tuple0 extends RheemTuple{

    @Override
    public int arity() {
        return 0;
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public RheemQuantum swap() {
        return new Tuple0();
    }

    @Override
    public <T> T field(int index) {
        throw new RheemException(
            new IndexOutOfBoundsException(
                "the Tuple0 can't invoque the method field"
            )
        );
    }

    @Override
    public RheemQuantum copy() {
        return new Tuple0();
    }
}
