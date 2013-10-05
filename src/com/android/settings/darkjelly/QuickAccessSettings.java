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

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class QuickAccessSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String PREF_QS_QUICK_ACCESS_LINKED = "qs_quick_access_linked";

    private CheckBoxPreference mQuickAccessLinked;

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

        mResolver = getActivity().getContentResolver();
        addPreferencesFromResource(R.xml.quick_access_settings);

        boolean isQuickAccessLinked = Settings.System.getInt(mResolver,
               Settings.System.QS_QUICK_ACCESS_LINKED, 1) == 1;

        mQuickAccessLinked = (CheckBoxPreference) findPreference(PREF_QS_QUICK_ACCESS_LINKED);
        mQuickAccessLinked.setChecked(isQuickAccessLinked);
        mQuickAccessLinked.setOnPreferenceChangeListener(this);

        // Remove uneeded preferences depending on enabled states
        PreferenceScreen tilePicker = (PreferenceScreen) findPreference("tile_picker");
        if (isQuickAccessLinked) {
            removePreference("tile_picker");
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mQuickAccessLinked) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.QS_QUICK_ACCESS_LINKED, value ? 1 : 0);
            refreshSettings();
            return true;
        }
        return false;
    }
}
