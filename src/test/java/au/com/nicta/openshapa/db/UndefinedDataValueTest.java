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
     * Test of updateForFargChange method, of class UndefinedDataValue.
     */
    @Test
    public void testUpdateForFargChange() throws Exception {
        System.out.println("updateForFargChange");
        boolean fargNameChanged = false;
        boolean fargSubRangeChanged = false;
        boolean fargRangeChanged = false;
        FormalArgument oldFA = null;
        FormalArgument newFA = null;
        UndefinedDataValue instance = null;
        instance.updateForFargChange(fargNameChanged, fargSubRangeChanged, fargRangeChanged, oldFA, newFA);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateSubRange method, of class UndefinedDataValue.
     */
    @Test
    public void testUpdateSubRange() throws Exception {
        System.out.println("updateSubRange");
        FormalArgument fa = null;
        UndefinedDataValue instance = null;
        instance.updateSubRange(fa);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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

    /**
     * Tests the implementation of the cloneable interface for the undefined
     * data value.
     *
     * @throws au.com.nicta.openshapa.db.SystemErrorException If unable to
     * create or manipulate undefined data values.
     * @throws java.lang.CloneNotSupportedException If the clone method is not
     * implemented for the undefined data value.
     */
    @Test
    public void testClone()
    throws SystemErrorException, CloneNotSupportedException {
        uDataValue.setItsValue("<oink>");
        UndefinedDataValue uCopy = (UndefinedDataValue) uDataValue.clone();

        assertEquals(uDataValue, uCopy);
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
    @Override
    public void testEquals()
    throws SystemErrorException, CloneNotSupportedException {
        super.testEquals();
        uDataValue.setItsValue("<oink>");
        UndefinedDataValue uValue = new UndefinedDataValue(db);
        uValue.setItsValue("<oink>");
        UndefinedDataValue uDifferent = new UndefinedDataValue(db);
        UndefinedDataValue uCopy = (UndefinedDataValue) uDataValue.clone();

        // Reflexive
        assertTrue(uDataValue.equals(uCopy));

        // Symmetric
        assertTrue(uCopy.equals(uDataValue));

        // Transitive
        assertTrue(uDataValue.equals(uValue));
        assertTrue(uValue.equals(uCopy));
        assertTrue(uCopy.equals(uDataValue));
        // Consistent not tested

        // Null
        assertFalse(uDataValue.equals(null));
        // Hashcode

        assertTrue(uDataValue.hashCode() == uCopy.hashCode());

        // Not equals tests
        assertFalse(uDataValue.equals(uDifferent));
        assertTrue(uDataValue.hashCode() != uDifferent.hashCode());

        // modify uDataValue
        uDataValue.setItsValue("<val>");
        assertTrue(uDataValue.equals(uDifferent));
        assertTrue(uDataValue.hashCode() == uDifferent.hashCode());
    }
}