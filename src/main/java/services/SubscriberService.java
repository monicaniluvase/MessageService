package services;

import enums.SubscriberType;
import exceptions.SubscriberException;
import models.Subscriber;

public interface SubscriberService {
    boolean registerSubscriber(String id, SubscriberType subscriberType) throws SubscriberException;

    Subscriber getSubscriberFromId(String id);

    void deleteSubscriber(String id);
}
