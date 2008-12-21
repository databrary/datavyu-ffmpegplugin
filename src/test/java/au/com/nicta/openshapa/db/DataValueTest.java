package au.com.nicta.openshapa.db;

import au.com.nicta.openshapa.db.FormalArgument.fArgType;
import java.io.PrintStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cfreeman
 */
public abstract class DataValueTest {

    public abstract DataValue getInstance();

    public DataValueTest() {
    }

    /**
     * Test of updateForFargChange method, of class DataValue.
     */
    @Test
    public void testUpdateForFargChange() throws Exception {
        System.out.println("updateForFargChange");
        boolean fargNameChanged = false;
        boolean fargSubRangeChanged = false;
        boolean fargRangeChanged = false;
        FormalArgument oldFA = null;
        FormalArgument newFA = null;
        DataValue instance = null;
        instance.updateForFargChange(fargNameChanged, fargSubRangeChanged, fargRangeChanged, oldFA, newFA);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of updateSubRange method, of class DataValue.
     */
    @Test
    public void testUpdateSubRange() throws Exception {
        System.out.println("updateSubRange");
        FormalArgument fa = null;
        DataValue instance = null;
        instance.updateSubRange(fa);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

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
        assertEquals(instance.getItsFargType(), fArgType.UNDEFINED);
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
    @Test
    public void testReplaceInIndex() throws Exception {
        System.out.println("replaceInIndex");
        DataValue old_dv = null;
        long DCID = 0L;
        boolean cascadeMveMod = false;
        boolean cascadeMveDel = false;
        long cascadeMveID = 0L;
        boolean cascadePveMod = false;
        boolean cascadePveDel = false;
        long cascadePveID = 0L;
        DataValue instance = null;
        instance.replaceInIndex(old_dv, DCID, cascadeMveMod, cascadeMveDel, cascadeMveID, cascadePveMod, cascadePveDel, cascadePveID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of hashCode method, of class DataValue.
     */
    @Test
    public void testHashCode() {
        System.out.println("hashCode");
        DataValue instance = null;
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class DataValue.
     */
    @Test
    public void testEquals()
    throws SystemErrorException, CloneNotSupportedException {
        System.out.println("equals");
        Object obj = null;
        DataValue instance = null;
        boolean expResult = false;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}