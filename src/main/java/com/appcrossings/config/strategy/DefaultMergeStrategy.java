package com.appcrossings.config.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import com.appcrossings.config.MergeStrategy;

public class DefaultMergeStrategy implements MergeStrategy {

  private final List<Properties> all = new ArrayList<>();

  @Override
  public void addConfig(Properties props) {
    all.add(props);
  }

  @Override
  public void clear() {
    all.clear();
  }

  @Override
  public Properties merge() {

    List<Properties> copy = new ArrayList<>(all);
    Collections.reverse(copy); // sort from root to highest

    Properties ps = new Properties();

    for (Properties p : copy) {
      ps.putAll(p);
    }
    clear();
    
    return ps;
  }

}
