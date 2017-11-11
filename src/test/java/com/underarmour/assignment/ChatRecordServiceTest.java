/**
 * 
 */
package com.underarmour.assignment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test for {@link ChatRecordService}.
 * 
 * @author krishnanand (Kartik Krishnanand)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes= {ChatService.class, ChatRecordDao.class, DatabaseCredentials.class})
@Sql(executionPhase=ExecutionPhase.AFTER_TEST_METHOD,scripts="classpath:/cleanup.sql")
public class ChatRecordServiceTest {
  
  @Autowired
  private ChatService chatService;
  
  @Autowired
  private DataSource dataSource;
  
  private JdbcTemplate jdbcTemplate;
  
  @Before
  public void setUp() throws Exception {
    this.jdbcTemplate = new JdbcTemplate(this.dataSource);
  }
  
  @Test
  public void testInsertChatRecord() throws Exception {
    ChatRecord record = new ChatRecord();
    record.setUsername("test");
    record.setText("text");
    record.setTimeout(1500);
    int count = jdbcTemplate.queryForObject(
        "SELECT count(*) from Chats", int.class);
    
    Assert.assertEquals(0, count);
    this.chatService.insertChatRecord(record);
    count = jdbcTemplate.queryForObject(
        "SELECT count(*) from Chats", int.class);
    Assert.assertEquals(1, count);
  }
  
  @Test
  public void testGetChatById() throws Exception {
    ChatRecord record = new ChatRecord();
    record.setUsername("test");
    record.setText("text");
    record.setTimeout(1500);
    int count = jdbcTemplate.queryForObject(
        "SELECT count(*) from Chats", int.class);
    
    Assert.assertEquals(0, count);
    long chatId = this.chatService.insertChatRecord(record);
    record.setChatId(chatId);
    count = jdbcTemplate.queryForObject(
        "SELECT count(*) from Chats", int.class);
    Assert.assertEquals(1, count);
    
    ChatHistory chatHistory = this.chatService.getChatById(chatId);
    Assert.assertEquals(record.getUsername(), chatHistory.getUsername());
    Assert.assertEquals(record.getText(), chatHistory.getText());
    Assert.assertEquals(record.getChatId(), chatHistory.getChatId());
  }
  
  @Test
  public void testGetChatById_Expired() throws Exception {
    ChatRecord record = new ChatRecord();
    record.setUsername("test");
    record.setText("text");
    record.setTimeout(1500);
    int count = jdbcTemplate.queryForObject(
        "SELECT count(*) from Chats", int.class);
    Assert.assertEquals(0, count);

    long chatId = this.chatService.insertChatRecord(record);
    record.setChatId(chatId);
    count = jdbcTemplate.queryForObject(
        "SELECT count(*) from Chats", int.class);
    Assert.assertEquals(1, count);
    
    Set<ChatHistory> chatHistories = this.chatService.getChatRecordByUserName(record.getUsername());
    Assert.assertEquals(1, chatHistories.size());
    ChatHistory chatHistory = chatHistories.iterator().next(); 
    Assert.assertEquals(record.getUsername(), chatHistory.getUsername());
    Assert.assertEquals(record.getText(), chatHistory.getText());
    Assert.assertEquals(record.getChatId(), chatHistory.getChatId());
    
     count = jdbcTemplate.queryForObject(
        "SELECT count(*) from Chats", int.class);
     Assert.assertEquals(0, count);
     
     ChatHistory fetchedChatHistory = this.chatService.getChatById(chatId);
     Assert.assertEquals(record.getUsername(), fetchedChatHistory.getUsername());
     Assert.assertEquals(record.getText(), fetchedChatHistory.getText());
     Assert.assertEquals(record.getChatId(), fetchedChatHistory.getChatId());

  }
  
  @Test
  public void testGetChatRecordByUserName() throws Exception {
    ChatRecord record = new ChatRecord();
    record.setUsername("test");
    record.setText("text");
    record.setTimeout(1500);
    int count = jdbcTemplate.queryForObject(
        "SELECT count(*) from Chats", int.class);
    
    Assert.assertEquals(0, count);
    long chatId = this.chatService.insertChatRecord(record);
    record.setChatId(chatId);
    count = jdbcTemplate.queryForObject(
        "SELECT count(*) from Chats", int.class);
    Assert.assertEquals(1, count);
    
    Set<ChatHistory> chatHistories = this.chatService.getChatRecordByUserName(record.getUsername());
    Assert.assertEquals(1,  chatHistories.size());
    ChatHistory chatHistory = chatHistories.iterator().next();
    Assert.assertEquals(record.getUsername(), chatHistory.getUsername());
    Assert.assertEquals(record.getText(), chatHistory.getText());
    Assert.assertEquals(record.getChatId(), chatHistory.getChatId());
    Assert.assertEquals(1, count);
    
    Instant instant = Instant.now();
    instant.atOffset(ZoneOffset.UTC);
    
    count = jdbcTemplate.queryForObject(
        "SELECT count(*) from Chats where expiration_date > ?",
        new Object[] {Timestamp.from(instant)}, int.class);
    Assert.assertEquals(0, count);
    
    Set<ChatRecord> actual = jdbcTemplate.query(
        "SELECT * from Expired_Chats",
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
    Assert.assertEquals(1, actual.size());
    ChatRecord expired = actual.iterator().next();
    Assert.assertEquals(expired.getUsername(), chatHistory.getUsername());
    Assert.assertEquals(expired.getText(), chatHistory.getText());
    Assert.assertEquals(expired.getChatId(), chatHistory.getChatId());
    
  }
  
  
}
