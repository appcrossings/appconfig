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
  String passWord;
  String userName;

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

  public String getPassWord() {
    return passWord;
  }

  public void setPassWord(String passWord) {
    this.passWord = passWord;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  @Override
  public String getPassword() {

    return passWord;
  }

  @Override
  public String getUsername() {

    return userName;
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
    builder.setFileNameIfMissing(getFileName()).setPasswordIfMissing(getPassWord())
        .setUsernameIfMissing(getUsername());
    return builder.build();
  }

}
