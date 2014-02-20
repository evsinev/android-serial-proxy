package com.serial_proxy;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ScrollView;
import android.widget.TextView;
import com.serialproxy.R;

public class MainActivity extends Activity {

    TextView logView;
    ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        logView = (TextView) findViewById(R.id.textView);
        scrollView = (ScrollView) findViewById(R.id.scroll);

        Logger.setActivity(this);

        serverThread = new Thread(new SerialProxyServerTask());
        serverThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        serverThread.interrupt();
    }

    private Thread serverThread;

    public void log(final String aText) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logView.append("\n" + aText);
            }
        });

        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 500);
    }
}
