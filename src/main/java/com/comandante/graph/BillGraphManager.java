package com.comandante.graph;

import com.comandante.BillHttpGraphSerializer;
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
        startGraph(billGraph, billHttpGraph.getRefreshRate());
        billHttpGraphs.put(billGraph.getId(), billHttpGraph);
        log.info("Graph added : " + billGraph);
    }

    public void removeGraph(String id) {
        refresherMap.get(id).stopAsync();
        refresherMap.remove(id);
        billHttpGraphs.remove(id);
    }

    public void resizeGraph(BillResizeEvent event) {
        resizeService.process(event);
    }

    private void startGraph(BillGraph billGraph, int reload) {
        BillGraphRefresher billGraphRefresher = new BillGraphRefresher(this, billGraph, reload);
        billGraphRefresher.startAsync();
        refresherMap.put(billGraph.getId(), billGraphRefresher);
    }

    public void generateAllGraphsFromDisk() {
        for (Map.Entry<String, BillHttpGraph> next : billHttpGraphs.entrySet()) {
            if (next.getValue() != null) {
                startGraph(BillGraph.createBillGraph(next.getValue(), Optional.of(next.getKey())), next.getValue().getRefreshRate());
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
