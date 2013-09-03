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
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StatusBarStyle extends SettingsPreferenceFragment {

    private static final String TAG = "StatusBarStyle";

    private PreferenceScreen mStatusBarClockStyle;
    private PreferenceScreen mStatusBarBatteryStatusStyle;
    private PreferenceScreen mStatusBarBatteryBarStyle;
    private PreferenceScreen StatusBarNetworkSpeedStyle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_style);
        ContentResolver resolver = getActivity().getContentResolver();

        mStatusBarClockStyle = (PreferenceScreen) findPreference("status_bar_clock_style");
        mStatusBarBatteryStatusStyle = (PreferenceScreen) findPreference("status_bar_battery_status_style");
        mStatusBarBatteryBarStyle = (PreferenceScreen) findPreference("status_bar_battery_bar_style");
        StatusBarNetworkSpeedStyle = (PreferenceScreen) findPreference("status_bar_network_speed_style");

        boolean isClockEnabled = Settings.System.getInt(resolver,
               Settings.System.STATUS_BAR_SHOW_CLOCK, 1) == 1;

        boolean isDateEnabled = Settings.System.getInt(resolver,
               Settings.System.STATUS_BAR_SHOW_DATE, 0) == 1;

        boolean isBatteryStatusEnabled = Settings.System.getInt(resolver,
               Settings.System.STATUS_BAR_SHOW_BATTERY_STATUS, 1) == 1;

        boolean isBatteryBarEnabled = Settings.System.getInt(resolver,
               Settings.System.STATUS_BAR_SHOW_BATTERY_BAR, 0) == 1;

        boolean isNetworkSpeedIndicatorEnabled = Settings.System.getInt(resolver,
               Settings.System.STATUS_BAR_ENABLE_NETWORK_SPEED_INDICATOR, 0) == 1;

        if (!isClockEnabled && !isDateEnabled) {
            mStatusBarClockStyle.setEnabled(false);
        } else {
            mStatusBarClockStyle.setEnabled(true);
        }

        if (isBatteryStatusEnabled) {
            mStatusBarBatteryStatusStyle.setEnabled(true);
        } else {
            mStatusBarBatteryStatusStyle.setEnabled(false);
        }

        if (isBatteryBarEnabled) {
            mStatusBarBatteryBarStyle.setEnabled(true);
        } else {
            mStatusBarBatteryBarStyle.setEnabled(false);
        }

        if (isNetworkSpeedIndicatorEnabled) {
            StatusBarNetworkSpeedStyle.setEnabled(true);
        } else {
            StatusBarNetworkSpeedStyle.setEnabled(false);
        }
    }
}
