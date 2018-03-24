package com.appcrossings.config.file;

import java.util.Map;
import com.appcrossings.config.Config;
import com.appcrossings.config.source.BaseRepoDef;
import com.appcrossings.config.source.FileBasedRepo;
import com.appcrossings.config.source.SecuredRepo;

@SuppressWarnings("serial")
public class FileRepoDef extends BaseRepoDef implements FileBasedRepo, SecuredRepo {

  String uri;
  String root = "/";
  String fileName = Config.DEFAULT_PROPERTIES_FILE_NAME;
  String hostsName = Config.DEFAULT_HOSTS_FILE_NAME;
  String userName;
  String passWord;

  protected FileRepoDef() {
    super();
  }

  public FileRepoDef(String name, Map<String, Object> values) {
    super(name, (Map) values.get("file"));
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
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
  public String getRoot() {
    return root;
  }

  @Override
  public String getUserName() {

    return userName;
  }

  @Override
  public String getPassword() {

    return passWord;
  }

}
