/*
 * ExternalVocabElementListener.java
 *
 * Created on February 3, 2008, 12:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.db;

/**
 * Interface ExternalVocabElementListener
 *
 * Objects external to the database that wish to be informed of changes in
 * vocab elements (i.e. PredicateVocabElements and MatrixVocabElements) may
 * implement this interface and then register with the vocab elements of
 * interest.
 *
 * The methods specified in this interface will be called when appropriate.
 *
 *                                                  JRM -- 2/2/08
 *
 *
 * @author mainzer
 */
public interface ExternalVocabElementListener
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
     * the varLen field before and after the change respectively.
     *
     * The fargListChanged parameter indecates whether the formal argument list
     * of the target VocabElement changed in some way.  If it did, the
     * oldFargList and newFargList parameters contain refereences to copies of
     * the formal argument list before and after the change the change
     * respectively.
     *
     * WARNING: For efficiency, the old and new farg list field are passed by
     *          reference.  Thus the listeners MUST NOT alter these vectors
     *          or their contents, or retain references to them.
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
                   java.util.Vector<FormalArgument> oldFargList,
                   java.util.Vector<FormalArgument> newFargList);


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
                   long VEID);
}
