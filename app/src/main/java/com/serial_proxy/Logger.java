package com.serial_proxy;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Logger {

    private static volatile File         FILE;
    private static volatile WeakReference<MainActivity> ACTIVITY;

    private final String tag;

    static void setActivity(MainActivity aActivity) {
        FILE = new File(Environment.getExternalStorageDirectory(), "sproxy-" + System.currentTimeMillis() + ".txt");
        ACTIVITY = new WeakReference<>(aActivity);
        if(writeToFile("File created")) {
            aActivity.log("You can find all logs in the " + FILE.getAbsolutePath());
        } else {
            aActivity.log("Cannot write to file " + FILE.getAbsolutePath());
        }


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
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.ENGLISH);
        String message = dateFormat.format(new Date()) + " " + aLevel + " " + formatString(aFormat, args);

        MainActivity mainActivity = ACTIVITY.get();
        if(mainActivity != null) {
            mainActivity.log(message);
        }

        if(FILE !=null ) {
            writeToFile(message);
        }
    }

    private static boolean writeToFile(String message) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(FILE, true));
            try {
                out.println(message);
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            Log.e("sproxy", "Cannot log message " + message, e);
            return false;
        }
    }

    public static Logger create(String aName) {
        return new Logger("sproxy."+aName);
    }
}
