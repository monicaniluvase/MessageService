package models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {
    private String id;
    private String data;
    private String publisherId;
    private long timestamp;
    private int retryCount = 0;

    public void incrementRetryCount() {
        retryCount++;
    }
}
