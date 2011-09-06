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
package org.openshapa.controllers;

import com.usermetrix.jclient.Logger;
import com.usermetrix.jclient.UserMetrix;

import java.io.IOException;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import org.apache.commons.io.FilenameUtils;
import org.openshapa.OpenSHAPA;
import org.openshapa.controllers.project.ProjectController;
import database.LogicErrorException;
import org.openshapa.util.FileFilters.OPFFilter;
import org.openshapa.util.FileFilters.SHAPAFilter;

public class AutosaveC implements ActionListener {
   
    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(DeleteColumnC.class);    
    
    private static Timer timer;
    private static File f;
    
    public static void setInterval(int interval) {
        if(interval == 0)  {
            if(timer != null)  {
                timer.stop();
		timer = null;
            }
            return;
	}
	interval *= 60000;
	if(timer == null)  {
            timer = new Timer(interval,new AutosaveC());
            timer.start();
	}
        else  {
            timer.setDelay(interval);
        }    
    } 

    public static void stop()  {
        if(timer != null)  {
            timer.stop();
        }        
    }

    @Override
    public void actionPerformed(ActionEvent evt)  {
        String baseName;
        String ext;
        // save the project
        try {    
            if (f != null) {
                f.delete();
            }
            ProjectController projController = OpenSHAPA.getProjectController();
            SaveC saveController = new SaveC();
            if (projController.isNewProject() || (projController.getProjectName() == null)) {
                baseName = "~noname_"; 
                ext = ".opf";
                f = File.createTempFile(baseName, ext);         
                saveController.saveProject(f,
                               projController.getProject(),
                               projController.getLegacyDB().getDatabase(), false);   
            } else {
                if ((projController.getLastSaveOption() instanceof SHAPAFilter)
                        || (projController.getLastSaveOption()
                            instanceof OPFFilter)) {            
                    baseName = "~" + projController.getProjectName() + "_";
                    ext = ".opf"; 
                    f = File.createTempFile(baseName, ext);          
                    saveController.saveProject(f,
                               projController.getProject(),
                               projController.getLegacyDB().getDatabase(), false);   
                // Save content just as a database.                          
                } else {               
                    String filename = "~" + projController.getDatabaseFileName();
                    baseName = FilenameUtils.getBaseName(filename) + "_";
                    ext = "." + FilenameUtils.getExtension(filename);
                    f = File.createTempFile(baseName, ext);
                    saveController.saveDatabase(f,
                        projController.getLegacyDB().getDatabase(), false);
                }                        
            }
        } catch (LogicErrorException lee) {
                LOGGER.error("LogicErrorException: Unable to autosave.", lee);
        } catch (IOException ioe) {
                LOGGER.error("IOException: Unable to autosave.", ioe);
        } finally {
                f.deleteOnExit();                                
        }                            
    } 

    private AutosaveC() {}    
}
