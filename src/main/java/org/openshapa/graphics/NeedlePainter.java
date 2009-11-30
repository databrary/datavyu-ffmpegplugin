/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.openshapa.graphics;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

/**
 * This class paints a timing needle.
 */
public class NeedlePainter extends Component {

    private int paddingTop;
    private int paddingLeft;

    private long currentTime;
    private long windowStart;
    private long windowEnd;

    private float intervalWidth;
    private float intervalTime;

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public long getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(long windowEnd) {
        this.windowEnd = windowEnd;
    }

    public long getWindowStart() {
        return windowStart;
    }

    public void setWindowStart(long windowStart) {
        this.windowStart = windowStart;
    }    

    public float getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(float intervalTime) {
        this.intervalTime = intervalTime;
    }

    public float getIntervalWidth() {
        return intervalWidth;
    }

    public void setIntervalWidth(float intervalWidth) {
        this.intervalWidth = intervalWidth;
    }

    public void setPadding(final int paddingTop, final int paddingLeft) {
        assert(paddingTop > 0);
        assert(paddingLeft > 0);
        this.paddingTop = paddingTop;
        this.paddingLeft = paddingLeft;
    }

    @Override
    public void paint(Graphics g) {
        // Don't paint if the needle is out of the current window
        if ((currentTime < windowStart) || (windowEnd < currentTime)) {
            return;
        }

        Dimension size = this.getSize();

        g.setColor(Color.red);

        float ratio = intervalWidth / intervalTime;
        int pos = Math.round(currentTime * ratio - windowStart * ratio);

        g.drawLine(pos + paddingLeft, paddingTop, pos + paddingLeft, size.height);
        g.drawLine(pos + paddingLeft + 1, paddingTop, pos + paddingLeft + 1, size.height);

    }

}
