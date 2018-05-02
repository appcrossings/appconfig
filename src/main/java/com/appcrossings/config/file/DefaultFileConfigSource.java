package com.appcrossings.config.file;

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

public class DefaultFileConfigSource implements ConfigSource {

  private final static Logger log = LoggerFactory.getLogger(DefaultFileConfigSource.class);
  private final StreamSource streamSource;

  public DefaultFileConfigSource(StreamSource source) {
    this.streamSource = source;
  }

  @Override
  public StreamSource getStreamSource() {
    return streamSource;
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

  public Properties get(String path) {


    final MergeStrategy merge = new DefaultMergeStrategy();

    if (StringUtils.hasText(path)) {

      final DirectoryTraverse traverse = new DirectoryTraverse(path);

      do {

        Properties props = getRaw(traverse.decend().toString());
        merge.addConfig(props);

      } while (traverse.hasNextDown());

    }

    return merge.merge();

  }

  @Override
  public boolean isCompatible(StreamSource source) {
    return source instanceof DefaultFileStreamSource;
  }

  @Override
  public String getName() {
    return streamSource.getSourceConfig().getName();
  }


}
