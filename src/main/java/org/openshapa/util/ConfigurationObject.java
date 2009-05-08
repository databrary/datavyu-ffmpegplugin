/*
 * ConfigurationObject.java
 *
 * Created on June 4, 2007, 12:13 PM
 *
 */

package org.openshapa.util;

import java.util.Enumeration;
import org.openshapa.util.OpenHashtable;
import org.xml.sax.Attributes;

/*
import java.io.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import javax.xml.parsers.*;
 */


/**
 * The ConfigurationObject class is an individual object in an xml
 * configuration file.  The objects are heirarchical and maintain the
 * xml structure.  ConfigurationOjects contain links to all children
 * as well as their attributes.
 * @deprecated Should use session storage in swing application framework.
 * @author FGA
 */
public class ConfigurationObject
{
  /**
   * Root Configuration
   */
  protected Configuration        configuration = null;

  protected String               name = null;
  protected String               value = null;
  protected Attributes           attributes = null;

  /**
   * Parent Object
   */
  protected ConfigurationObject  parent = null;

  /**
   * Child mapping
   */
  protected OpenHashtable<String, ConfigurationObject> elements =
      new OpenHashtable<String, ConfigurationObject>();


  /**
   * Creates a new instance of ConfigurationObject
   * @param configuration Root configuration
   * @param parent parent element
   * @param qName element name
   * @param value value
   * @param attributes sttributes
   */
  public ConfigurationObject(Configuration        configuration,
                             ConfigurationObject  parent,
                             String               qName,
                             String               value,
                             Attributes           attributes)
  {
    this.configuration = configuration;
    this.setParent(parent);
    this.setName(qName);
    this.setValue(value);
    this.setAttributes(attributes);
  } // End of ConfigurationObject Constructor

  /**
   * Creates a new instance of ConfigurationObject with no parent element
   * @param configuration Root configuration
   * @param qName element name
   * @param value value
   * @param attributes sttributes
   */
  public ConfigurationObject(Configuration   configuration,
                             String          qName,
                             String          value,
                             Attributes      attributes)
  {
    this(configuration, null, qName, value, attributes);
  } // End of ConfigurationObject Constructor

  /**
   * Sets the configurations name
   * @param name
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /**
   * Gets the configuration name
   * @return the configuration name
   */
  public String getName()
  {
    return (this.name);
  }

  /**
   * Sets the value
   * @param value
   */
  public void setValue(String value)
  {
    this.value = value;
  }

  /**
   * Gets the value
   * @return the configuration elements value
   */
  public String getValue()
  {
    return (this.value);
  }

  /**
   * Sets the attributes list
   * @param attributes
   */
  public void setAttributes(Attributes attributes)
  {
    this.attributes = attributes;
  }

  /**
   * Gets the attributes list
   * @return the attributes list
   */
  public Attributes getAttributes()
  {
    return(this.attributes);
  }

  /**
   * Sets the parent element
   * @param parent
   */
  public void setParent(ConfigurationObject parent)
  {
    this.parent = parent;
  }

  /**
   * Gets the parent element
   * @return the arent element
   */
  public ConfigurationObject getParent()
  {
    return (this.parent);
  }

  /**
   * Adds a child elements to the configuration
   * @param element
   */
  public void addElement(ConfigurationObject element)
  {
    this.elements.put(element.getName().toLowerCase(), element);
  }

  /**
   * Returns a string representation of the configuration
   * @return string representation "name=value"
   */
  public String toString()
  {
    StringBuffer sb = new StringBuffer();
    sb.append(this.pathString());
    if (this.getValue().length() > 0) {
      sb.append("=");
      sb.append(this.getValue());
    }

    return (sb.toString());
  }

  /**
   * Returns a full heirarchical path representation string of object
   * @return heirarchical path string "root.element.element. ... .parent.object"
   */
  public String pathString()
  {
    StringBuffer sb = new StringBuffer();
    if (this.parent != null) {
      sb.append(this.parent.toString());
      sb.append(".");
    }
    sb.append(this.getName());
    return (sb.toString());
  }

  /**
   * Recursive string dump of element's heirarchy.
   * @return a formated string representaion of the objects heirarchy
   */
  public String dumpString()
  {
    return(this.dumpString(""));
  }

  /**
   * Recursive string dump of element's heirarchy.
   * @param lead text offset inserted before dumped text
   * @return a formated string representaion of the objects heirarchy
   */
  public String dumpString(String lead)
  {
    StringBuffer sb = new StringBuffer();
    sb.append(lead);
    sb.append(this.toString());

    if (this.elements.size() > 0) {
      sb.append("\n");
      sb.append(lead);
      sb.append("{");
    }

    sb.append("\n");

    Enumeration en = this.elements.elements();
    while (en.hasMoreElements()) {
      ConfigurationObject co = (ConfigurationObject)en.nextElement();
      sb.append(co.dumpString(lead+"\t"));
    }

    if (this.elements.size() > 0) {
      sb.append(lead);
      sb.append("}\n");
    }

    return (sb.toString());
  }

  /**
   * Returns the child element with the given name
   * @param name
   * @return the child element
   */
  public ConfigurationObject getElement(String name)
  {
    ConfigurationObject co = (ConfigurationObject)this.elements.get(name.toLowerCase());
    return(co);
  }

} //End of ConfigurationObject class definition
