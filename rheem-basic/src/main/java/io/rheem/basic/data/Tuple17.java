package io.rheem.basic.data;

import io.rheem.core.api.exception.RheemException;

import java.io.Serializable;

public class Tuple17<T0, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>
        extends RheemTuple implements Serializable {

    private final T0 field0;
    private final T1 field1;
    private final T2 field2;
    private final T3 field3;
    private final T4 field4;
    private final T5 field5;
    private final T6 field6;
    private final T7 field7;
    private final T8 field8;
    private final T9 field9;
    private final T10 field10;
    private final T11 field11;
    private final T12 field12;
    private final T13 field13;
    private final T14 field14;
    private final T15 field15;
    private final T16 field16;

    public Tuple17() {
        this.field0 = null;
        this.field1 = null;
        this.field2 = null;
        this.field3 = null;
        this.field4 = null;
        this.field5 = null;
        this.field6 = null;
        this.field7 = null;
        this.field8 = null;
        this.field9 = null;
        this.field10 = null;
        this.field11 = null;
        this.field12 = null;
        this.field13 = null;
        this.field14 = null;
        this.field15 = null;
        this.field16 = null;
    }

    public Tuple17(T0 field0, T1 field1, T2 field2, T3 field3, T4 field4, T5 field5,
                   T6 field6, T7 field7, T8 field8, T9 field9, T10 field10, T11 field11,
                   T12 field12, T13 field13, T14 field14, T15 field15, T16 field16
    ) {
        this.field0 = field0;
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.field4 = field4;
        this.field5 = field5;
        this.field6 = field6;
        this.field7 = field7;
        this.field8 = field8;
        this.field9 = field9;
        this.field10 = field10;
        this.field11 = field11;
        this.field12 = field12;
        this.field13 = field13;
        this.field14 = field14;
        this.field15 = field15;
        this.field16 = field16;
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

    public T4 getField4() {
        return this.field4;
    }

    public T5 getField5() {
        return this.field5;
    }

    public T6 getField6() {
        return this.field6;
    }

    public T7 getField7() {
        return this.field7;
    }

    public T8 getField8() {
        return this.field8;
    }

    public T9 getField9() {
        return this.field9;
    }

    public T10 getField10() {
        return this.field10;
    }

    public T11 getField11() {
        return this.field11;
    }

    public T12 getField12() {
        return this.field12;
    }

    public T13 getField13() {
        return this.field13;
    }

    public T14 getField14() {
        return this.field14;
    }

    public T15 getField15() {
        return this.field15;
    }

    public T16 getField16() {
        return this.field16;
    }

    @Override
    public int arity() {
        return 17;
    }

    @Override
    public Object[] toArray() {
        return convertToArray(
            this.field0,
            this.field1,
            this.field2,
            this.field3,
            this.field4,
            this.field5,
            this.field6,
            this.field7,
            this.field8,
            this.field9,
            this.field10,
            this.field11,
            this.field12,
            this.field13,
            this.field14,
            this.field15,
            this.field16
        );
    }

    /**
     * @return a new instance with the fields of this instance swapped
     */
    public Tuple17<T16, T15, T14, T13, T12, T11, T10, T9, T8, T7, T6, T5, T4, T3, T2, T1, T0> swap() {
        return new Tuple17<>(
            this.field16,
            this.field15,
            this.field14,
            this.field13,
            this.field12,
            this.field11,
            this.field10,
            this.field9,
            this.field8,
            this.field7,
            this.field6,
            this.field5,
            this.field4,
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
            case 4: return (T) this.field4;
            case 5: return (T) this.field5;
            case 6: return (T) this.field6;
            case 7: return (T) this.field7;
            case 8: return (T) this.field8;
            case 9: return (T) this.field9;
            case 10: return (T) this.field10;
            case 11: return (T) this.field11;
            case 12: return (T) this.field12;
            case 13: return (T) this.field13;
            case 14: return (T) this.field14;
            case 15: return (T) this.field15;
            case 16: return (T) this.field16;
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
        return new Tuple17<>(
            this.field0,
            this.field1,
            this.field2,
            this.field3,
            this.field4,
            this.field5,
            this.field6,
            this.field7,
            this.field8,
            this.field9,
            this.field10,
            this.field11,
            this.field12,
            this.field13,
            this.field14,
            this.field15,
            this.field16
        );
    }
}
