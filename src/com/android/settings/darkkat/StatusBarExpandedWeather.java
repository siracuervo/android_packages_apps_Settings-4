/*
 * Copyright (C) 2014 DarkKat
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
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StatusBarExpandedWeather extends SettingsPreferenceFragment {

    private static final String PREF_WEATHER_OPTIONS =
            "status_bar_expanded_weather_options";
    private static final String PREF_WEATHER_COLORS =
            "status_bar_expanded_weather_colors";

    private PreferenceScreen mWeatherOptions;
    private PreferenceScreen mWeatherColors;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_expanded_weather);

        mWeatherOptions =
                (PreferenceScreen) findPreference(PREF_WEATHER_OPTIONS);
        mWeatherColors =
                (PreferenceScreen) findPreference(PREF_WEATHER_COLORS);

        updatePreferences();
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePreferences();
    }

    private void updatePreferences() {
        boolean weatherEnabled = Settings.System.getInt(getActivity().getContentResolver(),
               Settings.System.STATUS_BAR_EXPANDED_ENABLE_WEATHER, 0) == 1;

        String summary = getResources().getString(R.string.weather_disabled_summary);
        if (weatherEnabled) {
            summary = getResources().getString(R.string.weather_enabled_summary);
        }
        mWeatherOptions.setSummary(summary);
        mWeatherColors.setSummary(summary);
        mWeatherColors.setEnabled(weatherEnabled);
    }
}
