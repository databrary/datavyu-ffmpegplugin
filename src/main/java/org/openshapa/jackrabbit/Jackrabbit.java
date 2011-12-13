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
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.jdesktop.application.LocalStorage;
import org.openshapa.OpenSHAPA;

/**
 *
 * @author harold
 */
public final class Jackrabbit {

    private static final Logger LOGGER = UserMetrix.getLogger(Jackrabbit.class);
    private Repository repository = null;
    private ObjectContentManager ocm = null;
    private static Jackrabbit fINSTANCE = null;

    public static Jackrabbit getJackRabbit() {
        if (fINSTANCE == null) {
            fINSTANCE = new Jackrabbit();
        }
        return fINSTANCE;
    }

    // PRIVATE //
    /**
     * Single instance created upon class loading.
     */
    //private static final Jackrabbit fINSTANCE =  new Jackrabbit();  
    /**
     * Private constructor prevents construction outside this class.
     */
    private Jackrabbit() {
        LocalStorage ls = OpenSHAPA.getApplication().getContext().getLocalStorage();
        File repositoryDir = new File(ls.getDirectory().toString()
                + "/jackrabbit");
        if (!repositoryDir.exists()) {
            repositoryDir.mkdir();
        }
        try {
            InputStream istream = getClass().getResourceAsStream("/jackrabbit/repository.xml");
            RepositoryConfig config = RepositoryConfig.create(istream, ls.getDirectory().toString() + "/jackrabbit");
            repository = RepositoryImpl.create(config);
            Session session = RepositoryUtil.login(repository, "admin", "admin", "default");
            // Add persistent classes
            List<Class> classes = new ArrayList<Class>();
            classes.add(SpreadsheetOCM.class);
            classes.add(VariableOCM.class);
            classes.add(CellOCM.class);
            Mapper mapper = new AnnotationMapperImpl(classes);
            ocm = new ObjectContentManagerImpl(session, mapper);
        } catch (ConfigurationException ex) {
            LOGGER.error("Unable create a RepositoryConfig", ex);
        } catch (RepositoryException ex) {
            LOGGER.error("Unable to initialize the Jackrabbit repository", ex);
        }
    }

    public void push() {
        SpreadsheetOCM spreadshetOCM = new SpreadsheetOCM();
        ocm.insert(spreadshetOCM);
        ocm.save();
    }

    public void pull() {
        pull(0);
    }

    public void pull(int version) {
        SpreadsheetOCM spreadsheetOCM = (SpreadsheetOCM) ocm.getObject("/" + OpenSHAPA.getProjectController().getDB().getName());
    }

    public void shutdown() {
        ((RepositoryImpl) repository).shutdown();
        repository = null;
    }

    protected void finalize() throws Throwable {
        if (repository != null) {
            try {
                shutdown();
            } finally {
                super.finalize();
            }
        }
    }
}
