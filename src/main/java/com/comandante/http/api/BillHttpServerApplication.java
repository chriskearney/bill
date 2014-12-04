package com.comandante.http.api;

import com.comandante.GraphManager;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class BillHttpServerApplication extends Application<BillHttpServerConfiguration> {

    private final GraphManager graphManager;

    public BillHttpServerApplication(GraphManager graphManager) {
        this.graphManager = graphManager;
    }

    @Override
    public void initialize(Bootstrap<BillHttpServerConfiguration> bootstrap) {

    }

    @Override
    public void run(BillHttpServerConfiguration billHttpServerConfiguration, Environment environment) throws Exception {
        final GraphResource resource = new GraphResource(
                billHttpServerConfiguration.getTemplate(),
                billHttpServerConfiguration.getDefaultName(),
                graphManager
        );
        environment.jersey().register(resource);
    }

    @Override
    public String getName() {
        return "hello-world";
    }
}
