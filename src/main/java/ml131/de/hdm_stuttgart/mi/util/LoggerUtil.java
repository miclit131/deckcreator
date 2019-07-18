package ml131.de.hdm_stuttgart.mi.util;

public class LoggerUtil {

    // inspired by https://stackoverflow.com/questions/4347797/how-to-send-a-stacktrace-to-log4j
    public static String errorWithAnalysis(Exception exception) {
        String message="No Message on error";

        if(exception != null){
            StackTraceElement[] stackTrace = exception.getStackTrace();
            if(stackTrace!=null && stackTrace.length>0) {
                message="";
                for (StackTraceElement e : stackTrace) {
                    message += "\n" + e.toString();
                }
            }
        }
        return message;
    }

}
