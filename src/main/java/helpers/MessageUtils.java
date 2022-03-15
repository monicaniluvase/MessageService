package helpers;

import org.json.*;

public class MessageUtils {

    public static boolean isValidJson(String message) {
        try {
            new JSONObject(message);
        } catch (JSONException e) {
            try {
                new JSONArray(message);
            } catch (JSONException e1) {
                return false;
            }
        }
        return true;
    }
}
