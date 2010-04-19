/*
 * InternalMatrixVocabElementListener.java
 *
 * Created on August 26, 2008, 2:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.models.db;

/**
 * Interface InternalMatrixVocabElementListener
 *
 * Objects internal to the database that wish to be informed of changes in
 * matrix vocab elements may implement this interface and then register with
 * the matrix vocab elements of interest.
 *
 * The methods specified in this interface will be called when appropriate.
 *
 *                                                  -- 8/26/08
 */
public interface InternalMatrixVocabElementListener
{
    /**
     * MVEChanged()
     *
     * Called if the matrix vocab element of interest is changed.
     *
     * The db parameter is mainly for sanity checking, as it is unlikely that
     * any listener will be interested in vocab element from more than one
     * Database.
     *
     * The MVEID parameter contains the ID assigned the matrix vocab element
     * that has been changed.  In some cases, it will be needed to disambiguate
     * the call.
     *
     * The nameChanged parameter idicates whether the name changed.  If it  did,
     * the oldName and newName fields contain references to copies of the name
     * of the vocab element before and after the change respectively.
     *
     * WARNING: For efficiency, the old and new name fields are passed by
     *          reference.  Thus listeners MUST NOT alter these strings, or
     *          retain references to them.
     *
     * The varLenChanged parameter indicates whether the varLen field changed.
     * If it did, the oldVarLen and newVarLen parameters contain the values of
     * the varLen field before and after the change respectively,
     *
     * The fargListChanged parameter indecates whether the formal argument list
     * of the target MatrixVocabElement changed in some way.  Note that since
     * the formal argument list of the column predicate implied by the matrix
     * vocab element is derived from the matrix formal argument list, it will
     * have changed iff the matrix vocab element formal argument list has
     * changed.
     *
     * If it did, the oldFargList and newFargList parameters contain refereences
     * to copies of the formal argument list before and after the change the
     * change respectively.  Also, the n2o, o2n, fargNameChanged,
     * fargSubRangeChanged, fargRangeChanged, fargDeleted, and fargInserted
     * fields summerize the exact changes.
     *
     * Similarly, the oldCPFargList and newCPFargList parameters contain
     * references to copies of the implied column predicate formal argument
     * list before and after the change respectively.  Likewise, the cpn2o,
     * cpo2n, cpFargNameChanged, cpFargSubRangeChanged, cpFargRangeChanged,
     * cpFargDeleted, and cpFargInstered fields summerize the exact changes.
     *
     * See the comments on the fields of the same name in VocabElementListeners
     * and MatrixVocabElementListeners for the exact particulars on these
     * parameters.
     *
     * WARNING: For efficiency, the old and new farg list fields, and the
     *          n2o, o2n, fargNameChanged, fargSubRangeChanged,
     *          fargRangeChanged, fargDeleted, and fargInsertedare arrays,
     *          and all their column predicate cognates are passed by reference.
     *          Thus the listeners MUST NOT alter these vectors and arrays or
     *          their contents, or retain references to them.
     *
     *                                          -- 8/26/08
     *
     * Changes:
     *
     *    - None.
     */

    void MVEChanged(Database db,
                    long MVEID,
                    boolean nameChanged,
                    String oldName,
                    String newName,
                    boolean varLenChanged,
                    boolean oldVarLen,
                    boolean newVarLen,
                    boolean fargListChanged,
                    long[] n2o,
                    long[] o2n,
                    boolean[] fargNameChanged,
                    boolean[] fargSubRangeChanged,
                    boolean[] fargRangeChanged,
                    boolean[] fargDeleted,
                    boolean[] fargInserted,
                    java.util.Vector<FormalArgument> oldFargList,
                    java.util.Vector<FormalArgument> newFargList,
                    long[] cpn2o,
                    long[] cpo2n,
                    boolean[] cpFargNameChanged,
                    boolean[] cpFargSubRangeChanged,
                    boolean[] cpFargRangeChanged,
                    boolean[] cpFargDeleted,
                    boolean[] cpFargInserted,
                    java.util.Vector<FormalArgument> oldCPFargList,
                    java.util.Vector<FormalArgument> newCPFargList)
        throws SystemErrorException;


    /**
     * MVEDeleted()
     *
     * Called if the MatrixVocabElement has been deleted from the vocab list.
     *
     * The db parameter is mainly for sanity checking, as it is unlikely that
     * any listener will be interested in vocab elements from more than one
     * Database.
     *
     * The MVEID parameter contains the ID assigned the matrix vocab element
     * that is being deleted.  In some cases, it will be needed to disambiguate
     * the call.
     *
     * Note that the target matrix vocab element will not actually be deleted
     * until all listeners have been notified.
     *
     *                                          -- 8/26/08
     *
     * Changes:
     *
     *    - None.
     */

    void MVEDeleted(Database db,
                    long MVEID)
        throws SystemErrorException;
}
