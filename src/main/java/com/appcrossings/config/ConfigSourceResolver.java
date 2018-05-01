package com.appcrossings.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import com.appcrossings.config.source.ConfigSource;
import com.appcrossings.config.source.EnvironmentAware;
import com.appcrossings.config.source.RepoDef;
import com.appcrossings.config.source.StreamingConfigSource;
import com.appcrossings.config.util.Environment;
import com.appcrossings.config.util.StringUtils;
import com.appcrossings.config.util.UriUtil;

public class ConfigSourceResolver {

  private final static Logger logger = LoggerFactory.getLogger(ConfigSourceResolver.class);

  final Map<String, Object> defaults = new HashMap<>();
  final ServiceLoader<ConfigSource> loader;
  final Map<String, ConfigSource> reposByName = new HashMap<>();
  final Map<String, ConfigSource> reposBySource = new HashMap<>();
  final Environment environment;

  public ConfigSourceResolver(Environment environment) {

    defaults.put("fileName", Config.DEFAULT_PROPERTIES_FILE_NAME);
    defaults.put("hostsName", Config.DEFAULT_HOSTS_FILE_NAME);
    defaults.put("root", "/");

    this.environment = environment;

    loader = ServiceLoader.load(ConfigSource.class);

    for (ConfigSource s : loader) {
      reposBySource.put(s.getSourceName(), s);
    }
  }

  public ConfigSourceResolver(String repoDefPath, Environment environment) {
    this(environment);

    assert StringUtils.hasText(repoDefPath) : "no repo configuration file provided.";

    Optional<ConfigSource> source = resolveByUri(repoDefPath);
    RepoDef def;

    if (source.isPresent() && source.get() instanceof StreamingConfigSource) {

      try (InputStream stream = ((StreamingConfigSource) source.get()).stream(repoDefPath)) {

        Yaml yaml = new Yaml();
        LinkedHashMap<String, Object> y = (LinkedHashMap) yaml.load(stream);

        if (y.containsKey("defaults")) {
          defaults.putAll((Map) y.get("defaults"));
        }

        if (y.containsKey("service")) {

          LinkedHashMap<String, Object> service = (LinkedHashMap) y.get("service");
          if (service.containsKey("repos")) {

            LinkedHashMap<String, Object> repos = (LinkedHashMap) service.get("repos");
            for (Entry<String, Object> entry : repos.entrySet()) {

              if (entry.getValue() instanceof LinkedHashMap) {
                LinkedHashMap<String, Object> repo = (LinkedHashMap) entry.getValue();

                String repoName = entry.getKey();

                Optional<ConfigSource> cs = resolveBySourceName(((Map) entry.getValue()).keySet());

                if (cs.isPresent()) {

                  buildConfigSource(cs.get(), (Map) entry.getValue(), repoName, defaults);

                } else {
                  continue;
                }
              }
            }
          }
        }

      } catch (IOException e) {
        throw new IllegalArgumentException(
            "Unable to fetch repo definitions from location " + repoDefPath, e);
      }

    } else {
      throw new IllegalArgumentException(
          "Unable to fetch repo definitions from location " + repoDefPath);
    }

  }

  protected ConfigSource buildConfigSource(ConfigSource template, Map<String, Object> values,
      final String name, Map<String, Object> defaults) {

    try {

      if (!reposByName.containsKey(name.toLowerCase())) {
        ConfigSource s = template.newInstance(name.toLowerCase(), values, new HashMap<>(defaults));

        if (s != null && EnvironmentAware.class.isAssignableFrom(s.getClass())) {
          ((EnvironmentAware) s).setEnvironment(environment);
        }

        reposByName.put(name.toLowerCase(), s);
      }

    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      // Should never happen
    }

    return reposByName.get(name.toLowerCase());
  }

  public Optional<ConfigSource> resolveByRepoName(String repoName) {
    return Optional.ofNullable(reposByName.get(repoName.toLowerCase()));
  }

  public Optional<ConfigSource> resolveBySourceName(final Set<String> sourceNames) {

    ConfigSource cs = null;

    Optional<ConfigSource> ocs = StreamSupport.stream(loader.spliterator(), false).filter(s -> {
      return sourceNames.contains(s.getSourceName());
    }).findFirst();

    if (ocs.isPresent()) {
      cs = buildConfigSource(ocs.get(), new HashMap<>(), ocs.get().getSourceName(), defaults);
    } else {
      logger.warn("No config source found for " + sourceNames + ". Ignoring configuration.");
    }

    return Optional.ofNullable(cs);

  }

  public Optional<ConfigSource> resolveByUri(final String uri) {

    ConfigSource cs = null;
    UriUtil i = new UriUtil(uri);
    String repoName = i.getScheme();
    String firstPath = i.firstPathSegment();

    if (reposByName.containsKey(firstPath.toLowerCase())) {

      cs = reposByName.get(firstPath.toLowerCase());
      repoName = firstPath;
      logger.info("Resolving " + uri + " to repo definition " + repoName);

    } else {

      Optional<ConfigSource> ocs = StreamSupport.stream(loader.spliterator(), false).filter(s -> {
        return s.isCompatible(uri);
      }).findFirst();

      if (ocs.isPresent()) {
        cs = buildConfigSource(ocs.get(), new HashMap<>(), repoName, defaults);
      }
    }

    return Optional.ofNullable(cs);

  }

}
