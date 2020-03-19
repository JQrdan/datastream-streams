package com.datastream.streams.messages;

public class Tuple {
  public int count;
  public int rating;

  public Tuple(){}

  public Tuple(int count, int rating) {
      this.count = count;
      this.rating = rating;
  }
}