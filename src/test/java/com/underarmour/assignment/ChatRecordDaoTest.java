/**
 * 
 */
package com.underarmour.assignment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author krishnanand (Kartik Krishnanand)
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes= {ChatRecordDao.class, DatabaseCredentials.class})
@Sql(executionPhase=ExecutionPhase.BEFORE_TEST_METHOD,scripts="classpath:/schema.sql")
@Sql(executionPhase=ExecutionPhase.AFTER_TEST_METHOD,scripts="classpath:/cleanup.sql")
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
  public void testInsertDataInExpiredTable() throws Exception {
    ChatRecord expected = new ChatRecord();
    expected.setUsername("test");
    expected.setText("text");
    expected.setChatId(1L);
    Instant newInstant = Instant.now();
    ZonedDateTime dt = newInstant.atZone(ZoneOffset.UTC);
    expected.setExpirationTimestamp(dt.toInstant());
    Set<ChatRecord> expiredInserted = new LinkedHashSet<>();
    expiredInserted.add(expected);
   
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
    int count = jdbcTemplate.queryForObject(
        "SELECT count(*) from Expired_Chats", int.class);
    Assert.assertEquals(0, count);
    this.chatRecordDao.insertDataInExpiredTable(expiredInserted);
   
    Set<ChatRecord> actual = jdbcTemplate.query(
        "SELECT * from Expired_Chats where expiration_date <= ?",
        new Object[] {Timestamp.from(dt.toInstant())},
        new ResultSetExtractor<Set<ChatRecord>>() {

          @Override
          public Set<ChatRecord> extractData(ResultSet rs) throws SQLException, DataAccessException {
            // TODO Auto-generated method stub
            Set<ChatRecord> records = new LinkedHashSet<>();
            while(rs.next()) {
              ChatRecord chatRecord = new ChatRecord();
              chatRecord.setChatId(rs.getLong("chat_id"));
              chatRecord.setExpirationTimestamp(rs.getTimestamp("expiration_date").toInstant());
              chatRecord.setUsername(rs.getString("username"));
              chatRecord.setText(rs.getString("chat_text"));
              records.add(chatRecord);
            }
            return records;
          }
        });
    Assert.assertEquals(expiredInserted, actual);
  }
  
  @Test
  public void testDeleteByIds() throws Exception {
    ChatRecord expected = new ChatRecord();
    expected.setUsername("test");
    expected.setText("text");
    expected.setTimeout(1500);
    long chatId = this.chatRecordDao.insertChatRecord(expected);
    JdbcTemplate jdbcTemplate = new JdbcTemplate(this.dataSource);
    int count = jdbcTemplate.queryForObject(
        "SELECT count(*) from Chats", int.class);
    Assert.assertEquals(1, count);
    Set<Long> userIds = new LinkedHashSet<>();
    userIds.add(chatId);
    int deletedRecordsNumber = this.chatRecordDao.deleteByIds(userIds);
    Assert.assertEquals(1, deletedRecordsNumber);
    count = jdbcTemplate.queryForObject(
        "SELECT count(*) from Chats", int.class);
    Assert.assertEquals(0, count);
  }
}
