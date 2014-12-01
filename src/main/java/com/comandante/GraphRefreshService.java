package com.comandante;

import com.comandante.http.BillHttpClient;
import com.comandante.ui.GraphDisplayFrame;
import com.google.common.util.concurrent.AbstractScheduledService;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class GraphRefreshService extends AbstractScheduledService {

    private final GraphDisplayFrame graphDisplayFrame;
    private final BillHttpClient billHttpClient;
    private final BillGraph billGraph;
    private final int reload;

    public GraphRefreshService(GraphDisplayFrame graphDisplayFrame, BillHttpClient billHttpClient, BillGraph billGraph, int reload) {
        this.graphDisplayFrame = graphDisplayFrame;
        this.billHttpClient = billHttpClient;
        this.billGraph = billGraph;
        this.reload = reload;
    }

    @Override
    protected void runOneIteration() throws Exception {
        InputStream is = billHttpClient.getBillGraph(this.billGraph);
        graphDisplayFrame.updateImagePanel(is);
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(reload, reload, TimeUnit.SECONDS);
    }
}
