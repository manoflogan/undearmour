package com.underarmour.assignment;

import java.io.InputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



/**
 * Unit test for {@Database Credentials}.
 * @author krishnanand (Kartik Krishnanand)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=DatabaseCredentials.class)
public class DatabaseCredentialsTest {
  
  @Autowired
  private DatabaseCredentials databaseCredentials;
  
  
  private Properties props;
  
  private InputStream is;
  
  @Before
  public void setUp() throws Exception {
    Assert.assertNotNull(this.databaseCredentials);
    this.props = new Properties();
    is = ClassLoader.getSystemResourceAsStream("application.properties");
    this.props.load(is);
  }
  
  @After
  public void tearDown() throws Exception {
    this.databaseCredentials = null;
    if (this.is != null) {
      this.is.close();
    }
    System.setProperty("h2.implicitRelativePath", "");
  }
  
  @Test
  public void testInitialisation() throws Exception {
    Assert.assertEquals(
        this.props.getProperty("spring.datasource.url"),
        this.databaseCredentials.getUrl());
    Assert.assertEquals(
        this.props.getProperty("spring.datasource.driver-class-name"),
        this.databaseCredentials.getDriverClass());
    Assert.assertEquals(
        this.props.getProperty("spring.datasource.username"),
        this.databaseCredentials.getUsername());
    Assert.assertEquals(
        this.props.getProperty("spring.datasource.password"),
        this.databaseCredentials.getPassword());
  }
  
  @Test
  public void testSystemProperty() throws Exception {
    Assert.assertEquals(
        System.getProperty("h2.implicitRelativePath"), "true");
    
  }
}
