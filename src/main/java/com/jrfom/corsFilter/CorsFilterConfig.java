package com.jrfom.corsFilter;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

/**
 * <p>Provides a simple mechanism for configuring an instance of
 * {@link CorsFilter}.</p>
 */
public class CorsFilterConfig {
  private Set<String> exposedHeaders = Collections.synchronizedSet(
    new TreeSet<String>()
  );
  private Set<String> headers = Collections.synchronizedSet(
    new TreeSet<String>()
  );
  private Set<String> methods = Collections.synchronizedSet(
    new TreeSet<String>()
  );
  private Set<String> origins = Collections.synchronizedSet(
    new TreeSet<String>()
  );
  private Boolean supportsCredentials = true;
  private Integer preflightMaxAge = 1800;

  /**
   * <p>Creates a basic {@linkplain CorsFilterConfig} with all parameters set
   * to default values. That is, empty {@link #exposedHeaders},
   * empty {@link #headers}, empty {@link #methods}, empty {@link #origins},
   * {@link #supportsCredentials} set to {@code true}, and
   * {@link #preflightMaxAge} set to {@code 1800}.</p>
   */
  public CorsFilterConfig() {}

  /**
   * <p>Creates a very permissive {@linkplain CorsFilterConfig}:</p>
   *
   * <ul>
   *   <li>{@code origin}: "*"</li>
   *   <li>
   *     {@code methods}: "GET,POST,HEAD,OPTIONS"
   *   </li>
   *   <li>
   *     {@code headers}: "Origin,Access-Control-Request-Method,Access-Control-Request-Headers,Accept,X-Requested-With,Content-Type"
   *   </li>
   * </ul>
   *
   * @return An instance of {@link CorsFilterConfig}.
   */
  public static CorsFilterConfig wideOpenConfig() {
    CorsFilterConfig config = new CorsFilterConfig();
    config.addOrigin("*");

    config.addMethod("GET");
    config.addMethod("POST");
    config.addMethod("HEAD");
    config.addMethod("OPTIONS");

    config.supportHeader(CorsHeaders.Origin);
    config.supportHeader(CorsHeaders.RequestMethod);
    config.supportHeader(CorsHeaders.RequestHeaders);
    config.supportHeader("accept");
    config.supportHeader("x-requested-with");
    config.supportHeader("content-type");

    return config;
  }

  /// Localized methods

  /**
   * <p>Adds a header to the list of headers supported by the servlet via
   * CORS.</p>
   *
   * <p>The header name will be converted to all lower case.</p>
   *
   * @param header The header name to be added.
   */
  public void supportHeader(String header) {
    String h = header.toLowerCase();
    if (!this.headers.contains(h)) {
      this.headers.add(h);
    }
  }

  /**
   * <p>Remove a header from the list of headers supported by the servlet via
   * CORS.</p>
   *
   * <p>The header name will be converted to all lower case.</p>
   *
   * <p>Note: this <strong>will</strong> remove the header name from the list
   * of exposed headers if it is in the exposed headers list.</p>
   *
   * @param header The header name to remove.
   */
  public void unsupportHeader(String header) {
    String h = header.toLowerCase();
    if (this.headers.contains(h)) {
      this.headers.remove(h);
    }

    if (this.exposedHeaders.contains(h)) {
      this.exposedHeaders.remove(h);
    }
  }

  /**
   * <p>Expose a header name as CORS supported by the servlet.</p>
   *
   * <p>The header name will be converted to all lower case.</p>
   *
   * <p>Note: if the supplied header name is not already listed as supported,
   * i.e. through {@link #supportHeader(String)}, then it will be added to
   * that list.</p>
   *
   * @param header The header name to expose.
   */
  public void exposeHeader(String header) {
    String h = header.toLowerCase();
    if (!this.exposedHeaders.contains(h)) {
      this.exposedHeaders.add(h);
    }

    if (!this.headers.contains(h)) {
      this.headers.add(h);
    }
  }

  /**
   * <p>Remove a header name from the list of those exposed via CORS
   * by the servlet.</p>
   *
   * <p>The header name will be converted to all lower case.</p>
   *
   * <p>Note: this will <strong>not</strong> remove the header name from the
   * list of supported headers. If that is desired, you should also invoke
   * {@link #unsupportHeader(String)}.</p>
   *
   * @param header The header name to remove.
   */
  public void unexposeHeader(String header) {
    String h = header.toLowerCase();
    if (this.exposedHeaders.contains(h)) {
      this.exposedHeaders.remove(h);
    }
  }

  /**
   * <p>Adds a method name to the list of methods supported by the servlet
   * via CORS.</p>
   *
   * @param method The <strong>case sensitive</strong> method name to add.
   */
  public void addMethod(String method) {
    if (!this.methods.contains(method)) {
      this.methods.add(method);
    }
  }

  /**
   * <p>Removes a method name from the list of methods supported by the
   * servlet via CORS.</p>
   *
   * @param method The <strong>case sensitive</strong> method name to remove.
   */
  public void removeMethod(String method) {
    if (this.methods.contains(method)) {
      this.methods.remove(method);
    }
  }

  /**
   * <p>Adds an origin to the list of origins that are allowed to communicate
   * with the servlet via CORS.</p>
   *
   * <p>If you want to support <em>any</em> origin, then you should add the
   * "*" origin only.</p>
   *
   * @param origin The <strong>case sensitive</strong> origin to add to list of
   *               allowed origins.
   */
  public void addOrigin(String origin) {
    if (!this.origins.contains(origin)) {
      this.origins.add(origin);
    }
  }

  /**
   * <p>Removes an origin from the list of origins that are allowed to
   * communicate with the servlet via CORS.</p>
   *
   * @param origin The <strong>case sensitive</strong> origin to remove.
   */
  public void removeOrigin(String origin) {
    if (this.origins.contains(origin)) {
      this.origins.remove(origin);
    }
  }


  /// Boilerplate getters and setters

  public Set<String> getExposedHeaders() {
    return this.exposedHeaders;
  }

  public void setExposedHeaders(Set<String> exposedHeaders) {
    this.exposedHeaders = exposedHeaders;
  }

  public Set<String> getHeaders() {
    return this.headers;
  }

  public void setHeaders(Set<String> headers) {
    this.headers = headers;
  }

  public Set<String> getMethods() {
    return this.methods;
  }

  public void setMethods(Set<String> methods) {
    this.methods = methods;
  }

  public Set<String> getOrigins() {
    return this.origins;
  }

  public void setOrigins(Set<String> origins) {
    this.origins = origins;
  }

  public Boolean getSupportsCredentials() {
    return this.supportsCredentials;
  }

  public void setSupportsCredentials(Boolean supportsCredentials) {
    this.supportsCredentials = supportsCredentials;
  }

  public Integer getPreflightMaxAge() {
    return this.preflightMaxAge;
  }

  /**
   * <p>The number of seconds a browser will be allowed to cache a preflight
   * request. If set to a negative number then the {@link CorsHeaders#MaxAge}
   * header will not be sent.</p>
   *
   * @param preflightMaxAge Default: 1800.
   */
  public void setPreflightMaxAge(Integer preflightMaxAge) {
    this.preflightMaxAge = preflightMaxAge;
  }
}