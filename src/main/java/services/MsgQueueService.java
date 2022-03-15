package services;

import exceptions.MessageQueueException;
import models.MsgQueue;
import models.Subscriber;

import java.util.Set;

public interface MsgQueueService {

    void registerQueue(String id) throws MessageQueueException;

    void addSubscriberToQueue(String id, Subscriber subscriber) throws MessageQueueException;

    void removeSubscriberFromQueue(String id, Subscriber subscriber) throws MessageQueueException;

    MsgQueue getQueueFromId(String id);

    int getMainQueueCountForId(String id);

    int getRetryQueueCountForId(String id);

    Set<String> getAllRegisteredQueues();
}
