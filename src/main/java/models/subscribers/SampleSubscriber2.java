package models.subscribers;

import models.Message;
import models.Subscriber;

import static helpers.Constants.ANSI_PURPLE;

public class SampleSubscriber2 extends Subscriber {
    public SampleSubscriber2(String id) {
        super(id);
    }

    @Override
    public void receiveMessage(Message message) {
        System.out.println(ANSI_PURPLE + "\nReceived message in SampleSubscriber2");
        System.out.println(message);
    }
}
