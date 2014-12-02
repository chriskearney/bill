package com.comandante.http;

import com.comandante.BillGraph;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class BillHttpClient {

    private final HttpRequestFactory requestFactory;

    public BillHttpClient() {
        requestFactory = new NetHttpTransport().createRequestFactory();
    }

    public InputStream getBillGraph(BillGraph billGraph) throws IOException {
        return getGraphData(billGraph.getGraphUrl());
    }

    private InputStream getGraphData(String url) throws IOException {
        HttpRequest httpRequest = requestFactory.buildGetRequest(new GenericUrl(new URL(url)));
        HttpResponse execute = httpRequest.execute();
        System.out.println(execute.getStatusCode());
        return execute.getContent();
    }
}

