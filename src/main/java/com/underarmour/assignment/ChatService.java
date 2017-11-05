/**
 * 
 */
package com.underarmour.assignment;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Strategy implementation that is responsible for managing chat service.
 * 
 * @author krishnanand (Kartik Krishnanand)
 */
@Component
public class ChatService implements IChatService {
  
  private final static Logger LOGGER = LoggerFactory.getLogger(ChatService.class);
  
  private IChatRecordDao chatRecordDao;
  
  @Autowired
  public ChatService(IChatRecordDao dao) {
    this.chatRecordDao = dao;
  }


  @Transactional
  @Override
  public long insertChatRecord(ChatRecord chatRecord) {
    // TODO Auto-generated method stub
    if (LOGGER.isDebugEnabled())  {
      LOGGER.debug("Inserting chat record. " + chatRecord);
    }
    return this.chatRecordDao.insertChatRecord(chatRecord);
  }

  /**
   * Fetches chat by id.
   * 
   * @param id unique identifier
   */
  @Override
  @Transactional
  public ChatHistory getChatById(long id) {
    // TODO Auto-generated method stub
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Fetching chat record by " + id);
    }
    ChatRecord record = this.chatRecordDao.getChatRecordById(id);
    if (record == null) {
      return null;
    }
    ChatHistory chatHistory = new ChatHistory.Builder().
        withChatId(record.getChatId()).withUsername(record.getUsername()).
        withText(record.getText()).
        withExpirationDate(Utils.dateToString(record.getExpirationTimestamp())).build();
    return chatHistory;
    
  }

 /**
  * Performs two operations.
  * <ul>
  *   <li>Fetches all unexpired chats chat record by username.</li>
  *   <li>Updates the expired status status to expire
  * </ul>
  */
  @Transactional
  @Override
  public Set<ChatHistory> getChatRecordByUserName(String username) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Fetching chat record by " + username);
    }
    Set<ChatRecord> chatRecords = this.chatRecordDao.getChatRecordsByUsername(username);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Fetched chat records " + chatRecords);
    }
    Set<ChatHistory> chatHistories = new LinkedHashSet<>();
    if (!chatRecords.isEmpty()) {
      Set<Long> userIds = new LinkedHashSet<>();
      for (ChatRecord chatRecord : chatRecords) {
        if (chatRecord == null) {
          continue;
        }
        userIds.add(chatRecord.getChatId());
        ChatHistory chatHistory = new ChatHistory.Builder().
            withChatId(chatRecord.getChatId()).withUsername(chatRecord.getUsername()).
            withText(chatRecord.getText()).withExpirationDate(
                Utils.dateToString(chatRecord.getExpirationTimestamp())).build();
        chatHistories.add(chatHistory);
      }
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Marking the chat records as expired. " + userIds);
      }
      this.chatRecordDao.markChatRecordsAsExpired(userIds);
    }
    return Collections.unmodifiableSet(chatHistories);
  }

}
