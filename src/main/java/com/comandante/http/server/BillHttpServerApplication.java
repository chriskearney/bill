package com.comandante.http.server;

import com.comandante.BillGraphManager;
import com.comandante.http.server.resource.BillGraphCreateResource;
import com.comandante.http.server.resource.BillHttpServerHealthCheck;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class BillHttpServerApplication extends Application<BillHttpServerConfiguration> {

    private final BillGraphManager billGraphManager;

    public BillHttpServerApplication(BillGraphManager billGraphManager) {
        this.billGraphManager = billGraphManager;
    }

    @Override
    public void initialize(Bootstrap<BillHttpServerConfiguration> bootstrap) {

    }

    @Override
    public void run(BillHttpServerConfiguration billHttpServerConfiguration, Environment environment) throws Exception {
        final BillGraphCreateResource resource = new BillGraphCreateResource(
                billHttpServerConfiguration.getTemplate(),
                billHttpServerConfiguration.getDefaultName(),
                billGraphManager
        );
        environment.healthChecks().register("bill", new BillHttpServerHealthCheck());
        environment.jersey().register(resource);
    }

    @Override
    public String getName() {
        return "bill";
    }
}
