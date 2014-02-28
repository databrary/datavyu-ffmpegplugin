package org.datavyu.models.db;

/**
 * This is a general purpose exception that is used to flag errors that are
 * likely to be induced by the user (rather than incorrect programmer usage
 * of the database).
 *
 * All Logic error messages are recoverable, we display a warning message to the
 * user of the mistake they have made and allow them to fix the error and
 * proceed.
 */
public final class UserWarningException extends Exception {

    /**
     * Creates a new instance of <code>LogicErrorException</code> without detail
     * message.
     */
    public UserWarningException() {
    }

    /**
     * Constructs an instance of <code>LogicErrorException</code> with the
     * specified detail message.
     *
     * @param msg The detail message.
     */
    public UserWarningException(final String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>LogicErrorException</code> with the
     * specified message and call stack.
     *
     * @param msg The detail message.
     * @param e Used to create a call stack (the exception that lead to a logic
     * error exception).
     */
    public UserWarningException(final String msg, final Exception e) {
        super(msg, e);
    }
}
