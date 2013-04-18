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
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class WakeupOptions extends SettingsPreferenceFragment {

    private static final String KEY_VOLUME_WAKE = "pref_volume_wake";
    private static final String KEY_PLUGGED_UNPLUGGED_WAKE = "plugged_unplugged_wake";

    private CheckBoxPreference mVolumeWake;
    private CheckBoxPreference mPluggedUnpluggedWake;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.wakeup_options);

        mVolumeWake = (CheckBoxPreference) findPreference(KEY_VOLUME_WAKE);
        mVolumeWake.setChecked(Settings.System.getInt(resolver,
                Settings.System.VOLUME_WAKE_SCREEN, 0) == 1);

        mPluggedUnpluggedWake = (CheckBoxPreference) findPreference(KEY_PLUGGED_UNPLUGGED_WAKE);
        mPluggedUnpluggedWake.setChecked(Settings.System.getInt(resolver,
                Settings.System.KEY_PLUGGED_UNPLUGGED_WAKE, 1) == 1);

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mVolumeWake) {
            Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_WAKE_SCREEN,
                    mVolumeWake.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mPluggedUnpluggedWake) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.KEY_PLUGGED_UNPLUGGED_WAKE,
                    mPluggedUnpluggedWake.isChecked() ? 1 : 0);
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
