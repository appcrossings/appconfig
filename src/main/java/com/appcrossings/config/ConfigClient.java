package com.appcrossings.config;

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
import com.appcrossings.config.source.ConfigSource;
import com.appcrossings.config.strategy.DefaultConfigLookupStrategy;
import com.appcrossings.config.strategy.DefaultMergeStrategy;
import com.appcrossings.config.util.Environment;
import com.appcrossings.config.util.StringUtils;
import com.appcrossings.config.util.UriUtil;

/**
 * 
 * @author Krzysztof Karski
 *
 */
public class ConfigClient implements Config {

  public enum Method {
    DIRECT_PATH, HOST_FILE;
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

  protected String fileNamePattern = DEFAULT_PROPERTIES_FILE_NAME;

  protected String hostsNamePattern = DEFAULT_HOSTS_FILE_NAME;

  private final AtomicReference<Properties> loadedProperties = new AtomicReference<>();

  private ConfigLookupStrategy lookupStrategy = new DefaultConfigLookupStrategy();

  private MergeStrategy mergeStrategy = new DefaultMergeStrategy();

  private final Method method;

  protected final String repoDefLocation;

  protected ConfigSourceResolver sourceResolver;

  protected final String startLocation;

  protected StringUtils strings;

  private AtomicReference<Timer> timer = new AtomicReference<Timer>();

  protected Integer timerTTL = 0;

  protected boolean traverseClasspath = SEARCH_CLASSPATH;

  public ConfigClient(Properties props) throws Exception {

    assert !props
        .containsKey(Config.PATH) : "File path to hosts.properties or start location is mandatory";

    this.startLocation = props.getProperty(Config.PATH);

    if (props.containsKey(Config.REPO_DEF_PATH))
      this.repoDefLocation = props.getProperty(Config.REPO_DEF_PATH);
    else
      this.repoDefLocation = null;

    if (props.containsKey(Config.HOST_NAME))
      getEnvironment().setHostName(props.getProperty(Config.HOST_NAME));

    if (props.containsKey(Config.HOST_FILE_NAME))
      this.hostsNamePattern = props.getProperty(Config.HOST_FILE_NAME);

    if (props.containsKey(Config.PROPERTIES_FILE_NAME))
      this.fileNamePattern = props.getProperty(Config.PROPERTIES_FILE_NAME);

    if (props.containsKey(Config.SEARCH_CLASSPATH))
      this.traverseClasspath = Boolean.parseBoolean(props.getProperty(Config.TRAVERSE_CLASSPATH));

    if (props.containsKey(Config.REFRESH_RATE))
      this.timerTTL = Integer.parseInt((String) props.getProperty(Config.REFRESH_RATE));

    if (props.containsKey(Config.METHOD))
      this.method = Method.valueOf(props.getProperty(Config.METHOD));
    else
      this.method = Method.HOST_FILE;

    if (props.containsKey(Config.CONFIG_MERGE_STRATEGY))
      this.mergeStrategy = (MergeStrategy) props.get(Config.CONFIG_MERGE_STRATEGY);

    if (props.containsKey(Config.CONFIG_LOOKUP_STRATEGY))
      this.lookupStrategy = (ConfigLookupStrategy) props.get(Config.CONFIG_LOOKUP_STRATEGY);

    this.strings = new StringUtils(environment.getProperties());

  }

  /**
   * 
   * @param path - The path of the starting point for config exploration. Can be a
   *        default.properties or hosts.properties path
   * @param refresh - The period in seconds at which the config properties should be refreshed. 0
   *        indicates no automated timer
   * @throws Exception
   */
  public ConfigClient(String path, int refresh, Method method) {
    this(path, method);
    this.timerTTL = refresh;
  }

  /**
   * 
   * @param path The path of the starting point for config exploration. Can be a default.properties
   *        or hosts.properties path
   * @throws Exception
   */
  public ConfigClient(String path, Method method) {
    this.startLocation = path;
    this.method = method;
    this.repoDefLocation = null;
  }

  /**
   * 
   * @param path The path of the starting point for config exploration. Can be a default.properties
   *        or hosts.properties path
   * @throws Exception
   */
  public ConfigClient(String repoDefPath, String path, Method method) {
    this.startLocation = path;
    this.method = method;
    this.repoDefLocation = repoDefPath;
  }

  public Environment getEnvironment() {
    return environment;
  }

  public String getFileNamePattern() {
    return fileNamePattern;
  }

  public String getHostsNamePattern() {
    return hostsNamePattern;
  }

  public MergeStrategy getMergeStrategy() {
    return mergeStrategy;
  }

  public Properties getProperties() {
    Properties props = new Properties();
    props.putAll(loadedProperties.get());
    return props;
  }

  public <T> T getProperty(String key, Class<T> clazz) {

    String value = loadedProperties.get().getProperty(key);

    if (StringUtils.hasText(value)) {
      value = strings.fill(value);
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

    String environmentName = environment.detectEnvironment();
    String hostName = environment.detectHostName();
    Optional<String> startPath = Optional.empty();

    if (StringUtils.hasText(this.repoDefLocation)) {
      this.sourceResolver = new ConfigSourceResolver(this.repoDefLocation, environment);
    } else {
      this.sourceResolver = new ConfigSourceResolver(environment);
    }

    if (this.method.equals(Method.HOST_FILE)) {

      logger.info("Loading hosts file at " + startLocation);

      UriUtil uri = new UriUtil(startLocation);
      
      Optional<ConfigSource> configSource = this.sourceResolver.resolveByUri(startLocation);

      if (configSource.isPresent()) {
        Properties hosts = configSource.get().fetchConfig(startLocation);

        if (encryptor != null)
          hosts = new EncryptableProperties(hosts, encryptor);

        if (hosts.isEmpty()) {
          throw new IllegalArgumentException(
              "Hosts file empty or wrong hosts file path provided: " + startLocation);
        }

        startPath = lookupStrategy.lookupConfigPath(hosts, environment.getProperties());
      }

    } else if (this.method.equals(Method.DIRECT_PATH)) {

      startPath = Optional.of(this.startLocation);

    }

    if (startPath.isPresent()) {

      mergeStrategy.clear();

      Optional<ConfigSource> configSource = this.sourceResolver.resolveByUri(startPath.get());

      logger.debug("Searching for properties beginning at: " + startPath);

      if (configSource.isPresent()) {
        Properties ps = configSource.get().traverseConfigs(startPath.get());
        mergeStrategy.addConfig(ps);

        if (encryptor != null)
          ps = new EncryptableProperties(ps, encryptor);

        if (ps.isEmpty()) {
          logger.warn("Counldn't find any properties for host " + hostName + " or environment "
              + environmentName);
        }

        loadedProperties.set(ps);
      }

    } else {
      logger.warn("Counldn't find any properties for host " + hostName + " or environment "
          + environmentName);
    }

    mergeStrategy.addConfig(environment.getProperties());
    strings = new StringUtils(mergeStrategy.merge());
    setRefreshRate(timerTTL);

  }

  public boolean isTraverseClasspath() {
    return traverseClasspath;
  }

  public void setFileNamePattern(String fileNamePattern) {
    this.fileNamePattern = fileNamePattern;
  }

  public void setHostsNamePattern(String hostsNamePattern) {
    this.hostsNamePattern = hostsNamePattern;
  }

  public void setMergeStrategy(MergeStrategy mergeStrategy) {
    this.mergeStrategy = mergeStrategy;
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

  public void setTraverseClasspath(boolean traverseClasspath) {
    this.traverseClasspath = traverseClasspath;
  }
}
