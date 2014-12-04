package com.comandante;

import com.beust.jcommander.JCommander;

import javax.swing.*;
import java.io.IOException;
import java.net.URISyntaxException;

public class Bill {

    public static final int DEFAULT_WIDTH = 800;
    public static final int DEFAULT_HEIGHT = 600;

    public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        BillCommand billCommand = new BillCommand();
        new JCommander(billCommand, args);
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Bill -" + billCommand.getTitle());
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        BillGraph billGraph = BillGraph.createBillGraph(billCommand);
        GraphManager graphManager = new GraphManager();
        graphManager.addGraph(billGraph, billCommand.getReloadInterval());
    }
}
