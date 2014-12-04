package com.comandante;

import com.comandante.http.server.resource.BillHttpGraph;
import com.google.common.util.concurrent.AbstractScheduledService;
import org.mapdb.DB;
import org.mapdb.HTreeMap;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class BillGraphManager {

    private HTreeMap<String, BillHttpGraph> billHttpGraphs;
    private static final String BILL_HTTP_GRAPH_DB = "billHttpGraphs";
    private final Persister persister;

    public BillGraphManager(DB db) {
        if (db.exists(BILL_HTTP_GRAPH_DB)) {
            billHttpGraphs = db.get(BILL_HTTP_GRAPH_DB);
        } else {
            billHttpGraphs = db.createHashMap(BILL_HTTP_GRAPH_DB).valueSerializer(new BillHttpGraphSerializer()).make();
        }
        this.persister = new Persister(db);
        this.persister.startAsync();
    }

    public void addGraph(BillHttpGraph billHttpGraph) {
        BillGraph billGraph = BillGraph.createBillGraph(billHttpGraph);
        startGraph(billGraph, billHttpGraph.getRefreshRate());
        billHttpGraphs.put(billGraph.getId(), billHttpGraph);
    }

    private void startGraph(BillGraph billGraph, int reload) {
        BillGraphRefresher billGraphRefresher = new BillGraphRefresher(billGraph, reload);
        billGraphRefresher.startAsync();
    }

    public void generateAllGraphs() {
        for (Map.Entry<String, BillHttpGraph> next : billHttpGraphs.entrySet()) {
            startGraph(BillGraph.createBillGraph(next.getValue()), next.getValue().getRefreshRate());
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
