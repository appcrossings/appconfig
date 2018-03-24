package com.appcrossings.config.http;

import java.util.Map;
import com.appcrossings.config.Config;
import com.appcrossings.config.source.BaseRepoDef;
import com.appcrossings.config.source.FileBasedRepo;
import com.appcrossings.config.source.SecuredRepo;

@SuppressWarnings("serial")
public class HttpRepoDef extends BaseRepoDef implements FileBasedRepo, SecuredRepo {

  String uri;
  String root = "/";
  String fileName = Config.DEFAULT_PROPERTIES_FILE_NAME;
  String hostsName = Config.DEFAULT_HOSTS_FILE_NAME;
  String userName;
  String passWord;

  protected HttpRepoDef() {
    super();
  }

  public HttpRepoDef(String name, Map<String, Object> values) {
    super(name, (Map) values.get("http"));
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  @Override
  public String getRoot() {
    return root;
  }

  public void setRoot(String root) {
    this.root = root;
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
  public String getUserName() {
    // TODO Auto-generated method stub
    return userName;
  }

  @Override
  public String getPassword() {
    // TODO Auto-generated method stub
    return passWord;
  }


}
