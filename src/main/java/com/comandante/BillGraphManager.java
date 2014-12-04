package com.comandante;

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Optional;

import java.util.Map;

public class BillGraphManager {

    private final Map<String, BillGraphRefresher> graphRegistry = Maps.newHashMap();

    public BillGraphManager() {

    }

    public void addGraph(BillGraph billGraph, int reload) {
        BillGraphRefresher billGraphRefresher = new BillGraphRefresher(billGraph, reload);
        billGraphRefresher.startAsync();
        graphRegistry.put(billGraphRefresher.getBillGraph().getTitle(), billGraphRefresher);
    }

    public Optional<BillGraph> getGraph(String title) {
        BillGraphRefresher billGraphRefresher = graphRegistry.get(title);
        if (billGraphRefresher == null) {
            return Optional.absent();
        }
        return Optional.of(billGraphRefresher.getBillGraph());
    }
}
