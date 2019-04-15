package com.asaupe.wordfrequencycount;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import com.asaupe.wordfrequencycount.resources.HelloWorld;
import com.asaupe.wordfrequencycount.health.TemplateHealthCheck;

public class MainApplication extends Application<MainConfiguration> {
    public static void main(String[] args) throws Exception {
        new MainApplication().run(args);
    }

    @Override
    public String getName() {
        return "hello-world";
    }

    @Override
    public void initialize(Bootstrap<MainConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(MainConfiguration configuration,
                    Environment environment) {
        final HelloWorld resource = new HelloWorld(
                configuration.getTemplate(),
                configuration.getDefaultName()
            );
        final TemplateHealthCheck healthCheck =
                new TemplateHealthCheck(configuration.getTemplate());
        environment.healthChecks().register("template", healthCheck);
        environment.jersey().register(resource);
    }

}