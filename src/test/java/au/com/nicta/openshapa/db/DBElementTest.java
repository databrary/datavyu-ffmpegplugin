package au.com.nicta.openshapa.db;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Abstract test class for the abstract DBElement class.
 *
 * @author cfreeman
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
}