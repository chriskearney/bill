package com.comandante;

import com.comandante.http.server.resource.BillHttpGraph;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import org.mapdb.DB;
import org.mapdb.HTreeMap;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class BillGraphManager {

    private HTreeMap<String, BillHttpGraph> billHttpGraphs;
    private Map<String, BillGraphRefresher> refresherMap;
    private static final String BILL_HTTP_GRAPH_DB = "billHttpGraphs";
    private final DB db;
    private final ResizeService resizeService;

    public BillGraphManager(DB db) {
        billHttpGraphs = db.createHashMap(BILL_HTTP_GRAPH_DB)
                .valueSerializer(new BillHttpGraphSerializer())
                .makeOrGet();
        this.db = db;
        this.refresherMap = Maps.newConcurrentMap();
        this.resizeService = new ResizeService();
        this.resizeService.startAsync();
    }

    public void addNewGraph(BillHttpGraph billHttpGraph) {
        BillGraph billGraph = BillGraph.createBillGraph(billHttpGraph, Optional.<String>absent());
        startGraph(billGraph, billHttpGraph.getRefreshRate());
        billHttpGraphs.put(billGraph.getId(), billHttpGraph);
    }

    public void removeGraph(String id) {
        refresherMap.get(id).stopAsync();
        refresherMap.remove(id);
        billHttpGraphs.remove(id);
    }

    public void resizeGraph(String id, int width, int height) {
        // I pretty much had to async this or resizing a window was choppy/shitty.
        resizeService.process(ResizeEvent.newBuilder()
                .setWidth(width)
                .setHeight(height)
                .setId(id)
                .build());
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

    class ResizeService extends AbstractExecutionThreadService {
        private final LinkedBlockingQueue<ResizeEvent> events;

        public ResizeService() {
            this.events = new LinkedBlockingQueue<ResizeEvent>();
        }

        @Override
        protected void run() throws Exception {
            while (true) {
                ResizeEvent take = events.take();
                BillHttpGraph billHttpGraph = billHttpGraphs.get(take.getId());
                billHttpGraph.setWidth(take.getWidth());
                billHttpGraph.setHeight(take.getHeight());
                billHttpGraphs.put(take.getId(), billHttpGraph);
                refresherMap.get(take.getId()).getBillGraph().reSize(take.getWidth(), take.getHeight());
                System.out.println("Successfully processed resize Event.");
            }
        }

        public void process(ResizeEvent resizeEvent) {
            events.add(resizeEvent);
        }
    }
}
