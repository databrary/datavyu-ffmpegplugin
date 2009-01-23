/*
 * Editor.java
 *
 * Created on June 9, 2007, 12:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa.disc.editors;

import au.com.nicta.openshapa.db.DataValue;
import au.com.nicta.openshapa.views.discrete.DataValueView;
import javax.swing.JComponent;


/**
 *
 * @author FGA
 */
public abstract class Editor
    extends     JComponent
{
  DataValue dv = null;

  public void editValue(DataValueView dvl)
  {
    System.out.println("Editing Value: " + dvl.getValue().toString());
//    Rectangle r = dvl.getBounds();
//    this.setLocation(r.x, r.y);
//    this.dv = dvl.getValue();
//    this.setVisible(true);
//    this.paintComponent(dvl.getGraphics());
  }
}
