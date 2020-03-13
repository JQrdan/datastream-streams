FROM maven:3.6.3-ibmjava-8-alpine

COPY src /opt/kafka/streams/src/
COPY pom.xml /opt/kafka/streams

WORKDIR /opt/kafka/streams

RUN mvn package

ENTRYPOINT [ "mvn", "exec:java", "-Dexec.mainClass=com.datastream.streams.SongStream" ]
