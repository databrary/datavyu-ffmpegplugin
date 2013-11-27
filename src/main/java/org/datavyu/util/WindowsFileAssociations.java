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

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

//import ca.beq.util.win32.registry.*;


/**
 * Set up file associations in the Windows Registry. User must have admin
 * access.
 * http://www.jrsoftware.org/isfaq.php
 */
public class WindowsFileAssociations {

    public static void setup()  {
        String assoc = "cmd /c assoc .opf=opffile";
        String ftype = "cmd /c ftype opffile=\"" + cwd() + "\" \"%1\"";
        
        try { 
            Process process = Runtime.getRuntime().exec(assoc);
            
            System.out.println(assoc);
            System.out.println(ftype);
            
            // Get input streams
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // Read command standard output
            String s;
            System.out.println("Standard output: ");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // Read command errors
            System.out.println("Standard error: ");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            
            process = Runtime.getRuntime().exec(ftype);
            
            // Get input streams
            stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            // Read command standard output
            System.out.println("Standard output: ");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            // Read command errors
            System.out.println("Standard error: ");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static File cwd() {
        return new File(WindowsFileAssociations.class.getProtectionDomain()
                .getCodeSource().getLocation().getFile());
    }

}
