/*
 * ExternalColumnListListener.java
 *
 * Created on February 11, 2008, 10:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 * Interface ExternalColumnListListener
 *
 * Objects external to the database that wish to be informed of insertions to
 * and deletions from the column list may implement this interface and then
 * register with the column list of the database of interest.
 *
 * The methods specified in this interface will be called when appropriate.
 *
 *                                                  JRM -- 2/11/08
 *
 *
 * @author mainzer
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
     *                                          JRM -- 2/11/08
     *
     * Changes:
     *
     *    - None.
     */

    void colDeletion(Database db,
                     long colID);


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
     *                                          JRM -- 2/6/08
     *
     * Changes:
     *
     *    - None.
     */

    void colInsertion(Database db,
                      long colID);

}
