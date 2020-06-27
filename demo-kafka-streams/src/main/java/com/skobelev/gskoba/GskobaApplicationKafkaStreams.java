package com.skobelev.gskoba;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public Topology topology(StreamsBuilder streamsBuilder) {
        KStream<String, String> inputTopic = streamsBuilder.stream("first_topic",
                Consumed.with(Serdes.String(), Serdes.String()));
        inputTopic.mapValues(value -> value + " DevParty 2020 !!!!").to("second_topic",
                Produced.with(Serdes.String(), Serdes.String()));
        return streamsBuilder.build();
    }

    @Bean
    public StreamsBuilder streamsBuilder() {
        return new StreamsBuilder();
    }
}

