package com.comandante.ui;

import com.comandante.BillGraph;
import com.comandante.BillGraphManager;
import com.comandante.BillResizeEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;

public class BillGraphDisplayFrame extends JFrame implements KeyListener {

    private BillGraphDisplayPanel billGraphDisplayPanel;
    private final BillGraphManager billGraphManager;

    public BillGraphDisplayFrame(InputStream is, final BillGraph billGraph, final BillGraphManager billGraphManager) throws IOException {
        this.billGraphDisplayPanel = new BillGraphDisplayPanel(ImageIO.read(is));
        this.billGraphManager = billGraphManager;
        add(billGraphDisplayPanel);
        setVisible(true);
        setTitle(billGraph.getTitle());
        getContentPane().setPreferredSize(new Dimension(billGraph.getWidth(), billGraph.getHeight()));
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                billGraphManager.removeGraph(billGraph.getId());
            }
        });
        getRootPane().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                BillResizeEvent billResizeEvent = BillResizeEvent.newBuilder()
                        .setWidth(e.getComponent().getWidth())
                        .setHeight(e.getComponent().getHeight())
                        .setId(billGraph.getId())
                        .build();
                billGraphManager.resizeGraph(billResizeEvent);
            }
        });
        pack();
        com.apple.eawt.FullScreenUtilities.setWindowCanFullScreen(this, true);
        addKeyListener(this);
    }

    public void updateImagePanel(InputStream is, BillGraph billGraph) throws IOException {
        BillGraphDisplayPanel newPanel = new BillGraphDisplayPanel(ImageIO.read(is));
        this.add(newPanel);
        this.remove(billGraphDisplayPanel);
        this.billGraphDisplayPanel = newPanel;
        this.getContentPane().setPreferredSize(new Dimension(billGraph.getWidth(), billGraph.getHeight()));
        this.repaint();
        this.pack();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'f'){
            com.apple.eawt.Application.getApplication().requestToggleFullScreen(this);
        }
        if (e.getKeyChar() == 'o'){
            new BillGraphCreateFrame(billGraphManager).setVisible(true);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}

