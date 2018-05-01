package com.appcrossings.config.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.source.ConfigSource;
import com.appcrossings.config.source.RepoDef;
import com.appcrossings.config.source.StreamingConfigSource;
import com.appcrossings.config.util.JsonProcessor;
import com.appcrossings.config.util.PropertiesProcessor;
import com.appcrossings.config.util.StringUtils;
import com.appcrossings.config.util.UriUtil;
import com.appcrossings.config.util.YamlProcessor;

public class DefaultFilesystemSource implements ConfigSource, StreamingConfigSource {

  private final static Logger log = LoggerFactory.getLogger(DefaultFilesystemSource.class);
  protected FileRepoDef repoConfig;

  @Override
  public Properties fetchConfig(String propertiesPath) {

    Properties p = new Properties();

    String fullPath = resolveFullFilePath(propertiesPath, repoConfig.getConfigFileName());

    try (InputStream stream = stream(fullPath)) {

      log.info("Attempting " + fullPath);

      if (JsonProcessor.isJsonFile(fullPath)) {

        p = JsonProcessor.asProperties(stream);

      } else if (YamlProcessor.isYamlFile(fullPath)) {

        p = YamlProcessor.asProperties(stream);

      } else if (PropertiesProcessor.isPropertiesFile(fullPath)) {

        p = PropertiesProcessor.asProperties(stream);

      } else {

        log.warn("Unable to process file " + fullPath + ". No compatible file processor found.");

      }


    } catch (FileNotFoundException e) {

      log.info("Not found.");

    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }

    return p;
  }

  @Override
  public RepoDef getSourceConfiguration() {
    return repoConfig;
  }

  @Override
  public String getSourceName() {
    return ConfigSource.FILE_SYSTEM;
  }

  protected boolean isClasspath(String path) {
    return path.trim().startsWith("classpath:");
  }

  @Override
  public boolean isCompatible(String paths) {

    final String prefix = paths.trim().substring(0, paths.indexOf("/"));
    return (prefix == "" || prefix.equals(File.separator)
        || prefix.toLowerCase().startsWith("file:")
        || prefix.toLowerCase().startsWith("classpath"));
  }

  protected boolean isFilePath(String path) {
    return path.trim().startsWith("file:");
  }

  protected boolean isPath(String path) {
    URI uri = URI.create(path);
    uri.getScheme();

    return path.trim().startsWith(File.separator);
  }

  @Override
  public ConfigSource newInstance(String name, final Map<String, Object> values,
      final Map<String, Object> defaults) {

    DefaultFilesystemSource s = new DefaultFilesystemSource();

    final Map<String, Object> merged = new HashMap<>(defaults);

    if (!values.isEmpty() && values.containsKey(s.getSourceName()))
      merged.putAll((Map) values.get(s.getSourceName()));

    FileRepoDef def = new FileRepoDef(name, merged);
    s.repoConfig = (FileRepoDef) def;
    return s;
  }

  protected String resolveFullFilePath(final String propertiesPath, String propertiesFileName) {

    String path = "";
    URI uri = URI.create(propertiesPath);

    if (repoConfig != null
        && (!StringUtils.hasText(uri.getScheme()) || uri.getScheme().startsWith("repo"))) {

      if (StringUtils.hasText(repoConfig.uri)) {

        path = repoConfig.uri;

        if (StringUtils.hasText(repoConfig.root))
          path = path + repoConfig.root;
      }

      if (StringUtils.hasText(repoConfig.getContext())
          && propertiesPath.contains(repoConfig.getContext()))
        path = path + propertiesPath.replaceFirst(repoConfig.getContext(), "");
      else
        path = path + propertiesPath;

    } else {
      path = propertiesPath;
    }

    UriUtil pathUtil = new UriUtil(path);
    pathUtil.appendFileName(propertiesFileName);

    return pathUtil.toString();
  }

  @Override
  public InputStream stream(String fullPath) throws IOException {

    InputStream stream = null;

    if (isFilePath(fullPath)) {

      String trimmed = fullPath.replaceFirst("file:", "");
      stream = new FileInputStream(new File(trimmed));

    } else if (isClasspath(fullPath)) {

      String trimmed = fullPath.replaceFirst("classpath:", "");

      if (!trimmed.startsWith(File.separator))
        trimmed = File.separator + trimmed;
      stream = this.getClass().getResourceAsStream(trimmed);

    } else if (isPath(fullPath)) {

      // could be relative to classpath or filesystem root (i.e. linux)

      stream = this.getClass().getResourceAsStream(fullPath);

      if (stream == null)
        stream = new FileInputStream(new File(fullPath));

    }

    if (stream != null) {
      log.info("Found " + fullPath);
    } else {
      log.info("Not found.");
      throw new FileNotFoundException(fullPath);
    }

    return stream;

  }

  public Properties traverseConfigs(String propertiesPath) {

    try {

      if (StringUtils.hasText(propertiesPath)) {

        final UriUtil uri = new UriUtil(propertiesPath);

        do {

          Properties props = fetchConfig(uri.toString());
          repoConfig.getStrategy().addConfig(props);
          uri.stripPath();

        } while (uri.hasPath());
      }

      return repoConfig.getStrategy().merge();

    } catch (Exception e) {
      // should never happen
    }

    return new Properties();
  }

}
