package com.comandante;

import com.google.common.base.Optional;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ListIterator;

import static org.apache.http.client.utils.URLEncodedUtils.parse;

public class BillGraph {

    private List<NameValuePair> graphUrlPairs;
    private final URL url;
    private Optional<String> protocol = Optional.absent();
    private Optional<String> hostname = Optional.absent();
    private final BillCommand billCommand;

    public BillGraph(String graphUrl, BillCommand billCommand) throws URISyntaxException, MalformedURLException {
        this.graphUrlPairs = parse(new URI(graphUrl), "UTF-8");
        this.url = new URL(graphUrl);
        this.billCommand = billCommand;
        this.protocol = Optional.of(url.getProtocol());
        this.hostname = Optional.of(url.getHost());
    }

    public void setPair(final String k, final String v) {
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

    public NameValuePair getPair(String k) {
        for (NameValuePair pair: graphUrlPairs) {
            if (pair.getName().equals(k)) {
                return pair;
            }
        }
        return null;
    }

    public String getGraphUrl() {
        String prot = null;
        if (protocol.isPresent()) {
            prot = protocol.get();
        } else {
            prot = "http";
        }
        String host = null;
        if (hostname.isPresent()) {
            host = hostname.get();
        } else {
            host = billCommand.getGraphiteHost();
        }
        String url = prot + "://" + host + "/render/?" + URLEncodedUtils.format(graphUrlPairs, "UTF-8");
        System.out.println(url);
        return url;
    }


}
