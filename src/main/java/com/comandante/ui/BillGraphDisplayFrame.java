package com.comandante.ui;

import com.comandante.BillGraph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class BillGraphDisplayFrame extends JFrame {

    private BillGraphDisplayPanel billGraphDisplayPanel;
    private BillGraph billGraph;

    public BillGraphDisplayFrame(InputStream is, BillGraph billGraph) throws IOException {
        billGraphDisplayPanel = new BillGraphDisplayPanel(ImageIO.read(is));
        add(billGraphDisplayPanel);
        setVisible(true);
        setSize(new Dimension(billGraph.getWidth(), billGraph.getHeight()));
        setTitle(billGraph.getTitle());
    }

    public void updateImagePanel(InputStream is) throws IOException {
        BillGraphDisplayPanel newPanel = new BillGraphDisplayPanel(ImageIO.read(is));
        this.add(newPanel);
        this.remove(billGraphDisplayPanel);
        this.billGraphDisplayPanel = newPanel;
        this.repaint();
    }
}

