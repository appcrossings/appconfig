package com.appcrossings.config.util;

import java.net.URI;
import java.net.URL;

public class UriUtil {

  private boolean isURL = false;
  private final URI uri;
  private String manipulated;
  private URL url;
  private URI host;
  private String fileName;
  private boolean isClasspath = false;

  public UriUtil(String uri) {
    this.uri = URI.create(uri);

    if (uri.toLowerCase().startsWith("classpath"))
      this.isClasspath = true;

    if (StringUtils.hasText(this.uri.getPath()))
      this.manipulated = this.uri.getPath();
    else
      this.manipulated = this.uri.getSchemeSpecificPart();

    if (hasFile()) {
      fileName =
          this.manipulated.substring(this.manipulated.lastIndexOf("/"), this.manipulated.length());
      this.manipulated = this.manipulated.replace(fileName, "");
    }

    try {
      this.url = this.uri.toURL();
      this.isURL = true;
    } catch (Exception e) {
      this.url = null;
    }
  }

  public URI getHost() {
    if (isURL && this.host == null) {
      this.host =
          URI.create(this.uri.getScheme() + "://" + this.uri.getHost() + this.uri.getPort());
    }

    return host;
  }

  public boolean hasPath() {

    if (this.manipulated.startsWith("/"))
      return this.manipulated.lastIndexOf("/") > -1;

    return this.manipulated.contains("/");
  }

  public boolean hasFile() {
    return this.manipulated.substring(this.manipulated.lastIndexOf("/")).contains(".");
  }

  public boolean isURL() {
    return this.isURL;
  }

  public URL toURL() {
    if (isURL)
      try {
        return URI.create(toString()).toURL();
      } catch (Exception e) {
        // should never happen
      }

    return null;
  }

  public void stripPath() {

    int i = this.manipulated.lastIndexOf("/");

    if (hasPath())
      this.manipulated = this.manipulated.substring(0, i);

  }

  @Override
  public String toString() {

    StringBuilder s = new StringBuilder();

    if (isURL) {
      s.append(this.uri.getScheme() + "://" + this.uri.getAuthority());

      if (this.uri.getPort() > 0)
        s.append(":" + this.uri.getPort());

    } else if (isClasspath) {
      s.append(this.uri.toString().substring(0, this.uri.toString().indexOf(":")) + ":");
    }

    s.append(this.manipulated);

    if (StringUtils.hasText(this.fileName))
      s.append(fileName);

    return s.toString();
  }

}
