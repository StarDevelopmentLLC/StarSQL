package com.stardevllc.starsql.interfaces;

/**
 * This interface is about transcribing objects into text and text back into objects. <br>
 * The output of the {@code encode} method is the same as what would be put into the {@code decode} method. <br>
 * This only happens if the stored data is not null, or if the string is not empty. <br>
 * Codecs are intended to be used for cases where using a {@link TypeHandler} is not available. 
 * @param <T> The Java type that this codec is for
 */
public interface ObjectCodec<T> {
    /**
     * Encodes an object into a string<br>
     * The passed object should be safe to cast. It passes in the value in the Field of the Model Class
     * @param object The object to encode
     * @return The string form for this object. Please note: This is passed into the {@code decode} method
     */
    String encode(Object object);
    
    /**
     * Decodes the object from the provided String<br>
     * The string passed into this method is the same as the one returned from the {@code encode} method
     * @param encoded The encoded value
     * @return The decoded object
     */
    T decode(String encoded);
}
