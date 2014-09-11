package org.apache.streams.peoplepattern.example;

import com.typesafe.config.Config;
import org.apache.streams.config.StreamsConfigurator;
import org.apache.streams.console.ConsolePersistWriter;
import org.apache.streams.core.StreamBuilder;
import org.apache.streams.core.StreamsDatum;
import org.apache.streams.local.builders.LocalStreamBuilder;
import org.apache.streams.peoplepattern.AccountTypeProcessor;
import org.apache.streams.peoplepattern.DemographicsProcessor;
import org.apache.streams.pojo.json.Activity;
import org.apache.streams.twitter.TwitterStreamConfiguration;
import org.apache.streams.twitter.processor.TwitterTypeConverter;
import org.apache.streams.twitter.provider.TwitterConfigurator;
import org.apache.streams.twitter.provider.TwitterStreamProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by sblackmon on 12/10/13.
 */
public class TwitterProfilesPeoplePattern {

    private final static Logger LOGGER = LoggerFactory.getLogger(TwitterProfilesPeoplePattern.class);

    public static void main(String[] args)
    {
        LOGGER.info(StreamsConfigurator.config.toString());

        Config twitter = StreamsConfigurator.config.getConfig("twitter");

        StreamBuilder builder = new LocalStreamBuilder(new LinkedBlockingQueue<StreamsDatum>(2));

        TwitterStreamConfiguration twitterStreamConfiguration = TwitterConfigurator.detectTwitterStreamConfiguration(twitter);
        TwitterStreamProvider stream = new TwitterStreamProvider(twitterStreamConfiguration);
        TwitterTypeConverter converter = new TwitterTypeConverter(String.class, Activity.class);

        AccountTypeProcessor accountTypeProcessor = new AccountTypeProcessor();
        DemographicsProcessor demographicsProcessor = new DemographicsProcessor();

        builder.newPerpetualStream("provider", stream);
        builder.addStreamsProcessor("converter", converter, 4, "provider");
        builder.addStreamsProcessor("accountTypeProcessor", accountTypeProcessor, 1, "converter");
        builder.addStreamsProcessor("demographicsProcessor", demographicsProcessor, 1, "accountTypeProcessor");

        builder.addStreamsPersistWriter("console", new ConsolePersistWriter(), 1, "demographicsProcessor");

        builder.start();
    }
}
