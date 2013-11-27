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
package org.datavyu.util;

import java.io.File;

//import ca.beq.util.win32.registry.*;


/**
 * Set up file associations in the Windows Registry. User must have admin
 * access.
 * http://www.jrsoftware.org/isfaq.php
 */
public class WindowsFileAssociations {

    public static void setup()  {
        String assoc = "assoc .opf opffile";
        String ftype = "ftype opffile" + cwd() + " %1";
        
        try { 
            Process process = Runtime.getRuntime().exec(assoc);
            process = Runtime.getRuntime().exec(ftype);
        } catch (Exception e) {
            
        }
    }

    private static File cwd() {
        return new File(WindowsFileAssociations.class.getProtectionDomain()
                .getCodeSource().getLocation().getFile());
    }

}
