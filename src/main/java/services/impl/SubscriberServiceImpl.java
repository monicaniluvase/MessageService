package services.impl;

import enums.SubscriberType;
import exceptions.SubscriberException;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.Subscriber;
import models.subscribers.GenericSubscriber;
import models.subscribers.SampleSubscriber1;
import models.subscribers.SampleSubscriber2;
import services.SubscriberService;

import java.util.HashMap;
import java.util.Map;

import static helpers.Constants.ANSI_GREEN;
import static helpers.Constants.ANSI_RED;

@NoArgsConstructor
@Data
public class SubscriberServiceImpl implements SubscriberService {
    private final Map<String, Subscriber> idToSubscriberMap = new HashMap<>();

    @Override
    public boolean registerSubscriber(String id, SubscriberType type) throws SubscriberException {
        try {
            if (idToSubscriberMap.containsKey(id)) {
                System.out.println(ANSI_RED + "Subscriber already registered.");
                return false;
            }
            Subscriber subscriber = getSubscriberFromType(id, type);
            idToSubscriberMap.put(id, subscriber);
            System.out.println(ANSI_GREEN + "Subscriber successfully registered.");
            return true;
        } catch (Exception e) {
            throw new SubscriberException(e.getMessage());
        }
    }

    private Subscriber getSubscriberFromType(String id, SubscriberType type) {
        switch (type) {
            case type1:
                return new SampleSubscriber1(id);
            case type2:
                return new SampleSubscriber2(id);
            default:
                return new GenericSubscriber(id);
        }
    }

    @Override
    public Subscriber getSubscriberFromId(String id) {
        return idToSubscriberMap.get(id);
    }

    @Override
    public void deleteSubscriber(String id) {
        idToSubscriberMap.remove(id);
    }
}
