package ml131.de.hdm_stuttgart.mi.exceptions;

public class LogfriendlyNoInternetConnectionException extends LogfriendlyException {

    public LogfriendlyNoInternetConnectionException(String errorCall, Exception baseException) {
        super(errorCall, baseException);
    }

    public LogfriendlyNoInternetConnectionException(String errorCall) {
        super(errorCall);
    }

}
