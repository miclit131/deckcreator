package ml131.de.hdm_stuttgart.mi.exceptions;

public class LogfriendlyThreadException extends LogfriendlyException {

    public LogfriendlyThreadException(String errorCall, Exception baseException) {
        super(errorCall, baseException);
    }

    public LogfriendlyThreadException(String errorCall) {
        super(errorCall);
    }

}
