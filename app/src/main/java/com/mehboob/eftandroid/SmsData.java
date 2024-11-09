package com.mehboob.eftandroid;

import androidx.annotation.NonNull;

public class SmsData {
    private String sender;
    private String messageBody;
    private String receivedOnNumber;
    private String dateReceived;


    public SmsData(String sender, String messageBody, String receivedOnNumber, String dateReceived) {
        this.sender = sender;
        this.messageBody = messageBody;
        this.receivedOnNumber = receivedOnNumber;
        this.dateReceived = dateReceived;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public String getReceivedOnNumber() {
        return receivedOnNumber;
    }

    public void setReceivedOnNumber(String receivedOnNumber) {
        this.receivedOnNumber = receivedOnNumber;
    }

    public String getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(String dateReceived) {
        this.dateReceived = dateReceived;
    }

    @NonNull
    @Override
    public String toString() {
        return "SmsData{" +
                "sender='" + sender + '\'' +
                ", messageBody='" + messageBody + '\'' +
                ", receivedOnNumber='" + receivedOnNumber + '\'' +
                ", dateReceived='" + dateReceived + '\'' +
                '}';
    }
}
