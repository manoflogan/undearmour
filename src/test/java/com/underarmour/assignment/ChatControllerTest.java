/**
 * 
 */
package com.underarmour.assignment;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

/**
 * Unit test for {@link ChatController}.
 * 
 * @author krishnanand (Kartik Krishnanand)
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes= { ChatController.class, ChatService.class, ChatRecordDao.class, DatabaseCredentials.class})
//@Sql(executionPhase=ExecutionPhase.BEFORE_TEST_METHOD,scripts="classpath:/cleanup.sql")
@ContextConfiguration(classes = {TestContext.class, WebApplicationContext.class})
public class ChatControllerTest {
  
  
  @Autowired
  private ChatController controller;
  
  @MockBean
  private IChatService chatService;
  
  final String BASE_URL = "http://localhost:8080";
  
  @Test
  public void testPostChat() throws Exception {
     long chatId = 1000L;
     ChatRecord chatRecord = new ChatRecord();
     chatRecord.setUsername("username");
     chatRecord.setText("text");
     chatRecord.setTimeout(18000);
     Mockito.when(this.chatService.insertChatRecord(chatRecord)).thenReturn(chatId);
     ResponseEntity<Map<String, Object>> responseEntity = this.controller.postChat(chatRecord);
     Assert.assertEquals(responseEntity.getStatusCodeValue(), 201);
     Map<String, Object> map = new LinkedHashMap<>();
     map.put("id", chatId);
     Assert.assertEquals(responseEntity.getBody(), map);
     Mockito.verify(this.chatService).insertChatRecord(chatRecord);
     Mockito.verifyNoMoreInteractions(this.chatService);
  }
  
  @Test
  public void testGetChatHistoryById() throws Exception {
    ChatHistory chatRecord =
        new ChatHistory.Builder().withText("text").withUsername("username").withChatId(1000L).
        withExpirationDate("2015-08-12 06:22:52").build();
    Mockito.when(this.chatService.getChatById(1000L)).thenReturn(chatRecord);
    Map<String, Object> map = this.controller.fetchChatsById(1000L);
    Assert.assertEquals(map.get("username"), chatRecord.getUsername());
    Assert.assertEquals(map.get("text"), chatRecord.getText());
    Assert.assertEquals(map.get("expiration_date"), chatRecord.getExpirationDate());
    Mockito.verify(this.chatService).getChatById(1000L);
    Mockito.verifyNoMoreInteractions(this.chatService);
  }
  
  @Test
  public void testGetChatHistoryById_NullData() throws Exception {
    Mockito.when(this.chatService.getChatById(1000L)).thenReturn(null);
    Assert.assertEquals(Collections.emptyMap(), this.controller.fetchChatsById(1000L));
    Mockito.verify(this.chatService).getChatById(1000L);
    Mockito.verifyNoMoreInteractions(this.chatService);
  }
  
  @Test
  public void testGetChatHistoryByUsername() throws Exception {
    ChatHistory chatRecord =
        new ChatHistory.Builder().withText("text").withUsername("username").withChatId(1000L).
        withExpirationDate("2015-08-12 06:22:52").build();
    Set<ChatHistory> chatHistories = new LinkedHashSet<>();
    chatHistories.add(chatRecord);
    Mockito.when(this.chatService.getChatRecordByUserName("username")).
        thenReturn(chatHistories);
    Set<Map<String, Object>> actual = this.controller.fetchChatsByUsername("username");
    
    Set<Map<String, Object>> set = new LinkedHashSet<>();
    Map<String, Object> expected = new LinkedHashMap<>();
    expected.put("id", chatRecord.getChatId());
    expected.put("text", chatRecord.getText());
    set.add(expected);
    Assert.assertEquals(set, actual);
    Mockito.verify(this.chatService).getChatRecordByUserName("username");
    Mockito.verifyNoMoreInteractions(this.chatService);
  }

}
