package com.comandante;

import com.beust.jcommander.internal.Maps;
import com.comandante.http.server.resource.BillHttpGraph;
import com.google.common.base.Optional;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;

import static org.apache.http.client.utils.URLEncodedUtils.parse;

public class BillGraph {

    private List<NameValuePair> graphUrlPairs;
    private final URL url;
    private String protocol;
    private String hostname;
    private String title;
    private int width;
    private int height;
    private final String id;

    private BillGraph(String id, String graphUrl, int width, int height, String title) throws URISyntaxException, MalformedURLException {
        this.graphUrlPairs = parse(new URI(graphUrl), "UTF-8");
        this.url = new URL(graphUrl);
        this.protocol = url.getProtocol();
        this.hostname = url.getHost();
        this.title = title;
        this.width = width;
        this.height = height;
        this.id = id;
    }

    public static BillGraph createBillGraph(BillHttpGraph billHttpGraph, Optional<String> id) {
        Map<String, String> injectPairs = Maps.newHashMap();
        injectPairs.put("width", Integer.toString(billHttpGraph.getWidth()));
        injectPairs.put("height", Integer.toString(billHttpGraph.getHeight()));
        if (billHttpGraph.getTimezone() != null) {
            injectPairs.put("tz", billHttpGraph.getTimezone());
        }
        return createBillGraph(billHttpGraph.getGraphUrl(), injectPairs,
                billHttpGraph.getWidth(), billHttpGraph.getHeight(), billHttpGraph.getTitle(), id);
    }

    private static BillGraph createBillGraph(String graphUrl, Map<String, String> injectPairs, int width, int height, String title, Optional<String> id) {
        String newId;
        if (!id.isPresent()) {
            newId = UUID.randomUUID().toString();
        } else {
            newId = id.get();
        }
        BillGraph billGraph = null;
        try {
            billGraph = new BillGraph(newId, graphUrl, width, height + 20, title);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
        Iterator<Map.Entry<String, String>> iterator = injectPairs.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            billGraph.setPair(next.getKey(), next.getValue());
        }
        billGraph.deletePair("title");
        return billGraph;
    }

    private boolean doesKeyExistInPairs(String k) {
        for (NameValuePair nvp : graphUrlPairs) {
            if (nvp.getName().equals(k)) {
                return true;
            }
        }
        return false;
    }

    public void setPair(final String k, final String v) {
        // TODO: This is ugly.
        if (!doesKeyExistInPairs(k)) {
            graphUrlPairs.add(new NameValuePair() {
                @Override
                public String getName() {
                    return k;
                }

                @Override
                public String getValue() {
                    return null;
                }
            });
        }
        ListIterator<NameValuePair> nameValuePairListIterator = graphUrlPairs.listIterator();
        while (nameValuePairListIterator.hasNext()) {
            final NameValuePair next = nameValuePairListIterator.next();
            if (next.getName().equals(k)) {
                nameValuePairListIterator.set(new NameValuePair() {
                    @Override
                    public String getName() {
                        return next.getName();
                    }

                    @Override
                    public String getValue() {
                        return v;
                    }
                });
            }
        }

    }

    public void deletePair(String k) {
        ListIterator<NameValuePair> nameValuePairListIterator = graphUrlPairs.listIterator();
        while (nameValuePairListIterator.hasNext()) {
            final NameValuePair next = nameValuePairListIterator.next();
            if (next.getName().equals(k)) {
                nameValuePairListIterator.remove();
            }
        }
    }

    public NameValuePair getPair(String k) {
        for (NameValuePair pair : graphUrlPairs) {
            if (pair.getName().equals(k)) {
                return pair;
            }
        }
        return null;
    }

    public String getGraphUrl() {
        return protocol + "://" + hostname + "/render/?" + URLEncodedUtils.format(graphUrlPairs, "UTF-8");
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getTitle() {
        return title;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getId() {
        return id;
    }
}
