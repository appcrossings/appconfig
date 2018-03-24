package com.appcrossings.config.source;

import java.io.Serializable;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import com.appcrossings.config.Config;
import com.appcrossings.config.MergeStrategy;

@SuppressWarnings("serial")
public class BaseRepoDef implements Serializable, RepoDef {

  String mergeStrategy = Config.DEFAULT_MERGE_STRATEGY_CLASS;
  MergeStrategy strategy;

  protected String name;
  protected String context;

  protected BaseRepoDef() {

    try {
      strategy = (MergeStrategy) Class.forName(mergeStrategy).newInstance();
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  public BaseRepoDef(String name, Map<String, Object> values) {
    super();
    this.name = name;

    try {
      if (values != null && !values.isEmpty())
        BeanUtils.copyProperties(this, values);

      if (strategy != null && !mergeStrategy.equalsIgnoreCase(strategy.getClass().getName()))
        strategy = (MergeStrategy) Class.forName(mergeStrategy).newInstance();

    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  @Override
  public String getMergeStrategyClass() {
    return mergeStrategy;
  }

  @Override
  public String getName() {
    return name;
  }

  public String getContext() {
    return context;
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

  public MergeStrategy getStrategy() {
    return strategy;
  }

  public void setStrategy(MergeStrategy strategy) {
    this.strategy = strategy;
  }



}
