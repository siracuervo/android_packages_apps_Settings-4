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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.darkjelly.colorpicker.ColorPickerPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StatusBarBatteryStatusStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "StatusBarBatteryStatusStyle";

    private static final String PREF_BATT_STAT_STYLE = "battery_status_style";
    private static final String PREF_BATT_STAT_CIRCLE_DOTTED = "battery_circle_dotted";
    private static final String PREF_BATT_STAT_CIRCLE_COLOR = "circle_battery_color";
    private static final String PREF_BATT_STAT_TEXT_COLOR = "battery_text_color";
    private static final String PREF_BATT_STAT_TEXT_CHARGING_COLOR = "battery_text_charging_color";
    private static final String PREF_BATT_STAT_CIRCLE_ANIMATIONSPEED = "circle_battery_animation_speed";

    private ListPreference mBatteryStatusStyle;
    private CheckBoxPreference mCircleDotted;
    private ColorPickerPreference mCircleColor;
    private ColorPickerPreference mBatteryTextColor;
    private ColorPickerPreference mBatteryTextChargingColor;
    private ListPreference mCircleAnimSpeed;

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

        addPreferencesFromResource(R.xml.status_bar_battery_status_style);
        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        mBatteryStatusStyle = (ListPreference) findPreference(PREF_BATT_STAT_STYLE);
        int batteryStatusStyle = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_BATTERY_STATUS_STYLE, 4);
        mBatteryStatusStyle.setValue(String.valueOf(batteryStatusStyle));
        mBatteryStatusStyle.setSummary(mBatteryStatusStyle.getEntry());
        mBatteryStatusStyle.setOnPreferenceChangeListener(this);

        mCircleDotted = (CheckBoxPreference) findPreference(PREF_BATT_STAT_CIRCLE_DOTTED);
        mCircleDotted.setChecked((Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_DOTTED, 0) == 1));
        mCircleDotted.setOnPreferenceChangeListener(this);

        mCircleColor = (ColorPickerPreference) findPreference(PREF_BATT_STAT_CIRCLE_COLOR);
        intColor = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_BATTERY_COLOR, 0xff33b5e5); 
        mCircleColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mCircleColor.setSummary(hexColor);
        mCircleColor.setOnPreferenceChangeListener(this);

        mBatteryTextColor = (ColorPickerPreference) findPreference(PREF_BATT_STAT_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR, 0xffffffff); 
        mBatteryTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBatteryTextColor.setSummary(hexColor);
        mBatteryTextColor.setOnPreferenceChangeListener(this);

        mBatteryTextChargingColor = (ColorPickerPreference) findPreference(PREF_BATT_STAT_TEXT_CHARGING_COLOR);
        intColor = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR, 0xff00ff00); 
        mBatteryTextChargingColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBatteryTextChargingColor.setSummary(hexColor);
        mBatteryTextChargingColor.setOnPreferenceChangeListener(this);

        mCircleAnimSpeed = (ListPreference) findPreference(PREF_BATT_STAT_CIRCLE_ANIMATIONSPEED);
        int circleAnimSpeed = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED, 3);
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
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_STATUS_STYLE, 4);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_DOTTED, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_BATTERY_COLOR, 0xff33b5e5);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR, 0xffffffff);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR, 0xff00ff00);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED, 3);
                refreshSettings();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int index;
        int intHex;
        String hex;

        if (preference == mBatteryStatusStyle) {
            int batteryStatusStyle = Integer.valueOf((String) newValue);
            index = mBatteryStatusStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_STATUS_STYLE, batteryStatusStyle);
            mBatteryStatusStyle.setSummary(mBatteryStatusStyle.getEntries()[index]);
            udateBatteryStatusOptions(batteryStatusStyle);
            return true;
        } else if (preference == mCircleDotted) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_DOTTED, value ? 1 : 0);
            return true;
        } else if (preference == mCircleColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_BATTERY_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mBatteryTextColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mBatteryTextChargingColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mCircleAnimSpeed) {
            int circleAnimSpeed = Integer.valueOf((String) newValue);
            index = mCircleAnimSpeed.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED, circleAnimSpeed);
            mCircleAnimSpeed.setSummary(mCircleAnimSpeed.getEntries()[index]);
            return true;
        }
        return false;
    }

    private void udateBatteryStatusOptions(int batteryIconStat) {

        if (batteryIconStat == 0 ||
            batteryIconStat == 5 ||
            batteryIconStat == 6 ||
            batteryIconStat == 7) {

            mBatteryStatusStyle.setEnabled(true);
            mCircleDotted.setEnabled(false);
            mCircleColor.setEnabled(false);
            mBatteryTextColor.setEnabled(false);
            mBatteryTextChargingColor.setEnabled(false);
            mCircleAnimSpeed.setEnabled(false);
        } else if (batteryIconStat == 1 ||
            batteryIconStat == 2) {

            mBatteryStatusStyle.setEnabled(true);
            mCircleDotted.setEnabled(false);
            mCircleColor.setEnabled(false);
            mBatteryTextColor.setEnabled(true);
            mBatteryTextChargingColor.setEnabled(true);
            mCircleAnimSpeed.setEnabled(false);
        } else if (batteryIconStat == 3 ||
            batteryIconStat == 4) {

            mBatteryStatusStyle.setEnabled(true);
            mCircleDotted.setEnabled(true);
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
