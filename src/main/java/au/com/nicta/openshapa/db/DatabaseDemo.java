package au.com.nicta.openshapa.db;

import java.util.Vector;

/**
 * Add demo data to the current database.
 *
 * @author swhitcher
 */
public class DatabaseDemo {

    /** The default number of ticks per second to use. */
    private static final int TICKS_PER_SECOND = 1000;

    /** Private constructor for utility class. */
    private DatabaseDemo() {
    }

    /**
     * Populates db with demo data items.
     * Doesn't gracefully recover if any errors encountered. Data input
     * just stops.
     *
     * TODO - null values in Predicates and Matrices should display args.
     * TODO - Partition demo better to show making columns then finding and
     * adding data to those columns.
     * TODO - add try catches around the relevant points to recover gracefully
     * in various situations. e.g. Column already exists with that name.
     *
     * @param db The database to populate.
     * @throws SystemErrorException if trouble.
     */
    public static final void populateDemoData(Database db)
            throws SystemErrorException {

        DataColumn dc;
        long colid;

        // Float column
        dc = new DataColumn(db, "float",
                MatrixVocabElement.MatrixType.FLOAT);
        colid = db.addColumn(dc);

        FloatDataValue fdv = new FloatDataValue(db);
        for (long onset = 0; onset < 20000; onset += 2000 ) {
            fdv.setItsValue(1.2345 * onset);
            AddDataValue(db, colid, onset, onset + 1000, fdv);
        }

        // Int column
        dc = new DataColumn(db, "int",
                MatrixVocabElement.MatrixType.INTEGER);
        colid = db.addColumn(dc);

        IntDataValue idv = new IntDataValue(db);
        for (long onset = 0; onset < 20000; onset += 2000 ) {
            AddDataValue(db, colid, onset, onset + 1000, idv);
            idv.setItsValue(onset * 2);
        }

        // Text column
        dc = new DataColumn(db, "text",
                MatrixVocabElement.MatrixType.TEXT);
        colid = db.addColumn(dc);

        TextStringDataValue tdv = new TextStringDataValue(db);
        for (long onset = 0; onset < 20000; onset += 2000 ) {
            AddDataValue(db, colid, onset, onset + 1000, tdv);
            tdv.setItsValue("Testing string -- " + onset);
        }

        // Nominal column
        dc = new DataColumn(db, "nominal",
                MatrixVocabElement.MatrixType.NOMINAL);
        colid = db.addColumn(dc);

        NominalDataValue ndv = new NominalDataValue(db);
        for (long onset = 0; onset < 20000; onset += 2000 ) {
            AddDataValue(db, colid, onset, onset + 1000, ndv);
            ndv.setItsValue("Nom -- " + onset);
        }

        // Predicate column
        dc = new DataColumn(db, "predicate",
                MatrixVocabElement.MatrixType.PREDICATE);
        colid = db.addColumn(dc);

        PredDataValue pdv = new PredDataValue(db);
        Predicate p = MakeDemoPredicate(db);

        for (long onset = 0; onset < 20000; onset += 2000 ) {
            AddDataValue(db, colid, onset, onset + 1000, pdv);
            pdv.setItsValue(p);
        }

        // Matrix column
        String name = "matrix";
        dc = new DataColumn(db, name,
                MatrixVocabElement.MatrixType.MATRIX);
        colid = db.addColumn(dc);

        Matrix m = MakeDemoMatrix(db, name);

        for (long onset = 0; onset < 20000; onset += 2000 ) {
            AddMatrix(db, colid, onset, onset + 1000, m);
        }


    }

    /**
     * Adds a datavalue to a database.
     *
     * Creates a one argument matrix containing the datavalue and adds it
     * to the column identified.
     * @param db Database to add datavalue to.
     * @param colid ID of the column to use.
     * @param onset Onset value.
     * @param offset Offset value.
     * @param dv Datavalue to add.
     * @throws SystemErrorException if add doesn't work.
     */
    private static void AddDataValue(Database db,
                        long colid, long onset, long offset, DataValue dv)
            throws SystemErrorException {

        DataColumn dc = db.getDataColumn(colid);
        MatrixVocabElement mve = db.getMatrixVE(dc.getItsMveID());
        // get the first (the only in a one arg matrix) formal arg
        FormalArgument fa = mve.getFormalArg(0);

        dv.setItsFargID(fa.getID());

        Vector<DataValue> vec = new Vector<DataValue>();
        vec.add(dv);

        Matrix mat = new Matrix(db, mve.getID(), vec);

        AddMatrix(db, colid, onset, offset, mat);
    }

    /**
     * Add a matrix item to a database.
     * @param db Database to add the matrix item to.
     * @param colid ID of the column to use.
     * @param onset Onset.
     * @param offset Offset.
     * @param mat Matrix item to add.
     * @throws SystemErrorException
     */
    private static void AddMatrix(Database db,
                        long colid, long onset, long offset, Matrix mat)
            throws SystemErrorException {

        DataCell cell = new DataCell(db, "Datacell", colid, mat.getMveID(),
                        new TimeStamp(TICKS_PER_SECOND, onset),
                        new TimeStamp(TICKS_PER_SECOND, offset),
                        mat);

        db.appendCell(cell);
    }

    private static Predicate MakeDemoPredicate(Database db)
                                            throws SystemErrorException {
        PredicateVocabElement pve0;
        FormalArgument farg;

        pve0 = new PredicateVocabElement(db, "test0");

        farg = new FloatFormalArg(db, "<float>");
        pve0.appendFormalArg(farg);
        farg = new IntFormalArg(db, "<int>");
        pve0.appendFormalArg(farg);
        farg = new NominalFormalArg(db, "<nominal>");
        pve0.appendFormalArg(farg);
        farg = new PredFormalArg(db, "<pred>");
        pve0.appendFormalArg(farg);
        farg = new QuoteStringFormalArg(db, "<qstring>");
        pve0.appendFormalArg(farg);
        farg = new UnTypedFormalArg(db, "<untyped>");
        pve0.appendFormalArg(farg);
//            farg = new TimeStampFormalArg(db, "<timestamp>");
//            pve0.appendFormalArg(farg);

        long predID0 = db.addPredVE(pve0);

        // get a copy of the databases version of pve0 with ids assigned
        pve0 = db.getPredVE(predID0);

        Vector<DataValue> argList0 = new Vector<DataValue>();

        long fargID = pve0.getFormalArg(0).getID();
        DataValue arg = new FloatDataValue(db, fargID, 1.0);
        argList0.add(arg);
        fargID = pve0.getFormalArg(1).getID();
        arg = new IntDataValue(db, fargID, 2);
        argList0.add(arg);
        fargID = pve0.getFormalArg(2).getID();
        arg = new NominalDataValue(db, fargID, "a_nominal");
        argList0.add(arg);
        fargID = pve0.getFormalArg(3).getID();
        arg = new PredDataValue(db, fargID, new Predicate(db, predID0));
        argList0.add(arg);
        fargID = pve0.getFormalArg(4).getID();
        arg = new QuoteStringDataValue(db, fargID, "q-string");
        argList0.add(arg);
        fargID = pve0.getFormalArg(5).getID();
        arg = new UndefinedDataValue(db, fargID,
                                     pve0.getFormalArg(5).getFargName());
        argList0.add(arg);
//            fargID = pve0.getFormalArg(6).getID();
//            arg = new TimeStampDataValue(db, fargID,
//                                         new TimeStamp(db.getTicks()));
//            argList0.add(arg);

        return new Predicate(db, predID0, argList0);
    }

    private static Matrix MakeDemoMatrix(Database db, String mvename)
                    throws SystemErrorException {
        MatrixVocabElement mve0;
        FormalArgument farg;

        mve0 = (MatrixVocabElement)(db.vl.getVocabElement(mvename));
        mve0 = new MatrixVocabElement(mve0);

        mve0.deleteFormalArg(0);

        farg = new FloatFormalArg(db, "<float>");
        mve0.appendFormalArg(farg);
        farg = new IntFormalArg(db, "<int>");
        mve0.appendFormalArg(farg);
        farg = new NominalFormalArg(db, "<nominal>");
        mve0.appendFormalArg(farg);
        farg = new QuoteStringFormalArg(db, "<qstring>");
        mve0.appendFormalArg(farg);
        farg = new UnTypedFormalArg(db, "<untyped>");
        mve0.appendFormalArg(farg);

        db.vl.replaceVocabElement(mve0);
        long matID0 = mve0.getID();

        // get a copy of the databases version of mve0 with ids assigned
        mve0 = db.getMatrixVE(matID0);

        Vector<DataValue> argList0 = new Vector<DataValue>();

        long fargID = mve0.getFormalArg(0).getID();
        DataValue arg = new FloatDataValue(db, fargID, 1.234);
        argList0.add(arg);
        fargID = mve0.getFormalArg(1).getID();
        arg = new IntDataValue(db, fargID, 2);
        argList0.add(arg);
        fargID = mve0.getFormalArg(2).getID();
        arg = new NominalDataValue(db, fargID, "a_nominal");
        argList0.add(arg);
        fargID = mve0.getFormalArg(3).getID();
        arg = new QuoteStringDataValue(db, fargID, "q-string");
        argList0.add(arg);
        fargID = mve0.getFormalArg(4).getID();
        arg = new UndefinedDataValue(db, fargID,
                                     mve0.getFormalArg(4).getFargName());
        argList0.add(arg);

        long mveid = mve0.getID();
        return new Matrix(db, mveid, argList0);
    }

}
