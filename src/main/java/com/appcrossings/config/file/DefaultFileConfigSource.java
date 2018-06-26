package com.appcrossings.config.file;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import org.apache.commons.collections.map.HashedMap;
import org.jasypt.encryption.pbe.config.StringPBEConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.MergeStrategy;
import com.appcrossings.config.discovery.DefaultMergeStrategy;
import com.appcrossings.config.processor.ProcessorSelector;
import com.appcrossings.config.source.ConfigSource;
import com.appcrossings.config.source.RepoDef;
import com.appcrossings.config.source.StreamPacket;
import com.appcrossings.config.source.StreamSource;
import com.appcrossings.config.util.DirectoryTraverse;
import com.appcrossings.config.util.StringUtils;

public class DefaultFileConfigSource implements ConfigSource {

  private final static Logger log = LoggerFactory.getLogger(DefaultFileConfigSource.class);
  private final StreamSource streamSource;
  private final Map<String, String> namedPaths;

  protected DefaultFileConfigSource(StreamSource source, Map<String, Object> values) {
    this.streamSource = source;

    if (values.containsKey(RepoDef.NAMED_PATHS_FIELD)) {
      this.namedPaths = (Map) values.get(RepoDef.NAMED_PATHS_FIELD);
    } else {
      this.namedPaths = new HashedMap();
    }

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

  public Properties get(String path, String... names) {

    final MergeStrategy merge = new DefaultMergeStrategy();
    
    if(names.length > 0) {
      
      for(String name: names) {
        
        path = namedPaths.get(name);
        
        if(!StringUtils.hasText(path))
          continue;
          
        final DirectoryTraverse traverse = new DirectoryTraverse(path);

        do {

          Properties props = getRaw(traverse.decend().toString());
          merge.addConfig(props);

        } while (traverse.hasNextDown());
        
      }

    } else if (StringUtils.hasText(path)) {

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
