package au.com.nicta.openshapa.db;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cfreeman
 */
public abstract class DBElementTest {

    public abstract DBElement getInstance();

    public DBElementTest() {
    }

    @Before
    public void setUp() throws SystemErrorException {
    }

    /**
     * Test of clearID method, of class DBElement.
     */
    @Test
    public void testClearID() throws Exception {
        /*System.out.println("clearID");
        DBElement instance = null;
        instance.clearID();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }

    /**
     * Test of getDB method, of class DBElement.
     */
    @Test
    public void testGetDB() {
        /*System.out.println("getDB");
        DBElement instance = null;
        Database expResult = null;
        Database result = instance.getDB();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }

    /**
     * Test of getID method, of class DBElement.
     */
    @Test
    public void testGetID() {
        /*System.out.println("getID");
        DBElement instance = null;
        long expResult = 0L;
        long result = instance.getID();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }

    /**
     * Test of getLastModUID method, of class DBElement.
     */
    @Test
    public void testGetLastModUID() {
        /*System.out.println("getLastModUID");
        DBElement instance = null;
        int expResult = 0;
        int result = instance.getLastModUID();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }

    /**
     * Test of setID method, of class DBElement.
     */
    @Test
    public void testSetID() throws Exception {
        /*System.out.println("setID");
        long id = 0L;
        DBElement instance = null;
        instance.setID(id);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }

    /**
     * Test of setLastModUID method, of class DBElement.
     */
    @Test
    public void testSetLastModUID_0args() throws Exception {
        /*System.out.println("setLastModUID");
        DBElement instance = null;
        instance.setLastModUID();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }

    /**
     * Test of setLastModUID method, of class DBElement.
     */
    @Test
    public void testSetLastModUID_int() throws Exception {
        /*System.out.println("setLastModUID");
        int uid = 0;
        DBElement instance = null;
        instance.setLastModUID(uid);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }

    /**
     * Test of ResetID method, of class DBElement.
     */
    @Test
    public void testResetID() {
        /*System.out.println("ResetID");
        DBElement dbe = null;
        long expResult = 0L;
        long result = DBElement.ResetID(dbe);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");*/
    }

    /**
     * Test of clone method, of class DBElement.
     */
    @Test
    public void testClone() throws CloneNotSupportedException {
        DBElement element = getInstance();
        DBElement clone = (DBElement) element.clone();

        assertEquals(element, clone);
    }

    /**
     * Test of hashCode method, of class DBElement.
     *
     * @param a This DBElement should be logically equals to b, but not
     * reference the same object.
     * @param b This DBElement should be logically equals to a, but not
     * reference the same object
     * @param c This DBElement should logically different from a and b.
     */
    public void testHashCode(DBElement a, DBElement b, DBElement c) {
        assertTrue(a.hashCode() == b.hashCode());
        assertTrue(a.hashCode() != c.hashCode());
    }

    /**
     * Test of equals method, of class DBElement.
     *
     * @param a This DBElement should be logically equals to b and c, but not
     * reference the same object.
     * @param b This DBElement should be logically equals to a and c, but not
     * reference the same object.
     * @param c This DBElement should be logically equals to a and b, but not
     * reference the same object.
     * @param d This DBElement should be logically different from a, b and c.
     */
    public void testEquals(DBElement a, DBElement b, DBElement c, DBElement d) {
        // Reflexive
        assertTrue(a.equals(a));

        // Symmetric
        assertTrue(a.equals(b));
        assertTrue(b.equals(a));

        // Transitive
        assertTrue(a.equals(b));
        assertTrue(b.equals(c));
        assertTrue(c.equals(a));
        // Consistent not tested

        // Null
        assertFalse(a.equals(null));

        // Not equals tests
        assertFalse(a.equals(d));
    }

}