package program.exceptions;

public class IncorrectDataException extends RuntimeException {
    public IncorrectDataException(String msg) {
        super(msg);
    }
}