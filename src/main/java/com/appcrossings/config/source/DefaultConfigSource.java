package com.appcrossings.config.source;

import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.map.HashedMap;
import com.appcrossings.config.MergeStrategy;
import com.appcrossings.config.discovery.DefaultMergeStrategy;
import com.appcrossings.config.util.DirectoryTraverse;
import com.appcrossings.config.util.StringUtils;

public abstract class DefaultConfigSource implements ConfigSource {

  protected final Map<String, String> namedPaths;
  protected final StreamSource streamSource;

  public DefaultConfigSource(StreamSource source, Map<String, Object> values) {
    this.streamSource = source;

    if (values.containsKey(RepoDef.NAMED_PATHS_FIELD)) {
      this.namedPaths = (Map) values.get(RepoDef.NAMED_PATHS_FIELD);
    } else {
      this.namedPaths = new HashedMap();
    }
  }

  public Map<String, Object> get(String path, Set<String> names) {

    final MergeStrategy merge = new DefaultMergeStrategy();

    if (!names.isEmpty()) {

      for (String name : names) {

        path = namedPaths.get(name);

        if (!StringUtils.hasText(path))
          continue;

        final DirectoryTraverse traverse = new DirectoryTraverse(path);

        do {

          Map<String, Object> props = getRaw(traverse.decend().toString());
          merge.addConfig(props);

        } while (traverse.hasNextDown());

      }

    } else if (StringUtils.hasText(path)) {

      final DirectoryTraverse traverse = new DirectoryTraverse(path);

      do {

        Map<String, Object> props = getRaw(traverse.decend().toString());
        merge.addConfig(props);

      } while (traverse.hasNextDown());

    }

    return merge.merge();

  }


  @Override
  public String getName() {
    return streamSource.getSourceConfig().getName();
  }

  protected Map<String, String> getNamedPaths() {
    return namedPaths;
  }

  @Override
  public StreamSource getStreamSource() {
    return streamSource;
  }

}
