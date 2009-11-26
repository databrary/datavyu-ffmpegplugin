package org.openshapa.views.continuous.sound;

import java.awt.Dimension;
import javax.swing.JInternalFrame;

public class MyInternalFrame extends JInternalFrame {
        static int openFrameCount = 0;
        static final int xOffset = 300, yOffset = 30;

    public MyInternalFrame(boolean video, Dimension d) { // any value will assume video
        super("Movie",
              false,  //resizable
              false,  //closable
              false,  //maximizable
              false); //iconifiable

        setSize( (int)d.getWidth()+23, (int)d.getHeight()+23);

        //Set the window's location.
        // setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
        setLocation(-12,-27);
    }
    
    public MyInternalFrame() { // assumes equaliser
        super("Equaliser",
              false, //resizable
              false, //closable
              false, //maximizable
              false);//iconifiable

        setSize(423,423);

        //Set the window's location.
        // setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
        setLocation(-12,-27);
    }

    public MyInternalFrame(int error) { // assumes error
        super("Error",
              false, //resizable
              false, //closable
              false, //maximizable
              false);//iconifiable

        setSize(423,223);

        //Set the window's location.
        // setLocation(xOffset*openFrameCount, yOffset*openFrameCount);
        setLocation(-12,-27);
    }

    public void shove(int x, int y) {
        setLocation(x,y);
    }

}