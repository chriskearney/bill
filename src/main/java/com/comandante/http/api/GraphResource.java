package com.comandante.http.api;

import com.beust.jcommander.internal.Maps;
import com.comandante.BillGraph;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/bill")
@Produces(MediaType.APPLICATION_JSON)
public class GraphResource {

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createGraph(@PathParam("title") String graphName, BillGraphJson billGraphJson) {

        Map<String, String> injectPairs = Maps.newHashMap();
        injectPairs.put("width", Integer.toString(billGraphJson.getWidth()));
        injectPairs.put("height", Integer.toString(billGraphJson.getHeight()));
        if (billGraphJson.getTimezone() != null) {
            injectPairs.put("tz", billGraphJson.getTimezone());
        }
        BillGraph billGraph = BillGraph.createBillGraph(billGraphJson.getGraphUrl(), injectPairs,
                billGraphJson.getWidth(), billGraphJson.getHeight(), graphName);

        System.out.println(billGraph);
        return Response.status(200).build();
    }

}
