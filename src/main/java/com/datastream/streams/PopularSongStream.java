package com.datastream.streams;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
 
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import com.datastream.streams.serialization.*;
import com.datastream.streams.messages.DetailedSong;
import com.datastream.streams.messages.Tuple;
import com.datastream.streams.messages.Average;
 
public class PopularSongStream {
 
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "song-stream");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka2:9093");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 4);
 
        final StreamsBuilder builder = new StreamsBuilder();

        Map<String, Object> serdeProps = new HashMap<>();

        // DetailedSong Serdes
        final Serializer<DetailedSong> detailedSongSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", DetailedSong.class);
        detailedSongSerializer.configure(serdeProps, false);

        final Deserializer<DetailedSong> detailedSongDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", DetailedSong.class);
        detailedSongDeserializer.configure(serdeProps, false);

        final Serde<DetailedSong> detailedSongSerde = Serdes.serdeFrom(detailedSongSerializer, detailedSongDeserializer);

        final Serializer<Tuple> tupleSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", Tuple.class);
        tupleSerializer.configure(serdeProps, false);

        final Deserializer<Tuple> tupleDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", Tuple.class);
        tupleDeserializer.configure(serdeProps, false);

        final Serde<Tuple> tupleSerde = Serdes.serdeFrom(tupleSerializer, tupleDeserializer);

        final Serializer<Average> averageSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", Average.class);
        averageSerializer.configure(serdeProps, false);

        final Deserializer<Average> averageDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", Average.class);
        averageDeserializer.configure(serdeProps, false);

        final Serde<Average> averageSerde = Serdes.serdeFrom(averageSerializer, averageDeserializer);
 
        KStream<String, DetailedSong> details = builder.stream("details", Consumed.with(Serdes.String(), detailedSongSerde));
            
        KTable<String, Tuple> countedRatings = details
            .groupBy(
              (key, value) -> value.songID,
              Grouped.with(Serdes.String(), detailedSongSerde))
            .aggregate(
                () -> new Tuple(0, 0),
                (aggKey, newValue, aggValue) -> {
                    aggValue.rating += Integer.parseInt(newValue.rating);
                    aggValue.count++;
                    return aggValue;
                }, 
                Materialized.with(Serdes.String(), tupleSerde));

        KStream<String, Average> averages = countedRatings.toStream().map((key, value) -> KeyValue.pair(key, new Average(key, value.rating / (double) value.count, value.count)));

        averages.to("song-averages", Produced.with(Serdes.String(), averageSerde));
 
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