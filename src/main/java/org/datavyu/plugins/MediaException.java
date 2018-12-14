package org.datavyu.plugins;


public class MediaException extends RuntimeException {

    private MediaError error = null;

    /**
     * Constructor which merely passes its parameter to the corresponding
     * superclass constructor
     * {@link RuntimeException#RuntimeException(java.lang.String)}.
     *
     * @param message The detail message.
     */
    public MediaException(String message) {
        super(message);
    }

    /**
     * Constructor which merely passes its parameters to the corresponding
     * superclass constructor
     * {@link RuntimeException#RuntimeException(java.lang.String, java.lang.Throwable)}.
     *
     * @param message The detail message.
     * @param cause The cause.
     */
    public MediaException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor which merely passes its parameters to the corresponding
     * superclass constructor
     * {@link RuntimeException#RuntimeException(java.lang.String, java.lang.Throwable)}.
     *
     * @param message The detail message.
     * @param cause The cause.
     * @param error The media error.
     */
    public MediaException(String message, Throwable cause, MediaError error) {
        super(message, cause);
        this.error = error;
    }

    public MediaError getMediaError() {
        return error;
    }
}
