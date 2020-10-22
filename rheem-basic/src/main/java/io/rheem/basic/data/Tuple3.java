package io.rheem.basic.data;

import io.rheem.core.api.exception.RheemException;

import java.io.Serializable;

public class Tuple3<T0, T1, T2> extends RheemTuple implements Serializable {

    private final T0 field0;

    private final T1 field1;

    private final T2 field2;

    public Tuple3() {
        this.field0 = null;
        this.field1 = null;
        this.field2 = null;
    }

    public Tuple3(T0 field0, T1 field1, T2 field2) {
        this.field0 = field0;
        this.field1 = field1;
        this.field2 = field2;
    }

    public T0 getField0() {
        return this.field0;
    }

    public T1 getField1() {
        return this.field1;
    }

    public T2 getField2() {
        return this.field2;
    }

    @Override
    public int arity() {
        return 3;
    }

    @Override
    public Object[] toArray() {
        return convertToArray(
            this.field0,
            this.field1
        );
    }

    /**
     * @return a new instance with the fields of this instance swapped
     */
    public Tuple3<T2, T1, T0> swap() {
        return new Tuple3<>(
            this.field2,
            this.field1,
            this.field0
        );
    }

    @Override
    public <T> T field(int index) {
        switch (index){
            case 0: return (T) this.field0;
            case 1: return (T) this.field1;
            case 2: return (T) this.field2;
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
        return new Tuple3<>(
            this.field0,
            this.field1,
            this.field2
        );
    }
}
