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

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.darkjelly.colorpicker.ColorPickerPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StatusBarNetworkSpeedStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "StatusBarNetworkSpeedStyle";

    private static final String PREF_NETWORK_SPEED_DOWNLOAD = "network_speed_show_download";
    private static final String PREF_NETWORK_SPEED_UPLOAD = "network_speed_show_upload";
    private static final String PREF_NETWORK_SPEED_BIT_BYTE = "network_speed_bit_byte";
    private static final String PREF_NETWORK_SPEED_HIDE_TRAFFIC = "network_speed_hide_traffic";
    private static final String PREF_NETWORK_SPEED_DOWNLOAD_COLOR = "network_speed_download_color";
    private static final String PREF_NETWORK_SPEED_UPLOAD_COLOR = "network_speed_upload_color";

    private CheckBoxPreference mNetworkSpeedDl;
    private CheckBoxPreference mNetworkSpeedUl;
    private CheckBoxPreference mNetworkSpeedBitByte;
    private CheckBoxPreference mNetworkSpeedHide;
    private ColorPickerPreference mNetworkSpeedDownColor;
    private ColorPickerPreference mNetworkSpeedUpColor;

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

        int intColor;
        String hexColor;

        addPreferencesFromResource(R.xml.status_bar_network_speed);

        mNetworkSpeedDl = (CheckBoxPreference) findPreference(PREF_NETWORK_SPEED_DOWNLOAD);
        mNetworkSpeedDl.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_DOWNLOAD, 1) == 1));

        mNetworkSpeedUl = (CheckBoxPreference) findPreference(PREF_NETWORK_SPEED_UPLOAD);
        mNetworkSpeedUl.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_UPLOAD, 1) == 1));

        mNetworkSpeedBitByte = (CheckBoxPreference) findPreference(PREF_NETWORK_SPEED_BIT_BYTE);
        mNetworkSpeedBitByte.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_NETWORK_SPEED_BIT_BYTE, 0) == 1));

        mNetworkSpeedHide = (CheckBoxPreference) findPreference(PREF_NETWORK_SPEED_HIDE_TRAFFIC);
        mNetworkSpeedHide.setChecked((Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_NETWORK_SPEED_HIDE_TRAFFIC, 1) == 1));

        mNetworkSpeedDownColor = (ColorPickerPreference) findPreference(PREF_NETWORK_SPEED_DOWNLOAD_COLOR);
        mNetworkSpeedDownColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_NETWORK_SPEED_DOWNLOAD_COLOR, 0xff33b5e5); 
        mNetworkSpeedDownColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mNetworkSpeedDownColor.setSummary(hexColor);

        mNetworkSpeedUpColor = (ColorPickerPreference) findPreference(PREF_NETWORK_SPEED_UPLOAD_COLOR);
        mNetworkSpeedUpColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_NETWORK_SPEED_UPLOAD_COLOR, 0xff33b5e5); 
        mNetworkSpeedUpColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mNetworkSpeedUpColor.setSummary(hexColor);

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
            case R.id.reset_network_speed:
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_DOWNLOAD, 1);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_UPLOAD, 1);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_NETWORK_SPEED_BIT_BYTE, 0);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_NETWORK_SPEED_HIDE_TRAFFIC, 1);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_NETWORK_SPEED_DOWNLOAD_COLOR, 0xff33b5e5);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_NETWORK_SPEED_UPLOAD_COLOR, 0xff33b5e5);
                refreshSettings();
                return true;
            case R.id.network_speed_dark_jelly_default:
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_DOWNLOAD, 1);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_UPLOAD, 1);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_NETWORK_SPEED_BIT_BYTE, 1);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_NETWORK_SPEED_HIDE_TRAFFIC, 1);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_NETWORK_SPEED_DOWNLOAD_COLOR, 0xffff0000);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_NETWORK_SPEED_UPLOAD_COLOR, 0xffff0000);
                refreshSettings();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNetworkSpeedDownColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_NETWORK_SPEED_DOWNLOAD_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference ==  mNetworkSpeedUpColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_NETWORK_SPEED_UPLOAD_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

       if (preference == mNetworkSpeedDl) {
            value = mNetworkSpeedDl.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_DOWNLOAD, value ? 1 : 0);
            return true;
        } else if (preference == mNetworkSpeedUl) {
            value = mNetworkSpeedUl.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_UPLOAD, value ? 1 : 0);
            return true;
        } else if (preference == mNetworkSpeedBitByte) {
            value = mNetworkSpeedBitByte.isChecked();
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_NETWORK_SPEED_BIT_BYTE, value ? 1 : 0);
            return true;
        } else if (preference == mNetworkSpeedHide) {
            value = mNetworkSpeedHide.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_NETWORK_SPEED_HIDE_TRAFFIC, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
