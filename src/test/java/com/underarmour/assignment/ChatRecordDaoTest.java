/**
 * 
 */
package com.underarmour.assignment;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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


}
