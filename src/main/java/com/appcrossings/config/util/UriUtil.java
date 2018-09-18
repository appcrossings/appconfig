package com.appcrossings.config.util;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UriUtil {

  public static URI buildURI(String userName, String password, String scheme, int port,
      String authority, String path, String fileName) {

    StringBuilder s = new StringBuilder();

    s.append(scheme + ":");

    if (StringUtils.hasText(authority)) {
      s.append("//" + authority);

      if (port > 0)
        s.append(":" + port);
    }

    if (!path.endsWith(File.separator) && !path.equals(""))
      path += File.separator;

    s.append(path);

    if (StringUtils.hasText(fileName) && fileName.startsWith(File.separator)) {
      s.append(fileName.replaceFirst(File.separator, ""));
    }

    if (StringUtils.hasText(fileName))
      s.append(fileName);

    return URI.create(s.toString());

  }

  public static String getPath(URI uri) {
    String path = uri.getPath();

    if (path == null) {
      path = uri.getSchemeSpecificPart();
    }

    return path;
  }

  public static URI getRoot(URI uri) {

    String path = getPath(uri);
    String root = uri.toString();

    if (StringUtils.hasText(path) && !path.equals(root)) {
      root = uri.toString().replace(path, "");
    }

    if (root.equals(uri.getScheme() + ":")) {
      root += "/";
    }

    return URI.create(root);

  }

  public static String[] getDirSegements(String path) {

    String[] dirs = new String[] {};

    if (StringUtils.hasText(path)) {
      if (!getFileName(path).isPresent()) {
        dirs = path.split("/");
      } else {
        dirs = path.split("/");
        dirs = Arrays.copyOfRange(dirs, 0, dirs.length - 1);
      }
    }

    dirs = Arrays.stream(dirs).filter(s -> {
      return StringUtils.hasText(s);
    }).collect(Collectors.toList()).toArray(new String[] {});

    return dirs;
  }
  
  public static String[] getDirSegements(URI uri) {

    String path = getPath(uri);
    return getDirSegements(path);
  }

  public static Optional<String> getFileName(final String path) {

    Optional<String> fileName = Optional.empty();
    String p = path;

    try {

      URI uri = URI.create(path);
      p = getPath(uri);

    } catch (Exception e) {
      // good, should NOT be a URI
    }


    if (hasFile(p)) {

      if (p.contains(File.separator)) {
        return Optional.of(p.substring(p.lastIndexOf(File.separator) + 1, p.length()));
      } else if (p.contains(".")) {
        return Optional.of(p);
      }
    }

    return fileName;

  }

  public static Optional<String> getFileName(URI uri) {

    Optional<String> fileName = Optional.empty();

    if (hasFile(uri.toString())) {

      String path = getPath(uri);

      fileName = getFileName(path);
    }

    return fileName;

  }

  public static String getPassword(URI uri) {

    String passwrod = null;
    if (StringUtils.hasText(uri.getUserInfo()) && uri.getUserInfo().contains(":")) {
      passwrod = uri.getUserInfo().split(":")[1];
    } else {
      passwrod = uri.getUserInfo();
    }

    return passwrod;
  }

  public static String getUsername(URI uri) {

    String userName = null;
    if (StringUtils.hasText(uri.getUserInfo()) && uri.getUserInfo().contains(":")) {
      userName = uri.getUserInfo().split(":")[0];
    } else {
      userName = uri.getUserInfo();
    }

    return userName;
  }

  public static boolean hasFile(String uri) {

    String path = uri;

    try {

      URI ri = URI.create(uri);
      path = getPath(ri);

    } catch (Exception e) {
      // TODO: handle exception
    }

    if (path.toString().contains(File.separator)) {
      return path.toString().substring(path.toString().lastIndexOf(File.separator)).contains(".");
    }

    return path.toString().contains(".");
  }

  public static boolean isURI(String url) {

    try {

      URI uri = URI.create(url);

    } catch (Exception e) {
      return false;
    }

    return true;
  }

  public static UriValidator validate(String uri) {

    return new UriUtil.UriValidator(uri);
  }

  public static UriValidator validate(URI uri) {

    return new UriUtil.UriValidator(uri);
  }

  public static class UriValidator {

    private final URI uri;
    private Predicate<URI> test;

    protected UriValidator(URI uri) {
      this.uri = uri;
      this.test = u -> (u != null);
    }

    protected UriValidator(String str) {
      URI uri = URI.create(str);
      this.uri = uri;
      this.test = u -> (u != null);
    }

    public UriValidator hasHost() {
      this.test = this.test.and(u -> (StringUtils.hasText(u.getHost())));
      return this;
    }

    public UriValidator hasUsername() {
      this.test = this.test.and(u -> (StringUtils.hasText(UriUtil.getUsername(u))));
      return this;
    }

    public UriValidator hasPassword() {
      this.test = this.test.and(u -> (StringUtils.hasText(UriUtil.getPassword(u))));
      return this;
    }

    public UriValidator hasPath() {
      this.test = this.test.and(u -> (StringUtils.hasText(UriUtil.getPath(u))));
      return this;
    }

    public UriValidator hasScheme() {
      this.test =
          this.test.and(u -> (StringUtils.hasText(u.getScheme()) && !u.getScheme().equals("null")));
      return this;
    }

    public UriValidator isAbsolute() {
      this.test = this.test.and(u -> (u.isAbsolute() && !u.getScheme().equals("null")));
      return this;
    }

    public UriValidator hasFile() {
      this.test = this.test.and(u -> (UriUtil.hasFile(u.toString())));
      return this;
    }

    public UriValidator isScheme(String... strings) {

      Predicate<URI> test = u -> (u.getScheme() != null);

      for (String s : strings) {
        test.and(test.or(u -> (u.getScheme().equalsIgnoreCase(s))));
      }

      if (strings.length > 0)
        this.test = this.test.and(test);

      return this;
    }

    public boolean valid() {
      return this.test.test(uri);
    }

    public boolean invalid() {
      return !this.test.test(uri);
    }
  }

}
