package com.appcrossings.config.util;

import java.net.URI;

public class URITraverse extends DirectoryTraverse {

  private final URI uri;
  private final URIBuilder builder;

  public URITraverse(URI uri) {

    super(UriUtil.getPath(uri), UriUtil.getFileName(uri).orElse(""));
    this.uri = uri;
    this.builder = URIBuilder.create(uri);

  }

  public URITraverse(URI uri, String fileName) {
    super(UriUtil.getPath(uri), fileName);
    this.uri = uri;
    this.builder = URIBuilder.create(uri);
  }

  @Override
  public String at(int index) {

    String dir = super.at(index);
    return URIBuilder.create(uri).setPath(dir).build().toString();
  }

}
