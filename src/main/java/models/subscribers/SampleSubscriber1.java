package models.subscribers;

import models.Message;
import models.Subscriber;

import static helpers.Constants.ANSI_PURPLE;

public class SampleSubscriber1 extends Subscriber {
    public SampleSubscriber1(String id) {
        super(id);
    }

    @Override
    public void receiveMessage(Message message) {
        System.out.println(ANSI_PURPLE + "\nReceived message in SampleSubscriber1");
        System.out.println(message);
    }
}
