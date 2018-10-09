package com.appcrossings.config.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.source.DefaultConfigSource;
import com.appcrossings.config.source.PropertyPacket;
import com.appcrossings.config.source.StreamSource;

public class DefaultHttpConfigSource extends DefaultConfigSource {

  private final static Logger log = LoggerFactory.getLogger(DefaultHttpConfigSource.class);

  protected DefaultHttpConfigSource(StreamSource source, Map<String, Object> values) {
    super(source, values);
  }

  @Override
  public Map<String, Object> getRaw(String path) {

    Optional<PropertyPacket> stream = streamSource.stream(path);

    if (!stream.isPresent())
      return new HashMap<>();

    return stream.get();
  }

  @Override
  public boolean isCompatible(StreamSource source) {
    return source instanceof DefaultHttpStreamSource;
  }

}
