package ru.pussy_penetrator.pchat.request;

import java.util.Date;

import ru.pussy_penetrator.pchat.utils.DateUtils;

public class Message {
    private int id;
    private long time;
    private String formattedTime;
    private String text;
    private String sender;

    public Message(int id, long time, String text, String sender) {
        this.id = id;
        this.time = time;
        formattedTime = DateUtils.formatDate(new Date(time));
        this.text = text;
        this.sender = sender;
    }

    public int getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public String getFormattedTime() {
        return formattedTime;
    }

    public String getText() {
        return text;
    }

    public String getSender() {
        return sender;
    }
}
