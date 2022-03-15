package services.impl;

import enums.SubscriberType;
import exceptions.SubscriberException;
import models.Subscriber;
import models.subscribers.GenericSubscriber;
import models.subscribers.SampleSubscriber1;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static helpers.Constants.ANSI_GREEN;
import static helpers.Constants.ANSI_RED;
import static org.testng.Assert.*;

public class SubscriberServiceImplTest {
    private ByteArrayOutputStream outContent;
    private SubscriberServiceImpl subscriberService;

    @BeforeMethod
    public void setup(){
        subscriberService = new SubscriberServiceImpl();
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    public void testRegisterSubscriber() throws SubscriberException {
        String output = "";

        subscriberService.registerSubscriber("id1", SubscriberType.type1);
        Assert.assertEquals(subscriberService.getIdToSubscriberMap().size(), 1);
        output += ANSI_GREEN + "Subscriber successfully registered.\n" ;
        Assert.assertEquals(output, outContent.toString());
        Assert.assertTrue(subscriberService.getIdToSubscriberMap().get("id1") instanceof SampleSubscriber1);

        subscriberService.registerSubscriber("id1", SubscriberType.type2);
        Assert.assertEquals(subscriberService.getIdToSubscriberMap().size(), 1);
        output += ANSI_RED + "Subscriber already registered.\n";
        Assert.assertEquals(output, outContent.toString());

        subscriberService.registerSubscriber("id2", SubscriberType.generic);
        Assert.assertEquals(subscriberService.getIdToSubscriberMap().size(), 2);
        output += ANSI_GREEN + "Subscriber successfully registered.\n";
        Assert.assertEquals(output, outContent.toString());
        Assert.assertTrue(subscriberService.getIdToSubscriberMap().get("id2") instanceof GenericSubscriber);
    }

    @Test
    public void testGetSubscriberFromId() throws SubscriberException {
        Subscriber subscriber = new SampleSubscriber1("id1");
        subscriberService.registerSubscriber("id1", SubscriberType.type1);
        Assert.assertEquals(subscriberService.getSubscriberFromId("id1").getId(), subscriber.getId());
        Assert.assertNull(subscriberService.getSubscriberFromId("id2"));
        Assert.assertNull(subscriberService.getSubscriberFromId(null));
    }

    @Test
    public void testDeleteSubscriber() throws SubscriberException {
        subscriberService.registerSubscriber("id1", SubscriberType.type1);
        subscriberService.registerSubscriber("id2", SubscriberType.type1);
        Assert.assertEquals(subscriberService.getIdToSubscriberMap().size(), 2);

        subscriberService.deleteSubscriber("id1");
        Assert.assertEquals(subscriberService.getIdToSubscriberMap().size(), 1);

        subscriberService.deleteSubscriber("id1");
        Assert.assertEquals(subscriberService.getIdToSubscriberMap().size(), 1);

        subscriberService.deleteSubscriber("id2");
        Assert.assertEquals(subscriberService.getIdToSubscriberMap().size(), 0);
    }
}