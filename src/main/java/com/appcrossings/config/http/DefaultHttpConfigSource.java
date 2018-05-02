package com.appcrossings.config.http;

import java.net.URI;
import java.util.Optional;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.MergeStrategy;
import com.appcrossings.config.discovery.DefaultMergeStrategy;
import com.appcrossings.config.processor.ProcessorSelector;
import com.appcrossings.config.source.ConfigSource;
import com.appcrossings.config.source.StreamPacket;
import com.appcrossings.config.source.StreamSource;
import com.appcrossings.config.util.DirectoryTraverse;
import com.appcrossings.config.util.StringUtils;

public class DefaultHttpConfigSource implements ConfigSource {

  private final static Logger log = LoggerFactory.getLogger(DefaultHttpConfigSource.class);
  private final StreamSource streamSource;

  public DefaultHttpConfigSource(StreamSource source) {
    this.streamSource = source;
  }

  @Override
  public StreamSource getStreamSource() {
    return streamSource;
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

  public Properties get(String path) {

    Properties props = new Properties();

    if (StringUtils.hasText(path)) {

      final MergeStrategy merge = new DefaultMergeStrategy();

      final DirectoryTraverse traverse = new DirectoryTraverse(path);

      do {

        Properties p = getRaw(traverse.decend());
        merge.addConfig(p);

      } while (traverse.hasNextDown());

      props = merge.merge();
    }

    return props;
  }

  @Override
  public boolean isCompatible(StreamSource source) {
    return source instanceof DefaultHttpStreamSource;
  }

  @Override
  public String getName() {
    return streamSource.getSourceConfig().getName();
  }

}
