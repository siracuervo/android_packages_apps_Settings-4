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
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import com.android.internal.util.darkkat.DeviceUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StatusBarExpandedQsOptions extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String TAG = "StatusBarExpandedQsOptions";

    private static final String PREF_QUICK_PULLDOWN = "qs_quick_pulldown";
    private static final String PREF_TILES_PER_ROW = "tiles_per_row";
    private static final String PREF_TILES_PER_ROW_DUPLICATE_LANDSCAPE = "tiles_per_row_duplicate_landscape";

    private ListPreference mQuickPulldown;
    private ListPreference mTilesPerRow;
    private CheckBoxPreference mDuplicateColumnsLandscape;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_expanded_qs_options);

        mResolver = getActivity().getContentResolver();

        // Remove uneeded preferences on wifi only devices
        if (DeviceUtils.isPhone(getActivity())) {
            mQuickPulldown = (ListPreference) findPreference(PREF_QUICK_PULLDOWN);
            int quickPulldown = Settings.System.getInt(mResolver,
                    Settings.System.QS_QUICK_PULLDOWN, 0);
            mQuickPulldown.setValue(String.valueOf(quickPulldown));
            mQuickPulldown.setSummary(mQuickPulldown.getEntry());
            mQuickPulldown.setOnPreferenceChangeListener(this);

            mDuplicateColumnsLandscape =
                (CheckBoxPreference) findPreference(PREF_TILES_PER_ROW_DUPLICATE_LANDSCAPE);
            mDuplicateColumnsLandscape.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.QUICK_TILES_PER_ROW_DUPLICATE_LANDSCAPE, 1) == 1);
            mDuplicateColumnsLandscape.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_QUICK_PULLDOWN);
            removePreference(PREF_TILES_PER_ROW_DUPLICATE_LANDSCAPE);
        }

        mTilesPerRow = (ListPreference) findPreference(PREF_TILES_PER_ROW);
        int tilesPerRow = Settings.System.getInt(mResolver,
                Settings.System.QUICK_TILES_PER_ROW, 3);
        mTilesPerRow.setValue(String.valueOf(tilesPerRow));
        mTilesPerRow.setSummary(mTilesPerRow.getEntry());
        mTilesPerRow.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mQuickPulldown) {
            int quickPulldown = Integer.valueOf((String) newValue);
            int index = mQuickPulldown.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.QS_QUICK_PULLDOWN, quickPulldown);
            mQuickPulldown.setSummary(mQuickPulldown.getEntries()[index]);
            return true;
        } else if (preference == mTilesPerRow) {
            int index = mTilesPerRow.findIndexOfValue((String) newValue);
            int value = Integer.valueOf((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.QUICK_TILES_PER_ROW, value);
            mTilesPerRow.setSummary(mTilesPerRow.getEntries()[index]);
            return true;
        } else if (preference == mDuplicateColumnsLandscape) {
            Settings.System.putInt(mResolver, Settings.System.QUICK_TILES_PER_ROW_DUPLICATE_LANDSCAPE,
                    (Boolean) newValue ? 1 : 0);
            return true;
        }
        return false;
    }
}
