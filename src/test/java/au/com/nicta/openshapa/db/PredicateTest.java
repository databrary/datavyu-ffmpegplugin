package au.com.nicta.openshapa.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Junit test cases for the class Predicate.
 *
 * @author cfreeman
 */
public class PredicateTest {

    /** Database for tests. */
    private Database db;

    /** id for Predicate vocab element. */
    private long pveID;

    public PredicateTest() {
    }

    @Before
    public void setUp() throws SystemErrorException {
        db = new ODBCDatabase();

        PredicateVocabElement pve0 = new PredicateVocabElement(db, "test0");
        pve0.appendFormalArg(new FloatFormalArg(db, "<float>"));
        pve0.appendFormalArg(new IntFormalArg(db, "<int>"));
        pve0.appendFormalArg(new NominalFormalArg(db, "<nominal>"));
        pve0.appendFormalArg(new PredFormalArg(db, "<pred>"));
        pve0.appendFormalArg(new QuoteStringFormalArg(db, "<qstring>"));
        pve0.appendFormalArg(new TimeStampFormalArg(db, "<timestamp>"));
        pve0.appendFormalArg(new UnTypedFormalArg(db, "<untyped>"));
        pveID = db.addPredVE(pve0);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of hashCode method, of class Predicate.
     */
    @Test
    public void testHashCode() throws SystemErrorException {
        Predicate p0 = new Predicate(db);
        Predicate p1 = new Predicate(db);
        Predicate p2 = new Predicate(db, pveID);

        assertTrue(p0.hashCode() == p1.hashCode());
        assertTrue(p1.hashCode() != p2.hashCode());
    }

    /**
     * Test of equals method, of class Predicate.
     */
    @Test
    public void testEquals() throws SystemErrorException {
        Predicate p0 = new Predicate(db);
        Predicate p1 = new Predicate(db);
        
        Predicate p2 = new Predicate(db, pveID);
        Predicate p3 = new Predicate(db, pveID);
        Predicate p4 = new Predicate(db, pveID);

        // Reflexive
        assertTrue(p0.equals(p1));
        assertTrue(p2.equals(p3));

        // Symmetric
        assertTrue(p2.equals(p3));
        assertTrue(p3.equals(p2));
        assertTrue(p1.equals(p0));

        // Transitive
        assertTrue(p2.equals(p3));
        assertTrue(p3.equals(p4));
        assertTrue(p4.equals(p2));

        // Not equals
        assertFalse(p2.equals(null));
        assertFalse(p0.equals(p2));
    }
}