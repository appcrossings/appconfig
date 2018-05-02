package com.appcrossings.config.http;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.beanutils.BeanUtils;
import com.appcrossings.config.source.DefaultRepoDef;
import com.appcrossings.config.source.FileBasedRepo;
import com.appcrossings.config.source.SecuredRepo;
import com.appcrossings.config.util.StringUtils;
import com.appcrossings.config.util.URIBuilder;

@SuppressWarnings("serial")
public class HttpRepoDef extends DefaultRepoDef implements FileBasedRepo, SecuredRepo {

  String fileName;

  String hostsName;

  String passWord;

  String userName;

  /**
   * For testing convenience
   */
  protected HttpRepoDef() {
    super();
  }

  public HttpRepoDef(String name) {
    this.name = name;
  }

  public HttpRepoDef(String name, Map<String, Object> values) {
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

  public String getHostsName() {
    return hostsName;
  }

  @Override
  public String getPassword() {
    return passWord;
  }

  public String getUri() {
    return uri;
  }

  @Override
  public String getUsername() {
    return userName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public void setHostsName(String hostsName) {
    this.hostsName = hostsName;
  }

  public void setPassWord(String passWord) {
    this.passWord = passWord;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  @Override
  public URI toURI() {
    URIBuilder builder = URIBuilder.create(URI.create(getUri()));
    builder.setFileNameIfMissing(getFileName()).setPasswordIfMissing(getPassword()).setUsernameIfMissing(getUsername());
    return builder.build();
  }

  @Override
  public String[] valid() {

    Set<String> errors = new HashSet<>();

    if (!(StringUtils.hasText(getUri()) && StringUtils.hasText(getHostsName())
        && StringUtils.hasText(getFileName()))) {
      errors.add("Missing required values. Uri, hostFileName, configFileName are all required");
    } else {

      try {
        URI.create(getUri());
      } catch (IllegalArgumentException e) {
        errors.add("Uri is malformed or missing. Error:" + e.getMessage());
      }
    }

    return errors.toArray(new String[] {});
  }

}
