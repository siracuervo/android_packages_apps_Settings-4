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

import android.content.ContentResolver;
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

    private static final String PREF_ENABLE_THEME_DEFAULT = "status_bar_network_speed_enable_theme_default";
    private static final String PREF_NETWORK_SPEED_DOWNLOAD = "network_speed_show_download";
    private static final String PREF_NETWORK_SPEED_UPLOAD = "network_speed_show_upload";
    private static final String PREF_NETWORK_SPEED_BIT_BYTE = "network_speed_bit_byte";
    private static final String PREF_NETWORK_SPEED_HIDE_TRAFFIC = "network_speed_hide_traffic";
    private static final String PREF_NETWORK_SPEED_DOWNLOAD_COLOR = "network_speed_download_color";
    private static final String PREF_NETWORK_SPEED_UPLOAD_COLOR = "network_speed_upload_color";

    private CheckBoxPreference mEnableThemeDefault;
    private CheckBoxPreference mNetworkSpeedDl;
    private CheckBoxPreference mNetworkSpeedUl;
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

        int intColor;
        String hexColor;

        addPreferencesFromResource(R.xml.status_bar_network_speed);
        mResolver = getActivity().getContentResolver();

        mEnableThemeDefault = (CheckBoxPreference) findPreference(PREF_ENABLE_THEME_DEFAULT);
        mEnableThemeDefault.setChecked(Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_SPEED_ENABLE_THEME_DEFAULT, 1) == 1);
        mEnableThemeDefault.setOnPreferenceChangeListener(this);

        mNetworkSpeedDl = (CheckBoxPreference) findPreference(PREF_NETWORK_SPEED_DOWNLOAD);
        mNetworkSpeedDl.setChecked((Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_DOWNLOAD, 1) == 1));
        mNetworkSpeedDl.setOnPreferenceChangeListener(this);

        mNetworkSpeedUl = (CheckBoxPreference) findPreference(PREF_NETWORK_SPEED_UPLOAD);
        mNetworkSpeedUl.setChecked((Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_UPLOAD, 1) == 1));
        mNetworkSpeedUl.setOnPreferenceChangeListener(this);

        mNetworkSpeedBitByte = (CheckBoxPreference) findPreference(PREF_NETWORK_SPEED_BIT_BYTE);
        mNetworkSpeedBitByte.setChecked((Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_SPEED_BIT_BYTE, 0) == 1));
        mNetworkSpeedBitByte.setOnPreferenceChangeListener(this);

        mNetworkSpeedHide = (CheckBoxPreference) findPreference(PREF_NETWORK_SPEED_HIDE_TRAFFIC);
        mNetworkSpeedHide.setChecked((Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_SPEED_HIDE_TRAFFIC, 1) == 1));
        mNetworkSpeedHide.setOnPreferenceChangeListener(this);

        mNetworkSpeedDownColor = (ColorPickerPreference) findPreference(PREF_NETWORK_SPEED_DOWNLOAD_COLOR);
        intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_SPEED_DOWNLOAD_COLOR, 0xff33b5e5); 
        mNetworkSpeedDownColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mNetworkSpeedDownColor.setSummary(hexColor);
        mNetworkSpeedDownColor.setOnPreferenceChangeListener(this);

        mNetworkSpeedUpColor = (ColorPickerPreference) findPreference(PREF_NETWORK_SPEED_UPLOAD_COLOR);
        intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_SPEED_UPLOAD_COLOR, 0xff33b5e5); 
        mNetworkSpeedUpColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mNetworkSpeedUpColor.setSummary(hexColor);
        mNetworkSpeedUpColor.setOnPreferenceChangeListener(this);

        updatePreferences();
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
            case R.id.statusbar_network_speed_cm_default:
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_DOWNLOAD, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_UPLOAD, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_BIT_BYTE, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_HIDE_TRAFFIC, 1);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_DOWNLOAD_COLOR, 0xff33b5e5);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_UPLOAD_COLOR, 0xff33b5e5);
                refreshSettings();
                return true;
            case R.id.statusbar_network_speed_dark_jelly_default:
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_DOWNLOAD, 1);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_UPLOAD, 1);
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
        if (preference == mEnableThemeDefault) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_ENABLE_THEME_DEFAULT, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mNetworkSpeedDl) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_DOWNLOAD, value ? 1 : 0);
            return true;
        } else if (preference == mNetworkSpeedUl) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_SHOW_UPLOAD, value ? 1 : 0);
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

    public void updatePreferences() {
        boolean isThemeDefaultEnabled = mEnableThemeDefault.isChecked();
        String themeDefaultColorSummary = getResources().getString(R.string.theme_default_color);
        int intColor = 0xff33b5e5;
        String hexColor = String.format("#%08x", (0xffffffff & 0xff33b5e5));

        if (isThemeDefaultEnabled) {
            intColor = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_DOWNLOAD_COLOR, 0xff33b5e5);
            mNetworkSpeedDownColor.setNewPreviewColor(intColor);
            mNetworkSpeedDownColor.setSummary(themeDefaultColorSummary);
            mNetworkSpeedDownColor.setEnabled(false);

            intColor = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_UPLOAD_COLOR, 0xff33b5e5); 
            mNetworkSpeedUpColor.setNewPreviewColor(intColor);
            mNetworkSpeedUpColor.setSummary(themeDefaultColorSummary);
            mNetworkSpeedUpColor.setEnabled(false);
        } else {
            mNetworkSpeedDownColor.setEnabled(true);
            intColor = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_DOWNLOAD_COLOR, 0xff33b5e5);
            mNetworkSpeedDownColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mNetworkSpeedDownColor.setSummary(hexColor);

            mNetworkSpeedUpColor.setEnabled(true);
            intColor = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_NETWORK_SPEED_UPLOAD_COLOR, 0xff33b5e5); 
            mNetworkSpeedUpColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mNetworkSpeedUpColor.setSummary(hexColor);
        }
    }
}
