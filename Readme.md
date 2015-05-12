# CORS Filter

Provides a Java Servlet filter to manage CORS requests. This filter can be
configured via JavaConfig or via a web.xml.

Requires Java 8.

## But why?

Yes, there are already several of these filters out there. None of them support
JavaConfig. And the one bundled with Tomcat was giving me issues on a project.
So I wrote this simple implementation.

## Install

This library is available as a Maven artifact. Simply add the
dependency:

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<depdendencies>
  <dependency>
    <groupId>com.github.jsumners</groupId>
    <artifactId>cors-filter</artifactId>
    <version>0.1.0</version>
  </dependency>
</dependencies>
```

## web.xml config

* `cors.allowed.origins`: a comma separated list of origins,
  e.g. "http://example.com". Default: "*"
* `cors.allowed.methods`: a comma separated list of HTTP methods
  that are allowed to be used for CORS requests. Default: "GET,POST,HEAD,OPTIONS"
* `cors.allowed.headers`: a comma separated list of allowed
  headers in a CORS request (they will be converted to all lower case).
  Default: "origin,accept,x-requested-with,content-type,access-control-request-method,access-control-request-headers"
* `cors.exposed.headers`: a comma separated list of headers
  to expose via CORS requests. Default: ""
* `cors.preflight.maxage`: number of seconds to allow clients
  to cache CORS preflight requests. Set to -1 to prevent the header
  from being sent. Default: 1800
* `cors.support.credentials`: boolean indicating if the servlet
  supports CORS requests with credentials. Default: "true"

## JavaConfig

See the JavaDoc for the `CorsFilterConfig` class (it's brain dead simple).

# Licence

[http://jsumners.mit-license.org/](http://jsumners.mit-license.org/)