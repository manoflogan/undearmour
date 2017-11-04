/**
 * 
 */
package com.underarmour.assignment;

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
      LOGGER.debug("Inserting chat record.");
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
  public ChatRecord getChatById(long id) {
    // TODO Auto-generated method stub
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Fetching chat record by " + id);
    }
    return this.chatRecordDao.getChatRecordById(id);
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
  public Set<ChatRecord> getChatRecordByUserName(String username) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Fetching chat record by " + username);
    }
    Set<ChatRecord> chatRecords = this.getChatRecordByUserName(username);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Fetched chat records " + chatRecords);
    }
    if (!chatRecords.isEmpty()) {
      Set<Long> userIds = new LinkedHashSet<>();
      for (ChatRecord chatRecord : chatRecords) {
        userIds.add(chatRecord.getChatId());
      }
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Marking the chat records as expired. " + userIds);
      }
      this.chatRecordDao.markChatRecordsAsExpired(userIds);
    }
    return chatRecords;
  }

}
