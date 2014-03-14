package com.serial_proxy.settings;

import android.content.Context;
import android.preference.Preference;

public class BindingPreference extends Preference {

    private BindingProfile profile;

    public BindingPreference(Context context, BindingProfile aProfile) {
        super(context);
        setPersistent(false);
        setOrder(0);
        profile = aProfile;
        update();
    }

    private void update() {
        setTitle(profile.title);
        setSummary(String.format("%d → %s (%s)", profile.port, profile.bluetoothName, profile.bluetoothAddress));
    }

    public BindingProfile getProfile() {
        return profile;
    }
}
