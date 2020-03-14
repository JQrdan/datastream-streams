details:
	mvn exec:java -Dexec.mainClass=com.datastream.streams.DetailSongsStream -Dlog4j.configuration=file:src/main/resources/log4j.properties

genres:
	mvn exec:java -Dexec.mainClass=com.datastream.streams.PopularGenreStream -Dlog4j.configuration=file:src/main/resources/log4j.properties