package com.serial_proxy.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import com.serialproxy.R;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(R.string.binding_title);

        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, new BindingSettings())
                .commit();
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }
}
