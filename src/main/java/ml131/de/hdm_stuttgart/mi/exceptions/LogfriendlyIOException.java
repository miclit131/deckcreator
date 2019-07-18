package ml131.de.hdm_stuttgart.mi.exceptions;

public class LogfriendlyIOException extends LogfriendlyException {

    public LogfriendlyIOException(String errorCall, Exception baseException) {
        super(errorCall, baseException);
    }

    public LogfriendlyIOException(String errorCall) {
        super(errorCall);
    }

}
