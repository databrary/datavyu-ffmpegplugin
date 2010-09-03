/*
 * ExternalVocabListListener.java
 *
 * Created on February 7, 2008, 12:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.models.db;

/**
 * Interface ExternalVocabListListener
 *
 * Objects external to the database that wish to be informed of insertions to
 * and deletions from the vocab list may implement this interface and then
 * register with the vocab list of the database of interest.
 *
 * The methods specified in this interface will be called when appropriate.
 *
 *                                                   -- 2/6/08
 */
public interface ExternalVocabListListener
{

    /**
     * VLDeletion()
     *
     * Called when a VocabElement is deleted from the vocab list.
     *
     * The db parameter is mainly for sanity checking, as it is unlikely that
     * any listener will be interested in the vocab list of more than one
     * Database.
     *
     * The VEID parameter contains the ID assigned the vocab element that is
     * being deleted.
     *
     *                                           -- 2/6/08
     *
     * Changes:
     *
     *    - None.
     */

    void VLDeletion(Database db,
                    long VEID);


    /**
     * VLInsertion()
     *
     * Called when a VocabElement is inserted in the vocab list.
     *
     * The db parameter is mainly for sanity checking, as it is unlikely that
     * any listener will be interested in the vocab list of more than one
     * Database.
     *
     * The VEID parameter contains the ID assigned the vocab element that is
     * being inserted.
     *
     *                                           -- 2/6/08
     *
     * Changes:
     *
     *    - None.
     */

    void VLInsertion(Database db,
                     long VEID);

    void VLReplace(Database db,
                   long VEID);

} /* interface ExternalVocabListListener */

