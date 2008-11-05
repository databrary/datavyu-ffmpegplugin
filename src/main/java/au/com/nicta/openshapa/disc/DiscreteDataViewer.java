/*
 * DiscreteDataViewer.java
 *
 * Created on January 17, 2007, 11:32 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.disc;

import au.com.nicta.openshapa.*;
import au.com.nicta.openshapa.db.*;
import javax.swing.*;

/**
 * Default interface for all DiscreteDataViewers
 * @author FGA
 */
public abstract class DiscreteDataViewer extends JPanel
{
  public abstract void setExecutive(Executive exec);
  public abstract void setDatabase(Database db);
  public abstract Executive getExecutive();
  public abstract Database getDatabase();
} //End of DiscreteDataViewer interface definition
