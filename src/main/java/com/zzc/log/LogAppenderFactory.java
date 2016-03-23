package com.zzc.log;

/**
 * Created by zuozc on 3/18/16.
 */
public class LogAppenderFactory {

    private static LogWindow APPENDER;

    public static LogWindow getLogWindow() {
        if(APPENDER == null) {
            APPENDER = new LogWindow();
        }
        return APPENDER;
    }
}
