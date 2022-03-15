package helpers;

import org.testng.Assert;
import org.testng.annotations.Test;


public class MessageUtilsTest {

    @Test
    public void testIsValidJson() {
        Assert.assertTrue(MessageUtils.isValidJson("[\"valid\"]"));

        Assert.assertTrue(MessageUtils.isValidJson("{\"valid\":true}"));

        Assert.assertFalse(MessageUtils.isValidJson("{\"invalid\"}"));

        Assert.assertFalse(MessageUtils.isValidJson("invalid"));
    }
}