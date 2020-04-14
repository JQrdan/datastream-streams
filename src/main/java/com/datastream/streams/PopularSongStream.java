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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import com.datastream.streams.serialization.*;
import com.datastream.streams.messages.DetailedSong;
import com.datastream.streams.messages.TopSongs;
import com.datastream.streams.messages.Tuple;
import com.datastream.streams.messages.Average;
 
class SortByRating implements Comparator<Average> 
{ 
    public int compare(Average a, Average b) 
    { 
        if(a == null) return 1;
        if(b == null) return -1;
        return (int) (a.averageRating - b.averageRating); 
    } 
} 

public class PopularSongStream {
 
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "song-stream");
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

        final Serializer<Tuple> tupleSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", Tuple.class);
        tupleSerializer.configure(serdeProps, false);

        final Deserializer<Tuple> tupleDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", Tuple.class);
        tupleDeserializer.configure(serdeProps, false);

        final Serde<Tuple> tupleSerde = Serdes.serdeFrom(tupleSerializer, tupleDeserializer);

        final Serializer<TopSongs> topSongsSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", TopSongs.class);
        topSongsSerializer.configure(serdeProps, false);

        final Deserializer<TopSongs> topSongsDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", TopSongs.class);
        topSongsDeserializer.configure(serdeProps, false);

        final Serde<TopSongs> topSongsSerde = Serdes.serdeFrom(topSongsSerializer, topSongsDeserializer);

        final Serializer<Average> averageSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", Average.class);
        averageSerializer.configure(serdeProps, false);

        final Deserializer<Average> averageDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", Average.class);
        averageDeserializer.configure(serdeProps, false);

        final Serde<Average> averageSerde = Serdes.serdeFrom(averageSerializer, averageDeserializer);
 
        KStream<String, DetailedSong> details = builder.stream("details", Consumed.with(Serdes.String(), detailedSongSerde));
            
        // Sum up and count song ratings
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

        // Calculate the average rating for each song
        KStream<String, Average> averages = countedRatings.toStream().map((key, value) -> KeyValue.pair(key, new Average(key, value.rating / (double) value.count, value.count)));

        // Aggregate the averaged songs and create a top 50 list
        KTable<String, TopSongs> topSongList = averages
            .groupBy(
                (key, value) -> value.name,
                Grouped.with(Serdes.String(), averageSerde))
            .aggregate(
                () -> new TopSongs(),
                (aggKey, newValue, topList) -> {
                    // add the new song
                    topList.songList[50] = newValue;
                    // sort the list to see if the new song belongs in the top 50
                    Arrays.sort(topList.songList, new SortByRating());
                    // remove the 51st song from the list
                    topList.songList[50] = null;
                    return topList;
                }, 
                Materialized.with(Serdes.String(), topSongsSerde));

        topSongList.toStream().to("top-songs", Produced.with(Serdes.String(), topSongsSerde));
 
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