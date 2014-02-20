package com.serial_proxy;

import android.app.Activity;
import android.util.Log;

public class Logger {

    public static void setActivity(MainActivity aActivity) {
          ACTIVITY = aActivity;
    }

    public static Logger create(Class aClass) {
         return new Logger("sproxy."+aClass.getSimpleName());
    }

    private Logger(String aTag) {
        tag = aTag;
    }

    public void debug(String aFormat, Object ...args) {
        String msg = formatString(aFormat, args);
        Log.d(tag, msg);
        logOnActivity("DEBUG", msg);
    }

    public void warn(String aFormat, Object ...args) {
        String msg = formatString(aFormat, args);
        Log.w(tag, msg);
        logOnActivity("WARN ", msg);
    }

    public void info(String aFormat, Object ...args) {
        String msg = formatString(aFormat, args);
        Log.i(tag, msg);
        logOnActivity("INFO ", msg);
    }


    public void error(String aMessage, Exception error) {
        Log.e(tag, aMessage, error);
        logOnActivity("ERROR", aMessage, error.toString());
    }

    private static String formatString(String aFormat, Object... args) {
        return args==null ? aFormat : String.format(aFormat, args);
    }

    private static void logOnActivity(String aLevel, String aFormat, Object... args) {
        if(ACTIVITY!=null) {
            ACTIVITY.log(aLevel+" "+formatString(aFormat, args));
        }
    }

    private static MainActivity ACTIVITY;
    private final String tag;

    public static Logger create(String aName) {
        return new Logger("sproxy."+aName);
    }
}
