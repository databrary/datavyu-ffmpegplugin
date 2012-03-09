/*
 * Copyright (c) 2011 OpenSHAPA Foundation, http://openshapa.org
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

package org.openshapa.models.db;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import org.openshapa.util.NativeLoader;

/**
 * Acts as a connector between OpenSHAPA and a MongoDB instance.
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

    // 
    private List<DatastoreListener> dbListeners = new ArrayList<DatastoreListener>();

    public MongoDatastore () {
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

        DBCollection matrixCollection = mongoDB.getCollection("matrix_values");
        DBCursor matrixCursor = matrixCollection.find();
        while (matrixCursor.hasNext()) {
            matrixCollection.remove(matrixCursor.next());
        }

        DBCollection nominalCollection = mongoDB.getCollection("nominal_values");
        DBCursor nominalCursor = nominalCollection.find();
        while (nominalCursor.hasNext()) {
            nominalCollection.remove(nominalCursor.next());
        }

        DBCollection textCollection = mongoDB.getCollection("text_values");
        DBCursor textCursor = textCollection.find();
        while (textCursor.hasNext()) {
            textCollection.remove(textCursor.next());
        }
    }
    
    /**
     * Spin up the mongo instance so that we can query and do stuff with it.
     */
    public static void startMongo() {
        // Unpack the mongo executable.
        try {
            String mongoDir = NativeLoader.unpackNativeApp("mongodb-osx64-2.0.2");

            // When files are unjared - they loose their executable status.
            File f = new File(mongoDir + "/mongodb-osx-x86_64-2.0.2/bin/mongod");
            f.setExecutable(true);

            // Spin up a new mongo instance.
            File mongoD = new File(mongoDir);
            int port = findFreePort(27019);
            
            mongoProcess = new ProcessBuilder(f.getAbsolutePath(),
                                              "--dbpath", mongoD.getAbsolutePath(),
                                              "--port", String.valueOf(port),
                                              "--directoryperdb").start();
            InputStream in = mongoProcess.getInputStream();
            InputStreamReader isr = new InputStreamReader(in);

            System.out.println("Starting mongo driver.");
            mongoDriver = new Mongo("localhost", port);
            
            System.out.println("Getting DB");
            mongoDriver.dropDatabase("openshapa");
            mongoDB = mongoDriver.getDB("openshapa");
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
              System.out.println("---Selected port " + String.valueOf(port));
          } catch (IOException io) {
              port += 1;
              System.out.println("---ERROR: Port in use. Trying port " + String.valueOf(port));
          }
      }
      return free_port;
    }
    
    public static void stopMongo() {
        try {
            DB db = mongoDriver.getDB("admin");
            db.command(new BasicDBObject( "shutdown" , 1  ));
            mongoDriver.close();
        } catch (Exception e) {
            LOGGER.error("Unable to cleanly take down mongo", e);
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
        DBCursor varCursor = varCollection.find();
        
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
    public List<Cell> getSelectedCells() {
        List<Cell> selectedCells = new ArrayList<Cell>();

        return selectedCells;
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
        
        for(DatastoreListener dbl : this.dbListeners ) {
            dbl.variableAdded(v);
        }

        return v;
    }

    @Override
    @Deprecated
    public void addVariable(final Variable var) {

    }

    @Override
    public void removeVariable(final Variable var) {
        DBCollection varCollection = mongoDB.getCollection("variables");

        varCollection.remove((MongoVariable)var);

        for(DatastoreListener dbl : this.dbListeners ) {
            dbl.variableRemoved(var);
        }
    }

    @Override
    public void removeCell(final Cell cell) {
        getVariable(cell).removeCell(cell);
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void canSetUnsaved(final boolean canSet) {
    }

    @Override
    public void markAsUnchanged() {
    }

    @Override
    public boolean isChanged() {
        return false;
    }

    @Override
    public void setName(final String datastoreName) {
    }

    @Override
    public void setTitleNotifier(final TitleNotifier titleNotifier) {
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
