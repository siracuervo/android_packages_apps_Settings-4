/*
 * Copyright (C) 2013 DarkKat
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

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarNetworkSpeedStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "StatusBarNetworkSpeedStyle";

    private static final String PREF_NETWORK_SPEED_DOWNLOAD = "network_speed_show_download";
    private static final String PREF_NETWORK_SPEED_UPLOAD = "network_speed_show_upload";
    private static final String PREF_TRAFFIC_SUMMARY = "traffic_summary";
    private static final String PREF_NETWORK_SPEED_BIT_BYTE = "network_speed_bit_byte";
    private static final String PREF_NETWORK_SPEED_HIDE_TRAFFIC = "network_speed_hide_traffic";
    private static final String PREF_NETWORK_SPEED_DOWNLOAD_COLOR = "network_speed_download_color";
    private static final String PREF_NETWORK_SPEED_UPLOAD_COLOR = "network_speed_upload_color";

    private CheckBoxPreference mNetworkSpeedDl;
    private CheckBoxPreference mNetworkSpeedUl;
    private ListPreference mTrafficSummary; 
    private CheckBoxPreference mNetworkSpeedBitByte;
    private CheckBoxPreference mNetworkSpeedHide;
    private ColorPickerPreference mNetworkSpeedDownColor;
    private ColorPickerPreference mNetworkSpeedUpColor;

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

        int intColor = 0xffffffff;
        String hexColor = String.format("#%08x", (0xffffffff & 0xffffffff));

        addPreferencesFromResource(R.xml.status_bar_network_speed);
        mResolver = getActivity().getContentResolver();

        boolean showNetworkSpeedDl = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_DOWNLOAD, 1) == 1;
        boolean showNetworkSpeedUl = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_UPLOAD, 1) == 1;

        mNetworkSpeedDl = (CheckBoxPreference) findPreference(PREF_NETWORK_SPEED_DOWNLOAD);
        mNetworkSpeedDl.setChecked(showNetworkSpeedDl);
        mNetworkSpeedDl.setOnPreferenceChangeListener(this);

        mNetworkSpeedUl = (CheckBoxPreference) findPreference(PREF_NETWORK_SPEED_UPLOAD);
        mNetworkSpeedUl.setChecked(showNetworkSpeedUl);
        mNetworkSpeedUl.setOnPreferenceChangeListener(this);

        mTrafficSummary = (ListPreference) findPreference(PREF_TRAFFIC_SUMMARY);
        mTrafficSummary.setOnPreferenceChangeListener(this);
        int trafficSummary = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_TRAFFIC_SUMMARY, 3000);
        mTrafficSummary.setValue(String.valueOf(trafficSummary));
        mTrafficSummary.setSummary(mTrafficSummary.getEntry());
        mTrafficSummary.setOnPreferenceChangeListener(this);

        mNetworkSpeedBitByte = (CheckBoxPreference) findPreference(PREF_NETWORK_SPEED_BIT_BYTE);
        mNetworkSpeedBitByte.setChecked((Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_SPEED_BIT_BYTE, 0) == 1));
        mNetworkSpeedBitByte.setOnPreferenceChangeListener(this);

        mNetworkSpeedHide = (CheckBoxPreference) findPreference(PREF_NETWORK_SPEED_HIDE_TRAFFIC);
        mNetworkSpeedHide.setChecked((Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_SPEED_HIDE_TRAFFIC, 1) == 1));
        mNetworkSpeedHide.setOnPreferenceChangeListener(this);

        // Remove uneeded preferences depending on enabled states
        mNetworkSpeedDownColor = (ColorPickerPreference) findPreference(PREF_NETWORK_SPEED_DOWNLOAD_COLOR);
        if (showNetworkSpeedDl) {
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_SPEED_DOWNLOAD_COLOR, 0xffffffff); 
            mNetworkSpeedDownColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mNetworkSpeedDownColor.setSummary(hexColor);
            mNetworkSpeedDownColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_NETWORK_SPEED_DOWNLOAD_COLOR);
        }

        // Remove uneeded preferences depending on enabled states
        mNetworkSpeedUpColor = (ColorPickerPreference) findPreference(PREF_NETWORK_SPEED_UPLOAD_COLOR);
        if (showNetworkSpeedUl) {
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_SPEED_UPLOAD_COLOR, 0xffffffff); 
            mNetworkSpeedUpColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mNetworkSpeedUpColor.setSummary(hexColor);
            mNetworkSpeedUpColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_NETWORK_SPEED_UPLOAD_COLOR);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.status_bar_network_speed, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.statusbar_network_speed_android_default:
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_DOWNLOAD, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_UPLOAD, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_TRAFFIC_SUMMARY, 3000);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_BIT_BYTE, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_HIDE_TRAFFIC, 1);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_DOWNLOAD_COLOR, 0xffffffff);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_UPLOAD_COLOR, 0xffffffff);
                refreshSettings();
                return true;
            case R.id.statusbar_network_speed_darkkat_default:
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_DOWNLOAD, 1);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_UPLOAD, 1);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_TRAFFIC_SUMMARY, 3000);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_BIT_BYTE, 1);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_HIDE_TRAFFIC, 1);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_DOWNLOAD_COLOR, 0xffff0000);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_UPLOAD_COLOR, 0xffff0000);
                refreshSettings();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNetworkSpeedDl) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_DOWNLOAD, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mNetworkSpeedUl) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_UPLOAD, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mTrafficSummary) {
            int trafficSummary = Integer.valueOf((String) newValue);
            int index = mTrafficSummary.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_TRAFFIC_SUMMARY, trafficSummary);
            mTrafficSummary.setSummary(mTrafficSummary.getEntries()[index]);
            return true;
        } else if (preference == mNetworkSpeedBitByte) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_BIT_BYTE, value ? 1 : 0);
            return true;
        } else if (preference == mNetworkSpeedHide) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_HIDE_TRAFFIC, value ? 1 : 0);
            return true;
        } else if (preference == mNetworkSpeedDownColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_DOWNLOAD_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference ==  mNetworkSpeedUpColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_UPLOAD_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
