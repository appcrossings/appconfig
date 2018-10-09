package com.appcrossings.config;

import java.util.Map;

public interface MergeStrategy {

  public void addConfig(Map<String, Object> props);

  public void clear();

  public Map<String, Object> merge();

}
