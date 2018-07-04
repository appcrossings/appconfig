package com.appcrossings.config.file;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.processor.ProcessorSelector;
import com.appcrossings.config.source.DefaultConfigSource;
import com.appcrossings.config.source.StreamPacket;
import com.appcrossings.config.source.StreamSource;

public class DefaultFileConfigSource extends DefaultConfigSource {

  private final static Logger log = LoggerFactory.getLogger(DefaultFileConfigSource.class);

  protected DefaultFileConfigSource(StreamSource source, Map<String, Object> values) {
    super(source, values);
  }

  @Override
  public Properties getRaw(String path) {

    Optional<StreamPacket> stream = streamSource.stream(path);
    Properties p = new Properties();

    if (stream.isPresent() && stream.get().hasContent())
      p = ProcessorSelector.process(stream.get().getUri().toString(),
          stream.get().getInputStream());

    return p;
  }


  @Override
  public boolean isCompatible(StreamSource source) {
    return source instanceof DefaultFileStreamSource;
  }

}
