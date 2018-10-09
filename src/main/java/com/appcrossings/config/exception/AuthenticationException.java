package com.appcrossings.config.exception;

@SuppressWarnings("serial")
public class AuthenticationException extends RuntimeException {
  
  public AuthenticationException(String message) {
    super(message);
  }

}
