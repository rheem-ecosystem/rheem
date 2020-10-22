package io.rheem.basic.data;

import io.rheem.core.api.exception.RheemException;

import java.io.Serializable;

public class Tuple4<T0, T1, T2, T3>
        extends RheemTuple implements Serializable {

    private final T0 field0;
    private final T1 field1;
    private final T2 field2;
    private final T3 field3;

    public Tuple4() {
        this.field0 = null;
        this.field1 = null;
        this.field2 = null;
        this.field3 = null;
    }

    public Tuple4(T0 field0, T1 field1, T2 field2, T3 field3
    ) {
        this.field0 = field0;
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
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

    public T3 getField3() {
        return this.field3;
    }

    @Override
    public int arity() {
        return 4;
    }

    @Override
    public Object[] toArray() {
        return convertToArray(
            this.field0,
            this.field1,
            this.field2,
            this.field3
        );
    }

    /**
     * @return a new instance with the fields of this instance swapped
     */
    public Tuple4<T3, T2, T1, T0> swap() {
        return new Tuple4<>(
            this.field3,
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
            case 3: return (T) this.field3;
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
        return new Tuple4<>(
            this.field0,
            this.field1,
            this.field2,
            this.field3
        );
    }
}
