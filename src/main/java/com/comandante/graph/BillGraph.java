package com.comandante.graph;

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

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
    private String graphDuration;
    private boolean useLocalTimezone;
    private boolean disableUnitSystem;
    private int reloadInterval;
    private final String id;

    private BillGraph(String id, String graphUrl, int width, int height, String title, String graphDuration, boolean useLocalTimezone, boolean disableUnitSystem, int reloadInterval) throws URISyntaxException, MalformedURLException {
        this.graphUrlPairs = parse(new URI(graphUrl), "UTF-8");
        this.url = new URL(graphUrl);
        this.protocol = url.getProtocol();
        this.hostname = url.getHost();
        this.title = title;
        this.width = width;
        this.height = height;
        this.graphDuration = graphDuration;
        this.useLocalTimezone = useLocalTimezone;
        this.disableUnitSystem = disableUnitSystem;
        this.reloadInterval = reloadInterval;
        this.id = id;
    }

    public static BillGraph createBillGraph(BillHttpGraph billHttpGraph, Optional<String> id) {
        Map<String, String> injectPairs = Maps.newHashMap();
        injectPairs.put("width", Integer.toString(billHttpGraph.getWidth()));
        injectPairs.put("height", Integer.toString(billHttpGraph.getHeight()));
        if (billHttpGraph.getTimezone() != null) {
            injectPairs.put("tz", billHttpGraph.getTimezone());
        }
        if (billHttpGraph.getGraphDuration() != null) {
            injectPairs.put("from", billHttpGraph.getGraphDuration());
        }
        return createBillGraph(billHttpGraph.getGraphUrl(), injectPairs, billHttpGraph.getWidth(),
                billHttpGraph.getHeight(), billHttpGraph.getTitle(), billHttpGraph.getGraphDuration(),
                billHttpGraph.isUseLocalTimestamp(), billHttpGraph.isDisableUnitSystem(), billHttpGraph.getRefreshRate(), id);
    }

    private static BillGraph createBillGraph(String graphUrl, Map<String, String> injectPairs,
                                             int width, int height, String title, String graphDuration,
                                             boolean useLocalTimezone, boolean disableUnitSystem, int reloadInterval, Optional<String> id) {
        String newId;
        if (!id.isPresent()) {
            newId = UUID.randomUUID().toString();
        } else {
            newId = id.get();
        }
        BillGraph billGraph = null;
        try {
            billGraph = new BillGraph(newId, graphUrl, width, height, title, graphDuration, useLocalTimezone, disableUnitSystem, reloadInterval);
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

    public void reSize(int width, int height) {
        this.width = width;
        this.height = height;
        setPair("height", Integer.toString(height));
        setPair("width", Integer.toString(width));
    }

    public void updateDuration(String graphDuration) {
        this.graphDuration = graphDuration;
        setPair("from", graphDuration);
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
                nameValuePairListIterator.set(new BasicNameValuePair(k, v));
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

    public String getGraphUrlWithTitle() {
        List<NameValuePair> pairWithTitle = ImmutableList.<NameValuePair>builder()
                .add(new BasicNameValuePair("title", title))
                .addAll(ImmutableList.copyOf(graphUrlPairs))
                .build();
        return getGraphUrl(pairWithTitle);
    }

    public String getGraphUrl() {
        return getGraphUrl(graphUrlPairs);
    }

    public String getGraphUrl(List<NameValuePair> pairs) {
        return protocol + "://" + hostname + "/render/?" + URLEncodedUtils.format(pairs, "UTF-8");
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getTitle() {
        if (getGraphDuration() == null) {
            return title;
        }
        return title + " (" + getGraphDuration() + ")";
    }

    public String getRawTitle() {
        return title;
    }


    public String getGraphDuration() {
        return graphDuration;
    }

    public String getId() {
        return id;
    }

    public boolean isUseLocalTimezone() {
        return useLocalTimezone;
    }

    public boolean isDisableUnitSystem() {
        return disableUnitSystem;
    }

    public int getReloadInterval() {
        return reloadInterval;
    }
}
