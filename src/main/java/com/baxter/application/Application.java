/**
 * 
 */
package com.baxter.application;

/**
 * @author xpdev
 * 
 */
public interface Application
{

  /**
   * Components what implement Application interface should have this message on
   * the log. It will help to find where the component is restarted.
   */
  String START_MESSAGE = "*** CreateApplication invoked. ***";

  void startup() throws ApplicationException;

  void shutdown(boolean force);

}
