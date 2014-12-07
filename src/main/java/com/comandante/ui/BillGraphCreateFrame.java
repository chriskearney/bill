package com.comandante.ui;

import com.comandante.Bill;
import com.comandante.BillGraphManager;
import com.comandante.http.server.resource.BillHttpGraph;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/*
 * Created by JFormDesigner on Sat Dec 06 23:11:27 PST 2014
 */



/**
 * @author Chris Kearney
 */
public class BillGraphCreateFrame extends JFrame {

    private final BillGraphManager billGraphManager;


    public BillGraphCreateFrame(BillGraphManager billGraphManager) {
        this.billGraphManager = billGraphManager;
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Chris Kearney
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label3 = new JLabel();
        textField2 = new JTextField();
        scrollPane1 = new JScrollPane();
        editorPane1 = new JEditorPane();
        label1 = new JLabel();
        textField1 = new JTextField();
        label2 = new JLabel();
        textField3 = new JTextField();
        label4 = new JLabel();
        textField4 = new JTextField();
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
                        "39dlu, 36dlu, 53dlu, 4*(default), 13dlu, 46dlu, 74dlu",
                        "default, $lgap, 66dlu, $lgap, default, $lgap, 23dlu, 2*($lgap, default)"));

                //---- label3 ----
                label3.setText("title");
                contentPanel.add(label3, CC.xy(1, 1, CC.CENTER, CC.DEFAULT));
                contentPanel.add(textField2, CC.xywh(2, 1, 9, 1));

                //======== scrollPane1 ========
                {

                    //---- editorPane1 ----
                    editorPane1.setFont(editorPane1.getFont().deriveFont(editorPane1.getFont().getSize() - 4f));
                    editorPane1.setText("paste graphite graph here...");
                    scrollPane1.setViewportView(editorPane1);
                }
                contentPanel.add(scrollPane1, CC.xywh(1, 3, 10, 3));

                //---- label1 ----
                label1.setText("timezone:");
                contentPanel.add(label1, CC.xy(1, 7, CC.CENTER, CC.DEFAULT));

                //---- textField1 ----
                textField1.setText("America/Los_Angeles");
                contentPanel.add(textField1, CC.xywh(2, 7, 6, 1));

                //---- label2 ----
                label2.setText("duration");
                contentPanel.add(label2, CC.xy(1, 9, CC.CENTER, CC.DEFAULT));

                //---- textField3 ----
                textField3.setText("-48h");
                contentPanel.add(textField3, CC.xy(2, 9));

                //---- label4 ----
                label4.setText("reload interval");
                contentPanel.add(label4, CC.xy(3, 9, CC.CENTER, CC.DEFAULT));

                //---- textField4 ----
                textField4.setText("30");
                contentPanel.add(textField4, CC.xywh(4, 9, 6, 1));
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
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
        okButton.addActionListener(new OKButtonActionListener(this));
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Chris Kearney
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label3;
    private JTextField textField2;
    private JScrollPane scrollPane1;
    private JEditorPane editorPane1;
    private JLabel label1;
    private JTextField textField1;
    private JLabel label2;
    private JTextField textField3;
    private JLabel label4;
    private JTextField textField4;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

class OKButtonActionListener implements ActionListener {
        private final BillGraphCreateFrame frame;

        public OKButtonActionListener(BillGraphCreateFrame frame) {
            this.frame = frame;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            BillHttpGraph httpGraph = new BillHttpGraph();
            httpGraph.setHeight(Bill.DEFAULT_HEIGHT);
            httpGraph.setWidth(Bill.DEFAULT_WIDTH);
            httpGraph.setTitle(textField2.getText());
            httpGraph.setGraphUrl(editorPane1.getText());
            httpGraph.setTimezone(textField1.getText());
            httpGraph.setRefreshRate(Integer.parseInt(textField4.getText()));
            billGraphManager.addNewGraph(httpGraph);
            frame.setVisible(false);
            frame.dispose();
        }
    }

}
