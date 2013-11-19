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
    private static final String TAG = "StatusBar";

    private static final String KEY_STATUS_BAR_SHOW_CLOCK = "status_bar_show_clock";
    private static final String STATUS_BAR_SHOW_DATE = "status_bar_show_date";
    private static final String KEY_STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";

    private CheckBoxPreference mShowClock;
    private CheckBoxPreference mShowDate;
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

        mShowClock = (CheckBoxPreference) findPreference(KEY_STATUS_BAR_SHOW_CLOCK);
        mShowClock.setChecked(isClockEnabled);
        mShowClock.setOnPreferenceChangeListener(this);

        // Remove uneeded preferences if clock is disabled
        mShowDate = (CheckBoxPreference) findPreference(STATUS_BAR_SHOW_DATE);
        if (isClockEnabled) {
            mShowDate.setChecked(isDateEnabled);
            mShowDate.setOnPreferenceChangeListener(this);
        } else {
            removePreference(STATUS_BAR_SHOW_DATE);
            removePreference("status_bar_clock_date_style");
        }

        mNotifCount = (CheckBoxPreference) findPreference(KEY_STATUS_BAR_NOTIF_COUNT);
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
