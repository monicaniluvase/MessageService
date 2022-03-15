package exceptions;

public class MessageQueueException extends Exception{

    String message;

    public MessageQueueException(String message) {
        this.message = message;
    }
}
