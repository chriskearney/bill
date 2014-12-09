package com.comandante.ui;

import com.comandante.graph.BillGraph;
import com.comandante.graph.BillGraphManager;
import com.comandante.graph.BillHttpGraph;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BillGraphEditFrame extends JFrame {

    private BillGraph billGraph;
    private BillGraphManager billGraphManager;

    public BillGraphEditFrame(BillGraph billGraph, BillGraphManager billGraphManager) {
        this.billGraph = billGraph;
        this.billGraphManager = billGraphManager;
        initComponents();
    }

    private void initComponents() {

        dialogPane = new JPanel();
        contentPanel = new JPanel();
        graphTitleLabel = new JLabel();
        graphTitleField = new JTextField(billGraph.getRawTitle());
        hSpacer1 = new JPanel(null);
        graphImageLabel = new JLabel();
        graphUrlLabel = new JLabel();
        graphUrlField = new JTextField(billGraph.getGraphUrl());
        graphWidthLabel = new JLabel();
        graphWidthField = new JTextField(Integer.toString(billGraph.getWidth()));
        graphHeightLabel = new JLabel();
        graphHeightField = new JTextField(Integer.toString(billGraph.getHeight()));
        graphDurationLabel = new JLabel();
        graphDurationField = new JTextField(billGraph.getGraphDuration());
        graphReloadIntervalLabel = new JLabel();
        graphReloadIntervalField = new JTextField(Integer.toString(billGraph.getReloadInterval()));
        useLocalTimezoneCheckBox = new JCheckBox("Use Local Timezone", billGraph.isUseLocalTimezone());
        disableUnitSystemCheckBox = new JCheckBox("Disable Unit System", billGraph.isDisableUnitSystem());
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();
        setTitle("Edit Graph");

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
                        "34dlu, $lcgap, 127dlu, $lcgap, 10dlu, $lcgap, 205dlu",
                        "11*(default, $lgap), default"));

                //---- graphTitleLabel ----
                graphTitleLabel.setText("title");
                contentPanel.add(graphTitleLabel, CC.xy(1, 1, CC.CENTER, CC.DEFAULT));
                contentPanel.add(graphTitleField, CC.xy(3, 1));
                contentPanel.add(hSpacer1, CC.xywh(5, 1, 1, 10));

                //---- graphImageLabel ----
                graphImageLabel.setBorder(new EtchedBorder());
                contentPanel.add(graphImageLabel, CC.xywh(7, 1, 1, 21));

                //---- graphUrlLabel ----
                graphUrlLabel.setText("url");
                contentPanel.add(graphUrlLabel, CC.xy(1, 3, CC.CENTER, CC.DEFAULT));
                contentPanel.add(graphUrlField, CC.xy(3, 3));

                //---- graphWidthLabel ----
                graphWidthLabel.setText("width");
                contentPanel.add(graphWidthLabel, CC.xy(1, 5, CC.CENTER, CC.DEFAULT));
                contentPanel.add(graphWidthField, CC.xy(3, 5));

                //---- graphHeightLabel ----
                graphHeightLabel.setText("height");
                contentPanel.add(graphHeightLabel, CC.xy(1, 7, CC.CENTER, CC.DEFAULT));
                contentPanel.add(graphHeightField, CC.xy(3, 7));

                //---- graphDurationLabel ----
                graphDurationLabel.setText("duration");
                contentPanel.add(graphDurationLabel, CC.xy(1, 9, CC.CENTER, CC.DEFAULT));

                //---- graphDurationField ----
                graphDurationField.setColumns(4);
                contentPanel.add(graphDurationField, CC.xy(3, 9));

                //---- graphReloadIntervalLabel ----
                graphReloadIntervalLabel.setText("reload");
                contentPanel.add(graphReloadIntervalLabel, CC.xy(1, 11, CC.CENTER, CC.DEFAULT));

                //---- graphReloadIntervalField ----
                graphReloadIntervalField.setColumns(4);
                contentPanel.add(graphReloadIntervalField, CC.xy(3, 11));

                //---- useLocalTimezoneCheckBox ----
                useLocalTimezoneCheckBox.setText("Use Local Timezone");
                contentPanel.add(useLocalTimezoneCheckBox, CC.xy(3, 13));

                //---- disableUnitSystemCheckBox ----
                disableUnitSystemCheckBox.setText("Disable Unit System");
                contentPanel.add(disableUnitSystemCheckBox, CC.xy(3, 15));
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
        okButton.addActionListener(new OKButtonActionListener(this));
        getRootPane().setDefaultButton(okButton);
        pack();
        setLocationRelativeTo(getOwner());
    }

    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel graphTitleLabel;
    private JTextField graphTitleField;
    private JPanel hSpacer1;
    private JLabel graphImageLabel;
    private JLabel graphUrlLabel;
    private JTextField graphUrlField;
    private JLabel graphWidthLabel;
    private JTextField graphWidthField;
    private JLabel graphHeightLabel;
    private JTextField graphHeightField;
    private JLabel graphDurationLabel;
    private JTextField graphDurationField;
    private JLabel graphReloadIntervalLabel;
    private JTextField graphReloadIntervalField;
    private JCheckBox useLocalTimezoneCheckBox;
    private JCheckBox disableUnitSystemCheckBox;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;

    class OKButtonActionListener implements ActionListener {
        private final JFrame frame;

        public OKButtonActionListener(JFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    BillHttpGraph httpGraph = new BillHttpGraph();
                    httpGraph.setHeight(Integer.parseInt(graphHeightField.getText()));
                    httpGraph.setWidth(Integer.parseInt(graphWidthField.getText()));
                    httpGraph.setTitle(graphTitleField.getText());
                    httpGraph.setGraphUrl(graphUrlField.getText());
                    httpGraph.setUseLocalTimestamp(useLocalTimezoneCheckBox.isSelected());
                    httpGraph.setDisableUnitSystem(disableUnitSystemCheckBox.isSelected());
                    httpGraph.setRefreshRate(Integer.parseInt(graphReloadIntervalField.getText()));
                    httpGraph.setGraphDuration(graphDurationField.getText());
                    billGraphManager.replaceGraph(httpGraph, billGraph.getId());
                }
            });
            frame.setVisible(false);
            frame.dispose();
        }
    }
}
