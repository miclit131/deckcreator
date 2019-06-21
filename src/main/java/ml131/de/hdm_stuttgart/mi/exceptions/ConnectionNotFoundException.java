package ml131.de.hdm_stuttgart.mi.exceptions;

public class ConnectionNotFoundException extends ExceptionCluster{

        public ConnectionNotFoundException(String errorCalls){
            super(errorCalls);
        }
        @Override
        public String getErrorType(){
            return "ConnectionNotFoundException";
        }
}
