/**
 *
 * 
 * 
 */
package au.com.nicta.openshapa.disc;

import au.com.nicta.openshapa.db.*;
import au.com.nicta.openshapa.*;
import au.com.nicta.openshapa.util.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;


/**
 *
 * @author Felix
 */
public class ODBCManager extends DiscreteDataManager implements ActionListener
{
    JMenuItem[] menuItems = new JMenuItem[5];

    public ODBCManager()
    {
    }
    
    public void setExecutive(Executive exec)
    {
        super.setExecutive(exec);
        
        // Create menu items
        ConfigurationObject[] coa = this.exec.getLangConfiguration().getElements("OpenSHAPALang.odbc.menus.new");
        this.menuItems[0] = new JMenuItem();
        this.menuItems[0].setText(coa[0].getValue());
        this.menuItems[0].addActionListener(this);

        coa = this.exec.getLangConfiguration().getElements("OpenSHAPALang.odbc.menus.open");
        this.menuItems[1] = new JMenuItem();
        this.menuItems[1].setText(coa[0].getValue());
        this.menuItems[1].addActionListener(this);

        coa = this.exec.getLangConfiguration().getElements("OpenSHAPALang.odbc.menus.save");
        this.menuItems[2] = new JMenuItem();
        this.menuItems[2].setText(coa[0].getValue());
        this.menuItems[2].addActionListener(this);

        coa = this.exec.getLangConfiguration().getElements("OpenSHAPALang.odbc.menus.saveas");
        this.menuItems[3] = new JMenuItem();
        this.menuItems[3].setText(coa[0].getValue());
        this.menuItems[3].addActionListener(this);

        coa = this.exec.getLangConfiguration().getElements("OpenSHAPALang.odbc.menus.close");
        this.menuItems[4] = new JMenuItem();
        this.menuItems[4].setText(coa[0].getValue());
        this.menuItems[4].addActionListener(this);
    }
    
    public ODBCManager(Executive exec)
    {
        super(exec);
    }

    public JMenuItem[] getSubMenus()
    {
        return (this.menuItems);
    }

    public void actionPerformed(ActionEvent ae)
    {
        if (ae.getSource().equals(this.menuItems[0])) {
            Database db = this.createDatabase();
            if (db != null) {
                this.exec.addOpenDatabase(db);
            }
        }
        if (ae.getSource().equals(this.menuItems[1])) {
            Database db = this.openDatabase();
            if (db != null) {
                this.exec.addOpenDatabase(db);
            }
        }
        if (ae.getSource().equals(this.menuItems[2])) {
            this.saveDatabase(this.exec.getFocusedDatabase());
        }
        if (ae.getSource().equals(this.menuItems[3])) {
            this.saveDatabaseAs(this.exec.getFocusedDatabase());
        }
        if (ae.getSource().equals(this.menuItems[4])) {
            this.closeDatabase(this.exec.getFocusedDatabase());
        }
    }

    public Database createDatabase()
    {
        try {
            return (new ODBCDatabase());
        } catch (SystemErrorException see)
        {
            exec.dumpExceptionLog(see);
        }

        return (null);
    }

    public Database openDatabase()
    {
        ConfigurationObject[] coa = this.exec.getLangConfiguration().getElements("OpenSHAPALang.odbc.dialogs.open");
        JFileChooser jfc = new JFileChooser();
        jfc.setDialogTitle(coa[0].getValue());
        jfc.setFileFilter(new ODBCFilenameFilter());
        jfc.setMultiSelectionEnabled(false);
        int status = jfc.showOpenDialog(this.exec.getFrame());

        ODBCDatabase db = null;

        if (status == JFileChooser.APPROVE_OPTION) {
            //ODBCDatabase.createFromFile(jfc.getSelectedFile());
        }

        return (db);
    }

    public Database saveDatabase(Database db)
    {
        return (db);
    }

    public Database saveDatabaseAs(Database db)
    {
        return (db);
    }
    
    public void closeDatabase(Database db)
    {
        this.exec.removeOpenDatabase(db);
    }

}
class ODBCFilenameFilter extends javax.swing.filechooser.FileFilter
{
    public boolean accept(File f)
    {
        if (!f.isDirectory() && f.getName().endsWith(".odbc")) {
            return (true);
        }
        
        return (false);
    }
    
    public String getDescription()
    {
        return ("");
    }
}
