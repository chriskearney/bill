package com.comandante;

import com.beust.jcommander.JCommander;
import com.comandante.http.BillHttpClient;
import com.comandante.ui.GraphDisplayFrame;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class BillMain {

    public static final int DEFAULT_WIDTH = 800;
    public static final int DEFAULT_HEIGHT = 600;

    public static void main(String[] args) throws IOException, URISyntaxException {
        BillCommand billCommand = new BillCommand();
        new JCommander(billCommand, args);
        BillHttpClient httpClient = new BillHttpClient();
        BillGraph billGraph = createBillGraph(billCommand.getGraphUrl(), billCommand);
        InputStream is = httpClient.getBillGraph(billGraph);
        GraphDisplayFrame graphDisplayFrame = new GraphDisplayFrame(is, billCommand);
        if (billCommand.getReloadInterval() > 0) {
            GraphRefreshService graphRefreshService = new GraphRefreshService(graphDisplayFrame, httpClient, billGraph, billCommand.getReloadInterval());
            graphRefreshService.startAsync();
        }
    }

    public static BillGraph createBillGraph(String input, BillCommand billCommand) {
        BillGraph billGraph = null;
        try {
            billGraph = new BillGraph(input, billCommand);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (billCommand.getWidth() > 0) {
            billGraph.setPair("width", Integer.toString(billCommand.getWidth()));
        }
        if (billCommand.getHeight() > 0) {
            billGraph.setPair("height", Integer.toString(billCommand.getHeight()));
        }

        if (billGraph.getPair("width") == null) {
            billGraph.setPair("width", Integer.toString(DEFAULT_WIDTH));
            billCommand.setWidth(DEFAULT_WIDTH);
        }
        if (billGraph.getPair("height") == null) {
            billGraph.setPair("height", Integer.toString(DEFAULT_HEIGHT));
            billCommand.setHeight(DEFAULT_HEIGHT);
        }
        if (billCommand.getTimezone() != null) {
            billGraph.setPair("tz", billCommand.getTimezone());
        }
        return billGraph;
    }
}
