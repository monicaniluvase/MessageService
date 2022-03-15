package models;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Publisher {
    private String id;
    private MsgQueue queue;
}
