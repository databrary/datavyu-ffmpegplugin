package au.com.nicta.openshapa.db;

import au.com.nicta.openshapa.util.Constants;
import au.com.nicta.openshapa.util.HashUtils;

/**
 * An instance of UndefinedDataValue is used as a place holder for an untyped
 * formal argument until a value is assigned.
 *
 * @author mainzer
 */

public final class UndefinedDataValue extends DataValue
{
    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /*
     * itsValue:   String containing the name of the formal argument.
     */
    
    /** the name of the associated formal arg */
    String itsValue = "<val>";


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/
    
    /** 
     * UndefinedDataValue()
     *
     * Constructor for instances of UndefinedDataValue.  
     * 
     * Three versions of this constructor.  
     * 
     * The first takes a reference to a database as its parameter and just 
     * calls the super() constructor.
     *
     * The second takes a reference to a database, a formal argument ID, and 
     * a value as arguments, and attempts to set the itsFargID and itsValue 
     * of the data value accordingly.
     *
     * The third takes a reference to an instance of UndefinedDataValue as an
     * argument, and uses it to create a copy.
     *
     *                                              JRM -- 8/16/07  
     *
     * Changes:
     *
     *    - None.
     *      
     */
 
    public UndefinedDataValue(Database db)
        throws SystemErrorException
    {
        super(db);
        
    } /* UndefinedDataValue::UndefinedDataValue(db) */
    
    public UndefinedDataValue(Database db,
                              long fargID,
                              String value)
        throws SystemErrorException
    {
        super(db);
        
        this.setItsFargID(fargID);
        
        this.setItsValue(value);
    
    } /* UndefinedDataValue::UndefinedDataValue(db, fargID, value) */
    
    public UndefinedDataValue(UndefinedDataValue dv)
        throws SystemErrorException
    {
        super(dv);
        
        this.itsValue  = new String(dv.itsValue);
        
    } /* UndefinedDataValue::UndefinedDataValue(dv) */

    /**
     * Creates a new copy of the object.
     *
     * @return A duplicate of this object.
     *
     * @throws java.lang.CloneNotSupportedException If the clone interface has
     * not been implemented.
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        UndefinedDataValue clone = (UndefinedDataValue) super.clone();

        try {
            clone = new UndefinedDataValue(this);
        } catch (SystemErrorException e) {
            clone = null;
        }

        return clone;
    }    
        
    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getItsValue()
     *
     * Return a string containing a copy of the current value of the data value.
     *
     *                          JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public String getItsValue()
    {
        
        return new String(this.itsValue);
    
    } /* UndefinedDataValue::getItsValue() */
    
    /**
     * setItsValue()
     *
     * Set itsValue to the specified value.  In the case of an undefined 
     * data value, the value must be the name of the associated untyped 
     * formal argument, or any valid formal argument name if itsFargID
     * is undefined.
     *
     *                                              JRM -- 8/16/07
     *
     * Changes:
     *
     *    - With the advent of column predicates and the prospect of 
     *      implementing the old MacSHAPA query language in OpenSHAPA,
     *      the requirement that undefined data values only be used to 
     *      replace untyped formal arguments is removed.
     *
     *                                              JRM -- 12/12/08
     */
    
    public void setItsValue(String value)
        throws SystemErrorException
    {
        final String mName = "UndefinedDataValue::setItsValue(): ";
        
        if ( ! ( getDB().IsValidFargName(value) ) )
        {
            throw new SystemErrorException(mName +
                    "value isn't a valid formal argument name");
        }
        
        if ( this.itsFargID != DBIndex.INVALID_ID )
        {
            DBElement dbe;
            FormalArgument fa;
            
            if ( itsFargType == FormalArgument.FArgType.UNDEFINED )
            {
                throw new SystemErrorException(mName + 
                                               "itsFargType == UNDEFINED");
            }
            
            dbe = this.getDB().idx.getElement(this.itsFargID);

            if ( dbe == null )
            {
                throw new SystemErrorException(mName + 
                                               "itsFargID has no referent");
            }
            
            if ( ! ( dbe instanceof FormalArgument ) )
            {
                throw new SystemErrorException(mName +
                        "itsFargID doesn't refer to a formal arg");
            }
            
            fa = (FormalArgument)dbe;
            
            if ( fa.getFargName().compareTo(value) != 0 )
            {
                throw new SystemErrorException(mName + 
                        "value doesn't match farg name");
            }
        }
        
        this.itsValue = new String(value);
        
        return;
        
    } /* UndefinedDataValue::setItsValue() */
  
    /**
     * @return true if the value equals the default value
     */
    @Override
    public boolean isDefault() {
        return true;
    }


    /*************************************************************************/
    /*************************** Overrides: **********************************/
    /*************************************************************************/
    
    /**
     * toString()
     *
     * Returns a String representation of the DBValue for display.
     *
     *                                  JRM -- 8/15/07
     *
     * @return the string value.
     *
     * Changes:
     *
     *     - None.
     */
    
    public String toString()
    {
        return new String(this.itsValue);
    }


    /**
     * toDBString()
     *
     * Returns a database String representation of the DBValue for comparison 
     * against the database's expected value.<br>
     * <i>This function is intended for debugging purposses.</i>
     *
     *                                      JRM -- 8/15/07
     *
     * @return the string value.
     *
     * Changes:
     *
     *    - None.
     */
  
    public String toDBString()
    {
        return ("(UndefinedDataValue (id " + this.getID() +
                ") (itsFargID " + this.itsFargID +
                ") (itsFargType " + this.itsFargType +
                ") (itsCellID " + this.itsCellID +
                ") (itsValue " + new String(this.itsValue) +
                ") (subRange " + this.subRange + "))");
    }
    
    
    /** 
     * updateForFargChange()
     *
     * Update for a change in the formal argument name, and/or subrange.
     *
     *                                          JRM -- 3/22/08
     *
     * Changes:
     *
     *    - None.
     */
    
    public void updateForFargChange(boolean fargNameChanged,
                                    boolean fargSubRangeChanged,
                                    boolean fargRangeChanged,
                                    FormalArgument oldFA,
                                    FormalArgument newFA)
        throws SystemErrorException
    {
        final String mName = "TimeStampDataValue::updateForFargChange(): ";
        
        if ( ( oldFA == null ) || ( newFA == null ) )
        {
            throw new SystemErrorException(mName + 
                                           "null old and/or new FA on entry.");
        }
        
        if ( oldFA.getID() != newFA.getID() )
        {
            throw new SystemErrorException(mName + "old/new FA ID mismatch.");
        }
        
        if ( oldFA.getItsVocabElementID() != newFA.getItsVocabElementID() )
        {
            throw new SystemErrorException(mName + "old/new FA veID mismatch.");
        }
        
        if ( oldFA.getFargType() != newFA.getFargType() )
        {
            throw new SystemErrorException(mName + "old/new FA type mismatch.");
        }
        
        if ( this.itsFargID != newFA.getID() )
        {
            throw new SystemErrorException(mName + "FA/DV faID mismatch.");
        }
        
        if ( this.itsFargType != newFA.getFargType() )
        {
            throw new SystemErrorException(mName + "FA/DV FA type mismatch.");
        }
         
        if ( ( fargSubRangeChanged ) || ( fargRangeChanged ) ) 
        {
            this.updateSubRange(newFA);
        }
        
        if ( fargNameChanged )
        {
            this.setItsValue(newFA.getFargName());
        }
        
        return;
        
    } /* TimeStampDataValue::updateForFargChange() */
    
    
    /**
     * updateSubRange()
     *
     * Nominally, this function should determine if the formal argument 
     * associated with the data value is subranged, and if it is, update
     * the data values representation of the subrange (if any) accordingly.  
     * In passing, it would coerce the value ofthe datavalue into the subrange 
     * if necessary.
     *
     * This is meaningless for an undefine data value, as it never has a 
     * value, and it is only associated with untyped formal arguments.
     *
     * Thus the method verifies that the supplied formal argument is an
     * UnTypedFormalArg, and that the value of the data value equals the
     * name of the formal argument.
     *
     * The fa argument is a reference to the current representation of the
     * formal argument associated with the data value.
     *
     *                                          JRM -- 8/16/07
     *
     * Changes:
     *
     *    - None.
     */
    
    protected void updateSubRange(FormalArgument fa)
        throws SystemErrorException
    {
        final String mName = "UndefinedDataValue::updateSubRange(): ";
        UnTypedFormalArg utfa;
        
        if ( fa == null )
        {
            throw new SystemErrorException(mName + "fa null on entry");    
        }
        
        if ( fa instanceof UnTypedFormalArg )
        {
             this.subRange = false;
        }
        else
        {
            throw new SystemErrorException(mName + "Unexpected fa type");    
        }
        
        utfa = (UnTypedFormalArg)fa;
        
        if ( utfa.getFargName().compareTo(this.itsValue) != 0 )
        {
            throw new SystemErrorException(mName + "farg name mismatch");    
        }
        
        return;
        
    } /* UndefinedDataValue::updateSubRange() */
     
    /**
     * coerceToRange()
     *
     * The value of an UndefinedDataValue must be a valid formal argument name.
     * In addition, if the data value is associated with a formal argument
     * (always an UnTypedFormalArgument), its value must be the name of the
     * formal argument.
     *
     * Thus, coerce to the name of the associated UnTypedFormalArg if defined.
     *
     * Throw a system error if the value is not a valid formal argument name.
     * 
     *                                              JRM -- 070815
     *
     * Changes:
     *
     *    - None.
     */
    
    public String coerceToRange(String value) throws SystemErrorException {
        final String mName = "UndefinedDataValue::coerceToRange(): ";

        if (!getDB().IsValidFargName(value)) {
            throw new SystemErrorException(mName + 
                    "value not a valid formal argument name");
        }
        
        if (this.itsFargID != DBIndex.INVALID_ID) {
            DBElement dbe = this.getDB().idx.getElement(this.itsFargID);

            if (dbe == null) {
                throw new SystemErrorException(mName + 
                                               "itsFargID has no referent");
            }

            FormalArgument fa = (FormalArgument) dbe;
            
            if (fa.getFargName().compareTo(value) != 0) {
                return new String(fa.getFargName());
            }
        }

        return value;        
    } /* UndefinedDataValue::coerceToRange() */

    /**
     * Compares this UndefinedDataValue against another object.
     *
     * @param obj The object to compare this against.
     *
     * @return true if the Object obj is logically equal to this
     * UndefinedDataValue, or false otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }

        // Must be this class to be here
        UndefinedDataValue u = (UndefinedDataValue) obj;
        return u.itsValue.equals(this.itsValue) && super.equals(obj);
    }

    /**
     * @return A hash value for this object.
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += HashUtils.Obj2H(itsValue) * Constants.SEED1;

        return hash;
    }

} /* UndefinedDataValue */
