/*
 * Configuration.java
 *
 * Created on January 11, 2007, 1:28 PM
 *
 */

package au.com.nicta.openshapa.util;

import java.io.*;
import java.util.*;
import java.net.*;
import au.com.nicta.openshapa.db.Cell;
import au.com.nicta.openshapa.db.Column;
import au.com.nicta.openshapa.db.Database;
import au.com.nicta.openshapa.db.FormalArgument;
import au.com.nicta.openshapa.db.VocabElement;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;

/**
 * Configuration loading module.
 * @author FGA
 */
public class Configuration extends DefaultHandler
{
  public final static String DISCRETE_DISPLAY_PATH = "OpenSHAPAConfig.modules.discreteDisplay";
  public final static String DISCRETE_MANAGER_PATH = "OpenSHAPAConfig.modules.discreteManager";
  public final static String CONTINUOUS_DISPLAY_PATH = "OpenSHAPAConfig.modules.continuousDisplay";
  public final static String PLUGIN_PATH = "OpenSHAPAConfig.modules.plugin";

  public final static int UNKNOWN            = 0;
  public final static int DISCRETEMANAGER    = 1;
  public final static int CONTINUOUSDISPLAY  = 2;
  public final static int DISCRETEDISPLAY    = 3;
  public final static int PLUGIN             = 4;

  protected static File   prevDir = new File(File.pathSeparator);
  protected File          configFile = new File(File.pathSeparator);
  protected InputStream   inStream = null;

  protected StringBuffer  stateSB = new StringBuffer();
  protected StringBuffer  value = new StringBuffer();
  protected Attributes    attributes;

  protected OpenHashtable<String, Vector<ConfigurationObject>> configMap = new OpenHashtable<String, Vector<ConfigurationObject>>();
  protected int elementCount = 0;

  protected Stack<ConfigurationObject> parentStack = new Stack<ConfigurationObject>();

  protected Vector<ConfigurationObject> topLevelObjects = new Vector<ConfigurationObject>();

  /**
   * Creates a new instance of Configuration
   * @param configPath the Configuration file to load
   */
  public Configuration(String configPath)
  {
    this(new File(configPath));
  } //End of Configuration constructor


  /**
   * Creates a new instance of Configuration
   * @param configFile the Configuration file to load
   */
  public Configuration(File configFile)
  {
    this.configFile = new File(configFile.getAbsolutePath());
    // Use the default (non-validating) parser
    SAXParserFactory factory = SAXParserFactory.newInstance();
    try {
      // Parse the input
      this.inStream = new FileInputStream(this.configFile);
      SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(this.configFile, this);
    } catch (Throwable t) {
      t.printStackTrace();
    }
  } //End of Configuration constructor

  /**
   * Creates a new instance of Configuration
   * @param is Stream containing configuration file to load
   */
  public Configuration(InputStream is)
  {
    this.inStream = is;

    // Use the default (non-validating) parser
    SAXParserFactory factory = SAXParserFactory.newInstance();
    try {
      // Parse the input
      SAXParser saxParser = factory.newSAXParser();
      saxParser.parse(this.inStream, this);
    } catch (Throwable t) {
      t.printStackTrace();
    }
  } //End of Configuration constructor


  /**
   * Prompts user for a Configuration file and creates a new instance of a
   * Configuration object.
   * JFileChooser opens independantely from frame
   * @see Configuration#loadConfiguration(javax.swing.JFrame)
   */
  public final static Configuration loadConfiguration()
  {
    return(loadConfiguration(null));
  } //End of loadConfiguration() method


  /**
   * Prompts user for a Configuration file and creates a new instance of a
   * Configuration object.
   * @param frame The parent frame for the JFileChooser to open up from
   */
  public final static Configuration loadConfiguration(javax.swing.JFrame frame)
  {
    javax.swing.JFileChooser jfc = new javax.swing.JFileChooser(prevDir);
    jfc.setDialogTitle("Select your Configuration file.");
    jfc.setMultiSelectionEnabled(false);
    jfc.setFileSelectionMode(jfc.FILES_ONLY);
    jfc.setFileFilter(new javax.swing.filechooser.FileFilter() {
      public boolean accept(File file)
      {
        return (file.getName().toLowerCase().endsWith(".xml"));
      }
      public String getDescription()
      {
        return ("XML Files");
      }
    });
    int status = jfc.showOpenDialog(frame);
    if (status == jfc.APPROVE_OPTION) {
      File f = jfc.getSelectedFile();
      prevDir = f.getParentFile();
      Configuration config = new Configuration(f);
      return(config);
    } else {
      return (null);
    }
  } //End of loadConfiguration() method


  /**
   * Overides DefaultHandler startElement method to parse XML Objects
   */
  public void startElement(String uri, String localName, String qName, Attributes attributes)
  {
    if (this.stateSB.length() > 0) {
      this.stateSB.append(".");
    }
    this.stateSB.append(qName);
    this.value.delete(0, this.value.length());
    this.attributes = attributes;
    ConfigurationObject parent = null;
    if (!this.parentStack.empty()) {
      parent = (ConfigurationObject)this.parentStack.peek();
    }
    ConfigurationObject co =
        new ConfigurationObject(this, parent, qName, "", attributes);
    this.parentStack.push(co);
    if (parent == null) {
      this.topLevelObjects.addElement(co);
    }
  } //End of startElement() method

  /**
   * Overides DefaultHandler characters method to parse XML Objects
   */
  public void characters(char[] ch, int start, int length)
  {
    this.value.append(ch, start, length);
    ConfigurationObject co = (ConfigurationObject)this.parentStack.peek();
    co.setValue(this.value.toString().trim());
  } //End of characters() method


  /**
   * Overides DefaultHandler endElement method to parse XML Objects
   */
  public void endElement(String uri, String localName, String qName)
  {
    Vector<ConfigurationObject> v = this.configMap.get(this.stateSB.toString());
    if (v == null) {
      v = new Vector<ConfigurationObject>();
    }

    ConfigurationObject co = (ConfigurationObject)this.parentStack.pop();
    v.addElement(co);
    this.configMap.remove(this.stateSB.toString());
    this.configMap.put(this.stateSB.toString(), v);
    if (!this.parentStack.isEmpty()) {
      ((ConfigurationObject)this.parentStack.peek()).addElement(co);
    }

    int start = this.stateSB.length()-(qName.length()+1);
    int end = this.stateSB.length();
    if (this.stateSB.length() == qName.length()) {
      start++;
    }
    this.stateSB.delete(start, end);
    this.elementCount++;
  } //End of endElement() method



  /**
   * Loads a string file path into the classpath using a URLClassLoader
   * @param path the path to load
   * @see Configuration#loadPath(URL)
   */
  public static void loadPath(String path)
    throws IOException
  {
    File f = new File(path);
    loadPath(f);
  } //End of loadPath() method

  /**
   * Loads a file path into the classpath using a URLClassLoader
   * @param path the file path to load
   * @see Configuration#loadPath(URL)
   */
  public static void loadPath(File path)
    throws IOException
  {
    loadPath(path.toURL());
  } //End of loadPath() method

  /**
   * Loads a url into the classpath using a URLClassLoader
   * @param path the url to load
   * @throws IOException from the URLClassLoader
   */
  public static void loadPath(URL path)
    throws IOException
  {
    URL urls[] = new URL[]{path};
    ClassLoader aCL = Thread.currentThread().getContextClassLoader();
    URLClassLoader aUrlCL = new URLClassLoader(urls, aCL);

    Thread.currentThread().setContextClassLoader(aUrlCL);
  } //End of loadPath method


    /**
   * Loads the object's classpath into main classpath and creates an instance
   * of the object's class.  Using the type field checks the objects type
   * against the superclass interfaces for those types.
   * @throws IOException from {@link Configuration#loadPath(URL)} calls
   * @throws ClassNotFoundException from {@link java.lang.Class#forName(String)} call
   * @throws InstantiationException from {@link java.lang.Class#newInstance()} call
   * @throws IllegalAccessException from {@link java.lang.Class#newInstance()} call
   * @throws ClassCastException if class instance does not match object type
   */
  public Object getElementInstance(ConfigurationObject co)
    throws IOException, ClassNotFoundException,
           InstantiationException, IllegalAccessException,
           ClassCastException
  {
    int type = UNKNOWN;

    if (co.pathString().equalsIgnoreCase(this.DISCRETE_DISPLAY_PATH)) {
      type = this.DISCRETEDISPLAY;
    } else if (co.pathString().equalsIgnoreCase(this.DISCRETE_MANAGER_PATH)) {
      type = this.DISCRETEMANAGER;
    } else if (co.pathString().equalsIgnoreCase(this.CONTINUOUS_DISPLAY_PATH)) {
      type = this.CONTINUOUSDISPLAY;
    } else if (co.pathString().equalsIgnoreCase(this.PLUGIN_PATH)) {
      type = this.PLUGIN;
    } else {
      throw (new ClassCastException("Unknown Data Type: " + co.getName()));
    }

    String classname = co.getElement("class").getValue();
    String classpath = co.getElement("path").getValue();

    // Load classpath
    this.loadPath(classpath);

    // Create the object
    Class c = Class.forName(classname);
    Object o = c.newInstance();

    // Check for valid object type
    switch (type) {
      case DISCRETEDISPLAY: {
            au.com.nicta.openshapa.views.discrete.DiscreteDataViewer dv = (au.com.nicta.openshapa.views.discrete.DiscreteDataViewer)o;
        break;
      }
      case PLUGIN: {
        au.com.nicta.openshapa.plugin.Plugin pl = (au.com.nicta.openshapa.plugin.Plugin)o;
        break;
      }
      default: {
        throw (new ClassCastException("Unknown Data Type"));
      }
    }

    return (o);
  } //End of getInstance() method





  /**
   * Returns the ConfigurationObjects found in the Configuration file
   */
  @SuppressWarnings("unchecked")
  public ConfigurationObject[] getAllElements()
  {
    if (this.elementCount <= 0) {
      return(null);
    }

    Vector<ConfigurationObject>[] va = new Vector[this.configMap.size()];
    Enumeration en = this.configMap.elements();
    for (int i=0; en.hasMoreElements(); i++) {
      va[i] = (Vector<ConfigurationObject>)en.nextElement();
    }
    ConfigurationObject[] coa = new ConfigurationObject[this.elementCount];
    for (int i=0,j=0; i<this.elementCount; i++) {
      en = va[i].elements();
      while(en.hasMoreElements()) {
        coa[j] = (ConfigurationObject)en.nextElement();
        j++;
      }
    }

    return(coa);
  } //End of getAllElements() method

  /**
   *  Returns the number of total elements in configuration
   */
  public int getTotalElementCount()
  {
    return (this.elementCount);
  }

  /**
   *  Returns the number of root elements in configuration
   */
  public int getRootElementCount()
  {
    return (this.topLevelObjects.size());
  }

  /**
   * Gets all root elements
   */
  public ConfigurationObject[] getRootElements()
  {
    if (this.topLevelObjects.size() <= 0) {
      return (null);
    }
    ConfigurationObject[] coa = new ConfigurationObject[this.topLevelObjects.size()];

    for (int i=0; i<this.topLevelObjects.size(); i++) {
      coa[i] = this.topLevelObjects.elementAt(i);
    }

    return (coa);
  } //End of getRootElements

  /**
   * Gets all elements of matching type
   */
  public ConfigurationObject[] getElements(String type)
  {
    Vector<ConfigurationObject> cov = this.configMap.get(type);

    if (cov == null) {
      return (null);
    }

    if (cov.size() <= 0) {
      return (null);
    }

    ConfigurationObject[] coa = new ConfigurationObject[cov.size()];
    for (int i=0; i<coa.length; i++) {
      coa[i] = cov.elementAt(i);
    }

    return (coa);
  } //End of getElements()

  /**
   * Gets the discrete viewer Configuration objects
   */
  public ConfigurationObject[] getDiscreteManagers()
  {
    return(this.getElements(this.DISCRETE_MANAGER_PATH));
  } //End of getDiscreteManagers() method


  /**
   * Gets the discrete viewer Configuration objects
   */
  public ConfigurationObject[] getDiscreteViewers()
  {
    return(this.getElements(this.DISCRETE_DISPLAY_PATH));
  } //End of getDiscreteViewers() method


  /**
   * Gets the continuous viewer Configuration objects
   */
  public ConfigurationObject[] getContinuousViewers()
  {
    return(this.getElements(this.CONTINUOUS_DISPLAY_PATH));
  } //End of getContinuousViewers() method


  /**
   * Gets the plugin Configuration objects
   */
  public ConfigurationObject[] getPlugins()
  {
    return(this.getElements(this.PLUGIN_PATH));
  } //End of getPlugins() method


  /**
   * Main method used for testing purposes
   */
  /*
  public final static void main(String[] argv)
  {
    Configuration conf = Configuration.loadConfiguration();
    if (conf == null) {
      return;
    }
    System.out.println("Number of Elements At Root: " + conf.getRootElementCount());
    System.out.println("Total Elements Count: " + conf.getTotalElementCount());
    if (conf.getRootElements() == null) {
      return;
    }
    ConfigurationObject[] coa = conf.getRootElements();
    for (int i=0; i<coa.length; i++) {
      System.out.println(coa[i].dumpString());
    }

    System.out.println("\n\nDiscrete Managers:");
    coa = conf.getDiscreteManagers();
    if (coa != null) {
      for (int i=0; i<coa.length; i++) {
        System.out.println(coa[i].dumpString());
      }
    }

    System.out.println("\n\nDiscrete Viewers:");
    coa = conf.getDiscreteViewers();
    if (coa != null) {
      for (int i=0; i<coa.length; i++) {
        System.out.println(coa[i].dumpString());
      }
    }

    System.out.println("\n\nContinuous Viewers:");
    coa = conf.getContinuousViewers();
    if (coa != null) {
      for (int i=0; i<coa.length; i++) {
        System.out.println(coa[i].dumpString());
      }
    }

    System.out.println("\n\nPlugins:");
    coa = conf.getPlugins();
    if (coa != null) {
      for (int i=0; i<coa.length; i++) {
        System.out.println(coa[i].dumpString());
      }
    }

  } //End of main() method
   */

} //End of Configuration class definition
