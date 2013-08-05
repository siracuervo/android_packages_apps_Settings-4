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
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.darkjelly.colorpicker.ColorPickerPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class NavigationBarStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "NavigationBarStyle";

    private static final String PREF_NAV_BAR_COLOR = "nav_bar_color";
    private static final String PREF_NAV_BAR_HEIGHT = "nav_bar_height";
    private static final String PREF_NAV_BAR_WIDTH = "nav_bar_width";

    private ColorPickerPreference mNavBarColor;
    private ListPreference mNavBarHeight;
    private ListPreference mNavBarWidth;

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

        addPreferencesFromResource(R.xml.navigationbar_bar_style);

        mNavBarColor = (ColorPickerPreference) findPreference(PREF_NAV_BAR_COLOR);
        mNavBarColor.setOnPreferenceChangeListener(this);
        int intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_COLOR, 0xff000000); 
        mNavBarColor.setNewPreviewColor(intColor);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mNavBarColor.setSummary(hexColor);

        mNavBarHeight = (ListPreference) findPreference(PREF_NAV_BAR_HEIGHT);
        mNavBarHeight.setOnPreferenceChangeListener(this);
        int intHeight = Settings.System.getInt(getActivity().getContentResolver(),
                 Settings.System.NAVIGATION_BAR_HEIGHT, 48);
        mNavBarHeight.setValue(String.valueOf(intHeight));
        mNavBarHeight.setSummary(mNavBarHeight.getEntry());

        mNavBarWidth = (ListPreference) findPreference(PREF_NAV_BAR_WIDTH);
        mNavBarWidth.setOnPreferenceChangeListener(this);
        int intWidth = Settings.System.getInt(getActivity().getContentResolver(),
                 Settings.System.NAVIGATION_BAR_WIDTH, 48);
        mNavBarWidth.setValue(String.valueOf(intWidth));
        mNavBarWidth.setSummary(mNavBarWidth.getEntry());

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.navigationbar_bar_style, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset_navigation_bar_style:
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.NAVIGATION_BAR_COLOR, 0xff000000);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.NAVIGATION_BAR_HEIGHT, 48);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.NAVIGATION_BAR_WIDTH, 48);
                refreshSettings();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNavBarColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mNavBarHeight) {
            int intHeight = Integer.valueOf((String) newValue);
            int index = mNavBarHeight.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_HEIGHT, intHeight);
            mNavBarHeight.setSummary(mNavBarHeight.getEntries()[index]);
            return true;
        } else if (preference == mNavBarWidth) {
            int intWidth = Integer.valueOf((String) newValue);
            int index = mNavBarWidth.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_WIDTH, intWidth);
            mNavBarWidth.setSummary(mNavBarWidth.getEntries()[index]);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
