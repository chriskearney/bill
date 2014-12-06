package com.comandante;

import com.comandante.http.server.resource.BillHttpGraph;
import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import org.mapdb.DB;
import org.mapdb.HTreeMap;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BillGraphManager {

    private HTreeMap<String, BillHttpGraph> billHttpGraphs;
    private Map<String, BillGraphRefresher> refresherMap;
    private static final String BILL_HTTP_GRAPH_DB = "billHttpGraphs";
    private final ResizeService resizeService;

    public BillGraphManager(DB db) {
        billHttpGraphs = db.createHashMap(BILL_HTTP_GRAPH_DB)
                .valueSerializer(new BillHttpGraphSerializer())
                .makeOrGet();
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

    class ResizeService extends AbstractExecutionThreadService {
        private final LinkedBlockingQueue<BillResizeEvent> events;

        RemovalListener<String, BillResizeEvent> removalListener = new RemovalListener<String, BillResizeEvent>() {
            public void onRemoval(RemovalNotification<String, BillResizeEvent> removal) {
                try {
                    System.out.println(removal.getCause());
                    if (removal.getCause().equals(RemovalCause.EXPIRED)) {
                        BillResizeEvent billResizeEvent = removal.getValue();
                        if (billResizeEvent != null) {
                            BillResizeEvent event = removal.getValue();
                            BillGraphRefresher billGraphRefresher = refresherMap.get(event.getId());
                            if (billGraphRefresher != null) {
                                billGraphRefresher.runOneIteration();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Cache<String, BillResizeEvent> eventCache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.SECONDS)
                .removalListener(removalListener)
                .build();

        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();

        public ResizeService() {
            this.events = new LinkedBlockingQueue<BillResizeEvent>();
            this.ses.scheduleWithFixedDelay(
                    new Runnable() {
                        public void run() {
                            eventCache.cleanUp();
                        }
                    }, 0, 50, TimeUnit.MILLISECONDS);
        }

        @Override
        protected void run() throws Exception {
            while (true) {
                BillResizeEvent event = events.take();
                BillHttpGraph billHttpGraph = billHttpGraphs.get(event.getId());
                if (billHttpGraph.getWidth() != event.getWidth() || billHttpGraph.getHeight() != event.getHeight()) {
                    System.out.println("found a difference updating resize cache.");
                    billHttpGraph.setWidth(event.getWidth());
                    billHttpGraph.setHeight(event.getHeight());
                    billHttpGraphs.put(event.getId(), billHttpGraph);
                    refresherMap.get(event.getId()).getBillGraph().reSize(event.getWidth(), event.getHeight());
                    eventCache.invalidate(event.getId());
                    eventCache.put(event.getId(), event);
                } else {
                    System.out.println("no difference detected, skipping");
                }
            }
        }

        public void process(BillResizeEvent billResizeEvent) {
            events.add(billResizeEvent);
        }
    }
}
