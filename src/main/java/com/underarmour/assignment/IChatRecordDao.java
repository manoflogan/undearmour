package com.underarmour.assignment;

import java.util.Set;

/**
 * Strategy implementation that represents the data access layer.
 * 
 * @author krishnanand (Kartik Krishnanand)
 */
public interface IChatRecordDao {
  
  /**
   * Inserts chat record to the database.
   * 
   * @param chatRecord chat record to be saved to database
   * @return 
   */
  long insertChatRecord(ChatRecord chatRecord);
  
  /**
   * Fetches records by the id
   * 
   * @param id identifier
   * @return instance if found; {@code null} if not
   */
  ChatRecord getChatRecordById(long id);
  
  /**
   * Fetches chat records by username.
   * 
   * @param username
   * @return set of chat records
   */
  Set<ChatRecord> getChatRecordsByUsername(String username);
  
  /**
   * Updates the records as expired for the specific ids.
   * 
   * @param userIds user ids
   * @return number of affected rows
   */
  int markChatRecordsAsExpired(Set<Long> userIds);

}
