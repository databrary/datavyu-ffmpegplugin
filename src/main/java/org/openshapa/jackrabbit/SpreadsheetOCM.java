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
package org.openshapa.jackrabbit;

import com.usermetrix.jclient.UserMetrix;
import com.usermetrix.jclient.Logger;
import database.Cell;
import database.DataCell;
import database.DataCellTO;
import database.DataColumn;
import database.DataColumnTO;
import database.Database;
import database.SystemErrorException;
import java.util.ArrayList;
import java.util.List;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.openshapa.OpenSHAPA;
import org.openshapa.controllers.project.ProjectController;
import org.openshapa.models.db.Datastore;
import org.openshapa.models.db.Variable;

@Node
public class SpreadsheetOCM {
    private static final Logger LOGGER = UserMetrix.getLogger(SpreadsheetOCM.class);
    @Field(path = true)
    private String path;
    @Field
    private String name;
    @Collection
    List<VariableOCM> variables;

    private ProjectController controller;
    private Datastore model;
    private Database db;    
    
    public SpreadsheetOCM() {

        controller = OpenSHAPA.getProjectController();
        model = controller.getDB();
        db = controller.getLegacyDB().getDatabase();
        
        path = "/" + model.getName();
        name = db.getName();
        
        List<DataColumnTO> colsTO = getSpreadsheetState();
        variables = new ArrayList<VariableOCM>();
        
        for (DataColumnTO colTO : colsTO) {
            VariableOCM var = new VariableOCM(colTO);
            variables.add(var);
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<VariableOCM> getVariables() {
        return variables;
    }

    public void setVariables(List<VariableOCM> variables) {
        this.variables = variables;
    }
    
    private List<DataColumnTO> getSpreadsheetState() {
         List<DataColumnTO> colsTO = new ArrayList<DataColumnTO>();
         try {            
            List<Variable> vars = model.getAllVariables();
            List<DataColumn> colsToDelete = new ArrayList<DataColumn>();
            for (Variable var : vars) {
                String name = var.getName();
                for (DataColumn dc : db.getDataColumns()) {
                    if (name.equals(dc.getName())) {
                        colsToDelete.add(dc);    
                    }
                }
            }     
            // Add the cells to each Column
            for (DataColumn col : colsToDelete) {
                DataColumnTO colTO = new DataColumnTO(col);               
                int numCells = col.getNumCells(); 
                colTO.dataCellsTO.clear();
                for (int i = 0; i < numCells; i++) {
                        Cell c = db.getCell(col.getID(), i+1);
                        DataCellTO cTO = new DataCellTO((DataCell)c);
                        colTO.dataCellsTO.add(cTO);
                }
                colsTO.add(colTO);                    
            }
        } catch (SystemErrorException e) {
            LOGGER.error("Unable to getSpreadsheetState.", e);
        } finally {
            return colsTO;
        }       
    }      
}
