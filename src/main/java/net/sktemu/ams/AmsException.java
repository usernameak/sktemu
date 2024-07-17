package net.sktemu.ams;

public class AmsException extends Exception {
    public AmsException() {
    }

    public AmsException(String message) {
        super(message);
    }

    public AmsException(String message, Throwable cause) {
        super(message, cause);
    }

    public AmsException(Throwable cause) {
        super(cause);
    }
}
