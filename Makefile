start:
	mvn exec:java -Dexec.mainClass=com.datastream.streams.SongStream -Dlog4j.configuration=file:src/main/resources/log4j.properties

test:
	mvn exec:java -Dexec.mainClass=com.datastream.streams.TestStream