package com.comandante.http;

import com.comandante.BillGraph;
import com.google.api.client.http.ByteArrayContent;
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
        System.out.println(url);
        HttpRequest httpRequest = requestFactory.buildGetRequest(new GenericUrl(new URL(url)));
        HttpResponse execute = httpRequest.execute();
        return execute.getContent();
    }

    public boolean billServerHealthCheck(String url) throws IOException {
        HttpRequest httpRequest = requestFactory.buildGetRequest(new GenericUrl(new URL(url)));
        return httpRequest.execute().isSuccessStatusCode();
    }

    public boolean createGraph(String url, String json) throws IOException {
        HttpRequest httpRequest = requestFactory.buildPostRequest(new GenericUrl(new URL(url)), ByteArrayContent.fromString("application/json", json));
        httpRequest.getHeaders().setContentType("application/json");
        return httpRequest.execute().isSuccessStatusCode();
    }
}

