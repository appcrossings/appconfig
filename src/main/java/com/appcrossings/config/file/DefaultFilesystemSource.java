package com.appcrossings.config.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.Config;
import com.appcrossings.config.source.ConfigSource;
import com.appcrossings.config.source.RepoDef;
import com.appcrossings.config.source.StreamingConfigSource;
import com.appcrossings.config.util.JsonProcessor;
import com.appcrossings.config.util.PropertiesProcessor;
import com.appcrossings.config.util.StringUtils;
import com.appcrossings.config.util.YamlProcessor;

public class DefaultFilesystemSource implements ConfigSource, StreamingConfigSource {

  private final static Logger log = LoggerFactory.getLogger(DefaultFilesystemSource.class);
  private FileRepoDef repoConfig;

  @Override
  public InputStream stream(String fullPath, Optional<RepoDef> repo) throws IOException {

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
  public Properties fetchConfig(String propertiesPath, Optional<RepoDef> repo) {

    String fileName = Config.DEFAULT_PROPERTIES_FILE_NAME;

    if (repo.isPresent()) {
      assert repo.get() instanceof FileRepoDef : "Repo definitino must be file repo def";
      FileRepoDef fRepo = (FileRepoDef) repo.get();
      fileName = fRepo.getConfigFileName();
    }

    Properties p = new Properties();

    String fullPath = resolveFullFilePath(propertiesPath, fileName);

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

      log.info("Not found.");

    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }

    return p;
  }

  public Properties fetchHostEntries(String hostsFile, String hostsFileName) {

    log.info("Fetching hosts file from path: " + hostsFile);
    String fullPath = resolveFullFilePath(hostsFile, hostsFileName);
    return fetchConfig(fullPath, Optional.empty());
  }

  public Properties traverseConfigs(String propertiesPath, Optional<RepoDef> repo) {

    try {

      FileRepoDef fRepo = null;

      if (repo.isPresent()) {
        assert repo.get() instanceof FileRepoDef : "Repo definitino must be file repo def";
        fRepo = (FileRepoDef) repo.get();
      } else {
        fRepo = new FileRepoDef();
      }

      if (StringUtils.hasText(propertiesPath)) {

        do {

          fRepo.getStrategy().addConfig(fetchConfig(propertiesPath, repo));
          propertiesPath = stripDir(propertiesPath);

        } while (new File(propertiesPath).getParent() != null);
      }

      return fRepo.getStrategy().merge();

    } catch (Exception e) {
      // should never happen
    }

    return new Properties();
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

    if (propertiesPath.toLowerCase().contains(propertiesFileName.toLowerCase())) {

      fullPath = propertiesPath;

    } else if (propertiesPath.substring(propertiesPath.lastIndexOf(File.separator)).contains(".")) {

      fullPath = propertiesPath;

    } else if (!propertiesPath.endsWith(File.separator)
        && !propertiesFileName.startsWith(File.separator)) {

      fullPath = propertiesPath + "/" + propertiesFileName;

    } else {

      fullPath = propertiesPath + propertiesFileName;

    }

    return fullPath;
  }

  @Override
  public String getSourceName() {
    return ConfigSource.FILE_SYSTEM;
  }

  @Override
  public boolean isCompatible(String paths) {

    final String prefix = paths.trim().substring(0, paths.indexOf("/"));
    return (prefix == "" || prefix.equals(File.separator)
        || prefix.toLowerCase().startsWith("file:")
        || prefix.toLowerCase().startsWith("classpath"));
  }

  @Override
  public ConfigSource newInstance(String name, Map<String, Object> values) {

    DefaultFilesystemSource s = new DefaultFilesystemSource();
    FileRepoDef def = new FileRepoDef(name, values);
    s.repoConfig = (FileRepoDef) def;
    return s;
  }

  @Override
  public RepoDef getSourceConfiguration() {
    return repoConfig;
  }

}
