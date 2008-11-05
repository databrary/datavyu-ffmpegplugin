/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package au.com.nicta.openshapa;

import java.awt.event.*;

/**
 *
 * @author Felix
 */
public interface ExecutiveKeyListener
{
    public void executiveKeyPressed(KeyEvent ke);
    public void executiveKeyReleased(KeyEvent ke);
    public void executiveKeyTyped(KeyEvent ke);
    
    public void executiveKeyControlGained();
    public void executiveKeyControlLost();
}
