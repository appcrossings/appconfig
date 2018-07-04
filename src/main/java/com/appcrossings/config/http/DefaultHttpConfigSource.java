package com.appcrossings.config.http;

import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.processor.ProcessorSelector;
import com.appcrossings.config.source.DefaultConfigSource;
import com.appcrossings.config.source.StreamPacket;
import com.appcrossings.config.source.StreamSource;

public class DefaultHttpConfigSource extends DefaultConfigSource {

  private final static Logger log = LoggerFactory.getLogger(DefaultHttpConfigSource.class);

  protected DefaultHttpConfigSource(StreamSource source, Map<String, Object> values) {
    super(source, values);
  }

  @Override
  public Properties getRaw(String path) {

    Optional<StreamPacket> stream = streamSource.stream(path);
    URI fullyQualifiedPath = streamSource.prototypeURI(path);
    Properties p = new Properties();

    if (stream.isPresent() && stream.get().hasContent())
      p = ProcessorSelector.process(fullyQualifiedPath.toString(), stream.get().getInputStream());

    return p;
  }

  @Override
  public boolean isCompatible(StreamSource source) {
    return source instanceof DefaultHttpStreamSource;
  }

}
