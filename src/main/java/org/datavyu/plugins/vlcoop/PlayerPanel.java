/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Copyright 2009, 2010 Caprica Software Limited.
 */

package org.datavyu.plugins.vlcoop;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.TransferHandler;
import javax.swing.border.LineBorder;

import uk.co.caprica.vlcj.binding.LibVlcConst;
import uk.co.caprica.vlcj.oop.component.OutOfProcessMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.videosurface.ComponentIdVideoSurface;

import com.sun.jna.Native;
import java.awt.Canvas;

public class PlayerPanel extends JPanel {

    private final Canvas canvas;

    private OutOfProcessMediaPlayerComponent wrapper;

    private CardLayout cardLayout;

    private JPanel container;

    private JSlider volumeSlider;
    
    public PlayerPanel() {
        setBorder(new LineBorder(Color.gray, 2));
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBackground(Color.BLACK);

        add(buttonsPanel, BorderLayout.SOUTH);

        JPanel audioPanel = new JPanel();
        audioPanel.setBackground(Color.black);
        audioPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        audioPanel.setLayout(new BorderLayout());
        volumeSlider = new JSlider(JSlider.VERTICAL);
        volumeSlider.setMaximum(LibVlcConst.MAX_VOLUME);
        volumeSlider.setMinimum(LibVlcConst.MIN_VOLUME);
        volumeSlider.setOpaque(false);
        audioPanel.add(volumeSlider, BorderLayout.SOUTH);
        
        add(audioPanel, BorderLayout.EAST);
        
	System.out.println("MAKING NEW CANVAS");
        canvas = new Canvas();
        canvas.setBackground(Color.BLACK);
		System.out.println("DONE NEW CANVAS");


        cardLayout = new CardLayout();
        container = new JPanel();
        container.setBackground(Color.black);
        container.setLayout(cardLayout);

        container.add(canvas, "video");

        add(container, BorderLayout.CENTER);
//
//        previousButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                wrapper.mediaPlayer().previousChapter();
//            }
//        });
//
//        backButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                wrapper.mediaPlayer().skip(-10000);
//            }
//        });
//
//        stopButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                wrapper.mediaPlayer().stop();
//            }
//        });
//
//        pauseButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                wrapper.mediaPlayer().pause();
//            }
//        });
//
//        playButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                wrapper.mediaPlayer().play();
//            }
//        });
//
//        skipButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                wrapper.mediaPlayer().skip(10000);
//            }
//        });
//
//        nextButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                wrapper.mediaPlayer().nextChapter();
//            }
//        });
//
//        subtitlesButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                wrapper.mediaPlayer().cycleSpu();
//            }
//        });
//
//        muteButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                wrapper.mediaPlayer().mute();
//            }
//        });
        
        setTransferHandler(new FileDropTransferHandler());
    }

    public void showVideo(boolean show) {
        cardLayout.show(container, show ? "video" : "image");
    }

    public Canvas videoSurface() {
        return canvas;
    }

    public OutOfProcessMediaPlayerComponent wrapper() {
        return wrapper;
    }

    public void setWrapper(OutOfProcessMediaPlayerComponent wrapper) {
        this.wrapper = wrapper;
    }

    public void attachVideoSurface() {
        // TODO Clean up! shouldn't create a factory here
        ComponentIdVideoSurface videoSurface = new MediaPlayerFactory().newVideoSurface(Native.getComponentID(canvas));
	System.out.println("CRATED SURFACE");
//        wrapper.mediaPlayer().setVideoSurface(videoSurface);
	System.out.println("SET AS SURF");
    }
    
    private class FileDropTransferHandler extends TransferHandler {

        private final DataFlavor textFlavor;

        private FileDropTransferHandler() {
            try {
                textFlavor = new DataFlavor("text/plain;class=java.lang.String");
            }
            catch(ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public boolean canImport(TransferSupport transferSupport) {
            if(!transferSupport.isDrop()) {
                return false;
            }
            return transferSupport.isDataFlavorSupported(textFlavor);
        }

        public boolean importData(TransferSupport transferSupport) {
            if(!canImport(transferSupport)) {
                return false;
            }
            Transferable t = transferSupport.getTransferable();
            try {
                String data = (String)t.getTransferData(textFlavor);

                String[] files = data.split("\n");
                String file = files[0];

                URL u = new URL(file);

                wrapper().mediaPlayer().playMedia(URLDecoder.decode(u.getFile(), "UTF8"));
            }
            catch(UnsupportedFlavorException e) {
                return false;
            }
            catch(IOException e) {
                return false;
            }
            catch(Exception e) {
                return false;
            }
            return true;
        }
    }
}
