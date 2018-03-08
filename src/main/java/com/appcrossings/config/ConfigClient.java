package com.appcrossings.config;

import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.EnvironmentStringPBEConfig;
import org.jasypt.properties.EncryptableProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Krzysztof Karski
 *
 */
public class ConfigClient implements Config {

  protected ConfigSourceResolver configSource = new ConfigSourceResolver();

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

  protected final Environment envUtil = new Environment();

  protected Integer timerTTL = 0;

  protected StringUtils strings;

  protected final String startLocation;

  private final AtomicReference<Properties> loadedProperties = new AtomicReference<>();

  private Method method = Method.HOST_FILE;

  protected String propertiesFileName = DEFAULT_PROPERTIES_FILE_NAME;

  protected String hostsFileName = DEFAULT_HOSTS_FILE_NAME;

  protected boolean searchClasspath = SEARCH_CLASSPATH;

  private AtomicReference<Timer> timer = new AtomicReference<Timer>();

  public enum Method {
    HOST_FILE, DIRECT_PATH;
  }

  /**
   * 
   * @param path The path of the hosts.properties file
   * @throws Exception
   */
  public ConfigClient(String path) throws Exception {
    this.strings = new StringUtils(envUtil.getProperties());
    this.startLocation = path;
  }

  public ConfigClient(Properties props) throws Exception {

    assert !props
        .containsKey(Config.PATH) : "File path to hosts.properties or start location is mandatory";
    this.startLocation = props.getProperty(Config.PATH);

    if (props.containsKey(Config.HOST_NAME))
      getEnvironment().setHostName(props.getProperty(Config.HOST_NAME));

    if (props.containsKey(Config.HOST_FILE_NAME))
      this.hostsFileName = props.getProperty(Config.HOST_FILE_NAME);

    if (props.containsKey(Config.PROPERTIES_FILE_NAME))
      this.propertiesFileName = props.getProperty(Config.PROPERTIES_FILE_NAME);

    if (props.containsKey(Config.SEARCH_CLASSPATH))
      this.searchClasspath = Boolean.parseBoolean(props.getProperty(Config.TRAVERSE_CLASSPATH));

    if (props.containsKey(Config.REFRESH_RATE))
      this.timerTTL = Integer.parseInt((String) props.getProperty(Config.REFRESH_RATE));

    if (props.containsKey(Config.METHOD))
      this.method = Method.valueOf(props.getProperty(Config.METHOD));


    this.strings = new StringUtils(envUtil.getProperties());

  }

  /**
   * 
   * @param path The path of the starting point for config exploration. Can be a default.properties
   *        or hosts.properties path
   * @throws Exception
   */
  public ConfigClient(String path, Method method) throws Exception {
    this(path);
    this.method = method;
  }

  /**
   * 
   * @param path - The path of the starting point for config exploration. Can be a
   *        default.properties or hosts.properties path
   * @param refresh - The period in seconds at which the config properties should be refreshed. 0
   *        indicates no automated timer
   * @throws Exception
   */
  public ConfigClient(String path, int refresh, Method method) throws Exception {
    this(path, method);
    setRefreshRate(refresh);
  }

  public <T> T getProperty(String key, Class<T> clazz) {

    String value = loadedProperties.get().getProperty(key);
    value = strings.fill(value);
    return strings.cast(value, clazz);
  }

  public <T> T getProperty(String key, Class<T> clazz, T value) {

    T val = getProperty(key, clazz);

    if (val != null && val != "")
      return val;

    return value;

  }

  public void init() {

    String environmentName = envUtil.detectEnvironment();
    String hostName = envUtil.detectHostName();
    String startPath = null;

    if (this.method.equals(Method.HOST_FILE)) {

      logger.info("Loading hosts file at " + startLocation);

      ConfigSource configSource = this.configSource.resolveSource(startLocation);

      Properties hosts = configSource.resolveConfigPath(startLocation, hostsFileName);

      if (encryptor != null)
        hosts = new EncryptableProperties(hosts, encryptor);

      startPath = hosts.getProperty(hostName);

      // Attempt environment as a backup
      if (!strings.hasText(startPath) && strings.hasText(environmentName)) {

        startPath = hosts.getProperty(environmentName);

      }

      if (!strings.hasText(startPath)) {

        startPath = hosts.getProperty("*");// catch all

      }

      if (startPath == null || startPath.trim().equals("")) {
        throw new IllegalArgumentException(
            "Hosts file failed to resolve a config start path evnironment settings "
                + envUtil.toString());
      }

    } else if (this.method.equals(Method.DIRECT_PATH)) {
      startPath = this.startLocation;
    }

    if (StringUtils.hasText(startPath)) {

      ConfigSource configSource = this.configSource.resolveSource(startPath);

      logger.debug("Searching for properties beginning at: " + startPath);

      Properties ps = configSource.traverseConfigs(startPath, propertiesFileName);

      if (encryptor != null)
        ps = new EncryptableProperties(ps, encryptor);

      if (ps.isEmpty()) {
        logger.warn("Counldn't find any properties for host " + hostName + " or environment "
            + environmentName);
      }

      loadedProperties.set(ps);

    } else {
      logger.warn("Counldn't find any properties for host " + hostName + " or environment "
          + environmentName);
    }

    final Properties merged = new Properties(envUtil.getProperties());
    merged.putAll(loadedProperties.get());
    strings = new StringUtils(merged);

    setRefreshRate(timerTTL);

  }

  public Environment getEnvironment() {
    return envUtil;
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

  /**
   * Override default text encryptor (StandardPBEStringEncryptor). Enables overriding both password
   * and algorithm.
   * 
   * @param encryptor
   */
  public void setTextEncryptor(StandardPBEStringEncryptor config) {
    this.encryptor = config;
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
}
