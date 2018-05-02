package com.appcrossings.config;

import java.util.Properties;
import com.appcrossings.config.discovery.DefaultMergeStrategy;

/**
 * Interface for configuration implementations.
 * 
 * @author Krzysztof Karski
 *
 */
public interface Config {

  public final static String CONFIG_LOOKUP_STRATEGY = "config.strategy.lookup";
  public final static String CONFIG_MERGE_STRATEGY = "config.strategy.merge";
  public final static String DEFAULT_HOSTS_FILE_NAME = "hosts.properties";

  public final static String DEFAULT_MERGE_STRATEGY_CLASS = DefaultMergeStrategy.class.getName();
  public final static String DEFAULT_PROPERTIES_FILE_NAME = "default.properties";
  public final static String HOST_FILE_NAME = "file.hosts.name";
  public final static String HOST_NAME = "HOSTNAME";
  public final static String METHOD = "lookup.method";
  public final static String PATH = "file.props.path";
  public final static String PROPERTIES_FILE_NAME = "file.props.name";
  public final static String REFRESH_RATE = "timer.ttl";
  public final static String REPO_DEF_PATH = "repo.def.path";
  public final static boolean SEARCH_CLASSPATH = true;

  public final static String TRAVERSE_CLASSPATH = "traverse.classpath";


  /**
   * 
   * @return All loaded properties. Empty properties if none loaded.
   */
  public Properties getProperties();

  /**
   * 
   * @param key - key name of the property
   * @param clazz - the type of the property value for strong typing.
   * @return the property value case to the type provided. Returns null if no property is found
   */
  public <T> T getProperty(String key, Class<T> clazz);

  /**
   * 
   * @param key - key name of the property
   * @param clazz - the type of the property value for strong typing
   * @param defaultVal - a default value to return if a property value isn't found
   * @return the property value case to the type provided. Returns null if no property is found
   */
  public <T> T getProperty(String key, Class<T> clazz, T defaultVal);

}
