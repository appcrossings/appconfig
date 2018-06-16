package com.appcrossings.config;

import java.net.URI;
import java.util.Optional;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.properties.EncryptableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.discovery.ConfigDiscoveryStrategy;
import com.appcrossings.config.discovery.DefaultMergeStrategy;
import com.appcrossings.config.discovery.HostsFileDiscoveryStrategy;
import com.appcrossings.config.exception.InitializationException;
import com.appcrossings.config.source.ConfigSource;
import com.appcrossings.config.util.CfgrdURI;
import com.appcrossings.config.util.StringUtils;
import com.appcrossings.config.util.UriUtil;

/**
 * 
 * @author Krzysztof Karski
 *
 */
public class ConfigClient implements Config {

  public enum Method {

    /**
     * Fully qualified URI to the properites location
     */
    ABSOLUTE_URI,

    /**
     * A cfgrd:// uri format pointing at a repo name and relative path with the repo
     */
    CONFIGRD_URI,

    /**
     * Lookup properties location via hosts file
     */
    HOST_FILE,

    /**
     * Lookup properties location via configrd server
     */
    REDIRECT;
  }

  private class ReloadTask extends TimerTask {

    @Override
    public void run() {
      try {

        init();

      } catch (Exception e) {
        logger.error("Error refreshing configs", e);
      }
    }
  }


  private final static Logger logger = LoggerFactory.getLogger(ConfigClient.class);

  private StandardPBEStringEncryptor encryptor = null;

  public final Environment environment = new Environment();

  private final AtomicReference<Properties> loadedProperties =
      new AtomicReference<>(new Properties());

  private ConfigDiscoveryStrategy lookupStrategy = new HostsFileDiscoveryStrategy();

  private final Method method;

  protected final String REPO_DEF_PATH = "classpath:repo-defaults.yml";

  protected String repoDefLocation;

  protected final ConfigSourceResolver sourceResolver;

  protected final URI startLocation;

  protected StringUtils strings;

  private AtomicReference<Timer> timer = new AtomicReference<Timer>();

  protected Integer timerTTL = 0;

  /**
   * 
   * @param uri - The path of the starting point for config exploration. Can be a default.properties
   *        or hosts.properties path
   * @param refresh - The period in seconds at which the config properties should be refreshed. 0
   *        indicates no automated timer
   * @throws Exception
   */
  public ConfigClient(String uri) {

    assert StringUtils.hasText(uri) : "Host or properties file path null or empty";
    this.startLocation = URI.create(uri);
    this.method = Method.ABSOLUTE_URI;
    this.sourceResolver = new ConfigSourceResolver(REPO_DEF_PATH);
  }

  /**
   * 
   * @param uri The path of the starting point for config exploration. Can be a default.properties
   *        or hosts.properties path
   * @throws Exception
   */
  public ConfigClient(String repoDefPath, String uri, Method method) {

    assert StringUtils.hasText(repoDefPath) : "repo.def.path is null or empty";
    assert StringUtils.hasText(uri) : "Host or properties file path null or empty";
    assert method != null : "Method must be specified";

    this.repoDefLocation = repoDefPath;
    this.startLocation = URI.create(uri);
    this.method = method;
    this.sourceResolver = new ConfigSourceResolver(this.repoDefLocation);
  }

  public Environment getEnvironment() {
    return environment;
  }

  public Properties getProperties() {
    Properties props = new Properties();
    props.putAll(loadedProperties.get());
    return props;
  }

  public <T> T getProperty(String key, Class<T> clazz) {

    String value = loadedProperties.get().getProperty(key);

    if (StringUtils.hasText(value)) {
      return strings.cast(value, clazz);
    }

    return null;
  }

  public <T> T getProperty(String key, Class<T> clazz, T value) {

    T val = getProperty(key, clazz);

    if (val != null && val != "")
      return val;

    return value;

  }

  public ConfigSourceResolver getSourceResolver() {
    return sourceResolver;
  }

  public void init() {

    Optional<URI> startPath = Optional.empty();

    if (this.method.equals(Method.ABSOLUTE_URI)) {

      if(UriUtil.validate(startLocation).isAbsolute().invalid()) {
        throw new IllegalArgumentException("Uri must be an absolute URI to the config location.");
      }
      
      startPath = Optional.of(this.startLocation);

    } else {

      /*
       * Any of the below resolvers can return either an absolute URI or a cfgrd URI therefore a
       * repo configuration is required. They SHOULD NOT return another redirect URI but there is
       * nothing preventing it
       */

      if (this.method.equals(Method.HOST_FILE)) {

        startPath = resolveConfigPathFromHostFile(startLocation);

      } else if (this.method.equals(Method.REDIRECT)) {

        startPath = resolveConfigPathFromConfigrd(startLocation);

      } else if (this.method.equals(Method.CONFIGRD_URI)) {

        startPath = Optional.of(this.startLocation);

      }
    }


    Optional<ConfigSource> configSource = Optional.empty();

    if (startPath.isPresent()) {

      configSource = resolveConfigSource(startPath.get());

    } else {

      logger.error(
          "Unable to locate a config properties path using search location " + this.startLocation);

      throw new InitializationException(
          "Unable to locate a config properties path using search location " + this.startLocation);
    }

    final MergeStrategy merge = new DefaultMergeStrategy();

    if (configSource.isPresent()) {

      final String path = UriUtil.getPath(startPath.get());

      Properties p = configSource.get().get(path);

      if (encryptor != null)
        p = new EncryptableProperties(p, encryptor);

      if (p.isEmpty()) {
        logger.warn("Config location " + startPath.get()
            + " return an empty set of properties. Please check the location");
      }

      merge.addConfig(p);

    } else {

      logger.error("Unable to locate a config source for search location " + this.startLocation
          + " and config path " + startPath.toString());

      throw new InitializationException("Unable to locate a config source for search location "
          + this.startLocation + " and config path " + startPath.toString());

    }

    merge.addConfig(environment.getProperties());
    Properties merged = merge.merge();
    strings = new StringUtils(merged);

    if (merged.isEmpty()) {
      logger.warn("Properties collection returned empty per search location " + this.startLocation
          + " and config path " + startPath.toString()
          + ". If this is unexpected, please check your configuration.");
    } else {

      for (Object key : merged.keySet()) {

        String value = merged.getProperty((String) key);
        loadedProperties.get().put(key, strings.fill(value));

      }
    }

    logger.info("ConfigClient initialized.");
  }

  protected Optional<URI> resolveConfigPathFromConfigrd(URI serverPath) {

    return Optional.empty();

  }

  protected Optional<URI> resolveConfigPathFromHostFile(URI hostFilePath) {

    String environmentName = environment.detectEnvironment();
    String hostName = environment.detectHostName();

    Properties hosts = new Properties();
    logger.info("Loading hosts file at " + startLocation);
    Optional<URI> startPath = Optional.empty();

    Optional<ConfigSource> source = this.sourceResolver.buildAdHocConfigSource(hostFilePath);

    if (source.isPresent()) {
      String path = UriUtil.getPath(hostFilePath);

      hosts = source.get().getRaw(path);
      startPath = lookupStrategy.lookupConfigPath(hosts, environment.getProperties());
    }

    return startPath;
  }

  protected Optional<ConfigSource> resolveConfigSource(URI startPath) {

    Optional<ConfigSource> cs = Optional.empty();

    if (CfgrdURI.isCfgrdURI(startPath)) {

      CfgrdURI cfgrd = new CfgrdURI(startPath);

      cs = this.sourceResolver.findByRepoName(cfgrd.getRepoName());

    } else {

      cs = this.sourceResolver.buildAdHocConfigSource(startPath);
    }

    return cs;
  }

  /**
   * Set password on the encryptor. If an encryptor isn't configured, a BasicTextEncryptor will be
   * initialized and the password set on it. The basic assumed encryption algorithm is
   * PBEWithMD5AndDES. This can be changed by setting the StandardPBEStringEncryptor.
   * 
   * @param password
   */
  public void setPassword(String password) {

    this.encryptor = new StandardPBEStringEncryptor();
    EnvironmentStringPBEConfig configurationEncryptor = new EnvironmentStringPBEConfig();
    configurationEncryptor.setAlgorithm("PBEWithMD5AndDES");
    configurationEncryptor.setPassword(password);
    encryptor.setConfig(configurationEncryptor);

  }

  protected void setRefreshRate(Integer refresh) {

    this.timerTTL = refresh;

    if (timer.get() != null && (refresh == 0L || refresh == null)) {

      timer.get().cancel();
      return;

    } else if (refresh > 0) {

      Timer t2 = new Timer(true);
      t2.schedule(new ReloadTask(), refresh * 1000, refresh * 1000);

      Timer t1 = timer.getAndSet(t2);
      if (t1 != null)
        t1.cancel();
    }

  }

  /**
   * Override default text encryptor (StandardPBEStringEncryptor). Enables overriding both password
   * and algorithm.
   * 
   * @param encryptor
   */
  public void setTextEncryptor(StandardPBEStringEncryptor config) {
    this.encryptor = config;
  }


}
