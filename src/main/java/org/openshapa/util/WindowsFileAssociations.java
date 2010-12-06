package org.openshapa.util;

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
                "OpenSHAPA Project File"));

        if (!hkcr.hasSubkey("OpenSHAPA")) {
            RegistryKey program = hkcr.createSubkey("OpenSHAPA");
            program.setValue(new RegistryValue("", ValueType.REG_SZ,
                    "OpenSHAPA"));
        }

        File programFile = cwd(); // This will return the .exe in production
        String command = programFile.getAbsolutePath() + " \"%1\"";

        RegistryKey programCmd = null;

        if (hkcr.hasSubkey("OpenSHAPA\\shell\\open\\command")) {
            programCmd = new RegistryKey("OpenSHAPA\\shell\\open\\command");
        } else {
            programCmd = hkcr.createSubkey("OpenSHAPA\\shell\\open\\command");
        }

        // Update the key value so that the new version of OpenSHAPA is used.
        programCmd.deleteValue("");
        programCmd.setValue(new RegistryValue("", ValueType.REG_SZ, command));
    }

    private static File cwd() {
        return new File(WindowsFileAssociations.class.getProtectionDomain()
                .getCodeSource().getLocation().getFile());
    }

}
