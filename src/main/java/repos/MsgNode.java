package repos;

import lombok.Data;
import models.Message;

@Data
public class MsgNode {
    Message message;
    MsgNode next;

    public MsgNode(Message message) {
        this.message = message;
        this.next = null;
    }
}
