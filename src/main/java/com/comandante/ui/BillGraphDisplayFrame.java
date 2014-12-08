package com.comandante.ui;

import com.comandante.Bill;
import com.comandante.graph.BillGraph;
import com.comandante.graph.BillGraphManager;
import com.comandante.graph.BillResizeEvent;

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
    static GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getScreenDevices()[0];

    public BillGraphDisplayFrame(InputStream is, final BillGraph billGraph, final BillGraphManager billGraphManager) throws IOException {
        this.billGraphDisplayPanel = new BillGraphDisplayPanel(ImageIO.read(is));
        this.billGraphManager = billGraphManager;
        this.add(billGraphDisplayPanel);
        this.setVisible(true);
        this.setTitle(billGraph.getTitle());
        this.getContentPane().setPreferredSize(new Dimension(billGraph.getWidth(), billGraph.getHeight()));
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                billGraphManager.removeGraph(billGraph.getId());
            }
        });
        this.getRootPane().addComponentListener(new ComponentAdapter() {
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
        this.pack();
        if (Bill.isOSX()) {
            com.apple.eawt.FullScreenUtilities.setWindowCanFullScreen(this, true);
        }
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
            if (Bill.isOSX()) {
                com.apple.eawt.Application.getApplication().requestToggleFullScreen(this);
            }
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

