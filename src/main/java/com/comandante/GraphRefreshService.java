package com.comandante;

import com.comandante.http.BillHttpClient;
import com.comandante.ui.GraphDisplayFrame;
import com.google.common.util.concurrent.AbstractScheduledService;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class GraphRefreshService extends AbstractScheduledService {

    private final BillHttpClient billHttpClient;
    private final BillGraph billGraph;
    private final int reload;
    private GraphDisplayFrame graphDisplayFrame;

    public GraphRefreshService(BillGraph billGraph, int reload) {
        this.billHttpClient = new BillHttpClient();
        this.billGraph = billGraph;
        this.reload = reload;
    }

    @Override
    protected void runOneIteration() throws Exception {
        InputStream is = billHttpClient.getBillGraph(this.billGraph);
        if (graphDisplayFrame == null) {
            System.out.println("Creating First time Graph.");
            graphDisplayFrame = new GraphDisplayFrame(is, billGraph);
            return;
        }
        System.out.println("Updating image");
        graphDisplayFrame.updateImagePanel(is);
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0, reload, TimeUnit.SECONDS);
    }
}
