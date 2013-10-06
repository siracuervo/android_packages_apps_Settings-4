/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
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
import android.provider.Settings.SettingNotFoundException;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class StatusBar extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "StatusBar";

    private static final String STATUS_BAR_CATEGORY_OPTIONS = "category_status_bar_options";
    private static final String STATUS_BAR_CATEGORY_GENERAL = "status_bar_general";
    private static final String STATUS_BAR_SHOW_CLOCK = "status_bar_show_clock";
    private static final String STATUS_BAR_SHOW_DATE = "status_bar_show_date";
    private static final String STATUS_BAR_SHOW_BATTERY_STATUS = "status_bar_show_battery_status";
    private static final String STATUS_BAR_SHOW_BATTERY_BAR = "status_bar_show_battery_bar";
    private static final String STATUS_BAR_ENABLE_NETWORK_SPEED_INDICATOR = "status_bar_enable_network_speed_indicator";
    private static final String STATUS_BAR_SIGNAL = "status_bar_signal";
    private static final String STATUS_BAR_BRIGHTNESS_CONTROL = "status_bar_brightness_control";
    private static final String STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";

    private CheckBoxPreference mStatusBarShowClock;
    private CheckBoxPreference mStatusBarShowDate;
    private CheckBoxPreference mStatusBarShowBatteryStatus;
    private CheckBoxPreference mStatusBarShowBatteryBar;
    private CheckBoxPreference mStatusBarEnableNetworkSpeed;
    private ListPreference mStatusBarCmSignal;
    private CheckBoxPreference mStatusBarBrightnessControl;
    private CheckBoxPreference mStatusBarNotifCount;

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

        addPreferencesFromResource(R.xml.status_bar);
        mResolver = getActivity().getContentResolver();

        boolean isClockEnabled = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_SHOW_CLOCK, 1) == 1;
        boolean isDateEnabled = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_SHOW_DATE, 0) == 1;
        boolean isBatteryStatusEnabled = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_SHOW_BATTERY_STATUS, 1) == 1;
        boolean isBatteryBarEnabled = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_SHOW_BATTERY_BAR, 0) == 1;
        boolean isNetworkSpeedIndicatorEnabled = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_ENABLE_NETWORK_SPEED_INDICATOR, 0) == 1;

        mStatusBarShowClock = (CheckBoxPreference) findPreference(STATUS_BAR_SHOW_CLOCK);
        mStatusBarShowClock.setChecked(isClockEnabled);
        mStatusBarShowClock.setOnPreferenceChangeListener(this);

        mStatusBarShowDate = (CheckBoxPreference) findPreference(STATUS_BAR_SHOW_DATE);

        mStatusBarShowBatteryStatus = (CheckBoxPreference) findPreference(STATUS_BAR_SHOW_BATTERY_STATUS);
        mStatusBarShowBatteryStatus.setChecked(isBatteryStatusEnabled);
        mStatusBarShowBatteryStatus.setOnPreferenceChangeListener(this);

        mStatusBarShowBatteryBar = (CheckBoxPreference) findPreference(STATUS_BAR_SHOW_BATTERY_BAR);
        mStatusBarShowBatteryBar.setChecked(isBatteryBarEnabled);
        mStatusBarShowBatteryBar.setOnPreferenceChangeListener(this);

        mStatusBarEnableNetworkSpeed = (CheckBoxPreference) findPreference(STATUS_BAR_ENABLE_NETWORK_SPEED_INDICATOR);
        mStatusBarEnableNetworkSpeed.setChecked(isNetworkSpeedIndicatorEnabled);
        mStatusBarEnableNetworkSpeed.setOnPreferenceChangeListener(this);

        mStatusBarCmSignal = (ListPreference) findPreference(STATUS_BAR_SIGNAL);
        int signalStyle = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_SIGNAL_TEXT, 0);
        mStatusBarCmSignal.setValue(String.valueOf(signalStyle));
        mStatusBarCmSignal.setSummary(mStatusBarCmSignal.getEntry());
        mStatusBarCmSignal.setOnPreferenceChangeListener(this);

        mStatusBarBrightnessControl = (CheckBoxPreference) findPreference(STATUS_BAR_BRIGHTNESS_CONTROL);
        mStatusBarBrightnessControl.setChecked(Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, 0) == 1);
        mStatusBarBrightnessControl.setOnPreferenceChangeListener(this);

        try {
            if (Settings.System.getInt(mResolver, Settings.System.SCREEN_BRIGHTNESS_MODE)
                    == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                mStatusBarBrightnessControl.setEnabled(false);
                mStatusBarBrightnessControl.setSummary(R.string.status_bar_toggle_info);
            }
        } catch (SettingNotFoundException e) {
        }

        mStatusBarNotifCount = (CheckBoxPreference) findPreference(STATUS_BAR_NOTIF_COUNT);
        mStatusBarNotifCount.setChecked(Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NOTIF_COUNT, 0) == 1);
        mStatusBarNotifCount.setOnPreferenceChangeListener(this);

        PreferenceCategory statusBarOptionsCategory = (PreferenceCategory) findPreference(STATUS_BAR_CATEGORY_OPTIONS);

        // Handle "Show date" checkbox visibility depending on enabled clock state
        if (isClockEnabled) {
            mStatusBarShowDate.setChecked(isDateEnabled);
            mStatusBarShowDate.setOnPreferenceChangeListener(this);
        } else {
            statusBarOptionsCategory.removePreference(mStatusBarShowDate);
        }

        // Remove uneeded preferences depending on enabled states
        PreferenceScreen statusBarClockDateStyle = (PreferenceScreen) findPreference("status_bar_clock_date_style");
        if (!isClockEnabled) {
            statusBarOptionsCategory.removePreference(statusBarClockDateStyle);
        }

        PreferenceScreen statusBarBatteryStatusStyle = (PreferenceScreen) findPreference("status_bar_battery_status_style");
        if (!isBatteryStatusEnabled) {
            statusBarOptionsCategory.removePreference(statusBarBatteryStatusStyle);
        }

        PreferenceScreen statusBarBatteryBarStyle = (PreferenceScreen) findPreference("status_bar_battery_bar_style");
        if (!isBatteryBarEnabled) {
            statusBarOptionsCategory.removePreference(statusBarBatteryBarStyle);
        }

        PreferenceScreen statusBarNetworkSpeedStyle = (PreferenceScreen) findPreference("status_bar_network_speed_style");
        if (!isNetworkSpeedIndicatorEnabled) {
            statusBarOptionsCategory.removePreference(statusBarNetworkSpeedStyle);
        }

        // Remove uneeded preferences on wifi only devices
        PreferenceCategory generalCategory = (PreferenceCategory) findPreference(STATUS_BAR_CATEGORY_GENERAL);

        if (Utils.isWifiOnly(getActivity())) {
            statusBarOptionsCategory.removePreference(mStatusBarCmSignal);
        }

        if (Utils.isTablet(getActivity())) {
            generalCategory.removePreference(mStatusBarBrightnessControl);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.status_bar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset_statusbar:
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_SHOW_CLOCK, 1);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_SHOW_DATE, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_SHOW_BATTERY_STATUS, 1);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_SHOW_BATTERY_BAR, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_ENABLE_NETWORK_SPEED_INDICATOR, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_SIGNAL_TEXT, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NOTIF_COUNT, 0);
                refreshSettings();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mStatusBarShowClock) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_SHOW_CLOCK, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mStatusBarShowDate) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_SHOW_DATE, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mStatusBarShowBatteryStatus) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_SHOW_BATTERY_STATUS, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mStatusBarShowBatteryBar) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_SHOW_BATTERY_BAR, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mStatusBarEnableNetworkSpeed) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_ENABLE_NETWORK_SPEED_INDICATOR, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mStatusBarCmSignal) {
            int signalStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarCmSignal.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_SIGNAL_TEXT, signalStyle);
            mStatusBarCmSignal.setSummary(mStatusBarCmSignal.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarBrightnessControl) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarNotifCount) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_NOTIF_COUNT, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
