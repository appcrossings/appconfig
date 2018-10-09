package com.appcrossings.config.source;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import com.appcrossings.config.util.UriUtil;
import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil2;

@SuppressWarnings("serial")
public class StreamPacket extends PropertyPacket {

  private final static MimeUtil2 mimes = new MimeUtil2();
  private byte[] bytes = new byte[0];
  private String contentType;
  private String encoding = "UTF-8";

  public StreamPacket(URI uri, InputStream stream) throws FileNotFoundException {
    super(uri);
    readBytes(stream);
    detectMediaType();

  }

  public StreamPacket(URI uri, InputStream stream, String contentType)
      throws FileNotFoundException {
    this(uri, stream);
    this.contentType = contentType;
  }

  public byte[] bytes() throws IOException {
    return this.bytes;
  }

  private void detectMediaType() {

    String fileName = UriUtil.getFileName(getUri()).orElse(null);

    if (fileName != null) {

      Collection<MimeType> types = mimes.getMimeTypes(fileName);

      if (types.isEmpty()) {

      }

    } else {

      contentType = (String) mimes.getMimeTypes(bytes).stream().map(m -> {
        return ((MimeType) m).toString();
      }).findFirst().orElse("");

    }

  }

  public String getContentType() {
    return contentType;
  }

  public String getEncoding() {
    return encoding;
  }

  public InputStream getInputStream() {
    return new ByteArrayInputStream(this.bytes);
  }

  public boolean hasContent() {
    return bytes.length > 0;
  }

  private void readBytes(final InputStream stream) throws FileNotFoundException {

    if (stream == null) {
      return;
    }

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    int nRead;
    byte[] data = new byte[1024];

    try {

      while ((nRead = stream.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, nRead);
      }

      buffer.flush();
      this.bytes = buffer.toByteArray();

    } catch (FileNotFoundException e) {

      throw e;

    } catch (IOException e) {

      // nothing

    } finally {
      try {
        buffer.close();
      } catch (Exception e) {
        // TODO: handle exception
      }

      try {
        stream.close();
      } catch (Exception e) {
        // TODO: handle exception
      }
    }

  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }
}
