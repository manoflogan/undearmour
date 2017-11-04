package com.underarmour.assignment;

import java.util.Set;

/**
 * Represents the service that is responsible for managing chat related entities.
 * 
 * @author krishnanand (Kartik Krishnanand)
 */
public interface IChatService {
  
  /**
   * Inserts chat into the persistence tier.
   * 
   * @param chatRecord chat record to be persisted
   * @return returns the unique identifier
   */
  long insertChatRecord(ChatRecord chatRecord);
  
  /**
   * Fetches chat record by id
   * 
   * @param id id by which the chat record is fetched
   * @return chat record
   */
  ChatRecord getChatById(long id);
  
  /**
   * Fetches chat recored by username
   * 
   * @param username username
   * @return chat record instance
   */
  Set<ChatRecord> getChatRecordByUserName(String username);
  

}
