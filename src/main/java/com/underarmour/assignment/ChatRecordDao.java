/**
 * 
 */
package com.underarmour.assignment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

/**
 * Strategy implementation that manages access to the sql database.
 * 
 * @author krishnanand (Kartik Krishnanand)
 */
@Repository
public class ChatRecordDao implements IChatRecordDao {
  
  private final static Logger LOGGER = LoggerFactory.getLogger(ChatRecordDao.class);
  
  private DataSource dataSource;
  
  private JdbcTemplate jdbcTemplate;
  
  @Autowired
  public ChatRecordDao(DataSource dataSource) {
    this.dataSource = dataSource;
    this.jdbcTemplate = new JdbcTemplate(this.dataSource);
  }
  
  // Visible for testing only.
  void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * Inserts the chat record into the database.
   */
  @Override
  public long insertChatRecord(final ChatRecord chatRecord) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Inserting chat record: " + chatRecord);
    }
    
    long chatId = SequenceGenerator.getInstance().next();
    chatRecord.setChatId(chatId);
    DateTime dateTime = Utils.addTimeToCurrent(chatRecord.getTimeout(), 60);
    chatRecord.setExpirationTimestamp(dateTime);
    final String timestamp = Utils.dateToString(dateTime);
    StringBuilder sb = new StringBuilder();
    sb.append("INSERT INTO Chats(chat_id, username, chat_text, expiration_date) ");
    sb.append("VALUES(?, ?, ?, ?)");
    jdbcTemplate.update(sb.toString(), new PreparedStatementSetter() {

      @Override
      public void setValues(PreparedStatement ps)
          throws SQLException, DataAccessException {
        // TODO Auto-generated method stub
        ps.setLong(1, chatRecord.getChatId());
        ps.setString(2,  chatRecord.getUsername());
        ps.setString(3,  chatRecord.getText());
        ps.setString(4, timestamp);
      }});
    return chatId;
  }


  /**
   * Fetches records by chat id.
   */
  @Override
  public ChatRecord getChatRecordById(final long id) {
    // TODO Auto-generated method stub
    String sql =  new StringBuilder().append(
        "SELECT username, chat_text, expiration_date FROM Chats WHERE chat_id = ?").
            toString();
    return this.jdbcTemplate.query(sql, new PreparedStatementSetter() {
      
      @Override
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setLong(1,  id);
      }
    }, new ResultSetExtractor<ChatRecord>() {

      @Override
      public ChatRecord extractData(ResultSet rs) throws SQLException, DataAccessException {
        ChatRecord chatRecord = new ChatRecord();
        while(rs.next()) {
          chatRecord.setUsername(rs.getString("username"));
          chatRecord.setText(rs.getString("chat_text"));
          chatRecord.setExpirationTimestamp(
              Utils.stringToDate(rs.getString("expiration_date")));
        }
        return chatRecord;
      }});
  }

  /* (non-Javadoc)
   * @see com.underarmour.assignment.IChatRecordDao#getChatRecordsByUsername(java.lang.String)
   */
  @Override
  public Set<ChatRecord> getChatRecordsByUsername(String username) {
    String sql = "SELECT chat_id, chat_text FROM CHATS where username = ? and expiration_date > ?";
    Set<ChatRecord> chatRecords = 
        this.jdbcTemplate.query(sql, new PreparedStatementSetter() {

      @Override
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, username);
        ps.setString(2,  Utils.currentTimeToDate());
      }
      
    }, new ResultSetExtractor<Set<ChatRecord>>() {

      @Override
      public Set<ChatRecord> extractData(ResultSet rs) 
          throws SQLException, DataAccessException {
        Set<ChatRecord> chatRecords = new LinkedHashSet<>();
        while (rs.next()) {
          ChatRecord record = new ChatRecord();
          record.setChatId(rs.getLong("chat_id"));
          record.setText(rs.getString("chat_text"));
          record.setExpirationTimestamp(
              Utils.stringToDate(rs.getString("expiration_date")));
        }
        return Collections.unmodifiableSet(chatRecords);
      }});
    return chatRecords;
  }

  @Override
  public int markChatRecordsAsExpired(final Set<Long> userIds) {
    // TODO Auto-generated method stub
    StringBuilder sql = new StringBuilder(
        "UPDATE ChatRecords SET expiration_date = ? WHERE chat_id IN (");
    for (int i = 0; i < userIds.size(); i++) {
      sql.append("?");
      if (i < (userIds.size() - 1)) {
        sql.append(", ");
      }
    }
    sql.append(")");
    return this.jdbcTemplate.update(sql.toString(), new PreparedStatementSetter() {

      @Override
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, Utils.currentTimeToDate());
        int counter = 2;
        for (long userId : userIds) {
          ps.setLong(counter ++, userId);
        }
      }
    });
  }

}
