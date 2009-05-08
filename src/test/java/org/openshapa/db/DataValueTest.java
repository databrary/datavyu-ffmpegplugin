package org.openshapa.db;

import org.openshapa.db.FormalArgument.FArgType;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cfreeman
 */
public abstract class DataValueTest extends DBElementTest {

    public abstract DataValue getInstance();

    public DataValueTest() {
    }

    /**
     * Test of updateForFargChange method, of class DataValue.
     */
    @Test
    public abstract void testUpdateForFargChange() throws Exception;

    /**
     * Test of updateSubRange method, of class DataValue.
     */
    @Test
    public abstract void testUpdateSubRange() throws Exception;

    /**
     * Test of getItsFargID method, of class DataValue.
     */
    @Test
    public void testGetItsFargID() {
        DataValue instance = getInstance();
        assertEquals(instance.getItsFargID(), 0);
    }

    /**
     * Test of getItsFargType method, of class DataValue.
     */
    @Test
    public void testGetItsFargType() {
        DataValue instance = getInstance();
        assertEquals(instance.getItsFargType(), FArgType.UNDEFINED);
    }

    /**
     * Test of getSubRange method, of class DataValue.
     */
    @Test
    public void testGetSubRange() {
        DataValue instance = getInstance();
        assertFalse(instance.getSubRange());
    }

    /**
     * Test of setItsCellID method, of class DataValue.
     */
    @Test (expected = SystemErrorException.class)
    public void testSetItsCellID() throws Exception {
        DataValue instance = getInstance();
        final long ID = 5;
        instance.setItsCellID(ID);
    }

    /**
     * Test of setItsFargID method, of class DataValue.
     */
    @Test (expected = SystemErrorException.class)
    public void testSetItsFargID() throws Exception {
        DataValue instance = getInstance();
        instance.setItsFargID(DBIndex.INVALID_ID);
    }

    /**
     * Test of setItsPredID method, of class DataValue.
     */
    @Test (expected = SystemErrorException.class)
    public void testSetItsPredID() throws Exception {
        DataValue instance = getInstance();
        instance.setItsPredID(DBIndex.INVALID_ID);
    }

    /**
     * Test of insertInIndex method, of class DataValue.
     */
    @Test (expected = SystemErrorException.class)
    public void testInsertInIndex() throws Exception {
        DataValue instance = getInstance();
        final long ID = 5;
        instance.insertInIndex(ID);
    }

    /**
     * Test of removeFromIndex method, of class DataValue.
     */
    @Test (expected = SystemErrorException.class)
    public void testRemoveFromIndex() throws Exception {
        DataValue instance = getInstance();
        final long ID = 5;
        instance.removeFromIndex(ID);
    }

    /**
     * Test of replaceInIndex method, of class DataValue.
     */
    @Test (expected = SystemErrorException.class)
    public void testReplaceInIndex() throws Exception {
        DataValue original = getInstance();
        original.replaceInIndex(original, 5, false, false, 5, false, false, 5);
    }

    /**
     * Verify that the supplied instance of DataValue has been correctly
     * initialized by a one argument constructor.
     *
     * @param db Database
     * @param dv DataValue
     */
    static void verify1ArgInitialization(final Database db,
                                                final DataValue dv) {
        assertNotNull(db);
        assertNotNull(dv);
        assertEquals(dv.getDB(), db);
        assertEquals(dv.getDB(), db);
        assertEquals(dv.getID(), DBIndex.INVALID_ID);
        assertEquals(dv.itsCellID, DBIndex.INVALID_ID);
        assertEquals(dv.itsFargID, DBIndex.INVALID_ID);
        assertEquals(dv.itsFargType, FormalArgument.FArgType.UNDEFINED);
        assertEquals(dv.getLastModUID(), DBIndex.INVALID_ID);
        assertFalse(dv.subRange);
    }

    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/

    /**
     * TestAccessors()
     *
     * Verify that the accessors defined in this abstract class perform
     * as expected in the supplied instance of some subclass.
     *
     * The test assumes that:
     *
     * 1) The itsFargID field has been set to match the id of fa0.
     *
     * 2) The itsCellID field has not been assigned.
     *
     * 3) fa0 and fa1 are of different types, and that dv is compatible
     *    with both. (In practice, this means that fa0 is typed to accept
     *    DataValues of type equal to that of dv, and that fa1 is an untyped
     *    formal argument, or vise versa.)
     *
     *    Note that there is one exception to this assumption -- if dv is a
     *    TextStringDataValue, then both fa0 and fa1 must be instances of
     *    TextStringFormalArg
     *
     * 4) mve1 is a single element matrix vocab element, and fa1 is its single
     *    formal argument.  If dv is an instance of TextStringDataValue,
     *    mve1 must be of type TEXT.  Otherwise, mve1 must be either of
     *    of type MATRIX or of type matching dv.
     *
     *                                          JRM -- 11/14/07
     *
     * Changes:
     *
     *    - None.
     */
    
    public static int TestAccessors(Database db,
                                    FormalArgument fa0,
                                    MatrixVocabElement mve1,
                                    FormalArgument fa1,
                                    DataValue dv,
                                    java.io.PrintStream outStream,
                                    boolean verbose)
        throws SystemErrorException
    {
        String systemErrorExceptionString = null;
        boolean completed = false;
        boolean threwSystemErrorException = false;
        int failures = 0;
        long cellID = DBIndex.INVALID_ID;
        DataColumn col = null;
        DataCell dc = null;

        if ( db == null )
        {
            failures++;
            outStream.printf("TestAccessors: db null on entry.\n");
        }

        if ( fa0 == null )
        {
            failures++;
            outStream.printf("TestAccessors: fa0 null on entry.\n");
        }

        if ( fa1 == null )
        {
            failures++;
            outStream.printf("TestAccessors: fa1 null on entry.\n");
        }

        if ( dv == null )
        {
            failures++;
            outStream.printf("TestAccessors: dv null on entry.\n");
        }

        if ( dv.getItsFargID() != fa0.getID() )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("dv.getItsFargID() != fa0.getID().\n");
            }
        }

        if ( dv.getItsFargType() != fa0.getFargType() )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("dv.getItsFargType() != fa0.getFargType().\n");
            }
        }

        completed = false;
        threwSystemErrorException = false;
        try
        {
            dv.setItsFargID(fa1.getID());

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( ! completed )
                {
                    outStream.printf(
                            "dv.setItsFargID() failed to completed.\n");
                }
            }

            if ( threwSystemErrorException )
            {
                outStream.printf("dv.setItsFargID() threw a system error " +
                                 "exception: \"%s\"",
                                 systemErrorExceptionString);
            }
        }
        else
        {
            if ( dv.getItsFargID() != fa1.getID() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("dv.getItsFargID() != fa1.getID().\n");
                }
            }

            if ( dv.getItsFargType() != fa1.getFargType() )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf(
                            "dv.getItsFargType() != fa1.getFargType().\n");
                }
            }
        }

        if ( dv.itsCellID != DBIndex.INVALID_ID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("dv.itsCellID != DBIndex.INVALID_ID.\n");
            }
        }

        completed = false;
        threwSystemErrorException = false;
        try
        {
            // it is possible this method has been called before with the
            // supplied mve -- if so, we have already created a column for
            // it, and trying to create it again will throw a system error.
            if ( db.cl.inColumnList(mve1.getName()) )
            {
                col = db.getDataColumn(mve1.getName());
            }
            else
            {
                col = new DataColumn(db, mve1.getName(), false, false, mve1.getID());

                db.cl.addColumn(col);
            }

            dc = new DataCell(db, col.getID(), mve1.getID());

            cellID = db.appendCell(dc);

            dv.setItsCellID(cellID);

            completed = true;
        }

        catch (SystemErrorException e)
        {
            threwSystemErrorException = true;
            systemErrorExceptionString = e.getMessage();
        }

        if ( ( col == null ) ||
             ( dc == null ) ||
             ( cellID == DBIndex.INVALID_ID ) ||
             ( ! completed ) ||
             ( threwSystemErrorException ) )
        {
            failures++;

            if ( verbose )
            {
                if ( col == null )
                {
                    outStream.printf("DataColumn allocation failed.\n");
                }

                if ( dc == null )
                {
                    outStream.printf("DataCell allocation failed.\n");
                }

                if ( cellID == DBIndex.INVALID_ID )
                {
                    outStream.printf("*cellID is INVALID.\n");
                }

                if ( ! completed )
                {
                    outStream.printf(
                            "setItsCellID() test failed to complete.\n");
                }

                if ( threwSystemErrorException )
                {
                    outStream.printf("setItsCellID() test threw a system " +
                                     "error exception: \"%s\"",
                                     systemErrorExceptionString);
                }
            }
        }
        else
        {
            if ( dv.itsCellID != cellID )
            {
                failures++;

                if ( verbose )
                {
                    outStream.printf("dv.itsCellID != cellID.\n");
                }
            }
        }

        return failures;

    } /* DataValue::TestAccessors() */


    /**
     * Verify1ArgInitialization()
     *
     * Verify that the supplied instance of DataValue has been correctly
     * initialized by a one argument constructor.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None
     */

    public static int Verify1ArgInitialization(Database db,
                                               DataValue dv,
                                               java.io.PrintStream outStream,
                                               boolean verbose)
    {
        int failures = 0;

        if ( db == null )
        {
            failures++;
            outStream.printf("Verify1ArgInitialization: db null on entry.\n");
        }

        if ( dv == null )
        {
            failures++;
            outStream.printf("Verify1ArgInitialization: dv null on entry.\n");
        }

        if ( dv.getDB() != db )
        {
            failures++;

            if ( verbose )
            {
                outStream.print("dv.db not initialized correctly.\n");
            }
        }

        if ( dv.getID() != DBIndex.INVALID_ID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("dv.id not initialized corectly: %d.\n",
                                 dv.getID());
            }
        }

        if ( dv.itsCellID != DBIndex.INVALID_ID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "dv.itsCellID not initialized corectly: %d.\n",
                        dv.itsCellID);
            }
        }

        if ( dv.itsFargID != DBIndex.INVALID_ID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "dv.itsFargID not initialized correctly: %d.\n",
                        dv.itsFargID);
            }
        }

        if ( dv.itsFargType != FormalArgument.FArgType.UNDEFINED )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "dv.itsFargType not initialized correctly.\n");
            }
        }

        if ( dv.getLastModUID() != DBIndex.INVALID_ID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "dv.lastModUID not initialized correctly: %d.\n",
                        dv.getLastModUID());
            }
        }

        if ( dv.subRange )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("dv.subRange not set to false.\n");
            }
        }

        return failures;

    } /* DataValue::Verify1ArgInitialization() */


    /**
     * Verify2ArgInitialization()
     *
     * Verify that the supplied instance of DataValue has been correctly
     * initialized by a two or more argument constructor.
     *
     *                                              JRM -- 11/13/07
     *
     * Changes:
     *
     *    - None
     */

    public static int Verify2PlusArgInitialization(Database db,
                                                   FormalArgument fa,
                                                   DataValue dv,
                                                   java.io.PrintStream outStream,
                                                   boolean verbose,
                                                   String dvDesc)
        throws SystemErrorException
    {
        int failures = 0;

        if ( dv == null )
        {
            failures++;
            outStream.printf(
                    "Verify2PlusArgInitialization: dv null on entry.\n");
        }

        if ( fa == null )
        {
            failures++;
            outStream.printf(
                    "Verify2PlusArgInitialization: fa null on entry.\n");
        }
        else if ( fa.getID() == DBIndex.INVALID_ID )
        {
            failures++;
            outStream.printf("Verify2PlusArgInitialization: fa.getID() " +
                             "returns invalid ID.\n");
        }

        if ( dv.getDB() != db )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.db not initialized correctly.\n", dvDesc);
            }
        }

        if ( dv.getID() != DBIndex.INVALID_ID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.id not initialized corectly: %d.\n",
                                 dvDesc, dv.getID());
            }
        }

        if ( dv.itsCellID != DBIndex.INVALID_ID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.itsCellID not initialized corectly: %d.\n",
                        dvDesc, dv.itsCellID);
            }
        }

        if ( dv.itsFargID != fa.getID() )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.itsFargID not initialized correctly: %d(%d).\n",
                        dvDesc, dv.itsFargID, fa.getID());
            }
        }

        if ( dv.itsFargType != fa.getFargType() )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.itsFargType not initialized correctly.\n",
                                 dvDesc);
            }
        }

        if ( dv.getLastModUID() != DBIndex.INVALID_ID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.lastModUID not initialized correctly: %d.\n",
                        dvDesc, dv.getLastModUID());
            }
        }

        return failures;

    } /* DataValue::Verify2ArgInitialization() */


    /**
     * VerifyDVCopy()
     *
     * Verify that the supplied instances of DataValue are distinct, that they
     * contain no common references (other than db), and that they have the
     * same value.
     *
     * Note that we do not compare the itsCell and itsCellID.
     *
     *                                              JRM -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */

    protected static int VerifyDVCopy(DataValue base,
                                      DataValue copy,
                                      java.io.PrintStream outStream,
                                      boolean verbose,
                                      String baseDesc,
                                      String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyDVCopy: %s null on entry.\n", baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyDVCopy: %s null on entry.\n", copyDesc);
        }
        else if ( base == copy )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s == %s.\n", baseDesc, copyDesc);
            }
        }
        else if ( base.getDB() != copy.getDB() )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.db != %s.db.\n", baseDesc, copyDesc);
            }
        }
        else if ( base.itsFargID != copy.itsFargID )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.itsFargID != %s.itsFargID.\n",
                                 baseDesc, copyDesc);
            }
        }
        else if ( base.itsFargType != copy.itsFargType )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.itsFargType != %s.itsFargType.\n",
                                 baseDesc, copyDesc);
            }
        }
        else if ( base.subRange != copy.subRange )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.itsFargType != %s.itsFargType.\n",
                                 baseDesc, copyDesc);
            }
        }
        else if ( base.toString().compareTo(copy.toString()) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s.toString() doesn't match %s.toString().\n",
                                 baseDesc, copyDesc);
            }
        }
        else if ( base.toDBString().compareTo(copy.toDBString()) != 0 )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.toDBString() doesn't match %s.toDBString().\n",
                        baseDesc, copyDesc);
                outStream.printf(
                        "%s.toDBString() = \"%s\".\n%s.toDBString() = \"%s\".\n",
                        baseDesc, base.toDBString(),
                        copyDesc, copy.toDBString());
            }
        }
        else
        {
            if ( base instanceof ColPredDataValue )
            {
                if ( ! ( copy instanceof ColPredDataValue ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "%s is a ColPredDataValue but %s is not.\n",
                                baseDesc, copyDesc);
                    }
                }
                else
                {
                    failures +=
                        ColPredDataValueTest.
                            VerifyColPredDVCopy((ColPredDataValue)base,
                                                (ColPredDataValue)copy,
                                                outStream,
                                                verbose,
                                                baseDesc,
                                                copyDesc);
                }
            }
            else if ( base instanceof FloatDataValue )
            {
                if ( ! ( copy instanceof FloatDataValue ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "%s is a FloatDataValue but %s is not.\n",
                                baseDesc, copyDesc);
                    }
                }
                else
                {
                    failures +=
                        FloatDataValueTest.VerifyFloatDVCopy((FloatDataValue)base,
                                                         (FloatDataValue)copy,
                                                         outStream,
                                                         verbose,
                                                         baseDesc,
                                                         copyDesc);
                }
            }
            else if ( base instanceof IntDataValue )
            {
                if ( ! ( copy instanceof IntDataValue ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "%s is a IntDataValue but %s is not.\n",
                                baseDesc, copyDesc);
                    }
                }
                else
                {
                    failures +=
                        IntDataValueTest.VerifyIntDVCopy((IntDataValue)base,
                                                     (IntDataValue)copy,
                                                     outStream,
                                                     verbose,
                                                     baseDesc,
                                                     copyDesc);
                }
            }
            else if ( base instanceof NominalDataValue )
            {
                if ( ! ( copy instanceof NominalDataValue ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "%s is a NominalDataValue but %s is not.\n",
                                baseDesc, copyDesc);
                    }
                }
                else
                {
                    failures +=
                        NominalDataValueTest.
                            VerifyNominalDVCopy((NominalDataValue)base,
                                                (NominalDataValue)copy,
                                                outStream,
                                                verbose,
                                                baseDesc,
                                                copyDesc);
                }
            }
            else if ( base instanceof PredDataValue )
            {
                if ( ! ( copy instanceof PredDataValue ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "%s is a PredDataValue but %s is not.\n",
                                baseDesc, copyDesc);
                    }
                }
                else
                {
                    failures +=
                        PredDataValueTest.
                            VerifyPredDVCopy((PredDataValue)base,
                                             (PredDataValue)copy,
                                             outStream,
                                             verbose,
                                             baseDesc,
                                             copyDesc);
                }
            }
            else if ( base instanceof QuoteStringDataValue )
            {
                if ( ! ( copy instanceof QuoteStringDataValue ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "%s is a QuoteStringDataValue but %s is not.\n",
                                baseDesc, copyDesc);
                    }
                }
                else
                {
                    failures +=
                        QuoteStringDataValueTest.
                            VerifyQuoteStringDVCopy((QuoteStringDataValue)base,
                                                    (QuoteStringDataValue)copy,
                                                    outStream,
                                                    verbose,
                                                    baseDesc,
                                                    copyDesc);
                }
            }
            else if ( base instanceof TextStringDataValue )
            {
                if ( ! ( copy instanceof TextStringDataValue ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "%s is a TextStringDataValue but %s is not.\n",
                                baseDesc, copyDesc);
                    }
                }
                else
                {
                    failures +=
                        TextStringDataValueTest.
                            VerifyTextStringDVCopy((TextStringDataValue)base,
                                                   (TextStringDataValue)copy,
                                                   outStream,
                                                   verbose,
                                                   baseDesc,
                                                   copyDesc);
                }
            }
            else if ( base instanceof TimeStampDataValue )
            {
                if ( ! ( copy instanceof TimeStampDataValue ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "%s is a TimeStampDataValue but %s is not.\n",
                                baseDesc, copyDesc);
                    }
                }
                else
                {
                    failures +=
                        TimeStampDataValueTest.
                            VerifyTimeStampDVCopy((TimeStampDataValue)base,
                                                  (TimeStampDataValue)copy,
                                                  outStream,
                                                  verbose,
                                                  baseDesc,
                                                  copyDesc);
                }
            }
            else if ( base instanceof UndefinedDataValue )
            {
                if ( ! ( copy instanceof UndefinedDataValue ) )
                {
                    failures++;

                    if ( verbose )
                    {
                        outStream.printf(
                                "%s is a UndefinedDataValue but %s is not.\n",
                                baseDesc, copyDesc);
                    }
                }
                else
                {
                    failures +=
                        UndefinedDataValueTest.
                            VerifyUndefinedDVCopy((UndefinedDataValue)base,
                                                  (UndefinedDataValue)copy,
                                                  outStream,
                                                  verbose,
                                                  baseDesc,
                                                  copyDesc);
                }
            }
            else
            {
                failures++;
                outStream.printf("%s is a DataValue of unknown type. AAAA\n",
                                 baseDesc);
            }
        }

        return failures;

    } /* DataValue::VerifyDVCopy() */

}