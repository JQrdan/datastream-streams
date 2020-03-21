package com.datastream.streams.messages;

public class Average {
  public String name;
  public double averageRating;
  public int numRatings;

  public Average() {}

  public Average(String name, double averageRating, int numRatings) {
    this.name = name;
    this.averageRating = averageRating;
    this.numRatings = numRatings;
  }
}