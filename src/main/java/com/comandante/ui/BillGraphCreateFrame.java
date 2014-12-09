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


public class BillGraphCreateFrame extends JFrame {

    private final BillGraphManager billGraphManager;
    private static final Logger log = LogManager.getLogger(BillGraphCreateFrame.class);

    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label1;
    private JTextField graphUrl;
    private JLabel label2;
    private JTextField graphTitle;
    private JTextField reloadInterval;
    private JLabel label3;
    private JTextField graphTimezone;
    private JLabel label4;
    private JLabel label5;
    private JTextField graphDuration;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;

    public BillGraphCreateFrame(BillGraphManager billGraphManager) {
        this.billGraphManager = billGraphManager;
        initComponents();
    }

    private void initComponents() {
        setTitle("Add Graph");
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
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
        label2 = new JLabel();
        graphTitle = new JTextField();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                graphTitle.requestFocus();
            }
        });
        reloadInterval = new JTextField();
        label3 = new JLabel();
        graphTimezone = new JTextField();
        label4 = new JLabel();
        label5 = new JLabel();
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
                        "5*(default, $lgap), default"));

                //---- label1 ----
                label1.setText("graphUrl");
                contentPanel.add(label1, CC.xy(1, 1, CC.RIGHT, CC.DEFAULT));
                contentPanel.add(graphUrl, CC.xy(3, 1));

                //---- label2 ----
                label2.setText("graphTitle");
                contentPanel.add(label2, CC.xy(1, 3, CC.RIGHT, CC.DEFAULT));
                contentPanel.add(graphTitle, CC.xy(3, 3));

                //---- reloadInterval ----
                reloadInterval.setText("30");
                contentPanel.add(reloadInterval, CC.xy(3, 5));

                //---- label3 ----
                label3.setText("reload");
                contentPanel.add(label3, CC.xy(1, 5, CC.RIGHT, CC.DEFAULT));

                //---- graphTimezone ----
                graphTimezone.setText("America/Los_Angeles");
                contentPanel.add(graphTimezone, CC.xy(3, 7));

                //---- label4 ----
                label4.setText("timezone");
                contentPanel.add(label4, CC.xy(1, 7, CC.RIGHT, CC.DEFAULT));

                //---- label5 ----
                label5.setText("duration");
                contentPanel.add(label5, CC.xy(1, 9, CC.RIGHT, CC.DEFAULT));

                //---- graphDuration ----
                graphDuration.setText("-48h");
                contentPanel.add(graphDuration, CC.xy(3, 9));
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
        private final BillGraphCreateFrame frame;

        public CancelButtonListener(BillGraphCreateFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            frame.dispose();
        }
    }

    class OKButtonActionListener implements ActionListener {
        private final BillGraphCreateFrame frame;

        public OKButtonActionListener(BillGraphCreateFrame frame) {
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
                    httpGraph.setTimezone(graphTimezone.getText());
                    httpGraph.setRefreshRate(Integer.parseInt(reloadInterval.getText()));
                    httpGraph.setGraphDuration(graphDuration.getText());
                    billGraphManager.addNewGraph(httpGraph);
                }
            });
            frame.setVisible(false);
            frame.dispose();
        }
    }
}
