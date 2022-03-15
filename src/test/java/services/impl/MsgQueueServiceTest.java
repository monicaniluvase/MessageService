package services.impl;

import config.Configuration;
import exceptions.MessageQueueException;
import models.Message;
import models.Subscriber;
import models.subscribers.SampleSubscriber1;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import static helpers.Constants.ANSI_GREEN;
import static helpers.Constants.ANSI_RED;

public class MsgQueueServiceTest {
    private MsgQueueServiceImpl msgQueueService;
    private ByteArrayOutputStream outContent;

    @BeforeMethod
    public void setup(){
        msgQueueService = new MsgQueueServiceImpl(new Configuration());
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testRegisterQueue() throws MessageQueueException {
        String output = "";

        msgQueueService.registerQueue("id1");
        Assert.assertEquals(msgQueueService.getIdToQueueMap().size(), 1);
        output += ANSI_GREEN + "Message Queue registered\n" ;
        Assert.assertEquals(output, outContent.toString());

        msgQueueService.registerQueue("id1");
        Assert.assertEquals(msgQueueService.getIdToQueueMap().size(), 1);
        output += ANSI_RED + "Queue Already exists\n";
        Assert.assertEquals(output, outContent.toString());

        msgQueueService.registerQueue("id2");
        Assert.assertEquals(msgQueueService.getIdToQueueMap().size(), 2);
        output += ANSI_GREEN + "Message Queue registered\n";
        Assert.assertEquals(output, outContent.toString());
    }

    @Test
    public void testAddSubscriberToQueue() throws MessageQueueException {
        msgQueueService.registerQueue("id1");
        Subscriber subscriber = new SampleSubscriber1("s1");
        String output = ANSI_GREEN + "Message Queue registered\n";

        msgQueueService.addSubscriberToQueue("id2", subscriber);
        output += ANSI_RED + "Queue does not exist\n";
        Assert.assertEquals(output, outContent.toString());

        msgQueueService.addSubscriberToQueue("id1", null);
        output += ANSI_RED + "Subscriber cannot be null\n";
        Assert.assertEquals(output, outContent.toString());

        msgQueueService.addSubscriberToQueue("id1", subscriber);
        output += ANSI_GREEN + "Subscriber successfully Subscribed\n";
        Assert.assertEquals(output, outContent.toString());
        Assert.assertEquals(msgQueueService.getIdToQueueMap().get("id1").getSubscribers().size(), 1);
        Assert.assertEquals(msgQueueService.getIdToQueueMap().get("id1").getSubscribers().get("s1"), subscriber);

        Subscriber subscriber2 = new SampleSubscriber1("s2");
        msgQueueService.addSubscriberToQueue("id1", subscriber2);
        output += ANSI_GREEN + "Subscriber successfully Subscribed\n";
        Assert.assertEquals(output, outContent.toString());
        Assert.assertEquals(msgQueueService.getIdToQueueMap().get("id1").getSubscribers().size(), 2);
        Assert.assertEquals(msgQueueService.getIdToQueueMap().get("id1").getSubscribers().get("s2"), subscriber2);

        msgQueueService.addSubscriberToQueue("id1", subscriber);
        output += ANSI_RED + "Subscriber already Subscribed\n";
        Assert.assertEquals(output, outContent.toString());
        Assert.assertEquals(msgQueueService.getIdToQueueMap().get("id1").getSubscribers().size(), 2);
    }

    @Test
    public void testRemoveSubscriberFromQueue() throws MessageQueueException {
        msgQueueService.registerQueue("id1");
        Subscriber subscriber = new SampleSubscriber1("s1");
        String output = ANSI_GREEN + "Message Queue registered\n";

        msgQueueService.addSubscriberToQueue("id1", subscriber);
        output += ANSI_GREEN + "Subscriber successfully Subscribed\n";

        msgQueueService.removeSubscriberFromQueue("id2", subscriber);
        output += ANSI_RED + "Queue does not exist\n";
        Assert.assertEquals(output, outContent.toString());
        Assert.assertEquals(msgQueueService.getIdToQueueMap().get("id1").getSubscribers().get("s1"), subscriber);

        msgQueueService.removeSubscriberFromQueue("id1", subscriber);
        output += ANSI_GREEN + "Subscriber successfully unsubscribed\n";
        Assert.assertEquals(output, outContent.toString());
        Assert.assertNull(msgQueueService.getIdToQueueMap().get("id1").getSubscribers().get("s1"));

        msgQueueService.removeSubscriberFromQueue("id1", subscriber);
        output += ANSI_RED + "Subscriber already unsubscribed\n";
        Assert.assertEquals(output, outContent.toString());
        Assert.assertEquals(msgQueueService.getIdToQueueMap().get("id1").getSubscribers().size(), 0);
    }

    @Test
    public void testGetQueueFromId() throws MessageQueueException {
        msgQueueService.registerQueue("id1");
        Assert.assertEquals(msgQueueService.getQueueFromId("id1").getId(), "id1");
        Assert.assertNull(msgQueueService.getQueueFromId("id2"));
    }

    @Test
    public void testGetMainQueueCountForId() throws MessageQueueException {
        msgQueueService.registerQueue("id1");
        msgQueueService.getIdToQueueMap().get("id1").enqueue(new Message("1", "[0,1,2]", "p1", 123, 0));
        msgQueueService.getIdToQueueMap().get("id1").enqueue(new Message("2", "[0,1,2]", "p1", 123, 0));
        String output = ANSI_GREEN + "Message Queue registered\n";
        output += ANSI_RED + "Queue does not exist\n";

        Assert.assertEquals(msgQueueService.getMainQueueCountForId("id2"), 0);
        Assert.assertEquals(output, outContent.toString());
        Assert.assertEquals(msgQueueService.getMainQueueCountForId("id1"), 2);

        msgQueueService.getIdToQueueMap().get("id1").getQueue().dequeue();
        Assert.assertEquals(msgQueueService.getMainQueueCountForId("id1"), 1);
    }

    @Test
    public void testGetRetryQueueCountForId() throws MessageQueueException {
        msgQueueService.registerQueue("id1");
        msgQueueService.getIdToQueueMap().get("id1").enqueue(new Message("1", "invalidJson", "p1", 123, 0));
        msgQueueService.getIdToQueueMap().get("id1").enqueue(new Message("2", "invalidJson", "p1", 123, 0));
        String output = ANSI_GREEN + "Message Queue registered\n";
        Assert.assertNotEquals(msgQueueService.getRetryQueueCountForId("id1"), 0);
        Assert.assertTrue(outContent.toString().contains(output));

        msgQueueService.getIdToQueueMap().get("id1").getRetryQueue().dequeue();
        Assert.assertEquals(msgQueueService.getRetryQueueCountForId("id1"), 1);

        output = ANSI_RED + "Queue does not exist\n";
        Assert.assertEquals(msgQueueService.getRetryQueueCountForId("id2"), 0);
        Assert.assertTrue(outContent.toString().contains(output));
    }

    @Test
    public void testGetAllRegisteredQueues() throws MessageQueueException {
        Set<String> output = new HashSet<>();
        output.add("q1");
        output.add("q2");

        msgQueueService.registerQueue("q1");
        msgQueueService.registerQueue("q2");

        Assert.assertEquals(output, msgQueueService.getAllRegisteredQueues());
    }

}