package com.appcrossings.config.discovery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.appcrossings.config.MergeStrategy;

public class DefaultMergeStrategy implements MergeStrategy {

  private final List<Map<String, Object>> all = new ArrayList<>();

  @Override
  public void addConfig(Map<String, Object> props) {
    all.add(props);
  }

  @Override
  public void clear() {
    all.clear();
  }

  @Override
  public Map<String, Object> merge() {

    List<Map<String, Object>> copy = new ArrayList<>(all);
    Collections.reverse(copy); // sort from root to highest

    Map<String, Object> ps = new HashMap<>();

    for (Map p : copy) {
      ps.putAll(p);
    }
    clear();
    
    return ps;
  }

}
