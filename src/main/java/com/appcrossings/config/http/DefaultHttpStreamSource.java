package com.appcrossings.config.http;

import java.io.IOException;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.processor.ProcessorSelector;
import com.appcrossings.config.source.AdHocStreamSource;
import com.appcrossings.config.source.PropertyPacket;
import com.appcrossings.config.source.StreamPacket;
import com.appcrossings.config.source.StreamSource;
import com.appcrossings.config.util.StringUtils;
import com.appcrossings.config.util.URIBuilder;
import com.appcrossings.config.util.UriUtil;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.Route;

public class DefaultHttpStreamSource implements StreamSource, AdHocStreamSource {

  protected OkHttpClient client;
  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
  private final static Logger log = LoggerFactory.getLogger(DefaultHttpStreamSource.class);
  private final HttpRepoDef def;
  private final URIBuilder builder;

  public DefaultHttpStreamSource(HttpRepoDef def) {
    client = new OkHttpClient();
    this.def = def;
    URI uri = def.toURI();
    builder = URIBuilder.create(uri);
  }

  public void init() {

    OkHttpClient.Builder builder = new OkHttpClient.Builder();

    if (StringUtils.hasText(def.getPassword())) {
      final AtomicInteger result = new AtomicInteger(0);
      builder.authenticator(new okhttp3.Authenticator() {


        @Override
        public Request authenticate(Route route, Response response) throws IOException {

          while ((response = response.priorResponse()) != null) {
            if (result.incrementAndGet() >= 3) {
              return null;
            }
          }

          String credential = Credentials.basic(def.getUsername(), def.getPassword());
          return response.request().newBuilder().header("Authorization", credential).build();
        }
      });
    }
    builder.connectTimeout(10, TimeUnit.SECONDS);
    builder.writeTimeout(10, TimeUnit.SECONDS);
    builder.readTimeout(30, TimeUnit.SECONDS);

    client = builder.build();

  }

  public Optional<PropertyPacket> stream(URI uri) {

    Optional<PropertyPacket> stream = Optional.empty();
    Builder request = new Request.Builder().url(uri.toString()).get();
    log.debug(request.toString());

    if (!validateURI(uri)) {
      throw new IllegalArgumentException("Uri " + uri + " is not valid");
    }

    try (Response call = client.newCall(request.build()).execute()) {

      if (call.isSuccessful() && !call.isRedirect() && call.body().contentLength() > 0) {

        StreamPacket packet = new StreamPacket(uri, call.body().byteStream());
        packet.setETag(call.header("ETag"));
        packet.putAll(ProcessorSelector.process(uri.toString(), packet.bytes()));
        stream = Optional.of(packet);

      } else if (call.isSuccessful() && call.isRedirect()) {

        log.error("Redirect handling not implemented. Server returned location "
            + call.header("location"));
      }

    } catch (UnknownHostException e) {

      log.error(e.getMessage(), e);
      throw new IllegalArgumentException(e.getMessage());

    } catch (Exception e) {
      log.debug(e.getMessage(), e);
      // nothing else
    }

    return stream;
  }

  @Override
  public String getSourceName() {
    return StreamSource.HTTPS;
  }

  @Override
  public HttpRepoDef getSourceConfig() {
    return def;
  }

  private boolean validateURI(URI uri) {
    return UriUtil.validate(uri).hasScheme().isScheme("http", "https").hasHost().hasPath().hasFile()
        .valid();
  }

  @Override
  public Optional<PropertyPacket> stream(String path) {
    Optional<PropertyPacket> is = Optional.empty();

    if (builder != null) {

      URI tempUri = prototypeURI(path);
      is = stream(tempUri);

    }

    return is;
  }

  @Override
  public URI prototypeURI(String path) {
    return builder.build(path);
  }

  @Override
  public void close() {
    // nothing
  }

}
