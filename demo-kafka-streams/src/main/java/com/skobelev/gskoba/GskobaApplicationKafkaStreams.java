package com.skobelev.gskoba;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Properties;

@SpringBootApplication
public class GskobaApplicationKafkaStreams {
    public static void main(String[] args) {
        SpringApplication.run(GskobaApplicationKafkaStreams.class, args);
    }
}

@Configuration
class Config {

    @Bean
    public Properties kafkaProperties(@Value("${kafka.bootstrap-server}") String host) {
        Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, host);
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "demo-kafka-streams");
        return properties;
    }

    @Bean(initMethod = "start", destroyMethod = "close")
    public KafkaStreams kafkaStreams(Properties kafkaProperties, Topology topology) {
        return new KafkaStreams(topology, kafkaProperties);
    }

    @Bean
    public Topology topology(StreamsBuilder streamsBuilder,
                             ProcessorSupplier<String, String> batchProcessor,
                             StoreBuilder storeBuilders) {

            streamsBuilder.addStateStore(storeBuilders);

        KStream<String, String> firstTopic = streamsBuilder.stream("first_topic",
                Consumed.with(Serdes.String(), Serdes.String()));
        firstTopic.mapValues(value -> value + " RndConf 2020 !!!!").to("second_topic",
                Produced.with(Serdes.String(), Serdes.String()));

        streamsBuilder.stream("second_topic",
                Consumed.with(Serdes.String(), Serdes.String())).process(batchProcessor, storeBuilders.name());

        return streamsBuilder.build();
    }

    @Bean
    public StreamsBuilder streamsBuilder() {
        return new StreamsBuilder();
    }

    @Bean
    public ProcessorSupplier<String, String> batchProcessor() {
        return () -> new BatchProcessor();
    }

    @Bean
    public StoreBuilder store() {
        return Stores.keyValueStoreBuilder(Stores.persistentKeyValueStore("test-store"),
                Serdes.String(), Serdes.String());
    }
}


class BatchProcessor implements Processor<String, String> {

    private ProcessorContext context;
    private KeyValueStore<String, String> store;

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
        this.store = (KeyValueStore<String, String>) context.getStateStore("test-store");
    }

    @Override
    public void process(String key, String value) {
        store.put(key, value);
        if (store.approximateNumEntries() >= 3) {
            store.all().forEachRemaining(keyValue -> {
                System.out.println(keyValue.value);
            });
            context.commit();
            store.flush();
        }
    }

    @Override
    public void close() {
        //do nothing
    }
}
