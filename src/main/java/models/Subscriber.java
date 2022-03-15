package models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class Subscriber {
    private String id;

    public abstract void receiveMessage(Message message);

    public String getId() {
        return id;
    }
}
