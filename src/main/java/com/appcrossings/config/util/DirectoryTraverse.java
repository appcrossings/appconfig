package com.appcrossings.config.util;

import java.util.Arrays;
import com.appcrossings.config.source.Traverse;

public class DirectoryTraverse implements Traverse {

  private final String path;
  private final String[] paths;
  private final String fileName;
  private int i;

  public DirectoryTraverse(String path) {
    this(path, UriUtil.getFileName(path).orElse(""));
  }

  public DirectoryTraverse(String path, String fileName) {
    this.path = path.trim();
    this.paths = UriUtil.getDirSegements(path);
    this.fileName = fileName;
    this.i = this.paths.length;
  }

  @Override
  public String ascend() {

    if (i > available())
      i = available();

    if (i < 0)
      i = 0;

    if (hasNextUp()) {
      return at(i++);
    }

    return at(i);
  }


  @Override
  public String decend() {

    if (i > available())
      i = available();

    if (i < 0)
      i = 0;

    if (hasNextDown()) {
      return at(i--);
    }

    return at(i);

  }

  @Override
  public String at(int index) {

    String dir = String.join("/", Arrays.copyOfRange(this.paths, 0, index));

    if (StringUtils.hasText(this.fileName) && StringUtils.hasText(dir)) {
      dir += "/" + this.fileName;
    } else if (StringUtils.hasText(this.fileName) && !StringUtils.hasText(dir)) {
      dir += this.fileName;
    }
    
    if(path.startsWith("/"))
      dir = "/" + dir;

    return dir;
  }

  @Override
  public int available() {
    return this.paths.length;
  }

  @Override
  public boolean hasNextUp() {
    return i <= available();
  }

  @Override
  public boolean hasNextDown() {
    return i >= 0;
  }

}
