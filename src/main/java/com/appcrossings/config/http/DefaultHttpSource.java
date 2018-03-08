package com.appcrossings.config.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.ConfigSource;
import com.appcrossings.config.ConfigSourceResolver;

public class DefaultHttpSource implements ConfigSource {

  private final static Logger log = LoggerFactory.getLogger(DefaultHttpSource.class);

  @Override
  public Properties fetchConfig(String propertiesPath, String propertiesFileName) {

    String fullPath = "";
    Properties p = new Properties();

    if (propertiesPath.toLowerCase().contains(propertiesFileName.toLowerCase()))
      fullPath = propertiesPath;
    else if (!propertiesPath.endsWith(File.separator)
        && !propertiesFileName.startsWith(File.separator))
      fullPath = propertiesPath + "/" + propertiesFileName;
    else
      fullPath = propertiesPath + propertiesFileName;

    if (isURL(fullPath)) {
      try (InputStream stream = new URL(fullPath).openStream()) {

        if (stream != null)
          p.load(stream);

      } catch (IOException e) {
        log.error(e.getMessage(), e);
        throw new RuntimeException(e);
      }
    } else {
      throw new IllegalArgumentException("Path is not a http URL location.");
    }

    return p;


  }

  @Override
  public Properties resolveConfigPath(String hostsFile, String hostsFileName) {
    return traverseConfigs(hostsFile, hostsFileName);
  }

  protected boolean isURL(String path) {
    return path.trim().startsWith("http");
  }

  @Override
  public Properties traverseConfigs(String propertiesPath, String propertiesFileName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getSourceName() {
    return ConfigSourceResolver.HTTPS;
  }

  @Override
  public boolean isCompatible(String path) {
    final String prefix = path.trim().substring(0, path.indexOf("/"));
    return (prefix.toLowerCase().startsWith("http"));
  }


}
