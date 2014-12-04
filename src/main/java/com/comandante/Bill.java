package com.comandante;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.internal.Maps;
import com.comandante.http.BillHttpClient;
import com.comandante.http.server.BillHttpServerApplication;
import com.comandante.http.server.BillHttpServerConfiguration;
import com.comandante.http.server.resource.BillHttpGraph;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.cli.ServerCommand;
import io.dropwizard.setup.Bootstrap;
import net.sourceforge.argparse4j.inf.Namespace;

import javax.swing.*;
import java.io.IOException;

public class Bill {

    public static final int DEFAULT_WIDTH = 800;
    public static final int DEFAULT_HEIGHT = 600;
    public static final int DEFAULT_HTTP_PORT = 32224;
    public static final int DEFAULT_HTTP_PORT_ADMIN = 32225;

    public static void main(String[] args) throws Exception {
        BillCommand billCommand = new BillCommand();
        new JCommander(billCommand, args);
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Billaaaaa" + billCommand.getTitle());
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        BillHttpClient billHttpClient = new BillHttpClient();
        if (isServerRunning(billHttpClient)) {
            ObjectMapper mapper = new ObjectMapper();
            BillHttpGraph billHttpGraph = new BillHttpGraph();
            billHttpGraph.setHeight(billCommand.getHeight());
            billHttpGraph.setWidth(billCommand.getWidth());
            billHttpGraph.setTitle(billCommand.getTitle());
            billHttpGraph.setRefreshRate(billCommand.getReloadInterval());
            billHttpGraph.setGraphUrl(billCommand.getGraphUrl());
            if (billCommand.getTimezone() != null) {
                billHttpGraph.setTimezone(billCommand.getTimezone());
            }
            String s = mapper.writeValueAsString(billHttpGraph);
            billHttpClient.createGraph("http://localhost:" + DEFAULT_HTTP_PORT + "/bill/create", s);
            System.exit(0);
        }
        BillGraph billGraph = BillGraph.createBillGraph(billCommand);
        BillGraphManager billGraphManager = new BillGraphManager();
        billGraphManager.addGraph(billGraph, billCommand.getReloadInterval());
        BillHttpServerApplication billHttpServerApplication = new BillHttpServerApplication(billGraphManager);
        Bootstrap bootstrap = new Bootstrap(billHttpServerApplication);
        ServerCommand<BillHttpServerConfiguration> serverConfigurationServerCommand = new ServerCommand<BillHttpServerConfiguration>(billHttpServerApplication);
        serverConfigurationServerCommand.run(bootstrap, new Namespace(Maps.<String, Object>newHashMap()));
    }

    private static boolean isServerRunning(BillHttpClient billHttpClient)  {
        try {
            return billHttpClient.billServerHealthCheck("http://localhost:" + DEFAULT_HTTP_PORT_ADMIN + "/healthcheck");
        } catch (IOException e) {
            return false;
        }
    }
}
