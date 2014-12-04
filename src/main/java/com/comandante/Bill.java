package com.comandante;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.internal.Maps;
import com.comandante.http.server.BillHttpServerApplication;
import com.comandante.http.server.BillHttpServerConfiguration;
import io.dropwizard.cli.ServerCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;

import javax.swing.*;

public class Bill {

    public static final int DEFAULT_WIDTH = 800;
    public static final int DEFAULT_HEIGHT = 600;

    public static void main(String[] args) throws Exception {
        BillCommand billCommand = new BillCommand();
        new JCommander(billCommand, args);
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Billaaaaa" + billCommand.getTitle());
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        BillGraph billGraph = BillGraph.createBillGraph(billCommand);
        BillGraphManager billGraphManager = new BillGraphManager();
        billGraphManager.addGraph(billGraph, billCommand.getReloadInterval());
        BillHttpServerApplication billHttpServerApplication = new BillHttpServerApplication(billGraphManager);
        Bootstrap bootstrap = new Bootstrap(billHttpServerApplication);
        ServerCommand<BillHttpServerConfiguration> serverConfigurationServerCommand = new ServerCommand<BillHttpServerConfiguration>(billHttpServerApplication);
        serverConfigurationServerCommand.run(bootstrap, new Namespace(Maps.<String, Object>newHashMap()));
    }
}
