package com.comandante;

import com.beust.jcommander.JCommander;

import java.io.IOException;
import java.net.URISyntaxException;

public class BillMain {

    public static final int DEFAULT_WIDTH = 800;
    public static final int DEFAULT_HEIGHT = 600;

    public static void main(String[] args) throws IOException, URISyntaxException {
        BillCommand billCommand = new BillCommand();
        new JCommander(billCommand, args);
        BillGraph billGraph = BillGraph.createBillGraph(billCommand);
        GraphRefreshService graphRefreshService = new GraphRefreshService(billGraph, billCommand.getReloadInterval());
        graphRefreshService.startAsync();
    }
}
