package com.comandante;

import com.comandante.http.server.resource.BillHttpGraph;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AbstractScheduledService;
import org.mapdb.DB;
import org.mapdb.HTreeMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BillGraphManager {

    private HTreeMap<String, BillHttpGraph> billHttpGraphs;
    private Map<String, BillGraphRefresher> refresherMap;
    private static final String BILL_HTTP_GRAPH_DB = "billHttpGraphs";
    private final Persister persister;
    private final DB db;

    public BillGraphManager(DB db) {
        if (db.exists(BILL_HTTP_GRAPH_DB)) {
            billHttpGraphs = db.get(BILL_HTTP_GRAPH_DB);
        } else {
            billHttpGraphs = db.createHashMap(BILL_HTTP_GRAPH_DB).valueSerializer(new BillHttpGraphSerializer()).make();
        }
        this.db = db;
        this.persister = new Persister(db);
        this.persister.startAsync();
        this.refresherMap = Maps.newConcurrentMap();
    }

    public void addGraph(BillHttpGraph billHttpGraph) {
        BillGraph billGraph = BillGraph.createBillGraph(billHttpGraph);
        startGraph(billGraph, billHttpGraph.getRefreshRate());
        billHttpGraphs.put(billGraph.getId(), billHttpGraph);
        db.commit();
    }

    public void removeGraph(String id) {
        System.out.println("Removing graph with id: " + id);
        refresherMap.get(id).stopAsync();
        refresherMap.remove(id);
        billHttpGraphs.remove(id);
        for (Map.Entry<String, BillHttpGraph> httpGraph : billHttpGraphs.entrySet()) {
            System.out.println(httpGraph.getKey());
        }
        System.out.println(billHttpGraphs.size());
        for (Map.Entry<String, BillHttpGraph> httpGraph : billHttpGraphs.entrySet()) {
            System.out.println(httpGraph.getKey());
        }
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
                addGraph(next.getValue());
            }
        }
    }

    class Persister extends AbstractScheduledService {

        private final DB db;

        public Persister(DB db) {
            this.db = db;
        }

        @Override
        protected void runOneIteration() throws Exception {
            db.commit();
        }

        @Override
        protected Scheduler scheduler() {
            return Scheduler.newFixedDelaySchedule(0, 30, TimeUnit.SECONDS);
        }
    }
}
