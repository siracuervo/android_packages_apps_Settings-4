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
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StatusBarStyle extends SettingsPreferenceFragment {

    private static final String TAG = "StatusBarStyle";

    private PreferenceScreen mStatusBarBatteryStatusStyle;
    private PreferenceScreen mStatusBarBatteryBarStyle;
    private PreferenceScreen mStatusBarClockStyle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_style);

        mStatusBarBatteryStatusStyle = (PreferenceScreen) findPreference("status_bar_battery_status_style");
        mStatusBarBatteryBarStyle = (PreferenceScreen) findPreference("status_bar_battery_bar_style");
        mStatusBarClockStyle = (PreferenceScreen) findPreference("status_bar_clock_style");


        boolean isBatteryStatusEnabled = Settings.System.getInt(getContentResolver(),
               Settings.System.STATUS_BAR_SHOW_BATTERY_STATUS, 1) == 1;

        boolean isBatteryBarEnabled = Settings.System.getInt(getActivity().getContentResolver(),
               Settings.System.STATUS_BAR_SHOW_BATTERY_BAR, 0) == 1;

        boolean isClockEnabled = Settings.System.getInt(getContentResolver(),
               Settings.System.STATUS_BAR_SHOW_CLOCK, 1) == 1;

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

        if (isClockEnabled) {
            mStatusBarClockStyle.setEnabled(true);
        } else {
            mStatusBarClockStyle.setEnabled(false);
        }
    }
}
