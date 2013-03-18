/*
 * Copyright (c) 2011 Datavyu Foundation, http://datavyu.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.datavyu.models.db;

import com.mongodb.*;
import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.datavyu.Datavyu;
import org.datavyu.models.db.Argument;
import org.datavyu.models.db.Cell;
import org.datavyu.models.db.Datastore;
import org.datavyu.models.db.DatastoreListener;
import org.datavyu.models.db.TitleNotifier;
import org.datavyu.models.db.UserWarningException;
import org.datavyu.models.db.Variable;
import org.datavyu.util.NativeLoader;

/**
 * Acts as a connector between Datavyu and a MongoDB instance.
 */
public class MongoDatastore implements Datastore {

    // The logger for the mongo datastore -- Can't use in startMongo because
    // UserMetrix has not been initalised yet
    private static Logger LOGGER = UserMetrix.getLogger(MongoDatastore.class);

    // The process for the mongo executable kicking around in the background.
    private static Process mongoProcess;

    // Only a single instance of the mongo driver exists.
    private static Mongo mongoDriver = null;

    // Only a single instance of the mongo DB exists.
    private static DB mongoDB = null;

    // The current status of the database.
    private static boolean running = false;

    // Name of the datastore - does not need to persist - is used for file names.
    private String name = "untitled";

    // The notifier to ping when the application's title changes.
    private static TitleNotifier titleNotifier = null;

    // Has tbhe datastore changed since it has last been marked as unchanged?
    private static boolean changed;
    
    // Location of the Mongo OSX files
    private static final String mongoOSXLocation = "mongodb-osx64-2.0.2";
    
    // Location of the Mongo Win32 files
    private static final String mongoWindowsLocation = "mongodb-win32-2.0.2.jar";

    //
    private List<DatastoreListener> dbListeners = new ArrayList<DatastoreListener>();

    public MongoDatastore () {
        if(!running) {
            this.startMongo();
        }

        // Clear documents if any.
        DBCollection varCollection = mongoDB.getCollection("variables");
        DBCursor varCursor = varCollection.find();
        while (varCursor.hasNext()) {
            varCollection.remove(varCursor.next());
        }

        DBCollection cellCollection = mongoDB.getCollection("cells");
        DBCursor cellCursor = cellCollection.find();
        while (cellCursor.hasNext()) {
            cellCollection.remove(cellCursor.next());
        }

        // Place indexes on the cell collection for fast querying
        BasicDBObject cell_index = new BasicDBObject();
        cell_index.put("variable_id", 1);
        cell_index.put("onset", 1);

        cellCollection.ensureIndex(cell_index);

        cellCollection.ensureIndex(new BasicDBObject("variable_id", 1));
        cellCollection.ensureIndex(new BasicDBObject("onset", 1));
        cellCollection.ensureIndex(new BasicDBObject("offset", 1));

        cell_index = new BasicDBObject();
        cell_index.put("onset", 1);
        cell_index.put("offset", 1);
        cellCollection.ensureIndex(cell_index);

        DBCollection matrixCollection = mongoDB.getCollection("matrix_values");
        DBCursor matrixCursor = matrixCollection.find();
        while (matrixCursor.hasNext()) {
            matrixCollection.remove(matrixCursor.next());
        }
        matrixCollection.ensureIndex(new BasicDBObject("parent_id", 1));

        DBCollection nominalCollection = mongoDB.getCollection("nominal_values");
        DBCursor nominalCursor = nominalCollection.find();
        while (nominalCursor.hasNext()) {
            nominalCollection.remove(nominalCursor.next());
        }
        nominalCollection.ensureIndex(new BasicDBObject("parent_id", 1));

        DBCollection textCollection = mongoDB.getCollection("text_values");
        DBCursor textCursor = textCollection.find();
        while (textCursor.hasNext()) {
            textCollection.remove(textCursor.next());
        }
        textCollection.ensureIndex(new BasicDBObject("parent_id", 1));

        // Clear variable listeners.
        MongoVariable.clearListeners();
        MongoDatastore.changed = false;
    }

    public static void markDBAsChanged() {
	if (!MongoDatastore.changed) {
	    MongoDatastore.changed = true;

	    if (MongoDatastore.titleNotifier != null) {
	        MongoDatastore.titleNotifier.updateTitle();
            }
        }
    }

    /**
     * Spin up the mongo instance so that we can query and do stuff with it.
     */
    public static void startMongo() {
        // Unpack the mongo executable.
        try {
            String mongoDir;
	    switch (Datavyu.getPlatform()) {
		    case MAC:
			    mongoDir = NativeLoader.unpackNativeApp(mongoOSXLocation);
			    break;
		    case WINDOWS:
			    mongoDir = NativeLoader.unpackNativeApp(mongoWindowsLocation);
			    break;
		    default:
			    mongoDir = NativeLoader.unpackNativeApp(mongoOSXLocation);
	    }

            // When files are unjared - they loose their executable status.
	    // So find the mongod executable and set it to exe
	    Collection<File> files = FileUtils.listFiles(
			new File(mongoDir),
			new RegexFileFilter(".*mongod.*"),
			TrueFileFilter.INSTANCE);
	    
	    File f;
	    if(files.iterator().hasNext()) {
		f = files.iterator().next();
		f.setExecutable(true);
	    }
	    else {
		f = new File("");
		System.out.println("ERROR: Could not find mongod");
	    }
	    
//            File f = new File(mongoDir + "/mongodb-osx-x86_64-2.0.2/bin/mongod");
//            f.setExecutable(true);

            // Spin up a new mongo instance.
            File mongoD = new File(mongoDir);
//            int port = findFreePort(27019);
	    int port = 27019;

            mongoProcess = new ProcessBuilder(f.getAbsolutePath(),
//                                              "-v",
                                              "--dbpath", mongoD.getAbsolutePath(),
//                                              "--logpath", mongoD.getAbsolutePath() + "/mongolog.txt",
                                              "--port", String.valueOf(port),
                                              "--directoryperdb").start();
//            InputStream in = mongoProcess.getInputStream();
//            InputStreamReader isr = new InputStreamReader(in);

            System.out.println("Starting mongo driver.");
            mongoDriver = new Mongo("127.0.0.1", port);
	   
            System.out.println("Getting DB");

            // Start with a clean DB
            mongoDB = mongoDriver.getDB("datavyu");

            DBCollection varCollection = mongoDB.getCollection("variables");
            varCollection.setObjectClass(MongoVariable.class);

            DBCollection cellCollection = mongoDB.getCollection("cells");
            cellCollection.setObjectClass(MongoCell.class);

            DBCollection matrixCollection = mongoDB.getCollection("matrix_values");
            matrixCollection.setObjectClass(MongoMatrixValue.class);

            DBCollection nominalCollection = mongoDB.getCollection("nominal_values");
            nominalCollection.setObjectClass(MongoNominalValue.class);

            DBCollection textCollection = mongoDB.getCollection("text_values");
            textCollection.setObjectClass(MongoTextValue.class);



            System.out.println("Got DB");
            running = true;

        } catch (Exception e) {
            System.err.println("Unable to fire up the mongo datastore.");
            e.printStackTrace();
        }
    }

    public static int findFreePort(int port) throws IOException {
      int free_port = 0;
      while (free_port == 0) {
          try {
              ServerSocket server = new ServerSocket(port);
              server.close();

              free_port = port;
          } catch (IOException io) {
              port += 1;
          }
      }
      return free_port;
    }

    public static void stopMongo() {
        try {
            DB db = mongoDriver.getDB("admin");
            db.command(new BasicDBObject( "shutdownServer" , 1  ));
            running = false;
            mongoDriver.close();

            // Sleep for just a little bit before closing. Let everything
            // finish.
            Thread.sleep(2000);
        } catch (Exception e) {
            LOGGER.error("Unable to cleanly take down mongo. Maybe it was already taken down?", e);
        } finally {
            mongoProcess.destroy();
        }
    }

    public static DB getDB() {
        return mongoDB;
    }

    public static DBCollection getCellCollection() {
        return mongoDB.getCollection("cells");
    }
    public static DBCollection getVariableCollection() {
        return mongoDB.getCollection("variables");
    }
    public static DBCollection getMatrixValuesCollection() {
        return mongoDB.getCollection("matrix_values");
    }
    public static DBCollection getNominalValuesCollection() {
        return mongoDB.getCollection("nominal_values");
    }
    public static DBCollection getTextValuesCollection() {
        return mongoDB.getCollection("text_values");
    }

    @Override
    public List<Variable> getAllVariables() {

        DBCollection varCollection = mongoDB.getCollection("variables");
        BasicDBObject query = new BasicDBObject();
        query.put("_id", true);
        DBCursor varCursor = varCollection.find().sort(query);

        List<Variable> varList = new ArrayList<Variable>();
        while(varCursor.hasNext()) {
            MongoVariable v = (MongoVariable)varCursor.next();
            varList.add(v);

        }
        return varList;
    }

    @Override
    public List<Variable> getSelectedVariables() {
        List<Variable> selectedVariables = new ArrayList<Variable>();

        DBCollection varCollection = mongoDB.getCollection("variables");
        BasicDBObject query = new BasicDBObject();
        query.put("selected", true);
        DBCursor varCursor = varCollection.find(query);

        while (varCursor.hasNext()) {
            selectedVariables.add((MongoVariable) varCursor.next());
        }

        return selectedVariables;
    }

    @Override
    public void clearVariableSelection() {
        DBCollection varCollection = mongoDB.getCollection("variables");
        BasicDBObject query = new BasicDBObject();
        query.put("selected", true);
        DBCursor varCursor = varCollection.find(query);

        while (varCursor.hasNext()) {
            ((MongoVariable) varCursor.next()).setSelected(false);
        }
	markDBAsChanged();
    }

    @Override
    public List<Cell> getSelectedCells() {
        List<Cell> selectedCells = new ArrayList<Cell>();

        DBCollection cellCollection = mongoDB.getCollection("cells");
        BasicDBObject query = new BasicDBObject();
        query.put("selected", true);
        DBCursor cellCursor = cellCollection.find(query);

        while (cellCursor.hasNext()) {
            selectedCells.add((MongoCell) cellCursor.next());
        }

        return selectedCells;
    }

    @Override
    public void clearCellSelection() {
        DBCollection cellCollection = mongoDB.getCollection("cells");
        BasicDBObject query = new BasicDBObject();
        query.put("selected", true);
        DBCursor cellCursor = cellCollection.find(query);

        while (cellCursor.hasNext()) {
            ((MongoCell) cellCursor.next()).setSelected(false);
        }
        markDBAsChanged();
    }

    @Override
    public void deselectAll() {
        this.clearCellSelection();
        this.clearVariableSelection();
        markDBAsChanged();
    }

    @Override
    public Variable getVariable(String varName) {
        DBCollection varCollection = mongoDB.getCollection("variables");
        BasicDBObject query = new BasicDBObject();
        query.put("name", varName);

        DBCursor varCursor = varCollection.find(query);

        if (varCursor.hasNext()) {
            return (Variable)varCursor.next();
        } else {
            return null;
        }
    }

    @Override
    public Variable getVariable(Cell cell) {
        // We need to use a mongo-specific function to do the lookup
        MongoCell mcell = (MongoCell)cell;

        // Form the query
        DBCollection varCollection = mongoDB.getCollection("variables");
        BasicDBObject query = new BasicDBObject();
        query.put("_id", mcell.getVariableID());

        DBCursor varCursor = varCollection.find(query);

        if (varCursor.hasNext()) {
            return (Variable)varCursor.next();
        } else {
            return null;
        }
    }

    @Override
    public Variable createVariable(final String name, final Argument.Type type)
    throws UserWarningException {
        DBCollection varCollection = mongoDB.getCollection("variables");

        // Check to make sure the variable name is not already in use:
        Variable varTest = getVariable(name);
        if (varTest != null) {
            throw new UserWarningException("Unable to add variable, one with the same name already exists.");
        }

        Variable v = new MongoVariable(name, new Argument("arg01", type));

        varCollection.save((MongoVariable)v);

        for (DatastoreListener dbl : this.dbListeners) {
            dbl.variableAdded(v);
        }

        markDBAsChanged();
        return v;
    }

    @Override
    public void removeVariable(final Variable var) {
        DBCollection varCollection = mongoDB.getCollection("variables");

        BasicDBObject query = new BasicDBObject();

        for(Cell c : var.getCells()) {
            var.removeCell(c);
        }

        for(DatastoreListener dbl : this.dbListeners ) {
            dbl.variableRemoved(var);
        }

        query.put("_id", ((MongoVariable)var).getID());
        varCollection.remove(query);
        markDBAsChanged();

    }

    @Override
    public void removeCell(final Cell cell) {
        getVariable(cell).removeCell(cell);
        markDBAsChanged();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void canSetUnsaved(final boolean canSet) {
    }

    @Override
    public void markAsUnchanged() {
	if (MongoDatastore.changed) {
	    MongoDatastore.changed = false;

	    if (MongoDatastore.titleNotifier != null) {
	        MongoDatastore.titleNotifier.updateTitle();
            }
        }
    }

    @Override
    public boolean isChanged() {
        return MongoDatastore.changed;
    }

    @Override
    public void setName(final String datastoreName) {
        name = datastoreName;
    }

    @Override
    public void setTitleNotifier(final TitleNotifier titleNotifier) {
	MongoDatastore.titleNotifier = titleNotifier;
    }

    @Override
    public void addListener(final DatastoreListener listener) {
        dbListeners.add(listener);
    }

    @Override
    public void removeListener(final DatastoreListener listener) {
        if(dbListeners.contains(listener)) {
            dbListeners.remove(listener);
        }
    }
}
