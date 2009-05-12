/*
 * SystemErrorException.java
 *
 * This is a general purpose exception that is used to flag logic errors in OpenSHAPA
 * whenever they are detected during execution.  This exception is thrown whenever
 * we encounter an unexpected condition that we can't recover from.  Always use the
 * message version of the constructor, and always include a brief description of the
 * failure.  Always use an unique error message, so there is no confusion at to
 * where the exception was thrown.
 *
 * All system errors are un-recoverable.  We may want to put up a dialog informing
 * the user of the error and displaying the message.  However, we should exit after
 * the user dismisses the message.
 *                                                       -- 1/24/07
 *
 * Created on January 24, 2007, 2:14 AM
 */

package org.openshapa.db;

/**
 *
 */
public class SystemErrorException extends java.lang.Exception {

    /**
     * Creates a new instance of <code>SystemErrorException</code> without detail message.
     */
    public SystemErrorException() {
    }


    /**
     * Constructs an instance of <code>SystemErrorException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SystemErrorException(String msg) {
        super(msg);
    }
}
