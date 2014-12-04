package com.comandante.http.server.resource;

import com.beust.jcommander.internal.Maps;
import com.comandante.BillGraph;
import com.comandante.BillGraphManager;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/bill")
@Produces(MediaType.APPLICATION_JSON)
public class BillGraphCreateResource {
    private final String template;
    private final String defaultName;
    private final BillGraphManager billGraphManager;

    public BillGraphCreateResource(String template, String defaultName, BillGraphManager billGraphManager) {
        this.template = template;
        this.defaultName = defaultName;
        this.billGraphManager = billGraphManager;
    }

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createGraph(BillHttpGraph billHttpGraph) {
        Map<String, String> injectPairs = Maps.newHashMap();
        injectPairs.put("width", Integer.toString(billHttpGraph.getWidth()));
        injectPairs.put("height", Integer.toString(billHttpGraph.getHeight()));
        if (billHttpGraph.getTimezone() != null) {
            injectPairs.put("tz", billHttpGraph.getTimezone());
        }
        BillGraph billGraph = BillGraph.createBillGraph(billHttpGraph.getGraphUrl(), injectPairs,
                billHttpGraph.getWidth(), billHttpGraph.getHeight(), billHttpGraph.getTitle());
        billGraphManager.addGraph(billGraph, billHttpGraph.getRefreshRate());
        return Response.status(200).build();
    }
}
