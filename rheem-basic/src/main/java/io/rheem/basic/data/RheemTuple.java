package io.rheem.basic.data;

import io.rheem.core.api.exception.RheemException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Objects;

public abstract class RheemTuple implements RheemQuantum {

    //TODO add this to a parameter
    protected static final int MAX_TUPLE_COMPONENTES = 25;
    
    public RheemTuple(){}

    @Override
    public RheemQuantum project(int[] fields, boolean validate) {
        if(validate){
            if(fields.length > this.arity()){
                throw new RheemException("The projection is not valide");
            }
            for(int i = 0; i < fields.length; i++){
                int position = fields[i];
                if(position < 0 || position > this.arity()){
                    throw new RheemException(
                        String.format(
                            "The field of the projection need to be between [%d, %d]",
                            0,
                            this.arity()
                        )
                    );
                }
            }
        }
        Object[] elements = new Object[fields.length];
        for(int i = 0; i < fields.length; i++){
            elements[i] = this.field(fields[i]);
        }
        return createTuple(elements);
    }

    @Override
    public RheemQuantum join(RheemQuantum other) {
        int final_size = this.arity() + other.arity();

        Object[] elements = new Object[final_size];

        System.arraycopy(this.toArray(), 0, elements, 0, this.arity());
        System.arraycopy(other.toArray(), 0, elements, this.arity(), other.arity());

        if(final_size > RheemTuple.MAX_TUPLE_COMPONENTES){
            return new Record(elements);
        }

        return createTuple(elements);
    }
    
    protected RheemTuple createTuple(Object[] elements){
        if(elements.length > RheemTuple.MAX_TUPLE_COMPONENTES) {
            throw new RheemException(
                String.format(
                    "It's not possible to create a tuple bigger of %d elements",
                    RheemTuple.MAX_TUPLE_COMPONENTES
                )
            );
        }
        switch (elements.length) {
            case 0:
                return new Tuple0();
            case 1:
                return new Tuple1(
                        elements[0]
                );
            case 2:
                return new Tuple2(
                        elements[0], elements[1]
                );
            case 3:
                return new Tuple3(
                        elements[0], elements[1], elements[2]
                );
            case 4:
                return new Tuple4(
                        elements[0], elements[1], elements[2], elements[3]
                );
            case 5:
                return new Tuple5(
                        elements[0], elements[1], elements[2], elements[3], elements[4]
                );
            case 6:
                return new Tuple6(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5]
                );
            case 7:
                return new Tuple7(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5], elements[6]
                );
            case 8:
                return new Tuple8(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5], elements[6], elements[7]
                );
            case 9:
                return new Tuple9(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5], elements[6], elements[7], elements[8]
                );
            case 10:
                return new Tuple10(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5], elements[6], elements[7], elements[8], elements[9]
                );
            case 11:
                return new Tuple11(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5], elements[6], elements[7], elements[8], elements[9],
                        elements[10]
                );
            case 12:
                return new Tuple12(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5], elements[6], elements[7], elements[8], elements[9],
                        elements[10], elements[11]
                );
            case 13:
                return new Tuple13(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5], elements[6], elements[7], elements[8], elements[9],
                        elements[10], elements[11], elements[12]
                );
            case 14:
                return new Tuple14(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5], elements[6], elements[7], elements[8], elements[9],
                        elements[10], elements[11], elements[12], elements[13]
                );
            case 15:
                return new Tuple15(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5], elements[6], elements[7], elements[8], elements[9],
                        elements[10], elements[11], elements[12], elements[13], elements[14]
                );
            case 16:
                return new Tuple16(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5], elements[6], elements[7], elements[8], elements[9],
                        elements[10], elements[11], elements[12], elements[13], elements[14],
                        elements[15]
                );
            case 17:
                return new Tuple17(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5], elements[6], elements[7], elements[8], elements[9],
                        elements[10], elements[11], elements[12], elements[13], elements[14],
                        elements[15], elements[16]
                );
            case 18:
                return new Tuple18(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5], elements[6], elements[7], elements[8], elements[9],
                        elements[10], elements[11], elements[12], elements[13], elements[14],
                        elements[15], elements[16], elements[17]
                );
            case 19:
                return new Tuple19(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5], elements[6], elements[7], elements[8], elements[9],
                        elements[10], elements[11], elements[12], elements[13], elements[14],
                        elements[15], elements[16], elements[17], elements[18]
                );
            case 20:
                return new Tuple20(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5], elements[6], elements[7], elements[8], elements[9],
                        elements[10], elements[11], elements[12], elements[13], elements[14],
                        elements[15], elements[16], elements[17], elements[18], elements[19]
                );
            case 21:
                return new Tuple21(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5], elements[6], elements[7], elements[8], elements[9],
                        elements[10], elements[11], elements[12], elements[13], elements[14],
                        elements[15], elements[16], elements[17], elements[18], elements[19],
                        elements[20]
                );
            case 22:
                return new Tuple22(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5], elements[6], elements[7], elements[8], elements[9],
                        elements[10], elements[11], elements[12], elements[13], elements[14],
                        elements[15], elements[16], elements[17], elements[18], elements[19],
                        elements[20], elements[21]
                );
            case 23:
                return new Tuple23(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5], elements[6], elements[7], elements[8], elements[9],
                        elements[10], elements[11], elements[12], elements[13], elements[14],
                        elements[15], elements[16], elements[17], elements[18], elements[19],
                        elements[20], elements[21], elements[22]
                );
            case 24:
                return new Tuple24(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5], elements[6], elements[7], elements[8], elements[9],
                        elements[10], elements[11], elements[12], elements[13], elements[14],
                        elements[15], elements[16], elements[17], elements[18], elements[19],
                        elements[20], elements[21], elements[22], elements[23]
                );
            case 25:
                return new Tuple25(
                        elements[0], elements[1], elements[2], elements[3], elements[4],
                        elements[5], elements[6], elements[7], elements[8], elements[9],
                        elements[10], elements[11], elements[12], elements[13], elements[14],
                        elements[15], elements[16], elements[17], elements[18], elements[19],
                        elements[20], elements[21], elements[22], elements[23], elements[24]
                );
            //TODO Maybe is possible to create a Record in the case where the size is more than 25
            default:
                throw new RheemException(
                        String.format(
                                "The Tuple size %d is not valid, please check the size of the projection",
                                elements.length
                        )
                );
        }
    }

    @Override
    public String fieldAsString(int index) {
        return this.field(index).toString();
    }

    @Override
    public boolean fieldAsBoolean(int index) {
        try {
            return (boolean) this.field(index);
        }catch (Exception e){
            throw createExceptionCasting(e, index, "boolean");
        }
    }

    @Override
    public byte[] fieldAsBytes(int index) {
        try{
            return convertToBytes(this.fieldAsBytes(index));
        }catch (Exception e){
            throw createExceptionCasting(e, index, "byte[]");
        }
    }

    @Override
    public char fieldAsChar(int index) {
        try {
            return (char) this.field(index);
        }catch (Exception e){
            throw createExceptionCasting(e, index, "char");
        }
    }

    @Override
    public short fieldAsShort(int index) {
        try {
            return (short) this.field(index);
        }catch (Exception e){
            throw createExceptionCasting(e, index, "short");
        }
    }

    @Override
    public int fieldAsInt(int index) {
        try {
            return (int) this.field(index);
        }catch (Exception e){
            throw createExceptionCasting(e, index, "int");
        }
    }

    @Override
    public long fieldAsLong(int index) {
        try {
            return (long) this.field(index);
        }catch (Exception e){
            throw createExceptionCasting(e, index, "long");
        }
    }

    @Override
    public float fieldAsFloat(int index) {
        try {
            return (float) this.field(index);
        }catch (Exception e){
            throw createExceptionCasting(e, index, "float");
        }
    }

    @Override
    public double fieldAsDouble(int index) {
        try {
            return (double) this.field(index);
        }catch (Exception e){
            throw createExceptionCasting(e, index, "double");
        }
    }

    protected byte[] convertToBytes(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);
        out.writeObject(object);
        return bos.toByteArray();
    }

    protected RheemException createExceptionCasting(Exception cause, int index, String type){
        return new RheemException(
            String.format(
                "the field %d in the tuple is not castable to %s",
                index,
                type
            ),
            cause
        );
    }

    @Override
    public String toString(){
        return "Tuple "+Arrays.deepToString(this.toArray());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        RheemTuple tuple = (RheemTuple) o;

        boolean value = true;
        for(int i = 0; i < this.arity(); i++ ){
            value = value && Objects.equals(this.field(i), tuple.field(i));
            if( !value ){
                return false;
            }
        }
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.toArray());
    }


}
