package org.openshapa.models.db.legacy;

import org.openshapa.models.db.legacy.Database;
import org.openshapa.models.db.legacy.ODBCDatabase;
import org.openshapa.models.db.legacy.SystemErrorException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

/**
 *
 */
public class ODBCDatabaseTest extends DatabaseTest {

    public ODBCDatabase db;

    @Override
    public Database getInstance() {
        return db;
    }

    public ODBCDatabaseTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUp() throws SystemErrorException {
        db = new ODBCDatabase();
    }

    @AfterMethod
    public void tearDown() {
        db = null;
    }

    /**
     * Test of getType method, of class ODBCDatabase.
     */
    @Test
    @Override
    public void testGetType() {
        assertTrue(db.getType().length() != 0);
    }

    /**
     * Test of getVersion method, of class ODBCDatabase.
     */
    @Test
    public void testGetVersion() {
        assertTrue(db.getVersion() > 0.0);
    }


}