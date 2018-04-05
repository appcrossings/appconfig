package com.appcrossings.config;

import java.util.Properties;

public interface MergeStrategy {

  public void addConfig(Properties props);

  public void clear();

  public Properties merge();

}
