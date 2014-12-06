package com.comandante.ui;

import com.comandante.BillGraph;
import com.comandante.BillGraphManager;
import com.comandante.BillResizeEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.io.InputStream;

public class BillGraphDisplayFrame extends JFrame {

    private BillGraphDisplayPanel billGraphDisplayPanel;

    public BillGraphDisplayFrame(InputStream is, final BillGraph billGraph, final BillGraphManager billGraphManager) throws IOException {
        billGraphDisplayPanel = new BillGraphDisplayPanel(ImageIO.read(is));
        add(billGraphDisplayPanel);
        setVisible(true);
        setSize(new Dimension(billGraph.getWidth(), billGraph.getHeight()));
        setTitle(billGraph.getTitle());
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                billGraphManager.removeGraph(billGraph.getId());
            }
        });
        addComponentListener(new ComponentAdapter() {
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
    }

    public void updateImagePanel(InputStream is) throws IOException {
        BillGraphDisplayPanel newPanel = new BillGraphDisplayPanel(ImageIO.read(is));
        this.add(newPanel);
        this.remove(billGraphDisplayPanel);
        this.billGraphDisplayPanel = newPanel;
        this.repaint();
    }
}

