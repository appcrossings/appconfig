package com.appcrossings.config.source;

public interface FileBasedRepo extends RepoDef {
  
  public static final String FILE_NAME_FIELD = "fileName";
  public static final String HOSTS_FILE_NAME_FIELD = "hostsName";
 
  public String getFileName();

  public String getHostsName();


}
