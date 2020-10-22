package io.rheem.basic.data;

import io.rheem.core.api.exception.RheemException;

import java.io.Serializable;

/**
 * A type for tuples. Might be replaced by existing classes for this purpose, such as from the Scala library.
 */
public class Tuple2<T0, T1> extends RheemTuple implements Serializable {

    public final T0 field0;

    public final T1 field1;

    public Tuple2() {
        this.field0 = null;
        this.field1 = null;
    }

    public Tuple2(T0 field0, T1 field1) {
        this.field0 = field0;
        this.field1 = field1;
    }

    public T0 getField0() {
        return this.field0;
    }

    public T1 getField1() {
        return this.field1;
    }

    @Override
    public int arity() {
        return 2;
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
    public Tuple2<T1, T0> swap() {
        return new Tuple2<>(this.field1, this.field0);
    }

    @Override
    public <T> T field(int index) {
        switch (index){
            case 0: return (T) this.field0;
            case 1: return (T) this.field1;
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
        return new Tuple2<>(
            this.field0,
            this.field1
        );
    }
}
