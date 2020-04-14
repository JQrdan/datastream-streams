package com.datastream.streams.messages;

import java.util.List;

public class Album {
  public String albumID;
  public double averageRating;
  public int numRatings;
  public List<DetailedSong> songs;

  public Album() {}

  public Album(String albumID, double averageRating, int numRatings, List<DetailedSong> songs) {
    this.albumID = albumID;
    this.averageRating = averageRating;
    this.numRatings = numRatings;
    this.songs = songs;
  }
}