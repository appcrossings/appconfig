package com.appcrossings.config.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
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

public class DefaultHttpSource implements ConfigSource, StreamingConfigSource {

  private final static Logger log = LoggerFactory.getLogger(DefaultHttpSource.class);
  protected HttpRepoDef repoConfig;

  @Override
  public Properties fetchConfig(String propertiesPath) {

    String fullPath = resolveFullFilePath(propertiesPath, repoConfig.getFileName());
    Properties p = new Properties();

    if (isURL(fullPath)) {
      try (InputStream stream = stream(fullPath)) {

        if (stream == null)
          return p;

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

      } catch (Exception e) {
        log.warn("File " + fullPath + " not found.", e);
      }
    }

    return p;


  }

  @Override
  public Properties fetchHostEntries(String hostsFile, String hostsFileName) {
    return null;
  }

  @Override
  public RepoDef getSourceConfiguration() {
    return repoConfig;
  }

  @Override
  public String getSourceName() {
    return ConfigSource.HTTPS;
  }

  @Override
  public boolean isCompatible(String path) {
    final String prefix = path.trim().substring(0, path.indexOf("/"));
    return (prefix.toLowerCase().startsWith("http"));
  }

  protected boolean isURL(String path) {
    return path.trim().startsWith("http");
  }

  @Override
  public ConfigSource newInstance(String name, Map<String, Object> values,
      Map<String, Object> defaults) {

    DefaultHttpSource s = new DefaultHttpSource();

    final Map<String, Object> merged = new HashMap<>(defaults);

    if (!values.isEmpty() && values.containsKey(s.getSourceName()))
      merged.putAll((Map) values.get(s.getSourceName()));

    HttpRepoDef def = new HttpRepoDef(name, merged);
    s.repoConfig = (HttpRepoDef) def;
    return s;
  }

  public String resolveFullFilePath(String propertiesPath, String fileName) {

    String fullPath = "";
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

    if (path.toLowerCase().contains(fileName.toLowerCase())) {
      fullPath = path;
    } else if (path.substring(path.lastIndexOf(File.separator)).contains(".")) {
      fullPath = path;
    } else if (!path.endsWith(File.separator) && !fileName.startsWith(File.separator)) {
      fullPath = path + "/" + fileName;
    } else {
      fullPath = path + fileName;
    }
    return fullPath;
  }

  @Override
  public InputStream stream(String propertiesPath) throws IOException {

    try {
      return new URL(propertiesPath).openStream();
    } catch (IOException e) {
      return null;
    }
  }

  @Override
  public Properties traverseConfigs(String propertiesPath) {
    try {

      if (StringUtils.hasText(propertiesPath)) {

        final UriUtil uri = new UriUtil(propertiesPath);

        do {

          Properties temp = fetchConfig(uri.toString());
          repoConfig.getStrategy().addConfig(temp);
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
