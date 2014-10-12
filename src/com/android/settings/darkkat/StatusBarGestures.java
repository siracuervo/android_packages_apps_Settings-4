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
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StatusBarGestures extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_STATUS_BAR_BRIGHTNESS_CONTROL =
            "status_bar_brightness_control";
    private static final String PREF_STATUS_BAR_DOUBLE_TAP_TO_SLEEP =
            "status_bar_double_tap_to_sleep";

    private CheckBoxPreference mBrightnessControl;
    private CheckBoxPreference mDoubleTapToSleep;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_gestures);

        mResolver = getActivity().getContentResolver();

        mBrightnessControl =
                (CheckBoxPreference) findPreference(PREF_STATUS_BAR_BRIGHTNESS_CONTROL);
        mBrightnessControl.setChecked((Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, 0) == 1));
        mBrightnessControl.setOnPreferenceChangeListener(this);

        try {
            if (Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                mBrightnessControl.setEnabled(false);
                mBrightnessControl.setSummary(R.string.status_bar_toggle_info);
            }
        } catch (SettingNotFoundException e) {
        }

        mDoubleTapToSleep =
                (CheckBoxPreference) findPreference(PREF_STATUS_BAR_DOUBLE_TAP_TO_SLEEP);
        mDoubleTapToSleep.setChecked((Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_DOUBLE_TAP_TO_SLEEP, 0) == 1));
        mDoubleTapToSleep.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        boolean value;

        if (preference == mBrightnessControl) {
            value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, value ? 1 : 0);
            return true;
        } else if (preference == mDoubleTapToSleep) {
            value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_DOUBLE_TAP_TO_SLEEP, value ? 1 : 0);
            return true;
        }

        return false;
    }
}
