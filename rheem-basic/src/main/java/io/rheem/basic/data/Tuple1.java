package io.rheem.basic.data;

import io.rheem.core.api.exception.RheemException;

public class Tuple1<T0> extends RheemTuple {

    private final T0 field0;

    public Tuple1() {
        this.field0 = null;
    }

    public Tuple1(T0 field0) {
        this.field0 = field0;
    }

    public T0 getField0() {
        return this.field0;
    }

    @Override
    public int arity() {
        return 1;
    }

    @Override
    public Object[] toArray() {
        return convertToArray(this.field0);
    }

    @Override
    public RheemQuantum swap() {
        return new Tuple1<>(this.field0);
    }

    @Override
    public <T> T field(int index) {
        switch (index){
            case 0: return (T) this.field0;
            default:
                throw new RheemException(
                    String.format(
                        "index (%d) is out of boundering [0, %d]",
                        index,
                        this.arity()
                    )
                );
        }
    }

    @Override
    public RheemQuantum copy() {
        return new Tuple1<>(
            this.field0
        );
    }
}
