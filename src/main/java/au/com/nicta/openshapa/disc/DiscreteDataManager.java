/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.disc;

import au.com.nicta.openshapa.*;
import au.com.nicta.openshapa.db.*;

/**
 *
 * @author Felix
 */
public abstract class DiscreteDataManager implements ExecutiveMenuItem
{
    protected Executive exec = null;
    
    public DiscreteDataManager()
    {        
    }
    
    public void setExecutive(Executive exec)
    {
        this.exec = exec;
    }
    
    public DiscreteDataManager(Executive exec)
    {
        this.setExecutive(exec);
    }

    public abstract Database createDatabase();
    public abstract Database openDatabase();
    public abstract Database saveDatabase(Database db);
    public abstract Database saveDatabaseAs(Database db);
    public abstract void     closeDatabase(Database db);
}
