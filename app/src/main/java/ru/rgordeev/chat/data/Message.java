package ru.rgordeev.chat.data;

import com.google.gson.annotations.SerializedName;

public class Message {

    @SerializedName("from")
    private String from;

    @SerializedName("to")
    private String to;

    @SerializedName("message")
    private String message;

    private boolean curUser;

    public Message(String from, String to, String message, boolean curUser) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.curUser = curUser;
    }

    public boolean isBelongsToCurrentUser() {
        return curUser;
    }

    public void checkIsBelongsToCurrentUser(String login) {
        curUser = login.equals(from);
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
