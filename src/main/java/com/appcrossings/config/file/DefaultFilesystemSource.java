package com.appcrossings.config.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.ConfigSource;
import com.appcrossings.config.ConfigSourceResolver;
import com.appcrossings.config.MergeStrategy;
import com.appcrossings.config.util.StringUtils;

public class DefaultFilesystemSource implements ConfigSource {

  private final static Logger log = LoggerFactory.getLogger(DefaultFilesystemSource.class);

  public InputStream getFileStream(String fullPath) throws FileNotFoundException, IOException {

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

  @Override
  public Properties fetchConfig(String propertiesPath, String propertiesFileName) {

    Properties p = new Properties();

    String fullPath = resolveFullFilePath(propertiesPath, propertiesFileName);

    try (InputStream stream = getFileStream(fullPath);) {

      log.info("Attempting " + fullPath);
      p.load(stream);

    } catch (FileNotFoundException e) {

      log.info("Not found.");

    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }

    return p;
  }

  public Properties resolveConfigPath(String hostsFile, String hostsFileName) {

    log.info("Fetching hosts file from path: " + hostsFile);
    return fetchConfig(hostsFile, hostsFileName);
  }

  public Properties traverseConfigs(String propertiesPath, String propertiesFileName,
      MergeStrategy strategy) {

    if (StringUtils.hasText(propertiesPath)) {

      do {

        strategy.addConfig(fetchConfig(propertiesPath, propertiesFileName));
        propertiesPath = stripDir(propertiesPath);

      } while (new File(propertiesPath).getParent() != null);
    }

    // Finally, check classpath
    // if (searchClasspath) {
    strategy.addConfig(fetchConfig("classpath:/config/", propertiesFileName));
    strategy.addConfig(fetchConfig("classpath:", propertiesFileName));
    // }

    return strategy.merge();

  }

  protected String stripDir(String path) {

    int i = path.lastIndexOf("/");

    if (i > 0)
      return path.substring(0, i);

    return "";

  }

  protected boolean isClasspath(String path) {
    return path.trim().startsWith("classpath:");
  }

  protected boolean isFilePath(String path) {
    return path.trim().startsWith("file:");
  }

  protected boolean isPath(String path) {
    URI uri = URI.create(path);
    uri.getScheme();

    return path.trim().startsWith(File.separator);
  }

  protected String resolveFullFilePath(String propertiesPath, String propertiesFileName) {
    String fullPath = "";

    if (propertiesPath.toLowerCase().contains(propertiesFileName.toLowerCase()))
      fullPath = propertiesPath;
    else if (!propertiesPath.endsWith(File.separator)
        && !propertiesFileName.startsWith(File.separator))
      fullPath = propertiesPath + "/" + propertiesFileName;
    else
      fullPath = propertiesPath + propertiesFileName;

    return fullPath;
  }

  @Override
  public String getSourceName() {
    return ConfigSourceResolver.FILE_SYSTEM;
  }

  @Override
  public boolean isCompatible(String paths) {

    final String prefix = paths.trim().substring(0, paths.indexOf("/"));
    return (prefix == "" || prefix.equals(File.separator)
        || prefix.toLowerCase().startsWith("file:")
        || prefix.toLowerCase().startsWith("classpath"));
  }

}
