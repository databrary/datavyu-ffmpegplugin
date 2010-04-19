/*
 * MacshapaDatabase.java
 *
 * Created on July 13, 2008, 11:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.openshapa.models.db;

/**
 * Class MacshapaDatabase
 *
 * Instances of the MacshapaDatabase class are used to store and manipulate
 * MacSHAPA database.
 *
 * In essence, the class is just the Database class with some additional fields
 * needed to store and maintain data paricular to MacSHAPA.
 *
 * In its first incarnation, there are no safeguards to keep the user from
 * inserting things in the database that can't be represented in a MacSHAPA
 * ODB file.  For now, at least, we will simply ignore such features when
 * saving to file.
 *
 * Eventually, it would be useful to add code to prevent the addition of such
 * elements to begin with.  Specifically, we should have code to prevent:
 *
 *      Typed predicate and matrix formal arguments
 *
 *      Subranges on integer, float, nominal, and predicate formal arguments.
 *
 *      Definition of predicates that conflict with the query language preds
 *
 *      Changing the time base from sixty ticks per second.
 *
 * This list isn't exhaustive -- add more as you think of them.
 *
 *                                              -- 7/13/08
 */
public class MacshapaDatabase extends Database {

    /*************************************************************************/
    /*************************** Constants: **********************************/
    /*************************************************************************/
    public final static String DB_TYPE = "MacSHAPA Database";
    public final static float DB_VERSION = 1.0f;

    public final static int MIN_COLUMN_WIDTH = 125;
    public final static int DEFAULT_COLUMN_WIDTH = 200;
    public final static int MAX_COLUMN_WIDTH = 1500;

    public final static int MIN_DEBUG_LEVEL = 0;
    public final static int MAX_DEBUG_LEVEL = 3;

    public final static int MAX_ERRORS_MIN = 1;
    public final static int MAX_ERRORS_MAX = 10;

    public final static int MAX_WARNINGS_MIN = 1;
    public final static int MAX_WARNINGS_MAX = 99;

    public final static long MACSHAPA_MAX_INT = 2147483647;
    public final static long MACSHAPA_MIN_INT = -2147483648;

    public final static double MACSHAPA_MAX_FLT = 4.40282E+38;
    public final static double MACSHAPA_MIN_FLT = 1.17549E-38;

    public final static long MACSHAPA_MIN_TIME = 0;
    public final static long MACSHAPA_MAX_TIME =
            (59 * 60 * 60 * 60) + (59 * 60 * 60) + (59 * 60) + 59;

    public final static int MACSHAPA_TICKS_PER_SECOND = 60;

    // public final static String QUERY_VAR_NAME = "###QUERY VAR###";
    public final static String QUERY_VAR_NAME = "###QueryVar###";

    public final static long MAX_INT = 10000;
    public final static long MIN_INT = -10000;

    /**
     * This constant has been to temporarily allow typed matrix and
     * predicate formal arguments in a MacSHAPA data base.
     *
     * We will dis-allow typed matrix and predicate formal arguemnets
     * again as soon as we get the Openshapa database up and running.
     *
     * However, for now, some of our users are demanding them.
     */
    public final static boolean TYPED_FORMAL_ARGUMENTS_ALLOWED  = true;


    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/
    /**
     * debugLevel:   Integer containing the debug level read from and stored
     *      in a MacSHAPA ODB file.  This value is used to control the
     *      generation of debugging information when a MacSHAPA ODB file is
     *      read.
     *
     * maxErrors:   Integer field containing the maximum number of errors that
     *      may be detected while reading a MacSHAPA ODB file before the
     *      parser gives up trying to scan the input file.
     *
     * maxWarnings:  Integer field containing the maximum number of warnings
     *      the parser will issue before giving up on trying to load a
     *      MacSHAPA ODB file.
     *
     */

    /**
     * Debug level read from and stored in a MacSHAPA ODB file.  This value
     * is used to control the generation of debugging information when a
     * MacSHAPA ODB file is read
     */
    protected int debugLevel = 0;

    /**
     * Maximum number of errors that may be detected while reading a MacSHAPA
     * ODB file before the parser gives up trying to scan the input file.
     */
    protected int maxErrors = 1;

    /**
     * Maximum number of warnings the parser will issue before giving up on
     * trying to load a MacSHAPA ODB file.
     */
    protected int maxWarnings = 1;


    /*************************************************************************/
    /*************************** Constructors: *******************************/
    /*************************************************************************/

    /**
     * Database()
     *
     * Constructor for Database.  Sets up data structures used by all flavors
     * of databases.
     *
     * Since this is a MacSHAPA database, also setup the extra structures
     * needed to store a MacSHAPA database.
     *
     *                                              -- 4/30/07
     *
     * Changes:
     *
     *    - None.
     */
    public MacshapaDatabase() throws SystemErrorException {
        super();

        setupSystemPreds();

        return;

    } /* MacshapaDatabase::MacshapaDatabase() */

    /**
     * Constructor.
     *
     * @param ticksPerSecond The ticks per second to use with this database.
     *
     * @throws SystemErrorException If unable to create macshapa database.
     */
    public MacshapaDatabase(final int ticksPerSecond)
    throws SystemErrorException {
        super(ticksPerSecond);
        setupSystemPreds();
    }


    /*************************************************************************/
    /*************************** Overrides: **********************************/
    /*************************************************************************/
    /**
     * getVersion()
     *
     * Gets the database version number<br>
     * (eg 2.1)
     */

    @Override
    public float getVersion()
    {

        return (DB_VERSION);

    } /* MacshapaDatabase::getVersion() */


    /**
     * getType()
     *
     * Gets the database type string<br>
     * (eg ODB File)
     */

    @Override
    public String getType()
    {

        return (DB_TYPE);

    } /* MacshapaDatabase::getType() */


    /**
     * toMODBFile_includeDataColumnInUserSection() ** OVERRIDE **
     *
     * MacSHAPA databases store a variety of data as data columns in the
     * column list.  Such data columns should not appear in the user section
     * of the MacSHAPA ODB file, and this method exists to allow the database
     * prevent such data columns from appearing in the user section of a
     * MacSHAPA ODB file.
     *
     * At present, this method just prevents the query variable from appearing.
     * As time progresses, it will be necessary to be extended the method to
     * prevent other data columns appearing in the user section.
     *
     * This method examines
     *
     *                                           -- 7/5/09
     * Changes:
     *
     *    - None.
     */

    protected boolean toMODBFile_includeDataColumnInUserSection(final DataColumn dc)
    {
        boolean includeIt = true;

        if ( dc.getName().compareTo(this.QUERY_VAR_NAME) == 0 )
        {
            includeIt = false;
        }

        return(includeIt);

    } /* toMODBFile_includeDataColumnInUserSection() */


    /*************************************************************************/
    /****************** Supported Features -- Overrides: *********************/
    /*************************************************************************/
    /*                                                                       */
    /* Override the supported features methods as required to indicate the   */
    /* standard OpenSHAPA database features that are not supported in        */
    /* MacSHAPA databases.                                                   */
    /*                                                                       */
    /*************************************************************************/

    @Override
    public boolean floatSubrangeSupported()             { return false; }
    @Override
    public boolean integerSubrangeSupported()           { return false; }
    @Override
    public boolean nominalSubrangeSupported()           { return false; }
    @Override
    public boolean predSubrangeSupported()              { return false; }
    @Override
    public boolean tickSizeAgjustmentSupported()        { return false; }

    // TODO: Set this back to false ASAP -- it is a temporary compromise that
    //       should be reversed as soon as we have the regular openshapa
    //       database up and running.
    @Override
    public boolean typedFormalArgsSupported()           { return true; }
    @Override
    public boolean queryVariablesSupported()            { return true; }
    @Override
    public boolean typedColPredFormalArgsSupported()    { return false; }




    /*************************************************************************/
    /************************ Class Methods: *********************************/
    /*************************************************************************/

    /*************************************************************************/
    /*********************** Shadowed Class Methods: *************************/
    /*************************************************************************/
    // MacSHAPA and OpenSHAPA have slightly different definitions of valid
    // data values.  Shadow the appropriate class methods here to reflect this:

    // IsValidTextString() -- shadows version in Database.java
    /**
     * Test to see if a string contains a valid text string -- that is a string
     * that can appear as the value of a cell in a text column variable.
     * Return true if it does, and false if it doesn't.
     *
     * Here, we follow the old MacSHAPA definition of a text string which
     * is is as follows:
     *
     *  <char> --> Any character in the standard roman character set,
     *		   hexadecimal values 0x00 to 0xFF.
     *
     *  <bs> --> back space (i.e. ASCII code 0x08)
     *
     *  <text_string_char> --> ( <char> - ( <bs> ) )
     *
     *  <text_string> --> (<text_string_char>)*
     *
     * Note that the MacSHAPA definition of the text string makes used of
     * characters beyond 0x7F (the end point of the ASCII character set).
     * I'm not sure what Java will do with the characters, but it will probably
     * be different from the Mac
     *                                           -- 1/18/10
     *
     * Changes:
     *
     *    - None.
     *
     */

    public static boolean IsValidTextString(Object obj)
        throws SystemErrorException
    {

        final String mName = "MacshapaDatabase::IsValidTextString(): ";
        char ch;
        int i;
        int len;

        if ( obj == null )
        {
            throw new SystemErrorException(mName + "obj null on entry.");
        }
        else if ( ! ( obj instanceof String ) )
        {
            return false;
        }

        /* If we get this far, we know that obj is a String */

        String s = (String)obj;

        len = s.length();

        for ( i = 0; i < len; i++ ) {

            ch = s.charAt(i);

            if ( ( ch < 0 ) || ( ch > 0xFF ) || ( ch == '\b') )
            {
                // string contains a character that can't appear in a
                // text string.
                System.out.printf("%s: the bad char is %d\n", mName, (int)ch);
                return false;
            }
        }

        return true;

    } /* MacshapaDatabase::IsValidTextString() */


    /*************************************************************************/
    /***************************** Accessors: ********************************/
    /*************************************************************************/

    /**
     * getDebugLevel()
     *
     * Get the current MacSHAPA ODB file debug level.
     *
     *               12/29/08
     *
     * Changes:
     *
     *    - None.
     */

    public int getDebugLevel()
    {
        return this.debugLevel;

    } /* MacshapaDatabase::getDebugLevel() */


    /**
     * setDebugLevel()
     *
     * Set the MacSHAPA ODB file debug level.
     *
     *               12/29/08
     *
     * Changes:
     *
     *    - None.
     */

    public void setDebugLevel(int newDebugLevel)
        throws SystemErrorException
    {
        final String mName = "MacshapaDatabase::setDebugLevel()";

        if ( ( newDebugLevel < MacshapaDatabase.MIN_DEBUG_LEVEL ) ||
             ( newDebugLevel > MacshapaDatabase.MAX_DEBUG_LEVEL ) )
        {
            throw new SystemErrorException(mName +
                                          "newDebugLevel out of range.");
        }

        this.debugLevel = newDebugLevel;

        return;

    } /* MacshapaDatabase::setDebugLevel() */


    /**
     * getMaxErrors()
     *
     * Get the current MacSHAPA ODB file max errors.
     *
     *               12/29/08
     *
     * Changes:
     *
     *    - None.
     */

    public int getMaxErrors()
    {
        return this.maxErrors;

    } /* MacshapaDatabase::getMaxErrors() */


    /**
     * setMaxErrors()
     *
     * Set the MacSHAPA ODB file max errors.
     *
     *               12/29/08
     *
     * Changes:
     *
     *    - None.
     */

    public void setMaxErrors(int newMaxErrors)
        throws SystemErrorException
    {
        final String mName = "MacshapaDatabase::setMaxErrors()";

        if ( ( newMaxErrors < MacshapaDatabase.MAX_ERRORS_MIN ) ||
             ( newMaxErrors > MacshapaDatabase.MAX_ERRORS_MAX ) )
        {
            throw new SystemErrorException(mName +
                                          "newMaxErrors out of range.");
        }

        this.maxErrors = newMaxErrors;

        return;

    } /* MacshapaDatabase::setMaxErrors() */


    /**
     * getMaxWarnings()
     *
     * Get the current MacSHAPA ODB file max warnings.
     *
     *               12/29/08
     *
     * Changes:
     *
     *    - None.
     */

    public int getMaxWarnings()
    {
        return this.maxWarnings;

    } /* MacshapaDatabase::getMaxWarnings() */


    /**
     * setMaxWarnings()
     *
     * Set the MacSHAPA ODB file max warnings.
     *
     *               12/29/08
     *
     * Changes:
     *
     *    - None.
     */

    public void setMaxWarnings(int newMaxWarnings)
        throws SystemErrorException
    {
        final String mName = "MacshapaDatabase::setMaxWarnings()";

        if ( ( newMaxWarnings < MacshapaDatabase.MAX_WARNINGS_MIN ) ||
             ( newMaxWarnings > MacshapaDatabase.MAX_WARNINGS_MAX ) )
        {
            throw new SystemErrorException(mName +
                                          "newMaxWarnings out of range.");
        }

        this.maxWarnings = newMaxWarnings;

        return;

    } /* MacshapaDatabase::setMaxWarnings() */


    /*************************************************************************/
    /***************************** Methods: **********************************/
    /*************************************************************************/

    /**
     * constructPVE()
     *
     * Construct a predicate vocab element for a MacSHAPA database.  Do not
     * insert the pve in the database, but instead return a reference to it.
     *
     * Several versions of this method, to accomodate different numbers of
     * parameters
     *
     *                                              -- 7/20/08
     *
     * Changes:
     *
     *    - None.
     */

    private PredicateVocabElement constructPVE(String name,
                                               String arg1)
        throws SystemErrorException
    {
        FormalArgument fa = null;
        PredicateVocabElement pve = null;

        pve = new PredicateVocabElement(this, name);
        fa = new UnTypedFormalArg(this, arg1);
        pve.appendFormalArg(fa);

        return pve;

    } /* MacshapaDatabase::constructPVE(name, arg1) */

    private PredicateVocabElement constructPVE(String name,
                                               String arg1,
                                               String arg2)
        throws SystemErrorException
    {
        FormalArgument fa = null;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1);
        fa = new UnTypedFormalArg(this, arg2);
        pve.appendFormalArg(fa);

        return pve;

    } /* MacshapaDatabase::constructPVE(name, arg1, arg2) */

    private PredicateVocabElement constructPVE(String name,
                                               String arg1,
                                               String arg2,
                                               String arg3)
        throws SystemErrorException
    {
        FormalArgument fa = null;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1, arg2);
        fa = new UnTypedFormalArg(this, arg3);
        pve.appendFormalArg(fa);

        return pve;

    } /* MacshapaDatabase::constructPVE(name, arg1, arg2, arg3) */

    private PredicateVocabElement constructPVE(String name,
                                               String arg1,
                                               String arg2,
                                               String arg3,
                                               String arg4)
        throws SystemErrorException
    {
        FormalArgument fa = null;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1, arg2, arg3);
        fa = new UnTypedFormalArg(this, arg4);
        pve.appendFormalArg(fa);

        return pve;

    } /* MacshapaDatabase::constructPVE(name, arg1, arg2, arg3, arg4) */

    private PredicateVocabElement constructPVE(String name,
                                               String arg1,
                                               String arg2,
                                               String arg3,
                                               String arg4,
                                               String arg5)
        throws SystemErrorException
    {
        FormalArgument fa = null;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1, arg2, arg3, arg4);
        fa = new UnTypedFormalArg(this, arg5);
        pve.appendFormalArg(fa);

        return pve;

    } /* MacshapaDatabase::constructPVE(name, arg1, arg2, arg3, arg4, arg5) */

    private PredicateVocabElement constructPVE(String name,
                                               String arg1,
                                               String arg2,
                                               String arg3,
                                               String arg4,
                                               String arg5,
                                               String arg6)
        throws SystemErrorException
    {
        FormalArgument fa = null;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1, arg2, arg3, arg4, arg5);
        fa = new UnTypedFormalArg(this, arg6);
        pve.appendFormalArg(fa);

        return pve;

    } /* MacshapaDatabase::constructPVE(name, arg1, arg2, ... arg6) */

    private PredicateVocabElement constructPVE(String name,
                                               String arg1,
                                               String arg2,
                                               String arg3,
                                               String arg4,
                                               String arg5,
                                               String arg6,
                                               String arg7)
        throws SystemErrorException
    {
        FormalArgument fa = null;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1, arg2, arg3, arg4, arg5, arg6);
        fa = new UnTypedFormalArg(this, arg7);
        pve.appendFormalArg(fa);

        return pve;

    } /* MacshapaDatabase::constructPVE(name, arg1, arg2, ... arg7) */


    /**
     * definePVE()
     *
     * Construct a predicate vocab element for a MacSHAPA database with the
     * indicated name, formal arguments, variable length and system flags,
     * and then insert it into the database. Return the ID assigned to the
     * pve.
     *
     * Several versions of this method, to accomodate different numbers of
     * parameters
     *
     *                                              -- 7/20/08
     *
     * Changes:
     *
     * <ul>
     *   <li>
     *      Modified the method to use addSystemPredVE() it the system
     *      parameter is true.
     *                                              7/26/09
     *   </li>
     * </ul>
     */

    private long definePVE(String name,
                           String arg1,
                           boolean vLen,
                           boolean system) 
        throws SystemErrorException
    {
        long pve_id = DBIndex.INVALID_ID;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1);
        
        pve.setVarLen(vLen);

        if ( system )
        {
            pve.setSystem();
            pve_id = this.addSystemPredVE(pve);
        }
        else
        {
            pve_id = this.addPredVE(pve);
        }

        return pve_id;

    } /* MacshapaDatabase::definePVE(name, arg1, vLen, system) */

    private long definePVE(String name,
                           String arg1,
                           String arg2,
                           boolean vLen,
                           boolean system) 
        throws SystemErrorException
    {
        long pve_id = DBIndex.INVALID_ID;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1, arg2);

        pve.setVarLen(vLen);

        if ( system )
        {
            pve.setSystem();
            pve_id = this.addSystemPredVE(pve);
        }
        else
        {
            pve_id = this.addPredVE(pve);
        }

        return pve_id;

    } /* MacshapaDatabase::definePVE(name, arg1, arg2, vLen, system) */

    private long definePVE(String name,
                           String arg1,
                           String arg2,
                           String arg3,
                           boolean vLen,
                           boolean system) 
        throws SystemErrorException
    {
        long pve_id = DBIndex.INVALID_ID;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1, arg2, arg3);

        pve.setVarLen(vLen);

        if ( system )
        {
            pve.setSystem();
            pve_id = this.addSystemPredVE(pve);
        }
        else
        {
            pve_id = this.addPredVE(pve);
        }

        return pve_id;

    } /* MacshapaDatabase::definePVE(name, arg1, ... arg3, vLen, system) */

    private long definePVE(String name,
                           String arg1,
                           String arg2,
                           String arg3,
                           String arg4,
                           boolean vLen,
                           boolean system) 
        throws SystemErrorException
    {
        long pve_id = DBIndex.INVALID_ID;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1, arg2, arg3, arg4);

        pve.setVarLen(vLen);

        if ( system )
        {
            pve.setSystem();
            pve_id = this.addSystemPredVE(pve);
        }
        else
        {
            pve_id = this.addPredVE(pve);
        }

        return pve_id;

    } /* MacshapaDatabase::definePVE(name, arg1, ... arg4, vLen, system) */

    private long definePVE(String name,
                           String arg1,
                           String arg2,
                           String arg3,
                           String arg4,
                           String arg5,
                           boolean vLen,
                           boolean system) 
        throws SystemErrorException
    {
        long pve_id = DBIndex.INVALID_ID;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1, arg2, arg3, arg4, arg5);

        pve.setVarLen(vLen);

        if ( system )
        {
            pve.setSystem();
            pve_id = this.addSystemPredVE(pve);
        }
        else
        {
            pve_id = this.addPredVE(pve);
        }

        return pve_id;

    } /* MacshapaDatabase::definePVE(name, arg1, ... arg5, vLen, system) */


    private long definePVE(String name,
                           String arg1,
                           String arg2,
                           String arg3,
                           String arg4,
                           String arg5,
                           String arg6,
                           boolean vLen,
                           boolean system) 
        throws SystemErrorException
    {
        long pve_id = DBIndex.INVALID_ID;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1, arg2, arg3, arg4, arg5, arg6);

        pve.setVarLen(vLen);

        if ( system )
        {
            pve.setSystem();
            pve_id = this.addSystemPredVE(pve);
        }
        else
        {
            pve_id = this.addPredVE(pve);
        }

        return pve_id;

    } /* MacshapaDatabase::definePVE(name, arg1, ... arg6, vLen, system) */


    private long definePVE(String name,
                           String arg1,
                           String arg2,
                           String arg3,
                           String arg4,
                           String arg5,
                           String arg6,
                           String arg7,
                           boolean vLen,
                           boolean system) 
        throws SystemErrorException
    {
        long pve_id = DBIndex.INVALID_ID;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1, arg2, arg3, arg4, arg5, arg6, arg7);

        pve.setVarLen(vLen);

        if ( system )
        {
            pve.setSystem();
            pve_id = this.addSystemPredVE(pve);
        }
        else
        {
            pve_id = this.addPredVE(pve);
        }

        return pve_id;

    } /* MacshapaDatabase::definePVE(name, arg1, ... arg7, vLen, system) */


    /**
     * defineSystemPVE()
     *
     * Construct a predicate vocab element for a MacSHAPA database with the
     * indicated name, formal arguments, and variable length flag, and then
     * insert it into the database as a system pve. Return the ID assigned
     * to the pve.
     *
     * Several versions of this method, to accomodate different numbers of
     * parameters
     *
     *                                              -- 7/20/08
     *
     * Changes:
     *
     *    - None.
     */
    protected long defineSystemPVE(String name,
                                   String arg1,
                                   boolean vLen)
        throws SystemErrorException
    {
        return definePVE(name, arg1, vLen, true);
    }

    protected long defineSystemPVE(String name,
                                   String arg1,
                                   String arg2,
                                   boolean vLen)
        throws SystemErrorException
    {
        return definePVE(name, arg1, arg2, vLen, true);
    }

    protected long defineSystemPVE(String name,
                                   String arg1,
                                   String arg2,
                                   String arg3,
                                   boolean vLen)
        throws SystemErrorException
    {
        return definePVE(name, arg1, arg2, arg3, vLen, true);
    }

    protected long defineSystemPVE(String name,
                                   String arg1,
                                   String arg2,
                                   String arg3,
                                   String arg4,
                                   boolean vLen)
        throws SystemErrorException
    {
        return definePVE(name, arg1, arg2, arg3, arg4, vLen, true);
    }

    protected long defineSystemPVE(String name,
                                   String arg1,
                                   String arg2,
                                   String arg3,
                                   String arg4,
                                   String arg5,
                                   boolean vLen)
        throws SystemErrorException
    {
        return definePVE(name, arg1, arg2, arg3, arg4, arg5, vLen, true);
    }

    protected long defineSystemPVE(String name,
                                   String arg1,
                                   String arg2,
                                   String arg3,
                                   String arg4,
                                   String arg5,
                                   String arg6,
                                   boolean vLen)
        throws SystemErrorException
    {
        return definePVE(name, arg1, arg2, arg3, arg4, arg5, arg6, vLen, true);
    }

    protected long defineSystemPVE(String name,
                                   String arg1,
                                   String arg2,
                                   String arg3,
                                   String arg4,
                                   String arg5,
                                   String arg6,
                                   String arg7,
                                   boolean vLen)
        throws SystemErrorException
    {
        return definePVE(name, arg1, arg2, arg3, arg4, arg5, arg6, arg7, vLen, true);
    }


    /**
     * defineUserPVE()
     *
     * Construct a predicate vocab element for a MacSHAPA database with the
     * indicated name, formal arguments, and variable length flag, and then
     * insert it into the database as a non-system pve. Return the ID
     * assigned to the pve.
     *
     * Several versions of this method, to accomodate different numbers of
     * parameters
     *
     *                                              -- 7/20/08
     *
     * Changes:
     *
     *    - None.
     */
    protected long defineUserPVE(String name,
                                 String arg1,
                                 boolean vLen)
        throws SystemErrorException
    {
        return definePVE(name, arg1, vLen, false);
    }

    protected long defineUserPVE(String name,
                                 String arg1,
                                 String arg2,
                                 boolean vLen)
        throws SystemErrorException
    {
        return definePVE(name, arg1, arg2, vLen, false);
    }

    protected long defineUserPVE(String name,
                                 String arg1,
                                 String arg2,
                                 String arg3,
                                 boolean vLen)
        throws SystemErrorException
    {
        return definePVE(name, arg1, arg2, arg3, vLen, false);
    }

    protected long defineUserPVE(String name,
                                 String arg1,
                                 String arg2,
                                 String arg3,
                                 String arg4,
                                 boolean vLen)
        throws SystemErrorException
    {
        return definePVE(name, arg1, arg2, arg3, arg4, vLen, false);
    }

    protected long defineUserPVE(String name,
                                 String arg1,
                                 String arg2,
                                 String arg3,
                                 String arg4,
                                 String arg5,
                                 boolean vLen)
        throws SystemErrorException
    {
        return definePVE(name, arg1, arg2, arg3, arg4, arg5, vLen, false);
    }

    protected long defineUserPVE(String name,
                                 String arg1,
                                 String arg2,
                                 String arg3,
                                 String arg4,
                                 String arg5,
                                 String arg6,
                                 boolean vLen)
        throws SystemErrorException
    {
        return definePVE(name, arg1, arg2, arg3, arg4, arg5, arg6, vLen, false);
    }

    protected long defineUserPVE(String name,
                                 String arg1,
                                 String arg2,
                                 String arg3,
                                 String arg4,
                                 String arg5,
                                 String arg6,
                                 String arg7,
                                 boolean vLen)
        throws SystemErrorException
    {
        return definePVE(name, arg1, arg2, arg3, arg4, arg5, arg6, arg7, vLen, false);
    }

    /**
     * setupSystemPreds()
     *
     * Create the system predicates that exist in every MacSHAPA database.
     *
     *                                              -- 7/19/08
     * Changes:
     *
     *    - None
     */
    private void setupSystemPreds()
    throws SystemErrorException {
        final String mName = "MacshapaDatabase::constructSystemPreds()";

        defineSystemPVE("rule", "<condition>", "<action>", false);
        defineSystemPVE("query", "<condition>", "<action>", false);
        defineSystemPVE("or", "<arg1>", "<arg2>", "<arg3>",
                        "<arg4>", "<arg5>", "<arg6>", "<arg7>", true);
        defineSystemPVE("and", "<arg1>", "<arg2>", "<arg3>",
                        "<arg4>", "<arg5>", "<arg6>", "<arg7>", true);
        defineSystemPVE("not", "<arg1>", false);
        defineSystemPVE("assign", "<query-var>", "<val>", false);


        /* boolean predicates */
        defineSystemPVE("lt", "<x>", "<y>", false);
        defineSystemPVE("gt", "<x>", "<y>", false);
        defineSystemPVE("le", "<x>", "<y>", false);
        defineSystemPVE("ge", "<x>", "<y>", false);
        defineSystemPVE("eq", "<x>", "<y>", false);
        defineSystemPVE("ne", "<x>", "<y>", false);
        defineSystemPVE("substring", "<substr>", "<string>", false);
        defineSystemPVE("selected", "<var>", false);
        defineSystemPVE("isinteger", "<arg>", false);
        defineSystemPVE("istime", "<arg>", false);
        defineSystemPVE("isfloat", "<arg>", false);
        defineSystemPVE("isnominal", "<arg>", false);
        defineSystemPVE("isqstring", "<arg>", false);
        defineSystemPVE("istext", "<arg>", false);
        defineSystemPVE("ispred", "<arg>", false);
        defineSystemPVE("isempty", "<arg>", false);


        /* executable predicates */
        defineSystemPVE("print", "<arg1>", "<arg2>", "<arg3>",
                            "<arg4>", "<arg5>", "<arg6>", "<arg7>", true);
        defineSystemPVE("count", "<form>", false);
        defineSystemPVE("insert", "<var>", true);
        defineSystemPVE("sum", "<addend>", false);
        defineSystemPVE("modify", "<var>", true);
        defineSystemPVE("delete", "<var>", true);
        defineSystemPVE("select", "<var>", true);
        defineSystemPVE("deselect", "<var>", true);
        defineSystemPVE("cmin", "<val>", false);
        defineSystemPVE("cmax", "<val>", false);
        defineSystemPVE("cmean", "<val>", false);


        /* data predicates */
        defineSystemPVE("times", "<x>", "<y>", false);
        defineSystemPVE("divide", "<x>", "<y>", false);
        defineSystemPVE("plus", "<x>", "<y>", false);
        defineSystemPVE("minus", "<x>", "<y>", false);
        defineSystemPVE("clear", "<void>", false);
        defineSystemPVE("max", "<x>", "<y>", false);
        defineSystemPVE("min", "<x>", "<y>", false);
        defineSystemPVE("concat", "<str1>", "<str2>", true);
        defineSystemPVE("concatq", "<str1>", "<str2>", true);
        defineSystemPVE("sin", "<x>", false);
        defineSystemPVE("cos", "<x>", false);
        defineSystemPVE("tan", "<x>", false);
        defineSystemPVE("asin", "<x>", false);
        defineSystemPVE("acos", "<x>", false);
        defineSystemPVE("atan", "<x>", false);
        defineSystemPVE("abs", "<x>", false);
        defineSystemPVE("mod", "<x>", "<y>", false);
        defineSystemPVE("rem", "<x>", "<y>", false);


        /* macro predicates */
        defineSystemPVE("aftero", "<base-var>", "<successor-var>",
                        "<min-ord>", "<max-ord>", false);
        defineSystemPVE("aftert", "<base-var>", "<successor-var>",
                        "<min-time>", "<max-time>", false);
        defineSystemPVE("beforeo", "<base-var>", "<predecessor-var>",
                        "<min-ord>", "<max-ord>", false);
        defineSystemPVE("beforet", "<base-var>", "<predecessor-var>",
                        "<min-time>", "<max-time>", false);
        defineSystemPVE("during1", "<base-var>", "<contemporary-var>", false);
        defineSystemPVE("during2", "<base-var>", "<contemporary-var>", false);
        defineSystemPVE("during3", "<base-var>", "<contemporary-var>", false);
        defineSystemPVE("nextinstbo", "<base-var>", "<successor-var>", false);
        defineSystemPVE("nextinstbt", "<base-var>", "<successor-var>", false);
        defineSystemPVE("nextinsto", "<base-var>", "<successor-var>", false);
        defineSystemPVE("nextinstt", "<base-var>", "<successor-var>", false);
        defineSystemPVE("nexto", "<base-var>", "<successor-var>", false);
        defineSystemPVE("nextt", "<base-var>", "<successor-var>", false);
        defineSystemPVE("previnstbo", "<base-var>", "<predecessor-var>", false);
        defineSystemPVE("previnstbt", "<base-var>", "<predecessor-var>", false);
        defineSystemPVE("previnsto", "<base-var>", "<predecessor-var>", false);
        defineSystemPVE("previnstt", "<base-var>", "<predecessor-var>", false);
        defineSystemPVE("prevo", "<base-var>", "<predecessor-var>", false);
        defineSystemPVE("prevt", "<base-var>", "<predecessor-var>", false);

        return;

    } /* MacshapaDatabase::setupSystemPreds() */



    /**
     * toMODBFile()
     *
     * Write the contents of the database in MacSHAPA ODB file format to
     * the supplied stream.
     *
     * The newLine parameter exists to assist debugging.  While MacSHAPA
     * ODB files must always use '\r' as the new line character, in our
     * internal test code, it is frequently useful to use '\n' instead.
     *
     *                                              12/29/08
     *
     * Changes:
     *
     *    - None.
     */

    public void toMODBFile(java.io.PrintStream output,
                           String newLine)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaDatabase::toMODBFile()";

        if ( output == null )
        {
            throw new SystemErrorException(mName + "output null on entry");
        }

        if ( newLine == null )
        {
            throw new SystemErrorException(mName + "newLine null on entry");
        }

        output.printf("; MacSHAPA Open Database%s", newLine);
        output.printf("( setf macshapa-db%s", newLine);
        output.printf(" '(%s", newLine);

        this.toMODBFile_headerSection(output, newLine, "    ");

        this.toMODBFile_userSection(output, newLine, "    ");

        this.toMODBFile_querySection(output, newLine, "    ");

        this.toMODBFile_systemSection(output, newLine, "    ");

        output.printf("  )%s", newLine);
        output.printf(")%s", newLine);

        return;

    } /* MacshapaDatabase::toMODBFile() */


    /**
     * toMODBFile_headerSection()
     *
     * Write the contents of the header section of a MacSHAPA ODB file to
     * the supplied stream.
     *
     * The newLine parameter exists to assist debugging.  While MacSHAPA
     * ODB files must always use '\r' as the new line character, in our
     * internal test code, it is frequently useful to use '\n' instead.
     *
     *                                              12/29/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void toMODBFile_headerSection(java.io.PrintStream output,
                                            String newLine,
                                            String indent)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaDatabase::toMODBFile_headerSection()";

        if ( output == null )
        {
            throw new SystemErrorException(mName + "output null on entry");
        }

        if ( newLine == null )
        {
            throw new SystemErrorException(mName + "newLine null on entry");
        }

        if ( indent == null )
        {
            throw new SystemErrorException(mName + "indent null on entry");
        }

        output.printf("%s( HEADER>%s", indent, newLine);

        output.printf("%s  (%s", indent, newLine);

        output.printf("%s    ( NAME> \"%s\" )%s", indent, this.name, newLine);
        output.printf("%s    ( VERSION> 1 )%s", indent, newLine);
        output.printf("%s    ( DEBUG-LEVEL> %d )%s",
                      indent, this.debugLevel, newLine);
        output.printf("%s    ( MAX-ERRORS> %d )%s",
                      indent, this.maxErrors, newLine);
        output.printf("%s    ( MAX-WARNINGS> %d )%s",
                      indent, this.maxWarnings, newLine);

        output.printf("%s  )%s", indent, newLine);

        output.printf("%s)%s", indent, newLine);

        return;

    } /* MacshapaDatabase::toMODBFile_headerSection() */


    /**
     * toMODBFile_predDefs()
     *
     * Write the contents of the predicated definition list entry in
     * the user section of a MacSHAPA ODB file to the supplied stream.
     *
     * The newLine parameter exists to assist debugging.  While MacSHAPA
     * ODB files must always use '\r' as the new line character, in our
     * internal test code, it is frequently useful to use '\n' instead.
     *
     *                                              12/29/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void toMODBFile_predDefs(java.io.PrintStream output,
                                       String newLine,
                                       String indent)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaDatabase::toMODBFile_predDefs()";

        if ( output == null )
        {
            throw new SystemErrorException(mName + "output null on entry");
        }

        if ( newLine == null )
        {
            throw new SystemErrorException(mName + "newLine null on entry");
        }

        if ( indent == null )
        {
            throw new SystemErrorException(mName + "indent null on entry");
        }

        output.printf("%s( PREDICATE-DEFINITIONS>%s", indent, newLine);

        this.vl.toMODBFile_predDefs(output, newLine, indent + "  ");

        output.printf("%s)%s", indent, newLine);

        return;

    } /* MacshapaDatabase::toMODBFile_predDefs() */


    /**
     * toMODBFile_querySection()
     *
     * Write the contents of the query section of a MacSHAPA ODB file to
     * the supplied stream.
     *
     * Note that while the queries are simply stored in an instance of
     * DataColumn, the format in which the are displayed in a MacSHAPA
     * ODB database is different from that of a normal column.  Hence we
     * handle most of the printing of the queries in this function, instead
     * of passing it down to the column list as we do with regular columns.
     *
     * The newLine parameter exists to assist debugging.  While MacSHAPA
     * ODB files must always use '\r' as the new line character, in our
     * internal test code, it is frequently useful to use '\n' instead.
     *
     *                                              12/29/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void toMODBFile_querySection(java.io.PrintStream output,
                                           String newLine,
                                           String indent)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaDatabase::toMODBFile_querySection()";
        DataCell cell;
        DataValue dv = null;
        PredDataValue pdv = null;
        DataColumn qdc = null;
        int i;
        int numQueries = 0;

        if ( output == null )
        {
            throw new SystemErrorException(mName + "output null on entry");
        }

        if ( newLine == null )
        {
            throw new SystemErrorException(mName + "newLine null on entry");
        }

        if ( indent == null )
        {
            throw new SystemErrorException(mName + "indent null on entry");
        }

        // test to see if the query column exists.  If it does, get a reference
        // to it and prepare to dump its contents.
        if ( this.cl.inColumnList(QUERY_VAR_NAME) )
        {
            qdc = (DataColumn)this.cl.getColumn(QUERY_VAR_NAME);

            if ( qdc.getItsMveType() != MatrixVocabElement.MatrixType.PREDICATE )
            {
                throw new SystemErrorException(mName +
                        "query column not a predicate column?!?");
            }

            numQueries = qdc.getNumCells();
        }

        output.printf("%s( QUERY>%s", indent, newLine);


        output.printf("%s  (%s", indent, newLine);

        // dump queries here
        if ( qdc != null ) // we have queries to dump
        {
            for ( i = 0; i < numQueries; i++ )
            {
                // This is the actual cell in the database -- must be careful not
                // to modify it in any way.
                cell = qdc.getCell(i + 1);

                if ( cell == null )
                {
                    throw new SystemErrorException(mName + "qdc.getCell(" + i +
                                                   " + 1) returned null?!?");
                }

                if ( cell.val.getNumArgs() != 1 )
                {
                    throw new SystemErrorException(mName +
                                                   "cell.val.getNumArgs() != 1");
                }

                dv = cell.val.getArg(0);

                if ( ! ( dv instanceof PredDataValue ) )
                {
                    throw new SystemErrorException(mName +
                                                   "dv not a pred data val?!?");
                }

                pdv = (PredDataValue)dv;

                output.printf("%s    ", indent);
                pdv.toMODBFile(output);
                output.printf("%s", newLine);
            }
        }

        output.printf("%s  )%s", indent, newLine);

        output.printf("%s)%s", indent, newLine);

        return;

    } /* MacshapaDatabase::toMODBFile_querySection() */


    /**
     * toMODBFile_sVarDecs()
     *
     * Write the contents of the spreadsheed variable declarations list
     * entry in the user section of a MacSHAPA ODB file to the supplied
     * stream.
     *
     * The newLine parameter exists to assist debugging.  While MacSHAPA
     * ODB files must always use '\r' as the new line character, in our
     * internal test code, it is frequently useful to use '\n' instead.
     *
     *                                              12/29/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void toMODBFile_sVarDecs(java.io.PrintStream output,
                                       String newLine,
                                       String indent)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaDatabase::toMODBFile_sVarDecs()";

        if ( output == null )
        {
            throw new SystemErrorException(mName + "output null on entry");
        }

        if ( newLine == null )
        {
            throw new SystemErrorException(mName + "newLine null on entry");
        }

        if ( indent == null )
        {
            throw new SystemErrorException(mName + "indent null on entry");
        }

        output.printf("%s( SPREADSHEET-VARIABLE-DECLARATIONS>%s",
                      indent, newLine);

        this.cl.toMODBFile_colDecs(output, newLine, indent + "  ");

        output.printf("%s)%s", indent, newLine);

        return;

    } /* MacshapaDatabase::toMODBFile_sVarDecs() */


    /**
     * toMODBFile_sVarDefs()
     *
     * Write the contents of the spreadsheed variable definitions list
     * entry in the user section of a MacSHAPA ODB file to the supplied
     * stream.
     *
     * The newLine parameter exists to assist debugging.  While MacSHAPA
     * ODB files must always use '\r' as the new line character, in our
     * internal test code, it is frequently useful to use '\n' instead.
     *
     *                                              12/29/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void toMODBFile_sVarDefs(java.io.PrintStream output,
                                       String newLine,
                                       String indent)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaDatabase::toMODBFile_sVarDefs()";

        if ( output == null )
        {
            throw new SystemErrorException(mName + "output null on entry");
        }

        if ( newLine == null )
        {
            throw new SystemErrorException(mName + "newLine null on entry");
        }

        if ( indent == null )
        {
            throw new SystemErrorException(mName + "indent null on entry");
        }

        output.printf("%s( SPREADSHEET-VARIABLE-DEFINITIONS>%s",
                      indent, newLine);

        this.cl.toMODBFile_colDefs(output, newLine, indent + "  ");

        output.printf("%s)%s", indent, newLine);

        return;

    } /* MacshapaDatabase::toMODBFile_sVarDefs() */


    /**
     * toMODBFile_systemSection()
     *
     * Write the contents of the system section of a MacSHAPA ODB file to
     * the supplied stream.
     *
     * The newLine parameter exists to assist debugging.  While MacSHAPA
     * ODB files must always use '\r' as the new line character, in our
     * internal test code, it is frequently useful to use '\n' instead.
     *
     *                                              12/29/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void toMODBFile_systemSection(java.io.PrintStream output,
                                            String newLine,
                                            String indent)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaDatabase::toMODBFile_systemSection()";

        if ( output == null )
        {
            throw new SystemErrorException(mName + "output null on entry");
        }

        if ( newLine == null )
        {
            throw new SystemErrorException(mName + "newLine null on entry");
        }

        if ( indent == null )
        {
            throw new SystemErrorException(mName + "indent null on entry");
        }

        output.printf("%s( SYSTEM>%s", indent, newLine);

        output.printf("%s  (%s", indent, newLine);

        toMODBFile_systemSection_shapaPaneVars(output,
                                               newLine,
                                               indent + "    ");

        // add code to dump groups when we have reference columns going.

        // add code to dump import formats when and if we find it necessary.

        // add code to dump alignments when and if we find it necessary.

        output.printf("%s  )%s", indent, newLine);

        output.printf("%s)%s", indent, newLine);

        return;

    } /* MacshapaDatabase::toMODBFile_systemSection() */


    /**
     * toMODBFile_systemSection_shapaPaneVars()
     *
     * Write the contents of the system section of a MacSHAPA ODB file to
     * the supplied stream.
     *
     * The newLine parameter exists to assist debugging.  While MacSHAPA
     * ODB files must always use '\r' as the new line character, in our
     * internal test code, it is frequently useful to use '\n' instead.
     *
     *                                              JRM -- 12/29/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void toMODBFile_systemSection_shapaPaneVars(
                                                java.io.PrintStream output,
                                                String newLine,
                                                String indent)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName =
                "MacshapaDatabase::toMODBFile_systemSection_shapaPaneVars()";
        int i;
        long id;
        java.util.Vector<Long> cov;
        Column col;
        DataColumn dc;

        if ( output == null )
        {
            throw new SystemErrorException(mName + "output null on entry");
        }

        if ( newLine == null )
        {
            throw new SystemErrorException(mName + "newLine null on entry");
        }

        if ( indent == null )
        {
            throw new SystemErrorException(mName + "indent null on entry");
        }

        cov = this.cl.getColOrderVector();

        output.printf("%s( SHAPA-PANE-VARS>%s", indent, newLine);

        output.printf("%s  (%s", indent, newLine);

        for ( i = 0; i < cov.size(); i++ )
        {
            id = cov.get(i);

            col = this.cl.getColumn(id);

            if ( col instanceof DataColumn )
            {
                dc = (DataColumn)col;

                if ( ( ! dc.getHidden() ) &&
                     ( this.toMODBFile_includeDataColumnInUserSection(dc) ) )
                {
                    output.printf("%s    |%s|%s", indent, dc.getName(), newLine);
                }
            }
        }

        output.printf("%s  )%s", indent, newLine);

        output.printf("%s)%s", indent, newLine);

        return;

    } /* MacshapaDatabase::toMODBFile_systemSection_shapaPaneVars() */


    /**
     * toMODBFile_userSection()
     *
     * Write the contents of the user section of a MacSHAPA ODB file to
     * the supplied stream.
     *
     * The newLine parameter exists to assist debugging.  While MacSHAPA
     * ODB files must always use '\r' as the new line character, in our
     * internal test code, it is frequently useful to use '\n' instead.
     *
     *                                              12/29/08
     *
     * Changes:
     *
     *    - None.
     */

    protected void toMODBFile_userSection(java.io.PrintStream output,
                                          String newLine,
                                          String indent)
        throws SystemErrorException,
               java.io.IOException
    {
        final String mName = "MacshapaDatabase::toMODBFile_userSection()";

        if ( output == null )
        {
            throw new SystemErrorException(mName + "output null on entry");
        }

        if ( newLine == null )
        {
            throw new SystemErrorException(mName + "newLine null on entry");
        }

        if ( indent == null )
        {
            throw new SystemErrorException(mName + "indent null on entry");
        }

        output.printf("%s( USER>%s", indent, newLine);

        output.printf("%s  (%s", indent, newLine);

        this.toMODBFile_predDefs(output, newLine, indent + "    ");

        this.toMODBFile_sVarDecs(output, newLine, indent + "    ");

        this.toMODBFile_sVarDefs(output, newLine, indent + "    ");

        output.printf("%s  )%s", indent, newLine);

        output.printf("%s)%s", indent, newLine);

        return;

    } /* MacshapaDatabase::toMODBFile_userSection() */
}
