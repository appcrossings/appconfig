package com.appcrossings.config.util;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptUtil {

  public static final Logger log = LoggerFactory.getLogger(EncryptUtil.class);
  private BasicTextEncryptor encryptor = new BasicTextEncryptor();

  public EncryptUtil() {
    encryptor.setPassword(password());
  }

  public String[] values() {
    return new String[] {"password"};
  }

  public String password() {
    return "secret";
  }

  public static void main(String[] args) {
    new EncryptUtil().encrypt();
  }

  public final void encrypt() {

    for (String s : values()) {
      log.info("Encrypted [ " + s + " ] : [ " + encryptor.encrypt(s) + " ]");
    }

  }

}
