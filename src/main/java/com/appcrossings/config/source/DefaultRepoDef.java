package com.appcrossings.config.source;

import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public abstract class DefaultRepoDef implements Serializable, RepoDef {

  protected String name;
  protected String sourceName;
  protected String uri;
  protected String streamSource;
  protected Map<String, String> named = new HashMap<>();

  public Map<String, String> getNamed() {
    return named;
  }

  public void setNamed(Map<String, String> namedPaths) {
    this.named = namedPaths;
  }

  public String getStreamSource() {
    return streamSource;
  }

  public void setStreamSource(String streamSource) {
    this.streamSource = streamSource;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public String getSourceName() {
    return sourceName;
  }

  public void setSourceName(String sourceName) {
    this.sourceName = sourceName;
  }

  protected DefaultRepoDef() {
    super();
  }

  public DefaultRepoDef(String name) {

    super();
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
  
  public abstract String[] valid();
  
  public abstract URI toURI();
}
