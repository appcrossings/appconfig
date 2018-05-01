package com.appcrossings.config.source;

import java.io.IOException;
import java.io.InputStream;

public interface StreamingConfigSource {

  public InputStream stream(String propertiesPath) throws IOException;

}
