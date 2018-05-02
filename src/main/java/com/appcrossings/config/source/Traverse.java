package com.appcrossings.config.source;

public interface Traverse {

  /**
   * Traverse toward the leave of the hierarchy
   * @return
   */
  public String ascend();
  
  /**
   * Traverse towards the base/root of the hierarchy
   * @return
   */
  public String decend();

  /**
   * Check presence of a higher branch/node/leaf
   * @return
   */
  public boolean hasNextUp();
  
  
  /**
   * Check if root of hierarchy is reached
   * @return
   */
  public boolean hasNextDown();

  /**
   * Return path, uri, URL or other location indicator at this level in the hierarchy
   * @param i
   * @return
   */
  public String at(int i);

  /**
   * Available levels of tree depth
   * @param i
   * @return
   */
  public int available();

}
