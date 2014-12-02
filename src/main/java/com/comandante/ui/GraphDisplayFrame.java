package com.comandante.ui;

import com.comandante.BillGraph;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.io.InputStream;

public class GraphDisplayFrame extends JFrame {

    private GraphDisplayPanel graphDisplayPanel;
    private BillGraph billGraph;

    public GraphDisplayFrame(InputStream is, BillGraph billGraph) throws IOException {
        graphDisplayPanel = new GraphDisplayPanel(ImageIO.read(is));
        add(graphDisplayPanel);
        setVisible(true);
        setSize(new Dimension(billGraph.getWidth(), billGraph.getHeight()));
        setTitle(billGraph.getTitle());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                Component c = (Component) evt.getSource();
            }
        });

    }

    public void updateImagePanel(InputStream is) throws IOException {
        GraphDisplayPanel newPanel = new GraphDisplayPanel(ImageIO.read(is));
        this.add(newPanel);
        this.remove(graphDisplayPanel);
        this.graphDisplayPanel = newPanel;
        this.repaint();
    }
}

