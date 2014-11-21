package org.apache.streams.datasift.example;

import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.setup.Bootstrap;
import org.apache.streams.config.ComponentConfigurator;
import org.apache.streams.config.StreamsConfiguration;
import org.apache.streams.console.ConsolePersistWriter;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsProvider;
import org.apache.streams.datasift.processor.DatasiftTypeConverterProcessor;
import org.apache.streams.dropwizard.StreamsApplication;
import org.apache.streams.dropwizard.StreamsDropwizardConfiguration;
import org.apache.streams.dropwizard.StreamsDropwizardModule;
import org.apache.streams.elasticsearch.ElasticsearchConfiguration;
import org.apache.streams.elasticsearch.ElasticsearchPersistWriter;
import org.apache.streams.elasticsearch.ElasticsearchWriterConfiguration;
import org.apache.streams.jackson.CleanAdditionalPropertiesProcessor;
import org.apache.streams.pojo.json.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by sblackmon on 11/20/14.
 */
public class DatasiftWebhookElasticsearch extends StreamsApplication {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(DatasiftWebhookElasticsearch.class);

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

        builder.addStreamsProcessor("converter", new DatasiftTypeConverterProcessor(Activity.class), 2, providers.toArray(new String[providers.size()]));

        ComponentConfigurator<ElasticsearchConfiguration> elasticsearchConfigurationComponentConfigurator = new ComponentConfigurator<ElasticsearchConfiguration>(ElasticsearchConfiguration.class);
        ElasticsearchWriterConfiguration writerConfiguration = mapper.convertValue(streamsConfiguration.getAdditionalProperties().get("elasticsearch"), ElasticsearchWriterConfiguration.class);
        builder.addStreamsPersistWriter(ElasticsearchPersistWriter.STREAMS_ID, new ElasticsearchPersistWriter(writerConfiguration), 1, "converter");

        return builder;
    }

    public static void main(String[] args) throws Exception
    {

        new DatasiftWebhookElasticsearch().run(args);

    }
}
