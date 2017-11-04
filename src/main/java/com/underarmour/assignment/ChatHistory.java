/**
 * 
 */
package com.underarmour.assignment;

/**
 * An instance of the class represents a value object whose state represents the output.
 * 
 * @author krishnanand (Kartik Krishnanand)
 */
public class ChatHistory {
  
  private final String username;
  
  private final Long chatId;
  
  private final String expirationDate;
  
  private final String text;
  
  private ChatHistory(String username, Long chatId, String expirationDate, String text) {
    this.username = username;
    this.chatId = chatId;
    this.expirationDate = expirationDate;
    this.text = text;
  }

  public String getUsername() {
    return username;
  }

  public Long getChatId() {
    return chatId;
  }

  public String getExpirationDate() {
    return expirationDate;
  }

  public String getText() {
    return text;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((chatId == null) ? 0 : chatId.hashCode());
    result = prime * result + ((expirationDate == null) ? 0 : expirationDate.hashCode());
    result = prime * result + ((username == null) ? 0 : username.hashCode());
    result = prime * result + (this.text == null ? 0 : this.text.hashCode());
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
    if (!ChatHistory.class.isAssignableFrom(obj.getClass())) {
      return false;
    }
    ChatHistory other = (ChatHistory) obj;
    return this.chatId != null && this.chatId.equals(other.getChatId()) &&
        this.expirationDate != null &&
        this.expirationDate.equals(other.getExpirationDate()) &&
        this.username != null && this.username.equals(other.getUsername()) &&
        this.text != null && this.text.equals(other.getText());
  }
  

  @Override
  public String toString() {
    return new StringBuilder(this.getClass().getSimpleName()).append("[username=").
        append(username).append(", chatId =").append(chatId).
        append(", expirationDate=").append(expirationDate).append("]").toString();
  }
  
  /**
   * Helper builder pattern.
   * 
   * @author krishnanand (Kartik Krishnanand)
   */
  public static class Builder {
    
    private String username;
    
    private String expirationDate;
    
    private Long chatId;
    
    private String text;
    
    public Builder withUsername(String username) {
      this.username = username;
      return this;
    }
    
    public Builder withExpirationDate(String expirationDate) {
      this.expirationDate = expirationDate;
      return this;
    }
    
    public Builder withChatId(Long chatId) {
      this.chatId = chatId;
      return this;
    }
    
    public Builder withText(String text) {
      this.text = text;
      return this;
    }
    
    public ChatHistory build() {
      return new ChatHistory(this.username, this.chatId, this.expirationDate, this.text);
    }
  
  }
}
