package org.openshapa.models.database;

/**
 * Defines a method for retrieving the deprecated database implementation.
 * 
 * @param <T>
 *            the type of the deprecated database to retrieve.
 */
public interface DeprecatedDatabase<T> extends Database {

    @Deprecated
    public T getDatabase();

}
