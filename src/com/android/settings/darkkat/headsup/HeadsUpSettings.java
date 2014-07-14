/*
 * Copyright (C) 2014 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.darkkat.headsup;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class HeadsUpSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_HEADS_UP_EXPANDED =
            "heads_up_expanded";
    private static final String PREF_HEADS_UP_SHOW_UPDATE =
            "heads_up_show_update";
    private static final String PREF_HEADS_UP_SNOOZE_TIME =
            "heads_up_snooze_time";
    private static final String PREF_HEADS_UP_TIMEOUT =
            "heads_up_timeout";
    private static final String PREF_HEADS_UP_USE_CUSTOM_TIMEOUT_FS =
            "heads_up_use_custom_timeout_fs";

    protected static final int DEFAULT_TIME_HEADS_UP_SNOOZE = 300000;

    private CheckBoxPreference mHeadsUpExpanded;
    private CheckBoxPreference mHeadsUpShowUpdates;
    private ListPreference mHeadsUpSnoozeTime;
    private ListPreference mTimeout;
    private CheckBoxPreference mUseCustomTimeoutFs;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.heads_up);
        mResolver = getActivity().getContentResolver();

        mHeadsUpExpanded =
                (CheckBoxPreference) findPreference(PREF_HEADS_UP_EXPANDED);
        mHeadsUpExpanded.setChecked(Settings.System.getInt(mResolver,
                Settings.System.HEADS_UP_EXPANDED, 0) == 1);
        mHeadsUpExpanded.setOnPreferenceChangeListener(this);

        mHeadsUpShowUpdates =
                (CheckBoxPreference) findPreference(PREF_HEADS_UP_SHOW_UPDATE);
        mHeadsUpShowUpdates.setChecked(Settings.System.getInt(mResolver,
                Settings.System.HEADS_UP_SHOW_UPDATE, 0) == 1);
        mHeadsUpShowUpdates.setOnPreferenceChangeListener(this);

        mHeadsUpSnoozeTime =
                (ListPreference) findPreference(PREF_HEADS_UP_SNOOZE_TIME);
        int headsUpSnoozeTime = Settings.System.getInt(mResolver,
                Settings.System.HEADS_UP_SNOOZE_TIME,
                DEFAULT_TIME_HEADS_UP_SNOOZE);
        mHeadsUpSnoozeTime.setValue(String.valueOf(headsUpSnoozeTime));
        updateHeadsUpSnoozeTimeSummary(headsUpSnoozeTime);
        mHeadsUpSnoozeTime.setOnPreferenceChangeListener(this);

        mTimeout =
                (ListPreference) findPreference(PREF_HEADS_UP_TIMEOUT);
        int timeout = Settings.System.getInt(mResolver,
                Settings.System.HEADS_UP_TIMEOUT, 4000);
        mTimeout.setValue(String.valueOf(timeout));
        updateHeadsUpTimeoutSummary(timeout);
        mTimeout.setOnPreferenceChangeListener(this);

        mUseCustomTimeoutFs =
                (CheckBoxPreference) findPreference(PREF_HEADS_UP_USE_CUSTOM_TIMEOUT_FS);
        mUseCustomTimeoutFs.setChecked(Settings.System.getInt(mResolver,
                Settings.System.HEADS_UP_USE_CUSTOM_TIMEOUT_FS, 0) == 1);
        mUseCustomTimeoutFs.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int index;

        if (preference == mHeadsUpExpanded) {
            Settings.System.putInt(mResolver,
                    Settings.System.HEADS_UP_EXPANDED,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mHeadsUpShowUpdates) {
            Settings.System.putInt(mResolver,
                    Settings.System.HEADS_UP_SHOW_UPDATE,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mHeadsUpSnoozeTime) {
            int headsUpSnoozeTime = Integer.valueOf((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.HEADS_UP_SNOOZE_TIME,
                    headsUpSnoozeTime);
            updateHeadsUpSnoozeTimeSummary(headsUpSnoozeTime);
            return true;
        } else if (preference == mTimeout) {
            int timeout = Integer.valueOf((String) newValue);
            index = mTimeout.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.HEADS_UP_TIMEOUT, timeout);
            updateHeadsUpTimeoutSummary(timeout);
            return true;
        } else if (preference == mUseCustomTimeoutFs) {
            Settings.System.putInt(mResolver,
                    Settings.System.HEADS_UP_USE_CUSTOM_TIMEOUT_FS,
                    (Boolean) newValue ? 1 : 0);
            return true;
        }
        return false;
    }

    private void updateHeadsUpSnoozeTimeSummary(int value) {
        String summary = value != 0
                ? getResources().getString(R.string.heads_up_snooze_summary, value / 60 / 1000)
                : getResources().getString(R.string.heads_up_snooze_disabled_summary);
        mHeadsUpSnoozeTime.setSummary(summary);
    }

    private void updateHeadsUpTimeoutSummary(int value) {
        String summary = getResources().getString(R.string.heads_up_timeout_summary,
                value / 1000);
        if (value == 0) {
            mTimeout.setSummary(
                    getResources().getString(R.string.heads_up_timeout_never_summary));
        } else {
            mTimeout.setSummary(summary);
        }
    }
}
