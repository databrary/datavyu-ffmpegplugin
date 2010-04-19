package org.openshapa.models.db;

/**
 * This is a general purpose exception that is used to flag fatal errors in
 * OpenSHAPA whenever they are detected during execution.  This exception is
 * thrown whenever we encounter an unexpected condition that we can't recover
 * from.  Always use the message version of the constructor, and always include
 * a brief description of the failure.  Always use an unique error message, so
 * there is no confusion at to where the exception was thrown.
 *
 * All system errors are un-recoverable.  We may want to put up a dialog
 * informing the user of the error and displaying the message.  However, we
 * should exit after the user dismisses the message.
 *
 * @date 2007/01/24
 */
public class SystemErrorException extends Exception {

    /**
     * Creates a new instance of SystemErrorException without detail message.
     */
    public SystemErrorException() {
    }

    /**
     * Constructs an instance of SystemErrorException with the specified detail
     * message.
     *
     * @param msg the detail message.
     */
    public SystemErrorException(final String msg) {
        super(msg);
    }

    /**
     * Constrcuts an instance of SystemErrorException with the specified detail
     * message and cause.
     *
     * @param msg The detail message of the exception.
     * @param cause The cause of the exception.
     */
    public SystemErrorException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
