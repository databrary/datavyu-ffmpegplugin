package au.com.nicta.openshapa.db;

import junitx.util.PrivateAccessor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cfreeman
 */
public class UndefinedDataValueTest extends DataValueTest {

    /** Database for tests. */
    private Database db;
    /** UndefinedDatavalue to use for tests. */
    private UndefinedDataValue uDataValue;

    @Override
    public DataValue getInstance() {
        return uDataValue;
    }

    public UndefinedDataValueTest() {
    }

    @Before
    public void setUp() throws SystemErrorException {
        db = new ODBCDatabase();
        uDataValue =  new UndefinedDataValue(db);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of updateForFargChange method, of class IntDataValue.
     */
    @Test
    @Override
    public void testUpdateForFargChange() throws Exception {
    }

    /**
     * Test of updateSubRange method, of class IntDataValue.
     */
    @Test
    @Override
    public void testUpdateSubRange() throws Exception {
    }

    /**
     * Test of getItsValue method, of class UndefinedDataValue.
     */
    @Test
    public void testGetItsValue() throws SystemErrorException {        
        assertEquals(uDataValue.getItsValue(), "<val>");        
    }

    /**
     * Test of setItsValue method, of class UndefinedDataValue.
     */
    @Test
    public void testSetItsValue() throws Exception {
        uDataValue.setItsValue("<moo>");        
        String itsValue = (String) PrivateAccessor.getField(uDataValue,
                                                            "itsValue");
        assertEquals(itsValue, "<moo>");
    }

    /**
     * Test of toString method, of class UndefinedDataValue.
     */
    @Test
    public void testToString() throws SystemErrorException {
        assertEquals(uDataValue.toString(), "<val>");
    }

    /**
     * Test of toDBString method, of class UndefinedDataValue.
     */
    @Test
    public void testToDBString() {
        System.out.println(uDataValue.toDBString());
        assertEquals(uDataValue.toDBString(),
                     "(UndefinedDataValue (id 0) " +
                        "(itsFargID 0) " +
                        "(itsFargType UNDEFINED) " +
                        "(itsCellID 0) " +
                        "(itsValue <val>) " +
                        "(subRange false))");
    }

    /**
     * Test of coerceToRange method, of class UndefinedDataValue.
     */
    @Test
    public void testCoerceToRange() throws Exception {
        uDataValue.setItsValue("<moo>");
        assertEquals(uDataValue.coerceToRange("<oink>"), "<oink>");
        System.out.println(uDataValue.getItsValue());
    }

    /**
     * Test of Construct method, of class UndefinedDataValue.
     */
    @Test
    public void testConstruct() throws Exception {
        UndefinedDataValue uValue = UndefinedDataValue.Construct(db);
        assertEquals(uValue, uDataValue);
    }

    @Test
    public void testHashCode()
    throws SystemErrorException, CloneNotSupportedException {
        uDataValue.setItsValue("<oink>");
        UndefinedDataValue uDifferent = new UndefinedDataValue(db);
        UndefinedDataValue uCopy = (UndefinedDataValue) uDataValue.clone();

        super.testHashCode(uDataValue, uCopy, uDifferent);
    }

    /**
     * Tests the equals method of an undefined data value.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException If Unable to
     * create or manipulate undefined data values.
     * @throws java.lang.CloneNotSupportedException If the clone method is not
     * implemented for the undefined data value.
     */
    @Test
    public void testEquals()
    throws SystemErrorException, CloneNotSupportedException {
        uDataValue.setItsValue("<oink>");
            UndefinedDataValue uValue = new UndefinedDataValue(db);
        uValue.setItsValue("<oink>");
        UndefinedDataValue uDifferent = new UndefinedDataValue(db);
        UndefinedDataValue uCopy = (UndefinedDataValue) uDataValue.clone();

        super.testEquals(uDataValue, uValue, uCopy, uDifferent);
    }

    /*************************************************************************/
    /**************************** Test Code: *********************************/
    /*************************************************************************/

    // TODO: Write test suite for undefined data values.

    /**
     * VerifyUndefinedDVCopy()
     *
     * Verify that the supplied instances of UndefinedDataValue are distinct,
     * that they contain no common references (other than db), and that they
     * have the same value.
     *                                              JRM -- 11/8/07
     *
     * Changes:
     *
     *    - None
     */
    
    public static int VerifyUndefinedDVCopy(UndefinedDataValue base,
                                            UndefinedDataValue copy,
                                            java.io.PrintStream outStream,
                                            boolean verbose,
                                            String baseDesc,
                                            String copyDesc)
    {
        int failures = 0;

        if ( base == null )
        {
            failures++;
            outStream.printf("VerifyUndefinedDVCopy: %s null on entry.\n",
                             baseDesc);
        }
        else if ( copy == null )
        {
            failures++;
            outStream.printf("VerifyUndefinedDVCopy: %s null on entry.\n",
                             copyDesc);
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
        else if ( ( base.itsValue == copy.itsValue ) &&
                  ( base.itsValue != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s and %s share a string.\n",
                                  baseDesc, copyDesc);
            }
        }
        else if ( ( base.itsValue == copy.itsValue ) &&
                  ( base.itsValue != null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.itsValue is null, and %s.itsValue isn't.\n",
                        baseDesc, copyDesc);
            }
        }
        else if ( ( base.itsValue != copy.itsValue ) &&
                  ( copy.itsValue == null ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf(
                        "%s.itsValue is null, and %s.itsValue isn't.\n",
                        copyDesc, baseDesc);
            }
        }
        else if ( ( base.itsValue != copy.itsValue ) &&
                  ( base.itsValue.compareTo(copy.itsValue) != 0 ) )
        {
            failures++;

            if ( verbose )
            {
                outStream.printf("%s and %s contain different values.\n",
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
            }
        }

        return failures;

    } /* UndefinedDataValue::VerifyUndefinedDVCopy() */

}