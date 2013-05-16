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

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.darkjelly.colorpicker.ColorPickerPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SeekBarPreference;

public class StatusBarStyle extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String PREF_STATUS_BAR_COLOR = "status_bar_color";
    private static final String PREF_STATUS_BAR_ALPHA = "status_bar_alpha";

    private ColorPickerPreference mStatusBarColor;
    private SeekBarPreference mStatusbarTransparency;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_style);

        PreferenceScreen prefSet = getPreferenceScreen();

        mStatusBarColor = (ColorPickerPreference) prefSet.findPreference(PREF_STATUS_BAR_COLOR);
        mStatusbarTransparency = (SeekBarPreference) findPreference(PREF_STATUS_BAR_ALPHA); 

        mStatusBarColor.setOnPreferenceChangeListener(this);

        float statBarTransparency = 0.0f;
        try{
            statBarTransparency = Settings.System.getFloat(getActivity()
                 .getContentResolver(), Settings.System.STATUS_BAR_ALPHA);
        } catch (Exception e) {
            statBarTransparency = 0.0f;
            Settings.System.putFloat(getActivity().getContentResolver(), Settings.System.STATUS_BAR_ALPHA, 0.0f);
        }
        mStatusbarTransparency.setProperty(Settings.System.STATUS_BAR_ALPHA);
        mStatusbarTransparency.setInitValue((int) (statBarTransparency * 100));
        mStatusbarTransparency.setOnPreferenceChangeListener(this); 
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mStatusBarColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_COLOR, intHex);
            return true;
        } else if (preference == mStatusbarTransparency) {
            float valStat = Float.parseFloat((String) newValue);
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_ALPHA,
                    valStat / 100);
            return true; 
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
