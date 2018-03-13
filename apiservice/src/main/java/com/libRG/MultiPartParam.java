package com.libRG;

/**
 * Created by ${Raja} on 11-Jan-18.
 */

/**
 * A representation of a MultiPart parameter
 */
public final class MultiPartParam {

    public String contentType;
    public String value;

    /**
     * Initialize a multipart request param with the value and content type
     *
     * @param contentType The content type of the param
     * @param value       The value of the param
     */
    public MultiPartParam(String contentType, String value) {
        this.contentType = contentType;
        this.value = value;
    }
}