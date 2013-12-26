/* 
 * Copyright (C) 2013 Dark Jelly
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.darkkat;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarExpandedNotifLabelStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "NotificationDrawerCwLabelStyle";

    private static final String PREF_NOTIFICATION_SHOW_CUSTOM_CARRIER_LABEL = "notification_show_custom_carrier_label";
    private static final String PREF_NOTIFICATION_SHOW_WIFI_SSID = "notification_show_wifi_ssid";
    private static final String PREF_NOTIFICATION_CW_LABEL_COLOR = "notification_carrier_wifi_label_color";

    private CheckBoxPreference mNotificationShowCustomCarrierLabel;
    private CheckBoxPreference mNotificationShowWifiSsid;
    private ColorPickerPreference mNotificationCwLabelColor;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        addPreferencesFromResource(R.xml.status_bar_expanded_notif_label_style);
        mResolver = getActivity().getContentResolver();

        mNotificationShowCustomCarrierLabel = (CheckBoxPreference) findPreference(PREF_NOTIFICATION_SHOW_CUSTOM_CARRIER_LABEL);
        mNotificationShowCustomCarrierLabel.setChecked(Settings.System.getInt(mResolver,
                Settings.System.NOTIFICATION_SHOW_CUSTOM_CARRIER_LABEL, 1) == 1);
        String customLabelText = Settings.System.getString(mResolver,
                Settings.System.CUSTOM_CARRIER_LABEL);
        if (customLabelText == null || customLabelText.length() == 0) {
            mNotificationShowCustomCarrierLabel.setSummary(R.string.custom_carrier_label_notset);
            mNotificationShowCustomCarrierLabel.setEnabled(false);
        } else {
            mNotificationShowCustomCarrierLabel.setSummary(R.string.show_custom_carrier_label_enabled_summary);
            mNotificationShowCustomCarrierLabel.setEnabled(true);
        }
        mNotificationShowCustomCarrierLabel.setOnPreferenceChangeListener(this);

        // Remove show wifi ssid preferences on wifi only devices
        if (Utils.isWifiOnly(getActivity())) {
            removePreference(PREF_NOTIFICATION_SHOW_WIFI_SSID);
        } else {
            mNotificationShowWifiSsid = (CheckBoxPreference) findPreference(PREF_NOTIFICATION_SHOW_WIFI_SSID);
            mNotificationShowWifiSsid.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.NOTIFICATION_SHOW_WIFI_SSID, 0) == 1);
            mNotificationShowWifiSsid.setOnPreferenceChangeListener(this);
        }

        mNotificationCwLabelColor = (ColorPickerPreference) findPreference(PREF_NOTIFICATION_CW_LABEL_COLOR);
        int intColor = Settings.System.getInt(mResolver,
                Settings.System.NOTIFICATION_CARRIER_WIFI_LABEL_COLOR, 0xff999999);
        mNotificationCwLabelColor.setNewPreviewColor(intColor);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mNotificationCwLabelColor.setSummary(hexColor);
        mNotificationCwLabelColor.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.status_bar_expanded_notif_label_style, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notif_drawer_cw_label_android_default:
                Settings.System.putInt(mResolver, Settings.System.NOTIFICATION_SHOW_CUSTOM_CARRIER_LABEL, 0);
                Settings.System.putInt(mResolver, Settings.System.NOTIFICATION_SHOW_WIFI_SSID, 0);
                Settings.System.putInt(mResolver, Settings.System.NOTIFICATION_CARRIER_WIFI_LABEL_COLOR, 0xff999999);
                refreshSettings();
                return true;
            case R.id.notif_drawer_cw_label_darkkat_default:
                Settings.System.putInt(mResolver, Settings.System.NOTIFICATION_SHOW_CUSTOM_CARRIER_LABEL, 1);
                Settings.System.putInt(mResolver, Settings.System.NOTIFICATION_SHOW_WIFI_SSID, 1);
                Settings.System.putInt(mResolver, Settings.System.NOTIFICATION_CARRIER_WIFI_LABEL_COLOR, 0xffff0000);
                refreshSettings();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNotificationShowCustomCarrierLabel) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.NOTIFICATION_SHOW_CUSTOM_CARRIER_LABEL, value ? 1 : 0);
            Intent i = new Intent();
            i.setAction("com.android.settings.LABEL_VISIBILITY_CHANGED");
            getActivity().sendBroadcast(i);
            return true;
        } else if (preference == mNotificationShowWifiSsid) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.NOTIFICATION_SHOW_WIFI_SSID, value ? 1 : 0);
            Intent i = new Intent();
            i.setAction("com.android.settings.LABEL_VISIBILITY_CHANGED");
            getActivity().sendBroadcast(i);
            return true;
        } else if (preference == mNotificationCwLabelColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.NOTIFICATION_CARRIER_WIFI_LABEL_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
