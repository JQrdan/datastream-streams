package com.datastream.streams.messages;

import java.util.List;

public class TupleList<T> {
  public int count;
  public int rating;
  public List<T> list;

  public TupleList(){}

  public TupleList(int count, int rating, List<T> list) {
      this.count = count;
      this.rating = rating;
      this.list = list;
  }
}