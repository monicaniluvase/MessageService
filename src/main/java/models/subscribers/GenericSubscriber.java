package models.subscribers;

import models.Message;
import models.Subscriber;

import static helpers.Constants.ANSI_PURPLE;

public class GenericSubscriber extends Subscriber {
    public GenericSubscriber(String id) {
        super(id);
    }

    @Override
    public void receiveMessage(Message message) {
        System.out.println(ANSI_PURPLE + "\nReceived message in GenericSubscriber");
        System.out.println(message);
    }
}
