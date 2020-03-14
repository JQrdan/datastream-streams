package com.datastream.streams.messages;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "userID",
  "songID",
  "rating",
  "albumID",
  "artistID",
  "genreID"
})
public class DetailedSong {

  @JsonProperty("userID")
  private String userID;
  @JsonProperty("songID")
  private String songID;
  @JsonProperty("rating")
  private String rating;
  @JsonProperty("albumID")
  private String albumID;
  @JsonProperty("artistID")
  private String artistID;
  @JsonProperty("genreID")
  private String genreID;

  public DetailedSong(){}

  public DetailedSong(String userID, String songID, String rating, String albumID, String artistID, String genreID) {
    this.userID = userID;
    this.songID = songID;
    this.rating = rating;
    this.albumID = albumID;
    this.artistID = artistID;
    this.genreID = genreID;
  }

  @JsonProperty("userID")
  public String getUserID() {
    return userID;
  }

  @JsonProperty("userID")
  public void setUserID(String userID) {
    this.userID = userID;
  }

  @JsonProperty("songID")
  public String getSongID() {
    return songID;
  }

  @JsonProperty("songID")
  public void setSongID(String songID) {
    this.songID = songID;
  }

  @JsonProperty("rating")
  public String getRating() {
    return rating;
  }

  @JsonProperty("rating")
  public void setRating(String rating) {
    this.rating = rating;
  }

  @JsonProperty("albumID")
  public String getAlbumID() {
    return albumID;
  }

  @JsonProperty("albumID")
  public void setAlbumID(String albumID) {
    this.albumID = albumID;
  }

  @JsonProperty("artistID")
  public String getArtistID() {
    return artistID;
  }

  @JsonProperty("artistID")
  public void setArtistID(String artistID) {
    this.artistID = artistID;
  }

  @JsonProperty("genreID")
  public String getGenreID() {
    return genreID;
  }

  @JsonProperty("genreID")
  public void setGenreID(String genreID) {
    this.genreID = genreID;
  }
}
