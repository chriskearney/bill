package com.comandante;

import com.comandante.http.BillHttpClient;
import com.comandante.ui.BillGraphDisplayFrame;
import com.google.common.util.concurrent.AbstractScheduledService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class BillGraphRefresher extends AbstractScheduledService {

    private final BillHttpClient billHttpClient;
    private final BillGraph billGraph;
    private final int reload;
    private BillGraphDisplayFrame billGraphDisplayFrame;
    private final BillGraphManager billGraphManager;
    private static final Logger log = LogManager.getLogger(BillGraphManager.class);

    public BillGraphRefresher(BillGraphManager billGraphManager, BillGraph billGraph, int reload) {
        this.billGraphManager = billGraphManager;
        this.billHttpClient = new BillHttpClient();
        this.billGraph = billGraph;
        this.reload = reload;
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
        if (is != null) {
            billGraphDisplayFrame.updateImagePanel(is, billGraph);
        }
        log.debug("Graph refresh thread completed for graph id: " + billGraph.getId());
    }

    @Override
    protected Scheduler scheduler() {
        return Scheduler.newFixedDelaySchedule(0, reload, TimeUnit.SECONDS);
    }

    public BillGraph getBillGraph() {
        return billGraph;
    }

}
