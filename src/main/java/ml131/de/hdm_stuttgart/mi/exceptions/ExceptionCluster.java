package ml131.de.hdm_stuttgart.mi.exceptions;

    public abstract class ExceptionCluster extends Error implements errorAcess{
    ExceptionCluster(String errorCall){
        super(errorCall);
    }
    
}

