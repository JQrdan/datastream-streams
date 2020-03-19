package com.datastream.streams.messages;

public class DetailedSong {
  public String userID;
  public String songID;
  public String rating;
  public String albumID;
  public String artistID;
  public String genreID;

  public DetailedSong(){}

  public DetailedSong(String userID, String songID, String rating, String albumID, String artistID, String genreID) {
    this.userID = userID;
    this.songID = songID;
    this.rating = rating;
    this.albumID = albumID;
    this.artistID = artistID;
    this.genreID = genreID;
  }
}
