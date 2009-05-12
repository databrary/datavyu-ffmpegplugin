package org.openshapa.db;

import junitx.util.PrivateAccessor;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Abstract test class for the abstract DBElement class.
 */
public abstract class DBElementTest {

    /**
     * All child test classes should implement this call back method to return
     * a valid DBElement that can be tested.
     *
     * @return A valid DBElement.
     */
    public abstract DBElement getInstance();

    /**
     * Test of clone method, of class DBElement.
     *
     * @throws CloneNotSupportedException if unable to clone the DBElement.
     */
    @Test
    public final void testClone() throws CloneNotSupportedException {
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
    public final void testHashCode(final DBElement a,
                                   final DBElement b,
                                   final DBElement c) {
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
    public final void testEquals(final DBElement a,
                                 final DBElement b,
                                 final DBElement c,
                                 final DBElement d) {
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

    /**
     * Resets the id field to its initial value DBIndex.INVALID_ID.
     *
     * @param  dbe reference to the instance of DBElement whose id field is
     * to be reset.
     *
     * @return The current id used by this DBElement.
     */
    protected static long ResetID(DBElement dbe) {
        long old_id = DBIndex.INVALID_ID;
        try {
            Long oldid = (Long) PrivateAccessor.getField(dbe, "id");
            old_id = oldid;
            Long invID = new Long(DBIndex.INVALID_ID);
            PrivateAccessor.setField(dbe, "id", invID);
        } catch (Throwable th) {
            fail("Problem in ResetID PrivateAccessor calls.");
        }

        return old_id;
    }


}