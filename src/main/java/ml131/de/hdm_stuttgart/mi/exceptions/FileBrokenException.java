package ml131.de.hdm_stuttgart.mi.exceptions;

public class FileBrokenException extends ExceptionCluster {
    public FileBrokenException(String errorCalls){
        super(errorCalls);
    }
    @Override
    public String getErrorType(){
        return "FileBrokenException";
    }
}
