/*
 * ExternalDataColumnListener.java
 *
 * Created on February 7, 2008, 5:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.models.db.legacy;

/**
 * Interface ExternalDataColumnListener
 *
 * Objects external to the database that wish to be informed of insertions to,
 * deletions from, re-arrangements of, and configuration changes in DataColumns
 * may implement this interface and then register with the DataColumn of the
 * database of interest.
 *
 * The methods specified in this interface will be called when appropriate.
 */
public interface ExternalDataColumnListener
{

    /**
     * DColCellDeletion()
     *
     * Called when a DataCell is deleted from the DataColumn.
     *
     * The db parameter is mainly for sanity checking, as it is unlikely that
     * any listener will be interested in the DataColumns of more than one
     * Database.
     *
     * The colID parameter contains the ID assigned to the DataColumn.
     *
     * The cellID parameter contains the ID assigned to the DataCell that is
     * being deleted.
     *
     *                                          -- 2/6/08
     *
     * Changes:
     *
     *    - None.
     */

    void DColCellDeletion(Database db,
                          long colID,
                          long cellID);


    /**
     * DColCellInsertion()
     *
     * Called when a DataCell is inserted in the vocab list.
     *
     * The db parameter is mainly for sanity checking, as it is unlikely that
     * any listener will be interested in the vocab list of more than one
     * Database.
     *
     * The colID parameter contains the ID assigned to the DataColumn.
     *
     * The cellID parameter contains the ID assigned to the DataCell that is
     * being inserted.
     *
     *                                          -- 2/6/08
     *
     * Changes:
     *
     *    - None.
     */

    void DColCellInsertion(Database db,
                           long colID,
                           long cellID);


    /**
     * DColConfigChanged()
     *
     * Called when one fields of the target DataColumn are changed.
     *
     * The colID parameter contains the ID assigned to the DataColumn.
     *
     * The cellID parameter contains the ID assigned to the DataCell that is
     * being inserted.
     *
     * The nameChanged parameter iddicates whether the name changed.  If it did,
     * the oldName and newName fields contain references to copies of the name
     * of the vocab element before and after the change respectively.
     *
     * WARNING: For efficiency, the old and new name fields are passed by
     *          reference.  Thus listeners MUST NOT alter these strings, or
     *          retain references to them.
     *
     * The hiddenChanged parameter indicates whether the hidden field changed.
     * If it did, the oldHidden and newHidden parameters contain the old and new
     * values of the hidden field respectively.
     *
     * The readOnlyChanged parameter indicates whether the readOnly field
     * changed.  If it did, the oldReadOnly and newReadOnly parameters contain
     * the old and new values of the readOnly field respectively.
     *
     * The varLenChanged parameter indicates whether the varLen field changed.
     * If it did, the oldVarLen and newVarLen parameters contain the old and
     * new values of the varLen field respectively.
     *
     * The selectedChanged parameter indicates whether the selection
     * status of the DataColumn has changed.  If it has, the oldSelected and
     * newSelected parameters contain the old and new values of the selected
     * field of the DataCell.
     *
     *
     *                                          -- 2/6/08
     *
     * Changes:
     *
     *    - None.
     */

    void DColConfigChanged(Database db,
                           long colID,
                           boolean nameChanged,
                           String oldName,
                           String newName,
                           boolean hiddenChanged,
                           boolean oldHidden,
                           boolean newHidden,
                           boolean readOnlyChanged,
                           boolean oldReadOnly,
                           boolean newReadOnly,
                           boolean varLenChanged,
                           boolean oldVarLen,
                           boolean newVarLen,
                           boolean selectedChanged,
                           boolean oldSelected,
                           boolean newSelected);

    /**
     * DColDeleted()
     *
     * Called when the DataColumn of interest is deleted.
     *
     * The db parameter is mainly for sanity checking, as it is unlikely that
     * any listener will be interested in the vocab list of more than one
     * Database.
     *
     * The colID parameter contains the ID assigned to the DataColumn.
     *
     *                                          -- 2/6/08
     *
     * Changes:
     *
     *    - None.
     */

    void DColDeleted(Database db,
                      long colID);

} // interface ExternalDataColumnListener
