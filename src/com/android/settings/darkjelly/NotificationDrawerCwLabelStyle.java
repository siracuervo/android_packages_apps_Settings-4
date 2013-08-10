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

package com.android.settings.darkjelly;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference; 
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.Spannable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.settings.darkjelly.colorpicker.ColorPickerPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SeekBarPreference;

public class NotificationDrawerCwLabelStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "NotificationDrawerCwLabelStyle";

    private static final String PREF_NOTIFICATION_CW_LABEL_COLOR = "notification_carrier_wifi_label_color";
    private static final String PREF_CUSTOM_CARRIER_LABEL = "custom_carrier_label";
    private static final String PREF_NOTIFICATION_SHOW_WIFI_SSID = "notification_show_wifi_ssid";

    private ColorPickerPreference mNotificationCwLabelColor;
    private Preference mCustomLabel;
    private CheckBoxPreference mNotificationShowWifiSsid;

    private ContentResolver mResolver;

    private String mCustomLabelText = null;

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

        addPreferencesFromResource(R.xml.notification_drawer_cw_label_style);
        mResolver = getActivity().getContentResolver();
 
        mNotificationCwLabelColor = (ColorPickerPreference) findPreference(PREF_NOTIFICATION_CW_LABEL_COLOR);
        int intColor = Settings.System.getInt(mResolver,
                Settings.System.NOTIFICATION_CARRIER_WIFI_LABEL_COLOR, 0xff999999);
        mNotificationCwLabelColor.setNewPreviewColor(intColor);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mNotificationCwLabelColor.setSummary(hexColor);
        mNotificationCwLabelColor.setOnPreferenceChangeListener(this);

        mCustomLabel = findPreference(PREF_CUSTOM_CARRIER_LABEL);

        mNotificationShowWifiSsid = (CheckBoxPreference) findPreference(PREF_NOTIFICATION_SHOW_WIFI_SSID);
        mNotificationShowWifiSsid.setChecked(Settings.System.getInt(mResolver,
                Settings.System.NOTIFICATION_SHOW_WIFI_SSID, 0) == 1);
        mNotificationShowWifiSsid.setOnPreferenceChangeListener(this);

        updateCustomLabelTextSummary();

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.notification_drawer_cw_label_style, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset_notification_drawer_cw_label_style:
                Settings.System.putInt(mResolver, Settings.System.NOTIFICATION_CARRIER_WIFI_LABEL_COLOR, 0xff999999);
                Settings.System.putString(mResolver, Settings.System.CUSTOM_CARRIER_LABEL, "");
                Settings.System.putInt(mResolver, Settings.System.NOTIFICATION_SHOW_WIFI_SSID, 0);
                refreshSettings();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNotificationCwLabelColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.NOTIFICATION_CARRIER_WIFI_LABEL_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mNotificationShowWifiSsid) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.NOTIFICATION_SHOW_WIFI_SSID, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mCustomLabel) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.custom_carrier_label_title);
            alert.setMessage(R.string.custom_carrier_label_explain);

            // Set an EditText view to get user input
            final EditText input = new EditText(getActivity());
            input.setText(mCustomLabelText != null ? mCustomLabelText : "");
            alert.setView(input);
            alert.setPositiveButton(getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = ((Spannable) input.getText()).toString();
                    Settings.System.putString(mResolver,
                            Settings.System.CUSTOM_CARRIER_LABEL, value);
                    updateCustomLabelTextSummary();

                    Intent i = new Intent();
                    i.setAction("com.android.settings.LABEL_CHANGED");
                    getActivity().sendBroadcast(i);
                }
            });
            alert.setNegativeButton(getResources().getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();

        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void updateCustomLabelTextSummary() {
        mCustomLabelText = Settings.System.getString(mResolver,
                Settings.System.CUSTOM_CARRIER_LABEL);
        if (mCustomLabelText == null || mCustomLabelText.length() == 0) {
            mCustomLabel.setSummary(R.string.custom_carrier_label_notset);
        } else {
            mCustomLabel.setSummary(mCustomLabelText);
        }
    }

}
