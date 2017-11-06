package com.underarmour.assignment;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * An instance of this class represents a single chat record.
 * 
 * @author krishnanand (Kartik Krishnanand)
 */
@JsonInclude(Include.NON_NULL)
public class ChatRecord {
  
  private String username;
  
  private String text;
  
  private Long chatId;
  
  private Integer timeout;
  
  private Instant expirationTimestamp;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Long getChatId() {
    return chatId;
  }

  public void setChatId(Long chatId) {
    this.chatId = chatId;
  }

  public Instant getExpirationTimestamp() {
    return expirationTimestamp;
  }

  public void setExpirationTimestamp(Instant expirationTimestamp) {
    this.expirationTimestamp = expirationTimestamp;
  }

  public Integer getTimeout() {
    return timeout;
  }

  public void setTimeout(Integer timeout) {
    this.timeout = timeout;
  }

  @Override
  public String toString() {
    return new StringBuilder().append("ChatRecord ").append(
        "[username=").append(this.username).append(", text=").append(this.text).
        append(", expiration time stamp= ").append(this.expirationTimestamp).
        append(", time out = ").append(this.timeout).
        append(", chatId=" ).append(this.chatId).append("]").toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (chatId != null ? chatId.hashCode() : 0);
    result = prime * result + ((text == null) ? 0 : text.hashCode());
    result = prime * result + ((username == null) ? 0 : username.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!ChatRecord.class.isAssignableFrom(obj.getClass())) {
      return false;
    }
    ChatRecord other = (ChatRecord) obj;
    return (this.chatId == other.chatId ||
        (this.chatId != null && this.chatId.equals(other.chatId))) && 
        (this.text == other.text ||
        (this.text != null && this.text.equals(other.text))) &&
        (this.username == other.username ||
        (this.username != null && this.username.equals(other.username)));
  }
  
  

}
