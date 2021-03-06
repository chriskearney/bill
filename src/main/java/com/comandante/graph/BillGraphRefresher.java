package com.comandante.graph;

import com.comandante.http.BillHttpClient;
import com.comandante.ui.BillGraphDisplayFrame;
import com.google.common.util.concurrent.AbstractScheduledService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class BillGraphRefresher extends AbstractScheduledService {

    private final BillHttpClient billHttpClient;
    private BillGraph billGraph;
    private BillGraphDisplayFrame billGraphDisplayFrame;
    private final BillGraphManager billGraphManager;
    private static final Logger log = LogManager.getLogger(BillGraphManager.class);

    public BillGraphRefresher(BillGraphManager billGraphManager, BillGraph billGraph) {
        this.billGraphManager = billGraphManager;
        this.billHttpClient = new BillHttpClient();
        this.billGraph = billGraph;
    }

    @Override
    protected void runOneIteration() throws Exception {
        InputStream is = null;
        try {
            is = billHttpClient.getBillGraph(this.billGraph);
            if (billGraphDisplayFrame == null) {
                billGraphDisplayFrame = new BillGraphDisplayFrame(is, billGraph, billGraphManager);
                return;
            }
        } catch (Exception e) {
            log.error("Problem loading graph from graphite with id: " + billGraph.getId(), e);
        }
        if (is != null && billGraphDisplayFrame != null) {
            billGraphDisplayFrame.updateImagePanel(is, billGraph);
        }
        log.debug("Graph refresh thread completed for graph id: " + billGraph.getId());
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0, billGraph.getReloadInterval(), TimeUnit.SECONDS);
    }

    public BillGraph getBillGraph() {
        return billGraph;
    }

    public BillGraphDisplayFrame getBillGraphDisplayFrame() {
        return billGraphDisplayFrame;
    }

    public void setBillGraphDisplayFrame(BillGraphDisplayFrame billGraphDisplayFrame) {
        this.billGraphDisplayFrame = billGraphDisplayFrame;
    }
}
