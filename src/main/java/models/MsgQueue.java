package models;

import helpers.MessageUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import repos.Queue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static helpers.Constants.*;

@Getter
@AllArgsConstructor
public class MsgQueue {
    private int mainQueueSchedulerInterval;
    private int retryQueueSchedulerInterval;
    private ScheduledExecutorService queueScheduler = Executors.newScheduledThreadPool(1);
    private ScheduledExecutorService retryQueueScheduler = Executors.newScheduledThreadPool(1);
    private String id;
    private Queue queue = new Queue();
    private Queue retryQueue = new Queue();
    private Map<String, Subscriber> subscribers = new HashMap<>();
    private int retryThreshold;


    public MsgQueue(String id, int retryThreshold, int mainQueueSchedulerInterval, int retryQueueSchedulerInterval) {
        this.id = id;
        this.retryThreshold = retryThreshold;
        this.mainQueueSchedulerInterval = mainQueueSchedulerInterval;
        this.retryQueueSchedulerInterval = retryQueueSchedulerInterval;
        dequeueScheduler();
    }

    public void dequeueScheduler() {
        queueScheduler.scheduleWithFixedDelay(new Dequeue(queue, false), 0, mainQueueSchedulerInterval, TimeUnit.SECONDS);
        retryQueueScheduler.scheduleWithFixedDelay(new Dequeue(retryQueue, true), 0, retryQueueSchedulerInterval, TimeUnit.SECONDS);
    }

    private void dequeue(Queue queue, boolean isRetry) {
        Message message = queue.dequeue();
        if (message == null) {
            return;
        }

        try {
            if (isRetry) {
                System.out.println(ANSI_YELLOW + "\n------------[QueueId:" + id + "] Dequeuing retry queue------------\n");
            } else {
                System.out.println(ANSI_YELLOW + "\n------------[QueueId:" + id + "] Dequeuing main queue------------\n");
            }
            if (!validateMessage(message)) {
                handleProcessingExceptions(message);
            } else
                broadcastMessage(message);
        } catch (Exception e) {
            handleProcessingExceptions(message);
        }
    }

    private void broadcastMessage(Message message) {
        for (String subscriberId : subscribers.keySet()) {
            subscribers.get(subscriberId).receiveMessage(message);
        }
    }

    public boolean enqueue(Message message) {
        try {
            if (!validateMessage(message)) {
                handleProcessingExceptions(message);
                return false;
            }
            queue.enqueue(message);
        } catch (Exception e) {
            handleProcessingExceptions(message);
            return false;
        }
        return true;
    }

    private void handleProcessingExceptions(Message message) {
        if (message.getRetryCount() >= retryThreshold) {
            System.out.println(ANSI_RED + "\nMax retry reached, discarding message " + message);
        } else {
            message.incrementRetryCount();
            retryQueue.enqueue(message);
        }
    }

    private boolean validateMessage(Message message) {
        return MessageUtils.isValidJson(message.getData());
    }

    public void subscribe(Subscriber subscriber) {
        if (subscribers.containsKey(subscriber.getId())) {
            System.out.println(ANSI_RED + "Subscriber already Subscribed");
            return;
        }
        this.subscribers.put(subscriber.getId(), subscriber);
        System.out.println(ANSI_GREEN + "Subscriber successfully Subscribed");
    }

    public void unsubscribe(Subscriber subscriber) {
        if (!subscribers.containsKey(subscriber.getId())) {
            System.out.println(ANSI_RED + "Subscriber already unsubscribed");
            return;
        }
        this.subscribers.remove(subscriber.getId(), subscriber);
        System.out.println(ANSI_GREEN + "Subscriber successfully unsubscribed");
    }

    @AllArgsConstructor
    class Dequeue implements Runnable {
        Queue queue;
        boolean isRetry;

        @Override
        public void run() {
            try {
                dequeue(this.queue, this.isRetry);
            } catch (Throwable e) {
                System.out.println(ANSI_RED + "Exception while dequeuing: " + e.getMessage());
            }
        }
    }
}
