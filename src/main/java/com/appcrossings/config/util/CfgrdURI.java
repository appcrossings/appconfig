package com.appcrossings.config.util;

import java.net.URI;

public class CfgrdURI {

  private final URI uri;
  private String fileName;

  public CfgrdURI(URI uri) {

    if (!isCfgrdURI(uri)) {
      throw new IllegalArgumentException("cfgrd uri must start with 'cfgrd://'");
    }

    this.uri = uri;

    fileName = UriUtil.getFileName(this.uri).orElse(null);
  }

  public static boolean isCfgrdURI(URI uri) {
    return uri.getScheme().equalsIgnoreCase("cfgrd");
  }

  public String getRepoName() {
    return uri.getHost();
  }

  public String getPath() {
    return this.uri.getPath();
  }

  public boolean hasFile() {
    return StringUtils.hasText(fileName);
  }

  public String getFileName() {
    return fileName;
  }

  public String getUserName() {

    return UriUtil.getUsername(uri);
  }

  public String getPassword() {

    return UriUtil.getPassword(uri);
  }

  @Override
  public String toString() {
    return uri.toString();
  }

  public URI toURI() {
    return uri;
  }

}
