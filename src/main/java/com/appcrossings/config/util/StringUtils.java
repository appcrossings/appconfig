package com.appcrossings.config.util;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.lang3.text.StrSubstitutor;

public class StringUtils {

  /** Default placeholder prefix: {@value} */
  public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

  /** Default placeholder suffix: {@value} */
  public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

  public static boolean hasText(String string) {
    return (string != null && !string.trim().equals(""));
  }

  private final ConvertUtilsBean bean = new ConvertUtilsBean();

  protected boolean ignoreUnresolvablePlaceholders = false;

  protected AtomicReference<StrSubstitutor> sub = new AtomicReference<>(new StrSubstitutor());

  public StringUtils(Map<String, Object> vals) {

    sub.set(new StrSubstitutor(vals));
    sub.get().setVariablePrefix(DEFAULT_PLACEHOLDER_PREFIX);
    sub.get().setVariableSuffix(DEFAULT_PLACEHOLDER_SUFFIX);
  }

  public <T> T cast(String property, Class<T> clazz) {
    if (clazz.equals(String.class))
      return (T) property;
    else if (property != null)
      return (T) bean.convert(property, clazz);
    else
      return null;
  }

  public String fill(String value) {

    if (value.contains(DEFAULT_PLACEHOLDER_PREFIX))
      value = sub.get().replace(value);


    return value;
  }

}
