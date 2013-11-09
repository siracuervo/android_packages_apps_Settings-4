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
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.darkjelly.colorpicker.ColorPickerPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StatusBarSignalWifiStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener { 

    private static final String TAG = "StatusBarSignalWifiStyle";

    private static final String PREF_STAT_BAR_CAT_SIGNAL_STRENGTH = "status_bar_cat_signal_strength";
    private static final String PREF_STAT_BAR_CAT_SIGNAL_NETWORK_TYPE = "status_bar_cat_signal_network_type";
    private static final String PREF_STAT_BAR_CAT_SIGNAL_ACTIVITY = "status_bar_cat_signal_activity";
    private static final String PREF_STAT_BAR_CAT_WIFI_STRENGTH = "status_bar_cat_wifi_strength";
    private static final String PREF_STAT_BAR_CAT_WIFI_ACTIVITY = "status_bar_cat_wifi_activity";
    private static final String PREF_ENABLE_DEFAULTS = "status_bar_signal_wifi_defaults";
    private static final String PREF_STAT_BAR_SIGNAL_NORMAL_COLOR = "status_bar_signal_normal_color";
    private static final String PREF_STAT_BAR_SIGNAL_CONNECTED_COLOR = "status_bar_signal_connected_color";
    private static final String PREF_STAT_BAR_NETWORK_TYPE_NORMAL_COLOR = "status_bar_network_type_normal_color";
    private static final String PREF_STAT_BAR_NETWORK_TYPE_CONNECTED_COLOR = "status_bar_network_type_connected_color";
    private static final String PREF_STAT_BAR_SIGNAL_ACTIVITY_NORMAL_COLOR = "status_bar_signal_activity_normal_color";
    private static final String PREF_STAT_BAR_SIGNAL_ACTIVITY_CONNECTED_COLOR = "status_bar_signal_activity_connected_color";
    private static final String PREF_STAT_BAR_WIFI_ICON_NORMAL_COLOR = "status_bar_wifi_icon_normal_color";
    private static final String PREF_STAT_BAR_WIFI_ICON_CONNECTED_COLOR = "status_bar_wifi_icon_connected_color";
    private static final String PREF_STAT_BAR_WIFI_ACTIVITY_NORMAL_COLOR = "status_bar_wifi_activity_normal_color";
    private static final String PREF_STAT_BAR_WIFI_ACTIVITY_CONNECTED_COLOR = "status_bar_wifi_activity_connected_color";

    private ListPreference mEnableDefaults;
    private ColorPickerPreference mSignalNormalColor;
    private ColorPickerPreference mSignalConnectedColor;
    private ColorPickerPreference mNetworkTypeNormalColor;
    private ColorPickerPreference mNetworkTypeConnectedColor;
    private ColorPickerPreference mSignalActivityNormalColor;
    private ColorPickerPreference mSignalActivityConnectedColor;
    private ColorPickerPreference mWifiIconNormalColor;
    private ColorPickerPreference mWifiIconConnectedColor;
    private ColorPickerPreference mWifiActivityNormalColor;
    private ColorPickerPreference mWifiActivityConnectedColor;

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

        addPreferencesFromResource(R.xml.status_bar_signal_wifi_style);
        mResolver = getActivity().getContentResolver();

        boolean isDefaultEnabled = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_SIGNAL_WIFI_STYLE_ENABLE_DEFAULTS, 0) != 2;
        int signalStrengthStatus = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_SIGNAL_TEXT, 0);

        mEnableDefaults = (ListPreference) findPreference(PREF_ENABLE_DEFAULTS);
        int enableDefaults = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_SIGNAL_WIFI_STYLE_ENABLE_DEFAULTS, 0);
        mEnableDefaults.setValue(String.valueOf(enableDefaults));
        mEnableDefaults.setSummary(mEnableDefaults.getEntry());
        mEnableDefaults.setOnPreferenceChangeListener(this);

        PreferenceCategory categorySignalStrength = (PreferenceCategory) findPreference(PREF_STAT_BAR_CAT_SIGNAL_STRENGTH);
        PreferenceCategory categorySignalNetworkType = (PreferenceCategory) findPreference(PREF_STAT_BAR_CAT_SIGNAL_NETWORK_TYPE);
        PreferenceCategory categorySignalActivity = (PreferenceCategory) findPreference(PREF_STAT_BAR_CAT_SIGNAL_ACTIVITY);
        PreferenceCategory categoryWifiStrength = (PreferenceCategory) findPreference(PREF_STAT_BAR_CAT_WIFI_STRENGTH);
        PreferenceCategory categoryWifiActivity = (PreferenceCategory) findPreference(PREF_STAT_BAR_CAT_WIFI_ACTIVITY);

        mSignalNormalColor = (ColorPickerPreference) findPreference(PREF_STAT_BAR_SIGNAL_NORMAL_COLOR);
        mSignalConnectedColor = (ColorPickerPreference) findPreference(PREF_STAT_BAR_SIGNAL_CONNECTED_COLOR);
        mNetworkTypeNormalColor = (ColorPickerPreference) findPreference(PREF_STAT_BAR_NETWORK_TYPE_NORMAL_COLOR);
        mNetworkTypeConnectedColor = (ColorPickerPreference) findPreference(PREF_STAT_BAR_NETWORK_TYPE_CONNECTED_COLOR);
        mSignalActivityNormalColor = (ColorPickerPreference) findPreference(PREF_STAT_BAR_SIGNAL_ACTIVITY_NORMAL_COLOR);
        mSignalActivityConnectedColor = (ColorPickerPreference) findPreference(PREF_STAT_BAR_SIGNAL_ACTIVITY_CONNECTED_COLOR);
        mWifiIconNormalColor = (ColorPickerPreference) findPreference(PREF_STAT_BAR_WIFI_ICON_NORMAL_COLOR);
        mWifiIconConnectedColor = (ColorPickerPreference) findPreference(PREF_STAT_BAR_WIFI_ICON_CONNECTED_COLOR);
        mWifiActivityNormalColor = (ColorPickerPreference) findPreference(PREF_STAT_BAR_WIFI_ACTIVITY_NORMAL_COLOR);
        mWifiActivityConnectedColor = (ColorPickerPreference) findPreference(PREF_STAT_BAR_WIFI_ACTIVITY_CONNECTED_COLOR);

        if (!isDefaultEnabled) {
            int intColor;
            String hexColor;

            if (signalStrengthStatus == 0) {
                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_SIGNAL_NORMAL_COLOR, 0xffaaaaaa); 
                mSignalNormalColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mSignalNormalColor.setSummary(hexColor);
                mSignalNormalColor.setOnPreferenceChangeListener(this);

                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_SIGNAL_CONNECTED_COLOR, 0xff33b5e5); 
                mSignalConnectedColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mSignalConnectedColor.setSummary(hexColor);
                mSignalConnectedColor.setOnPreferenceChangeListener(this);

                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_NETWORK_TYPE_NORMAL_COLOR, 0xffaaaaaa); 
                mNetworkTypeNormalColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mNetworkTypeNormalColor.setSummary(hexColor);
                mNetworkTypeNormalColor.setOnPreferenceChangeListener(this);

                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_NETWORK_TYPE_CONNECTED_COLOR, 0xff33b5e5); 
                mNetworkTypeConnectedColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mNetworkTypeConnectedColor.setSummary(hexColor);
                mNetworkTypeConnectedColor.setOnPreferenceChangeListener(this);

                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_SIGNAL_ACTIVITY_NORMAL_COLOR, 0xffaaaaaa); 
                mSignalActivityNormalColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mSignalActivityNormalColor.setSummary(hexColor);
                mSignalActivityNormalColor.setOnPreferenceChangeListener(this);

                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_SIGNAL_ACTIVITY_CONNECTED_COLOR, 0xff33b5e5); 
                mSignalActivityConnectedColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mSignalActivityConnectedColor.setSummary(hexColor);
                mSignalActivityConnectedColor.setOnPreferenceChangeListener(this);
            } else if (signalStrengthStatus == 1) {
                categorySignalNetworkType.removePreference(mNetworkTypeNormalColor);
                categorySignalNetworkType.removePreference(mNetworkTypeConnectedColor);
                categorySignalActivity.removePreference(mSignalActivityNormalColor);
                categorySignalActivity.removePreference(mSignalActivityConnectedColor);
                removePreference(PREF_STAT_BAR_CAT_SIGNAL_NETWORK_TYPE);
                removePreference(PREF_STAT_BAR_CAT_SIGNAL_ACTIVITY);
            } else {
                categorySignalStrength.removePreference(mSignalNormalColor);
                categorySignalStrength.removePreference(mSignalConnectedColor);
                categorySignalNetworkType.removePreference(mNetworkTypeNormalColor);
                categorySignalNetworkType.removePreference(mNetworkTypeConnectedColor);
                categorySignalActivity.removePreference(mSignalActivityNormalColor);
                categorySignalActivity.removePreference(mSignalActivityConnectedColor);
                removePreference(PREF_STAT_BAR_CAT_SIGNAL_STRENGTH);
                removePreference(PREF_STAT_BAR_CAT_SIGNAL_NETWORK_TYPE);
                removePreference(PREF_STAT_BAR_CAT_SIGNAL_ACTIVITY);
            }
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_WIFI_ICON_NORMAL_COLOR, 0xffaaaaaa); 
            mWifiIconNormalColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mWifiIconNormalColor.setSummary(hexColor);
            mWifiIconNormalColor.setOnPreferenceChangeListener(this);

            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_WIFI_ICON_CONNECTED_COLOR, 0xff33b5e5); 
            mWifiIconConnectedColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mWifiIconConnectedColor.setSummary(hexColor);
            mWifiIconConnectedColor.setOnPreferenceChangeListener(this);

            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_WIFI_ACTIVITY_NORMAL_COLOR, 0xffaaaaaa); 
            mWifiActivityNormalColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mWifiActivityNormalColor.setSummary(hexColor);
            mWifiActivityNormalColor.setOnPreferenceChangeListener(this);

            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_WIFI_ACTIVITY_CONNECTED_COLOR, 0xff33b5e5); 
            mWifiActivityConnectedColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mWifiActivityConnectedColor.setSummary(hexColor);
            mWifiActivityConnectedColor.setOnPreferenceChangeListener(this);
        } else {
            categorySignalStrength.removePreference(mSignalNormalColor);
            categorySignalStrength.removePreference(mSignalConnectedColor);
            categorySignalNetworkType.removePreference(mNetworkTypeNormalColor);
            categorySignalNetworkType.removePreference(mNetworkTypeConnectedColor);
            categorySignalActivity.removePreference(mSignalActivityNormalColor);
            categorySignalActivity.removePreference(mSignalActivityConnectedColor);
            categoryWifiStrength.removePreference(mWifiIconNormalColor);
            categoryWifiStrength.removePreference(mWifiIconConnectedColor);
            categoryWifiActivity.removePreference(mWifiActivityNormalColor);
            categoryWifiActivity.removePreference(mWifiActivityConnectedColor);
            removePreference(PREF_STAT_BAR_CAT_SIGNAL_STRENGTH);
            removePreference(PREF_STAT_BAR_CAT_SIGNAL_NETWORK_TYPE);
            removePreference(PREF_STAT_BAR_CAT_SIGNAL_ACTIVITY);
            removePreference(PREF_STAT_BAR_CAT_WIFI_STRENGTH);
            removePreference(PREF_STAT_BAR_CAT_WIFI_ACTIVITY);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String hex;
        int intHex;

        if (preference == mEnableDefaults) {
            int enableDefaults = Integer.valueOf((String) newValue);
            int index = mEnableDefaults.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_SIGNAL_WIFI_STYLE_ENABLE_DEFAULTS, enableDefaults);
            mEnableDefaults.setSummary(mEnableDefaults.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mSignalNormalColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_SIGNAL_NORMAL_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mSignalConnectedColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_SIGNAL_CONNECTED_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mNetworkTypeNormalColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_TYPE_NORMAL_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mNetworkTypeConnectedColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NETWORK_TYPE_CONNECTED_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mSignalActivityNormalColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_SIGNAL_ACTIVITY_NORMAL_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mSignalActivityConnectedColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_SIGNAL_ACTIVITY_CONNECTED_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mWifiIconNormalColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_WIFI_ICON_NORMAL_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mWifiIconConnectedColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_WIFI_ICON_CONNECTED_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mWifiActivityNormalColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_WIFI_ACTIVITY_NORMAL_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mWifiActivityConnectedColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_WIFI_ACTIVITY_CONNECTED_COLOR, intHex);
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
