package com.serial_proxy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.TextView;
import com.serial_proxy.settings.BindingProfile;
import com.serial_proxy.settings.ProfilesManager;
import com.serial_proxy.settings.SettingsActivity;
import com.serialproxy.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    TextView logView;
    ScrollView scrollView;
    ProfilesManager profilesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profilesManager = new ProfilesManager(this);

        setContentView(R.layout.activity_main);
        logView = (TextView) findViewById(R.id.textView);
        scrollView = (ScrollView) findViewById(R.id.scroll);

        Logger.setActivity(this);

        startThreads();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopThreads();
    }


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        stopThreads();

        startThreads();
    }

    private void startThreads() {
        List<BindingProfile> profiles = profilesManager.load(null);
        for (BindingProfile profile : profiles) {
            SerialProxyServerTask thread = new SerialProxyServerTask(profile);
            thread.start();
            threads.add(thread) ;
        }
    }

    private void stopThreads() {
        for (SerialProxyServerTask thread : threads) {
            thread.cancel();
            for(int i=0; i<10 && thread.isAlive(); i++) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivityForResult(new Intent(this, SettingsActivity.class), 100);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    private List<SerialProxyServerTask> threads = new ArrayList<SerialProxyServerTask>();
}
