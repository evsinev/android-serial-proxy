package com.serial_proxy.settings;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceScreen;
import android.util.Xml;
import com.serial_proxy.Logger;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ProfilesManager {

    private static final Logger LOG = Logger.create(ProfilesManager.class);
    public static final String NAMESPACE = "";

    private final Context context;
    private List<BindingProfile> profiles = new ArrayList<BindingProfile>();

    public ProfilesManager(Activity aContext) {
        context = aContext;
    }

    public void save() {
        try {
            XmlSerializer serializer = Xml.newSerializer();
            // Environment.getExternalStorageDirectory()
            File file = getConfigFile();
            LOG.debug("Saving to %s", file.getAbsolutePath());

            PrintWriter out = new PrintWriter(file);
            try {
                serializer.setOutput(out);
                serializer.startDocument("utf-8", true);
                serializer.startTag(NAMESPACE, "bindings");
                for (BindingProfile profile : profiles) {
                    serializer.startTag(NAMESPACE, "binding");
                    serializer.attribute(NAMESPACE, "title", profile.title);
                    serializer.attribute(NAMESPACE, "port", String.valueOf(profile.port));
                    serializer.attribute(NAMESPACE, "bt-address", profile.bluetoothAddress);
                    serializer.attribute(NAMESPACE, "bt-name", profile.bluetoothName);
                    serializer.endTag(NAMESPACE, "binding");
                }
                serializer.endTag(NAMESPACE, "bindings");
                serializer.endDocument();
            } finally {
                out.close();
            }
        } catch (Exception e) {
            LOG.error("Can't save configuration", e);
            throw new IllegalStateException("Can't save config", e);
        }
    }

    private File getConfigFile() {
        return new File(context.getFilesDir(), "serial-proxy.xml");
    }

    public List<BindingProfile> load(PreferenceScreen aGroup) {
        File configFile = getConfigFile();
        LOG.debug("Loading from %s", configFile.getAbsolutePath());
        if(configFile.exists()) {
            XmlPullParser parser = Xml.newPullParser();
            try {
                parser.setInput(new FileReader(configFile));

                int event = parser.getEventType();
                profiles = new ArrayList<BindingProfile>();
                for(int i=0; i<100 && event!=XmlPullParser.END_DOCUMENT; i++) {
                    switch (event) {
                        case XmlPullParser.END_TAG:
                            if(parser.getName().equals("binding")) {
                                BindingProfile profile = new BindingProfile();
                                profile.title = parser.getAttributeValue(NAMESPACE, "title");
                                profile.port = Integer.parseInt(parser.getAttributeValue(NAMESPACE, "port"));
                                profile.bluetoothName = parser.getAttributeValue(NAMESPACE, "bt-name");
                                profile.bluetoothAddress = parser.getAttributeValue(NAMESPACE, "bt-address");
                                profiles.add(profile);
                            }
                    }

                    event = parser.next();
                }
            } catch (Exception e) {
                LOG.error("Cant' load "+configFile.getAbsolutePath(), e);
            }
        }

        if(aGroup!=null) {
            for (BindingProfile profile : profiles) {
                aGroup.addPreference(new BindingPreference(context, profile));
            }
        }
        return profiles;
    }



    public void addProfile(PreferenceScreen group, BindingProfile profile, Activity aActivity) {
        profiles.add(profile);
        group.addPreference(new BindingPreference(aActivity, profile));
    }

    public void removeProfile(PreferenceScreen group, BindingPreference selectedPreference) {
        String key = selectedPreference.getProfile().title;

        Iterator<BindingProfile> iterator = profiles.iterator();
        while (iterator.hasNext()) {
            BindingProfile next = iterator.next();
            if(next.title.equals(key)) {
                iterator.remove();
            }
        }
        group.removePreference(selectedPreference);

    }
}
