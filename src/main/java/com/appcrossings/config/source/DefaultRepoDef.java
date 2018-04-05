package com.appcrossings.config.source;

import java.io.Serializable;
import com.appcrossings.config.Config;
import com.appcrossings.config.MergeStrategy;

@SuppressWarnings("serial")
public class DefaultRepoDef implements Serializable, RepoDef {

  protected String context;
  protected String mergeStrategy = Config.DEFAULT_MERGE_STRATEGY_CLASS;
  protected String name;
  protected MergeStrategy strategy;

  protected DefaultRepoDef() {

    super();

    try {
      strategy = (MergeStrategy) Class.forName(mergeStrategy).newInstance();
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  public DefaultRepoDef(String name) {

    super();

    try {
      strategy = (MergeStrategy) Class.forName(mergeStrategy).newInstance();
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }

    this.name = name;
  }

  public String getContext() {
    return context;
  }

  @Override
  public String getMergeStrategyClass() {
    return mergeStrategy;
  }

  @Override
  public String getName() {
    return name;
  }

  public MergeStrategy getStrategy() {
    return strategy;
  }

  public void setContext(String context) {
    this.context = context;
  }

  public void setMergeStrategyClass(String mergeStrategy) {
    this.mergeStrategy = mergeStrategy;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setStrategy(MergeStrategy strategy) {
    this.strategy = strategy;
  }



}
