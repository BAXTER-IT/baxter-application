package com.baxter.application;

/**
 * The standard application shutdown hook.
 * 
 * @author xpdev
 * 
 */
public class ShutdownHook extends Thread
{

  private final Application application;

  public ShutdownHook(final Application application)
  {
	super("ShutdownHook");
	this.application = application;
  }

  @Override
  public void run()
  {
	application.shutdown(true);
  }

}
