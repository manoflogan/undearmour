/**
 * 
 */
package com.underarmour.assignment;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Collection of utility functions.
 * 
 * @author krishnanand (Kartik Krishnanand)
 */
public class Utils {
  
  private Utils() {
    throw new AssertionError("Not to be instantiated.");
  }
  
  private static final String TIMESTAMP_FORMAT = "YYYY-MM-DD HH:MM:SS";
  
  /**
   * Converts string to date
   * 
   * @param str string to converted to string
   * @return datetime objects
   */
  static DateTime stringToDate(String str) {
    DateTimeFormatter format = DateTimeFormat.forPattern(TIMESTAMP_FORMAT);
    DateTime dateTime = format.parseDateTime(str);
    return dateTime;
  }
  
  /**
   * Converts date to string
   * 
   * @return datetime objects
   */
  static String currentTimeToDate() {
    DateTime dt = currentTime();
    return dateToString(dt);
  }
  
  static String dateToString(DateTime dateTime) {
    return dateTime != null ? dateTime.toString(TIMESTAMP_FORMAT) : "";
  }
  
  static DateTime currentTime() {
    return DateTime.now().withZone(DateTimeZone.UTC);
  }
  
  /**
   * Adds times to current date time.
   * 
   * @param seconds seconds to be incremented
   * @return date time
   */
  static DateTime addTimeToCurrent(Integer seconds, int defaultTime) {
    DateTime dt = currentTime();
    DateTime newTime = dt.plusSeconds(seconds != null ? seconds.intValue() : defaultTime);
    return newTime;
  }

}
