package com.jrfom.corsFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Implements a standard {@link Servlet} {@link Filter} for processing
 * CORS requests according to the spec at
 * <a href="http://www.w3.org/TR/cors/">http://www.w3.org/TR/cors/</a>.</p>
 *
 * <p>This filter can be configured and added to a servlet at runtime.
 * Simply create and instance of {@link CorsFilter} and give it some
 * configuration via {@link CorsFilter#setConfig(CorsFilterConfig)}.</p>
 *
 * <p>If you simply want to allow pretty much any CORS requests, you can
 * use the instance returned by {@link CorsFilter#wideOpenFilter()}. It will
 * use the configuration as returned by {@link CorsFilterConfig#wideOpenConfig()}.</p>
 *
 * <p>You could also add the filter via your web.xml configuration. The
 * following configuration parameters are available:</p>
 *
 * <ul>
 *   <li>
 *     <code>cors.allowed.origins</code>: a comma separated list of origins,
 *     e.g. "http://example.com". Default: "*"
 *   </li>
 *   <li>
 *     <code>cors.allowed.methods</code>: a comma separated list of HTTP methods
 *     that are allowed to be used for CORS requests. Default: "GET,POST,HEAD,OPTIONS"
 *   </li>
 *   <li>
 *     <code>cors.allowed.headers</code>: a comma separated list of allowed
 *     headers in a CORS request (they will be converted to all lower case).
 *     Default: "origin,accept,x-requested-with,content-type,access-control-request-method,access-control-request-headers"
 *   </li>
 *   <li>
 *     <code>cors.exposed.headers</code>: a comma separated list of headers
 *     to expose via CORS requests. Default: ""
 *   </li>
 *   <li>
 *     <code>cors.preflight.maxage</code>: number of seconds to allow clients
 *     to cache CORS preflight requests. Set to -1 to prevent the header
 *     from being sent. Default: 1800
 *   </li>
 *   <li>
 *     <code>cors.support.credentials</code>: boolean indicating if the servlet
 *     supports CORS requests with credentials. Default: "true"
 *   </li>
 * </ul>
 */
public class CorsFilter implements Filter {
  private static final Logger log = LoggerFactory.getLogger(CorsFilter.class);
  private CorsFilterConfig config;

  /**
   * <p>Creates an instance of {@link CorsFilter} that allows CORS requests
   * from any origin. The configuration used is that which is returned by
   * {@link CorsFilterConfig#wideOpenConfig()}.</p>
   *
   * @return A very permissive CORS filter.
   */
  public static CorsFilter wideOpenFilter() {
    CorsFilter filter = new CorsFilter();
    filter.setConfig(CorsFilterConfig.wideOpenConfig());
    return filter;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    this.config = new CorsFilterConfig();

    String allowedOrigins = filterConfig.getInitParameter("cors.allowed.origins");
    if (allowedOrigins == null) {
      config.addOrigin("*");
    } else {
      String[] origins = (allowedOrigins.indexOf(",") > -1) ?
        allowedOrigins.split(",") : new String[] { allowedOrigins };
      Arrays.asList(origins).forEach( (o) -> config.addOrigin(o) );
    }

    String allowedMethods = filterConfig.getInitParameter("cors.allowed.methods");
    if (allowedMethods == null) {
      allowedMethods = "GET,POST,HEAD,OPTIONS";
    }
    String[] methods = (allowedMethods.indexOf(",") > -1) ?
      allowedMethods.split(",") : new String[] { allowedMethods };
    Arrays.asList(methods).forEach( (m) -> config.addMethod(m) );

    String allowedHeaders = filterConfig.getInitParameter("cors.allowed.headers");
    if (allowedHeaders == null) {
      allowedHeaders = "origin,accept,x-requested-with,content-type,access-control-request-method,access-control-request-headers";
    }
    String[] headers = (allowedHeaders.indexOf(",") > -1) ?
      allowedHeaders.split(",") : new String[] { allowedHeaders };
    Arrays.asList(headers).forEach( (h) -> config.supportHeader(h) );

    String exposedHeaders = filterConfig.getInitParameter("cors.exposed.headers");
    if (exposedHeaders == null) {
      exposedHeaders = "";
    }
    String[] exposed = (exposedHeaders.indexOf(",") > -1) ?
      exposedHeaders.split(",") : new String[] { exposedHeaders };
    Arrays.asList(exposed).forEach( (e) -> {
      if (!e.equals("")) {
        config.exposeHeader(e);
      }
    });

    String strMaxAge = filterConfig.getInitParameter("cors.preflight.maxage");
    if (strMaxAge == null) {
      strMaxAge = "1800";
    }
    this.config.setPreflightMaxAge(Integer.valueOf(strMaxAge));

    String strCredentials = filterConfig.getInitParameter("cors.support.credentials");
    if (strCredentials == null) {
      strCredentials = "true";
    } else if (strCredentials.equals("0") || strCredentials.equals("1")) {
      strCredentials = (strCredentials.equals("0")) ? "false" : "true";
    }
    this.config.setSupportsCredentials(Boolean.valueOf(strCredentials));
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    // No Origin header at all? No more CORS processing
    String requestOrigin = request.getHeader(CorsHeaders.Origin);
    if (requestOrigin == null) {
      log.debug("Aborting CORS processing. Origin header not set");
      chain.doFilter(req, res);
      return;
    }

    // Origin isn't in the allowed list? No more CORS processing
    if (!this.originIsAllowed(requestOrigin)) {
      log.debug("Aborting CORS processing. Origin not allowed: `{}`", requestOrigin);
      chain.doFilter(req, res);
      return;
    }

    // Request method isn't in allowed list? No more CORS processing
    if (request.getMethod() == null ||
      !this.config.getMethods().contains(request.getMethod()) )
    {
      log.debug("Aborting CORS processing: Method not allowed: `{}`", request.getMethod());
      chain.doFilter(req, res);
      return;
    }

    if (this.isSimpleMethod(request)) {
      // Section 6.1, step 3
      this.addOriginAndCredentialsResponseHeaders(response, requestOrigin);

      // Section 6.1, step 4
      if (this.config.getExposedHeaders().size() > 0) {
        log.debug("Setting exposed headers header");
        response.addHeader(
          CorsHeaders.ExposeHeaders,
          this.toCSV(this.config.getExposedHeaders())
        );
      }

      log.debug("Simple CORS request complete");
      chain.doFilter(req, res);
      return;
    }

    if (!isPreflightRequest(request)) {
      chain.doFilter(req, res);
      return;
    }

    /// Begin preflight request processing
    log.debug("Starting to process preflight CORS request");

    // Section 6.2, step 3 & 5
    String method = request.getHeader(CorsHeaders.RequestMethod);
    if (method == null || !this.config.getMethods().contains(method)) {
      log.debug("Aborting CORS processing. Request method did not validate: `{}`", method);
      chain.doFilter(req, res);
      return;
    }

    // Section 6.2, step 4
    log.debug("Processing request headers header");
    String strHeaders = request.getHeader(CorsHeaders.RequestHeaders);
    String[] headers;
    if (strHeaders == null) {
      headers = new String[0];
    } else {
      headers = (strHeaders.indexOf(",") > -1) ?
        strHeaders.split(",") : new String[] { strHeaders.trim() };
    }
    List<String> listHeaders = Arrays.asList(headers);
    listHeaders.stream().map((h) -> h.toLowerCase());

    // Section 6.2, step 6
    long matches = listHeaders.stream()
      .filter( (h) -> this.config.getHeaders().contains(h) )
      .count();
    if (matches == 0) {
      log.debug(
        "Aborting CORS processing. Request headers header invalid: `{}`",
        strHeaders
      );
      chain.doFilter(req, res);
      return;
    }
    log.debug("Request headers header validated");

    // Section 6.2, step 7
    this.addOriginAndCredentialsResponseHeaders(response, requestOrigin);

    // Section 6.2, step 8
    if (this.config.getPreflightMaxAge() >= 0) {
      log.debug("Setting preflight cache max age header to: `{}`", this.config.getPreflightMaxAge());
      response.addHeader(CorsHeaders.MaxAge, this.config.getPreflightMaxAge().toString());
    }

    // Section 6.2, step 9
    response.addHeader(
      CorsHeaders.AllowMethods,
      this.toCSV(this.config.getMethods())
    );

    // Section 6.2, step 10
    response.addHeader(
      CorsHeaders.AllowHeaders,
      this.toCSV(this.config.getHeaders())
    );

    log.debug("CORS processing finished");
    chain.doFilter(req, res);
  }

  @Override
  public void destroy() {}

  public CorsFilterConfig getConfig() {
    return this.config;
  }

  public void setConfig(CorsFilterConfig config) {
    this.config = config;
  }

  private void addOriginAndCredentialsResponseHeaders(HttpServletResponse response, String origin) {
    log.debug("Adding origin header set to: `{}`", origin);
    response.addHeader(CorsHeaders.AllowOrigin, origin);
    if (this.config.getSupportsCredentials()) {
      log.debug("Setting supports credentials header to: `true`");
      response.addHeader(CorsHeaders.AllowCredentials, "true");
    }
  }

  protected Boolean isPreflightRequest(HttpServletRequest request) {
    Boolean result = false;

    if (request.getMethod() != null && request.getMethod().equalsIgnoreCase("options")) {
      result = request.getHeader(CorsHeaders.RequestMethod) != null;
    }

    log.debug("Preflight request: `{}`", result);
    return result;
  }

  protected Boolean isSimpleMethod(HttpServletRequest request) {
    Boolean result = true; // should only be when the method is null

    if (request.getMethod() != null) {
      switch (request.getMethod().toLowerCase()) {
        case "get":
        case "head":
        case "post":
          result = true;
          break;
        default:
          result = false;
      }
    }

    log.debug("Simple request: `{}`", result);
    return result;
  }

  protected Boolean originIsAllowed(String origin) {
    Boolean result;

    if (this.config.getOrigins().size() == 1 && this.config.getOrigins().contains("*")) {
      log.debug("All origins allowed. Allowing origin: `{}`", origin);
      result = true;
    } else {
      log.debug("Validating origin against allowed list. Origin: `{}`", origin);
      result = this.config.getOrigins().contains(origin);
    }

    return result;
  }

  protected String toCSV(Set<String> set) {
    StringBuilder result = new StringBuilder();

    set.stream().forEach( (item) ->
        result.append(
          String.format("%s,", item)
        )
    );

    return result.substring(0, result.lastIndexOf(","));
  }
}