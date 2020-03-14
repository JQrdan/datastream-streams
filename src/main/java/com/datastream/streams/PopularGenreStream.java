package com.datastream.streams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Serialized;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.kstream.Materialized;
 
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import com.datastream.streams.serialization.*;
import com.datastream.streams.messages.Song;
import com.datastream.streams.messages.SongAttributes;
import com.datastream.streams.messages.DetailedSong;
import com.datastream.streams.messages.Genre;
 
public class PopularGenreStream {
 
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "genre-stream");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka2:9093");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 4);
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Double().getClass().getName());
 
        final StreamsBuilder builder = new StreamsBuilder();

        Map<String, Object> serdeProps = new HashMap<>();

        // Genre Serdes
        final Serializer<Genre> genreSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", Genre.class);
        genreSerializer.configure(serdeProps, false);

        final Deserializer<Genre> genreDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", Genre.class);
        genreDeserializer.configure(serdeProps, false);

        final Serde<Genre> genreSerde = Serdes.serdeFrom(genreSerializer, genreDeserializer);

        // DetailedSong Serdes
        final Serializer<DetailedSong> detailedSongSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", DetailedSong.class);
        detailedSongSerializer.configure(serdeProps, false);

        final Deserializer<DetailedSong> detailedSongDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", DetailedSong.class);
        detailedSongDeserializer.configure(serdeProps, false);

        final Serde<DetailedSong> detailedSongSerde = Serdes.serdeFrom(detailedSongSerializer, detailedSongDeserializer);
 
        KStream<String, DetailedSong> details = builder.stream("details", Consumed.with(Serdes.String(), detailedSongSerde));
            
        KTable<String, Double> countedRatings = details
            .groupByKey(Serialized.with(Serdes.String(), detailedSongSerde))
            .aggregate(
                () -> 0D, // initial aggregate is 0
                (aggKey, newValue, aggValue) -> {
                    System.out.println(aggValue + " " + newValue.getRating());
                    return aggValue + Double.parseDouble(newValue.getRating());
                }, 
                Materialized.as("aggregated-stream-store"));
 
        countedRatings.toStream().to("counted", Produced.with(Serdes.String(), Serdes.Double()));
 
        final KafkaStreams streams = new KafkaStreams(builder.build(), props);
        final CountDownLatch latch = new CountDownLatch(1);
 
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });
 
        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}