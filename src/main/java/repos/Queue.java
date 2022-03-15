package repos;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Synchronized;
import models.Message;

//I am implementing queue with Linked List
@NoArgsConstructor
public class Queue {
    MsgNode front;
    MsgNode rear;
    @Getter
    int count;

    @Synchronized
    public void enqueue(Message message) {
        MsgNode temp = new MsgNode(message);
        if (this.rear == null) {
            this.front = temp;
            this.rear = temp;
            count += 1;
            return;
        }
        this.rear.next = temp;
        this.rear = temp;
        count += 1;
    }

    @Synchronized
    public Message dequeue() {
        if (front == null) {
            return null;
        }

        MsgNode temp = front;
        front = front.next;
        if (front == null) {
            rear = null;
        }
        count -= 1;
        return temp.message;
    }
}

