package com.comandante.http.server.resource;

import com.codahale.metrics.health.HealthCheck;

public class BillHttpServerHealthCheck extends HealthCheck {
    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}
