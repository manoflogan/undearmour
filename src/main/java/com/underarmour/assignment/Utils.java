package com.underarmour.assignment;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * Collection of utility functions.
 * 
 * @author krishnanand (Kartik Krishnanand)
 */
public class Utils {
  
  private Utils() {
    throw new AssertionError("Not to be instantiated.");
  }
  
  private static final String TIMESTAMP_FORMAT = "YYYY-MM-dd HH:mm:ss";
  
  static String dateToString(Instant instant) {
    SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
    Date date = Date.from(instant);
    return formatter.format(date);
  }
}
