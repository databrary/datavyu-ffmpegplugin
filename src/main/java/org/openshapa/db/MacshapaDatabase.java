/*
 * MacshapaDatabase.java
 *
 * Created on July 13, 2008, 11:03 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.openshapa.db;

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
 *                                              JRM -- 7/13/08
 *
 *
 * @author mainzer
 */
public class MacshapaDatabase extends Database
{

    /*************************************************************************/
    /*************************** Constants: **********************************/
    /*************************************************************************/

    public final static String DB_TYPE = "MacSHAPA Database";
    public final static float DB_VERSION = 1.0f;

    public final static long MAX_INT = 10000;
    public final static long MIN_INT = -10000;

    /*************************************************************************/
    /***************************** Fields: ***********************************/
    /*************************************************************************/



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
     *                                              JRM -- 4/30/07
     *
     * Changes:
     *
     *    - None.
     */

    public MacshapaDatabase()
    throws SystemErrorException, LogicErrorException {
        super();

        setupSystemPreds();

        return;

    } /* MacshapaDatabase::MacshapaDatabase() */


    /*************************************************************************/
    /*************************** Overrides: **********************************/
    /*************************************************************************/

    /**
     * getVersion()
     *
     * Gets the database version number<br>
     * (eg 2.1)
     */

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

    public String getType()
    {

        return (DB_TYPE);

    } /* MacshapaDatabase::getType() */

    /*************************************************************************/
    /****************** Supported Features -- Overrides: *********************/
    /*************************************************************************/
    /*                                                                       */
    /* Override the supported features methods as required to indicate the   */
    /* standard OpenSHAPA database features that are not supported in        */
    /* MacSHAPA databases.                                                   */
    /*                                                                       */
    /*************************************************************************/

    public boolean floatSubrangeSupported()         { return false; }
    public boolean integerSubrangeSupported()       { return false; }
    public boolean nominalSubrangeSupported()       { return false; }
    public boolean predSubrangeSupported()          { return false; }
    public boolean tickSizeAgjustmentSupported()    { return false; }
    public boolean typedFormalArgsSupported()       { return false; }


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
     *                                              JRM -- 7/20/08
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
     *                                              JRM -- 7/20/08
     *
     * Changes:
     *
     *    - None.
     */

    private long definePVE(String name,
                           String arg1,
                           boolean vLen,
                           boolean system)
    throws SystemErrorException, LogicErrorException {
        long pve_id = DBIndex.INVALID_ID;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1);
        pve.setVarLen(vLen);
        if ( system )
        {
            pve.setSystem();
        }

        pve_id = this.addPredVE(pve);

        return pve_id;

    } /* MacshapaDatabase::definePVE(name, arg1, vLen, system) */

    private long definePVE(String name,
                           String arg1,
                           String arg2,
                           boolean vLen,
                           boolean system)
    throws SystemErrorException, LogicErrorException {
        long pve_id = DBIndex.INVALID_ID;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1, arg2);
        pve.setVarLen(vLen);
        if ( system )
        {
            pve.setSystem();
        }

        pve_id = this.addPredVE(pve);

        return pve_id;

    } /* MacshapaDatabase::definePVE(name, arg1, arg2, vLen, system) */

    private long definePVE(String name,
                           String arg1,
                           String arg2,
                           String arg3,
                           boolean vLen,
                           boolean system)
    throws SystemErrorException, LogicErrorException {
        long pve_id = DBIndex.INVALID_ID;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1, arg2, arg3);
        pve.setVarLen(vLen);
        if ( system )
        {
            pve.setSystem();
        }

        pve_id = this.addPredVE(pve);

        return pve_id;

    } /* MacshapaDatabase::definePVE(name, arg1, ... arg3, vLen, system) */

    private long definePVE(String name,
                           String arg1,
                           String arg2,
                           String arg3,
                           String arg4,
                           boolean vLen,
                           boolean system)
    throws SystemErrorException, LogicErrorException {
        long pve_id = DBIndex.INVALID_ID;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1, arg2, arg3, arg4);
        pve.setVarLen(vLen);
        if ( system )
        {
            pve.setSystem();
        }

        pve_id = this.addPredVE(pve);

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
    throws SystemErrorException, LogicErrorException {
        long pve_id = DBIndex.INVALID_ID;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1, arg2, arg3, arg4, arg5);
        pve.setVarLen(vLen);
        if ( system )
        {
            pve.setSystem();
        }

        pve_id = this.addPredVE(pve);

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
    throws SystemErrorException, LogicErrorException {
        long pve_id = DBIndex.INVALID_ID;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1, arg2, arg3, arg4, arg5, arg6);
        pve.setVarLen(vLen);
        if ( system )
        {
            pve.setSystem();
        }

        pve_id = this.addPredVE(pve);

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
    throws SystemErrorException, LogicErrorException {
        long pve_id = DBIndex.INVALID_ID;
        PredicateVocabElement pve = null;

        pve = constructPVE(name, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
        pve.setVarLen(vLen);
        if ( system )
        {
            pve.setSystem();
        }

        pve_id = this.addPredVE(pve);

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
     *                                              JRM -- 7/20/08
     *
     * Changes:
     *
     *    - None.
     */

    protected long defineSystemPVE(String name,
                                   String arg1,
                                   boolean vLen)
    throws SystemErrorException, LogicErrorException {
        return definePVE(name, arg1, vLen, true);
    }

    protected long defineSystemPVE(String name,
                                   String arg1,
                                   String arg2,
                                   boolean vLen)
    throws SystemErrorException, LogicErrorException {
        return definePVE(name, arg1, arg2, vLen, true);
    }

    protected long defineSystemPVE(String name,
                                   String arg1,
                                   String arg2,
                                   String arg3,
                                   boolean vLen)
    throws SystemErrorException, LogicErrorException {
        return definePVE(name, arg1, arg2, arg3, vLen, true);
    }

    protected long defineSystemPVE(String name,
                                   String arg1,
                                   String arg2,
                                   String arg3,
                                   String arg4,
                                   boolean vLen)
    throws SystemErrorException, LogicErrorException {
        return definePVE(name, arg1, arg2, arg3, arg4, vLen, true);
    }

    protected long defineSystemPVE(String name,
                                   String arg1,
                                   String arg2,
                                   String arg3,
                                   String arg4,
                                   String arg5,
                                   boolean vLen)
    throws SystemErrorException, LogicErrorException {
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
    throws SystemErrorException, LogicErrorException {
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
    throws SystemErrorException, LogicErrorException {
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
     *                                              JRM -- 7/20/08
     *
     * Changes:
     *
     *    - None.
     */

    protected long defineUserPVE(String name,
                                 String arg1,
                                 boolean vLen)
    throws SystemErrorException, LogicErrorException {
        return definePVE(name, arg1, vLen, false);
    }

    protected long defineUserPVE(String name,
                                 String arg1,
                                 String arg2,
                                 boolean vLen)
    throws SystemErrorException, LogicErrorException {
        return definePVE(name, arg1, arg2, vLen, false);
    }

    protected long defineUserPVE(String name,
                                 String arg1,
                                 String arg2,
                                 String arg3,
                                 boolean vLen)
    throws SystemErrorException, LogicErrorException {
        return definePVE(name, arg1, arg2, arg3, vLen, false);
    }

    protected long defineUserPVE(String name,
                                 String arg1,
                                 String arg2,
                                 String arg3,
                                 String arg4,
                                 boolean vLen)
    throws SystemErrorException, LogicErrorException {
        return definePVE(name, arg1, arg2, arg3, arg4, vLen, false);
    }

    protected long defineUserPVE(String name,
                                 String arg1,
                                 String arg2,
                                 String arg3,
                                 String arg4,
                                 String arg5,
                                 boolean vLen)
    throws SystemErrorException, LogicErrorException {
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
    throws SystemErrorException, LogicErrorException {
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
    throws SystemErrorException, LogicErrorException {
        return definePVE(name, arg1, arg2, arg3, arg4, arg5, arg6, arg7, vLen, false);
    }

    /**
     * setupSystemPreds()
     *
     * Create the system predicates that exist in every MacSHAPA database.
     *
     *                                              JRM -- 7/19/08
     * Changes:
     *
     *    - None
     */

    private void setupSystemPreds()
    throws SystemErrorException, LogicErrorException {
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

}
