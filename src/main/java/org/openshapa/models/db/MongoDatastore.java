/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openshapa.models.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.openshapa.util.NativeLoader;

/**
 * Acts as a connector between OpenSHAPA and a MongoDB instance.
 */
public class MongoDatastore implements Datastore {
    
    public MongoDatastore () {
        // Unpack the mongo executable.
        try {
            String mongoDir = NativeLoader.unpackNativeApp("mongodb-osx64-2.0.2");                

            // When files are unjared - they loose their executable status.
            File f = new File(mongoDir + "/mongodb-osx-x86_64-2.0.2/bin/mongod");
            f.setExecutable(true);

            // Spin up a new mongo instance.
            Process p = new ProcessBuilder(f.getAbsolutePath()).start();
            InputStream in = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);
            String line;

            System.err.println("Output of running is:");
            while ((line = br.readLine()) != null) {
                System.err.println(line);
            }

            p.destroy();
        } catch (Exception e) {
            System.err.println("Unable to fire up the mongo datastore.");
        }
    }

    @Override
    public List<Variable> getAllVariables() {
        return null;
    }

    @Override
    public List<Variable> getSelectedVariables() {
        return null;
    }

    @Override
    public List<Cell> getSelectedCells() {
        return null;
    }

    @Override
    public Variable getVariable(String varName) {
        return null;
    }

    @Override
    public Variable getVariable(Cell cell) {
        return null;
    }

    @Override
    public Variable createVariable(final String name, final Argument.Type type)
    throws UserWarningException {
        return null;
    }

    @Override
    @Deprecated
    public void addVariable(final Variable var) {
    }

    @Override
    public void removeVariable(final Variable var) {        
    }

    @Override
    public void removeCell(final Cell cell) {        
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
    }

    @Override
    public void removeListener(final DatastoreListener listener) {
    }
}
