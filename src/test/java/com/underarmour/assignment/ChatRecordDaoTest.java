/**
 * 
 */
package com.underarmour.assignment;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author krishnanand (Kartik Krishnanand)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= {ChatRecordDao.class, DatabaseCredentials.class})
@Sql(executionPhase=ExecutionPhase.BEFORE_TEST_METHOD,scripts="classpath:/cleanup.sql")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class ChatRecordDaoTest {
  
  @Autowired
  private DataSource dataSource;
  
  @Autowired
  private ChatRecordDao chatRecordDao;
  
  @Test
  public void testInsertChatRecord() {
    ChatRecord record = new ChatRecord();
    record.setUsername("test");
    record.setText("text");
    record.setTimeout(1500);
    long chatId = this.chatRecordDao.insertChatRecord(record);
    Assert.assertTrue(chatId > 0);
  }
  
  @Test
  public void testGetChatRecordById() throws Exception {
    ChatRecord expected = new ChatRecord();
    expected.setUsername("test");
    expected.setText("text");
    expected.setTimeout(1500);
    long chatId = this.chatRecordDao.insertChatRecord(expected);
    expected.setChatId(chatId);
    ChatRecord actual = this.chatRecordDao.getChatRecordById(chatId);
    Assert.assertEquals(expected, actual);
  }
  
  @Test
  public void testGetChatRecordByUsername() throws Exception {
    ChatRecord expected = new ChatRecord();
    expected.setUsername("test");
    expected.setText("text");
    expected.setTimeout(1500);
    long chatId = this.chatRecordDao.insertChatRecord(expected);
    expected.setChatId(chatId);
    Set<ChatRecord> actual = this.chatRecordDao.getChatRecordsByUsername(expected.getUsername());
    Assert.assertEquals(1, actual.size());
    Assert.assertEquals(expected, actual.iterator().next());
  }
  
  @Test
  public void testMarkChatRecordsAsExpired() throws Exception {
    ChatRecord expected = new ChatRecord();
    expected.setUsername("test");
    expected.setText("text");
    expected.setTimeout(14400);
    long chatId = this.chatRecordDao.insertChatRecord(expected);
    expected.setChatId(chatId);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
    Instant instant = Instant.now();
    instant.atOffset(ZoneOffset.UTC);
    int count = jdbcTemplate.queryForObject(
        "SELECT count(*) from Chats where expiration_date >= ?" ,
        new Object[] {Timestamp.from(instant)}, int.class);
    Assert.assertEquals(1, count);
    Set<Long> userIds = new LinkedHashSet<>();
    userIds.add(chatId);
    this.chatRecordDao.markChatRecordsAsExpired(userIds);
    Instant newInstant = Instant.now();
    newInstant.atOffset(ZoneOffset.UTC);
    count = jdbcTemplate.queryForObject(
        "SELECT count(*) from Chats where expiration_date > ?" ,
        new Object[] {Timestamp.from(newInstant)}, int.class);
    Assert.assertEquals(0, count);
  }


}
