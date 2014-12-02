package com.comandante.http.api;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/bill")
@Produces(MediaType.APPLICATION_JSON)
public class GraphResource {

    @POST
    @Path("/create")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response successAPNS(@PathParam("graphName") String graphName, @Context HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();


        return Response.status(200).build();
    }

}
