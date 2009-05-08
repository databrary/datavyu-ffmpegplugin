/*
 * UIConfiguration.java
 *
 * Created on June 15, 2007, 8:04 PM
 *
 */

package org.openshapa.util;

import java.awt.*;

/**
 * Global class for User Interface Definitions
 * All variables in this class are static so any instantiation will result in
 * only one copy accessible throughout the application.  These parameters are
 * set in from a Configuration class.
 *
 * @deprecated Should use session storage in swing application framework.
 * @author FGA
 */
public class UIConfiguration
{
  /**
   * Application defaults
   */
  public final static String DEFAULT_TYPE  = "Arial";
  public final static int    DEFAULT_STYLE = Font.PLAIN;
  public final static int    DEFAULT_SIZE  = 10;
  public final static Font   DEFAULT_FONT  = new Font(DEFAULT_TYPE,
                                                      DEFAULT_STYLE,
                                                      DEFAULT_SIZE);
  public final static Color  DEFAULT_BACKGROUND = Color.WHITE;
  public final static Color  DEFAULT_FOREGROUND = Color.BLACK;
  public final static Color  DEFAULT_SELECTED   = Color.PINK;
  public final static Color  DEFAULT_HIGHLIGHT  = Color.CYAN;
  public final static Color  DEFAULT_BORDER     = Color.BLACK;


  /**
   * XML Configuration Path Elements
   */
  public final static String FONTTYPE   = "type";
  public final static String FONTSTYLE  = "style";
  public final static String FONTSIZE   = "size";

  public final static String MENUFONT   = "OpenSHAPAConfig.gui.menu.font";
  public final static String MENUBGCOL  = "OpenSHAPAConfig.gui.menu.backgoundColor";
  public final static String MENUFGCOL  = "OpenSHAPAConfig.gui.menu.foregoundColor";
  public final static String MENUHLCOL  = "OpenSHAPAConfig.gui.menu.highlightColor";

  public final static String SSTSFONT   = "OpenSHAPAConfig.gui.spreadsheet.timeStampFont";
  public final static String SSDATAFONT = "OpenSHAPAConfig.gui.spreadsheet.dataFont";
  public final static String SSBGCOL    = "OpenSHAPAConfig.gui.spreadsheet.backgroundColor";
  public final static String SSFGCOL    = "OpenSHAPAConfig.gui.spreadsheet.foregroundColor";
  public final static String SSSELCOL   = "OpenSHAPAConfig.gui.spreadsheet.selectedColor";
  public final static String SSHLCOL    = "OpenSHAPAConfig.gui.spreadsheet.hightlightColor";
  public final static String SSBORDCOL  = "OpenSHAPAConfig.gui.spreadsheet.borderColor";

  /**
   * Global Menu Variables
   */
  public static Font  menuFont            = DEFAULT_FONT;
  public static Color menuBackgroundColor = DEFAULT_BACKGROUND;
  public static Color menuForegroundColor = DEFAULT_FOREGROUND;
  public static Color menuHighlightColor  = DEFAULT_HIGHLIGHT;

  /**
   * Global Dialog Variables
   */
  public static Font  dialogFont            = DEFAULT_FONT;
  public static Color dialogBackgroundColor = DEFAULT_BACKGROUND;
  public static Color dialogForegroundColor = DEFAULT_FOREGROUND;

  /**
   * Spreadsheet Variables
   */
  public static Font  spreadsheetTimeStampFont    = DEFAULT_FONT;
  public static Font  spreadsheetDataFont         = DEFAULT_FONT;
  public static Color spreadsheetBackgroundColor  = DEFAULT_BACKGROUND;
  public static Color spreadsheetForegroundColor  = DEFAULT_FOREGROUND;
  public static Color spreadsheetSelectedColor    = DEFAULT_SELECTED;
  public static Color spreadsheetHightlightColor  = DEFAULT_HIGHLIGHT;
  public static Color spreadsheetBorderColor      = DEFAULT_BORDER;

  /**
   * Parses a configuration
   * @param config
   */
  public final static void parseConfiguration(Configuration config)
  {
    if (config.getElements(MENUFONT) != null) {
      menuFont = parseFont(config, MENUFONT);
    }

    if (config.getElements(MENUBGCOL) != null) {
      menuBackgroundColor = parseColor(config.getElements(MENUBGCOL)[0].getValue());
    }

    if (config.getElements(MENUFGCOL) != null) {
      menuForegroundColor = parseColor(config.getElements(MENUFGCOL)[0].getValue());
    }

    if (config.getElements(MENUHLCOL) != null) {
      menuHighlightColor = parseColor(config.getElements(MENUHLCOL)[0].getValue());
    }

    if (config.getElements(SSTSFONT) != null) {
      spreadsheetTimeStampFont = parseFont(config, SSTSFONT);
    }

    if (config.getElements(SSDATAFONT) != null) {
      spreadsheetDataFont = parseFont(config, SSDATAFONT);
    }

    if (config.getElements(SSBGCOL) != null) {
      spreadsheetBackgroundColor = parseColor(config.getElements(SSBGCOL)[0].getValue());
    }

    if (config.getElements(SSFGCOL) != null) {
      spreadsheetForegroundColor = parseColor(config.getElements(SSFGCOL)[0].getValue());
    }

    if(config.getElements(SSSELCOL) != null) {
        spreadsheetSelectedColor = parseColor(config.getElements(SSSELCOL)[0].getValue());
    }

    if (config.getElements(SSHLCOL) != null) {
      spreadsheetHightlightColor = parseColor(config.getElements(SSHLCOL)[0].getValue());
    }

    if (config.getElements(SSBORDCOL) != null) {
      spreadsheetBorderColor = parseColor(config.getElements(SSBORDCOL)[0].getValue());
    }
  }

  /**
   * Returns a font for the given element
   * @param config The configuration to get the font parameters from
   * @param element The element to search for
   * @return the font or the default font if not found
   */
  public final static Font parseFont(Configuration  config,
                                     String         element)
  {
    ConfigurationObject co = config.getElements(element)[0];

    String type = co.getElement(FONTTYPE).getValue();
    if (type == null) {
      type = DEFAULT_TYPE;
    }

    int style = parseStyle(co.getElement(FONTSTYLE).getValue());

    int size = DEFAULT_SIZE;
    try {
      size = Integer.parseInt(co.getElement(FONTSIZE).getValue());
    } catch (Exception e) {}

    return(new Font(type, style, size));
  }

  /**
   * Parses a string for font style
   * @param style string containing style (PLAIN, BOLD, ITALIC, BOLD&ITALIC"
   * @return  the font style or the default if none found
   */
  public final static int parseStyle(String style)
  {
    int s = DEFAULT_STYLE;
    if (style.toLowerCase().contains("bold")) {
      s |= Font.BOLD;
    }
    if (style.toLowerCase().contains("italic")) {
      s |= Font.ITALIC;
    }

    return (s);
  }

  /**
   * Parses a string for color information
   * @param color (black, blue, cyan, darGray, gray, green, lightGray, magenta,
   *               orange, pink, red, white, yellow) OR (#rrggbb)
   * @return the color or null if not a valid color
   */
  public final static Color parseColor(String color)
  {
    color = color.trim();

    if (color.equalsIgnoreCase("black"))
    {
      return (Color.black);
    }
    if (color.equalsIgnoreCase("blue"))
    {
      return (Color.blue);
    }
    if (color.equalsIgnoreCase("cyan"))
    {
      return (Color.cyan);
    }
    if (color.equalsIgnoreCase("darkGray"))
    {
      return (Color.darkGray);
    }
    if (color.equalsIgnoreCase("gray"))
    {
      return (Color.gray);
    }
    if (color.equalsIgnoreCase("green"))
    {
      return (Color.green);
    }
    if (color.equalsIgnoreCase("lightGray"))
    {
      return (Color.lightGray);
    }
    if (color.equalsIgnoreCase("magenta"))
    {
      return (Color.magenta);
    }
    if (color.equalsIgnoreCase("orange"))
    {
      return (Color.orange);
    }
    if (color.equalsIgnoreCase("pink"))
    {
      return (Color.pink);
    }
    if (color.equalsIgnoreCase("red"))
    {
      return (Color.red);
    }
    if (color.equalsIgnoreCase("white"))
    {
      return (Color.white);
    }
    if (color.equalsIgnoreCase("yellow"))
    {
      return (Color.yellow);
    }

    if (color.charAt(0) == '#') {
      try {
        int rr = Integer.parseInt(color.substring(1,3), 16);
        int gg = Integer.parseInt(color.substring(4,6), 16);
        int bb = Integer.parseInt(color.substring(7,9), 16);
        return (new Color(rr, gg, bb));
      } catch (Exception e) {}
    }

    return (null);
  }
} //End of UIConfiguration class definition
