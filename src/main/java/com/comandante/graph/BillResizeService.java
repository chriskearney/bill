package com.comandante.graph;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Resizing a window in Swing generates many ComponentResized events
 * and I don't want to call home to a graphite server for each one, so I track events
 * and wait for them to stop before requesting a graph.
 * Stores BillResizeEvents in a queue.  A Worker thread takes events from the queue
 * and places them into a Guava Cache with an expireAfterWrite config and a custom
 * RemovalListener that will "refresh" the graphs upon any Cache Removal's with the
 * "EXPIRED" cause.
 */
public class BillResizeService extends AbstractExecutionThreadService {
    private final LinkedBlockingQueue<BillResizeEvent> events;
    private final BillGraphManager billGraphManager;
    private final ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    private static final Logger log = LogManager.getLogger(BillResizeService.class);

    public BillResizeService(BillGraphManager billGraphManager) {
        this.events = new LinkedBlockingQueue<BillResizeEvent>();
        this.billGraphManager = billGraphManager;
        this.ses.scheduleWithFixedDelay(
                new Runnable() {
                    public void run() {
                        eventCache.cleanUp();
                    }
                }, 0, 50, TimeUnit.MILLISECONDS);
    }

    Cache<String, BillResizeEvent> eventCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .removalListener(new ResizeRemovalListener())
            .build();

    public void process(BillResizeEvent billResizeEvent) {
        events.add(billResizeEvent);
    }

    /**
     * Remove BillResizeEvents from the Queue and determine if they are valid before
     * adding them to the event cache.  If either the width or the height is the same
     * as the current BillGraph, we skip the cache as there is no resize to be performed.
     * @throws Exception
     */
    @Override
    protected void run() throws Exception {
        while (true) {
            BillResizeEvent event = events.take();
            BillHttpGraph billHttpGraph = billGraphManager.getBillHttpGraphs().get(event.getId());
            if (billHttpGraph.getWidth() != event.getWidth() || billHttpGraph.getHeight() != event.getHeight()) {
                log.debug("ResizeEvent triggered with coordinates: " + event.getWidth() + "x" + event.getHeight());
                billHttpGraph.setWidth(event.getWidth());
                billHttpGraph.setHeight(event.getHeight());
                billGraphManager.getBillHttpGraphs().put(event.getId(), billHttpGraph);
                billGraphManager.getRefresherMap().get(event.getId()).getBillGraph().reSize(event.getWidth(), event.getHeight());
                eventCache.invalidate(event.getId());
                eventCache.put(event.getId(), event);
            } else {
                log.debug("ResizeEvent dropped, dimensions are the same. (" + event.getWidth() + "x" + event.getHeight() + ")");
            }
        }
    }

    class ResizeRemovalListener implements RemovalListener<String, BillResizeEvent> {
        @Override
        public void onRemoval(RemovalNotification<String, BillResizeEvent> removal) {
            BillResizeEvent event = removal.getValue();
            if (event == null) return;
            try {
                log.debug("Graph " + event.getId() + " resize cache evicted with reason: " + removal.getCause());
                if (removal.getCause().equals(RemovalCause.EXPIRED)) {
                    BillGraphRefresher billGraphRefresher = billGraphManager.getRefresherMap().get(event.getId());
                    if (billGraphRefresher != null) {
                        billGraphRefresher.runOneIteration();
                        log.debug("Graph resize refresh completed (" + event.getWidth() + "x" + event.getHeight() + ")");
                    }
                }
            } catch (Exception e) {
                log.error("Unable to complete a refresh resize attempt.", e);
            }
        }
    }
}