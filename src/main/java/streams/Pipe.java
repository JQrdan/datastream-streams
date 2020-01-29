package streams;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import serialization.JsonPOJODeserializer;
import serialization.JsonPOJOSerializer;

public class Pipe {
	
	public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "pipestream");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka1:9092,kafka2:9092,kafka3:9092");
	
        final StreamsBuilder builder = new StreamsBuilder();
        
        Map<String, Object> serdeProps = new HashMap<>();
        
        final Serializer<PipeMessage> pipeSerializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", PipeMessage.class);
        pipeSerializer.configure(serdeProps, false);
        
        final Deserializer<PipeMessage> pipeDeserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", PipeMessage.class);
        pipeDeserializer.configure(serdeProps, false);
        
        final Serde<PipeMessage> pipeSerde  = Serdes.serdeFrom(pipeSerializer, pipeDeserializer);
        
        KStream<String, PipeMessage> source = builder.stream("test-input", Consumed.with(Serdes.String(), pipeSerde));
        source.to("test-output", Produced.with(Serdes.String(), pipeSerde));
        
        final Topology topology = builder.build();
        System.out.println(topology.describe());
        
        final KafkaStreams streams = new KafkaStreams(topology, props);
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