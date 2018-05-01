package com.appcrossings.config.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.source.ConfigSource;
import com.appcrossings.config.source.EnvironmentAware;
import com.appcrossings.config.source.RepoDef;
import com.appcrossings.config.source.StreamingConfigSource;
import com.appcrossings.config.util.Environment;
import com.appcrossings.config.util.JsonProcessor;
import com.appcrossings.config.util.PropertiesProcessor;
import com.appcrossings.config.util.StringUtils;
import com.appcrossings.config.util.UriUtil;
import com.appcrossings.config.util.YamlProcessor;
import com.jsoniter.output.JsonStream;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.internal.http.StatusLine;

public class DefaultHttpSource implements ConfigSource, StreamingConfigSource, EnvironmentAware {

  private final static Logger log = LoggerFactory.getLogger(DefaultHttpSource.class);
  protected HttpRepoDef repoConfig;
  protected OkHttpClient client;
  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  protected Environment environment;

  public DefaultHttpSource() {
    client = new OkHttpClient();
  }

  @Override
  public Properties fetchConfig(String propertiesPath) {

    String fullPath = resolveFullFilePath(propertiesPath, repoConfig.getFileName());
    Properties p = new Properties();

    if (isURL(fullPath)) {
      try (InputStream stream = stream(fullPath)) {

        if (stream == null)
          return p;

        log.info("Attempting " + fullPath);

        if (JsonProcessor.isJsonFile(fullPath)) {

          p = JsonProcessor.asProperties(stream);

        } else if (YamlProcessor.isYamlFile(fullPath)) {

          p = YamlProcessor.asProperties(stream);

        } else if (PropertiesProcessor.isPropertiesFile(fullPath)) {

          p = PropertiesProcessor.asProperties(stream);

        } else {

          log.warn("Unable to process file " + fullPath + ". No compatible file processor found.");

        }

      } catch (Exception e) {
        log.warn("File " + fullPath + " not found.", e);
      }
    }

    return p;


  }

  @Override
  public RepoDef getSourceConfiguration() {
    return repoConfig;
  }

  @Override
  public String getSourceName() {
    return ConfigSource.HTTPS;
  }

  @Override
  public boolean isCompatible(String path) {
    final String prefix = path.trim().substring(0, path.indexOf("/"));
    return (prefix.toLowerCase().startsWith("http"));
  }

  protected boolean isURL(String path) {
    return path.trim().startsWith("http");
  }

  @Override
  public ConfigSource newInstance(String name, Map<String, Object> values,
      Map<String, Object> defaults) {

    DefaultHttpSource s = new DefaultHttpSource();

    final Map<String, Object> merged = new HashMap<>(defaults);

    if (!values.isEmpty() && values.containsKey(s.getSourceName()))
      merged.putAll((Map) values.get(s.getSourceName()));

    HttpRepoDef def = new HttpRepoDef(name, merged);
    s.repoConfig = (HttpRepoDef) def;
    s.setEnvironment(environment);
    s.init();
    return s;
  }

  public String resolveFullFilePath(String propertiesPath, String fileName) {

    String path = "";
    URI uri = URI.create(propertiesPath);

    if (repoConfig != null
        && (!StringUtils.hasText(uri.getScheme()) || uri.getScheme().startsWith("repo"))) {

      if (StringUtils.hasText(repoConfig.uri)) {

        path = repoConfig.uri;

        if (StringUtils.hasText(repoConfig.root))
          path = path + repoConfig.root;
      }

      if (StringUtils.hasText(repoConfig.getContext())
          && propertiesPath.contains(repoConfig.getContext()))
        path = path + propertiesPath.replaceFirst(repoConfig.getContext(), "");
      else
        path = path + propertiesPath;

    } else {
      path = propertiesPath;
    }

    UriUtil pathUtil = new UriUtil(path);
    pathUtil.appendFileName(fileName);

    return pathUtil.toString();
  }

  protected void init() {

    OkHttpClient.Builder builder = new OkHttpClient.Builder();

    if (StringUtils.hasText(repoConfig.passWord)) {
      final AtomicInteger result = new AtomicInteger(0);
      builder.authenticator(new Authenticator() {
        @Override
        public Request authenticate(Route route, Response response) throws IOException {

          while ((response = response.priorResponse()) != null) {
            if (result.incrementAndGet() >= 3) {
              return null;
            }
          }

          String credential = Credentials.basic(repoConfig.userName, repoConfig.passWord);
          return response.request().newBuilder().header("Authorization", credential).build();
        }
      });
    }
    builder.connectTimeout(10, TimeUnit.SECONDS);
    builder.writeTimeout(10, TimeUnit.SECONDS);
    builder.readTimeout(30, TimeUnit.SECONDS);

    client = builder.build();

  }

  @Override
  public InputStream stream(String propertiesPath) throws IOException {

    UriUtil uri = new UriUtil(propertiesPath);
    InputStream stream = null;
    if (uri.isConfigrdServer()) {

      Properties props = environment.getProperties();
      String json = JsonStream.serialize(props);

      Response call = null;
      try {
        RequestBody body = RequestBody.create(JSON, json);
        Builder request = new Request.Builder().url(propertiesPath).post(body);
        log.debug(request.toString());
        call = client.newCall(request.build()).execute();

        if (call.isSuccessful() && !call.isRedirect()) {

          stream = call.body().byteStream();

        } else if (call.isSuccessful() && call.isRedirect()) {

          switch (call.code()) {
            case StatusLine.HTTP_TEMP_REDIRECT:

            case StatusLine.HTTP_PERM_REDIRECT:
          }

        }

      } catch (Exception e) {
        log.error(e.getMessage(), e);

      } finally {
        call.close();
      }

    } else {

      try {
        stream = new URL(propertiesPath).openStream();
      } catch (IOException e) {
        // Nothing
      }
    }

    return stream;
  }

  @Override
  public Properties traverseConfigs(String propertiesPath) {
    try {

      if (StringUtils.hasText(propertiesPath)) {

        final UriUtil uri = new UriUtil(propertiesPath);

        do {

          Properties temp = fetchConfig(uri.toString());
          repoConfig.getStrategy().addConfig(temp);
          uri.stripPath();

        } while (uri.hasPath());
      }

      return repoConfig.getStrategy().merge();

    } catch (Exception e) {
      // should never happen
    }

    return new Properties();

  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }


}
