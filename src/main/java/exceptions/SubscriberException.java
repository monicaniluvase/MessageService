package exceptions;

public class SubscriberException extends Exception{

    String message;

    public SubscriberException(String message) {
        this.message = message;
    }
}
