package exceptions;

public class OverlapException extends IllegalArgumentException {
    public OverlapException(String message) {
        super(message);
    }
}