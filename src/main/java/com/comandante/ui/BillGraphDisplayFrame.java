package com.comandante.ui;

import com.comandante.Bill;
import com.comandante.graph.BillGraph;
import com.comandante.graph.BillGraphManager;
import com.comandante.graph.BillResizeEvent;
import com.google.common.collect.ImmutableSet;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

public class BillGraphDisplayFrame extends JFrame {

    private BillGraphDisplayPanel billGraphDisplayPanel;
    private final BillGraphManager billGraphManager;
    private BillGraph billGraph;
    private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
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

    public void updateBillGraph(BillGraph billGraph) {
        this.billGraph = billGraph;
    }

    class GraphPopUpMenu extends JPopupMenu {
        public GraphPopUpMenu() {
            add(new GraphEditMenu());
            JMenuItem editGraphItem = new JMenuItem("Edit Graph");
            editGraphItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new BillGraphEditFrame(billGraph, billGraphManager).setVisible(true);
                }
            });
            add(editGraphItem);
            JMenuItem addGraphItem = new JMenuItem("Add Graph");
            addGraphItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            new BillGraphAddFrame(billGraphManager).setVisible(true);
                        }
                    });
                }
            });
            add(addGraphItem);
            JMenuItem copyGraphUrl = new JMenuItem("Copy Graph Url");
            copyGraphUrl.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            StringSelection selection = new StringSelection(billGraph.getGraphUrl());
                            clipboard.setContents(selection, selection);
                        }
                    });
                }
            });
            add(copyGraphUrl);
        }
    }

    private static Set<String> validDurations = ImmutableSet.<String>builder()
            .add("-1hours")
            .add("-3hours")
            .add("-6hours")
            .add("-12hours")
            .add("-1days")
            .add("-2days")
            .add("-3days")
            .add("-1weeks")
            .add("-2weeks")
            .add("-1months")
            .add("-2months")
            .add("-3months")
            .add("-6months")
            .add("-1years")
            .add("-2years")
            .build();

    class GraphEditMenu extends JMenu {

        public GraphEditMenu() {
            super("Duration");
            for (String duration : validDurations) {
                final JMenuItem jMenuItem = new JMenuItem(duration);
                jMenuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        final String duration = jMenuItem.getText();
                        EventQueue.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                billGraphManager.updateGraphDuration(billGraph.getId(), duration);
                            }
                        });
                    }
                });
                add(jMenuItem);
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
                        new BillGraphAddFrame(billGraphManager).setVisible(true);
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

