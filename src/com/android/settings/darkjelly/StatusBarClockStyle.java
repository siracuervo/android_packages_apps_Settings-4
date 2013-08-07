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
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.darkjelly.colorpicker.ColorPickerPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StatusBarClockStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener { 

    private static final String TAG = "StatusBarClockStyle";

    private static final String PREF_STAT_BAR_CLOCK_POSITION = "status_bar_clock_position";
    private static final String PREF_STAT_BAR_CLOCK_COLOR = "status_bar_clock_color";
    private static final String PREF_STAT_BAR_AM_PM = "status_bar_am_pm";

    private CheckBoxPreference mStatusBarClockPosition;
    private ColorPickerPreference mStatusBarClockColor;
    private ListPreference mStatusBarAmPm;

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

        addPreferencesFromResource(R.xml.status_bar_clock_style);
        mResolver = getActivity().getContentResolver();

        mStatusBarClockPosition = (CheckBoxPreference) findPreference(PREF_STAT_BAR_CLOCK_POSITION);
        mStatusBarClockPosition.setChecked((Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_CLOCK_POSITION, 0) == 1));
        mStatusBarClockPosition.setOnPreferenceChangeListener(this);

        mStatusBarClockColor = (ColorPickerPreference) findPreference(PREF_STAT_BAR_CLOCK_COLOR);
        int intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_CLOCK_COLOR, 0xff33b5e5); 
        mStatusBarClockColor.setNewPreviewColor(intColor);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mStatusBarClockColor.setSummary(hexColor);
        mStatusBarClockColor.setOnPreferenceChangeListener(this);

        mStatusBarAmPm = (ListPreference) findPreference(PREF_STAT_BAR_AM_PM);
        try {
            if (Settings.System.getInt(mResolver,
                    Settings.System.TIME_12_24) == 24) {
                mStatusBarAmPm.setEnabled(false);
                mStatusBarAmPm.setSummary(R.string.status_bar_am_pm_info);
            }
        } catch (SettingNotFoundException e ) {
        }
        int statusBarAmPm = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_AM_PM, 2);
        mStatusBarAmPm.setValue(String.valueOf(statusBarAmPm));
        mStatusBarAmPm.setSummary(mStatusBarAmPm.getEntry());
        mStatusBarAmPm.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.status_bar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset_statusbar:
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CLOCK_POSITION, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CLOCK_COLOR, 0xff33b5e5);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_AM_PM, 2);
                refreshSettings();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mStatusBarClockPosition) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CLOCK_POSITION, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarAmPm) {
            int statusBarAmPm = Integer.valueOf((String) newValue);
            int index = mStatusBarAmPm.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_AM_PM, statusBarAmPm);
            mStatusBarAmPm.setSummary(mStatusBarAmPm.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarClockColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CLOCK_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
