package com.serial_proxy.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.*;
import com.serial_proxy.Logger;
import com.serialproxy.R;

public class BindingSettings extends PreferenceFragment {

    private static Logger LOG = Logger.create(BindingSettings.class);

    private BindingSettingsDelegate delegate;
    private ProfilesManager profilesManager ;
    private BindingPreference selectedPreference;
    private BindingDialog bindingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        profilesManager = new ProfilesManager(getActivity());
        delegate = new BindingSettingsDelegate(this);

        setHasOptionsMenu(true);
        addPreferencesFromResource(R.xml.binding_settings);
        profilesManager.load(getPreferenceScreen());
    }

    @Override
    public void onResume() {
        super.onResume();

        registerForContextMenu(delegate.getListView());
    }

    @Override
    public void onPause() {
        super.onPause();
        profilesManager.save();
        if(getView()!=null) {
            unregisterForContextMenu(delegate.getListView());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.binding_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(selectedPreference==null) return false;

        PreferenceScreen group = getPreferenceScreen();

        switch (item.getItemId()) {
            case R.string.binding_menu_delete:
                profilesManager.removeProfile(group, selectedPreference);
                return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final PreferenceScreen group = getPreferenceScreen();
        switch (item.getItemId()) {
            case R.id.action_add:
                if(bindingDialog==null) {
                    bindingDialog = new BindingDialog(getActivity(), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which==DialogInterface.BUTTON_POSITIVE) {
                                BindingProfile profile = bindingDialog.getProfile();
                                profilesManager.addProfile(group, profile, getActivity());
                            }
                        }
                    });
                }
                bindingDialog.show();

                return true;
            case R.string.binding_menu_delete:
                if(selectedPreference!=null) {
                    group.removePreference(selectedPreference);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu aMenu, View aView, ContextMenu.ContextMenuInfo aInfo) {
        selectedPreference = delegate.getSelectedPreference(aMenu, aInfo);
    }

}
