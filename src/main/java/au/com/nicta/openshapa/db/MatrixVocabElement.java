/*
 * MatrixVocabElement.java
 *
 * Created on December 14, 2006, 5:58 PM
 *
 */

package au.com.nicta.openshapa.db;

import java.util.Vector;


/**
 * Class MatrixVocabElement
 *
 * Instances of MatrixVocabElement are used to store vocabulary data on matrix
 * columns.  All matrix vocab elements are associated with a single data column 
 * variable, and all data column have an associated unique matrix vocab 
 * element.
 *
 * A major conceptual change in the OpenSHAPA database is the use of matricies
 * for all data columns.  Thus text, integer, float, nominal, and predicate
 * column variables are actualy single element fixed length matricies with 
 * the single formal argument strongly typed to match the column type.  All
 * these matricies are system matricies that cannot be edited by the user
 * and therefore do not appear in the global vocab.
 *
 * The vocab element for matrix column variables remains editiable.
 *
 *                                          -- JRM 3/03/07
 *
 * @author FGA
 */

public class MatrixVocabElement extends VocabElement
{
    
    /*************************************************************************/
    /************************** Type Definitions: ****************************/
    /*************************************************************************/
    
    /**
     * matrixType:  Enumerated type used to specify the type of the matrix.
     *      The UNDEFINED value is used to set the initial value of the type.
     *      All other values are associated with column types. 
     */
    
    public enum MatrixType
        {UNDEFINED, TEXT, NOMINAL, INTEGER, FLOAT, PREDICATE, MATRIX};

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    
    /**
     * type:    Type of the matrix, and of the assoicated column variable.
     *      The system flag must be set iff the type is not MATRIX.
     *
     * itsColID:   ID to the column variable with which this matrix
     *      vocab element is associated.
     *
     * cpfArgList:  Vector containing the formal argument list of the 
     *      column predicate implied by the matrix vocab element and the
     *      associated DataColumn.
     * 
     *      This argument list is simply a duplicate of the regular formal
     *      argument list, with <ord>, <onset>, and <offset> prepended.  At 
     *      present, these arguments are instances  IntFormalArg, 
     *      TimeStampFormalArg, and TimeStampFormalArg respectively -- but
     *      we may wish to change this in the future.
     * 
     *      While the column predicate formal argument list must be visible 
     *      to the outside world, there are no facilites for editing it -- 
     *      instead all edits to the regular formal argument list are mirrored
     *      in the column pred formal argument list.
     */
    
    /** type of the matrix vocab element and its associate column */
    MatrixType type = MatrixType.UNDEFINED;
    
    /** DataColumn which this matrix vocab element describes. */
    long itsColID = DBIndex.INVALID_ID;
     
    /** column predicate formal argument list */
    protected Vector<FormalArgument> cpfArgList = 
            new Vector<FormalArgument>();
   
    
    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/
    
    /** 
     * MatrixVocabElement()
     *
     * Constructor for instances of MatrixVocabElement.
     *
     * Two versions of this constructor -- one which sets the name of the
     * of the new matrix vocab element, and one which doesn't.  Note that 
     * we check to verify that the name is a valid spreadsheet variable name.  
     *
     *                                              JRM -- 3/03/07
     *
     * Changes:
     *
     *    - Added copy constructor.                 JRM -- 4/30/07
     *
     */
   
    public MatrixVocabElement(Database db) 
        throws SystemErrorException
    {
        
        super(db);
        
        this.constructInitCPfArgList();
        
    } /* MatrixVocabElement::MatrixVocabElement(db) */
    
    
    public MatrixVocabElement(Database db, 
                              String name)
        throws SystemErrorException
    {
        
        super(db);
        
        final String mName = 
                "MatrixVocabElement::MatrixVocabElement(db, name): ";
        
        if ( ! Database.IsValidSVarName(name) )
        {
            throw new SystemErrorException(mName + "name is invalid");
        }
                      
        this.name = (new String(name));
        
        this.constructInitCPfArgList();
        
    } /* MatrixVocabElement::MatrixVocabElement(db, name) */
    
    
    public MatrixVocabElement(MatrixVocabElement ve)
        throws SystemErrorException
    {
        
        super(ve);
        
        final String mName = "MatrixVocabElement::MatrixVocabElement(ve): ";
        
        if ( ( ve == null ) || ( ! ( ve instanceof MatrixVocabElement ) ) )
        {
            throw new SystemErrorException(mName + "bad ve");
        }
        
        if ( ! Database.IsValidSVarName(name) )
        {
            throw new SystemErrorException(mName + "name is invalid");
        }
        
        /* The type must be set before the formal arguments can be copied 
         * over, so there is code in the superclass constructor to do this
         * for instances of MatrixVocabElement.  However, our initialization
         * code will overwrite this, so we have to do it again here.
         */
        
        this.type = ve.type;

        this.itsColID = ve.itsColID;
        
        this.cpfArgList = ve.copyCPFormalArgList();
        
    } /* MatrixVocabElement::MatrixVocabElement(ve) */
     
        
    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/
    
    /**
     * getType() and setType()
     *
     * Get and set routines for the type of the matrix vocab element.
     *
     * getType() can be called whenever, but setType() may only be called once,
     * and must set the type to something other than matrixType.UNDEFINED.
     *
     * Note that while we will not allow the type of an instance of 
     * MatrixVocabEntry to be changed once it is set, we will have to allow 
     * users to change the type of column variables.  We will need conversion
     * routines for this, and probably just replace the old column with a 
     * new derived column.
     *
     * Changes:
     *
     *    - None.
     */
    
    public MatrixType getType()
    {
        return type;
        
    } /* MatrixVocabElement::getType() */
    
    public void setType(MatrixType newType)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::setType(): ";
        
        if ( type != MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName + "type has already been set");
        }
        else if ( newType == MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName + "newType is UNDEFINED");
        }
        
        type = newType;
        
        return;
            
    } /* MatrixVocabElement::setType() */
    
    
    /**
     * getItsColID() and setItsColID()
     *
     * Get and set routines for the itsColID field.
     *
     * getItsColID() can be called whenever, but setItsColID may only be 
     * called once, and may not set itsColID to the invalid ID.
     *
     * Changes:
     *
     *    - None.
     */
    
    public long getItsColID()
    {
        return itsColID;
        
    } /* MatrixVocabElement::getItsColID() */
    
    public void setItsColID(long colID)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::setItsColID(): ";
        DBElement dbe;
        DataColumn dc;
        
        if ( this.type == MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName + "type is UNDEFINED");
        }

        if ( itsColID != DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "itsColID already set");
        }
        else if ( colID == DBIndex.INVALID_ID )
        {
            throw new SystemErrorException(mName + "colID == INVALID_ID");
        }
        
        dbe = this.db.idx.getElement(colID);
        
        if ( dbe == null )
        {
            throw new SystemErrorException(mName + "colID has no referenct");
        }
        else if ( ! ( dbe instanceof DataColumn ) )
        {
            throw new SystemErrorException(mName + 
                    "colID doesn't refer to a DataColumn");
        }
        
        dc = (DataColumn)dbe;
        
        if ( this.name.compareTo(dc.getName()) != 0 )
        {
            throw new SystemErrorException(mName + "name mismatch");
        }
        
        if ( this.type != dc.getItsMveType() )
        {
            throw new SystemErrorException(mName + "type mismatch");
        }
        
        this.itsColID = colID;
        
        return;
              
    } /* MatrixVocabElement::setItsColID() */
  
        
    /*************************************************************************/
    /*************************** Overrides: **********************************/
    /*************************************************************************/

    /**
     * toDBString() -- Override of abstract method in DataValue
     * 
     * Returns a database String representation of the DBValue for comparison 
     * against the database's expected value.<br>
     *
     * <i>This function is intended for debugging purposses.</i>
     *
     * @return the string value.
     *
     * Changes:
     *
     *    - None.
     *      
     */
    public String toDBString()         
    {
        String s;
        
        try
        {
            s = "((MatrixVocabElement: ";
            s += getID();
            s += " ";
            s += getName();
            s += ") (system: ";
            s += system;
            s += ") (type: ";
            s += type;
            s += ") (varLen: ";
            s += varLen;
            s += ") (fArgList: ";
            s += fArgListToDBString();
            s += ")";
        }
        
        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }
       
        return s;
        
    } /* MatrixVocabElement::toDBString() */

    
    /**
     * toString() -- Override of abstract method in DataValue
     *
     * Returns a String representation of the DBValue for display.
     *
     * @return the string value.
     *
     * Changes:
     *
     *    - None.
     *      
     */
    public String toString() 
    {
        String s;
        
        try
        {
            s = getName();
            s += fArgListToString();
        }

        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }
               
        return (s);
        
    } /* toString() */
    
    
    /*** accessor overrides ***/
    
    /* setName() -- Override of method in VocabElement
     *
     * Does some additional error checking and then set the name directly.
     * Do this as the superclass mthod will throw a system error if this is
     * a system MVE -- which it is unless this.type == MATRIX.
     *
     *                                              JRM -- 3/04/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public void setName(String name) 
        throws SystemErrorException 
    {
        final String mName = "MatrixVocabElement::setName(): ";

        if ( ( name == null ) || 
             ( ! ( name instanceof String ) ) || 
             ( name.length() <= 0 ) )
        {
            throw new SystemErrorException(mName + "Bad name param");
        }
        
        if ( ! Database.IsValidSVarName(name) )
        {
            throw new SystemErrorException(mName + "Bad name param");
        }
        
        this.name = (new String(name));             
        
        return;
        
    } /* MatrixVocabElement::setName() */

    
    /**
     * setSystem() -- Override
     *
     * Do some additional error checking and then call the superclass version
     * of this method.
     * 
     * Changes:
     *
     *   - None.
     */
    public void setSystem()
        throws SystemErrorException 
    {
        final String mName = "MatrixVocabElement::setSystem(): ";
        
        if ( type == MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName + "type undefined?!?!");
        }
//        /*** TODO:  Uncomment this once we have columns going ***/
//        else if ( itsColumn == null )
//        {
//            throw new SystemErrorException(mName + "itsColumn is null?!?!");
//        }
        else if ( this.name.compareTo("") == 0 )
        {
            throw new SystemErrorException(mName + "name is empty?!?!");
        }
        
        super.setSystem();
        
        return;
        
    } /* MatrixFormalArgument::setSystem() */
    
    
    /*** formal argument list management overrides ***/
    
    /**
     * appendFormalArg() 
     * 
     * Make FormalArgument::appendFormalArg() accessible to 
     * the outside world, but add some error checking.
     * 
     *                                          JRM -- 3/04/07
     * 
     * Changes:
     * 
     *    - Modified function to append a copy of the newArg to the 
     *      column predicate formal argument list.
     *
     *                                          JRM -- 8/09/08
     */
    
    public void appendFormalArg(FormalArgument newArg)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::appendFormalArg(): ";
        boolean typeMisMatch = false;
        
        if ( system )
        {
            throw new SystemErrorException(mName +
                    "can't modify system formal argument.");
        }
        else if ( this.type == MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName + 
                    "must set type before adding arguments.");
        }
        else if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }
        else if ( ( type != MatrixType.MATRIX ) &&
                  ( fArgList.size() != 0 ) )
        {
            throw new SystemErrorException(mName + "too many arguments.");
        }
        else if ( newArg == null )
        {
            throw new SystemErrorException(mName + "newArg is null on entry");
        }
        
        switch ( this.type )
        {
            case FLOAT:
                if ( ! ( newArg instanceof FloatFormalArg ) )
                {
                    typeMisMatch = true;
                }
                break;
                
            case INTEGER:
                if ( ! ( newArg instanceof IntFormalArg ) )
                {
                    typeMisMatch = true;
                }
                break;
                
            case MATRIX:
                if ( newArg instanceof TextStringFormalArg )
                {
                    typeMisMatch = true;
                }
                break;
                
            case NOMINAL:
                if ( ! ( newArg instanceof NominalFormalArg ) )
                {
                    typeMisMatch = true;
                }                
                break;
                
            case PREDICATE:
                if ( ! ( newArg instanceof PredFormalArg ) )
                {
                    typeMisMatch = true;
                }                

                break;
                
            case TEXT:
                if ( ! ( newArg instanceof TextStringFormalArg ) )
                {
                    typeMisMatch = true;
                }
                break;
        }
        
        if ( typeMisMatch )
        {
            throw new SystemErrorException(mName + "type miss match.");
        }
        
        if ( ! cpfArgNameIsUnique(newArg.getFargName()) )
        {
            throw new SystemErrorException(mName + "newArg name not unique.");
        }
        
        super.appendFormalArg(newArg);

        this.appendCPFormalArg(FormalArgument.CopyFormalArg(newArg, true, true));
        
        return;
        
    } /* MatrixVocabElement::appendFormalArg() */
    
       
    /**
     * deleteFormalArg()
     * 
     * Make FormalArgument::deleteFormalArg() accessible to 
     * the outside world, but add some error checking.
     * 
     *                                          JRM -- 3/4/07
     * 
     * Changes:
     * 
     *    - Updated to support maintenance of the column predicate 
     *      formal argument list.
     *                                          JRM -- 8/9/08
     */  
    
    public void deleteFormalArg(int n)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::deleteFormalArg(): ";
        FormalArgument fa = null;
        FormalArgument cpfa = null;

        if ( this.system )
        {
            throw new SystemErrorException(mName +
                    "can't modify system formal argument.");
        }
        else if ( this.type == MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName + 
                    "must set type before deleting arguments.");
        }
        else if ( this.fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }
        else if ( type != MatrixType.MATRIX )
        {
            throw new SystemErrorException(mName + 
               "can't delete formal args from non matrix type vocab elements.");
        }
        else if ( n < 0 )
        {
            /* can't have a negative index -- scream and die */
            throw new SystemErrorException(mName + "negative index supplied");
        }
        else if ( n >= fArgList.size() )
        {
            throw new SystemErrorException(mName + "no nth formal argument");
        }
        else if ( (fArgList.size() + 3) != cpfArgList.size() )
        {
            throw new SystemErrorException(mName + 
                    "unexpected cpfArgList.size()");
        }
        
        fa = this.getFormalArg(n);
        cpfa = this.getCPFormalArg(n + 3);
        
        if ( ! FormalArgument.FormalArgsAreEquivalent(fa, cpfa) )
        {
            System.out.printf("  fa = %s\n", fa.toDBString());
            System.out.printf("cpfa = %s\n", cpfa.toDBString());
            System.out.printf("  fArgList = %s\n", this.fArgListToDBString());
            System.out.printf("cpfArgList = %s\n", this.cpfArgListToDBString());
            
            throw new SystemErrorException(mName + 
                    "target fArg & cpfArg aren't equivalent.");
        }
        
        super.deleteFormalArg(n);
        
        this.deleteCPFormalArg(n + 3);
        
        return;
        
    } /* MatrixVocabElement::deleteFormalArg() */

    
    /**
     * getFormalArg() 
     * 
     * Make FormalArgument::getFormalArg() accessible to 
     * the outside world, but add some error checking.
     * 
     *                                          JRM -- 3/4/07
     * 
     * Changes:
     * 
     *   - None.
     */
    public FormalArgument getFormalArg(int n)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::getFormalArg(): ";
        
        if ( type == MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName + 
                    "must set type before getting arguments.");
        }
        else if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }
        else if ( ( type != MatrixType.MATRIX ) && ( n != 0 ) )
        {
            throw new SystemErrorException(mName + 
                    "n must be 0 if type isn't MATRIX.");
        }
        
        return super.getFormalArg(n);
        
    } /*MatrixVocabElement::getFormalArg() */
    
    
    /**
     * getNumFormalArgs()
     * 
     * Make VocabElement::getNumFormalArgs() public with some additional
     * error checking.
     * 
     *                                      JRM 3/04/07
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public int getNumFormalArgs()
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::getNumFormalArgs(): ";

        if ( type == MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName + 
                    "type must be defined before fArgList manipulations");
        }
        
        return super.getNumFormalArgs();
        
    } /* MatrixVocabElement::getNumFormalArgs() */

    
    /**
     * insertFormalArg()
     * 
     * 
     * Make VocabElement::insertFormalArg() public with some additional
     * error checking.
     * 
     *                                          JRM -- 3/04/07
     * 
     * Changes:
     * 
     *    - Updated function to insert a copy of the supplied formal argument
     *      in the column predicate formal argument list.
     */
    
    public void insertFormalArg(FormalArgument newArg, 
                                int n)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::insertFormalArg(): ";
        boolean typeMisMatch = false;

        if ( system )
        {
            throw new SystemErrorException(mName +
                    "can't modify system formal argument.");
        }
        else if ( type == MatrixType.UNDEFINED )
        {
            throw new SystemErrorException(mName + 
                    "must set type before inserting arguments.");
        }
        else if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }
        else if ( ( type != MatrixType.MATRIX ) &&
                  ( fArgList.size() != 0 ) )
        {
            throw new SystemErrorException(mName + "too many arguments.");
        }
        else if ( ( type != MatrixType.MATRIX ) &&
                  ( n != 0 ) )
        {
            throw new SystemErrorException(mName + 
                    "insertion point must be 0.");
        }
        else if ( newArg == null )
        {
            throw new SystemErrorException(mName + "newArg null on entry.");
        }
        else if ( ! this.cpfArgNameIsUnique(newArg.getFargName()) )
        {
            throw new SystemErrorException(mName + 
                    "newArg.getFargName() isn't unique.");
        }
        
        switch ( type )
        {
            case FLOAT:
                if ( ! ( newArg instanceof FloatFormalArg ) )
                {
                    typeMisMatch = true;
                }
                break;
                
            case INTEGER:
                if ( ! ( newArg instanceof IntFormalArg ) )
                {
                    typeMisMatch = true;
                }
                break;
                
            case NOMINAL:
                if ( ! ( newArg instanceof NominalFormalArg ) )
                {
                    typeMisMatch = true;
                }                
                break;
                
            case PREDICATE:
                if ( ! ( newArg instanceof PredFormalArg ) )
                {
                    typeMisMatch = true;
                }                

                break;
                
            case TEXT:
                if ( ! ( newArg instanceof TextStringFormalArg ) )
                {
                    typeMisMatch = true;
                }
                break;
                
            case MATRIX:
                if ( newArg instanceof TextStringFormalArg )
                {
                    typeMisMatch = true;
                }
                break;
        }
        
        if ( typeMisMatch )
        {
            throw new SystemErrorException(mName + "type miss match.");
        }
        
        super.insertFormalArg(newArg, n);
        
        this.insertCPFormalArg(FormalArgument.CopyFormalArg(newArg, true, true), 
                                                            n + 3);
                
        return;        
        
    } /* MatrixVocabElement::insertFormalArg() */
    
    
    /** 
     * isWellFormed() -- Override
     *
     * Examine the vocab element and return true if it is well formed and 
     * thus acceptable for insertion into the vocab list, and false if it
     * is not.
     *
     * This method is essentially a duplicate of the PredicateVocabElement
     * version with the addition of code that verifies that the type has
     * been set, and that the size and contents of the formal argument
     * list is congruent with the type.
     *
     *                                                  JRM -- 6/19/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public boolean isWellFormed(boolean newVE)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::isWellFormed(): ";
        boolean wellFormed = true;
        int i;
        int j;
        FormalArgument fArg = null;
        FormalArgument scanfArg = null;
        VocabElement ve = null;
        MatrixVocabElement mve = null;
        
        if ( this.getName().length() == 0 )
        {
            wellFormed = false;
        }
        else if ( this.db == null )
        {
            wellFormed = false;
        }
        else if ( ( newVE ) && ( this.db.vl.inVocabList(this.getName()) ) )
        {
            wellFormed = false;
        }
        else if ( ( ! newVE ) && 
                  ( ( this.getID() == DBIndex.INVALID_ID) || 
                    ( ! this.db.vl.inVocabList(this.getID()) ) ) )
        {
            wellFormed = false;
        }
        else if ( this.fArgList.size() <= 0 )
        {
            wellFormed = false;
        }
        else if ( ! Database.IsValidSVarName(this.getName()) )
        {
            wellFormed = false;
            throw new SystemErrorException(mName + "Invalid matrix name");
        }
        else
        {
            if ( ! newVE )
            {
                ve = this.db.vl.getVocabElement(this.getID());
                
                if ( ( ve == null ) ||
                     ( ! ( ve instanceof MatrixVocabElement ) ) )
                {
                    wellFormed = false;
                }
                else
                {
                    mve = (MatrixVocabElement)ve;
                    
                    if ( mve.getType() != this.getType() )
                    {
                        wellFormed = false;
                    }
                }
            }
        }
        
        if ( wellFormed )
        {
            switch(this.getType())
            {
                case FLOAT:
                    if ( this.fArgList.size() != 1 )
                    {
                        wellFormed = false;
                    }
                    else if ( ! ( this.fArgList.get(0) instanceof
                            FloatFormalArg ) )
                    {
                        wellFormed = false;
                    }
                    break;

                case INTEGER:
                    if ( this.fArgList.size() != 1 )
                    {
                        wellFormed = false;
                    }
                    else if ( ! ( this.fArgList.get(0) instanceof 
                            IntFormalArg ) )
                    {
                        wellFormed = false;
                    }
                    break;

                case NOMINAL:
                    if ( this.fArgList.size() != 1 )
                    {
                        wellFormed = false;
                    }
                    else if ( ! ( this.fArgList.get(0) instanceof 
                            NominalFormalArg ) )
                    {
                        wellFormed = false;
                    }
                    break;

                case PREDICATE:
                    if ( this.fArgList.size() != 1 )
                    {
                        wellFormed = false;
                    }
                    else if ( ! ( this.fArgList.get(0) instanceof 
                            PredFormalArg ) )
                    {
                        wellFormed = false;
                    }

                    break;

                case TEXT:
                    if ( this.fArgList.size() != 1 )
                    {
                        wellFormed = false;
                    }
                    else if ( ! ( this.fArgList.get(0) instanceof 
                            TextStringFormalArg ) )
                    {
                        wellFormed = false;
                    }
                    break;

                case MATRIX:
                    if ( this.fArgList.size() < 1 )
                    {
                        wellFormed = false;
                    }
                    else 
                    {
                        i = 0;
                        while ( ( i < this.fArgList.size() ) && ( wellFormed ) )
                        {
                            fArg = this.fArgList.get(i);
                
                            j = 0;
                            while ( ( j < this.fArgList.size() ) && 
                                    (  wellFormed ) )
                            {
                                if ( i != j )
                                {
                                    scanfArg = this.fArgList.get(j);

                                    if ( fArg.getFargName().
                                            compareTo(scanfArg.getFargName()) 
                                            == 0 )
                                    {
                                        wellFormed = false;
                                        throw new SystemErrorException(mName + 
                                                "non unique fArg name");
                                    }
                                }

                                j++;
                            }
                            
                            if ( fArg instanceof TextStringFormalArg )
                            {
                                wellFormed = false;
                                throw new SystemErrorException(mName + 
                                        "Text String fArg in matrix");
                            }
                            
                            i++;
                        }
                    }
                    break;
                
                default:
                    throw new SystemErrorException(mName + 
                                                   "Unknown matrix ve type?!?");
            }
        }
       
        return wellFormed;
        
    } /* MatrixVocabElement::isWellFormed() */
    
    
     /**
      * propagateID() -- Override
      *
      * Propagate the id assigned to the MatrixVocabElement to all current 
      * formal arguments, if any.  This method should be called after the 
      * MatrixVocabElement is assigned an ID and inserted into the vocab list.
      *
      *                                         JRM -- 8/31/07
      *
      * Changes:
      *
      *   - None.
      */

    public void propagateID()
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::propagateID(): ";
        int i;
        int numCPFArgs;
        FormalArgument fArg;

        super.propagateID();
        
        if ( cpfArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }
        
        numCPFArgs = cpfArgList.size();
        
        if ( numCPFArgs > 0 )
        {
            i = 0;
            
            while ( i <= (numCPFArgs - 1) )
            {
                fArg = getCPFormalArg(i);
                fArg.setItsVocabElementID(this.id);
                i++;
            }
        }
         
        return;
         
    } /* MatrixVocabElement::propagateID() */
    
    
    /**
     * replaceFormalArg()
     * 
     * Rewrite of the inherited method to deal with the peculiarities of 
     * matrix vocab elements.
     * 
     *                                          JRM -- 3/04/07
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public void replaceFormalArg(FormalArgument newArg, 
                                 int n)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::replaceFormalArg()";
        boolean unique = true;
        FormalArgument oldArg;
        FormalArgument newCPArg;
        FormalArgument oldCPArg;
        
        if ( fArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "fArgList unitialized?!?!");
        }
        
        if ( cpfArgList == null )
        {
            /* cpfArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "cpfArgList unitialized?!?!");
        }
        
        if ( this.system )
        {
            /* this is a system vocab element, and thus is read only. */
            throw new SystemErrorException(mName + 
                    "attempt to modify a system matrix vocab element.");
        }
        
        if ( newArg == null )
        {
            throw new SystemErrorException(mName + "newArg null on entry.");
        }
        
        if ( n < 0 )
        {
            throw new SystemErrorException(mName + "negative index supplied.");
        }
        
        if ( n >= this.fArgList.size() )
        {
            throw new SystemErrorException(mName + "no n-th formal argument.");
        }
        
        assert( (this.fArgList.size() + 3) == this.cpfArgList.size() );
        
        oldArg = this.getFormalArg(n);
        
        if ( ( this.type != MatrixType.MATRIX ) &&
             ( newArg.getFargType() != oldArg.getFargType() ) )
        {
            throw new SystemErrorException(mName + "In non matrix MVEs, " +
                    "formal arguments may only be replaced with formal " +
                    "arguments of the same type.");
        }
        
        if ( ( this.type == MatrixType.MATRIX ) &&
             ( newArg instanceof TextStringFormalArg ) )
        {
            throw new SystemErrorException(mName + "Text formal arguments, " +
                    "may not appear in MATRIX mve's.");
        }

        for ( FormalArgument t : this.fArgList )
        {
            if ( ( oldArg != t ) &&
                 ( newArg.getFargName().compareTo(t.getFargName()) == 0 ) )
            {
                unique = false;
            }
        }
        
        if ( ! unique )
        {
            throw new SystemErrorException(mName + 
                    "new arg name not unique in fArgList");
        }

        oldCPArg = this.getCPFormalArg(n + 3);
       
        if ( ( oldArg.getID() == newArg.getID() ) &&
             ( oldArg.getID() != DBIndex.INVALID_ID ) )
        {
            if ( oldArg.getFargType() != newArg.getFargType() )
            {
                throw new SystemErrorException(mName + "Attempt to replace " +
                        "old fArg with new fArg with the same ID but of " +
                        "different type.");
            }

            newCPArg = FormalArgument.CopyFormalArg(newArg, true, true);
            
            assert( oldCPArg.getFargType() == newCPArg.getFargType() );
            
            if ( oldCPArg.getID() != DBIndex.INVALID_ID )
            {
                newCPArg.setID(oldCPArg.getID());
            }
        }
        else
        {
            newCPArg = FormalArgument.CopyFormalArg(newArg, true, true);
        }

        for ( FormalArgument t : this.cpfArgList )
        {
            if ( ( oldCPArg != t ) &&
                 ( newArg.getFargName().compareTo(t.getFargName()) == 0 ) )
            {
                unique = false;
            }
        }
        
        if ( ! unique )
        {
            throw new SystemErrorException(mName + 
                    "new arg name not unique in cpfArgList");
        }
        
        newArg.setItsVocabElement(this);
        newArg.setItsVocabElementID(this.getID());  /* may be INVALID_ID */
        
        newCPArg.setItsVocabElement(this);
        newCPArg.setItsVocabElementID(this.getID());  /* may be INVALID_ID */
        
        if ( this.fArgList.set(n, newArg) != oldArg )
        {
            throw new SystemErrorException(mName + "arg replace failed.");
        }
        
        if ( this.cpfArgList.set(n + 3, newCPArg) != oldCPArg )
        {
            throw new SystemErrorException(mName + "cp arg replace failed.");
        }
        
        return;
        
    } /* MatrixVocabElement::replaceFormalArg() */
  
        
    /*************************************************************************/
    /***************************** Methods: **********************************/
    /*************************************************************************/
    
    /** 
     * appendCPFormalArg()
     *
     * Append the supplied formal argument to the end of the column predicate
     * formal argument list.
     *
     *                                          JRM -- 8/9/08
     *
     * Changes:
     *
     *    - None.
     *
     */
    
    private void appendCPFormalArg(FormalArgument newArg)
        throws SystemErrorException
    {
        final String mName = "VocabElement::appendCPFormalArg(): ";

        if ( cpfArgList == null )
        {
            /* cpfArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "cpfArgList unitialized?!?!");
        }
        
        if ( this.system )
        {
            /* this is a system vocab element, and thus is read only. */
            throw new SystemErrorException(mName + 
                    "attempt to modify a system vocab element.");
        }
        
        if ( newArg == null )
        {
            throw new SystemErrorException(mName + 
                    "Attempt to insert null formal argument");
        }
        else if ( ! ( newArg instanceof FormalArgument ) )
        {
            throw new SystemErrorException(mName + 
                    "newArg not a formal argument");
        }
        else if ( ! cpfArgNameIsUnique(newArg.getFargName()) )
        {
            throw new SystemErrorException(mName + "newArg name not unique.");
        }
        
        cpfArgList.add(newArg);
        
        newArg.setItsVocabElement(this);
        newArg.setItsVocabElementID(this.getID());  /* may be INVALID_ID */
        
        return;
        
    } /* VocabElement::appendCPFormalArg() */
    
    
    /**
     * constructInitCPfArgList()
     * 
     * Construct the formal argument list for the column predicate implied
     * by the MatrixVocabElement.  This method is called at construction time
     * only, and should not be called otherwise.
     * 
     * Note that the method presumes that this.db has been initialized prior
     * to the method's invocation.
     * 
     *                                          JRM -- 8/9/08
     * 
     * Changes:
     * 
     *    - None.
     */
    
    private void constructInitCPfArgList()
        throws SystemErrorException
    {
        final String mName = 
                "MatrixVocabElement::constructInitCPfArgList(): ";
        FormalArgument fa = null;
        
        if ( this.db == null )
        {
            throw new SystemErrorException(mName + "this.db null on entry.");
        }
        
        if ( this.cpfArgList == null )
        {
            throw new SystemErrorException(mName + 
                    "this.colPredFormalArgList == null?!?");
        }
        
        if ( this.cpfArgList.size() != 0 )
        {
            throw new SystemErrorException(mName + 
                    "this.colPredFormalArgList.size() != 0?!?");
        }
        
        fa = new IntFormalArg(this.db, "<ord>");
        fa.setItsVocabElement(this);
        fa.setItsVocabElementID(this.getID());  /* will be INVALID_ID */
        this.cpfArgList.add(fa);
        
        fa = new TimeStampFormalArg(this.db, "<onset>");
        fa.setItsVocabElement(this);
        fa.setItsVocabElementID(this.getID());  /* will be INVALID_ID */
        this.cpfArgList.add(fa);
        
        fa = new TimeStampFormalArg(this.db, "<offset>");
        fa.setItsVocabElement(this);
        fa.setItsVocabElementID(this.getID());  /* will be INVALID_ID */
        this.cpfArgList.add(fa);
        
        if ( this.cpfArgList.size() != 3 )
        {
            throw new SystemErrorException(mName + 
                    "this.colPredFormalArgList.size() != 3");
        }
        
        return;
        
    } /* "MatrixVocabElement::constructInitCPfArgList() */
    

    /**
     * cpfArgListIsValid()
     *
     * Verify that the column predicate formal argument list is valid.  Return
     * true if it is, and false otherwise.
     *
     *                                          JRM -- 8/9/08
     *
     * Changes:
     *
     *    - None.
     *
     */

    private boolean cpfArgListIsValid()
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::cpfArgListIsValid()";
        boolean valid = true;
        int i;
        FormalArgument cpfa = null;
        FormalArgument fa = null;
        
        if ( ( this.cpfArgList.size() < 4 ) ||
             ( this.cpfArgList.size() != ( this.fArgList.size() + 3 ) ) )
        {
            return false;
        }

        cpfa = cpfArgList.get(0);
        if ( ( cpfa == null ) ||
             ( ! ( cpfa instanceof IntFormalArg ) ) ||
             ( cpfa.getFargName().compareTo("<ord>") != 0 ) )
        {
            return false;
        }

        cpfa = cpfArgList.get(1);
        if ( ( cpfa == null ) ||
             ( ! ( cpfa instanceof TimeStampFormalArg ) ) ||
             ( cpfa.getFargName().compareTo("<onset>") != 0 ) )
        {
            return false;
        }

        cpfa = cpfArgList.get(2);
        if ( ( cpfa == null ) ||
             ( ! ( cpfa instanceof TimeStampFormalArg ) ) ||
             ( cpfa.getFargName().compareTo("<offset>") != 0 ) )
        {
            return false;
        }
        
        i = 3;
        
        while ( i < cpfArgList.size() )
        {
            fa = fArgList.get(i - 3);
            cpfa = cpfArgList.get(i);
            
            if ( ( fa == null ) || ( cpfa == null ) )
            {
                return false;
            }
            else if ( ! FormalArgument.FormalArgsAreEquivalent(fa, cpfa) )
            {
                return false;
            }
            
            i++;
        }
        
        return true;
        
    } /* MatrixVocabElement::cpfArgListIsValid() */
    
    
    /**
     * cpfArgListToDBString()
     *
     * Construct a string containing the names of the formal arguments in 
     * the column predicate implied by this MatrixVocabElement in a
     * format that displays the full status of the formal arguments and 
     * facilitates debugging.  
     *                                          JRM -- 8/9/08
     *
     * Changes:
     *
     *    - None.
     *      
     */
    
    private String cpfArgListToDBString()
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::cpfArgListToDBString(): ";
        int i = 0;
        int numCPFArgs = 0;
        String s = new String("(");

        if ( cpfArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + 
                    "cpfArgList unitialized?!?!");
        }
        
        numCPFArgs = cpfArgList.size();
        
        if ( numCPFArgs < 3 )
        {
            throw new SystemErrorException(mName + "numCPFArgs < 3");
        }
        
        if ( numCPFArgs > 0 )
        {
            while ( i < (numCPFArgs - 1) )
            {
                s += getCPFormalArg(i).toDBString() + ", ";
                i++;
            }
            s += getCPFormalArg(i).toDBString();
        }
        
        s += ")";
        
        return s;
        
    } /* MatrixVocabElement::cpfArgListToDBString() */
        
    
    /**
     * cpfArgListToString()
     *
     * Construct a string containing the names of the formal arguments of the
     * column predicate implied by the MatrixVocabElement in the 
     * format: (<ord>, <onset>, <offset>, <arg0>, <arg1>, ... <argn>). 
     * 
     *                                          JRM -- 8/9/08
     *
     * Changes:
     *
     *    - None.
     *      
     */
    
    private String cpfArgListToString()
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::cpfArgListToString(): ";
        int i = 0;
        int numCPFArgs = 0;
        String s = new String("(");

        if ( cpfArgList == null )
        {
            /* cpfArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "cpfArgList unitialized?!?!");
        }

        numCPFArgs = cpfArgList.size();
        
        if ( numCPFArgs < 3 )
        {
            throw new SystemErrorException(mName + "numCPFArgs < 3");
        }
        
        if ( numCPFArgs > 0 )
        {
            while ( i < (numCPFArgs - 1) )
            {
                s += getCPFormalArg(i).toString() + ", ";
                i++;
            }
            s += getCPFormalArg(i).toString();
        }
        
        s += ")";
        
        return s;
        
    } /* MatrixVocabElement::cpfArgListToString() */

    
    /**
     * cpfArgNameIsUnique()
     *
     * Scan the column predicate formal argument list, and test to see if the 
     * supplied formal argument list is unique.  Return true if it is, and 
     * false otherwise.
     *
     *                                      JRM -- 8/9/08
     *
     * Changes:
     *
     *    - None.
     */
    
    private boolean cpfArgNameIsUnique(String fArgName)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::cpfArgNameIsUnique(): ";
        boolean unique = true;
        
        if ( fArgName == null )
        {
            throw new SystemErrorException(mName + "fArgName null on entry.");
        }
        else if ( ! Database.IsValidFargName(fArgName) )
        {
            throw new SystemErrorException(mName + 
                    "fArgName not a valid formal arg name.");
        }
        else if ( this.cpfArgList == null )
        {
            throw new SystemErrorException(mName + "this.cpfArgList null??");
        }
    
        for ( FormalArgument t : this.cpfArgList )
        {
            if ( fArgName.compareTo(t.getFargName()) == 0 )
            {
                unique = false;
            }
        }
        
        return unique;
        
    } /* MatrixVocabElement::cpfArgNameIsUnique() */
    
    
    /**
     * copyCPFormalArg()
     *
     * Returns a copy of the n-th column predicate formal argument, or null 
     * if there is no such argument.  
     *                                          JRM -- 8/10/08
     *
     * Changes:
     *
     *   - None.
     *
     */
    
    protected FormalArgument copyCPFormalArg(int n)
        throws SystemErrorException
    {
        final String mName = "VocabElement::copyCPFormalArg(): ";
        FormalArgument fArg = null;
        FormalArgument fArgCopy = null;

        if ( cpfArgList == null )
        {
            /* cpfArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "cpfArgList unitialized?!?!");
        }
        else if ( n < 0 )
        {
            /* can't have a negative index -- scream and die */
            throw new SystemErrorException(mName + "negative index supplied");
        }
        else if ( n >= cpfArgList.size() )
        {
            /* n-th formal argument doesn't exist -- return null */
            return null;
        }

        fArg = cpfArgList.get(n);
        
        fArgCopy = FormalArgument.CopyFormalArg(fArg, false, false);
        
        if ( fArgCopy == null )
        {
            throw new SystemErrorException(mName + "fArgcopy is null");
        }

        return fArgCopy;

    } /* VocabElement::copyCPFormalArg() */
    
    
    /**
     * copyCPFormalArgList()
     *
     * Construct and return a vector containing a copy of the column predicate
     * formal argument list.  
     *                                          JRM -- 8/10/08
     *
     * Changes:
     *
     *    - None.
     */
    
    protected java.util.Vector<FormalArgument> copyCPFormalArgList()
        throws SystemErrorException
    {
        final String mName = "VocabElement::copyCPFormalArgList()";
        int i;
        java.util.Vector<FormalArgument> copy = 
                new java.util.Vector<FormalArgument>();
        
        if ( this.cpfArgList == null )
        {
            /* cpfArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "cpfArgList unitialized?!?!");
        }
        
        for ( i = 0; i < this.cpfArgList.size(); i++)
        { 
            copy.add(this.copyCPFormalArg(i));
        }
        
        return copy;
        
    } /* VocabElement::copyCPFormalArgList() */
    
       
    /**
     * deleteCPFormalArg()
     *
     * Delete the n-th formal argument from the column predicate formal 
     * argument list.  Throw a system error exception if there is no n-th 
     * formal argument.
     *
     *                                          JRM -- 8/09/08
     *
     * Changes:
     *
     *    - None.
     *
     */  
    
    private void deleteCPFormalArg(int n)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::deleteCPFormalArg(): ";
        FormalArgument deletedArg = null;

        if ( cpfArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + 
                    "cpfArgList unitialized?!?!");
        }
        else if ( this.system )
        {
            /* this is a system vocab element, and thus is read only. */
            throw new SystemErrorException(mName + 
                    "attempt to modify a system vocab element.");
        }
        else if ( n < 0 )
        {
            /* can't have a negative index -- scream and die */
            throw new SystemErrorException(mName + "negative index supplied");
        }
        else if ( n >= cpfArgList.size() )
        {
            throw new SystemErrorException(mName + "no nth formal argument");
        }
        
        deletedArg = cpfArgList.remove(n);
        
        if ( deletedArg == null )
        {
            throw new SystemErrorException(mName + "deleted arg is null");
        }
        
        deletedArg.setItsVocabElement(null);
        deletedArg.setItsVocabElementID(DBIndex.INVALID_ID);
        
        return;
        
    } /* MatrixVocabElement::deleteCPFormalArg() */
    
    
    /**
     * getCPFormalArg() 
     *
     * Returns a reference to the n-th formal argument in the column predicate 
     * implied by the MatrixVoacabElement, or null if there is no such argument.
     *
     * N.B. The formal argument whose referene is returned is the formal 
     *      argument that appears in the column predicate formal argument 
     *      list.  Thus the caller must be careful not to alter it.
     *
     *                                          JRM -- 8/9/08
     *
     * Changes:
     *
     */

    protected FormalArgument getCPFormalArg(int n)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::getCPFormalArg(): ";
        FormalArgument cpfArg = null;

        if ( cpfArgList == null )
        {
            /* cpfArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "cpfArgList unitialized?!?!");
        }
        else if ( n < 0 )
        {
            /* can't have a negative index -- scream and die */
            throw new SystemErrorException(mName + "negative index supplied");
        }
        else if ( n >= cpfArgList.size() )
        {
            /* n-th formal argument doesn't exist -- return null */
            return null;
        }
        
        cpfArg = cpfArgList.get(n);
        
        if ( cpfArg == null )
        {
            throw new SystemErrorException(mName + "cpfArg is null?!?");
        }
        if ( ! ( ( cpfArg instanceof UnTypedFormalArg ) ||
                 ( cpfArg instanceof ColPredFormalArg ) ||
                 ( cpfArg instanceof IntFormalArg ) ||
                 ( cpfArg instanceof FloatFormalArg ) ||
                 ( cpfArg instanceof TimeStampFormalArg ) ||
                 ( cpfArg instanceof QuoteStringFormalArg ) ||
                 ( cpfArg instanceof TextStringFormalArg ) ||
                 ( cpfArg instanceof NominalFormalArg ) ||
                 ( cpfArg instanceof PredFormalArg ) ) )
        {
            throw new SystemErrorException(mName + "cpfArg of unknown type");
        }
        
        return cpfArg;
        
    } /* MatrixVocabElement::getCPFormalArg() */
    
    
    /**
     * getNumCPFormalArgs()
     *
     * Return the number of formal arguments in the column predicate implied
     * by the MatrixVocabElement.
     *
     *                                      JRM 8/9/08
     *
     * Changes:
     *
     *    - None.
     */
    
    public int getNumCPFormalArgs()
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::getNumCPFormalArgs(): ";
        int cpfArgListLen;
        int fArgListLen;

        if ( cpfArgList == null )
        {
            /* fArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + 
                    "cpfArgList unitialized?!?!");
        }
        
        cpfArgListLen = this.cpfArgList.size();
        fArgListLen = this.fArgList.size();

        if ( cpfArgListLen != ( fArgListLen + 3 ) )
        {
            throw new SystemErrorException(mName + 
                    "cpfArgListLen != ( fArgListLen + 3 )!?!");
        }
        
        return cpfArgList.size(); 
        
    } /* MatrixVocabElement::getNumCPFormalArgs() */
    
        
    /**
     * getNumElements()
     *
     * Gets the number of elements in the Matrix
     *
     * Changes:
     *
     *    - None.
     */
  
    public int getNumElements()
        throws SystemErrorException
    {
        return (this.getNumFormalArgs());
    
    } /* MatrixFormalArgument::getNumElements() */
    

    /**
     * insertCPFormalArg()
     *
     * Insert the supplied formal argument in the n-th position in the 
     * column predicate formal argument list.  If n is not zero, there must 
     * be at least n-1 formal arguments in the list to begin with.  Any 
     * existing arguments with index greater than or equal to n have their 
     * indicies increased by 1.
     *
     *                                          JRM -- 8/9/08
     *
     * Changes:
     *
     *    - None.
     *
     */
    
    private void insertCPFormalArg(FormalArgument newArg, 
                                     int n)
        throws SystemErrorException
    {
        final String mName = "VocabElement::insertCPFormalArg(): ";
        
        if ( cpfArgList == null )
        {
            /* cpfArgList hasn't been instantiated yet -- scream and die */
            throw new SystemErrorException(mName + "cpfArgList unitialized?!?!");
        }
        else if ( system )
        {
            /* attempt to insert an argument in a system (and thus read only)
             * vocab element.
             */
            throw new SystemErrorException(mName + 
                    "attempt to modify a system vocab element.");
        }
        else if ( newArg == null )
        {
            throw new SystemErrorException(mName + 
                    "Attempt to insert null formal argument");
        }
        else if ( ! ( newArg instanceof FormalArgument ) )
        {
            throw new SystemErrorException(mName + 
                    "newArg not a formal argument");
        } 
        else if ( ! cpfArgNameIsUnique(newArg.getFargName()) )
        {
            throw new SystemErrorException(mName + "newArg name not unique.");
        }
        else  if ( n < 0 )
        {
            /* can't have a negative index -- scream and die */
            throw new SystemErrorException(mName + "negative index supplied");
        }
        else if ( n > cpfArgList.size() )
        {
            /* n-1-th formal argument doesn't exist -- scream and die */
            throw new SystemErrorException(mName + "n > cp arg list len");
        }
        
        cpfArgList.insertElementAt(newArg, n);
        
        newArg.setItsVocabElement(this);
        newArg.setItsVocabElementID(this.getID());  /* may be INVALID_ID */
        
        return;        
        
    } /* MatrixVocabElement::insertCPFormalArg() */
    

// delete this eventually -- JRM
//    /**
//     * replaceCPFormalArg()
//     *
//     * If the n-th column predicate formal argument exists, replace it with 
//     * the supplied formal argument.
//     * 
//     * Throw a system error exception if there is no n-th formal column
//     * predicate argument to begin with.
//     *
//     *                                          JRM -- 2/27/07
//     *
//     * Changes:
//     *
//     *    - None.
//     *
//     */
//    
//    protected void replaceCPFormalArg(FormalArgument newCPArg, 
//                                      int n)
//        throws SystemErrorException
//    {
//        final String mName = "MatrixVocabElement::replaceCPFormalArg()";
//        
//        deleteCPFormalArg(n);
//        insertCPFormalArg(newCPArg, n);
//        
//        return;
//        
//    } /* MatrixVocabElement::replaceCPFormalArg() */
    

    /**
     * toCPDBString()
     * 
     * Returns a String representation of the column predicate implied by
     * the instance of MatrixVocabElement for comparison against the 
     * database's expected value.<br>
     *
     * <i>This function is intended for debugging purposses.</i>
     *
     * @return the string value.
     *
     * Changes:
     *
     *    - None.
     *      
     */
    public String toCPDBString()         
    {
        String s;
        
        try
        {
            s = "((ColumnPredicate: ";
            s += getID();
            s += " ";
            s += getName();
            s += ") (system: ";
            s += system;
            s += ") (type: ";
            s += type;
            s += ") (varLen: ";
            s += varLen;
            s += ") (fArgList: ";
            s += cpfArgListToDBString();
            s += ")";
        }
        
        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }
       
        return s;
        
    } /* MatrixVocabElement::toDBString() */

    
    /**
     * toCPString()
     *
     * Returns a String representation of the column predicate that is 
     * implied by the instance of MatrixVocabElement.
     *
     * @return the string value.
     *
     * Changes:
     *
     *    - None.
     *      
     */
    public String toCPString() 
    {
        String s;
        
        try
        {
            s = getName();
            s += cpfArgListToString();
        }

        catch (SystemErrorException e)
        {
             s = "FAILED with SystemErrorException \"" + e.toString() + "\")";
        }
               
        return (s);
        
    } /* toString() */
    

    
    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/
    
    /**
     * TestAccessors()
     *
     * Run a battery of tests on the accessors for this class.
     *
     * Changes:
     *
     *    - None.
     */
    
    public static boolean TestAccessors(java.io.PrintStream outStream,
                                        boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing class MatrixVocabElement accessors                       ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwInvalidFargNameException = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        ve = new MatrixVocabElement(new ODBCDatabase(), "test");
        
        if ( ve == null )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.print("new MatrixVocabElement() returned null.\n");
            }
        }
        
        /* test the inherited accessors */
        if ( failures == 0 )
        {
            threwSystemErrorException = false;
            
            try
            {
                ve.setType(MatrixType.MATRIX); /* test will fail otherwise */
                failures += VocabElement.TestAccessors(ve, true, 
                                                       outStream, verbose);
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( threwSystemErrorException )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("AbstractFormalArgument.TestAccessors()" +
                            " threw a SystemErrorException: \"%s\"\n",
                            systemErrorExceptionString);
                }
            }
        }
        
        /* MatrixVocabElement both adds new fields and does extensive error 
         * checking.  Thus we have a lot more work to do.  
         */
        
        /* the setName method adds test code to verify that the new name is a 
         * valid spreadsheet variable name.  Run a quick test to verify that 
         * the method will throw a system error if supplied an invalid 
         * spreads sheet variable name.
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve.setName("in,valid");
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }
        
            
            if ( ( methodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print(
                                "\"ve.setName(\"in,valid\")\" returned.\n");
                    }

                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "\"ve.setName(\"in,valid\")\" failed to " +
                                "throw a SystemErrorException.\n");
                    }
                }
            }
        }
        
        /* the itsColumn field and the associated getItsColumn() and 
         * setItsColumn() accessors in flux at present, so we will not test
         * them now.  
         *
         * TODO: fix this as soon as the underlying design settles.
         */
        
        /* MatrixVocabElement adds the type field, which specifies the type of 
         * the spreadsheet variable with which it is associated.  Since the 
         * type is tightly bound to the types and numbers of formal arguments
         * permitted, this field is initializes to UNDEFINED, and most operations
         * on the instance of MatrixVocabElement are disabled until the type
         * has been specified.  
         *
         * Here, we verify that setSystem() will fail if type has not been
         * specified.
         *
         * TODO: Also verify that setItSColumn() fails if the type has not 
         *       been set.
         *
         * Methods modifying the formal argument list will not junction until
         * the type has been set, and the selected type constricts the number 
         * and type of formal arguments.  However, we will test this in the 
         * formal argument list management tests.
         *
         * Finally, once a type has been selected, it may not be changed.  
         *
         * Tests follow:
         */  
        
        /* start by allocating a fresh instance of MatrixVocabElement, and
         * verifying that the type field is initialized correctly.
         */
        if ( failures == 0 )
        {
            MatrixType initType = MatrixType.FLOAT;
            
            threwSystemErrorException = false;
            
            try
            {
                ve = null;
                ve = new MatrixVocabElement(new ODBCDatabase(), "test");
                initType = ve.getType();
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( ve == null ) ||
                 ( initType != MatrixType.UNDEFINED ) ||
                 ( ve.type != MatrixType.UNDEFINED ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ve == null )
                    {
                        outStream.print("Couldn't allocate instance of " +
                                "MatrixVocabElement for type init value test.\n");
                    }
                    
                    if ( ( initType != MatrixType.UNDEFINED ) ||
                         ( ve.type != MatrixType.UNDEFINED ) )
                    {
                        outStream.print("Unexpected initial value of type.\n");
                    }
                
                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(100): %s.\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Now, try to set system to true with type still undefined.  
         * Should fail.  We don't bother to test the other way round as
         * that has already been tested in VocabElement.TestAccessors()
         * above.
         */
        if ( failures == 0 )
        {
            threwSystemErrorException = false;
            methodReturned = false;
            
            try
            {
                ve.setSystem();
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( methodReturned ) || 
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print(
                                "setSystem() returned with type UNDEFINED.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("setSystem() with type undefined " +
                                "failed to throw a system error.\n");
                    }
                }
            }
        }
        
        if ( ( failures == 0 ) && ( ve.type != MatrixType.UNDEFINED ) )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.print("Unexpected value of ve.type(1).\n");
            }
        }
        
        /* Try to set the type to UNDEFINED.  Should fail with a system error. */ 
        if ( failures == 0 )
        {
            threwSystemErrorException = false;
            methodReturned = false;
            
            try
            {
                ve.setType(MatrixType.UNDEFINED);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( methodReturned ) || 
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print("setType(UNDEFINED) returned.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("setType(UNDEFINED) failed to throw " +
                                "a system error.\n");
                    }
                }
            }
        }
        
        if ( ( failures == 0 ) && ( ve.type != MatrixType.UNDEFINED ) )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.print("Unexpected value of ve.type(2).\n");
            }
        }
        
        /* Finally, set type to some legal value, and then try to change it.
         * The first operation should succeed, the second should fail with 
         * a system error.
         */
        if ( failures == 0 )
        {
            boolean secondMethodReturned = false;
            
            threwSystemErrorException = false;
            methodReturned = false;
            
            try
            {
                ve.setType(MatrixType.FLOAT);
                methodReturned = true;
                ve.setType(MatrixType.INTEGER);
                secondMethodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( ! methodReturned ) || 
                 ( secondMethodReturned ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "first call to setType() failed to return.\n");
                    }
                    
                    if ( secondMethodReturned )
                    {
                        outStream.print("second call to setType() returned.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        if ( methodReturned )
                        {
                            outStream.print("second call to setType failed to " +
                                    "throw a system error.\n");
                        }
                        else
                        {
                            outStream.printf(
                                    "Unexpected system error exception " +
                                    "in first call to setType: \"%s\"\n",
                                    systemErrorExceptionString);
                        }
                    }
                }
            }
        }
        
        if ( ( failures == 0 ) && ( ve.type != MatrixType.FLOAT ) )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.print("Unexpected value of ve.type(3).\n");
            }
        }
         
        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }
        
        return pass;
        
    } /* MatrixVocabElement::TestAccessors() */
    
    /**
     * TestArgListManagement()
     *
     * Run a battery of tests on the formal argument list management methods 
     * for this class.
     *                                          JRM -- 3/18/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public static boolean TestArgListManagement(java.io.PrintStream outStream,
                                                boolean verbose)
        throws SystemErrorException
    {
        String testBanner =
            "Testing class MatrixVocabElement formal arg list management      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String SystemErrorExceptionString = null;
        boolean threwInvalidFargNameException = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        ve = new MatrixVocabElement(new ODBCDatabase(), "test");
        
        if ( ve == null )
        {
            failures++;
            
            if ( verbose )
            {
                outStream.print("new MatrixVocabElement() returned null.\n");
            }
        }
        
        /* test the inherited accessors */
        if ( failures == 0 )
        {
            threwSystemErrorException = false;
            
            try
            {
                ve.setType(MatrixType.MATRIX); /* test will fail otherwise */
                failures += VocabElement.TestfArgListManagement(ve, 
                                                                outStream, 
                                                                verbose);
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }
            
            if ( threwSystemErrorException )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf("FormalArgument.TestfArgListManagement()" +
                            " threw a SystemErrorException: \"%s\"\n",
                            SystemErrorExceptionString);
                }
            }
        }
        
        /* MatrixVocabElement makes a lot of changes to the formal argument
         * list management code, mostly in the area of error checking, as 
         * most matrix types can only take one formal argument, and that of 
         * a specified type.
         *
         * We will test these restrictions shortly.  However another change 
         * is the addition of the getNumElements() method, which is just another
         * name for getNumFormalArgs().  Thus it is probably sufficient to 
         * just call getNumElements() and verify that it returns the expected
         * value.
         *
         * As it happens, the above inherited test routine leaves the predicate
         * vocab element with 7 arguments.  Call getNumElements() now and 
         * verify that it returns 7.
         */
        
        if ( failures == 0 )
        {
            if ( ve.getNumElements() != 7 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "getNumElements() returned unexpected value: %d\n",
                            ve.getNumElements());
                }
            }
        }
        
        /* Now on to testing the type and number of argument restrictions on 
         * all types of MatrixVocabElements with the exception of 
         * matrixType.MATRIX.  These tests are extensive, so I have put the 
         * tests for each type in its own method.
         */
        
        if ( failures == 0 )
        {
            int progress = 0;
            
            try
            {
                failures += TestIntArgListManagement(outStream, verbose);
                progress++;
                failures += TestFloatArgListManagement(outStream, verbose);
                progress++;
                failures += TestNominalArgListManagement(outStream, verbose);
                progress++;
                failures += TestTextArgListManagement(outStream, verbose);
                progress++;
                failures += TestMatrixArgListManagement(outStream, verbose);
                progress++;
                failures += TestPredArgListManagement(outStream, verbose);
                progress = 10;
            }
            
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                SystemErrorExceptionString = e.toString();
            }
            
            if ( ( progress < 10 ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( progress < 10 )
                    {
                        outStream.printf("Typed arg list management tests " +
                                "did not complete.  Progress = %d\n", progress);
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in typed " +
                                "arg list management tests: \"%s\"\n",
                                SystemErrorExceptionString);
                    }
                }
            }
        }
         
        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }
        
        return pass;
        
    } /* MatrixVocabElement::TestArgListManagement() */

    
    /**
     * TestFloatArgListManagement()
     *
     * Run a battery of tests on the formal argument list management methods 
     * for float instances of this class.
     *
     *                                          JRM -- 3/22/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public static int TestFloatArgListManagement(java.io.PrintStream outStream,
                                                 boolean verbose)
        throws SystemErrorException
    {
        String invalidFargNameExceptionString = null;
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;
        
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve = new MatrixVocabElement(new ODBCDatabase(), "float_test");
                ve.setType(MatrixType.FLOAT);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( ! methodReturned ) || ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.printf("Initializtion for float type " +
                                "test failed to complete.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected SystemErrorException " +
                                "in float test(1): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* try to append & insert non integer arguments -- should fail */
        if ( failures == 0 ) 
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                failures += VerifyTypeMisMatchError(ve,
                                                    new IntFormalArg(ve.getDB()),
                                                    "float",
                                                    "IntFormalArg",
                                                    outStream,
                                                    verbose,
                                                    2);

                failures += VerifyTypeMisMatchError(ve,
                                                    new NominalFormalArg(ve.getDB()),
                                                    "float",
                                                    "NominalFormalArg",
                                                    outStream,
                                                    verbose,
                                                    3);

                failures += VerifyTypeMisMatchError(ve,
                                                    new QuoteStringFormalArg(ve.getDB()),
                                                    "float",
                                                    "QuoteStringFormalArg",
                                                    outStream,
                                                    verbose,
                                                    4);

                failures += VerifyTypeMisMatchError(ve,
                                                    new TextStringFormalArg(ve.getDB()),
                                                    "float",
                                                    "TextStringFormalArg",
                                                    outStream,
                                                    verbose,
                                                    5);

                failures += VerifyTypeMisMatchError(ve,
                                                    new TimeStampFormalArg(ve.getDB()),
                                                    "float",
                                                    "TimeStampFormalArg",
                                                    outStream,
                                                    verbose,
                                                    6);
                
                /*** TODO:  Add predicate formal arguments when available ***/
                
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( ! methodReturned ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("Type mismatch tests for float " +
                                "matrix failed to complete.\n");
                    }
                                        
                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(10): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Verify that none of the insertions took */
        if ( failures == 0 )
        {
            if ( ve.getNumFormalArgs() != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected number of formal args(11): %d\n",
                            ve.getNumFormalArgs());
                }
            }
        }
         
        /* try to append an float argument -- should succeed */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve.appendFormalArg(new FloatFormalArg(ve.getDB(), "<float0>"));
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( ! methodReturned ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "appendFormalArg(new FloatFormalArg(<float0>)) " +
                                "in a float matrix didn't return.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(12): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElement.VerifyfArgListContents(ve,
                                                            "(<float0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             13);
         }
       
        /* try to append a second integer argument -- should fail */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve.appendFormalArg(new FloatFormalArg(ve.getDB(), "<float1>"));
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( methodReturned ) || 
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print(
                                "appendFormalArg(new FloatFormalArg(<float1>)) " +
                                "in an float matrix returned.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "appendFormalArg(new FloatFormalArg(<float1>)) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }
        
        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElement.VerifyfArgListContents(ve,
                                                            "(<float0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             15);
         }
        
        /* Finally, try to insert a second float formal argument 
         *                              -- should fail 
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve.insertFormalArg(new FloatFormalArg(ve.getDB(), "<float2>"), 0);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( methodReturned ) || 
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print(
                                "insertFormalArg(new FloatFormalArg(<float2>)) " +
                                "in an float matrix returned.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "insertFormalArg(new FloatFormalArg(<float2>)) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }
        
        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElement.VerifyfArgListContents(ve,
                                                            "(<float0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             17);
         }
        
        
        return failures;
        
    } /* MatrixVocabElement::TestFloatArgListManagement() */

    
    /**
     * TestIntArgListManagement()
     *
     * Run a battery of tests on the formal argument list management methods 
     * for integer instances of this class.
     *
     *                                          JRM -- 3/22/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public static int TestIntArgListManagement(java.io.PrintStream outStream,
                                               boolean verbose)
        throws SystemErrorException
    {
        String invalidFargNameExceptionString = null;
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;
        
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve = new MatrixVocabElement(new ODBCDatabase(), "int_test");
                ve.setType(MatrixType.INTEGER);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( ! methodReturned ) || ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.printf("Initializtion for integer type " +
                                "test failed to complete.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected SystemErrorException " +
                                "in Integer test(1): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* try to append & insert non integer arguments -- should fail */
        if ( failures == 0 ) 
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                failures += VerifyTypeMisMatchError(ve,
                        new FloatFormalArg(ve.getDB()),
                        "integer",
                        "FloatFormalArg",
                        outStream,
                        verbose,
                        2);

                failures += VerifyTypeMisMatchError(ve,
                        new NominalFormalArg(ve.getDB()),
                        "integer",
                        "NominalFormalArg",
                        outStream,
                        verbose,
                        3);

                failures += VerifyTypeMisMatchError(ve,
                        new QuoteStringFormalArg(ve.getDB()),
                        "integer",
                        "QuoteStringFormalArg",
                        outStream,
                        verbose,
                        4);

                failures += VerifyTypeMisMatchError(ve,
                        new TextStringFormalArg(ve.getDB()),
                        "integer",
                        "TextStringFormalArg",
                        outStream,
                        verbose,
                        5);

                failures += VerifyTypeMisMatchError(ve,
                        new TimeStampFormalArg(ve.getDB()),
                        "integer",
                        "TimeStampFormalArg",
                        outStream,
                        verbose,
                        6);
                
                /*** TODO:  Add predicate formal arguments when available ***/
                
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( ! methodReturned ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("Type mismatch tests for integer " +
                                "matrix failed to complete.\n");
                    }
                                        
                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(10): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Verify that none of the insertions took */
        if ( failures == 0 )
        {
            if ( ve.getNumFormalArgs() != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected number of formal args(11): %d\n",
                            ve.getNumFormalArgs());
                }
            }
        }
         
        /* try to append an integer argument -- should succeed */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve.appendFormalArg(new IntFormalArg(ve.getDB(), "<int0>"));
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( ! methodReturned ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "appendFormalArg(new IntFormalArg(<int0>)) " +
                                "in an integer matrix didn't return.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(12): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElement.VerifyfArgListContents(ve,
                                                            "(<int0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             13);
         }
       
        /* try to append a second integer argument -- should fail */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve.appendFormalArg(new IntFormalArg(ve.getDB(), "<int1>"));
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( methodReturned ) || 
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print(
                                "appendFormalArg(new IntFormalArg(<int1>)) " +
                                "in an integer matrix returned.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "appendFormalArg(new IntFormalArg(<int1>)) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }
        
        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElement.VerifyfArgListContents(ve,
                                                            "(<int0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             15);
         }
        
        /* Finally, try to insert a second integer formal argument 
         *                              -- should fail 
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve.insertFormalArg(new IntFormalArg(ve.getDB(), "<int2>"), 0);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( methodReturned ) || 
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print(
                                "insertFormalArg(new IntFormalArg(<int2>)) " +
                                "in an integer matrix returned.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "insertFormalArg(new IntFormalArg(<int2>)) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }
        
        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElement.VerifyfArgListContents(ve,
                                                            "(<int0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             17);
         }
        
        
        return failures;
        
    } /* MatrixVocabElement::TestIntArgListManagement() */

    
    /**
     * TestMatrixArgListManagement()
     * 
     * Run a battery of tests on the formal argument list management methods 
     * for matrix instances of this class.
     * 
     * In this case, there isn't much to do, as VocabElement::
     * TestfArgListManagement() has tested almost everything we need to test
     * in matrix argument list management.  All we have to do here is verify
     * that attempts to append or insert text string formal arguments fail.
     * 
     *                                          JRM -- 3/26/07
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public static int TestMatrixArgListManagement(java.io.PrintStream outStream,
                                                  boolean verbose)
        throws SystemErrorException
    {
        String invalidFargNameExceptionString = null;
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;
        
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve = new MatrixVocabElement(new ODBCDatabase(), "matrix_test");
                ve.setType(MatrixType.NOMINAL);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( ! methodReturned ) || ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.printf("Initializtion for matrix type " +
                                "test failed to complete.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected SystemErrorException " +
                                "in matrix test(1): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* try to append & insert text stringl arguments -- should fail */
        if ( failures == 0 ) 
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                failures += VerifyTypeMisMatchError(ve,
                        new TextStringFormalArg(ve.getDB()),
                        "matrix",
                        "TextStringFormalArg",
                        outStream,
                        verbose,
                        2);
                
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( ! methodReturned ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("Type mismatch tests for matrix " +
                                "matrix failed to complete.\n");
                    }
                                        
                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(10): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Verify that none of the insertions took */
        if ( failures == 0 )
        {
            if ( ve.getNumFormalArgs() != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected number of formal args(11): %d\n",
                            ve.getNumFormalArgs());
                }
            }
        }
        
        return failures;
        
    } /* MatrixVocabElement::TestMatrixArgListManagement() */

    
    /**
     * TestNominalArgListManagement()
     *
     * Run a battery of tests on the formal argument list management methods 
     * for Nominal instances of this class.
     *
     *                                          JRM -- 3/22/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public static int TestNominalArgListManagement(java.io.PrintStream outStream,
                                                   boolean verbose)
        throws SystemErrorException
    {
        String invalidFargNameExceptionString = null;
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;
        
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve = new MatrixVocabElement(new ODBCDatabase(), "nominal_test");
                ve.setType(MatrixType.NOMINAL);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( ! methodReturned ) || ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.printf("Initializtion for nominal type " +
                                "test failed to complete.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected SystemErrorException " +
                                "in Nominal test(1): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* try to append & insert non nominal arguments -- should fail */
        if ( failures == 0 ) 
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                failures += VerifyTypeMisMatchError(ve,
                        new IntFormalArg(ve.getDB()),
                        "nominal",
                        "IntFormalArg",
                        outStream,
                        verbose,
                        2);

                failures += VerifyTypeMisMatchError(ve,
                        new FloatFormalArg(ve.getDB()),
                        "nominal",
                        "FloatFormalArg",
                        outStream,
                        verbose,
                        3);

                failures += VerifyTypeMisMatchError(ve,
                        new QuoteStringFormalArg(ve.getDB()),
                        "nominal",
                        "QuoteStringFormalArg",
                        outStream,
                        verbose,
                        4);

                failures += VerifyTypeMisMatchError(ve,
                        new TextStringFormalArg(ve.getDB()),
                        "nominal",
                        "TextStringFormalArg",
                        outStream,
                        verbose,
                        5);

                failures += VerifyTypeMisMatchError(ve,
                        new TimeStampFormalArg(ve.getDB()),
                        "nominal",
                        "TimeStampFormalArg",
                        outStream,
                        verbose,
                        6);
                
                /*** TODO:  Add predicate formal arguments when available ***/
                
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( ! methodReturned ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("Type mismatch tests for nominal " +
                                "matrix failed to complete.\n");
                    }
                                        
                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(10): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Verify that none of the insertions took */
        if ( failures == 0 )
        {
            if ( ve.getNumFormalArgs() != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected number of formal args(11): %d\n",
                            ve.getNumFormalArgs());
                }
            }
        }
         
        /* try to append an nominal argument -- should succeed */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve.appendFormalArg(
                        new NominalFormalArg(ve.getDB(), "<nominal0>"));
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( ! methodReturned ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "appendFormalArg(new NominalFormalArg(<nominal0>)) " +
                                "in a nominal matrix didn't return.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(12): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElement.VerifyfArgListContents(ve,
                                                            "(<nominal0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             13);
         }
       
        /* try to append a second nominal argument -- should fail */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve.appendFormalArg(
                        new NominalFormalArg(ve.getDB(), "<nominal1>"));
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( methodReturned ) || 
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print(
                                "appendFormalArg(new IntFormalArg(<nominal1>)) " +
                                "in a nominal matrix returned.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "appendFormalArg(new NominalFormalArg(<nominal1>)) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }
        
        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElement.VerifyfArgListContents(ve,
                                                            "(<nominal0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             15);
         }
        
        /* Finally, try to insert a second nominal formal argument 
         *                              -- should fail 
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve.insertFormalArg(
                        new NominalFormalArg(ve.getDB(), "<nominal2>"), 0);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( methodReturned ) || 
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print(
                                "insertFormalArg(new NominalFormalArg(<nominal2>)) " +
                                "in a nominal matrix returned.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "insertFormalArg(new NominalFormalArg(<nominal2>)) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }
        
        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElement.VerifyfArgListContents(ve,
                                                            "(<nominal0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             17);
         }
        
        
        return failures;
        
    } /* MatrixVocabElement::TestNominalArgListManagement() */

    
    /**
     * TestPredArgListManagement()
     *
     * Run a battery of tests on the formal argument list management methods 
     * for Predicate instances of this class.
     *
     *                                          JRM -- 3/22/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public static int TestPredArgListManagement(java.io.PrintStream outStream,
                                                boolean verbose)
        throws SystemErrorException
    {
        String invalidFargNameExceptionString = null;
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;
        
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve = new MatrixVocabElement(new ODBCDatabase(), 
                                           "predicate_test");
                ve.setType(MatrixType.PREDICATE);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( ! methodReturned ) || ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.printf("Initializtion for predicate type " +
                                "test failed to complete.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected SystemErrorException " +
                                "in Predicate test(1): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* try to append & insert non predicate arguments -- should fail */
        if ( failures == 0 ) 
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                failures += VerifyTypeMisMatchError(ve,
                        new IntFormalArg(ve.getDB()),
                        "predicate",
                        "IntFormalArg",
                        outStream,
                        verbose,
                        2);

                failures += VerifyTypeMisMatchError(ve,
                        new FloatFormalArg(ve.getDB()),
                        "predicate",
                        "FloatFormalArg",
                        outStream,
                        verbose,
                        3);

                failures += VerifyTypeMisMatchError(ve,
                        new NominalFormalArg(ve.getDB()),
                        "predicate",
                        "NominalFormalArg",
                        outStream,
                        verbose,
                        4);

                failures += VerifyTypeMisMatchError(ve,
                        new QuoteStringFormalArg(ve.getDB()),
                        "predicate",
                        "QuoteStringFormalArg",
                        outStream,
                        verbose,
                        5);

                failures += VerifyTypeMisMatchError(ve,
                        new TextStringFormalArg(ve.getDB()),
                        "predicate",
                        "TextStringFormalArg",
                        outStream,
                        verbose,
                        6);

                failures += VerifyTypeMisMatchError(ve,
                        new TimeStampFormalArg(ve.getDB()),
                        "predicate",
                        "TimeStampFormalArg",
                        outStream,
                        verbose,
                        7);
                
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( ! methodReturned ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("Type mismatch tests for predicate " +
                                "matrix failed to complete.\n");
                    }
                                        
                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(10): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Verify that none of the insertions took */
        if ( failures == 0 )
        {
            if ( ve.getNumFormalArgs() != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected number of formal args(11): %d\n",
                            ve.getNumFormalArgs());
                }
            }
        }
         
        /* try to append an predicate argument -- should succeed */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve.appendFormalArg(
                        new PredFormalArg(ve.getDB(), "<pred0>"));
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( ! methodReturned ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "appendFormalArg(new PredFormalArg(<pred0>)) " +
                                "in a predicate matrix didn't return.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(12): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElement.VerifyfArgListContents(ve,
                                                            "(<pred0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             13);
         }
       
        /* try to append a second nominal argument -- should fail */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve.appendFormalArg(new PredFormalArg(ve.getDB(), "<pred1>"));
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( methodReturned ) || 
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print(
                                "appendFormalArg(new PredFormalArg(<pred1>)) " +
                                "in a predicate matrix returned.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "appendFormalArg(new PredFormalArg(<pred1>)) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }
        
        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElement.VerifyfArgListContents(ve,
                                                            "(<pred0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             15);
         }
        
        /* Finally, try to insert a second predicate formal argument 
         *                              -- should fail 
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve.insertFormalArg(
                        new PredFormalArg(ve.getDB(), "<pred2>"), 0);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( methodReturned ) || 
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print(
                                "insertFormalArg(new PredFormalArg(<pred2>)) " +
                                "in a predicate matrix returned.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "insertFormalArg(new PredFormalArg(<pred2>)) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }
        
        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElement.VerifyfArgListContents(ve,
                                                            "(<pred0>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             17);
         }
        
        
        return failures;
        
    } /* MatrixVocabElement::TestPredArgListManagement() */
    

    /**
     * TestTextArgListManagement()
     *
     * Run a battery of tests on the formal argument list management methods 
     * for text string instances of this class.
     *
     *                                          JRM -- 3/22/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public static int TestTextArgListManagement(java.io.PrintStream outStream,
                                                boolean verbose)
        throws SystemErrorException
    {
        String invalidFargNameExceptionString = null;
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;
        
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve = new MatrixVocabElement(new ODBCDatabase(), "text_test");
                ve.setType(MatrixType.TEXT);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( ! methodReturned ) || ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.printf("Initializtion for text " +
                                "type test failed to complete.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected SystemErrorException " +
                                "in text test(1): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* try to append & insert non text string arguments -- should fail */
        if ( failures == 0 ) 
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                failures += VerifyTypeMisMatchError(ve,
                        new IntFormalArg(ve.getDB()),
                        "text",
                        "IntFormalArg",
                        outStream, 
                        verbose,
                        2);

                failures += VerifyTypeMisMatchError(ve,
                        new FloatFormalArg(ve.getDB()),
                        "text",
                        "FloatFormalArg",
                        outStream, 
                        verbose,
                        3);

                failures += VerifyTypeMisMatchError(ve,
                        new NominalFormalArg(ve.getDB()),
                        "text",
                        "NominalFormalArg",
                        outStream,
                        verbose,
                        4);

                failures += VerifyTypeMisMatchError(ve,
                        new QuoteStringFormalArg(ve.getDB()),
                        "text",
                        "QuoteStringFormalArg",
                        outStream,
                        verbose,
                        5);

                failures += VerifyTypeMisMatchError(ve,
                        new TimeStampFormalArg(ve.getDB()),
                        "text",
                        "TimeStampFormalArg",
                        outStream,
                        verbose,
                        6);
                
                /*** TODO:  Add predicate formal arguments when available ***/
                
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( ! methodReturned ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print("Type mismatch tests for text " +
                                "matrix failed to complete.\n");
                    }
                                        
                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(10): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Verify that none of the appends / insertions took */
        if ( failures == 0 )
        {
            if ( ve.getNumFormalArgs() != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "Unexpected number of formal args(11): %d\n",
                            ve.getNumFormalArgs());
                }
            }
        }
         
        /* try to append an text string argument -- should succeed */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve.appendFormalArg(new TextStringFormalArg(ve.getDB()));
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( ! methodReturned ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "appendFormalArg(new TextStringFormalArg()) " +
                                "in an text matrix didn't return.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf(
                                "Unexpected SystemErrorException(12): \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElement.VerifyfArgListContents(ve,
                                                            "(<arg>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             13);
         }
       
        /* try to append a second nominal argument -- should fail */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                /* set up an instance of TextStringFormalArg with a name
                 * other than the default name of "<arg>".  Need to do this
                 * as using a duplicate name could cause a false negative in
                 * the test.
                 */
                FormalArgument t = new TextStringFormalArg(ve.getDB());
                t.setFargName("<arg1>");
                ve.appendFormalArg(t);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( methodReturned ) || 
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print(
                                "appendFormalArg(new TextStringFormalArg()) " +
                                "in a text matrix returned.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "appendFormalArg(new TextStringFormalArg()) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }
        
        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElement.VerifyfArgListContents(ve,
                                                            "(<arg>)",
                                                            null,
                                                            outStream,
                                                            verbose,
                                                            15);
         }
        
        /* Finally, try to insert a second nominal formal argument 
         *                              -- should fail 
         */
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                /* set up an instance of TextStringFormalArg with a name
                 * other than the default name of "<arg>".  Need to do this
                 * as usning a duplicate name could cause a false negative in
                 * the test.
                 */
                FormalArgument t = new TextStringFormalArg(ve.getDB());
                t.setFargName("<arg2>");
                ve.insertFormalArg(t, 0);
                methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( methodReturned ) || 
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print(
                                "insertFormalArg(new TextStringFormalArg()) " +
                                "in a text matrix returned.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print(
                                "insertFormalArg(new TextStringFormalArg()) " +
                                "failed to throw a SystemErrorException.\n");
                    }
                }
            }
        }
        
        /* Verify the formal argument list contents */
        if ( failures == 0 )
        {
            failures += VocabElement.VerifyfArgListContents(ve,
                                                            "(<arg>)",
                                                             null,
                                                             outStream,
                                                             verbose,
                                                             17);
         }
        
        
        return failures;
        
    } /* MatrixVocabElement::TestTextArgListManagement() */

    
    /**
     * TestClassMatrixVocabElement()
     *
     * Main routine for tests of class MatrixVocabElement.
     *
     *                                      JRM -- 3/10/07
     *
     * Changes:
     *
     *    - Non.
     */
    
    public static boolean TestClassMatrixVocabElement(
            java.io.PrintStream outStream, 
            boolean verbose)
        throws SystemErrorException
    {
        boolean pass = true;
        int failures = 0;
        
        outStream.print("Testing class MatrixVocabElement:\n");
        
        if ( ! Test1ArgConstructor(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! Test2ArgConstructor(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestCopyConstructor(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestAccessors(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestArgListManagement(outStream, verbose) )
        {
            failures++;
        }
        
        if ( ! TestToStringMethods(outStream, verbose) )
        {
            failures++;
        }
       
        if ( failures > 0 )
        {
            pass = false;
            outStream.printf("%d failures in tests for class MatrixVocabElement.\n\n",
                              failures);
        }
        else
        {
            outStream.print("All tests passed for class MatrixVocabElement.\n\n");
        }
        
        return pass;
        
    } /* MatrixVocabElement::TestClassMatrixVocabElement() */

    
    /**
     * Test1ArgConstructor()
     * 
     * Run a battery of tests on the one argument constructor for this
     * class, and on the instance returned.
     * 
     * Changes:
     * 
     *    - None.
     */

    public static boolean Test1ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing 1 argument constructor for class MatrixVocabElement      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;        
        boolean methodReturned;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            ve = null;
        
            try
            {
                ve = new MatrixVocabElement((Database)null);
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
            }
            
            if ( ( methodReturned ) ||
                 ( ve != null ) ||
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.print(
                                "new MatrixVocabElement(null) returned.\n");
                    }
                    
                    if ( ve != null )
                    {
                        outStream.print(
                            "new MatrixVocabElement(null) returned non-null.\n");
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.print("new MatrixVocabElement(null) failed " +
                                "to throw a system error exception.\n");
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;
            ve = null;
        
            try
            {
                ve = new MatrixVocabElement(new ODBCDatabase());
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();            
            }
            
            if ( ( ! methodReturned ) ||
                 ( ve == null ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                                "new MatrixVocabElement(db) didn't return.\n");
                    }
                    
                    if ( ve == null )
                    {
                        outStream.print(
                                "new MatrixVocabElement(db) returned null.\n");
                    }
                    
                    if ( threwSystemErrorException )
                    {
                        outStream.printf("new MatrixVocabElement(db) threw " +
                                "unexpected system error exception: \"%s\".\n",
                                systemErrorExceptionString);
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( ve.getName().compareTo("") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial name \"%s\".\n",
                                       ve.getName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ve.getSystem() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of system: %b.\n",
                                       ve.getSystem());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ve.getVarLen() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of varLen: %b.\n",
                                       ve.getVarLen());
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( ve.getType() != MatrixType.UNDEFINED )
            {
                failures++;
                        
                if ( verbose )
                {
                    outStream.print("Unexpected initial value of type.\n");
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        return pass;

    } /* MatrixVocabElement::Test1ArgConstructor() */

    
    /**
     * Test2ArgConstructor()
     * 
     * Run a battery of tests on the two argument constructor for this
     * class, and on the instance returned.
     * 
     * Changes:
     * 
     *    - None.
     */

    public static boolean Test2ArgConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
    {
        String testBanner =
            "Testing 2 argument constructor for class MatrixVocabElement      ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        MatrixVocabElement ve = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        try
        {
            ve = new MatrixVocabElement(new ODBCDatabase(), "valid");
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( ve == null ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( ve == null )
                {
                    outStream.print("new MatrixVocabElement(db, \"valid\") " +
                            "returned null.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.print("new MatrixVocabElement(db, \"valid\")\"" +
                                     " threw a SystemErrorException.\n");
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ve.getName().compareTo("valid") != 0 )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial name \"%s\".\n",
                                       ve.getName());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ve.getSystem() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of system: %b.\n",
                                       ve.getSystem());
                }
            }
        }

        if ( failures == 0 )
        {
            if ( ve.getVarLen() != false )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("Unexpected initial value of varLen: %b.\n",
                                       ve.getVarLen());
                }
            }
        }
        
        if ( failures == 0 )
        {
            if ( ve.getType() != MatrixType.UNDEFINED )
            {
                failures++;
                        
                if ( verbose )
                {
                    outStream.print("Unexpected initial value of type.\n");
                }
            }
        }

        /* Verify that the constructor fails when passed an invalid db. */
        ve = null;

        try
        {
            ve = new MatrixVocabElement(null, "valid");
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( ve != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( ve != null )
                {
                    outStream.print(
                        "new MatrixVocabElement(null, \"valid\") != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print("new MatrixVocabElement(null, \"valid\")\" "
                        + "didn't throw an SystemErrorException.\n");
                }
            }
        }

        /* Verify that the constructor fails when passed an invalid
         * formal argument name.
         */
        ve = null;

        try
        {
            ve = new MatrixVocabElement(new ODBCDatabase(), " in valid ");
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
        }

        if ( ( ve != null ) ||
             ( ! threwSystemErrorException ) )
        {
            failures++;


            if ( verbose )
            {
                if ( ve != null )
                {
                    outStream.print(
                        "new MatrixVocabElement(db, \" in valid \") != null.\n");
                }

                if ( ! threwSystemErrorException )
                {
                    outStream.print(
                            "new MatrixVocabElement(db, \" in valid \")\" " +
                            "didn't throw an SystemErrorException.\n");
                }
            }
        }

        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        return pass;

    } /* MatrixVocabElement::Test2ArgConstructor() */

    
    /**
     * TestCopyConstructor()
     * 
     * Run a battery of tests on the copy constructor for this 
     * class, and on the instance returned.
     * 
     * Changes:
     * 
     *    - None.
     */
    
    public static boolean TestCopyConstructor(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::TestCopyConstructor(): ";
        String testBanner =
            "Testing copy constructor for class MatrixVocabElement            ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int i;
        int failures = 0;
        int progress = 0;
        String s = null;
        IntFormalArg alpha = null;
        FloatFormalArg bravo = null;
        NominalFormalArg charlie = null;
        QuoteStringFormalArg delta = null;
        TimeStampFormalArg echo = null;
        UnTypedFormalArg foxtrot = null;
        TextStringFormalArg golf = null;
        MatrixVocabElement base_ve = null;
        MatrixVocabElement copy_ve = null;
        Database db = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }

        if ( failures == 0 )
        {
            /* Start by creating a base matrix vocab element, and loading it
             * with a variety of formal arguments.
             */
            progress = 0;
            completed = false;
            threwSystemErrorException = false;

            try
            {
                /** TODO: Add predicate formal arguments to this test when
                 *  they become available.
                 */

                db = new ODBCDatabase();
                
                progress++;

                alpha = new IntFormalArg(db, "<alpha>");
                bravo = new FloatFormalArg(db, "<bravo>");
                charlie = new NominalFormalArg(db, "<charlie>");
                delta = new QuoteStringFormalArg(db, "<delta>");
                echo = new TimeStampFormalArg(db, "<echo>");
                foxtrot = new UnTypedFormalArg(db, "<foxtrot>");

                progress++;
                
                base_ve = new MatrixVocabElement(db, "matrix");

                progress++;
                
                base_ve.setType(MatrixType.MATRIX);
                
                progress++;
                
                base_ve.appendFormalArg(alpha);
                base_ve.appendFormalArg(bravo);
                base_ve.appendFormalArg(charlie);
                base_ve.appendFormalArg(delta);
                base_ve.appendFormalArg(echo);
                base_ve.appendFormalArg(foxtrot);

                progress++;
                
                /* set other fields to non-default values just to make
                 * sure they get copied.
                 */
                base_ve.lastModUID = 2;
                base_ve.varLen = true;
                base_ve.system = true;
                
                progress++;
                
                /* add the base_ve to the vocab list to assign an id */
                db.vl.addElement(base_ve); 

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( alpha == null ) ||
                 ( bravo == null ) ||
                 ( charlie == null ) ||
                 ( delta == null ) ||
                 ( echo == null ) ||
                 ( foxtrot == null ) ||
                 ( base_ve == null ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.printf("base_ve initialization didn't " +
                                "complete(1).  progress = %d\n", progress);
                    }

                    if ( ( db == null ) ||
                         ( alpha == null ) ||
                         ( bravo == null ) ||
                         ( charlie == null ) ||
                         ( delta == null ) ||
                         ( echo == null ) ||
                         ( foxtrot == null ) ||
                         ( base_ve == null ) )
                    {
                        outStream.print("One or more classes not allocated(1).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in base_ve " +
                                         "initialization(1): \"%s\"\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Now run the copy constructor on base_ve, and verify that the 
         * result is a copy.
         */
        
        if ( failures == 0 )
        {      
            copy_ve = null;
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                copy_ve = new MatrixVocabElement(base_ve);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( copy_ve == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("copy constructor didn't complete(1).\n");
                    }

                    if ( copy_ve == null )
                    {
                        outStream.print("copy_ve is null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in copy(1):" +
                                         " \"%s\"\n",
                                         systemErrorExceptionString);
                    }

                }
            }
            /* Use the toString and toDBString methods to verify that the 
             * base and copy contain the same data.
             */
            else if ( base_ve.toString().compareTo(copy_ve.toString()) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "base_ve.toString() != copy_ve.toString()(1).\n" +
                            "base_ve.toString() = \"%s\"\n" +
                            "copy_ve.toString() = \"%s\"\n",
                            base_ve.toString(), copy_ve.toString());
                }
            }
            else if ( base_ve.toDBString().compareTo(copy_ve.toDBString()) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "base_ve.toDBString() != copy_ve.toDBString()(1).\n" +
                            "base_ve.toDBString() = \"%s\"\n" +
                            "copy_ve.toDBString() = \"%s\"\n",
                            base_ve.toDBString(), copy_ve.toDBString());
                }
            }
            /* at this point, we have verified that the base and copy contain
             * the same data.  Must now verify that they don't refer to any
             * common locations in memory.
             */
            else if ( base_ve == copy_ve )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.print("base_ve == copy_ve(1)\n");
                }
            }
            else
            {
                for ( i = 0; i < base_ve.getNumFormalArgs(); i++ )
                {
                    /* This should never happen, but it if did, it could 
                     * hide other failures.  Thus test for it anyway.
                     */
                    if ( base_ve.getFormalArg(i) != base_ve.getFormalArg(i) )
                    {
                        throw new SystemErrorException(mName + 
                                "unexpected return from getFormalArg() (1)");
                        
                    }
                    
                    if ( base_ve.getFormalArg(i) == copy_ve.getFormalArg(i) )
                    {
                        failures++;
                        
                        if ( verbose )
                        {
                            outStream.printf("base_ve.getFormalArg(%d) == " +
                                             "copy_ve.getFormalArg(%d) (1)\n",
                                             i, i);
                        }
                    }
                    else if (base_ve.getFormalArg(i).getClass() != 
                             copy_ve.getFormalArg(i).getClass() )
                    {
                        outStream.printf("class mismatch detected in copy " +
                                "of %dth formal argument(1).\n", i);
                    }
                }
                if ( ( failures == 0 ) && ( i != 6 ) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        outStream.printf("Unexpected number of formal " +
                                         "args(1): %d\n", i);
                    }
                }
            }
        }
        
        if ( failures == 0 )
        {
            /* now create a base matrix vocab element, and loading it
             * with only one formal argument.
             */
            alpha = null;
            bravo = null;
            charlie = null;
            delta = null;
            echo = null;
            foxtrot = null;
            completed = false;
            threwSystemErrorException = false;

            try
            {
                /** TODO: Add predicate formal arguments to this test when
                 *  they become available.
                 */

                db = new ODBCDatabase();

                foxtrot = new UnTypedFormalArg(db, "<foxtrot2>");

                base_ve = new MatrixVocabElement(db, "matrix2");

                base_ve.setType(MatrixType.MATRIX);
                
                base_ve.appendFormalArg(foxtrot);

                base_ve.lastModUID = 4;
                base_ve.varLen = false;
                base_ve.system = true;
                
                /* add the base_ve to the vocab list to assign an id */
                db.vl.addElement(base_ve); 

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( base_ve == null ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print(
                                "base_ve initialization didn't complete(2).\n");
                    }

                    if ( ( db == null ) ||
                         ( foxtrot == null ) ||
                         ( base_ve == null ) )
                    {
                        outStream.print(
                                "One or more classes not allocated(2).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in base_ve " +
                                         "initialization(2): \"%s\"\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Now run the copy constructor on base_ve, and verify that the 
         * result is a copy.
         */
        
        if ( failures == 0 )
        {      
            copy_ve = null;
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                copy_ve = new MatrixVocabElement(base_ve);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( copy_ve == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("copy constructor didn't complete(2).\n");
                    }

                    if ( copy_ve == null )
                    {
                        outStream.print("copy_ve is null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in copy(2):" +
                                         " \"%s\"\n",
                                         systemErrorExceptionString);
                    }

                }
            }
            /* Use the toString and toDBString methods to verify that the 
             * base and copy contain the same data.
             */
            else if ( base_ve.toString().compareTo(copy_ve.toString()) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "base_ve.toString() != copy_ve.toString()(2).\n" +
                            "base_ve.toString() = \"%s\"\n" +
                            "copy_ve.toString() = \"%s\"\n",
                            base_ve.toString(), copy_ve.toString());
                }
            }
            else if ( base_ve.toDBString().compareTo(copy_ve.toDBString()) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "base_ve.toDBString() != copy_ve.toDBString()(2).\n" +
                            "base_ve.toDBString() = \"%s\"\n" +
                            "copy_ve.toDBString() = \"%s\"\n",
                            base_ve.toDBString(), copy_ve.toDBString());
                }
            }
            /* at this point, we have verified that the base and copy contain
             * the same data.  Must now verify that they don't refer to any
             * common locations in memory.
             */
            else if ( base_ve == copy_ve )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.print("base_ve == copy_ve(2)\n");
                }
            }
            else
            {
                for ( i = 0; i < base_ve.getNumFormalArgs(); i++ )
                {
                    /* This should never happen, but it if did, it could 
                     * hide other failures.  Thus test for it anyway.
                     */
                    if ( base_ve.getFormalArg(i) != base_ve.getFormalArg(i) )
                    {
                        throw new SystemErrorException(mName + 
                                "unexpected return from getFormalArg() (2)");
                        
                    }
                    
                    if ( base_ve.getFormalArg(i) == copy_ve.getFormalArg(i) )
                    {
                        failures++;
                        
                        if ( verbose )
                        {
                            outStream.printf("base_ve.getFormalArg(%d) == " +
                                             "copy_ve.getFormalArg(%d) (2)\n",
                                             i, i);
                        }
                    }
                    else if (base_ve.getFormalArg(i).getClass() != 
                             copy_ve.getFormalArg(i).getClass() )
                    {
                        outStream.printf("class mismatch detected in copy " +
                                "of %dth formal argument(2).\n", i);
                    }
                }
                if ( ( failures == 0 ) && ( i != 1 ) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        outStream.printf("Unexpected number of formal " +
                                         "args(2): %d\n", i);
                    }
                }
            }
        }

        
        if ( failures == 0 )
        {
            /* now create a base matrix vocab element, and don't load it
             * with any arguments.
             */
            alpha = null;
            bravo = null;
            charlie = null;
            delta = null;
            echo = null;
            foxtrot = null;
            completed = false;
            threwSystemErrorException = false;

            try
            {
                db = new ODBCDatabase();

                base_ve = new MatrixVocabElement(db, "matrix3");

                base_ve.lastModUID = 6;
                base_ve.varLen = false;
                base_ve.system = false;
                
                /* add the base_ve to the vocab list to assign an id */
                db.vl.addElement(base_ve); 

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( base_ve == null ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print(
                                "base_ve initialization didn't complete(3).\n");
                    }

                    if ( ( db == null ) ||
                         ( foxtrot == null ) ||
                         ( base_ve == null ) )
                    {
                        outStream.print(
                                "One or more classes not allocated(3).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in base_ve " +
                                         "initialization(3): \"%s\"\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Now run the copy constructor on base_ve, and verify that the 
         * result is a copy.
         */
        
        if ( failures == 0 )
        {      
            copy_ve = null;
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                copy_ve = new MatrixVocabElement(base_ve);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( copy_ve == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("copy constructor didn't complete(3).\n");
                    }

                    if ( copy_ve == null )
                    {
                        outStream.print("copy_ve is null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in copy(3):" +
                                         " \"%s\"\n",
                                         systemErrorExceptionString);
                    }

                }
            }
            /* Use the toString and toDBString methods to verify that the 
             * base and copy contain the same data.
             */
            else if ( base_ve.toString().compareTo(copy_ve.toString()) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "base_ve.toString() != copy_ve.toString()(3).\n" +
                            "base_ve.toString() = \"%s\"\n" +
                            "copy_ve.toString() = \"%s\"\n",
                            base_ve.toString(), copy_ve.toString());
                }
            }
            else if ( base_ve.toDBString().compareTo(copy_ve.toDBString()) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "base_ve.toDBString() != copy_ve.toDBString()(3).\n" +
                            "base_ve.toDBString() = \"%s\"\n" +
                            "copy_ve.toDBString() = \"%s\"\n",
                            base_ve.toDBString(), copy_ve.toDBString());
                }
            }
            /* at this point, we have verified that the base and copy contain
             * the same data.  Must now verify that they don't refer to any
             * common locations in memory.
             */
            else if ( base_ve == copy_ve )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.print("base_ve == copy_ve(3)\n");
                }
            }
            else if ( ( base_ve.fArgList != null ) &&
                      ( base_ve.fArgList.size() != 0 ) )
            {
                failures++;
                    
                if ( verbose )
                {
                    outStream.printf("Unexpected number of formal " +
                                     "args(3): %d\n", 
                                     base_ve.fArgList.size());
                }
            }
        }
        
        /* So far we have been testing matrix type matrix vocab elements.
         * Must also spot check the other types.  Will only do one or tow
         * as they are all pretty similar.
         */
        
        if ( failures == 0 )
        {
            /* Create a text string matrix vocab element, and loading it
             * with a text string formal argument.
             */
            alpha = null;
            bravo = null;
            charlie = null;
            delta = null;
            echo = null;
            foxtrot = null;
            golf = null;
            completed = false;
            threwSystemErrorException = false;

            try
            {
                db = new ODBCDatabase();

                golf = new TextStringFormalArg(db);

                base_ve = new MatrixVocabElement(db, "text4");

                base_ve.setType(MatrixType.TEXT);
                
                base_ve.appendFormalArg(golf);

                base_ve.lastModUID = 8;
                base_ve.varLen = false;
                base_ve.system = true;
                
                /* add the base_ve to the vocab list to assign an id */
                db.vl.addElement(base_ve); 

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( base_ve == null ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print(
                                "base_ve initialization didn't complete(4).\n");
                    }

                    if ( ( db == null ) ||
                         ( foxtrot == null ) ||
                         ( base_ve == null ) )
                    {
                        outStream.print(
                                "One or more classes not allocated(4).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in base_ve " +
                                         "initialization(4): \"%s\"\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Now run the copy constructor on base_ve, and verify that the 
         * result is a copy.
         */
        
        if ( failures == 0 )
        {      
            copy_ve = null;
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                copy_ve = new MatrixVocabElement(base_ve);
                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( copy_ve == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("copy constructor didn't complete(4).\n");
                    }

                    if ( copy_ve == null )
                    {
                        outStream.print("copy_ve is null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in copy(4):" +
                                         " \"%s\"\n",
                                         systemErrorExceptionString);
                    }

                }
            }
            /* Use the toString and toDBString methods to verify that the 
             * base and copy contain the same data.
             */
            else if ( base_ve.toString().compareTo(copy_ve.toString()) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "base_ve.toString() != copy_ve.toString()(4).\n" +
                            "base_ve.toString() = \"%s\"\n" +
                            "copy_ve.toString() = \"%s\"\n",
                            base_ve.toString(), copy_ve.toString());
                }
            }
            else if ( base_ve.toDBString().compareTo(copy_ve.toDBString()) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "base_ve.toDBString() != copy_ve.toDBString()(4).\n" +
                            "base_ve.toDBString() = \"%s\"\n" +
                            "copy_ve.toDBString() = \"%s\"\n",
                            base_ve.toDBString(), copy_ve.toDBString());
                }
            }
            /* at this point, we have verified that the base and copy contain
             * the same data.  Must now verify that they don't refer to any
             * common locations in memory.
             */
            else if ( base_ve == copy_ve )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.print("base_ve == copy_ve(4)\n");
                }
            }
            else
            {
                for ( i = 0; i < base_ve.getNumFormalArgs(); i++ )
                {
                    /* This should never happen, but it if did, it could 
                     * hide other failures.  Thus test for it anyway.
                     */
                    if ( base_ve.getFormalArg(i) != base_ve.getFormalArg(i) )
                    {
                        throw new SystemErrorException(mName + 
                                "unexpected return from getFormalArg() (4)");
                        
                    }
                    
                    if ( base_ve.getFormalArg(i) == copy_ve.getFormalArg(i) )
                    {
                        failures++;
                        
                        if ( verbose )
                        {
                            outStream.printf("base_ve.getFormalArg(%d) == " +
                                             "copy_ve.getFormalArg(%d) (4)\n",
                                             i, i);
                        }
                    }
                    else if (base_ve.getFormalArg(i).getClass() != 
                             copy_ve.getFormalArg(i).getClass() )
                    {
                        outStream.printf("class mismatch detected in copy " +
                                "of %dth formal argument(4).\n", i);
                    }
                }
                if ( ( failures == 0 ) && ( i != 1 ) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        outStream.printf("Unexpected number of formal " +
                                         "args(4): %d\n", i);
                    }
                }
            }
        }

        
        if ( failures == 0 )
        {
            /* Create a nominal matrix vocab element, and load it
             * with a text string formal argument.
             */
            alpha = null;
            bravo = null;
            charlie = null;
            delta = null;
            echo = null;
            foxtrot = null;
            golf = null;
            completed = false;
            threwSystemErrorException = false;

            try
            {
                db = new ODBCDatabase();

                bravo = new FloatFormalArg(db, "<bravo5>");

                base_ve = new MatrixVocabElement(db, "float5");

                base_ve.setType(MatrixType.FLOAT);
                
                base_ve.appendFormalArg(bravo);

                base_ve.lastModUID = 10;
                base_ve.varLen = false;
                base_ve.system = false;
                
                /* add the base_ve to the vocab list to assign an id */
                db.vl.addElement(base_ve); 

                completed = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }

            if ( ( ! completed ) ||
                 ( db == null ) ||
                 ( base_ve == null ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print(
                                "base_ve initialization didn't complete(5).\n");
                    }

                    if ( ( db == null ) ||
                         ( foxtrot == null ) ||
                         ( base_ve == null ) )
                    {
                        outStream.print(
                                "One or more classes not allocated(5).\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in base_ve " +
                                         "initialization(5): \"%s\"\n",
                                         systemErrorExceptionString);
                    }
                }
            }
        }
        
        /* Now run the copy constructor on base_ve, and verify that the 
         * result is a copy.
         */
        
        if ( failures == 0 )
        {      
            copy_ve = null;
            completed = false;
            threwSystemErrorException = false;
            
            try
            {
                copy_ve = new MatrixVocabElement(base_ve);
                completed = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.getMessage();
            }
            
            if ( ( copy_ve == null ) ||
                 ( ! completed ) ||
                 ( threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( ! completed )
                    {
                        outStream.print("copy constructor didn't complete(5).\n");
                    }

                    if ( copy_ve == null )
                    {
                        outStream.print("copy_ve is null.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected system error in copy(5):" +
                                         " \"%s\"\n",
                                         systemErrorExceptionString);
                    }

                }
            }
            /* Use the toString and toDBString methods to verify that the 
             * base and copy contain the same data.
             */
            else if ( base_ve.toString().compareTo(copy_ve.toString()) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "base_ve.toString() != copy_ve.toString()(5).\n" +
                            "base_ve.toString() = \"%s\"\n" +
                            "copy_ve.toString() = \"%s\"\n",
                            base_ve.toString(), copy_ve.toString());
                }
            }
            else if ( base_ve.toDBString().compareTo(copy_ve.toDBString()) != 0 )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.printf(
                            "base_ve.toDBString() != copy_ve.toDBString()(5).\n" +
                            "base_ve.toDBString() = \"%s\"\n" +
                            "copy_ve.toDBString() = \"%s\"\n",
                            base_ve.toDBString(), copy_ve.toDBString());
                }
            }
            /* at this point, we have verified that the base and copy contain
             * the same data.  Must now verify that they don't refer to any
             * common locations in memory.
             */
            else if ( base_ve == copy_ve )
            {
                failures++;
                
                if ( verbose )
                {
                    outStream.print("base_ve == copy_ve(5)\n");
                }
            }
            else
            {
                for ( i = 0; i < base_ve.getNumFormalArgs(); i++ )
                {
                    /* This should never happen, but it if did, it could 
                     * hide other failures.  Thus test for it anyway.
                     */
                    if ( base_ve.getFormalArg(i) != base_ve.getFormalArg(i) )
                    {
                        throw new SystemErrorException(mName + 
                                "unexpected return from getFormalArg() (5)");
                        
                    }
                    
                    if ( base_ve.getFormalArg(i) == copy_ve.getFormalArg(i) )
                    {
                        failures++;
                        
                        if ( verbose )
                        {
                            outStream.printf("base_ve.getFormalArg(%d) == " +
                                             "copy_ve.getFormalArg(%d) (5)\n",
                                             i, i);
                        }
                    }
                    else if (base_ve.getFormalArg(i).getClass() != 
                             copy_ve.getFormalArg(i).getClass() )
                    {
                        outStream.printf("class mismatch detected in copy " +
                                "of %dth formal argument(5).\n", i);
                    }
                }
                if ( ( failures == 0 ) && ( i != 1 ) )
                {
                    failures++;
                    
                    if ( verbose )
                    {
                        outStream.printf("Unexpected number of formal " +
                                         "args(5): %d\n", i);
                    }
                }
            }
        }
        
        /* Verify that the constructor fails when passed a null matrix
         * vocab element. 
         */
        
        base_ve = null;
        copy_ve = null;
        threwSystemErrorException = false;
        
        try
        {
            copy_ve = new MatrixVocabElement(base_ve);
        }
        
        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }
        
        if ( ( copy_ve != null ) || 
             ( ! threwSystemErrorException ) )
        {
            failures++;
            
            
            if ( verbose )
            {
                if ( copy_ve != null )
                {
                    outStream.print(
                        "new MatrixVocabElement(null) != null.\n");
                }
                
                if ( ! threwSystemErrorException )
                {
                    outStream.print("new MatrixVocabElement(null) " +
                        "didn't throw an SystemErrorException.\n");
                }
            }
        }
        
        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }
        
        return pass;
        
    } /* MatrixVocabElement::TestCopyConstructor() */

    
    /**
     * TestToStringMethods()
     *
     * Test the toString() and toDBString() methods.
     *
     *              JRM -- 3/11/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public static boolean TestToStringMethods(java.io.PrintStream outStream,
                                              boolean verbose)
        throws SystemErrorException
    {
        final String expectedString = "test(<a>, <b>, <c>, <d>, <e>, <f>)";
        final String expectedDBString = 
            "((MatrixVocabElement: 0 test) " +
             "(system: true) " +
             "(type: MATRIX) " +
             "(varLen: true) " +
             "(fArgList: ((UnTypedFormalArg 0 <a>), " +
                          "(IntFormalArg 0 <b> false " +
                                "-9223372036854775808 " +
                                "9223372036854775807), " +
                          "(FloatFormalArg 0 <c> false " +
                                "-1.7976931348623157E308 " +
                                "1.7976931348623157E308), " +
                          "(TimeStampFormalArg 0 <d> false null null), " +
                          "(NominalFormalArg 0 <e> false ()), " +
                          "(QuoteStringFormalArg 0 <f>)))";
       String testBanner =
            "Testing toString() & toDBString()                                ";
        String passBanner = "PASSED\n";
        String failBanner = "FAILED\n";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        MatrixVocabElement ve = null;

        outStream.print(testBanner);

        if ( verbose )
        {
            outStream.print("\n");
        }
        
        if ( failures == 0 )
        {
            methodReturned = false;
            threwSystemErrorException = false;

            try
            {
                ve = new MatrixVocabElement(new ODBCDatabase(), "test");
                ve.setType(MatrixType.MATRIX);
                ve.appendFormalArg(new UnTypedFormalArg(ve.getDB(), "<a>"));
                ve.appendFormalArg(new IntFormalArg(ve.getDB(), "<b>"));
                ve.appendFormalArg(new FloatFormalArg(ve.getDB(), "<c>"));
                ve.appendFormalArg(new TimeStampFormalArg(ve.getDB(), "<d>"));
                ve.appendFormalArg(new NominalFormalArg(ve.getDB(), "<e>"));
                ve.appendFormalArg(new QuoteStringFormalArg(ve.getDB(), "<f>"));
                ve.setVarLen(true);
                ve.setSystem();
                methodReturned = true;
            }

            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }

            if ( ( ! methodReturned ) || 
                 ( threwSystemErrorException ) )
            {
                failures++;

                if ( verbose )
                {
                    if ( ! methodReturned )
                    {
                        outStream.print(
                            "Setup for strings test failed to complete.\n");
                    }

                    if ( threwSystemErrorException )
                    {
                        outStream.printf("Unexpected SystemErrorException in " +
                                "setup for strings test: \"%s\"\n",
                                systemErrorExceptionString);
                    }
                }
                
                ve = null;
            }
        }
        
        if ( ve != null )
        {
            if ( ve.toString().compareTo(expectedString) != 0 )
            {
                failures++;
                outStream.printf(
                        "ve.toString() returned unexpected value: \"%s\".\n",
                        ve.toString());
            }
        }
        
        if ( ve != null )
        {
            if ( ve.toDBString().compareTo(expectedDBString) != 0 )
            {
                failures++;
                outStream.printf(
                        "ve.toDBString() returned unexpected value: \"%s\".\n",
                        ve.toDBString());
            }
        }
        
        if ( failures > 0 )
        {
            pass = false;

            if ( verbose )
            {
                outStream.printf("%d failures.\n", failures);
            }
        }
        else if ( verbose )
        {
            outStream.print("All tests passed.\n");
        }

        if ( verbose )
        {
            /* print the banner again. */
            outStream.print(testBanner);
        }

        if ( pass )
        {
            outStream.print(passBanner);
        }
        else
        {
            outStream.print(failBanner);
        }

        return pass;
        
    } /* MatrixVocabElement::TestToStringMethods() */

    
    /**
     * VerifyTypeMisMatchError()
     *
     * Attempt to both append and insert the supplied formal argument
     * in the supplied instance of MatrixVocabElement.  Return the
     * number of failures.  If verbose, also issue a diabnostic message.
     *
     *                                  JRM -- 3/22/07
     *
     * Changes:
     *
     *    - None.
     */
    public static int VerifyTypeMisMatchError(MatrixVocabElement ve,
                                              FormalArgument fArg,
                                              String veTypeString,
                                              String fArgTypeString,
                                              java.io.PrintStream outStream,
                                              boolean verbose,
                                              int testNum)
        throws SystemErrorException
    {
        final String mName = "MatrixVocabElement::VerifyTypeMisMatchError(): ";
        String systemErrorExceptionString = null;
        boolean methodReturned = false;
        boolean threwSystemErrorException = false;
        boolean pass = true;
        int failures = 0;
        String s = null;
        
        if ( ( ve == null ) ||
             ( ! ( ve instanceof MatrixVocabElement ) ) ||
             ( fArg == null ) ||
             ( ! ( fArg instanceof FormalArgument ) ) ||
             ( veTypeString == null ) ||
             ( fArgTypeString == null ) ||
             ( testNum < 0 ) )
        {
            throw new SystemErrorException(mName + "bad param(s) on entry.");
        }
        
        if ( failures == 0 ) /* try to append the argument -- should fail */
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve.appendFormalArg(fArg);
               methodReturned = true;
            }
        
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( methodReturned ) || 
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.printf(
                                "appendFormalArg(new %s()) in an " +
                                "%s matrix returned.\n", fArgTypeString,
                                veTypeString);
                    }
                    
                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("appendFormalArg(new %s()) in a %s " +
                            "matrix failed to throw a SystemErrorException.\n",
                            fArgTypeString, veTypeString);
                    }
                }
            }
        }
         
        
        if ( failures == 0 ) /* try to insert thel argument -- should fail */
        {
            methodReturned = false;
            threwSystemErrorException = false;
            
            try
            {
                ve.insertFormalArg(fArg, 0);
                methodReturned = true;
            }
         
            catch (SystemErrorException e)
            {
                threwSystemErrorException = true;
                systemErrorExceptionString = e.toString();
            }
            
            if ( ( methodReturned ) || 
                 ( ! threwSystemErrorException ) )
            {
                failures++;
                
                if ( verbose )
                {
                    if ( methodReturned )
                    {
                        outStream.printf(
                                "insertFormalArg(new %s()) in an " +
                                "%s matrix returned.\n", fArgTypeString,
                                veTypeString);
                    }
                   
                    if ( ! threwSystemErrorException )
                    {
                        outStream.printf("insertFormalArg(new %s()) in a %s " +
                            "matrix failed to throw a SystemErrorException.\n",
                            fArgTypeString, veTypeString);
                    }
                }
            }
        }
        
        return failures;
        
    } /* MatrixVocabElement::VerifyTypeMisMatchError() */

} /* class MatrixFormalArgument */
