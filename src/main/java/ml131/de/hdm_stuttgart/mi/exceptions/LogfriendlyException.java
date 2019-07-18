package ml131.de.hdm_stuttgart.mi.exceptions;

import ml131.de.hdm_stuttgart.mi.util.LoggerUtil;

public abstract class LogfriendlyException extends Exception{

    LogfriendlyException(String errorCall, Exception baseException){
        super(errorCall);
    }

    LogfriendlyException(String errorCall){
        super(errorCall);
    }

    String getStacktraceString(Exception baseException){
        return LoggerUtil.errorWithAnalysis(baseException);
    }

}

