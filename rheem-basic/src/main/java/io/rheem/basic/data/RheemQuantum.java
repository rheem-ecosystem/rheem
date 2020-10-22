package io.rheem.basic.data;

import io.rheem.core.util.Copyable;

import java.io.Serializable;

/**
 * RheemQuantum is abstraction of the type use in data process and simplify
 *  the manipulation
 */
public interface RheemQuantum extends Serializable, Copyable<RheemQuantum> {

    /**
     * Gets the number of field in the RheemQuantum.
     *
     * @return The number of fields in the RheemQuantum.
     */
    int arity();

    /**
     * create an object array with the content of the RheemQuantum
     *
     * @return object array
     */
    Object[] toArray();

    /**
     * @return a new instance with the fields of this instance swapped
     */
    RheemQuantum swap();

    /**
     * Creates a new RheemQuantum with projected fields and identical
     *  from another RheemQuantum.
     *
     * <p>This method does not perform a deep copy.
     *
     * @param fields field indices to be projected
     */
    default RheemQuantum project(int[] fields){
        return this.project(fields, false);
    }

    RheemQuantum project(int[] fields, boolean validate);

    /**
     * Creates a new RheemQuantum with fields that are copied from the other
     *  RheemQuantum and appended to the resulting RheemQuantum in the given order.
     *
     * <p>This method does not perform a deep copy.
     */
    RheemQuantum join(RheemQuantum other);

    /**
     * Get type of the field in the position of the index
     *
     * @param index position of the required field
     * @return Type of the field in the position of the index
     */
    default Class fieldType(int index){
        return this.field(index).getClass();
    }

//    /**
//     * Get field in the position of the index
//     *
//     * @param index position of the required field
//     * @return field on the position of the index
//     */
//    Object field(int index);

    /**
     *
     * Get field in the position of the index, adding the type
     *
     * @param index position of the required field
     * @return field on the position of the index
     */
    <T> T field(int index);

    /**
     * Retrieve a field as a {@link String}.
     *
     * @param index the index of the field
     * @return the field as a {@link String} (obtained via {@link Object#toString()})
     *  or {@code null} if the field is {@code null}
     */
    String fieldAsString(int index);

    /**
     * Retrieve a field as a {@code boolean}. It must be castable as such.
     *
     * @param index the index of the field
     * @return the {@code boolean} representation of the field
     */
    boolean fieldAsBoolean(int index);

    /**
     * Retrieve a field as a {@code byte[]}. It must be castable as such.
     *
     * @param index the index of the field
     * @return the {@code byte[]} representation of the field
     */
    byte[] fieldAsBytes(int index);

    /**
     * Retrieve a field as a {@code char}. It must be castable as such.
     *
     * @param index the index of the field
     * @return the {@code char} representation of the field
     */
    char fieldAsChar(int index);

    /**
     * Retrieve a field as a {@code short}. It must be castable as such.
     *
     * @param index the index of the field
     * @return the {@code short} representation of the field
     */
    short fieldAsShort(int index);

    /**
     * Retrieve a field as a {@code int}. It must be castable as such.
     *
     * @param index the index of the field
     * @return the {@code int} representation of the field
     */
    int fieldAsInt(int index);

    /**
     * Retrieve a field as a {@code long}. It must be castable as such.
     *
     * @param index the index of the field
     * @return the {@code long} representation of the field
     */
    long fieldAsLong(int index);

    /**
     * Retrieve a field as a {@code float}. It must be castable as such.
     *
     * @param index the index of the field
     * @return the {@code float} representation of the field
     */
    float fieldAsFloat(int index);

    /**
     * Retrieve a field as a {@code double}. It must be castable as such.
     *
     * @param index the index of the field
     * @return the {@code double} representation of the field
     */
    double fieldAsDouble(int index);

    //TODO validate if the date management is need it

    default Object[] convertToArray(Object... objs){
        return objs;
    }

}
