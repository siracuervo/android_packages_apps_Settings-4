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

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StatusBarExpandedNotificationDrawerWeather extends SettingsPreferenceFragment {

    private static final String PREF_WEATHER_OPTIONS =
            "weather_options";
    private static final String PREF_WEATHER_CLICK_ACTIONS =
            "weather_click_actions";
    private static final String PREF_WEATHER_COLORS =
            "weather_colors";

    private PreferenceScreen mWeatherOptions;
    private PreferenceScreen mWeatherClickActions;
    private PreferenceScreen mWeatherColors;

    private ContentResolver mResolver;

    private ContentObserver mWeatherStyleObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            updatePreferences();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_expanded_notification_drawer_weather);

        mResolver = getActivity().getContentResolver();

        mWeatherOptions =
                (PreferenceScreen) findPreference(PREF_WEATHER_OPTIONS);
        mWeatherClickActions =
                (PreferenceScreen) findPreference(PREF_WEATHER_CLICK_ACTIONS);
        mWeatherColors =
                (PreferenceScreen) findPreference(PREF_WEATHER_COLORS);

        updatePreferences();
    }

    @Override
    public void onResume() {
        super.onResume();
        getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.STATUS_BAR_EXPANDED_WEATHER_STYLE), true,
                mWeatherStyleObserver);
        updatePreferences();
    }

    @Override
    public void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(mWeatherStyleObserver);
    }

    private void updatePreferences() {
        boolean weatherEnabled = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_ENABLE_WEATHER, 0) == 1;
        boolean usePanel = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_WEATHER_STYLE, 0) == 0;

        String summary = null;
        if (weatherEnabled) {
            summary = getResources().getString(R.string.options_summary);
            mWeatherOptions.setSummary(summary);
            if (!usePanel) {
                summary = getResources().getString(R.string.weather_style_bar_summary);
            } else {
                summary = getResources().getString(
                        R.string.weather_click_actions_summary);
            }
            mWeatherClickActions.setSummary(summary);
            summary = getResources().getString(R.string.colors_summary);
            mWeatherColors.setSummary(summary);
        } else {
            summary = getResources().getString(R.string.weather_disabled_summary);
            mWeatherOptions.setSummary(summary);
            mWeatherClickActions.setSummary(summary);
            mWeatherColors.setSummary(summary);
        }
        mWeatherClickActions.setEnabled(weatherEnabled && usePanel);
        mWeatherColors.setEnabled(weatherEnabled);
    }
}
