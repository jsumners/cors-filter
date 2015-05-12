package com.jrfom.corsFilter;

/**
 * <p>A set of constants representing the names of CORS headers as defined in
 * <a href="http://www.w3.org/TR/cors/#syntax">http://www.w3.org/TR/cors/#syntax</a>.</p>
 *
 * <p>The constants herein strip the "Access-Control" part of the name. That is,
 * if the header is {@code Access-Control-Allow-Origin} then the constant name
 * is {@code AllowOrigin}.</p>
 *
 * <p>All constant values are lower case. Thus:</p>
 *
 * {@code
 * CorsHeaders.AllowOrigin.equals("Access-Control-Allow-Origin"); // false
 * CorsHeaders.AllowOrigin.equals("access-control-allow-origin"); // true
 * }
 */
public class CorsHeaders {
  public final static String AllowCredentials = "access-control-allow-credentials";
  public final static String AllowHeaders = "access-control-allow-headers";
  public final static String AllowMethods = "access-control-allow-methods";
  public final static String AllowOrigin = "access-control-allow-origin";
  public final static String ExposeHeaders = "access-control-expose-headers";
  public final static String MaxAge = "access-control-max-age";
  public final static String Origin = "origin";
  public final static String RequestHeaders = "access-control-request-headers";
  public final static String RequestMethod = "access-control-request-method";
}