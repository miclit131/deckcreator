package ml131.de.hdm_stuttgart.mi.exceptions;

public class FileNotFoundException extends ExceptionCluster{
    public FileNotFoundException(String errorCalls){
        super(errorCalls);
    }
    @Override
    public String getErrorType(){
        return "FileNotFoundException";
    }
}
