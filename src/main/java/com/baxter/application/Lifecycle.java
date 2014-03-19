/**
 * 
 */
package com.baxter.application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Standalone application lifecycle.
 * 
 * @author yura
 * @since ${developmentVersion}
 */
public final class Lifecycle
{

  public enum State
  {
	LAUNCHING
	{
	},
	STARTING
	{
	},
	STARTED
	{
	  @Override
	  State enter()
	  {
		System.out.println("Application started");
		return super.enter();
	  }
	},
	FAILED
	{
	  @Override
	  State enter()
	  {
		System.err.println("Application failed");
		return super.enter();
	  }
	},
	QUITTING
	{
	  @Override
	  State enter()
	  {
		System.err.println("Application quits");
		return super.enter();
	  }
	},
	;
	State enter()
	{
	  return this;
	}
  }

  private static final Lifecycle INSTANCE = new Lifecycle();

  static
  {
	Runtime.getRuntime().addShutdownHook(new Thread()
	{
	  @Override
	  public void run()
	  {
		Lifecycle.getInstance().quitting();
	  }
	});
  }

  private State state;

  private Lifecycle()
  {
  }

  public static Lifecycle getInstance()
  {
	return INSTANCE;
  }

  public void stateTo(final State state)
  {
	if (this.state == state)
	{
	  // ignoring the same state
	  return;
	}
	else if (this.state == null || state.ordinal() > this.state.ordinal())
	{
	  this.state = state.enter();
	}
	else
	{
	  throw new IllegalArgumentException("Illegal state");
	}
  }

  public void launching()
  {
	stateTo(State.LAUNCHING);
  }

  public void start(final String appClassName) throws ApplicationException
  {
	new ClassLoaderConfigurator().configure();
	try
	{
	  final Class<?> loadedClass = Class.forName(appClassName);
	  final Class<? extends Application> appClass = loadedClass.asSubclass(Application.class);
	  start(appClass);
	}
	catch (final ClassNotFoundException e)
	{
	  throw new ApplicationException(e);
	}
  }

  public void start(final Class<? extends Application> appClass) throws ApplicationException
  {
	try
	{
	  final Method method = appClass.getMethod("createApplication");
	  final Application application = (Application) method.invoke(null);
	  stateTo(State.STARTING);
	  Runtime.getRuntime().addShutdownHook(new ShutdownHook(application));
	  application.startup();
	}
	catch (final NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
	{
	  throw new ApplicationException(e);
	}
  }

  public void quitting()
  {
	stateTo(State.QUITTING);
  }

  public void started()
  {
	stateTo(State.STARTED);
  }

  public void failed(final Throwable t)
  {
	stateTo(State.FAILED);
	t.printStackTrace();
	System.exit(1);
  }

}
