/*
 * ExternalColumnListListener.java
 *
 * Created on February 11, 2008, 10:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.db;

/**
 * Interface ExternalColumnListListener
 *
 * Objects external to the database that wish to be informed of insertions to
 * and deletions from the column list may implement this interface and then
 * register with the column list of the database of interest.
 *
 * The methods specified in this interface will be called when appropriate.
 *
 *                                                  -- 2/11/08
 */
public interface ExternalColumnListListener
{

    /**
     * colDeletion()
     *
     * Called when a Column is deleted from the column list.
     *
     * The db parameter is mainly for sanity checking, as it is unlikely that
     * any listener will be interested in the column list of more than one
     * Database.
     *
     * The colID parameter contains the ID assigned the column that is
     * being deleted.
     *
     *                                          -- 2/11/08
     *
     * Changes:
     *
     *    - None.
     */

    void colDeletion(final Database db,
                     final long colID);


    /**
     * colInsertion()
     *
     * Called when a Column is inserted in the column list.
     *
     * The db parameter is mainly for sanity checking, as it is unlikely that
     * any listener will be interested in the column list of more than one
     * Database.
     *
     * The colID parameter contains the ID assigned the column that is
     * being inserted.
     *
     *                                          -- 2/6/08
     *
     * Changes:
     *
     *    - None.
     */

    void colInsertion(final Database db,
                      final long colID);

}
