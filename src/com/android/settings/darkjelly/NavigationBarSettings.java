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

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.darkjelly.colorpicker.ColorPickerPreference;

public class NavigationBarSettings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String KEY_NAV_BUTTONS_HEIGHT = "nav_buttons_height";
    private static final String KEY_NAVIGATION_BAR_COLOR = "nav_bar_color";
    private static final String KEY_PIE_CONTROL = "pie_control";

    private ListPreference mNavButtonsHeight;
    private ColorPickerPreference mNavigationBarColor;
    private PreferenceScreen mPieControl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.navigation_bar_settings);

        PreferenceScreen prefScreen = getPreferenceScreen();

        mNavButtonsHeight = (ListPreference) findPreference(KEY_NAV_BUTTONS_HEIGHT);
        mNavButtonsHeight.setOnPreferenceChangeListener(this);
        int statusNavButtonsHeight = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                 Settings.System.NAV_BUTTONS_HEIGHT, 48);
        mNavButtonsHeight.setValue(String.valueOf(statusNavButtonsHeight));
        mNavButtonsHeight.setSummary(mNavButtonsHeight.getEntry());
        mNavigationBarColor = (ColorPickerPreference) findPreference(KEY_NAVIGATION_BAR_COLOR);
        mNavigationBarColor.setOnPreferenceChangeListener(this);
        mPieControl = (PreferenceScreen) findPreference(KEY_PIE_CONTROL);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mPieControl != null) {
            updatePieControlDescription();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mNavButtonsHeight) {
            int statusNavButtonsHeight = Integer.valueOf((String) objValue);
            int index = mNavButtonsHeight.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.NAV_BUTTONS_HEIGHT, statusNavButtonsHeight);
            mNavButtonsHeight.setSummary(mNavButtonsHeight.getEntries()[index]);
            return true;
        } else if (preference == mNavigationBarColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex) & 0x00FFFFFF;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_COLOR, intHex);
            return true;
        }
        return false;
    }

    private void updatePieControlDescription() {
        if (Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.PIE_CONTROLS, 0) == 1) {
            mPieControl.setSummary(getString(R.string.pie_control_enabled));
        } else {
            mPieControl.setSummary(getString(R.string.pie_control_disabled));
        }
    }
}
