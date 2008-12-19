// Modified from code posted by PaulFMendler based on code from J. Knudsen

package au.com.nicta.openshapa.util;

import java.awt.*;
import java.awt.font.*;
import java.text.*;
import javax.swing.*;

public class JMultilineLabel extends JComponent {
    private String text;
    private Insets margin = new Insets(1,1,1,1);
    private int maxWidth = Integer.MAX_VALUE;
    private boolean justify;
    private final FontRenderContext frc =
        new FontRenderContext(null, false, false);

    private void morph() {
        revalidate();
        repaint();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String old = this.text;
        this.text = text;
        firePropertyChange("text", old, this.text);
        if ((old == null) ? text!=null : !old.equals(text))
            morph();
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(int maxWidth) {
        if (maxWidth <= 0)
            throw new IllegalArgumentException();
        int old = this.maxWidth;
        this.maxWidth = maxWidth;
        firePropertyChange("maxWidth", old, this.maxWidth);
        if (old !=  this.maxWidth)
            morph();
    }

    public boolean isJustified() {
        return justify;
    }

    public void setJustified(boolean justify) {
        boolean old = this.justify;
        this.justify = justify;
        firePropertyChange("justified", old, this.justify);
        if (old != this.justify)
            repaint();
    }

    public Dimension getPreferredSize() {
        return paintOrGetSize(null, getMaxWidth());
    }

    public Dimension getMinimumSize() {
        //return getPreferredSize();
      Dimension d = this.getPreferredSize();
      return (new Dimension(d.width, this.getFontMetrics(this.getFont()).getHeight()));
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintOrGetSize((Graphics2D)g, getWidth());
    }

    private Dimension paintOrGetSize(Graphics2D g, int width) {
        Insets insets = getInsets();
        width -= insets.left + insets.right + margin.left + margin.right;
        float w = insets.left + insets.right + margin.left + margin.right;
        float x = insets.left + margin.left;
        float y = insets.top + margin.top;
        if (width > 0 && text != null && text.length() > 0) {
            AttributedString as = new AttributedString(getText());
            as.addAttribute(TextAttribute.FONT, getFont());
            AttributedCharacterIterator aci = as.getIterator();
            LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);
            float max = 0;
            int count = 0;
            for (count=0; lbm.getPosition() < aci.getEndIndex(); count++) {
                TextLayout textLayout = lbm.nextLayout(width);
                if (g != null && isJustified() && textLayout.getVisibleAdvance() > 0.80 * width)
                    textLayout = textLayout.getJustifiedLayout(width);
                if (g != null)
                    textLayout.draw(g, x, y + textLayout.getAscent());
                y += textLayout.getDescent() + textLayout.getLeading() + textLayout.getAscent();
                max = Math.max(max, textLayout.getVisibleAdvance());
            }
            w += max;
        }
        Dimension d =
            new Dimension((int)Math.ceil(w),
                          (int)Math.ceil(y) + insets.bottom + margin.bottom);
        return (d);
    }
}
