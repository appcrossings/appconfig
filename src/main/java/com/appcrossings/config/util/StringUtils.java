package com.appcrossings.config.util;

import java.util.HashMap;
import java.util.Map;
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

  private static final ConvertUtilsBean bean = new ConvertUtilsBean();

  protected boolean ignoreUnresolvablePlaceholders = false;

  protected final StrSubstitutor sub;
  protected final Map<String, Object> mapped;

  public StringUtils(Map<String, Object> vals) {

    sub = new StrSubstitutor(vals);
    sub.setVariablePrefix(DEFAULT_PLACEHOLDER_PREFIX);
    sub.setVariableSuffix(DEFAULT_PLACEHOLDER_SUFFIX);
    this.mapped = fillAll(vals);
  }

  public static <T> T cast(String property, Class<T> clazz) {
    if (clazz.equals(String.class))
      return (T) property;
    else if (property != null)
      return (T) bean.convert(property, clazz);
    else
      return null;
  }

  public String fill(final Object value) {

    String v = String.valueOf(value);

    if (v.contains(DEFAULT_PLACEHOLDER_PREFIX))
      v = sub.replace(v);

    return v;
  }

  public Map<String, Object> filled() {
    return this.mapped;
  }

  protected Map<String, Object> fillAll(Map<String, Object> vals) {

    Map<String, Object> filled = new HashMap<>();

    vals.entrySet().stream().forEach(e -> {
      filled.put(e.getKey(), fill(e.getValue()));
    });

    return filled;

  }

}
