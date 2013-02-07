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

import ca.beq.util.win32.registry.*;


/**
 * Set up file associations in the Windows Registry. User must have admin
 * access.
 * http://www.jrsoftware.org/isfaq.php
 */
public class WindowsFileAssociations {

    public static void setup() throws Win32Exception {
        RegistryKey hkcr = new RegistryKey(RootKey.HKEY_CLASSES_ROOT);

        RegistryKey extension = hkcr.createSubkey(".opf");
        extension.setValue(new RegistryValue("", ValueType.REG_SZ,
                "Datavyu Project File"));

        if (!hkcr.hasSubkey("Datavyu")) {
            RegistryKey program = hkcr.createSubkey("Datavyu");
            program.setValue(new RegistryValue("", ValueType.REG_SZ,
                    "Datavyu"));
        }

        File programFile = cwd(); // This will return the .exe in production
        String command = programFile.getAbsolutePath() + " \"%1\"";

        RegistryKey programCmd = null;

        if (hkcr.hasSubkey("Datavyu\\shell\\open\\command")) {
            programCmd = new RegistryKey("Datavyu\\shell\\open\\command");
        } else {
            programCmd = hkcr.createSubkey("Datavyu\\shell\\open\\command");
        }

        // Update the key value so that the new version of Datavyu is used.
        programCmd.deleteValue("");
        programCmd.setValue(new RegistryValue("", ValueType.REG_SZ, command));
    }

    private static File cwd() {
        return new File(WindowsFileAssociations.class.getProtectionDomain()
                .getCodeSource().getLocation().getFile());
    }

}
