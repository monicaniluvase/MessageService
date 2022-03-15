package exceptions;

public class PublisherException extends Exception {

    String message;

    public PublisherException(String message) {
        this.message = message;
    }
}
