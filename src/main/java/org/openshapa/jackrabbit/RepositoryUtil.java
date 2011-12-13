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

import java.io.IOException;

import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.core.TransientRepository; 
import org.apache.jackrabbit.ocm.exception.RepositoryException;

/**
 * Utility class for managing JCR repositories.
 * <b>Note</b>: most of the utility methods in this class can be used only with Jackrabbit.
 *
 */
public class RepositoryUtil {

    /** Item path separator */
    public static final String PATH_SEPARATOR = "/";

    public static Repository getTrancientRepository() {
        return new TransientRepository();
    }

    /**
     * Connect to a JCR repository
     *
     * @param repository The JCR repository
     * @param user The user name
     * @param password The password
     * @return a valid JCR session
     *
     * @throws RepositoryException when it is not possible to connect to the JCR repository
     */
    public static Session login(Repository repository, String user,
            String password) throws RepositoryException {
        try {
            Session session = repository.login(new SimpleCredentials(user,
                    password.toCharArray()), null);

            return session;
        } catch (Exception e) {
            throw new RepositoryException("Impossible to login ", e);

        }

    }

    public static Session login(Repository repository, String user,
            String password, String workspace) throws RepositoryException {
        try {
            Session session = repository.login(new SimpleCredentials(user,
                    password.toCharArray()), workspace);
            return session;
        } catch (Exception e) {
            throw new RepositoryException("Impossible to login ", e);

        }

    }
}
