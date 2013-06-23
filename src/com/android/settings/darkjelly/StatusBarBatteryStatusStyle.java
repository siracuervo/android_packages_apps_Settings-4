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
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.darkjelly.colorpicker.ColorPickerPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class StatusBarBatteryStatusStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "StatusBarBatteryStatusStyle";

    private static final String PREF_BATT_STAT_STYLE = "battery_status_style";
    private static final String PREF_BATT_STAT_CIRCLE_COLOR = "circle_battery_color";
    private static final String PREF_BATT_STAT_TEXT_COLOR = "battery_text_color";
    private static final String PREF_BATT_STAT_TEXT_CHARGING_COLOR = "battery_text_charging_color";
    private static final String PREF_BATT_STAT_CIRCLE_ANIMATIONSPEED = "circle_battery_animation_speed";

    private ListPreference mBatteryStatusStyle;
    private ColorPickerPreference mCircleColor;
    private ColorPickerPreference mBatteryTextColor;
    private ColorPickerPreference mBatteryTextChargingColor;
    private ListPreference mCircleAnimSpeed;

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

        addPreferencesFromResource(R.xml.status_bar_battery_status_style);

        mBatteryStatusStyle = (ListPreference) findPreference(PREF_BATT_STAT_STYLE);
        mCircleColor = (ColorPickerPreference) findPreference(PREF_BATT_STAT_CIRCLE_COLOR);
        mBatteryTextColor = (ColorPickerPreference) findPreference(PREF_BATT_STAT_TEXT_COLOR);
        mBatteryTextChargingColor = (ColorPickerPreference) findPreference(PREF_BATT_STAT_TEXT_CHARGING_COLOR);
        mCircleAnimSpeed = (ListPreference) findPreference(PREF_BATT_STAT_CIRCLE_ANIMATIONSPEED);

        int batteryStatusStyle = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_BATTERY_STATUS_STYLE, 0);
        mBatteryStatusStyle.setValue(String.valueOf(batteryStatusStyle));
        mBatteryStatusStyle.setSummary(mBatteryStatusStyle.getEntry());
        mBatteryStatusStyle.setOnPreferenceChangeListener(this);


        int circleColor = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_CIRCLE_BATTERY_COLOR, 0xff33b5e5); 
        mCircleColor.setNewPreviewColor(circleColor);
        mCircleColor.setOnPreferenceChangeListener(this);

        int batteryTextColor = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR, 0xff33b5e5); 
        mBatteryTextColor.setNewPreviewColor(batteryTextColor);
        mBatteryTextColor.setOnPreferenceChangeListener(this);

        int batteryTextChargingColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR, 0xff33b5e5); 
        mBatteryTextChargingColor.setNewPreviewColor(batteryTextChargingColor);
        mBatteryTextChargingColor.setOnPreferenceChangeListener(this);

        int circleAnimSpeed = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED, 3);
        mCircleAnimSpeed.setValue(String.valueOf(circleAnimSpeed));
        mCircleAnimSpeed.setSummary(mCircleAnimSpeed.getEntry());
        mCircleAnimSpeed.setOnPreferenceChangeListener(this);

        udateBatteryStatusOptions(batteryStatusStyle);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.status_bar_battery_status_style, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset_statusbar_battery_status:
                Settings.System.putInt(getActivity().getContentResolver(),
                       Settings.System.STATUS_BAR_BATTERY_STATUS_STYLE, 0);
                Settings.System.putInt(getActivity().getContentResolver(),
                       Settings.System.STATUS_BAR_CIRCLE_BATTERY_COLOR, 0xff33b5e5);
                Settings.System.putInt(getActivity().getContentResolver(),
                       Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR, 0xff33b5e5);
                Settings.System.putInt(getActivity().getContentResolver(),
                       Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR, 0xff33b5e5);
                Settings.System.putInt(getActivity().getContentResolver(),
                       Settings.System.STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED, 3);
                refreshSettings();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mBatteryStatusStyle) {
            int batteryStatusStyle = Integer.valueOf((String) newValue);
            int index = mBatteryStatusStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_BATTERY_STATUS_STYLE, batteryStatusStyle);
            mBatteryStatusStyle.setSummary(mBatteryStatusStyle.getEntries()[index]);
            udateBatteryStatusOptions(batteryStatusStyle);
            return true;
        } else if (preference == mCircleColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_CIRCLE_BATTERY_COLOR, intHex);
            return true;
        } else if (preference == mBatteryTextColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR, intHex);
            return true;
        } else if (preference == mBatteryTextChargingColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR, intHex);
            return true;
        } else if (preference == mCircleAnimSpeed) {
            int circleAnimSpeed = Integer.valueOf((String) newValue);
            int index = mCircleAnimSpeed.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED, circleAnimSpeed);
            mCircleAnimSpeed.setSummary(mCircleAnimSpeed.getEntries()[index]);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void udateBatteryStatusOptions(int batteryIconStat) {

        if (batteryIconStat == 0 ||
            batteryIconStat == 7 ||
            batteryIconStat == 8 ||
            batteryIconStat == 9) {

            mBatteryStatusStyle.setEnabled(true);
            mCircleColor.setEnabled(false);
            mBatteryTextColor.setEnabled(false);
            mBatteryTextChargingColor.setEnabled(false);
            mCircleAnimSpeed.setEnabled(false);
        } else if (batteryIconStat == 1 ||
            batteryIconStat == 2) {

            mBatteryStatusStyle.setEnabled(true);
            mCircleColor.setEnabled(false);
            mBatteryTextColor.setEnabled(true);
            mBatteryTextChargingColor.setEnabled(true);
            mCircleAnimSpeed.setEnabled(true);
        } else if (batteryIconStat == 3 ||
            batteryIconStat == 4 ||
            batteryIconStat == 5 ||
            batteryIconStat == 6) {

            mBatteryStatusStyle.setEnabled(true);
            mCircleColor.setEnabled(true);
            mBatteryTextColor.setEnabled(true);
            mBatteryTextChargingColor.setEnabled(true);
            mCircleAnimSpeed.setEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
