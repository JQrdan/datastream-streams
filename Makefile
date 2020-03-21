details:
	mvn exec:java -Dexec.mainClass=com.datastream.streams.DetailSongsStream -Dlog4j.configuration=file:src/main/resources/log4j.properties

genres:
	mvn exec:java -Dexec.mainClass=com.datastream.streams.PopularGenreStream -Dlog4j.configuration=file:src/main/resources/log4j.properties

songs:
	mvn exec:java -Dexec.mainClass=com.datastream.streams.PopularSongStream -Dlog4j.configuration=file:src/main/resources/log4j.properties

build-docker:
	docker build . -t datastream-stream-details:1.0.0 -f Dockerfile-details
	docker build . -t datastream-stream-genres:1.0.0 -f Dockerfile-genres
	docker build . -t datastream-stream-songs:1.0.0 -f Dockerfile-songs