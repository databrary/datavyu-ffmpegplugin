/*
 * InternalVocabElementListener.java
 *
 * Created on February 3, 2008, 4:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 * Interface InternalVocabElementListener
 *
 * Objects internal to the database that wish to be informed of changes in
 * vocab elements (i.e. PredicateVocabElements and MatrixVocabElements) may
 * implement this interface and then register with the vocab elements of
 * interest.
 *
 * The methods specified in this interface will be called when appropriate.
 *
 *                                                  JRM -- 2/2/08
 *
 * Note:  When this interface was defined, it was possible to handle listeners
 *        for changes to both MatrixVocabElements and PredicateVocabElements
 *        with the same listener interface.
 *
 *        Since then, it has become necessary to send more information to the
 *        listeners for changes to MatrixVocabElements, and thus an
 *        InternalMatrixVocabElementListener interface has been created.
 *
 *        Thus pending further modifications, this interface will be used
 *        only by listeners for changes to PredicateVocabElements.
 *
 *                                                  JRM -- 8/26/08
 *
 *
 * @author mainzer
 */
public interface InternalVocabElementListener
{
    /**
     * VEChanged()
     *
     * Called if the vocab element of interest is changed.
     *
     * The db parameter is mainly for sanity checking, as it is unlikely that
     * any listener will be interested in vocab element from more than one
     * Database.
     *
     * The VEID parameter contains the ID assigned the vocab element that has
     * been changed.  In some cases, it will be needed to disambiguate
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
     * of the target VocabElement changed in some way.  If it did, the
     * oldFargList and newFargList parameters contain refereences to copies of
     * the formal argument list before and after the change the change
     * respectively.  Also, the n2o, o2n, fargNameChanged, fargSubRangeChanged,
     * fargRangeChanged, fargDeleted, and fargInserted fields summerize the
     * exact changes.  See the comments on the fields of the same name in
     * VEChangeListeners for the exact particulars on these parameters.
     *
     * WARNING: For efficiency, the old and new farg list fields, and the
     *          n2o, o2n, fargNameChanged, fargSubRangeChanged,
     *          fargRangeChanged, fargDeleted, and fargInsertedare arrays
     *          are passed by reference.  Thus the listeners MUST NOT alter
     *          these vectors and arrays or their contents, or retain
     *          references to them.
     *
     *                                          JRM -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    void VEChanged(Database db,
                   long VEID,
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
                   java.util.Vector<FormalArgument> newFargList)
        throws SystemErrorException;


    /**
     * VEDeleted()
     *
     * Called if the VocabElement has been deleted from the vocab list.
     *
     * The db parameter is mainly for sanity checking, as it is unlikely that
     * any listener will be interested in vocab element from more than one
     * Database.
     *
     * The VEID parameter contains the ID assigned the vocab element that is
     * being deleted.  In some cases, it will be needed to disambiguate
     * the call.
     *
     * Note that the target vocab element will not actually be deleted until
     * all listeners have been notified.
     *
     *                                          JRM -- 2/2/08
     *
     * Changes:
     *
     *    - None.
     */

    void VEDeleted(Database db,
                   long VEID)
        throws SystemErrorException;
}
