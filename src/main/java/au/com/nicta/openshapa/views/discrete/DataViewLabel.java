package au.com.nicta.openshapa.views.discrete;

import au.com.nicta.openshapa.db.DataValue;
import au.com.nicta.openshapa.util.JMultilineLabel;
import au.com.nicta.openshapa.util.UIConfiguration;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.SwingUtilities;

/**
 *
 * @author FGA
 */
public class DataViewLabel extends JMultilineLabel implements MouseListener {
  //protected static FloatEditor     floatEditor     = new FloatEditor();
  //protected static IntegerEditor   integerEditor   = new IntegerEditor();
  //protected static MatrixEditor    matrixEditor    = new MatrixEditor();
  //protected static NominalEditor   nominalEditor   = new NominalEditor();
  //protected static PredicateEditor predicateEditor = new PredicateEditor();
  //protected static StringEditor    stringEditor    = new StringEditor();
  //protected static TimeStampEditor timeStampEditor = new TimeStampEditor();

  protected DataValue value     = null;
  protected boolean   editable  = false;
  protected boolean   isdata    = false;
  protected boolean   wrap      = false;
  protected int       maxWidth  = -1;
  protected int       maxHeight = -1;

  protected static UIConfiguration uiconfig = new UIConfiguration();

  /**
   * Creates a new instance of DataViewLabel
   */
  public DataViewLabel(DataValue  dv,
                       boolean    editable,
                       boolean    isData,
                       boolean    wrap)
  {
    this.setValue(dv);
    this.setEditable(editable);
    this.addMouseListener(this);
    this.isdata = isData;
    if (this.isdata) {
      this.setJustified(true);
    }
    this.wrap = wrap;
    this.setOpaque(false);
  } // End of DataViewLabel Constructor

  public DataViewLabel(DataValue dv, boolean editable, boolean isData)
  {
    this(dv, editable, isData, false);
  } // End of DataViewLabel Constructor

  public DataViewLabel(DataValue dv, boolean editable)
  {
    this(dv, editable, false);
  } // End of DataViewLabel Constructor

  public DataViewLabel(DataValue dv)
  {
    this(dv, false, false);
  } // End of DataViewLabel Constructor

  public void setValue(DataValue dv)
  {
    this.value = dv;

    this.updateStrings();
    this.repaint();
  }

  public void setWrapWidth(int width)
  {
    this.setMaxWidth(width);
  }

  public DataValue getValue()
  {
    return (this.value);
  }

  public void setEditable(boolean value)
  {
    this.editable = value;
  }

  public boolean isEditable()
  {
    return(this.editable);
  }

  public void paintComponent(Graphics g)
  {
    if (this.isdata) {
      this.setFont(uiconfig.spreadsheetDataFont);
    } else {
      this.setFont(uiconfig.spreadsheetTimeStampFont);
    }
    this.setForeground(uiconfig.spreadsheetForegroundColor);

    super.paintComponent(g);
  }

  public void updateStrings()
  {
    if (this.value != null) {
      String t = this.value.toString();
      this.setText(t);
      this.setToolTipText(this.value.toString());
    }
  }

  public void redispathMouseEvent(MouseEvent me)
  {
    Container container = this.getParent();

    Point containerPoint =
        SwingUtilities.convertPoint(this,
                                    me.getPoint(),
                                    container);

    container.dispatchEvent(new MouseEvent(container,
                                           me.getID(),
                                           me.getWhen(),
                                           me.getModifiers(),
                                           containerPoint.x,
                                           containerPoint.y,
                                           me.getClickCount(),
                                           me.isPopupTrigger()));
  }

  public void mouseEntered(MouseEvent me)
  {
    this.redispathMouseEvent(me);
  }

  public void mouseExited(MouseEvent me)
  {
    this.redispathMouseEvent(me);
  }

  public void mousePressed(MouseEvent me)
  {
    if (!this.isEditable()) {
      this.redispathMouseEvent(me);
    }
  }

  public void mouseReleased(MouseEvent me)
  {
    if (!this.isEditable()) {
      this.redispathMouseEvent(me);
    }
  }

  public void mouseClicked(MouseEvent me)
  {
    /*
    if (this.isEditable() && (this.value != null)) {
      Editor e = this.stringEditor;

      if (this.value instanceof FloatDataValue) {
        e = this.floatEditor;
      } else if (this.value instanceof IntDataValue) {
        e = this.integerEditor;
      } else if (this.value instanceof NominalDataValue) {
        e = this.nominalEditor;
      } else if (this.value instanceof TextStringDataValue) {
        e = this.stringEditor;
      } else if (this.value instanceof TimeStampDataValue) {
        e = this.timeStampEditor;
      } else if (this.value instanceof PredDataValue) {
        e = this.predicateEditor;
      } else {
        return;
      }

      e.editValue(this);
    } else {
      this.redispathMouseEvent(me);
    }
     */
  }

  /*
  public FloatEditor getFloatEditor()
  {
    return (this.floatEditor);
  }

  public IntegerEditor getIntegerEditor()
  {
    return (this.integerEditor);
  }

  public MatrixEditor getMatrixEditor()
  {
    return (this.matrixEditor);
  }

  public NominalEditor getNominalEditor()
  {
    return (this.nominalEditor);
  }

  public PredicateEditor getPredicateEditor()
  {
    return (this.predicateEditor);
  }

  public StringEditor getStringEditor()
  {
    return (this.stringEditor);
  }

  public TimeStampEditor getTimeStampEditor()
  {
    return (this.timeStampEditor);
  }*/

  /*
  public final static void main(String[] args)
  {
    try {
      JFrame jf = new JFrame();
      jf.setLayout(new BorderLayout());
      jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);

      TimeStampDataValue tsdv = new TimeStampDataValue((Database)null);
      TimeStamp ts = new TimeStamp(60, 60);
      tsdv.setItsValue(ts);
      DataViewLabel dvl = new DataViewLabel(tsdv);
      dvl.setEditable(true);
      jf.getContentPane().add(dvl, BorderLayout.CENTER);
      jf.setSize(new Dimension(100,20));


      jf.setVisible(true);
      //dvl.setMaxWidth(50);
    } catch (Exception e) {
      System.err.println("An exception occurred: " + e);
      e.printStackTrace();
      System.exit(-1);
    }
  }
   */

} //End of DataViewLabel class definition
