package com.sebastian_daschner.coffee_shop.boundary;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

@Health
@ApplicationScoped
public class HealthResource implements HealthCheck {

    @Inject
    @ConfigProperty(name = "default.barista.http.port")
    String baristaHttpPort;

    public boolean isHealthy() {
        System.out.println(System.getProperties());
        try {
            String url = "http://localhost:" + Integer.parseInt(baristaHttpPort)
                    + "/health";
            Client client = ClientBuilder.newClient();
            Response response = client.target(url).request(MediaType.APPLICATION_JSON).get();
            if (response.getStatus() != 200) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public HealthCheckResponse call() {
        if (!isHealthy()) {
            return HealthCheckResponse.named(this.getClass().getSimpleName()).withData("services", "not available")
                    .down().build();
        }
        return HealthCheckResponse.named(this.getClass().getSimpleName()).withData("services", "available").up()
                .build();
    }

}
