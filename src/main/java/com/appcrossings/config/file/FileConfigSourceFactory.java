package com.appcrossings.config.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import com.appcrossings.config.source.ConfigSource;
import com.appcrossings.config.source.ConfigSourceFactory;
import com.appcrossings.config.source.StreamSource;

public class FileConfigSourceFactory implements ConfigSourceFactory {

  @Override
  public  ConfigSource newConfigSource(String name, final Map<String, Object> values,
      final Map<String, Object> defaults) {

    StreamSource source = newStreamSource(name, values, defaults);

    DefaultFileConfigSource configSource = new DefaultFileConfigSource(source, values);
    return configSource;
  }


  @Override
  public boolean isCompatible(String paths) {

    return (paths == "" || paths.toLowerCase().startsWith(File.separator + File.separator)
        || paths.toLowerCase().startsWith("file:") || paths.toLowerCase().startsWith("classpath"));
  }

  @Override
  public String getSourceName() {
    return StreamSource.FILE_SYSTEM;
  }

  public StreamSource newStreamSource(String name, Map<String, Object> values,
      Map<String, Object> defaults) {
    final Map<String, Object> merged = new HashMap<>(defaults);
    merged.putAll(values);

    FileRepoDef def = new FileRepoDef(name, merged);

    if (def.valid().length > 0) {
      throw new IllegalArgumentException(String.join(",", def.valid()));
    }

    DefaultFileStreamSource source = new DefaultFileStreamSource(def);
    source.init();

    return source;
  }



}
