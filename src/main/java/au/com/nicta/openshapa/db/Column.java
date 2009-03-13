/*
 * Column.java
 *
 * Created on December 7, 2006, 5:13 PM
 *
 */

package au.com.nicta.openshapa.db;

import au.com.nicta.openshapa.OpenSHAPA;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

/**
 * class Column
 *
 * Abstract class for columns (AKA spreadsheet variables).
 *
 * Instances of subclasses of Column serve a double purpose.
 *
 * Within the database, instances serve as a header class for
 * a column.
 *
 * When passed to the user, the header copies of the header minus
 * its vector ov cells is used to allow the user to modify the
 * user accessible fields.
 *
 *                                              JRM -- 8/29/07
 *
 * Changes:
 *
 *    - None.
 *
 * @author FGA
 */
public abstract class Column
        extends DBElement
        implements InternalCascadeListener
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/

    // TODO add comments field and supporting methods.

    /*
     * name: String containing the name of the Column.
     *      This name must be a valid svar name, and must be unique in both
     *      the vocab list and column list of the associated Database.
     *
     * hidden: Boolean flag indicating whether the Column (AKA Spreadsheet
     *      variable) is visible on the spreadsheet.
     *
     * readOnly: Boolean flag indicating whether the Column (AKA Spreadsheet
     *      variable) is read only.
     *
     * numCells:  Integer containing the number of cells in the column.  Within
     *      the Database, we can always ask the associated Vector of cells
     *      (defined in the subclasses to ensure stronger typing) how many
     *      cells it has.  However, we may also need this information outside
     *      the Database -- hence this field.
     *
     * selected:  Boolean flag indicating whether the column is currently
     *      selected.
     *
     * cascadeInProgress:  Boolean flag indicating that the column has been
     *      advised that a cascade of changes is in progress.  This field is
     *      used to determine whether cells can be added to the pending
     *      set (see below).
     *
     * pendingSet:  The set of cells in the column that have accumulated one
     *      or more changes in the current cascade of changes.  When the
     *      column is advised of the end of the cascade, it must notify the
     *      members of the set so they can replace their old incarnations with
     *      new ones.
     */

    /** column name */
    protected String name = null;

    /** whether the Column appears in the spreadsheet */
    protected boolean hidden = false;

    /** whether the Column and its contents are read only */
    protected boolean readOnly = false;

    /** number of cells in the column */
    protected int numCells = 0;

    /** whether a cascade of changes is in progress */
    protected boolean cascadeInProgress = false;

    /** set of cells which have pending changes in the current cascade of
     *  changes.
     */
    protected java.util.HashSet<Cell> pendingSet = null;

    /** whether the column is selected */
    boolean selected = false;

//    /** Column Change Listeners */
//    protected Vector<ColumnListener> changeListeners =
//            new Vector<ColumnListener>();


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * Column()
     *
     * Constructor for instances of Column.
     *
     * Three versions of this constructor.
     *
     * The first takes only a reference to a database as its parameter and
     * constructs an instance of Column with default values.
     *
     * The second takes a reference to a database, and initial values for
     * the name, hidden and readOnly fields.
     *
     *  The third takes and instance of Copumn as its parameter, and returns
     *  a copy.
     *
     *                                              JRM -- 8/29/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    public Column(Database db)
        throws SystemErrorException
    {

        super(db);

    } /* Column::Column(db) */

    public Column(Database db,
                  String name,
                  boolean hidden,
                  boolean readOnly)
        throws SystemErrorException, LogicErrorException
    {

        super(db);

        this.setName(name);

        this.hidden = hidden;
        this.readOnly = readOnly;

    } /* Column::Column(db, hidden, readOnly) */

    public Column(Column c)
        throws SystemErrorException
    {
        super((DBElement)c);

        if ( c.name == null )
        {
            this.name = null;
        }
        else
        {
            this.name = new String(c.name);
        }

        this.hidden  = c.hidden;
        this.readOnly = c.readOnly;
        this.numCells = c.numCells;
        this.selected = c.selected;

    } /* Column::Column(c) */


    /*************************************************************************/
    /******************* Abstract Method Declarations: ***********************/
    /*************************************************************************/

    /**
     * constructItsCells()
     *
     * Subclasses must define this method, which must construct itsCells --
     * the Vector of cells in which the cells of the column is stored.
     *
     * This method should be called exactly once -- when the Column is
     * inserted into the column list.
     *                                              JRM -- 6/19/07
     *
     * Changes:
     *
     *    - None.
     *
     */

    abstract protected void constructItsCells()
        throws SystemErrorException;

    /**
     * sortItsCells()
     *
     * Subclasses must define this method, which must sort itsCells by cell
     * onset.
     *                                              JRM -- 3/20/08
     *
     * Changes:
     *
     *    - None.
     */

    abstract protected void sortItsCells()
        throws SystemErrorException;



    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getHidden() & setHidden()
     *
     * Get and set the value of the hidden field.
     *
     *                                      JRM -- 8/29/07
     *
     * Changes:
     *
     *    - None.
     */

    public boolean getHidden()
    {

        return this.hidden;

    } /* Column::getHidden() */

    public void setHidden(boolean hidden)
    {

        this.hidden = hidden;

        return;

    } /* Column::setHidden(hidden) */


    /**
     * getReadOnly() & setReadOnly()
     *
     * Get and set the value of the readOnly field.
     *
     *                                      JRM -- 8/29/07
     *
     * Changes:
     *
     *    - None.
     */

    public boolean getReadOnly()
    {

        return this.readOnly;

    } /* Column::getReadOnly() */

    public void setReadOnly(boolean readOnly)
    {

        this.readOnly = readOnly;

        return;

    } /* Column::setReadOnly(readOnly) */


    /**
     * getNumCells() & setNumCells()
     *
     * Get and set the current value of the numCells field.  Observe that
     * setNumCells is protected.
     *
     *                          JRM -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     */

    public int getNumCells()
    {

        return this.numCells;

    } /* Column::getNumCells() */

    protected void setNumCells(int newNumCells)
        throws SystemErrorException
    {
        final String mName = "Column::setNumCells(newNumCells): ";

        if ( newNumCells < 0 )
        {
            throw new SystemErrorException(mName + "newNumCells < 0");
        }

        this.numCells = newNumCells;

        return;

    } /* Column::setNumCells(newNumCells) */


    /**
     * getName(), and setName()
     *
     * Get and set the name of the column.
     *
     * For setName() the supplied name must be a valid svar name, and must
     * be unique in both the vocab list and the column list.
     *
     *                                          JRM -- 8/30/07
     *
     * Changes:
     *
     *    - None.
     */

    public String getName()
    {
        String retVal = null;

        if ( this.name != null )
        {
            retVal = new String(this.name);
        }

        return retVal;

    } /* Column::getName() */


    /**
     * Sets the name of the column
     *
     * @param name The new name of the column to use
     * @throws au.com.nicta.openshapa.db.SystemErrorException when unable to set
     * the name of the column, either the database or the name is invalid.
     * @throws au.com.nicta.openshapa.db.LogicErrorException When the user has
     * induced an error that we can recover from (i.e. set the name of the
     * column to be identical to an existing column.
     */
    public void setName(final String name)
    throws SystemErrorException, LogicErrorException {
        ResourceMap rMap = Application.getInstance(OpenSHAPA.class)
                                      .getContext()
                                      .getResourceMap(Column.class);

        if (!Database.IsValidSVarName(name)) {
            throw new LogicErrorException(rMap.getString("Error.invalid",
                                                         name));
        }

        if (this.getDB() == null) {
            throw new SystemErrorException(rMap.getString("Error.baddb", name));
        }

        if (this.getDB().vl.inVocabList(name)) {
            throw new LogicErrorException(rMap.getString("Error.exists", name));
        }

        if (this.getDB().cl.inColumnList(name)) {
            throw new LogicErrorException(rMap.getString("Error.exists", name));
        }

        this.name = new String(name);
        return;

    } /* Column::setName() */


    /**
     * getSelected() & setSelected()
     *
     * Get and set the value of the selected field.
     *
     *                              JRM -- 2/8/08
     *
     * Changes:
     *
     *    - None.
     */

    public boolean getSelected()
    {

        return this.selected;

    } /* Column::getSelected() */

    public void setSelected(boolean selected)
    {

        this.selected = selected;

        return;

    } /* Column::setSelected() */


    /*************************************************************************/
    /************************* Cascade Management: ***************************/
    /*************************************************************************/

    /**
     * addPending()
     *
     * Add the specified cell to the pending set.  Note that the instance of
     * Cell MUST be the current cannonical incarnation.  This should be verified
     * by the subclass.
     *
     *                                              JRM -- 3/6/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void addPending(Cell c)
        throws SystemErrorException
    {
        final String mName = "Column::addPending(c): ";

        if ( c == null )
        {
            throw new SystemErrorException(mName + "c null on entry");
        }

        if ( c.getItsColID() != this.getID() )
        {
            throw new SystemErrorException(mName + "col ID mismatch");
        }

        if ( ! this.cascadeInProgress )
        {
            throw new SystemErrorException(mName +
                    "call to addPending() when cascade not in progress.");
        }

        if ( this.pendingSet == null )
        {
            throw new SystemErrorException(mName + "pendingSet is null?!?!");
        }

        /* If we get this far, go ahead and add the cell to the pendingSet
         * if it is not in the pending set already.
         */

        if ( ! this.pendingSet.contains(c) )
        {
            this.pendingSet.add(c);
        }

        return;

    } /* Column::addPending() */


    /**
     * beginCascade()
     *
     * Needed to implement the InternalCascadeListener interface.
     *
     * Handle the various housekeeping required to process the start
     * of a cascade of changes through the database.
     *
     * Verify that this.cascadeInProgress is false.  Throw a system
     * error it it isn't.
     *
     * If this.pendingSet is null, allocate it.  Otherwise verify
     * that this.pending set is empty, and throw a system error if
     * it isn't.
     *
     * Finally set this.cascadeInProgress to true, and exit.
     *
     *                                  JRM -- 3/15/08
     *
     * Changes:
     *
     *    - None.
     */

    public void beginCascade(Database db)
        throws SystemErrorException
    {
        final String mName = "Column::beginCascade(): ";

        if ( this.getDB() != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( this.cascadeInProgress )
        {
            throw new SystemErrorException(mName +
                "call to beginCascade() when this.cascadeInProgress is true?!?");
        }

        if ( this.pendingSet == null )
        {
            this.pendingSet = new java.util.HashSet<Cell>();
        }
        else if ( ! this.pendingSet.isEmpty() )
        {
            throw new SystemErrorException(mName +
                    "pendingSet not empty at beginning of cascade?!?");
        }

        this.cascadeInProgress = true;

        return;

    } /* column::beginCascade() */


    /**
     * clearPending()
     *
     * Delete any entries from the pending set, and then discard it.
     *
     *                                              JRM -- 3/14/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void clearPending()
        throws SystemErrorException
    {
        final String mName = "Column::clearPending(): ";

        if ( ! this.cascadeInProgress )
        {
            throw new SystemErrorException(mName +
                    "call to clearPending() when cascade not in progress.");
        }

        if ( this.pendingSet == null )
        {
            throw new SystemErrorException(mName + "pendingSet is null?!?!");
        }

        /* If we get this far, clear the pending set.
         */

        this.pendingSet.clear();

        return;

    } /* Column::clearPending() */


    /**
     * endCascade()
     *
     * Needed to implement the InternalCascadeListener interface.
     *
     * Handle the various housekeeping required to process the end
     * of a cascade of changes through the database.  Subclasses will
     * almost always override this method, and then call it from
     * within the override.
     *
     * Verify that this.cascadeInProgress is true.  Throw a system
     * it it isn't.
     *
     * If this.pendingSet is null, throw a system error.
     *
     * Then clear the pending set, set this.cascadeInProgress to false,
     * and exit.
     *
     *                                  JRM -- 3/15/08
     *
     * Changes:
     *
     *    - None.
     */

    public void endCascade(Database db)
        throws SystemErrorException
    {
        final String mName = "Column::endCascade(): ";

        if ( this.getDB() != db )
        {
            throw new SystemErrorException(mName + "db mismatch.");
        }

        if ( ! this.cascadeInProgress )
        {
            throw new SystemErrorException(mName +
                "call to endCascade() when this.cascadeInProgress is false?!?");
        }

        if ( this.pendingSet == null )
        {
            throw new SystemErrorException(mName + "this.pendingSet is null?!?");
        }

        this.clearPending();
        this.cascadeInProgress = false;

        return;

    } /* Column::endCascade() */



    /*************************************************************************/
    /************************ Listener Management: ***************************/
    /*************************************************************************/

    /**
     * deregister()
     *
     * Do any listener deregistrations necessary before the column is removed
     * from the data base.
     *
     * This method is intened to be over-ridden as needed by subclasses.
     *
     *                                              JRM -- 3/23/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void deregister()
        throws SystemErrorException
    {

        return;

    } /* Column::deregister() */


    /**
     * register()
     *
     * Do any listener registrations necessary when the column is inserted
     * into the data base.
     *
     * This method is intened to be over-ridden as needed by subclasses.
     *
     *                                              JRM -- 3/23/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void register()
        throws SystemErrorException
    {

        return;

    } /* Column::register() */


//  /**
//   * Adds a change listener to the column listener list
//   * @param listener the listener to add
//   */
//  public void addColumnChangeListener(ColumnListener listener)
//  {
//    this.changeListeners.add(listener);
//  } //End of addColumnChangeListener() method
//
//  /**
//   * Removes a change listener from the column listener list
//   */
//  public void removeColumnChangeListener(ColumnListener listener)
//  {
//    this.changeListeners.remove(listener);
//  } //End of removeColumnChangeListener() method


} //End of Column class definition
