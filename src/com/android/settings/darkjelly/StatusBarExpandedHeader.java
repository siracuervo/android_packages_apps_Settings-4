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
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.darkjelly.colorpicker.ColorPickerPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StatusBarExpandedHeader extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "StatusBarExpandedHeader";

    private static final String PREF_ENABLE_THEME_DEFAULT = "status_bar_expanded_header_enable_theme_default";
    private static final String PREF_HEADER_SETTINGS_BUTTON = "header_settings_button";
    private static final String PREF_HEADER_CLOCK_COLOR = "header_clock_date_color";

    private CheckBoxPreference mEnableThemeDefault;
    private ColorPickerPreference mHeaderClockDateColor;
    private CheckBoxPreference mHeaderSettingsButton;

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

        addPreferencesFromResource(R.xml.status_bar_expanded_header);
        mResolver = getActivity().getContentResolver();

        mEnableThemeDefault = (CheckBoxPreference) findPreference(PREF_ENABLE_THEME_DEFAULT);
        mEnableThemeDefault.setChecked(Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_HEADER_ENABLE_THEME_DEFAULT, 1) == 1);
        mEnableThemeDefault.setOnPreferenceChangeListener(this);

        mHeaderSettingsButton = (CheckBoxPreference) findPreference(PREF_HEADER_SETTINGS_BUTTON);
        mHeaderSettingsButton.setChecked(Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_SETTINGS_BUTTON, 0) == 1);
        mHeaderSettingsButton.setOnPreferenceChangeListener(this);

        mHeaderClockDateColor = (ColorPickerPreference) findPreference(PREF_HEADER_CLOCK_COLOR);
        mHeaderClockDateColor.setOnPreferenceChangeListener(this);

        updatePreferences();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.status_bar_expanded_header, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.status_bar_expanded_header_cm_default:
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_EXPANDED_SETTINGS_BUTTON, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_EXPANDED_CLOCK_DATE_COLOR, 0xffffffff);
                refreshSettings();
                return true;
            case R.id.status_bar_expanded_header_dark_jelly_default:
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_EXPANDED_SETTINGS_BUTTON, 1);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_EXPANDED_CLOCK_DATE_COLOR, 0xffff0000);
                refreshSettings();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mEnableThemeDefault) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_EXPANDED_HEADER_ENABLE_THEME_DEFAULT, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mHeaderSettingsButton) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_EXPANDED_SETTINGS_BUTTON, value ? 1 : 0);
            return true;
        } else if (preference == mHeaderClockDateColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_EXPANDED_CLOCK_DATE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    public void updatePreferences() {
        boolean isThemeDefaultEnabled = mEnableThemeDefault.isChecked();
        int color = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_EXPANDED_CLOCK_DATE_COLOR, 0xffffffff);

        if (isThemeDefaultEnabled) {
            mHeaderClockDateColor.setNewPreviewColor(color);
            String themeDefaultColorSummary = getResources().getString(R.string.theme_default_color);
            mHeaderClockDateColor.setSummary(themeDefaultColorSummary);
            mHeaderClockDateColor.setEnabled(false);
        } else {
            mHeaderClockDateColor.setEnabled(true);
            mHeaderClockDateColor.setNewPreviewColor(color);
            String hexColor = String.format("#%08x", (0xffffffff & color));
            mHeaderClockDateColor.setSummary(hexColor);
        }
    }
}
