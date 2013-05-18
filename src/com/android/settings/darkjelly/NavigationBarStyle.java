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

public class NavigationBarStyle extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String PREF_NAVIGATION_BAR_COLOR = "navigation_bar_color";
    private static final String PREF_NAVIGATION_BAR_ALPHA = "navigation_bar_alpha";

    private ColorPickerPreference mNavigationBarColor;
    private SeekBarPreference mNavigationBarTransparency;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.navigationbar_bar_style);

        PreferenceScreen prefSet = getPreferenceScreen();

        mNavigationBarColor = (ColorPickerPreference) prefSet.findPreference(PREF_NAVIGATION_BAR_COLOR);
        mNavigationBarTransparency = (SeekBarPreference) findPreference(PREF_NAVIGATION_BAR_ALPHA); 

        mNavigationBarColor.setOnPreferenceChangeListener(this);

        float navBarTransparency = 0.0f;
        try{
            navBarTransparency = Settings.System.getFloat(getActivity()
                 .getContentResolver(), Settings.System.NAVIGATION_BAR_ALPHA);
        } catch (Exception e) {
            navBarTransparency = 0.0f;
            Settings.System.putFloat(getActivity().getContentResolver(), Settings.System.NAVIGATION_BAR_ALPHA, 0.0f);
        }
        mNavigationBarTransparency.setProperty(Settings.System.NAVIGATION_BAR_ALPHA);
        mNavigationBarTransparency.setInitValue((int) (navBarTransparency * 100));
        mNavigationBarTransparency.setOnPreferenceChangeListener(this); 
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNavigationBarColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_COLOR, intHex);
            return true;
        } else if (preference == mNavigationBarTransparency) {
            float valNav = Float.parseFloat((String) newValue);
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_ALPHA,
                    valNav / 100);
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
