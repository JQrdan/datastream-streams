start:
	mvn exec:java -Dexec.mainClass=com.datastream.streams.SongStream

test:
	mvn exec:java -Dexec.mainClass=com.datastream.streams.TestStream