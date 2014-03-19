/**
 * 
 */
package com.baxter.application;

/**
 * @author xpdev
 * 
 */
public class ApplicationException extends Exception
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public ApplicationException(final Throwable cause)
  {
	super(cause);
  }

  public ApplicationException(final String message, final Throwable cause)
  {
	super(message, cause);
  }

}
