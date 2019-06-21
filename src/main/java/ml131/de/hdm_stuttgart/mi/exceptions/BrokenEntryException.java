package ml131.de.hdm_stuttgart.mi.exceptions;

public class BrokenEntryException extends ExceptionCluster {

        public BrokenEntryException(String errorCalls){
            super(errorCalls);
        }
        @Override
        public String getErrorType(){
            return "BrokenEntryException";
        }
}
