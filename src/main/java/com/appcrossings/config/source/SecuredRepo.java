package com.appcrossings.config.source;

public interface SecuredRepo {
  
  public static final String PASSWORD_FIELD = "password";
  public static final String USERNAME_FIELD = "username";
 

  public String getPassword();

  public String getUsername();

}
