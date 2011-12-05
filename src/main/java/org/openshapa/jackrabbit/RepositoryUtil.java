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
