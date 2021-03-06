package com.comandante.graph;

import com.comandante.BillHttpGraphSerializer;
import com.comandante.ui.BillGraphDisplayFrame;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.mapdb.DB;
import org.mapdb.HTreeMap;

import java.util.Map;

public class BillGraphManager {

    private HTreeMap<String, BillHttpGraph> billHttpGraphs;
    private Map<String, BillGraphRefresher> refresherMap;
    private static final String BILL_HTTP_GRAPH_DB = "billHttpGraphs";
    private final BillResizeService resizeService;
    private static final Logger log = LogManager.getLogger(BillGraphManager.class);

    public BillGraphManager(DB db) {
        billHttpGraphs = db.createHashMap(BILL_HTTP_GRAPH_DB)
                .valueSerializer(new BillHttpGraphSerializer())
                .makeOrGet();
        this.refresherMap = Maps.newConcurrentMap();
        this.resizeService = new BillResizeService(this);
        this.resizeService.startAsync();
    }

    public void addNewGraph(BillHttpGraph billHttpGraph) {
        BillGraph billGraph = BillGraph.createBillGraph(billHttpGraph, Optional.<String>absent());
        startGraph(billGraph);
        billHttpGraphs.put(billGraph.getId(), billHttpGraph);
        log.info("Graph added : " + billGraph);
    }

    public void removeGraph(String id) {
        refresherMap.get(id).stopAsync();
        refresherMap.remove(id);
        billHttpGraphs.remove(id);
    }

    public void replaceGraph(BillHttpGraph billHttpGraph, String id) {
        billHttpGraphs.put(id, billHttpGraph);
        BillGraph billGraph = BillGraph.createBillGraph(billHttpGraph, Optional.of(id));
        BillGraphRefresher billGraphRefresher = refresherMap.get(id);
        billGraphRefresher.stopAsync();
        billGraphRefresher.awaitTerminated();
        BillGraphDisplayFrame billGraphDisplayFrame = billGraphRefresher.getBillGraphDisplayFrame();
        billGraphDisplayFrame.updateBillGraph(billGraph);
        BillGraphRefresher newRefresher = new BillGraphRefresher(this, billGraph);
        newRefresher.setBillGraphDisplayFrame(billGraphDisplayFrame);
        newRefresher.startAsync();
        refresherMap.put(id, newRefresher);
    }

    public void resizeGraph(BillResizeEvent event) {
        resizeService.process(event);
    }

    public void updateGraphDuration(String graphId, String duration){
        BillHttpGraph billHttpGraph = billHttpGraphs.get(graphId);
        if (billHttpGraph.getGraphDuration() != null && billHttpGraph.getGraphDuration().equals(duration)) {
            log.debug("Graph duration is unchanged, skipping update.");
            return;
        }
        billHttpGraph.setGraphDuration(duration);
        billHttpGraphs.put(graphId, billHttpGraph);
        this.getRefresherMap().get(graphId).getBillGraph().updateDuration(duration);
        try {
            this.getRefresherMap().get(graphId).runOneIteration();
        } catch (Exception e) {
            log.error("Unable to update graph duration.", e);
        }
    }

    private void startGraph(BillGraph billGraph) {
        BillGraphRefresher billGraphRefresher = new BillGraphRefresher(this, billGraph);
        billGraphRefresher.startAsync();
        refresherMap.put(billGraph.getId(), billGraphRefresher);
    }

    public void generateAllGraphsFromDisk() {
        for (Map.Entry<String, BillHttpGraph> next : billHttpGraphs.entrySet()) {
            if (next.getValue() != null) {
                startGraph(BillGraph.createBillGraph(next.getValue(), Optional.of(next.getKey())));
            }
        }
    }

    public Map<String, BillGraphRefresher> getRefresherMap() {
        return refresherMap;
    }

    public HTreeMap<String, BillHttpGraph> getBillHttpGraphs() {
        return billHttpGraphs;
    }
}
