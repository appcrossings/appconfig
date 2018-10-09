package com.appcrossings.config.source;

import java.util.Map;

public interface RepoDef {
  
  public static final String NAME_FIELD = "name";
  public static final String NAMED_PATHS_FIELD = "named";
  public static final String URI_FIELD = "uri";
  public static final String STREAM_SOURCE_FIELD = "streamSource";
  
  public String getName();
  public String getUri();
  public String getStreamSource();
  public Map<String, String> getNamed();

}

