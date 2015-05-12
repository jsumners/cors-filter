package com.jrfom.corsFilter;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CorsFilterTest {

  @Test
  public void testToCSV() throws Exception {
    CorsFilter filter = new CorsFilter();

    String strItems = "a,b,c,d,e,f";
    String[] items = strItems.split(",");

    Set<String> setItems = new TreeSet<>();
    setItems.addAll(Arrays.asList(items));

    String csv = filter.toCSV(setItems);

    assertEquals(strItems, csv);
  }
}