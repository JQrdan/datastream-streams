package com.datastream.streams;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
 
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import com.datastream.streams.serialization.*;
import com.datastream.streams.messages.Song;
import com.datastream.streams.messages.SongAttributes;
import com.datastream.streams.messages.DetailedSong;
 
public class DetailSongsStream {
 
    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "song-detailer");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9092,kafka2:9093,kafka3:9094");
        props.put(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 4);
        props.put(StreamsConfig.RETRIES_CONFIG, 100);
 
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

        // DetailedSong Serdes
        final Serializer<DetailedSong> detailedSongSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", DetailedSong.class);
        detailedSongSerializer.configure(serdeProps, false);

        final Deserializer<DetailedSong> detailedSongDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", DetailedSong.class);
        detailedSongDeserializer.configure(serdeProps, false);

        final Serde<DetailedSong> detailedSongSerde = Serdes.serdeFrom(detailedSongSerializer, detailedSongDeserializer);
 
        KStream<String, Song> songs = builder.stream("songs", Consumed.with(Serdes.String(), songSerde));
        KTable<String, SongAttributes> songAttributes = builder.table("songattributes", Consumed.with(Serdes.String(), songAttributesSerde));

        KStream<String, DetailedSong> detailedSongs = songs.leftJoin(songAttributes,
            (song, attributes) -> {
                if (attributes == null) {
                    return new DetailedSong(song.userID, song.songID, song.rating, "UNKNOWN", "UNKNOWN", "UNKNOWN");
                } else {
                    return new DetailedSong(song.userID, song.songID, song.rating, attributes.albumID, attributes.artistID, attributes.genreID);
                }
            },
            Joined.with(Serdes.String(), songSerde, songAttributesSerde)
        );
 
        detailedSongs.to("details", Produced.with(Serdes.String(), detailedSongSerde));
 
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