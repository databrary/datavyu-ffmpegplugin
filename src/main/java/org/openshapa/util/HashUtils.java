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
package org.openshapa.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.DigestInputStream;
import java.security.NoSuchAlgorithmException;

import com.usermetrix.jclient.UserMetrix;
import com.usermetrix.jclient.Logger;

/**
 * Class for a couple of helper functions for generating hashcodes.
 */
public final class HashUtils {

    /** The logger for this class. */
    private static Logger LOGGER = UserMetrix.getLogger(HashUtils.class);

    /**
     * Generates a hash code for the supplied object.
     *
     * @param obj The object to generate a hash code for.
     *
     * @return The hashcode for the object, 0 if the supplied object is null.
     */
    public static int Obj2H(final Object obj) {
        if (obj == null) {
            return 0;
        } else {
            return obj.hashCode();
        }
    }

    /**
     * Generates an integer hashcode from a long value.
     *
     * @param l The long value to turn into an integer hashCode.
     *
     * @return The integer hashcode for the long value.
     */
    public static int Long2H(final long l) {
        return (int)(l ^ (l >>> 32));
    }

    /**
     * Computes the MD5 digest for a given file.
     *
     * @param file The file over which to compute the MD5 digest
     * @return The hex string MD5 digest for the file. Returns null in case of an exception.
     */
    public static String computeDigest(final File file) {
        if (!file.exists())
            return null;

        LOGGER.event("Compute MD5 digest for file " + file.getName());
        String md5 = null;
        byte[] buffer = new byte[8192];
        MessageDigest md = null;
        FileInputStream fis = null;
        DigestInputStream dis = null;

        try {

            md = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            dis = new DigestInputStream(fis, md);
            while (dis.read(buffer) != -1) { }
            md5 = convertToHex(md.digest());

        } catch (NoSuchAlgorithmException nsae) {
            LOGGER.error("Unable to instantiate MD5 algorithm for file " + file.getName(), nsae);
        } catch (FileNotFoundException fnfe) {
            LOGGER.error("Unable to find file " + file.getName(), fnfe);
        } catch (IOException ioe) {
            LOGGER.error("Unable to compute MD5 digest for file " + file.getName(), ioe);
        } finally {
            try {
                if (dis != null)
                    dis.close();
                if (fis != null)
                    fis.close();
            } catch (IOException ioe) {
                LOGGER.error("Unable to close InputStream. Exception in finally clause.", ioe);
            }
        }

        return md5;
    }
    
    /**
     * Given a SHA-1 message digest in byte array form, create it's string
     * representation.
     *
     * This was taken from:
     * http://www.anyexample.com/programming/java/java_simple_class_to_compute_sha_1_hash.xml
     *
     * License permitting its use:
     * http://www.anyexample.com/license.xml
     * 
     * @param data
     * @return String representation of the message digest
     */
    public static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }
}
