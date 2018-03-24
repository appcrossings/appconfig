package com.appcrossings.config.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.Config;
import com.appcrossings.config.source.BaseRepoDef;
import com.appcrossings.config.source.ConfigSource;
import com.appcrossings.config.source.RepoDef;
import com.appcrossings.config.source.StreamingConfigSource;
import com.appcrossings.config.util.JsonProcessor;
import com.appcrossings.config.util.PropertiesProcessor;
import com.appcrossings.config.util.YamlProcessor;

public class DefaultHttpSource implements ConfigSource, StreamingConfigSource {

  private final static Logger log = LoggerFactory.getLogger(DefaultHttpSource.class);
  private HttpRepoDef repoConfig;

  @Override
  public Properties fetchConfig(String propertiesPath, Optional<RepoDef> repo) {

    String fileName = Config.DEFAULT_PROPERTIES_FILE_NAME;

    if (repo.isPresent()) {
      assert repo.get() instanceof HttpRepoDef : "Repo definition must be a http repo definition";
      HttpRepoDef httpRepo = (HttpRepoDef) repo.get();
      fileName = httpRepo.getConfigFileName();
    }

    String fullPath = resolveFullPathName(propertiesPath, fileName);
    Properties p = new Properties();

    if (isURL(fullPath)) {
      try (InputStream stream = stream(fullPath, repo)) {

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
        log.warn("File " + fullPath + " not found.");
      } catch (IOException e) {
        log.error(e.getMessage(), e);
        throw new RuntimeException(e);
      }
    } else {
      throw new IllegalArgumentException("Path is not a http URL location.");
    }

    return p;


  }

  public String resolveFullPathName(String propertiesPath, String fileName) {

    String fullPath = "";

    if (propertiesPath.toLowerCase().contains(fileName.toLowerCase()))
      fullPath = propertiesPath;
    else if (!propertiesPath.endsWith(File.separator) && !fileName.startsWith(File.separator))
      fullPath = propertiesPath + "/" + fileName;
    else
      fullPath = propertiesPath + fileName;

    return fullPath;
  }

  @Override
  public Properties fetchHostEntries(String hostsFile, String hostsFileName) {
    return null;
  }

  protected boolean isURL(String path) {
    return path.trim().startsWith("http");
  }

  @Override
  public Properties traverseConfigs(String propertiesPath, Optional<RepoDef> repo) {
    // TODO Auto-generated method stub
    return null;
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

  @Override
  public InputStream stream(String propertiesPath, Optional<RepoDef> repo) throws IOException {
    return new URL(propertiesPath).openStream();
  }

  @Override
  public ConfigSource newInstance(String name, Map<String, Object> values) {

    HttpRepoDef def = new HttpRepoDef(name, values);
    DefaultHttpSource s = new DefaultHttpSource();
    s.repoConfig = (HttpRepoDef) def;
    return s;
  }

  @Override
  public RepoDef getSourceConfiguration() {
    return repoConfig;
  }

}
