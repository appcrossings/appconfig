package com.appcrossings.config.http;

import java.util.Map;
import org.apache.commons.beanutils.BeanUtils;
import com.appcrossings.config.source.DefaultRepoDef;
import com.appcrossings.config.source.FileBasedRepo;
import com.appcrossings.config.source.SecuredRepo;

@SuppressWarnings("serial")
public class HttpRepoDef extends DefaultRepoDef implements FileBasedRepo, SecuredRepo {

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

  String fileName;
  String hostsName;
  String passWord;
  String root;
  String uri;
  String userName;

  /**
   * For testing convenience
   */
  protected HttpRepoDef() {
    super();
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

  @Override
  public String getConfigFileName() {
    return fileName;
  }

  @Override
  public String getHostsFileName() {
    return hostsName;
  }

  @Override
  public String getPassword() {
    // TODO Auto-generated method stub
    return passWord;
  }

  @Override
  public String getRoot() {
    return root;
  }

  public String getUri() {
    return uri;
  }

  @Override
  public String getUserName() {
    // TODO Auto-generated method stub
    return userName;
  }

  public void setRoot(String root) {
    this.root = root;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }


}
