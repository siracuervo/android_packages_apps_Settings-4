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

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.android.settings.darkjelly.colorpicker.ColorPickerPreference;

public class BatteryStyle extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "BatteryStyle"; 
    private static final String PREF_BATT_BAR_STYLE = "battery_bar_style";
    private static final String PREF_BATT_ANIMATE = "battery_bar_animate";
    private static final String PREF_BATT_BAR_COLOR = "battery_bar_color";
    private static final String PREF_BATT_BAR_WIDTH = "battery_bar_thickness";

    private CheckBoxPreference mBatteryBarStyle;
    private CheckBoxPreference mBatteryBarChargingAnimation;
    private ColorPickerPreference mBatteryBarColor;
    private ListPreference mBatteryBarThickness;

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

        addPreferencesFromResource(R.xml.battery_style);

        prefs = getPreferenceScreen();

        mBatteryBarStyle = (CheckBoxPreference) prefs.findPreference(PREF_BATT_BAR_STYLE);
        mBatteryBarChargingAnimation = (CheckBoxPreference) prefs.findPreference(PREF_BATT_ANIMATE);
        mBatteryBarColor = (ColorPickerPreference) prefs.findPreference(PREF_BATT_BAR_COLOR);
        mBatteryBarThickness = (ListPreference) prefs.findPreference(PREF_BATT_BAR_WIDTH);

        mBatteryBarStyle.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR_STYLE, 0) == 1));

        mBatteryBarChargingAnimation.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, 0) == 1));

        mBatteryBarColor.setOnPreferenceChangeListener(this);
        int intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_COLOR, 0xff33b5e5); 
        mBatteryBarColor.setNewPreviewColor(intColor);

        int batteryBarThickness = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, 0);
        mBatteryBarThickness.setValue(String.valueOf(batteryBarThickness));
        mBatteryBarThickness.setSummary(mBatteryBarThickness.getEntry());
        mBatteryBarThickness.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.battery_style, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUSBAR_BATTERY_BAR_STYLE, 0);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, 0);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUSBAR_BATTERY_BAR_COLOR, 0xff33b5e5);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, 1);
                refreshSettings();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mBatteryBarColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_COLOR, intHex);
            return true;
        } else if (preference == mBatteryBarThickness) {
            int batteryBarThickness = Integer.valueOf((String) newValue);
            int index = mBatteryBarThickness.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_THICKNESS, batteryBarThickness);
            mBatteryBarThickness.setSummary(mBatteryBarThickness.getEntries()[index]);
            return true;
        }
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mBatteryBarStyle) {
            value = mBatteryBarStyle.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_STYLE, value ? 1 : 0);
            return true;
        } else if (preference == mBatteryBarChargingAnimation) {
            value = mBatteryBarChargingAnimation.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUSBAR_BATTERY_BAR_ANIMATE, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
