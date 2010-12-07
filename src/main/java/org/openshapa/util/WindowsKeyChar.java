package org.openshapa.util;

public class WindowsKeyChar {

    public static char remap(final char brokenChar) {
        int val = brokenChar;

        // Not guaranteed to work for all characters.
        return (char) (val + 64);
    }

}
