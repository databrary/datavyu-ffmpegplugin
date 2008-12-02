package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.Database;
import javax.swing.JPanel;

/**
 * Default interface for all DiscreteDataViewers
 * @author FGA
 */
public abstract class DiscreteDataViewer extends JPanel {
    public abstract void setDatabase(Database db);
    public abstract Database getDatabase();
}
