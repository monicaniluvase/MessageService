package services.impl;

import config.Configuration;
import exceptions.MessageQueueException;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.MsgQueue;
import models.Subscriber;
import services.MsgQueueService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static helpers.Constants.ANSI_GREEN;
import static helpers.Constants.ANSI_RED;

@Data
public class MsgQueueServiceImpl implements MsgQueueService {
    private final Map<String, MsgQueue> idToQueueMap = new HashMap<>();
    private Configuration configuration;

    public MsgQueueServiceImpl(Configuration config) {
        configuration = config;
    }

    @Override
    public void registerQueue(String id) throws MessageQueueException {
        try {
            if (idToQueueMap.containsKey(id)) {
                System.out.println(ANSI_RED + "Queue Already exists");
                return;
            }
            idToQueueMap.put(id, new MsgQueue(id,
                    configuration.getRetryQueueThreshold(),
                    configuration.getScheduleDelayMainQueueInSecs(),
                    configuration.getScheduleDelayRetryQueueInSecs()));
            System.out.println(ANSI_GREEN + "Message Queue registered");
        } catch (Exception e) {
            throw new MessageQueueException(e.getMessage());
        }
    }

    @Override
    public void addSubscriberToQueue(String id, Subscriber subscriber) throws MessageQueueException {
        try {
            if (!idToQueueMap.containsKey(id)) {
                System.out.println(ANSI_RED + "Queue does not exist");
                return;
            }
            if (subscriber == null) {
                System.out.println(ANSI_RED + "Subscriber cannot be null");
                return;
            }
            idToQueueMap.get(id).subscribe(subscriber);
        } catch (Exception e) {
            throw new MessageQueueException(e.getMessage());
        }
    }

    @Override
    public void removeSubscriberFromQueue(String id, Subscriber subscriber) throws MessageQueueException {
        try {
            if (!idToQueueMap.containsKey(id)) {
                System.out.println(ANSI_RED + "Queue does not exist");
                return;
            }
            idToQueueMap.get(id).unsubscribe(subscriber);
        } catch (Exception e) {
            throw new MessageQueueException(e.getMessage());
        }
    }

    @Override
    public MsgQueue getQueueFromId(String id) {
        return idToQueueMap.get(id);
    }

    @Override
    public int getMainQueueCountForId(String id) {
        if (!idToQueueMap.containsKey(id)) {
            System.out.println(ANSI_RED + "Queue does not exist");
            return 0;
        }
        return idToQueueMap.get(id).getQueue().getCount();
    }

    @Override
    public int getRetryQueueCountForId(String id) {
        if (!idToQueueMap.containsKey(id)) {
            System.out.println(ANSI_RED + "Queue does not exist");
            return 0;
        }
        return idToQueueMap.get(id).getRetryQueue().getCount();
    }

    @Override
    public Set<String> getAllRegisteredQueues() {
        return idToQueueMap.keySet();
    }
}
