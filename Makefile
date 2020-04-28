details:
	mvn exec:java -Dexec.mainClass=com.datastream.streams.DetailSongsStream -Dlog4j.configuration=file:src/main/resources/log4j.properties

genres:
	mvn exec:java -Dexec.mainClass=com.datastream.streams.PopularGenreStream -Dlog4j.configuration=file:src/main/resources/log4j.properties

artists:
	mvn exec:java -Dexec.mainClass=com.datastream.streams.ArtistStream -Dlog4j.configuration=file:src/main/resources/log4j.properties

albums:
	mvn exec:java -Dexec.mainClass=com.datastream.streams.AlbumStream -Dlog4j.configuration=file:src/main/resources/log4j.properties

build-docker:
	docker build . -t datastream-stream-details:1.0.0 -f Dockerfile-details
	docker build . -t datastream-stream-genres:1.0.0 -f Dockerfile-genres
	docker build . -t datastream-stream-artists:1.0.0 -f Dockerfile-artists
	docker build . -t datastream-stream-albums:1.0.0 -f Dockerfile-artists