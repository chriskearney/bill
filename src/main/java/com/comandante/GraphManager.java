package com.comandante;

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Optional;

import java.util.Map;

public class GraphManager {

    private final Map<String, GraphRefreshService> graphRegistry = Maps.newHashMap();

    public GraphManager() {

    }

    public void addGraph(BillGraph billGraph, int reload) {
        GraphRefreshService graphRefreshService = new GraphRefreshService(billGraph, reload);
        graphRefreshService.startAsync();
        graphRegistry.put(graphRefreshService.getBillGraph().getTitle(), graphRefreshService);
    }

    public Optional<BillGraph> getGraph(String title) {
        GraphRefreshService graphRefreshService = graphRegistry.get(title);
        if (graphRefreshService == null) {
            return Optional.absent();
        }
        return Optional.of(graphRefreshService.getBillGraph());
    }
}
