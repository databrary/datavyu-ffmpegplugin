package org.openshapa.db;

/**
 * This is a general purpose exception that is used to flag errors that are
 * likely to be induced by the user (rather than incorrect programmer usage
 * of the database).
 *
 * All Logic error messages are recoverable, we display a warning message to the
 * user of the mistake they have made and allow them to fix the error and
 * procede
 */
public final class LogicErrorException extends java.lang.Exception {

    /**
     * Creates a new instance of <code>LogicErrorException</code> without detail
     * message.
     */
    public LogicErrorException() {
    }


    /**
     * Constructs an instance of <code>LogicErrorException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public LogicErrorException(final String msg) {
        super(msg);
    }
}
