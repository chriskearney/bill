package com.comandante.http.server;

import ch.qos.logback.classic.Level;
import com.comandante.Bill;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.jetty.HttpConnectorFactory;
import io.dropwizard.server.DefaultServerFactory;
import org.hibernate.validator.constraints.NotEmpty;

public class BillHttpServerConfiguration extends Configuration {

    public BillHttpServerConfiguration() {
        ((HttpConnectorFactory) ((DefaultServerFactory) getServerFactory()).getApplicationConnectors().get(0)).setPort(Bill.DEFAULT_HTTP_PORT);
        ((HttpConnectorFactory) ((DefaultServerFactory) getServerFactory()).getAdminConnectors().get(0)).setPort(Bill.DEFAULT_HTTP_PORT_ADMIN);

        getLoggingFactory().setLevel(Level.ERROR);
    }

    @NotEmpty
    private String template = "bill";

    @NotEmpty
    private String defaultName = "bill";

    @JsonProperty
    public String getTemplate() {
        return template;
    }

    @JsonProperty
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty
    public String getDefaultName() {
        return defaultName;
    }

    @JsonProperty
    public void setDefaultName(String name) {
        this.defaultName = name;
    }


}