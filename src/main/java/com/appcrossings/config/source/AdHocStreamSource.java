package com.appcrossings.config.source;

import java.net.URI;
import java.util.Optional;

public interface AdHocStreamSource {

  /**
   * Streams the absolute fully qualified URI provided.
   * 
   * @param uri
   * @return
   */
  public Optional<PropertyPacket> stream(URI uri);

}
