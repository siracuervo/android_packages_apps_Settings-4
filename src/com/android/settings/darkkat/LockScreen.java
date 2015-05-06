/*
 * Copyright (C) 2015 DarkKat
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

import android.os.Bundle;
import android.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class LockScreen extends SettingsPreferenceFragment {
    private static final String PREF_WEATHER =
            "lock_screen_weather_settings";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lock_screen);

        int summaryResId;
        boolean cLockInstalled = true;

        PreferenceScreen weather =
                (PreferenceScreen) findPreference(PREF_WEATHER);
        if (!Utils.isPackageInstalled(getActivity(), "com.cyanogenmod.lockclock")) {
            summaryResId = R.string.lock_clock_missing_summary;
            cLockInstalled = false;
        } else {
            summaryResId = R.string.lock_screen_weather_settings_summary;
        }
        weather.setSummary(summaryResId);
        weather.setEnabled(cLockInstalled);
    }
}
