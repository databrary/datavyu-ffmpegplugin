/*
 * Executive.java
 *
 * Created on January 23, 2007, 2:21 PM
 *
 */

package au.com.nicta.openshapa;

import au.com.nicta.openshapa.cont.ContinuousDataViewer;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.disc.DiscreteDataViewer;
import au.com.nicta.openshapa.plugin.Plugin;
import au.com.nicta.openshapa.util.Configuration;
import au.com.nicta.openshapa.util.ConfigurationObject;
import au.com.nicta.openshapa.util.Globals;
import au.com.nicta.openshapa.util.UIConfiguration;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

/*
import au.com.nicta.openshapa.util.*;
import au.com.nicta.openshapa.db.*;
import au.com.nicta.openshapa.disc.*;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
 */

/**
 * This is the main OpenSHAPA Runtime Class
 * It launches and connects everything.
 * All exceptions not caught earlier are handled here.
 * @author FGA
 */
public class Executive implements KeyListener {

  /** The path to the configuration file. */
  public static final String DEFAULT_CONFIG_FILE = "defaults/default.oscfg.xml";

  /** The user configuration file. */
  public static final String USER_CONFIG_FILE = ".oscfg.xml";

  /** Dump log file. */
  public static final String CRASH_DUMP_FILE  = "os.crashdump.log";

  /** Tag that defines the default language. */
  public static final String LANG_TAG = "OpenSHAPAConfig.language";

  /** Path to internationalisation texts. */
  public static final String LANG_PATH = "defaults/lang/";

  protected Configuration   config     = null;
  protected Configuration   langConfig = null;
  protected UIConfiguration uiconfig   = new UIConfiguration();

  protected Database focusedDatabase = null;
  
  /**
   * Configuration Objects lists
   */
  protected Vector<Database> discreteManagers =
      new Vector<Database>();
  protected Vector<DiscreteDataViewer> discreteViewers =
      new Vector<DiscreteDataViewer>();
  protected Vector<ContinuousDataViewer> continuousViewers =
      new Vector<ContinuousDataViewer>();
  protected Vector<Plugin> plugins =
      new Vector<Plugin>();

  protected Vector<Database> openDatabases =
      new Vector<Database>();

  protected ExecutiveFrame mainFrame = null;


  protected Vector<ExecutiveKeyListener> execKeyListeners =
          new Vector<ExecutiveKeyListener>();
  protected Vector<Component> keyListenerParents =
          new Vector<Component>();


  /**
   * Creates a new instance of Executive.
   */
  public Executive() {
    // Final last chance catch block,
    // in case anything slips by uncaught
    try {
      /*
       * Get the users default configuration file if it exists otherwise
       * copy the default one to the user's home directory.
       */
      String userDir = System.getProperty("user.home");
      File userConfig = new File(userDir, USER_CONFIG_FILE);
      if (!userConfig.exists()) {
        // User configuration doesn't exist so make one from defaults
        FileOutputStream fos = new FileOutputStream(userConfig);
        ClassLoader cl = this.getClass().getClassLoader();
        InputStream is = cl.getResourceAsStream(DEFAULT_CONFIG_FILE);
        int b;
        while ((b = is.read()) != -1) {
          fos.write(b);
        }
        is.close();
        fos.close();
      }

      // Set hidden attribute if system is windows
      try {
        if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("attrib +h " + userConfig.getAbsolutePath());
            int exitVal = proc.exitValue();
            if (exitVal != 0) {
              System.err.println("Process exitValue: " + exitVal);
            }
        }
      } catch (Exception e) {
        System.err.println("An exception occurred while trying to set file permissions: "
                           + e.getMessage());
      }

      // Open configuration
      try {
        this.config = new Configuration(userConfig);
      } catch (Exception e) {
        System.err.println("An exception occurred while trying to open the configuration: "
                           + e.getMessage());
        try {
          this.config = Configuration.loadConfiguration();
        } catch (Exception ee) {
          System.err.println("An exception occurred while trying to open the configuration: "
                             + ee.getMessage());
          ee.printStackTrace();
          System.exit(-2);
        }
        if (this.config == null) {
          System.err.println("Error opening configuration!  Cannot continue!");
          e.printStackTrace();
          System.exit(-2);
        }
      }

      // Parse the configuration file for gui values
      this.uiconfig.parseConfiguration(this.config);

      // Get the language file
      String lang = "english";
      if (this.config.getElements(LANG_TAG) != null) {
        lang = (this.config.getElements(LANG_TAG))[0].getValue();
      }
      ClassLoader cl = this.getClass().getClassLoader();
      //InputStream is = cl.getResourceAsStream(LANG_PATH + lang + ".xml");
      //this.langConfig = new Configuration(is);

      // Initialize the menu bar
      this.mainFrame = new ExecutiveFrame(this);
      this.mainFrame.setVisible(true);
      this.mainFrame.setTitle(Globals.appString());

      ConfigurationObject[] coa;

      // Load Menus
/*      coa = this.config.getDiscreteManagers();
      if (coa != null) {
        for (int i=0; i<coa.length; i++) {
          this.mainFrame.addMenuItem(coa[i]);
        }
      }
 */
      coa = this.config.getDiscreteViewers();
      if (coa != null) {
        for (int i=0; i<coa.length; i++) {
          this.mainFrame.addMenuItem(coa[i]);
        }
      }
      coa = this.config.getContinuousViewers();
      if (coa != null) {
        for (int i=0; i<coa.length; i++) {
          this.mainFrame.addMenuItem(coa[i]);
        }
      }
      coa = this.config.getPlugins();
      if (coa != null) {
        for (int i=0; i<coa.length; i++) {
          this.mainFrame.addMenuItem(coa[i]);
        }
      }

    } catch (Exception e) {
      try {
        System.err.println("An uncaught error has occurred! (" +
                           e.getMessage() + ")");
        dumpExceptionLog(e);
      } catch (Exception ee) {
        System.err.println("An error occurred trying to dump log file (" +
                           ee.getMessage() + ")");
        ee.printStackTrace();
      }
      System.exit(-1);
    } //End of final chance try/catch block
  } // End of Executive Constructor


  /**
   * Dumps a log file of an exception to the users home directory
   */
  public final static void dumpExceptionLog(Exception e)
  {
    try {
        String userDir = System.getProperty("user.home");
        File logfile = new File(userDir, CRASH_DUMP_FILE);
        String nl = System.getProperty("line.separator");
        PrintWriter pw = new PrintWriter(new FileWriter(logfile));
        pw.write("********************************************************************************" + nl);
        pw.write("* OpenSHAPA Crash Dump Log File" + nl);
        pw.write("* OpenSHAPA Version: " + Globals.version() + nl);
        pw.write("* Date: " + (new java.util.Date()).toString() + nl);
        pw.write("********************************************************************************" + nl);
        pw.write("* java.class.path: " + System.getProperty("java.class.path") + nl);
        pw.write("* java.home: " + System.getProperty("java.home") + nl);
        pw.write("* java.vendor: " + System.getProperty("java.vendor") + nl);
        pw.write("* java.vendor.url: " + System.getProperty("java.vendor.url") + nl);
        pw.write("* java.version: " + System.getProperty("java.version") + nl);
        pw.write("* os.arch: " + System.getProperty("os.arch") + nl);
        pw.write("* os.name: " + System.getProperty("os.name") + nl);
        pw.write("* os.version: " + System.getProperty("os.version") + nl);
        pw.write("********************************************************************************" + nl);
        e.printStackTrace(pw);
        pw.close();
        System.err.println("A dump file has been logged to " +
            logfile.getAbsolutePath());
    } catch (Exception ee) {
    }
  }
  
  /**
   * Returns the main configuration object
   */
  public Configuration getConfiguration()
  {
    return (this.config);
  }

  /**
   * Returns the language configuration object
   */
  public Configuration getLangConfiguration()
  {
    return (this.langConfig);
  }

  /**
   * Returns the User Interface configuration object
   */
  public UIConfiguration getUIConfiguration()
  {
    return (this.uiconfig);
  }

  /**
   * Main method for handling menu clicks of all elements in configuration
   */
  public void menuClicked(ConfigurationObject co)
  {
    System.out.println(co.getElement("name").getValue());
    try {
      Object o = this.config.getElementInstance(co);
      if (o instanceof DiscreteDataViewer) {
        DiscreteDataViewer ddv = (DiscreteDataViewer)o;
        ddv.setExecutive(this);

// Temporary code for testing purposes
        ExecutiveContainer ec = new ExecutiveContainer();
        ec.setLayout(new BorderLayout());
        ec.getContentPane().add(ddv, BorderLayout.CENTER);
        ec.setSize(new Dimension(800,600));
        ec.setVisible(true);
      }
    } catch (Exception e) {
      System.err.println("Exception: " + e);
      e.printStackTrace();
    }
  }

  /**
   * Adds a database to the open database list
   */
  public void addOpenDatabase(Database db)
  {
      this.openDatabases.add(db);
  }
  
  /**
   * Removes a database from the open database list
   */
  public void removeOpenDatabase(Database db)
  {
      this.openDatabases.remove(db);
  }

  /**
   * Gets the list of open databases
   */
  public Vector<Database> getOpenDatabases()
  {
    return (this.openDatabases);
  }


  /**
   * Used to display a Database Selection Dialog
   */
  public Database selectDatabase()
  {
    if (this.openDatabases.size() <= 0) {
      return (null);
    }

    Database[] dba = new Database[this.openDatabases.size()];
    for (int i=0; i<dba.length; i++) {
      dba[i] = this.openDatabases.elementAt(i);
    }

    ConfigurationObject co[] =
        this.langConfig.getElements("OpenSHAPALang.executive.selectDatabaseDialog.title");
    String title = "Select a database";
    if (co != null) {
      title = co[0].getValue();
    }
    co =
        this.langConfig.getElements("OpenSHAPALang.executive.selectDatabaseDialog.prompt");
    String prompt = "Select a database";
    if (co != null) {
      prompt = co[0].getValue();
    }
    
    Database selection = (Database)JOptionPane.showInputDialog(
                              this.mainFrame,
                              prompt, title,
                              JOptionPane.PLAIN_MESSAGE,null,
                              dba,null);

    if (selection != null) {
      this.focusedDatabase = selection;
    }
    
    return (selection);
  }

  public void selectDatabase(Database db)
  {
    this.focusedDatabase = db;
  }

  public Database getFocusedDatabase()
  {
    return (this.focusedDatabase);
  }
  
  public ExecutiveFrame getFrame()
  {
      return (this.mainFrame);
  }
  
  ExecutiveKeyListener activeKeyListener = null;
  
  public void setActiveExecutiveKeyListener(ExecutiveKeyListener ekl)
  {
      if (!this.execKeyListeners.contains(ekl)) {
          this.addExecutiveKeyListener(ekl);
      }

      
      if (this.activeKeyListener != null) {
          this.activeKeyListener.executiveKeyControlLost();
      }
      this.activeKeyListener = ekl;
      if (ekl != null) {
          ekl.executiveKeyControlGained();
      }
  }
  
  public void addExecutiveKeyListener(ExecutiveKeyListener ekl)
  {
      if (ekl == null) {
          return;
      }
      if (!this.execKeyListeners.contains(ekl)) {
        this.execKeyListeners.add(ekl);
        if (ekl instanceof Container) {
           this.recurseAddKeyListeners((Container)ekl); 
        }
      }
  }
  
  public void removeExecutiveKeyListener(ExecutiveKeyListener ekl)
  {
      if (ekl == null) {
          return;
      }
      if (this.execKeyListeners.contains(ekl)) {
        this.execKeyListeners.remove(ekl);
        if (ekl.equals(this.activeKeyListener)) {
            ekl.executiveKeyControlLost();
        }
        if (ekl instanceof JComponent) {
           this.recurseRemoveKeyListeners((JComponent)ekl); 
        }
      }
  }
  
  protected void recurseAddKeyListeners(Container comp)
  {
      if (comp == null) {
          return;
      }
      
      if (this.keyListenerParents.contains(comp))
      {
        return;
      }
      Component[] ca = comp.getComponents();
      if (ca == null) {
          return;
      }
      
      for (int i=0; i<ca.length; i++) {
          comp.addKeyListener(this);
          this.keyListenerParents.add(comp);
          if (ca[i] instanceof Container) {
              this.recurseAddKeyListeners(comp);
          }
      }
  }
  
  protected void recurseRemoveKeyListeners(Container comp)
  {
      if (comp == null) {
          return;
      }
      if (!this.keyListenerParents.contains(comp))
      {
        return;
      }
      Component[] ca = comp.getComponents();
      if (ca == null) {
          return;
      }
      
      for (int i=0; i<ca.length; i++) {
          comp.removeKeyListener(this);
          this.keyListenerParents.remove(comp);
          if (ca[i] instanceof Container) {
              this.recurseAddKeyListeners(comp);
          }
      }
  }
  
  public void keyPressed(KeyEvent ke)
  {
    if (this.activeKeyListener != null) {
        this.activeKeyListener.executiveKeyPressed(ke);
    }
  }
    
  public void keyReleased(KeyEvent ke)
  {
    if (this.activeKeyListener != null) {
        this.activeKeyListener.executiveKeyReleased(ke);
    }
  }
    
  public void keyTyped(KeyEvent ke)
  {
      if (this.activeKeyListener != null) {
          this.activeKeyListener.executiveKeyTyped(ke);
      }
  }

  /*
  public final static void main(String argv[])
  {
    
  } //End of main() method
   */

} //End of Executive class definition
