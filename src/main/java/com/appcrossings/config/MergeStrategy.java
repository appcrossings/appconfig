package com.appcrossings.config;

import java.util.Properties;

public interface MergeStrategy {
  
  public void addConfig(Properties props);
  public Properties merge();
  public void clear();

}
