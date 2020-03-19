package com.datastream.streams.messages;

public class Average {
  public String genreName;
  public double averageRating;
  public int numRatings;

  public Average() {}

  public Average(String genreName, double averageRating, int numRatings) {
    this.genreName = genreName;
    this.averageRating = averageRating;
    this.numRatings = numRatings;
  }
}