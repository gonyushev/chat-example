package ru.example.chat.server.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Message implements Serializable {

    private static final long serialVersionUID = -2140547346169750275L;

    private User author;

    private User receiver;

    private String text;

    public User getAuthor() {
        return author;
    }

    public User getReceiver() {
        return receiver;
    }

    public String getText() {
        return text;
    }

}
