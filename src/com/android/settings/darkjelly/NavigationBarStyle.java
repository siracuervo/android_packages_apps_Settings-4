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
import android.os.Bundle;
import android.preference.CheckBoxPreference;
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

    private static final String PREF_ENABLE_THEME_DEFAULT = "navigation_bar_enable_theme_default";
    private static final String PREF_NAV_BAR_COLOR = "nav_bar_color";
    private static final String PREF_NAV_BAR_HEIGHT = "nav_bar_height";
    private static final String PREF_NAV_BAR_WIDTH = "nav_bar_width";

    private CheckBoxPreference mEnableThemeDefault;
    private ColorPickerPreference mNavBarColor;
    private ListPreference mNavBarHeight;
    private ListPreference mNavBarWidth;

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

        addPreferencesFromResource(R.xml.navigationbar_bar_style);
        mResolver = getActivity().getContentResolver();

        boolean isThemeDefaultEnabled = Settings.System.getInt(mResolver,
               Settings.System.NAVIGATION_BAR_ENABLE_THEME_DEFAULT, 1) == 1;

        mEnableThemeDefault = (CheckBoxPreference) findPreference(PREF_ENABLE_THEME_DEFAULT);
        mEnableThemeDefault.setChecked(isThemeDefaultEnabled);
        mEnableThemeDefault.setOnPreferenceChangeListener(this);

        // Remove uneeded preferences depending on enabled states
        mNavBarColor = (ColorPickerPreference) findPreference(PREF_NAV_BAR_COLOR);
        if (!isThemeDefaultEnabled) {
            int color = Settings.System.getInt(mResolver, Settings.System.NAVIGATION_BAR_COLOR, 0xff000000);
            String hexColor = String.format("#%08x", (0xffffffff & color));
            mNavBarColor.setNewPreviewColor(color);
            mNavBarColor.setSummary(hexColor);
            mNavBarColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_NAV_BAR_COLOR);
        }

        mNavBarHeight = (ListPreference) findPreference(PREF_NAV_BAR_HEIGHT);
        int intHeight = Settings.System.getInt(mResolver,
                 Settings.System.NAVIGATION_BAR_HEIGHT, 48);
        mNavBarHeight.setValue(String.valueOf(intHeight));
        mNavBarHeight.setSummary(mNavBarHeight.getEntry());
        mNavBarHeight.setOnPreferenceChangeListener(this);

        mNavBarWidth = (ListPreference) findPreference(PREF_NAV_BAR_WIDTH);
        int intWidth = Settings.System.getInt(mResolver,
                 Settings.System.NAVIGATION_BAR_WIDTH, 48);
        mNavBarWidth.setValue(String.valueOf(intWidth));
        mNavBarWidth.setSummary(mNavBarWidth.getEntry());
        mNavBarWidth.setOnPreferenceChangeListener(this);

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
            case R.id.navigationbar_cm_default:
                Settings.System.putInt(mResolver, Settings.System.NAVIGATION_BAR_COLOR, 0xff000000);
                Settings.System.putInt(mResolver, Settings.System.NAVIGATION_BAR_HEIGHT, 48);
                Settings.System.putInt(mResolver, Settings.System.NAVIGATION_BAR_WIDTH, 48);
                refreshSettings();
                return true;
            case R.id.navigationbar_dark_jelly_default:
                Settings.System.putInt(mResolver, Settings.System.NAVIGATION_BAR_COLOR, 0xff202020);
                Settings.System.putInt(mResolver, Settings.System.NAVIGATION_BAR_HEIGHT, 48);
                Settings.System.putInt(mResolver, Settings.System.NAVIGATION_BAR_WIDTH, 48);
                refreshSettings();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mEnableThemeDefault) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.NAVIGATION_BAR_ENABLE_THEME_DEFAULT, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mNavBarColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mNavBarHeight) {
            int intHeight = Integer.valueOf((String) newValue);
            int index = mNavBarHeight.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_HEIGHT, intHeight);
            mNavBarHeight.setSummary(mNavBarHeight.getEntries()[index]);
            return true;
        } else if (preference == mNavBarWidth) {
            int intWidth = Integer.valueOf((String) newValue);
            int index = mNavBarWidth.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_WIDTH, intWidth);
            mNavBarWidth.setSummary(mNavBarWidth.getEntries()[index]);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
