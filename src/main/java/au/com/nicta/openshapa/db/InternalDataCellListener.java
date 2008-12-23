/*
 * InternalDataCellListener.java
 *
 * Created on February 7, 2008, 5:05 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 * Interface InternalDataCellListener
 *
 * Objects internal to the database that wish to be informed of changes in
 * DataCells may implement this interface and then register with the
 * DataCell of interest.
 *
 * The methods specified in this interface will be called when appropriate.
 *
 *                                                  JRM -- 2/6/08
 *
 *
 * @author mainzer
 */
public interface InternalDataCellListener
{
    /**
     * DCellChanged()
     *
     * Called if the DataCell of interest is changed.
     *
     * The db parameter is mainly for sanity checking, as it is unlikely that
     * any listener will be interested in DataCells from more than one
     * Database.
     *
     * The colID parameter contains the ID assigned the DataColumn that
     * contains the targed DataCell.
     *
     * The cellID parameter contains the ID assigned to the target DataCell.
     *
     * The ordChanged parameter indicates whether the ord changed.  If it did,
     * the oldOrd and newOrd fields contain the ord of the DataCell before
     * and after the change respectively.
     *
     * The onsetChanged parameter indicates whether the onset changed.  If it
     * did, the oldOnset and newOnset parameter contain references to copies of
     * the old onset and new onset respectively.
     *
     * WARNING: For efficiency, the old and new onset fields are passed by
     *          reference.  Thus listeners MUST NOT alter these instances
     *          of TimeStamp, or retain references to them.
     *
     * The offsetChanged parameter indicates whether the offset changed.  If it
     * did, the oldOffset and newOffset parameter contain references to copies
     * of the old offset and new offset respectively.
     *
     * WARNING: For efficiency, the old and new offset fields are passed by
     *          reference.  Thus listeners MUST NOT alter these instances
     *          of TimeStamp, or retain references to them.
     *
     * The valChanged parameter indicates whether the value of the DataCell
     * changed.  If it did, the oldVal and newVal parameters contain copies of
     * the value of the cell before and after the change respectively.
     *
     * WARNING: For efficiency, the old and new value fields are passed by
     *          reference.  Thus listeners MUST NOT alter these instances
     *          of Matrix, or retain references to them.
     *
     * The commentChanged parameter indicates whether the comment associated
     * with the cell has changed.  It it has the oldComment and newComment
     * parameters contain references to Strings containing the old and new
     * versions of the comment, or null if the comment was empty.
     *
     * WARNING: For efficiency, the old and new comment fields are passed by
     *          reference.  Thus listeners MUST NOT alter these Strings, or
     *          retain references to them.
     *
     *                                          JRM -- 2/6/08
     *
     * Changes:
     *
     *    - None.
     */

    void DCellChanged(Database db,
                      long colID,
                      long cellID,
                      boolean ordChanged,
                      int oldOrd,
                      int newOrd,
                      boolean onsetChanged,
                      TimeStamp oldOnset,
                      TimeStamp newOnset,
                      boolean offsetChanged,
                      TimeStamp oldOffset,
                      TimeStamp newOffset,
                      boolean valChanged,
                      Matrix oldVal,
                      Matrix newVal,
                      boolean commentChanged,
                      String oldComment,
                      String newComment);


    /**
     * DCellDeleted()
     *
     * Called if the DataCell has been deleted from its DataColumn.
     *
     * The db parameter is mainly for sanity checking, as it is unlikely that
     * any listener will be interested in DataCells from more than one
     * Database.
     *
     * The ColID parameter contains the ID assigned the DataColumn that
     * contains the targed DataCell.
     *
     * The CellID parameter contains the ID assigned to the target DataCell.
     *
     *                                          JRM -- 2/6/08
     *
     * Changes:
     *
     *    - None.
     */

    void DCellDeleted(Database db,
                      long colID,
                      long cellID);


}
