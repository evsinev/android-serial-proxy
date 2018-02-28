package com.serial_proxy.settings;

import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.ContextMenu;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ListView;
import com.serial_proxy.Logger;
import com.serialproxy.R;

import java.lang.reflect.Method;

public class BindingSettingsDelegate {

    private static Logger LOG = Logger.create(BindingSettingsDelegate.class);

    private final PreferenceFragment fragment;

    public BindingSettingsDelegate(PreferenceFragment aFragment) {
        fragment = aFragment;
    }

    public BindingPreference getSelectedPreference(ContextMenu aMenu, ContextMenu.ContextMenuInfo aInfo) {
        if(aInfo instanceof AdapterView.AdapterContextMenuInfo) {
            AdapterView.AdapterContextMenuInfo adapterInfo = (AdapterView.AdapterContextMenuInfo) aInfo;
            Preference preference = (Preference) getListView().getItemAtPosition(adapterInfo.position);
            if(preference instanceof BindingPreference) {
                BindingPreference bindingPreference = (BindingPreference) preference;
                BindingProfile profile = bindingPreference.getProfile();
                aMenu.setHeaderTitle(profile.title);
                aMenu.add(Menu.NONE, R.string.binding_menu_edit, 0, R.string.binding_menu_edit);
                aMenu.add(Menu.NONE, R.string.binding_menu_delete, 0, R.string.binding_menu_delete);
                return bindingPreference;
            }
        }
        return null;
    }

    public ListView getListView() {
        try {
            Method method = fragment.getClass().getMethod("getListView");
            return (ListView) method.invoke(fragment);
        } catch (Exception e) {
            LOG.error("Error invoking getListView()", e);
            throw new IllegalStateException("No method getListView()");
        }
    }

}
