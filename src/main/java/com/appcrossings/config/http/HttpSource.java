package com.appcrossings.config.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.Config;
import com.appcrossings.config.ConfigSource;

public class HttpSource implements ConfigSource {

  private final static Logger log = LoggerFactory.getLogger(HttpSource.class);

  @Override
  public Properties traverseConfigs(String propertiesPath, String propertiesFileName) {

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
  public Properties resolveConfigPath(String hostsFile) {
    return traverseConfigs(hostsFile, Config.DEFAULT_HOSTS_FILE_NAME);
  }

  protected boolean isURL(String path) {
    return path.trim().startsWith("http");
  }

}
