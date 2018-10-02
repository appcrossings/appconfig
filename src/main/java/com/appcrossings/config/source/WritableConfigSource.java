package com.appcrossings.config.source;

import java.util.Map;

/**
 * Defines a config source cable of handling writes
 * 
 * @author kkarski
 *
 */
public interface WritableConfigSource {

  /**
   * Writes all properties to the given path. If properties already exist, they will be overwritten
   * with the new values. Any properties not in the map, will be deleted/lost.
   * 
   * @param path path to the property being updated. Must be in format "my/property/path"
   * @param props Properties with keys and values being overwritten
   * @return True if operation succeeds
   */
  public boolean put(String path, Map<String, Object> props);

  /**
   * Updates the given property's value in the store. If the property doesn't exist, it will be
   * created. If it already exists, it's value will be updated to the new provided value. The etag
   * value serves as an optimistic lock. If the etag doesn't match, the update operation will fail
   * assuming underlying value has changed since read. To force an update, pass an empty etag which essentially
   * renders the method a put.
   * 
   * @param path path to the property being updated. Must be in format
   *        "my/property/path#property.name
   * @param etag Etag value when last reading the value being overwritten. This provides an
   *        optimistic lock. Pass an empty value to force update.
   * @param key Property key
   * @param value Value being storied
   * @return True if operation succeeds
   */
  public boolean patch(String path, String etag, Map<String, Object> props);

}
