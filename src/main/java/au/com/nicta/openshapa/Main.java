/*
 * Main.java
 *
 * Created on November 9, 2006, 3:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package au.com.nicta.openshapa;

/**
 *
 * @author FGA
 */
public class Main {
    
    /** Creates a new instance of Main */
    //public Main() {
    //}
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
        throws au.com.nicta.openshapa.db.SystemErrorException
    {
        // TODO code application logic here
        au.com.nicta.openshapa.db.Database.TestDatabase(System.out);
    }
    
}
