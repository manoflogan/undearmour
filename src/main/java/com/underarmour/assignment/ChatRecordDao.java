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
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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

  private static final String TIMESTAMP_FORMAT = "YYYY-MM-DD HH:MM:SS";
  
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
    DateTime dateTime = DateTime.now().withZone(DateTimeZone.UTC);
    DateTime expirationTime = dateTime.plusSeconds(
        chatRecord.getTimeout() != null ? chatRecord.getTimeout().intValue(): 60);
    final String timestamp = expirationTime.toString(TIMESTAMP_FORMAT);
    long chatId = SequenceGenerator.getInstance().next();
    chatRecord.setChatId(chatId);
    chatRecord.setExpirationTimestamp(expirationTime);
    StringBuilder sb = new StringBuilder();
    sb.append("INSERT INTO CHATS(chat_id, username, chat_text, expiration_date) ");
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
          DateTimeFormatter format = DateTimeFormat.forPattern(TIMESTAMP_FORMAT);
          DateTime dateTime = format.parseDateTime(rs.getString("expiration_date"));
          chatRecord.setExpirationTimestamp(dateTime);
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
        DateTime dateTime = DateTime.now().withZone(DateTimeZone.UTC);
        final String timestamp = dateTime.toString(TIMESTAMP_FORMAT);
        ps.setString(2,  timestamp);
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
          DateTimeFormatter format = DateTimeFormat.forPattern(TIMESTAMP_FORMAT);
          DateTime dateTime = format.parseDateTime(rs.getString("expiration_date"));
          record.setExpirationTimestamp(dateTime);
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
        
        DateTime dt = DateTime.now().withZone(DateTimeZone.UTC);
        final String timestamp = dt.toString(TIMESTAMP_FORMAT);
        ps.setString(1, timestamp);
        int counter = 2;
        for (long userId : userIds) {
          ps.setLong(counter ++, userId);
        }
      }
    });
  }

}
