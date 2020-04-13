package com.datastream.streams.messages;

import java.util.List;

public class Artist {
  public String artistID;
  public double averageRating;
  public int numRatings;
  public List<DetailedSong> songs;

  public Artist() {}

  public Artist(String artistID, double averageRating, int numRatings, List<DetailedSong> songs) {
    this.artistID = artistID;
    this.averageRating = averageRating;
    this.numRatings = numRatings;
    this.songs = songs;
  }
}