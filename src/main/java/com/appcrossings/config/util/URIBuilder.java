package com.appcrossings.config.util;

import java.io.File;
import java.net.URI;
import java.util.StringJoiner;

public class URIBuilder {

  private String host;
  private String passWord;
  private String path;
  private int port;
  private String scheme;
  private URI uri;
  private String userName;
  private String fileName;
  private String[] fragments;

  public static URIBuilder create() {
    return new URIBuilder();
  }

  public static URIBuilder create(URI uri) {
    return new URIBuilder(uri);
  }

  public static URIBuilder create(String uri) {
    return new URIBuilder(URI.create(uri));
  }

  protected URIBuilder() {}

  protected URIBuilder(URI uri) {
    this.scheme = uri.getScheme();
    this.uri = uri;
    this.host = uri.getHost();
    this.userName = UriUtil.getUsername(uri);
    this.passWord = UriUtil.getPassword(uri);
    this.path = UriUtil.getPath(uri);
    this.port = uri.getPort();
    this.fileName = UriUtil.getFileName(uri).orElse(null);

    if (StringUtils.hasText(this.fileName))
      this.path = this.path.replace(fileName, "");
  }

  public URIBuilder setScheme(String scheme) {
    this.scheme = scheme;
    return this;
  }

  public boolean hasFileName() {
    return StringUtils.hasText(fileName);
  }

  public String getFileName() {
    return fileName;
  }

  public URIBuilder setUsernameIfMissing(String userName) {

    if (!StringUtils.hasText(this.userName))
      this.userName = userName;

    return this;
  }

  public URIBuilder setPasswordIfMissing(String passWord) {

    if (!StringUtils.hasText(this.passWord))
      this.passWord = passWord;

    return this;
  }

  public URIBuilder setFileNameIfMissing(String fileName) {

    if (!StringUtils.hasText(this.fileName))
      this.fileName = fileName;

    return this;
  }

  public URIBuilder setFileName(String fileName) {
    this.fileName = fileName;
    return this;
  }

  public URI build() {
    return build("");
  }

  public URI build(String extendedPath) {

    boolean hasFile = false;

    if (StringUtils.hasText(extendedPath)) {
      hasFile = UriUtil.hasFile(URI.create(extendedPath).toString());

      if (!extendedPath.endsWith(File.separator) && !hasFile)
        extendedPath += File.separator;

      if (extendedPath.startsWith(File.separator))
        extendedPath = extendedPath.replaceFirst(File.separator, "");
    }

    StringBuilder s = new StringBuilder();
    s.append(scheme + ":");

    String authority = buildAuthority();

    if (StringUtils.hasText(authority)) {
      s.append("//" + buildAuthority());

      if (!path.startsWith("/"))
        s.append("/");
    }

    if (StringUtils.hasText(path))
      s.append(path);

    s.append(extendedPath);

    if (StringUtils.hasText(fileName) && !hasFile) {

      if (StringUtils.hasText(path) && !path.endsWith("/"))
        s.append("/");

      s.append(fileName);
    }

    if (this.fragments != null && this.fragments.length > 0) {
      StringJoiner joiner = new StringJoiner(",", "#", "");

      for (String name : this.fragments) {
        joiner.add(name);
      }
      
      s.append(joiner.toString());
    }

    return URI.create(s.toString());

  }

  private String buildAuthority() {

    String authority = host;

    if (!StringUtils.hasText(authority))
      authority = "";

    if (StringUtils.hasText(userName) && !StringUtils.hasText(passWord)) {
      authority = userName + "@" + authority;
    } else if (StringUtils.hasText(userName) && StringUtils.hasText(passWord)) {
      authority = userName + ":" + passWord + "@" + authority;
    }

    if (port > 0)
      authority += ":" + port;

    return authority;
  }

  public URIBuilder setHost(String host) {
    this.host = host;
    return this;
  }

  public URIBuilder setPassword(String passWord) {
    this.passWord = passWord;
    return this;
  }

  public URIBuilder setFragment(String... names) {
    this.fragments = names;
    return this;
  }

  public URIBuilder setPath(String path) {

    String fileName = UriUtil.getFileName(path).orElse("");

    if (StringUtils.hasText(fileName)) {
      path = path.replace(fileName, "");
      setFileName(fileName);
    }

    this.path = path;
    return this;
  }

  public URIBuilder setPort(int port) {
    this.port = port;
    return this;
  }

  public URIBuilder setUsername(String userName) {
    this.userName = userName;
    return this;
  }

  @Override
  public String toString() {
    return build().toString();
  }
}
