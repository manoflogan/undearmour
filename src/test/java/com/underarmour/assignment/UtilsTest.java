/**
 * 
 */
package com.underarmour.assignment;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Test;



/**
 * @author krishnanand (Kartik Krishnanand)
 *
 */
public class UtilsTest {
  
  @Test
  public void testOne() throws Exception {
    String actual = Utils.dateToString(Instant.EPOCH);
    Assert.assertEquals("1970-12-31 16:00:00" , actual);
  }
// 
}
