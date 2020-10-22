package io.rheem.basic.data;

import io.rheem.core.api.exception.RheemException;
import io.rheem.core.util.ReflectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Arrays;

/**
 * A Type that represents a record with a schema, might be replaced with something standard like JPA entity.
 */
public class Record implements RheemQuantum {

    private Object[] values;

    public Record(Object... values) {
        this.values = values;
    }

    @Override
    public Record copy() {
        return new Record(this.values.clone());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Record record2 = (Record) o;
        return Arrays.deepEquals(this.values, record2.values);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(values);
    }

    @Override
    public String toString() {
        return "Record" + Arrays.deepToString(this.values);
    }

    @Deprecated
    public Object getField(int index) {
        return this.values[index];
    }

    /**
     * Retrieve a field as a {@code double}. It must be castable as such.
     *
     * @param index the index of the field
     * @return the {@code double} representation of the field
     */
    @Deprecated
    public double getDouble(int index) {
        Object field = this.values[index];
        return ReflectionUtils.toDouble(field);
       }

    /**
     * Retrieve a field as a {@code long}. It must be castable as such.
     *
     * @param index the index of the field
     * @return the {@code long} representation of the field
     */
    @Deprecated
    public long getLong(int index) {
        Object field = this.values[index];
        if (field instanceof Integer) return (Integer) field;
        else if (field instanceof Long) return (Long) field;
        else if (field instanceof Short) return (Short) field;
        else if (field instanceof Byte) return (Byte) field;
        throw new IllegalStateException(String.format("%s cannot be retrieved as long.", field));
    }

    /**
     * Retrieve a field as a {@code int}. It must be castable as such.
     *
     * @param index the index of the field
     * @return the {@code int} representation of the field
     */
    @Deprecated
    public int getInt(int index) {
        Object field = this.values[index];
        if (field instanceof Integer) return (Integer) field;
        else if (field instanceof Short) return (Short) field;
        else if (field instanceof Byte) return (Byte) field;
        throw new IllegalStateException(String.format("%s cannot be retrieved as int.", field));
    }

    /**
     * Retrieve a field as a {@link String}.
     *
     * @param index the index of the field
     * @return the field as a {@link String} (obtained via {@link Object#toString()}) or {@code null} if the field is {@code null}
     */
    @Deprecated
    public String getString(int index) {
        Object field = this.values[index];
        return field == null ? null : field.toString();
    }

    /**
     * Retrieve the size of this instance.
     *
     * @return the number of fields in this instance
     */
    @Deprecated
    public int size() {
        return this.arity();
    }


    @Override
    public int arity() {
        return this.values.length;
    }

    @Override
    public Object[] toArray() {
        return this.values;
    }

    @Override
    public RheemQuantum swap() {
        Object[] tmp = new Object[this.arity()];

        int end = this.arity() - 1;
        for(int i = 0; i < this.arity(); i++){
            tmp[end - i] = this.field(i);
        }

        return new Record(tmp);
    }

    @Override
    public RheemQuantum project(int[] fields, boolean validate) {
        //TODO add the validations
        Object[] tmp = new Object[fields.length];

        for(int i = 0; i < fields.length; i++){
            tmp[i] = this.field(fields[i]);
        }

        return new Record(tmp);
    }

    @Override
    public RheemQuantum join(RheemQuantum other) {
        int final_size = this.arity() + other.arity();

        Object[] elements = new Object[final_size];

        System.arraycopy(this.toArray(), 0, elements, 0, this.arity());
        System.arraycopy(other.toArray(), 0, elements, this.arity(), other.arity());

        return new Record(elements);
    }

    @Override
    public <T> T field(int index) {
        return (T) this.values[index];
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

    protected byte[] convertToBytes(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);
        out.writeObject(object);
        return bos.toByteArray();
    }
}
