package services.impl;

import exceptions.PublisherException;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.Message;
import models.MsgQueue;
import models.Publisher;
import services.PublisherService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static helpers.Constants.ANSI_GREEN;
import static helpers.Constants.ANSI_RED;

@NoArgsConstructor
@Data
public class PublisherServiceImpl implements PublisherService {
    private final Map<String, Publisher> publisherMap = new HashMap<>();
    private final Map<String, String> registeredQueueMap = new HashMap<>();

    @Override
    public void registerPublisher(String id, MsgQueue msgQueue) throws PublisherException {
        try {
            if (publisherMap.containsKey(id)) {
                System.out.println(ANSI_RED + "Publisher already registered");
            } else if (registeredQueueMap.containsKey(msgQueue.getId())) {
                System.out.println(ANSI_RED + "Queue is already mapped to another publisher:" + registeredQueueMap.get(msgQueue.getId()));
            } else {
                publisherMap.put(id, new Publisher(id, msgQueue));
                registeredQueueMap.put(msgQueue.getId(), id);
                System.out.println(ANSI_GREEN + "Publisher Registered.");
            }
        } catch (Exception e) {
            throw new PublisherException(e.getMessage());
        }
    }

    @Override
    public void publishMessage(Message message, String publisherId) throws PublisherException {
        try {
            if (!publisherMap.containsKey(publisherId)) {
                System.out.println(ANSI_RED + "Publisher not registered");
                return;
            }
            MsgQueue queue = publisherMap.get(publisherId).getQueue();
            if (queue == null) {
                System.out.println(ANSI_RED + "Publisher does not have a queue linked. Please link a queue.");
                return;
            }
            if (queue.enqueue(message)) {
                System.out.println(ANSI_GREEN + "Message successfully queued");
            } else {
                System.out.println(ANSI_RED + "Message Queue Failed. Moved to retry queue.");
            }
        } catch (Exception e) {
            throw new PublisherException(e.getMessage());
        }
    }

    @Override
    public Set<String> getAllPublisherIds() {
        return publisherMap.keySet();
    }

    @Override
    public Map<String, String> getPublisherToQueueMapping() {
        Map<String, String> result = new HashMap<>();
        for (Publisher publisher : publisherMap.values()) {
            result.put(publisher.getId(), publisher.getQueue().getId());
        }

        return result;
    }
}
