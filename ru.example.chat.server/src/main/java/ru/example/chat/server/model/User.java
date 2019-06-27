package ru.example.chat.server.model;

import java.io.Serializable;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class User implements Serializable {

    private static final long serialVersionUID = -3441552537843474850L;

    private String name;

    private String lastname;

    @Override
    public String toString() {
        return name + " " + lastname;
    }

}
