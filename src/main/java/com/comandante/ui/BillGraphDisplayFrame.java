package com.comandante.ui;

import com.beust.jcommander.internal.Lists;
import com.comandante.Bill;
import com.comandante.graph.BillGraph;
import com.comandante.graph.BillGraphManager;
import com.comandante.graph.BillResizeEvent;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;

public class BillGraphDisplayFrame extends JFrame implements KeyListener {

    private BillGraphDisplayPanel billGraphDisplayPanel;
    private final BillGraphManager billGraphManager;
    private final BillGraph billGraph;
    static GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getScreenDevices()[0];

    public BillGraphDisplayFrame(InputStream is, final BillGraph billGraph, final BillGraphManager billGraphManager) throws IOException {
        this.billGraphDisplayPanel = new BillGraphDisplayPanel(ImageIO.read(is));
        this.billGraphManager = billGraphManager;
        this.billGraph = billGraph;
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
        billGraphDisplayPanel.setComponentPopupMenu(new GraphEditMenu());
    }

    public void updateImagePanel(InputStream is, BillGraph billGraph) throws IOException {
        BillGraphDisplayPanel newPanel = new BillGraphDisplayPanel(ImageIO.read(is));
        this.add(newPanel);
        this.remove(billGraphDisplayPanel);
        this.billGraphDisplayPanel = newPanel;
        this.billGraphDisplayPanel.setComponentPopupMenu(new GraphEditMenu());
        this.getContentPane().setPreferredSize(new Dimension(billGraph.getWidth(), billGraph.getHeight()));
        this.repaint();
        this.pack();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == 'f') {
            if (Bill.isOSX()) {
                com.apple.eawt.Application.getApplication().requestToggleFullScreen(this);
            }
        }
        if (e.getKeyChar() == 'o') {
            new BillGraphCreateFrame(billGraphManager).setVisible(true);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    class GraphEditMenu extends JPopupMenu {
        private final JMenuItem threeHours;
        private final JMenuItem twelveHours;
        private final JMenuItem twentyFourHours;
        private final JMenuItem fortyEightHours;
        private final JMenuItem oneWeek;
        private final JMenuItem oneMonth;
        private final JMenuItem oneYear;

        public GraphEditMenu() {
            java.util.List<JMenuItem> durations = Lists.newArrayList();
            durations.add(threeHours = new JMenuItem("-3h"));
            durations.add(twelveHours = new JMenuItem("-12h"));
            durations.add(twentyFourHours = new JMenuItem("-24h"));
            durations.add(fortyEightHours = new JMenuItem("-48h"));
            durations.add(oneWeek = new JMenuItem("-1weeks"));
            durations.add(oneMonth = new JMenuItem("-1months"));
            durations.add(oneYear = new JMenuItem("-1years"));

            for (final JMenuItem item : durations) {
                item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final String duration = item.getText();
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                billGraphManager.updateGraphDuration(billGraph.getId(), duration);
                            }
                        });
                    }
                });
                add(item);
            }
        }
    }
}

