package com.datastream.streams;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Serialized;
import org.apache.kafka.streams.kstream.ValueJoiner;
 
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import com.datastream.streams.serialization.*;
import com.datastream.streams.messages.Song;
import com.datastream.streams.messages.SongAttributes;
import com.datastream.streams.messages.DetailedSong;
 
public class SongStream {
 
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-pipe");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9092,kafka2:9093,kafka3:9094");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 4);
 
        final StreamsBuilder builder = new StreamsBuilder();

        Map<String, Object> serdeProps = new HashMap<>();

        // Song Serdes
        final Serializer<Song> songSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", Song.class);
        songSerializer.configure(serdeProps, false);

        final Deserializer<Song> songDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", Song.class);
        songDeserializer.configure(serdeProps, false);

        final Serde<Song> songSerde = Serdes.serdeFrom(songSerializer, songDeserializer);

        // SongAttributes Serdes
        final Serializer<SongAttributes> songAttributesSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", SongAttributes.class);
        songAttributesSerializer.configure(serdeProps, false);

        final Deserializer<SongAttributes> songAttributesDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", SongAttributes.class);
        songAttributesDeserializer.configure(serdeProps, false);

        final Serde<SongAttributes> songAttributesSerde = Serdes.serdeFrom(songAttributesSerializer, songAttributesDeserializer);

        // // DetailedSong Serdes
        // final Serializer<DetailedSong> detailedSongSerializer = new JsonPOJOSerializer<>();
        // serdeProps.put("JsonPOJOClass", DetailedSong.class);
        // detailedSongSerializer.configure(serdeProps, false);

        // final Deserializer<DetailedSong> detailedSongDeserializer = new JsonPOJODeserializer<>();
        // serdeProps.put("JsonPOJOClass", DetailedSong.class);
        // detailedSongDeserializer.configure(serdeProps, false);

        // final Serde<DetailedSong> detailedSongSerde = Serdes.serdeFrom(detailedSongSerializer, detailedSongDeserializer);
 
        KStream<String, Song> songs = builder.stream("songs", Consumed.with(Serdes.String(), songSerde));
        // KTable<String, SongAttributes> songAttributess = builder.table("songAttributess", Consumed.with(Serdes.String(), songAttributesSerde));

        // KStream<String, DetailedSong> detailedSongs = songs
        //     .leftJoin(songAttributess, new ValueJoiner<Song, SongAttributes, DetailedSong>() {
        //         @Override
        //         public DetailedSong apply(Song song, SongAttributes songAttributes) {
        //             DetailedSong detailedSong = new DetailedSong();
        //             detailedSong.userID = song.userID;
        //             detailedSong.songID = song.songID;
        //             detailedSong.rating = song.rating;
        //             detailedSong.albumID = songAttributes.albumID;
        //             detailedSong.artistID = songAttributes.artistID;
        //             detailedSong.genreID = songAttributes.artistID;

        //             return detailedSong;
        //         }
        //     });
 
        // songAttributess.to("apple", Produced.with(Serdes.String(), songAttributesSerde));
        songs.to("songsOutput", Produced.with(Serdes.String(), songSerde));
 
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