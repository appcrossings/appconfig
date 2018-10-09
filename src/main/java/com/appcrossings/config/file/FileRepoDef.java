package com.appcrossings.config.file;

import java.net.URI;
import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import com.appcrossings.config.source.DefaultRepoDef;
import com.appcrossings.config.source.FileBasedRepo;
import com.appcrossings.config.source.SecuredRepo;
import com.appcrossings.config.util.URIBuilder;
import com.appcrossings.config.util.UriUtil;

@SuppressWarnings("serial")
public class FileRepoDef extends DefaultRepoDef implements FileBasedRepo, SecuredRepo {

  String fileName;
  String hostsName;
  String password;
  String username;

  /**
   * For testing purposes
   */
  public FileRepoDef(String name) {
    super(name);
  }

  public FileRepoDef(String name, Map<String, Object> values) {
    super(name);

    try {
      if (values != null && !values.isEmpty())
        BeanUtils.populate(this, values);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }

  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getHostsName() {
    return hostsName;
  }

  public void setHostsName(String hostsName) {
    this.hostsName = hostsName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String passWord) {
    this.password = passWord;
  }

  public void setUsername(String userName) {
    this.username = userName;
  }

  @Override
  public String getUsername() {

    return username;
  }

  @Override
  public String[] valid() {

    String[] err = new String[] {};
    
    URI uri = toURI();

    if (UriUtil.validate(uri).isAbsolute().invalid()) {
      err = new String[] {"Uri must be absolute"};
    }

    return err;
  }

  @Override
  public URI toURI() {
    URIBuilder builder = URIBuilder.create(URI.create(getUri()));
    builder.setFileNameIfMissing(getFileName()).setPasswordIfMissing(getPassword())
        .setUsernameIfMissing(getUsername());
    return builder.build();
  }

}
