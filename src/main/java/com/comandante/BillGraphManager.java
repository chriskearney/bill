package com.comandante;

import com.comandante.http.server.resource.BillHttpGraph;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.mapdb.DB;
import org.mapdb.HTreeMap;

import java.util.Map;

public class BillGraphManager {

    private HTreeMap<String, BillHttpGraph> billHttpGraphs;
    private Map<String, BillGraphRefresher> refresherMap;
    private static final String BILL_HTTP_GRAPH_DB = "billHttpGraphs";
    private final DB db;

    public BillGraphManager(DB db) {
        if (db.exists(BILL_HTTP_GRAPH_DB)) {
            billHttpGraphs = db.get(BILL_HTTP_GRAPH_DB);
        } else {
            billHttpGraphs = db.createHashMap(BILL_HTTP_GRAPH_DB).valueSerializer(new BillHttpGraphSerializer()).make();
        }
        this.db = db;
        this.refresherMap = Maps.newConcurrentMap();
    }

    public void addNewGraph(BillHttpGraph billHttpGraph) {
        BillGraph billGraph = BillGraph.createBillGraph(billHttpGraph, Optional.<String>absent());
        startGraph(billGraph, billHttpGraph.getRefreshRate());
        billHttpGraphs.put(billGraph.getId(), billHttpGraph);
        db.commit();
    }

    public void removeGraph(String id) {
        refresherMap.get(id).stopAsync();
        refresherMap.remove(id);
        billHttpGraphs.remove(id);
        db.commit();
    }

    private void startGraph(BillGraph billGraph, int reload) {
        BillGraphRefresher billGraphRefresher = new BillGraphRefresher(this, billGraph, reload);
        billGraphRefresher.startAsync();
        refresherMap.put(billGraph.getId(), billGraphRefresher);
    }

    public void generateAllGraphs() {
        for (Map.Entry<String, BillHttpGraph> next : billHttpGraphs.entrySet()) {
            if (next.getValue() != null) {
                startGraph(BillGraph.createBillGraph(next.getValue(), Optional.of(next.getKey())), next.getValue().getRefreshRate());
            }
        }
    }
}
