package config;

import lombok.Data;
import lombok.Getter;

@Data
public class Configuration {
    private int scheduleDelayMainQueueInSecs = 30;

    private int scheduleDelayRetryQueueInSecs = 60;

    private int retryQueueThreshold = 3;
}
