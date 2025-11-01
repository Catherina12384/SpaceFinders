package Com.Spacefinders.Exception;

public class ComplaintNotFoundException extends RuntimeException {

    public ComplaintNotFoundException(String message) {
        super(message);
    }
}