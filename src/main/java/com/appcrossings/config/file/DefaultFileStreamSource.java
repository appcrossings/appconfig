package com.appcrossings.config.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.appcrossings.config.processor.ProcessorSelector;
import com.appcrossings.config.source.AdHocStreamSource;
import com.appcrossings.config.source.PropertyPacket;
import com.appcrossings.config.source.RepoDef;
import com.appcrossings.config.source.StreamPacket;
import com.appcrossings.config.source.StreamSource;
import com.appcrossings.config.util.URIBuilder;
import com.appcrossings.config.util.UriUtil;

public class DefaultFileStreamSource implements StreamSource, AdHocStreamSource {

  private final static Logger log = LoggerFactory.getLogger(DefaultFileStreamSource.class);
  private final FileRepoDef def;
  private final URIBuilder builder;

  public DefaultFileStreamSource(FileRepoDef def) {
    this.def = def;
    URI uri = def.toURI();
    builder = URIBuilder.create(uri);
  }

  @Override
  public Optional<PropertyPacket> stream(final URI uri) {

    StreamPacket p = null;

    if (!validateURI(uri)) {
      throw new IllegalArgumentException("Uri " + uri + " is not valid");
    }

    if (uri.getScheme().equalsIgnoreCase("file")) {

      try (InputStream is = new FileInputStream(new File(uri))) {

        if (is != null) {
          p = new StreamPacket(uri, is);
          p.putAll(ProcessorSelector.process(uri.toString(), p.bytes()));
        }

      } catch (FileNotFoundException e) {
        log.debug(e.getMessage());
        // nothing else
      } catch (IOException e) {

      }

    } else if (uri.getScheme().equalsIgnoreCase("classpath")) {

      String trimmed = uri.getSchemeSpecificPart();

      if (!trimmed.startsWith(File.separator))
        trimmed = File.separator + trimmed;

      try (InputStream is = this.getClass().getResourceAsStream(trimmed)) {

        if (is != null) {
          p = new StreamPacket(uri, is);
          Map<String, Object> vals = ProcessorSelector.process(uri.toString(), p.bytes());
          p.putAll(vals);
        }

      } catch (IOException e) {
        log.debug(e.getMessage());
        // nothing else
      }

    } else {
      throw new IllegalArgumentException("Incompatible stream uri " + uri);
    }

    if (p != null) {
      log.info("Found " + uri.toString());
    }

    return Optional.ofNullable(p);

  }

  protected boolean validateURI(URI uri) {

    return UriUtil.validate(uri).isScheme("classpath", "file").hasPath().hasFile().valid();

  }

  @Override
  public String getSourceName() {
    return StreamSource.FILE_SYSTEM;
  }

  @Override
  public RepoDef getSourceConfig() {
    return this.def;
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
  public void init() {
    // nothing
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
