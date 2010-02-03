/*
 * ExternalCascadeListener.java
 *
 * Created on February 11, 2008, 10:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.models.db;

/**
 * Interface ExternalCascadeListener
 *
 * Objects external to the database that wish to be informed of the beginning
 * and end of a cascade of changes should implement this interface and
 * register with the database of interest.
 *
 *                                                   -- 2/11/08
 */
public interface ExternalCascadeListener
{
    /**
     * beginCascade()
     *
     * Called at the beginning of a cascade of changes through the database.
     *
     *                                               -- 2/11/08
     *
     * Changes:
     *
     *    - None.
     */

    void beginCascade(Database db);

    /**
     * endCascade()
     *
     * Called at the end of a cascade of changes through the database.
     *
     *                                               -- 2/11/08
     *
     * Changes:
     *
     *    - None.
     */

    void endCascade(Database db);

} // interface ExternalCascadeListener
