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

import com.android.settings.Utils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class WakeupOptions extends SettingsPreferenceFragment {

    private static final String PREF_HOME_WAKE = "pref_home_wake";
    private static final String PREF_VOLUME_WAKE = "pref_volume_wake";
    private static final String PREF_PLUGGED_UNPLUGGED_WAKE = "plugged_unplugged_wake";

    private CheckBoxPreference mHomeWake;
    private CheckBoxPreference mVolumeWake;
    private CheckBoxPreference mPluggedUnpluggedWake;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.wakeup_options);

        mHomeWake = (CheckBoxPreference) findPreference(PREF_HOME_WAKE);
        mVolumeWake = (CheckBoxPreference) findPreference(PREF_VOLUME_WAKE);
        mPluggedUnpluggedWake = (CheckBoxPreference) findPreference(PREF_PLUGGED_UNPLUGGED_WAKE);

        // Start the wake-up preference handling

        // Home button wake
        if (mHomeWake != null) {
            if (!getResources().getBoolean(R.bool.config_show_homeWake)) {
                getPreferenceScreen().removePreference(mHomeWake);
            } else {
                mHomeWake.setChecked(Settings.System.getInt(resolver,
                        Settings.System.HOME_WAKE_SCREEN, 1) == 1);
            }
        }

        // Volume rocker wake
        if (mVolumeWake != null) {
            if (!getResources().getBoolean(R.bool.config_show_volumeRockerWake)
                    || !Utils.hasVolumeRocker(getActivity())) {
                getPreferenceScreen().removePreference(mVolumeWake);
            } else {
                mVolumeWake.setChecked(Settings.System.getInt(resolver,
                        Settings.System.VOLUME_WAKE_SCREEN, 0) == 1);
            }
        }

        // Plugged unplugged wake
        if (mPluggedUnpluggedWake != null) {
            if (!getResources().getBoolean(R.bool.config_show_PluggedUnpluggedWake)) {
                getPreferenceScreen().removePreference(mPluggedUnpluggedWake);
            } else {
                mPluggedUnpluggedWake.setChecked(Settings.System.getInt(resolver,
                        Settings.System.KEY_PLUGGED_UNPLUGGED_WAKE, 1) == 1);
            }
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mHomeWake) {
            Settings.System.putInt(getContentResolver(), Settings.System.HOME_WAKE_SCREEN,
                    mVolumeWake.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mVolumeWake) {
            Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_WAKE_SCREEN,
                    mVolumeWake.isChecked() ? 1 : 0);
            return true;
        } else if (preference == mPluggedUnpluggedWake) {
            Settings.System.putInt(getContentResolver(), Settings.System.KEY_PLUGGED_UNPLUGGED_WAKE,
                    mPluggedUnpluggedWake.isChecked() ? 1 : 0);
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
