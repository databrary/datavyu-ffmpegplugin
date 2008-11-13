/*
 * ODBCDatabase.java
 *
 * Created on January 12, 2007, 5:39 PM
 *
 */

package au.com.nicta.openshapa.db;

/**
 * Default OpenSHAPA Database.  Implements expanded/new version of MacSHAPA ODBC
 * database functionality.
 * @author FGA
 */
public class ODBCDatabase extends Database
{
  public final static String DB_TYPE = "ODBC Database";
  public final static float DB_VERSION = 2.0f;
  
  /**
   * Creates a new instance of ODBCDatabase
   */
  public ODBCDatabase()
    throws SystemErrorException
  {
  } //End of ODBCDatabase constructor

  /**
   * Gets the database type string<br>
   * (eg ODB File)
   */
  public String getType()
  {
    return (DB_TYPE);
  } //End of getType() method

  /**
   * Gets the database version number<br>
   * (eg 2.1)
   */
  public float getVersion()
  {
    return (DB_VERSION);
  } //End of getVersion() method

  /**
   * Sets the ticks per second
   * @param tps ticks per second
   */
  public void setTicks(int tps)
    throws SystemErrorException
  {
    int prevTPS = this.tps;
    this.tps = tps;
    
    throw new SystemErrorException("Not fully implemented");

//    // Notify all listeners of TPS change
//    for (int i=0; i<this.changeListeners.size(); i++) {
//      ((DatabaseChangeListener)this.changeListeners.elementAt(i)).databaseTicksChanged(this, prevTPS);
//    }
  } //End of setTicks() method

  /**
   * Gets the ticks per second
   * @return ticks per second
   */
  public int getTicks()
  {
    return (this.tps);
  } //End of getTicks() method

  /**
   * Sets the start time flag
   * @param useStartTime the use start time flag value
   */
  public void setUseStartTime(boolean useStartTime)
  {
    this.useStartTime = useStartTime; 
  } //End of setUseStartTime() method

  /**
   * Gets the use start time flag
   * @return true if we are to use a start time
   */
  public boolean useStartTime()
  {
    return (this.useStartTime);
  } //End of useStartTime() method

  /**
   * Sets the start time
   * @param startTime the start time
   */
  public void setStartTime(long startTime)
    throws SystemErrorException
  {
    long prevST = this.startTime;
    this.startTime = startTime;
    
    throw new SystemErrorException("not fully implemented");

//    // Notify all listeners of TPS change
//    for (int i=0; i<this.changeListeners.size(); i++) {
//      ((DatabaseChangeListener)this.changeListeners.elementAt(i)).databaseStartTimeChanged(this, prevST);
//    }
  } // End of setStartTime() method

  /**
   * Gets the start time
   * @return the start time value
   */
  public long getStartTime()
  {
    return (this.startTime);
  } //End of getStartTime() method


//  /**
//   * Gets the cell associated with the given id in the given column
//   * @param columnID the id of the column the cell is in
//   * @param cellID the id of the cell
//   * @return the cell associated with the given cell id
//   */
//  public Cell getCell(long columnID, long cellID)
//  {
//    return (null);
//  } //End of getCell() method

//  /**
//   * Gets the cell associated with the given id in the given column
//   * @param column the column the cell is in
//   * @param cellID the id of the cell
//   * @return the cell associated with the given cell id
//   */
//  public Cell getCell(Column column, long cellID)
//  {
//    return (this.getCell(column.getID(), cellID));
//  } //End of getCell() method

  
//  /**
//   * Gets the column associated with the given id
//   * @param columnID the id of the column
//   * @return the column associated with the given column id
//   */
//  public Column getColumn(long columnID)
//  {
//    return (null);
//  } //End of getColumn() method

  /**
   * Gets the argument associated with the given id
   * @param argumentID the id of the argument
   * @return the argument associated with the given argument id
   */
  public FormalArgument getFormalArgument(long argumentID)
  {
    return (null);
  } //End of getArgument() method

  /**
   * Gets the vocab associated with the given id
   * @param vocabID the id of the vocab
   * @return the vocab associated with the given vocab id
   */
  // delete this eventually
//  public VocabElement getVocabElement(long vocabID)
//  {
//    return (null);
//  } //End of getVocab() method

  /**
   * Creates a Column of the given type in the database.
   * @param columnType the type of column to create:<br>
   * Must be either:
   * <UL>
   * <LI>COLUMN_TYPE_DATA</LI> or
   * <LI>COLUMN_TYPE_REFERENCE</LI>
   * </UL>
   * @return the newly created column object 
   */
  public Column createColumn(int columnType)
  {
    return (null);
  } //End of createColumn() method

  /**
   * Creates a new cell in the given column.
   * @param columnID the id of the column in which to create the cell
   * @return the newly created cell
   */
  public Cell createCell(long columnID)
  {
    return (null);
  } //End of createCell() method

  /**
   * Creates a new cell in the given column.
   * @param column the column in which to create the cell
   * @return the newly created cell
   */
  public Cell createCell(Column column)
  {
    return (this.createCell(column.getID()));
  } // End of createCell() method

  /**
   * Creates a new formal argument.
   * @return the newly created formal argument
   */
  public FormalArgument createFormalArgument()
  {
    return (null);
  } //End of createFormalArgument() method

  /**
   * Creates a new matrix vocab element.
   * @return the newly created matrix vocab element
   */
  public VocabElement createMatrixVocabElement()
  {
    return (null);
  } //End of createMatrixVocabElement() method

  /**
   * Creates a new predicate vocab element.
   * @return the newly created predicate vocab element
   */
  public VocabElement createPredicateVocabElement()
  {
    return (null);
  } //End of createPredicateVocabElement() method

} //End of ODBCDatabase class definition
