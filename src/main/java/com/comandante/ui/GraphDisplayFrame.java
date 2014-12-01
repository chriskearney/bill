package com.comandante.ui;

import com.comandante.BillCommand;
import com.comandante.BillMain;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

public class GraphDisplayFrame extends JFrame {

    private GraphDisplayPanel graphDisplayPanel;

    public GraphDisplayFrame(InputStream is, BillCommand billCommand) throws IOException {
        graphDisplayPanel = new GraphDisplayPanel(ImageIO.read(is));
        add(graphDisplayPanel);
        setVisible(true);
        int width = 0;
        if (billCommand.getWidth() > 0) {
            width = billCommand.getWidth();
        } else {
            width = BillMain.DEFAULT_WIDTH;
        }
        int height = 0;
        if (billCommand.getHeight() > 0) {
            height = billCommand.getHeight();
        } else {
            height = BillMain.DEFAULT_HEIGHT;
        }
        setSize(new Dimension(width, height + 20));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void updateImagePanel(InputStream is) throws IOException {
        GraphDisplayPanel newPanel = new GraphDisplayPanel(ImageIO.read(is));
        this.add(newPanel);
        this.remove(graphDisplayPanel);
        this.graphDisplayPanel = newPanel;
        this.repaint();
    }
}

