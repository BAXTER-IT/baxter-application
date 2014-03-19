/**
 * 
 */
package com.baxter.application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baxter.config.client.Configuration;
import com.baxter.config.client.ConfigurationException;
import com.baxter.config.client.Request;
import com.baxter.config.client.RequestBuilder;
import com.baxter.config.om.jvm.Classpath;

/**
 * The utility to extend the class loader with extra class path.
 * 
 * @author xpdev
 * @sinceDevelopmentVersion
 */
class ClassLoaderConfigurator
{

  private static final Logger LOGGER = LoggerFactory.getLogger(ClassLoaderConfigurator.class);

  private final Method addURL;

  ClassLoaderConfigurator()
  {
	addURL = getAddUrlMethod();
  }

  private static Method getAddUrlMethod()
  {
	try
	{
	  final Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
	  addURL.setAccessible(true);
	  return addURL;
	}
	catch (final NoSuchMethodException e)
	{
	  LOGGER.warn("Cannot get method to modify ClassLoader", e);
	  return null;
	}
  }

  void configure() throws ApplicationException
  {
	final ClassLoader cl = ClassLoaderConfigurator.class.getClassLoader();
	if (cl instanceof URLClassLoader)
	{
	  configure((URLClassLoader) cl);
	}
	else
	{
	  LOGGER.warn("The classloader to modify {} is not URLClassLoader", cl);
	}
  }

  void configure(final URLClassLoader classLoader) throws ApplicationException
  {
	if (addURL != null)
	{
	  final RequestBuilder requestBuilder = new RequestBuilder().forType("classpath");
	  try
	  {
		final Request configRequest = requestBuilder.createRequest(Configuration.getInstance().getEnvironment());
		final Classpath cp = configRequest.loadObject(Classpath.class);
		for (final URL cpUrl : cp.getUrls())
		{
		  addURL.invoke(classLoader, cpUrl);
		}
	  }
	  catch (ConfigurationException | MalformedURLException e)
	  {
		throw new ApplicationException("Could not load classpath from remote", e);
	  }
	  catch (InvocationTargetException | IllegalAccessException e)
	  {
		throw new ApplicationException("Could not modify classloader", e);
	  }
	}
	else
	{
	  LOGGER.warn("Attemption to modify ClassLoader {} but no possibility for that", classLoader);
	}
  }

}
