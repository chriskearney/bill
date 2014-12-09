package com.comandante.ui;

import com.comandante.Bill;
import com.comandante.graph.BillGraphManager;
import com.comandante.graph.BillHttpGraph;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class BillGraphAddFrame extends JFrame {

    private final BillGraphManager billGraphManager;
    private static final Logger log = LogManager.getLogger(BillGraphAddFrame.class);

    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel graphUrlLabel;
    private JTextField graphUrl;
    private JLabel graphTitleLabel;
    private JTextField graphTitle;
    private JTextField reloadInterval;
    private JTextField graphTimezone;
    private JTextField graphDuration;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;

    public BillGraphAddFrame(BillGraphManager billGraphManager) {
        this.billGraphManager = billGraphManager;
        initComponents();
    }

    private void initComponents() {
        setTitle("Add Graph");
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        graphUrlLabel = new JLabel();
        String clipContents = null;
        try {
            String c = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            if (c != null && c.contains("/render")) {
                clipContents = c;
            }
        } catch (Exception e) {
            log.error("Problem obtaining clipboard.", e);
        }
        if (clipContents != null) {
            graphUrl = new JTextField(clipContents);
        } else {
            graphUrl = new JTextField();
        }
        graphTitleLabel = new JLabel();
        graphTitle = new JTextField();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (graphUrl.getText().length() > 0) {
                    graphTitle.requestFocus();
                } else {
                    graphUrl.requestFocus();
                }
            }
        });
        reloadInterval = new JTextField();
        graphTimezone = new JTextField();
        graphDuration = new JTextField();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        CellConstraints CC = new CellConstraints();

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.createEmptyBorder("9dlu, 9dlu, 9dlu, 9dlu"));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FormLayout(
                        "37dlu, $lcgap, 160dlu",
                        "2*(default, $lgap), default"));

                //---- graphUrlLabel ----
                graphUrlLabel.setText("url");
                contentPanel.add(graphUrlLabel, CC.xy(1, 1, CC.CENTER, CC.DEFAULT));
                contentPanel.add(graphUrl, CC.xy(3, 1));

                //---- graphTitleLabel ----
                graphTitleLabel.setText("title");
                contentPanel.add(graphTitleLabel, CC.xy(1, 3, CC.CENTER, CC.DEFAULT));
                contentPanel.add(graphTitle, CC.xy(3, 3));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(Borders.createEmptyBorder("4dlu, 0dlu, 0dlu, 0dlu"));
                buttonBar.setLayout(new FormLayout(
                        "$glue, $button, $rgap, $button",
                        "pref"));

                //---- okButton ----
                okButton.setText("OK");
                buttonBar.add(okButton, CC.xy(2, 1));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                buttonBar.add(cancelButton, CC.xy(4, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        okButton.addActionListener(new OKButtonActionListener(this));
        getRootPane().setDefaultButton(okButton);
        cancelButton.addActionListener(new CancelButtonListener(this));
    }


    class CancelButtonListener implements ActionListener {
        private final BillGraphAddFrame frame;

        public CancelButtonListener(BillGraphAddFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            frame.dispose();
        }
    }

    class OKButtonActionListener implements ActionListener {
        private final BillGraphAddFrame frame;

        public OKButtonActionListener(BillGraphAddFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    BillHttpGraph httpGraph = new BillHttpGraph();
                    httpGraph.setHeight(Bill.DEFAULT_HEIGHT);
                    httpGraph.setWidth(Bill.DEFAULT_WIDTH);
                    httpGraph.setTitle(graphTitle.getText());
                    httpGraph.setGraphUrl(graphUrl.getText());
                    httpGraph.setRefreshRate(60);
                    billGraphManager.addNewGraph(httpGraph);
                }
            });
            frame.setVisible(false);
            frame.dispose();
        }
    }
}
