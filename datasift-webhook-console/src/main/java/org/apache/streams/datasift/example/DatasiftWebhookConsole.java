package org.apache.streams.datasift.example;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.setup.Bootstrap;
import org.apache.streams.config.StreamsConfiguration;
import org.apache.streams.console.ConsolePersistWriter;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsProvider;
import org.apache.streams.dropwizard.StreamDropwizardBuilder;
import org.apache.streams.dropwizard.StreamsApplication;
import org.apache.streams.dropwizard.StreamsDropwizardConfiguration;
import org.apache.streams.dropwizard.StreamsDropwizardModule;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by sblackmon on 11/20/14.
 */
public class DatasiftWebhookConsole extends StreamsApplication {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(DatasiftWebhookConsole.class);

    @Override
    public void initialize(Bootstrap<StreamsDropwizardConfiguration> bootstrap) {

        LOGGER.info(getClass().getPackage().getName());

        GuiceBundle<StreamsDropwizardConfiguration> guiceBundle =
                GuiceBundle.<StreamsDropwizardConfiguration>newBuilder()
                        .addModule(new StreamsDropwizardModule())
                        .setConfigClass(StreamsDropwizardConfiguration.class)
                        .enableAutoConfig("org.apache.streams.dropwizard", "org.apache.streams.datasift.provider")
                        .build();
        bootstrap.addBundle(guiceBundle);

    }

    @Override
    public StreamBuilder setup(StreamsConfiguration streamsConfiguration, Set<StreamsProvider> resourceProviders) {

        builder = super.setup(streamsConfiguration, resourceProviders);

        List<String> providers = new ArrayList<>();
        for( StreamsProvider provider: resourceProviders) {
            String providerId = provider.getClass().getSimpleName();
            providers.add(providerId);
        }

        builder.addStreamsPersistWriter("console", new ConsolePersistWriter(), 1, providers.toArray(new String[providers.size()]));

        return builder;
    }

    public static void main(String[] args) throws Exception
    {

        new DatasiftWebhookConsole().run(args);

    }
}
