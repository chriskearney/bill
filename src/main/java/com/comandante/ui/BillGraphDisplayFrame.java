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
import java.io.IOException;
import java.io.InputStream;

public class BillGraphDisplayFrame extends JFrame {

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
        billGraphDisplayPanel.setComponentPopupMenu(new GraphPopUpMenu());
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new BillGraphKeyDispatcher(this));
    }

    public void updateImagePanel(InputStream is, BillGraph billGraph) throws IOException {
        BillGraphDisplayPanel newPanel = new BillGraphDisplayPanel(ImageIO.read(is));
        this.setTitle(billGraph.getTitle());
        this.add(newPanel);
        this.remove(billGraphDisplayPanel);
        this.billGraphDisplayPanel = newPanel;
        this.billGraphDisplayPanel.setComponentPopupMenu(new GraphPopUpMenu());
        this.getContentPane().setPreferredSize(new Dimension(billGraph.getWidth(), billGraph.getHeight()));
        this.repaint();
        this.pack();
    }

    class GraphPopUpMenu extends JPopupMenu {
        public GraphPopUpMenu() {
            add(new GraphEditMenu());
            add(new JMenuItem("Edit Graph"));
            JMenuItem addGraphItem = new JMenuItem("Add Graph");
            addGraphItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            new BillGraphCreateFrame(billGraphManager).setVisible(true);
                        }
                    });
                }
            });
            add(addGraphItem);
        }
    }

    class GraphEditMenu extends JMenu {
        public GraphEditMenu() {
            super("Duration");
            java.util.List<JMenuItem> durations = Lists.newArrayList();
            durations.add(new JMenuItem("-1hours"));
            durations.add(new JMenuItem("-3hours"));
            durations.add(new JMenuItem("-6hours"));
            durations.add(new JMenuItem("-12hours"));
            durations.add(new JMenuItem("-1days"));
            durations.add(new JMenuItem("-2days"));
            durations.add(new JMenuItem("-3days"));
            durations.add(new JMenuItem("-1weeks"));
            durations.add(new JMenuItem("-2weeks"));
            durations.add(new JMenuItem("-1months"));
            durations.add(new JMenuItem("-2months"));
            durations.add(new JMenuItem("-3months"));
            durations.add(new JMenuItem("-6months"));
            durations.add(new JMenuItem("-1years"));
            durations.add(new JMenuItem("-2years"));
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

    class BillGraphKeyDispatcher implements KeyEventDispatcher {

        private final BillGraphDisplayFrame graphDisplayFrame;

        public BillGraphKeyDispatcher(BillGraphDisplayFrame graphDisplayFrame) {
            this.graphDisplayFrame = graphDisplayFrame;
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (graphDisplayFrame.hasFocus()) {
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    if (e.getKeyChar() == 'f' || e.getKeyChar() == 'F') {
                        if (Bill.isOSX()) {
                            com.apple.eawt.Application.getApplication().requestToggleFullScreen(graphDisplayFrame);
                        }
                    }
                    if (e.getKeyChar() == 'a' || e.getKeyChar() == 'A') {
                        new BillGraphCreateFrame(billGraphManager).setVisible(true);
                    }
                } else if (e.getID() == KeyEvent.KEY_RELEASED) {
                    System.out.println("2test2");
                } else if (e.getID() == KeyEvent.KEY_TYPED) {
                    System.out.println("3test3");
                }
                return false;
            }
            return false;
        }
    }
}

