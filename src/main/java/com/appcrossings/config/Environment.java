package com.appcrossings.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.processor.PropertiesProcessor;
import com.appcrossings.config.util.StringUtils;

public class Environment {

  public final static String APP_NAME = "app.name";
  public final static String APP_VERSION = "app.version";
  public final static String ENV_NAME = "app.env";
  public final static String HOST_NAME = "hostname";
  public final static String IP_ADDRESS = "ip";
  private final static Logger log = LoggerFactory.getLogger(Environment.class);
  public final static String OS_NAME = "os.name";
  public final static String OS_VERSION = "os.version";
  public final static String SUBNET_ADDRESS = "subnet";
  protected final Map<String, String> envProps = new HashMap<>();

  public Environment() {}

  /**
   * Attempt to detect environment of the application.
   * 
   * @return environment string
   */
  public String detectEnvironment() {

    String env = this.envProps.get(ENV_NAME); // initialize to configured environment

    if (!StringUtils.hasText(env)) {

      env = StringUtils.hasText(env) ? env : System.getProperty("env");
      env = StringUtils.hasText(env) ? env : System.getProperty("ENV");
      env = StringUtils.hasText(env) ? env : System.getProperty("environment");
      env = StringUtils.hasText(env) ? env : System.getProperty("ENVIRONMENT");

      if (!StringUtils.hasText(env)) {
        log.info(
            "No environment variable detected under 'spring.profiles' or system properties 'env', 'ENV', 'environment', 'ENVIRONMENT'");
      } else {
        log.info("Detected environment: " + env);
      }

      this.envProps.put(ENV_NAME, env);
    }

    return this.envProps.get(ENV_NAME);
  }

  /**
   * Reads the underlying host's name. This is used to match this host against its configuration.
   * You can programmatically override the hostname value by setting System.setProperty("hostname",
   * "value").
   * 
   * @return hostname
   */
  public String detectHostName() {

    String hostName = this.envProps.get(HOST_NAME); // initialize to configured host name

    if (!StringUtils.hasText(hostName)) {

      try {

        hostName =
            StringUtils.hasText(System.getProperty("hostname")) ? System.getProperty("hostname")
                : InetAddress.getLocalHost().getHostName();

      } catch (UnknownHostException ex) {
        // Nothing
      }

      hostName = StringUtils.hasText(hostName) ? hostName : System.getProperty("HOSTNAME");

      if (!StringUtils.hasText(hostName)) {
        log.error(
            "Unable to resolve host in order to resolve hosts file config. Searched system property 'hostname', 'HOSTNAME' and localhost.hostName");
      } else {

        if (hostName.contains(".")) {
          hostName = hostName.substring(0, hostName.indexOf("."));
        }

        this.envProps.put(HOST_NAME, hostName);
      }
    } else {
      log.info("Resolved hostname to: " + hostName);
    }

    return hostName;
  }

  /**
   * Reads the underlying host's name. This is used to match this host against its configuration.
   * You can programmatically override the hostname value by setting System.setProperty("hostname",
   * "value").
   * 
   * @return ip
   */
  public String detectIP() {

    String ipaddress = this.envProps.get(IP_ADDRESS); // initialize to configured host name

    if (!StringUtils.hasText(ipaddress)) {

      try {

        ipaddress = StringUtils.hasText(System.getProperty("ip")) ? System.getProperty("ip")
            : InetAddress.getLocalHost().getHostAddress();

        ipaddress = StringUtils.hasText(ipaddress) ? ipaddress : System.getProperty("IP");

        if (!StringUtils.hasText(ipaddress)) {
          throw new UnknownHostException(
              "Unable to resolve ip in order to resolve hosts file config. Searched system property 'ip', 'IP'");
        }

        log.info("Resolved ip to: " + ipaddress);

        this.envProps.put(IP_ADDRESS, ipaddress);

      } catch (UnknownHostException ex) {
        log.error("Can't resolve ip", ex);
        throw new RuntimeException(ex);
      }
    }

    return this.envProps.get(IP_ADDRESS);
  }

  public Map<String, String> getEnvironment() {
    return Collections.unmodifiableMap(this.envProps);
  }

  public Properties getProperties() {

    return PropertiesProcessor.asProperties((Map) this.envProps);

  }

  public void setApplicationName(String appName) {
    this.envProps.put(APP_NAME, appName);
  }

  public void setEnvironmentName(String environmentName) {
    this.envProps.put(ENV_NAME, environmentName);
  }

  public void setHostName(String hostName) {
    this.envProps.put(HOST_NAME, hostName);
  }

  public void setProperty(String key, String value) {
    this.envProps.put(key, value);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this.envProps);
  }

}
