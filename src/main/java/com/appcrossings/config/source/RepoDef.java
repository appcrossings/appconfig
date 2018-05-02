package com.appcrossings.config.source;

public interface RepoDef {
  
  public static final String NAME_FIELD = "name";
  public static final String URI_FIELD = "uri";
  public static final String STREAM_SOURCe_FIELD = "steamSource";
  
  public String getName();
  public String getUri();
  public String getStreamSource();

}

