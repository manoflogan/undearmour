/**
 * 
 */
package com.underarmour.assignment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

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
  public long insertChatRecord(final ChatRecord chatRecord) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Inserting chat record: " + chatRecord);
    }
    
    long chatId = SequenceGenerator.getInstance().next();
    chatRecord.setChatId(chatId);
    Instant instant = Instant.now();
    
    Instant newInstant = instant.plusSeconds
       (chatRecord.getTimeout() != null ? chatRecord.getTimeout().intValue() : 60);
    ZonedDateTime zdt = newInstant.atZone(ZoneOffset.UTC);
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
        ps.setTimestamp(4, Timestamp.from(zdt.toInstant()));
      }});
    return chatId;
  }


  /**
   * Fetches records by chat id.
   */
  @Override
  public ChatRecord getChatRecordById(final long id) {
    // TODO Auto-generated method stub
    String sql =  new StringBuilder().
        append("SELECT chat_id, username, chat_text, expiration_date FROM Chats WHERE chat_id = ?").
        append(" UNION ALL ").
        append("SELECT chat_id, username, chat_text, expiration_date FROM Expired_Chats WHERE chat_id = ?").
        toString();
    return this.jdbcTemplate.query(sql, new PreparedStatementSetter() {
      
      @Override
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setLong(1,  id);
        ps.setLong(2,  id);
      }
    }, new ResultSetExtractor<ChatRecord>() {

      @Override
      public ChatRecord extractData(ResultSet rs) throws SQLException, DataAccessException {
        
        while(rs.next()) {
          ChatRecord chatRecord = new ChatRecord();
          chatRecord.setChatId(rs.getLong("chat_id"));
          chatRecord.setUsername(rs.getString("username"));
          chatRecord.setText(rs.getString("chat_text"));
          chatRecord.setExpirationTimestamp(
              rs.getTimestamp("expiration_date").toInstant());
          return chatRecord;
        }
        return null;
        
      }});
  }

  /* (non-Javadoc)
   * @see com.underarmour.assignment.IChatRecordDao#getChatRecordsByUsername(java.lang.String)
   */
  @Override
  public Set<ChatRecord> getChatRecordsByUsername(String username) {
    String sql =
        new StringBuilder("SELECT chat_id, chat_text, expiration_date FROM Chats ").
            append("where username = ? and expiration_date >= ?").toString();
    Set<ChatRecord> chatRecords = 
        this.jdbcTemplate.query(sql, new PreparedStatementSetter() {

      @Override
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, username);
        Instant instant = Instant.now();
        ZonedDateTime zt = instant.atZone(ZoneOffset.UTC);
        ps.setTimestamp(2,  Timestamp.from(zt.toInstant()));
      }
      
    }, new ResultSetExtractor<Set<ChatRecord>>() {

      @Override
      public Set<ChatRecord> extractData(ResultSet rs) 
          throws SQLException, DataAccessException {
        Set<ChatRecord> chatRecords = new LinkedHashSet<>();
        while (rs.next()) {
          ChatRecord record = new ChatRecord();
          record.setChatId(rs.getLong("chat_id"));
          record.setUsername(username);
          record.setText(rs.getString("chat_text"));
          record.setExpirationTimestamp(rs.getTimestamp("expiration_date").toInstant());
          chatRecords.add(record);
        }
        return Collections.unmodifiableSet(chatRecords);
      }});
    return Collections.unmodifiableSet(chatRecords);
  }

  /**
   * Marks the chat records as expired by resetting the expiration time stamp
   * for a set of user ids.
   */
  @Override
  public int deleteByIds(final Set<Long> userIds) {
    // TODO Auto-generated method stub
    StringBuilder sql = new StringBuilder(
        "DELETE FROM Chats WHERE chat_id IN (");
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
        int counter = 1;
        for (long userId : userIds) {
          ps.setLong(counter ++, userId);
        }
      }
    });
  }

  @Override
  public void insertDataInExpiredTable(Set<ChatRecord> chatRecords) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Trying to inserting " + chatRecords.size() + " record(s) into the expired chat table.");
    }
    StringBuilder sb = new StringBuilder();
    sb.append("INSERT INTO Expired_Chats(chat_id, username, chat_text, expiration_date) ");
    sb.append("VALUES(?, ?, ?, ?)");
    
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Triggering " + chatRecords.size() + " batch insert(s) to expired chat table.");
    }
    List<Object[]> arguments = new ArrayList<>();
    for (ChatRecord chatRecord : chatRecords) {
      Object[] args = new Object[] {chatRecord.getChatId(), chatRecord.getUsername(),
          chatRecord.getText(), Timestamp.from(chatRecord.getExpirationTimestamp())};
      arguments.add(args);
    }
    this.jdbcTemplate.batchUpdate(sb.toString(), arguments);
  }

}
