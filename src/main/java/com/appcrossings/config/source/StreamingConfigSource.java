package com.appcrossings.config.source;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public interface StreamingConfigSource {
  
  public InputStream stream(String propertiesPath, Optional<RepoDef> repo)  throws IOException;

}
