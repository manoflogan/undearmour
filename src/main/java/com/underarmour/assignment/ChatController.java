package com.underarmour.assignment;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is a single point of entry for all chat related issues.
 * 
 * @author krishnanand (Kartik Krishnanand)
 */
@RestController("/underarmour")
public class ChatController {
  
  private final IChatService chatService;
  
  private final static Logger LOGGER = LoggerFactory.getLogger(ChatController.class);
  
  @Autowired
  public ChatController(IChatService chatService) {
    this.chatService = chatService;
  }
  
  /**
   * Inserts and generates a new chat id
   * @param chatRecord
   * @return inserted record
   */
  @RequestMapping("/chats")
  @PostMapping
  public ResponseEntity<Map<String, Object>> postChat(@RequestBody ChatRecord chatRecord) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Inserting the chat record " + chatRecord);
    }
    long chatId = this.chatService.insertChatRecord(chatRecord);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Generated the chat record id: " + chatId);
    }
    Map<String, Object> response = new LinkedHashMap<>();
    response.put("id", chatId);
    return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
  }
  
  @RequestMapping("/chats/id/{id}")
  @GetMapping
  public Map<String, Object> fetchChatsById(@PathVariable("id") final long id) {
    ChatHistory chatRecord = this.chatService.getChatById(id);
    if (chatRecord == null) {
      return Collections.emptyMap();
    }
    Map<String, Object> chatRecordMap = new LinkedHashMap<>();
    chatRecordMap.put("username", chatRecord.getUsername());
    chatRecordMap.put("text", chatRecord.getText());
    chatRecordMap.put("expiration_date", chatRecord.getExpirationDate());
    return chatRecordMap;
  }
  
  @RequestMapping("/chats/username/{username}")
  @GetMapping
  public Set<Map<String, Object>> fetchChatsByUsername(
      @PathVariable("username") final String username) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Fetching chats by username : " + username);
    }
    Set<ChatHistory> chatRecords = this.chatService.getChatRecordByUserName(username);
    Set<Map<String, Object>> records = new LinkedHashSet<>();
    for (ChatHistory chatRecord: chatRecords) {
      Map<String, Object> map = new LinkedHashMap<>();
      map.put("id", chatRecord.getChatId());
      map.put("text", chatRecord.getText());
      records.add(map);
    }
    return records;
  }

}
