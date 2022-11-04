package com.banlap.llmusic.model;

import android.os.Handler;

import java.util.Date;

public class Message {
    public int messageId;
    public String messageType;
    public String messageImg;
    public String messageTitle;
    public String messageContent;
    public Date messageDate;
    public Handler handler;

    public int getMessageId() { return messageId; }

    public void setMessageId(int messageId) { this.messageId = messageId; }

    public String getMessageType() { return messageType; }

    public void setMessageType(String messageType) { this.messageType = messageType; }

    public String getMessageImg() { return messageImg; }

    public void setMessageImg(String messageImg) { this.messageImg = messageImg; }

    public String getMessageTitle() { return messageTitle; }

    public void setMessageTitle(String messageTitle) { this.messageTitle = messageTitle; }

    public String getMessageContent() { return messageContent; }

    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

    public Date getMessageDate() { return messageDate; }

    public void setMessageDate(Date messageDate) { this.messageDate = messageDate; }
}
