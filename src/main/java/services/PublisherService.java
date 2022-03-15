package services;

import exceptions.PublisherException;
import models.Message;
import models.MsgQueue;

import java.util.Map;
import java.util.Set;

public interface PublisherService {

    void registerPublisher(String id, MsgQueue msgQueue) throws PublisherException;

    void publishMessage(Message message, String publisherId) throws PublisherException;

    Set<String> getAllPublisherIds();

    Map<String, String> getPublisherToQueueMapping();
}
