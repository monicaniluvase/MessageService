import enums.SubscriberType;
import exceptions.MessageQueueException;
import exceptions.PublisherException;
import exceptions.SubscriberException;
import models.Message;
import models.MsgQueue;
import models.Subscriber;
import org.apache.commons.io.FileUtils;
import services.MsgQueueService;
import services.PublisherService;
import services.SubscriberService;
import services.ConfigService;
import services.impl.ConfigServiceImpl;
import services.impl.MsgQueueServiceImpl;
import services.impl.PublisherServiceImpl;
import services.impl.SubscriberServiceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.UUID;

import static helpers.Constants.*;


public class MessageQueueService {
    private static MsgQueueService msgQueueService;
    private static PublisherService publisherService;
    private static SubscriberService subscriberService;

    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws Exception {
        initializeServices();
        while (true) {
            try {
                mainMenu();
            } catch (Exception e) {
                System.out.println(ANSI_RED + "Caught Exception: " +e.getMessage());
                System.out.println(ANSI_RED + "StackTrace: \n"+ Arrays.toString(e.getStackTrace()));
            }
        }
    }

    private static void initializeServices() throws IOException {
        try {
            ConfigService configService = new ConfigServiceImpl();
            subscriberService = new SubscriberServiceImpl();
            publisherService = new PublisherServiceImpl();
            msgQueueService = new MsgQueueServiceImpl(configService.getConfig());
        }  catch (Exception e) {
            System.out.println(ANSI_RED + "Caught Exception: " +e.getMessage());
            System.out.println(ANSI_RED + "StackTrace: \n"+ Arrays.toString(e.getStackTrace()));
            throw e;
        }
    }

    private static void mainMenu() throws PublisherException, SubscriberException, MessageQueueException {
        System.out.println(ANSI_BLUE + "*************** Message Queue Service ***************");

        System.out.println(ANSI_BLUE + "Select an option:");
        System.out.println(ANSI_BLUE + "1. Registrations");
        System.out.println(ANSI_BLUE + "2. Publish Message");
        System.out.println(ANSI_BLUE + "3. Utils");
        System.out.println(ANSI_BLUE + "4. Exit the program\n");

        String input = sc.nextLine();
        switch (input) {
            case "1":
                registrationsMenu();
                break;
            case "2":
                publishMessage();
                break;
            case "3":
                utils();
                break;
            case "4":
                System.exit(0);
            default:
                System.out.println(ANSI_RED + "Wrong input, redirecting to main menu");
        }
    }

    private static void utils() {
        System.out.println(ANSI_BLUE + "*************** UTILS ***************");
        System.out.println(ANSI_BLUE + "Select an option:");
        System.out.println(ANSI_BLUE + "1. View all publisher ids registered");
        System.out.println(ANSI_BLUE + "2. View all message queues registered");
        System.out.println(ANSI_BLUE + "3. View all publisher to message queues mapping");
        System.out.println(ANSI_BLUE + "4. View all subscribers of a queue");
        System.out.println(ANSI_BLUE + "5. View message queue lag");

        //We can have retry count threshold and scheduler time interval configurable from here.

        String input = sc.nextLine();
        switch (input) {
            case "1":
                System.out.println(ANSI_CYAN + publisherService.getAllPublisherIds());
                break;
            case "2":
                System.out.println(ANSI_CYAN + msgQueueService.getAllRegisteredQueues());
                break;
            case "3":
                System.out.println(ANSI_CYAN + publisherService.getPublisherToQueueMapping());
                break;
            case "4":
                viewSubscribersOfAQueue();
                break;
            case "5":
                viewQueueLag();
                break;
            default:
                System.out.println(ANSI_RED + "Wrong input, redirecting to main menu");
        }
    }

    private static void viewQueueLag() {
        String queueId;
        System.out.println(ANSI_BLUE + "\nEnter queue id:");
        queueId = sc.nextLine();
        System.out.println(ANSI_CYAN + "Main Queue count:" + msgQueueService.getMainQueueCountForId(queueId));
        System.out.println(ANSI_CYAN + "Retry Queue count:" + msgQueueService.getRetryQueueCountForId(queueId));
    }

    private static void viewSubscribersOfAQueue() {
        String queueId;
        System.out.println(ANSI_BLUE + "\nEnter queue id:");
        queueId = sc.nextLine();
        System.out.println(ANSI_CYAN + msgQueueService.getQueueFromId(queueId).getSubscribers().keySet());
    }

    private static void publishMessage() throws PublisherException {
        System.out.println(ANSI_BLUE + "\nEnter publisher id:");
        String publisherId = sc.nextLine();

        System.out.println(ANSI_BLUE + "\nEnter payload location (Absolute path):");
        String fileLocation = sc.nextLine();

        Message payload = getMessageFromFileLocation(fileLocation, publisherId);

        publisherService.publishMessage(payload, publisherId);
    }

    private static Message getMessageFromFileLocation(String fileLocation, String publisherId) {
        String data = null;
        try {
            File file = new File(fileLocation);
            data = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println(ANSI_RED + "Exception while reading contents from file");
        }
        return new Message(UUID.randomUUID().toString(), data, publisherId, new Date().getTime(), 0);
    }

    private static void registrationsMenu() throws MessageQueueException, SubscriberException, PublisherException {
        System.out.println(ANSI_BLUE + "*************** Registration Menu ***************");
        System.out.println(ANSI_BLUE + "Select an option:");
        System.out.println(ANSI_BLUE + "1. Register new queue");
        System.out.println(ANSI_BLUE + "2. Register new publisher");
        System.out.println(ANSI_BLUE + "3. Add new subscriber to queue");
        System.out.println(ANSI_BLUE + "4. Remove subscriber from queue");
        System.out.println(ANSI_BLUE + "5. Return to main menu");

        String input = sc.nextLine();
        switch (input) {
            case "1":
                registerNewQueue();
                break;
            case "2":
                registerNewPublisher();
                break;
            case "3":
                addSubscriber();
                break;
            case "4":
                removeSubscriber();
                break;
            case "5":
                mainMenu();
            default:
                System.out.println(ANSI_RED + "Wrong input, redirecting to main menu");
        }
    }

    private static void removeSubscriber() throws MessageQueueException {
        System.out.println(ANSI_BLUE + "\nEnter queue id:");
        String queueId = sc.nextLine();

        MsgQueue msgQueue = msgQueueService.getQueueFromId(queueId);
        if (msgQueue == null) {
            System.out.println(ANSI_RED + "Invalid queue");
        } else {
            System.out.println(ANSI_BLUE + "\nEnter Subscriber id:");
            String subscriberId = sc.nextLine();
            Subscriber subscriber = subscriberService.getSubscriberFromId(subscriberId);

            if (subscriber != null) {
                msgQueueService.removeSubscriberFromQueue(queueId, subscriber);
                subscriberService.deleteSubscriber(subscriberId);
            } else {
                System.out.println(ANSI_RED + "Invalid Subscriber Id");
            }
        }
    }

    private static void addSubscriber() throws MessageQueueException, SubscriberException {
        System.out.println(ANSI_BLUE + "\nEnter queue id:");
        String queueId = sc.nextLine();

        MsgQueue msgQueue = msgQueueService.getQueueFromId(queueId);
        if (msgQueue == null) {
            System.out.println(ANSI_RED + "Invalid queue");
        } else {
            System.out.println(ANSI_BLUE + "\nEnter Subscriber id:");
            String subscriberId = sc.nextLine();

            System.out.println(ANSI_BLUE + "\nEnter Subscriber type [type1, type2, generic]:");
            SubscriberType type = SubscriberType.valueOf(sc.nextLine().toLowerCase());
            if (subscriberService.registerSubscriber(subscriberId, type))
                msgQueueService.addSubscriberToQueue(queueId, subscriberService.getSubscriberFromId(subscriberId));
        }
    }

    private static void registerNewQueue() throws MessageQueueException {
        System.out.println(ANSI_BLUE + "\nEnter queue id:");
        String queueId = sc.nextLine();

        msgQueueService.registerQueue(queueId);
    }

    private static void registerNewPublisher() throws PublisherException {
        System.out.println(ANSI_BLUE + "\nEnter queue id:");
        String queueId = sc.nextLine();

        MsgQueue msgQueue = msgQueueService.getQueueFromId(queueId);
        if (msgQueue == null) {
            System.out.println(ANSI_RED + "Invalid queue");
        } else {
            System.out.println(ANSI_BLUE + "\nEnter publisher id:");
            String publisherId = sc.nextLine();
            publisherService.registerPublisher(publisherId, msgQueue);
        }
    }
}
