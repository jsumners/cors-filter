package com.jrfom.corsFilter;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CorsFilterConfigTest {
  CorsFilterConfig config;

  @Before
  public void setup() {
    this.config = new CorsFilterConfig();
  }

  @Test
  public void testSupportHeader() throws Exception {
    assertEquals(0, this.config.getHeaders().size());

    this.config.supportHeader("fooBar");
    assertEquals(1, this.config.getHeaders().size());
    assertTrue(this.config.getHeaders().contains("foobar"));
  }

  @Test
  public void testUnsupportHeader() throws Exception {
    assertEquals(0, this.config.getHeaders().size());

    this.config.supportHeader("fooBar");
    assertEquals(1, this.config.getHeaders().size());

    this.config.unsupportHeader("fooBar");
    assertEquals(0, this.config.getHeaders().size());

    // And with the header exposed
    this.config.supportHeader("fooBar");
    this.config.exposeHeader("fooBar");
    assertEquals(1, this.config.getHeaders().size());
    assertEquals(1, this.config.getExposedHeaders().size());

    this.config.unsupportHeader("fooBar");
    assertEquals(0, this.config.getHeaders().size());
    assertEquals(0, this.config.getExposedHeaders().size());
  }

  @Test
  public void testExposeHeader() throws Exception {
    assertEquals(0, this.config.getHeaders().size());
    assertEquals(0, this.config.getExposedHeaders().size());

    this.config.exposeHeader("fooBar");
    assertEquals(1, this.config.getExposedHeaders().size());
    assertEquals(1, this.config.getHeaders().size());
    assertTrue(this.config.getExposedHeaders().contains("foobar"));
    assertTrue(this.config.getHeaders().contains("foobar"));
  }

  @Test
  public void testUnexposeHeader() throws Exception {
    assertEquals(0, this.config.getHeaders().size());
    assertEquals(0, this.config.getExposedHeaders().size());

    this.config.exposeHeader("fooBar");
    assertEquals(1, this.config.getExposedHeaders().size());
    assertEquals(1, this.config.getHeaders().size());

    this.config.unexposeHeader("fooBar");
    assertEquals(0, this.config.getExposedHeaders().size());
    assertEquals(1, this.config.getHeaders().size());
    assertTrue(this.config.getHeaders().contains("foobar"));
  }

  @Test
  public void testAddMethod() throws Exception {
    assertEquals(0, this.config.getMethods().size());

    this.config.addMethod("fooBar");
    assertEquals(1, this.config.getMethods().size());
    assertTrue(this.config.getMethods().contains("fooBar"));
  }

  @Test
  public void testRemoveMethod() throws Exception {
    assertEquals(0, this.config.getMethods().size());

    this.config.addMethod("fooBar");
    assertEquals(1, this.config.getMethods().size());

    this.config.removeMethod("fooBar");
    assertEquals(0, this.config.getMethods().size());
  }

  @Test
  public void testAddOrigin() throws Exception {
    assertEquals(0, this.config.getOrigins().size());

    this.config.addOrigin("*");
    assertEquals(1, this.config.getOrigins().size());
    assertTrue(this.config.getOrigins().contains("*"));
  }

  @Test
  public void testRemoveOrigin() throws Exception {
    assertEquals(0, this.config.getOrigins().size());

    this.config.addOrigin("*");
    assertEquals(1, this.config.getOrigins().size());

    this.config.removeOrigin("*");
    assertEquals(0, this.config.getOrigins().size());
  }
}