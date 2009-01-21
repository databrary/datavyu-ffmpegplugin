package au.com.nicta.openshapa.db;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for the ColPred class.
 *
 * @author cfreeman
 */
public class ColPredTest {

    Database db;
    long floatMveId;
    long intMveId;

    public ColPredTest() {
    }

    @Before
    public void setUp() throws SystemErrorException {
        db = new ODBCDatabase();
        
        MatrixVocabElement floatMVE = new MatrixVocabElement(db, "floatMVE");
        floatMVE.setType(MatrixVocabElement.MatrixType.FLOAT);
        FloatFormalArg farg = new FloatFormalArg(db);
        floatMVE.appendFormalArg(farg);
        db.vl.addElement(floatMVE);
        floatMveId = floatMVE.getID();

        MatrixVocabElement intMVE = new MatrixVocabElement(db, "intMVE");
        intMVE.setType(MatrixVocabElement.MatrixType.INTEGER);
        IntFormalArg iarg = new IntFormalArg(db);
        intMVE.appendFormalArg(iarg);
        db.vl.addElement(intMVE);
        intMveId = intMVE.getID();
    }

    /**
     * Test of clone method, of class ColPred.
     */
    @Test
    public void testClone()
    throws SystemErrorException, CloneNotSupportedException {
        ColPred value0 = new ColPred(db, floatMveId);
        ColPred value1 = (ColPred) value0.clone();

        assertEquals(value0, value1);
    }

    /**
     * Test of hashCode method, of class ColPred.
     */
    @Test
    public void testHashCode() throws SystemErrorException {
        ColPred value0 = new ColPred(db, floatMveId);
        ColPred value1 = new ColPred(db, floatMveId);
        ColPred value2 = new ColPred(db, intMveId);

        // Hashcode
        assertTrue(value0.hashCode() == value1.hashCode());
        assertTrue(value0.hashCode() != value2.hashCode());
    }

    /**
     * Test of equals method, of class ColPred.
     */
    @Test
    public void testEquals() throws SystemErrorException {
        ColPred value0 = new ColPred(db, floatMveId);
        ColPred value1 = new ColPred(db, floatMveId);
        ColPred value2 = new ColPred(db, floatMveId);
        ColPred value3 = new ColPred(db, intMveId);

        // Reflexive
        assertTrue(value0.equals(value0));

        // Symmetric
        assertTrue(value0.equals(value1));
        assertTrue(value1.equals(value0));

        // Transitive
        assertTrue(value0.equals(value1));
        assertTrue(value0.equals(value2));
        assertTrue(value1.equals(value2));
        // Consistent not tested

        // Null
        assertFalse(value0.equals(null));        

        // Not equals tests
        assertFalse(value0.equals(value3));
    }
}