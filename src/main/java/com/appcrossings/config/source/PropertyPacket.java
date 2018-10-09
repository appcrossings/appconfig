package com.appcrossings.config.source;

import java.net.URI;
import java.util.HashMap;

@SuppressWarnings("serial")
public class PropertyPacket extends HashMap<String, Object> {

  private String eTag;
  private final URI uri;

  public PropertyPacket(URI uri) {
    super();
    this.uri = uri;
  }

  public String getETag() {
    return eTag;
  }

  public URI getUri() {
    return uri;
  }

  public void setETag(String eTag) {
    this.eTag = eTag;
  }

}
