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

package com.android.settings.darkkat;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class Statusbar extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String KEY_STATUS_BAR_SHOW_CLOCK =
            "status_bar_show_clock";
    private static final String STATUS_BAR_SHOW_DATE =
            "status_bar_show_date";
    private static final String STATUS_BAR_SHOW_BATTERY_STATUS =
            "status_bar_show_battery_status";
    private static final String STATUS_BAR_SHOW_BATTERY_BAR =
            "status_bar_show_battery_bar";
    private static final String KEY_STATUS_BAR_ENABLE_NETWORK_SPEED_INDICATOR =
            "status_bar_enable_network_speed_indicator";
    private static final String KEY_STATUS_BAR_NOTIF_COUNT =
            "status_bar_notif_count";

    private CheckBoxPreference mShowClock;
    private CheckBoxPreference mShowDate;
    private CheckBoxPreference mShowBatteryStatus;
    private CheckBoxPreference mShowBatteryBar;
    private CheckBoxPreference mShowNetworkSpeedIndicator;
    private CheckBoxPreference mNotifCount;

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

        mShowClock = (CheckBoxPreference) findPreference(KEY_STATUS_BAR_SHOW_CLOCK);
        mShowClock.setChecked(isClockEnabled);
        mShowClock.setOnPreferenceChangeListener(this);

        mShowBatteryStatus =
                (CheckBoxPreference) findPreference(STATUS_BAR_SHOW_BATTERY_STATUS);
        mShowBatteryStatus.setChecked(isBatteryStatusEnabled);
        mShowBatteryStatus.setOnPreferenceChangeListener(this);

        mShowBatteryBar =
                (CheckBoxPreference) findPreference(STATUS_BAR_SHOW_BATTERY_BAR);
        mShowBatteryBar.setChecked(isBatteryBarEnabled);
        mShowBatteryBar.setOnPreferenceChangeListener(this);

        mShowNetworkSpeedIndicator =
                (CheckBoxPreference) findPreference(KEY_STATUS_BAR_ENABLE_NETWORK_SPEED_INDICATOR);
        mShowNetworkSpeedIndicator.setChecked(isNetworkSpeedIndicatorEnabled);
        mShowNetworkSpeedIndicator.setOnPreferenceChangeListener(this);

        // Remove uneeded preferences depending on enabled states
        mShowDate = (CheckBoxPreference) findPreference(STATUS_BAR_SHOW_DATE);
        if (isClockEnabled) {
            mShowDate.setChecked(isDateEnabled);
            mShowDate.setOnPreferenceChangeListener(this);
        } else {
            removePreference(STATUS_BAR_SHOW_DATE);
            removePreference("status_bar_clock_date_style");
        }
        if (!isBatteryStatusEnabled) {
            removePreference("status_bar_battery_status_style");
        }
        if (!isBatteryBarEnabled) {
            removePreference("status_bar_battery_bar_style");
        }
        if (!isNetworkSpeedIndicatorEnabled) {
            removePreference("status_bar_network_speed_style");
        }

        mNotifCount =
                (CheckBoxPreference) findPreference(KEY_STATUS_BAR_NOTIF_COUNT);
        mNotifCount.setChecked(Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_NOTIF_COUNT, 0) == 1);
        mNotifCount.setOnPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mShowClock) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_SHOW_CLOCK, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowDate) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_SHOW_DATE, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowBatteryStatus) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_SHOW_BATTERY_STATUS, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowBatteryBar) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_SHOW_BATTERY_BAR, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowNetworkSpeedIndicator) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_ENABLE_NETWORK_SPEED_INDICATOR,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mNotifCount) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_NOTIF_COUNT, value ? 1 : 0);
            return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
