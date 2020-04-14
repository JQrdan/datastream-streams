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
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Materialized;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import com.datastream.streams.serialization.JsonPOJODeserializer;
import com.datastream.streams.serialization.JsonPOJOSerializer;
import com.datastream.streams.messages.Artist;
import com.datastream.streams.messages.DetailedSong;
import com.datastream.streams.messages.TupleList;
 
public class ArtistStream {
 
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "artist-stream");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9092,kafka2:9093,kafka3:9094");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 4);
        props.put(StreamsConfig.RETRIES_CONFIG, 100);
 
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
        
        final Serializer<TupleList<DetailedSong>> tupleListSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", TupleList.class);
        tupleListSerializer.configure(serdeProps, false);

        final Deserializer<TupleList<DetailedSong>> tupleListDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", TupleList.class);
        tupleListDeserializer.configure(serdeProps, false);

        final Serde<TupleList<DetailedSong>> tupleListSerde = Serdes.serdeFrom(tupleListSerializer, tupleListDeserializer);

        final Serializer<Artist> artistSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", Artist.class);
        artistSerializer.configure(serdeProps, false);

        final Deserializer<Artist> artistDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", Artist.class);
        artistDeserializer.configure(serdeProps, false);

        final Serde<Artist> artistSerde = Serdes.serdeFrom(artistSerializer, artistDeserializer);
 
        KStream<String, DetailedSong> details = builder.stream("details", Consumed.with(Serdes.String(), detailedSongSerde));
            
        KTable<String, TupleList<DetailedSong>> countedRatings = details
            .groupBy(
                (key, value) -> value.artistID,
                Grouped.with(Serdes.String(), detailedSongSerde))
            .aggregate(
                () -> new TupleList<DetailedSong>(0, 0, new ArrayList<>()),
                (aggKey, newValue, aggValue) -> {
                    aggValue.rating += Integer.parseInt(newValue.rating);
                    aggValue.count++;
                    aggValue.list.add(new DetailedSong(newValue.userID, newValue.songID, newValue.rating, newValue.albumID, newValue.artistID, newValue.genreID));
                    return aggValue;
                }, 
                Materialized.with(Serdes.String(), tupleListSerde));

        KStream<String, Artist> artists = countedRatings.toStream().map(
                (key, value) -> KeyValue.pair(key, new Artist(key, value.rating / (double) value.count, value.count, value.list)));
 
        artists.to("artist-averages", Produced.with(Serdes.String(), artistSerde));
 
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