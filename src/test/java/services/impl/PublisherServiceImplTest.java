package services.impl;

import exceptions.PublisherException;
import models.Message;
import models.MsgQueue;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static helpers.Constants.ANSI_GREEN;
import static helpers.Constants.ANSI_RED;

public class PublisherServiceImplTest {
    private ByteArrayOutputStream outContent;
    private PublisherServiceImpl publisherService;

    @BeforeMethod
    public void setup(){
        publisherService = new PublisherServiceImpl();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testRegisterPublisher() throws PublisherException {
        String output = "";

        publisherService.registerPublisher("id1", new MsgQueue("q1", 3, 30,60));
        Assert.assertEquals(publisherService.getPublisherMap().size(), 1);
        Assert.assertEquals(publisherService.getRegisteredQueueMap().size(), 1);
        output += ANSI_GREEN + "Publisher Registered.\n" ;
        Assert.assertEquals(output, outContent.toString());

        publisherService.registerPublisher("id2", new MsgQueue("q1", 3, 30,60));
        Assert.assertEquals(publisherService.getPublisherMap().size(), 1);
        Assert.assertEquals(publisherService.getRegisteredQueueMap().size(), 1);
        output += ANSI_RED + "Queue is already mapped to another publisher:id1\n";
        Assert.assertEquals(output, outContent.toString());

        publisherService.registerPublisher("id1", new MsgQueue("q2", 3, 30,60));
        Assert.assertEquals(publisherService.getPublisherMap().size(), 1);
        Assert.assertEquals(publisherService.getRegisteredQueueMap().size(), 1);
        output += ANSI_RED + "Publisher already registered\n";
        Assert.assertEquals(output, outContent.toString());

        publisherService.registerPublisher("id2", new MsgQueue("q2", 3, 30,60));
        Assert.assertEquals(publisherService.getPublisherMap().size(), 2);
        Assert.assertEquals(publisherService.getRegisteredQueueMap().size(), 2);
        output += ANSI_GREEN + "Publisher Registered.\n";
        Assert.assertEquals(output, outContent.toString());
    }

    @Test
    public void testPublishMessage() throws PublisherException {
        String output = "";

        publisherService.registerPublisher("id1", new MsgQueue("q1", 3, 30,60));
        output += ANSI_GREEN + "Publisher Registered.\n" ;
        Message validMessage = new Message("1", "[0,1,2]", "p1", 123, 0);
        Message invalidMessage1 = new Message("1", "invalidJson", "p1", 123, 0);
        Message invalidMessage2 = new Message("1", "invalidJson", "p1", 123, 3);

        publisherService.publishMessage(validMessage, "id1");
        output += ANSI_GREEN + "Message successfully queued\n";
        Assert.assertEquals(output, outContent.toString());

        publisherService.publishMessage(invalidMessage1, "id1");
        output += ANSI_RED + "Message Queue Failed. Moved to retry queue.\n";
        Assert.assertEquals(output, outContent.toString());

        publisherService.publishMessage(invalidMessage2, "id1");
        output += ANSI_RED + "\nMax retry reached, discarding message " + invalidMessage2.toString() + "\n";
        output += ANSI_RED + "Message Queue Failed. Moved to retry queue.\n";
        Assert.assertEquals(output, outContent.toString());

        publisherService.publishMessage(validMessage, "id2");
        output += ANSI_RED + "Publisher not registered\n";
        Assert.assertEquals(output, outContent.toString());

        publisherService.getPublisherMap().get("id1").setQueue(null);
        publisherService.publishMessage(validMessage, "id1");
        output += ANSI_RED + "Publisher does not have a queue linked. Please link a queue.\n";
        Assert.assertEquals(output, outContent.toString());
    }

    @Test
    public void testGetAllPublisherIds() throws PublisherException {
        Assert.assertEquals(publisherService.getAllPublisherIds().size(),0);
        publisherService.registerPublisher("id1", new MsgQueue("q1", 3, 30,60));
        publisherService.registerPublisher("id2", new MsgQueue("q2", 3, 30,60));
        Set<String> output = new HashSet<>();
        output.add("id1");
        output.add("id2");
        Assert.assertEquals(publisherService.getAllPublisherIds(), output);
    }

    @Test
    public void testGetPublisherToQueueMapping() throws PublisherException {
        Assert.assertEquals(publisherService.getPublisherToQueueMapping().size(),0);
        publisherService.registerPublisher("id1", new MsgQueue("q1", 3, 30,60));
        publisherService.registerPublisher("id2", new MsgQueue("q2", 3, 30,60));
        Map<String, String> output = new HashMap<>();
        output.put("id1", "q1");
        output.put("id2", "q2");
        Assert.assertEquals(publisherService.getPublisherToQueueMapping(), output);
    }
}