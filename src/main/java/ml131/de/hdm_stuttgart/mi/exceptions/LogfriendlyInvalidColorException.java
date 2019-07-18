package ml131.de.hdm_stuttgart.mi.exceptions;

public class LogfriendlyInvalidColorException extends LogfriendlyException {

    LogfriendlyInvalidColorException(String errorCall, Exception baseException) {
        super(errorCall, baseException);
    }

    public LogfriendlyInvalidColorException(String errorCall) {
        super(errorCall);
    }
}
