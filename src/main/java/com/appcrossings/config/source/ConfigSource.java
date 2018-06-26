package com.appcrossings.config.source;

import java.util.Properties;

public interface ConfigSource {

  /**
   * Traverses a config tree per the underlying implementation's mechanism.
   * 
   * @param path Path extending the pre-configured uri in the repo definition
   * @return
   */
  public Properties get(String path, String...names);

  /**
   * Retrieves a single node of the config given by the properties path. No traversal.
   * 
   * @param path Path extending the pre-configured uri in the repo definition
   * @return
   */
  public Properties getRaw(String path);
  
  public StreamSource getStreamSource();

  public boolean isCompatible(StreamSource source);
  
  public String getName();


}
