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
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SeekBarPreference;

public class LockscreenBackgroundSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String KEY_LOCKSCREEN_SEE_THROUGH =
            "lockscreen_see_through";
    private static final String KEY_LOCKSCREEN_BLUR_RADIUS =
            "lockscreen_blur_radius";

    private CheckBoxPreference mSeeThrough;
    private SeekBarPreference  mBlurRadius;

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

        addPreferencesFromResource(R.xml.lockscreen_background_settings);

        mResolver = getActivity().getContentResolver();

        boolean seeThroughEnabled = Settings.System.getInt(mResolver,
               Settings.System.LOCKSCREEN_SEE_THROUGH, 0) == 1;
        mSeeThrough =
                (CheckBoxPreference) findPreference(KEY_LOCKSCREEN_SEE_THROUGH);
        mSeeThrough.setChecked(seeThroughEnabled);
        mSeeThrough.setOnPreferenceChangeListener(this);

        // Remove blur radius setting depending on enabled states
        if (seeThroughEnabled) {
            mBlurRadius =
                    (SeekBarPreference) findPreference(KEY_LOCKSCREEN_BLUR_RADIUS);
            mBlurRadius.setInitValue(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_BLUR_RADIUS, 48));
            mBlurRadius.setProperty(String.valueOf(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_BLUR_RADIUS, 48) / 100));
            mBlurRadius.setOnPreferenceChangeListener(this);
        } else {
            removePreference(KEY_LOCKSCREEN_BLUR_RADIUS);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {

        if (preference == mSeeThrough) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_SEE_THROUGH, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mBlurRadius) {
            int value = Integer.parseInt((String) objValue);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_BLUR_RADIUS, value);
            return true;
        }
        return false;
    }
}
