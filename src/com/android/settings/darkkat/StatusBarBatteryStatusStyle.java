/*
 * Copyright (C) 2013 DarkKat
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

package com.android.settings.darkkat;

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

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarBatteryStatusStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "StatusBarBatteryStatusStyle";

    private static final String PREF_BATT_STAT_STYLE = "battery_status_style";
    private static final String PREF_BATT_STAT_SHOW_TEXT = "battery_status_show_text";
    private static final String PREF_BATT_STAT_CUSTOM_FRAME_COLOR = "battery_status_custom_frame_color";
    private static final String PREF_BATT_STAT_CIRCLE_DOTTED = "battery_circle_dotted";
    private static final String PREF_BATT_STAT_CIRCLE_DOT_LENGTH = "battery_circle_dot_length";
    private static final String PREF_BATT_STAT_CIRCLE_DOT_INTERVAL = "battery_circle_dot_interval";
    private static final String PREF_BATT_STAT_CIRCLE_DOT_OFFSET = "battery_circle_dot_offset";
    private static final String PREF_BATT_STAT_COLOR = "battery_status_color";
    private static final String PREF_BATT_STAT_FRAME_COLOR = "battery_frame_color";
    private static final String PREF_BATT_STAT_TEXT_COLOR = "battery_text_color";
    private static final String PREF_BATT_STAT_TEXT_CHARGING_COLOR = "battery_text_charging_color";
    private static final String PREF_BATT_STAT_CIRCLE_ANIMATIONSPEED = "circle_battery_animation_speed";

    private ListPreference mBatteryStatusStyle;
    private CheckBoxPreference mShowText;
    private CheckBoxPreference mCustomFrameColor;
    private CheckBoxPreference mCircleDotted;
    private ListPreference mCircleDotLength;
    private ListPreference mCircleDotInterval;
    private ListPreference mCircleDotOffset;
    private ColorPickerPreference mStatusColor;
    private ColorPickerPreference mFrameColor;
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

        boolean showtext = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_TEXT, 1) == 1;
        boolean customFrameColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_CUSTOM_FRAME_COLOR, 0) == 1;
        boolean isCircleDottedEnabled = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_CIRCLE_DOTTED, 0) == 1;
        int batteryStatus = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_BATTERY_STATUS_STYLE, 2);
        int intColor = 0xffffffff;
        String hexColor = String.format("#%08x", (0xffffffff & 0xffffffff));

        mBatteryStatusStyle = (ListPreference) findPreference(PREF_BATT_STAT_STYLE);
        mBatteryStatusStyle.setValue(String.valueOf(batteryStatus));
        mBatteryStatusStyle.setSummary(mBatteryStatusStyle.getEntry());
        mBatteryStatusStyle.setOnPreferenceChangeListener(this);

        mShowText = (CheckBoxPreference) findPreference(PREF_BATT_STAT_SHOW_TEXT);
        mCustomFrameColor = (CheckBoxPreference) findPreference(PREF_BATT_STAT_CUSTOM_FRAME_COLOR);
        if (batteryStatus == 1) {
            removePreference(PREF_BATT_STAT_SHOW_TEXT);
            removePreference(PREF_BATT_STAT_CUSTOM_FRAME_COLOR);
        } else {
            mShowText.setChecked(showtext);
            mShowText.setOnPreferenceChangeListener(this);
            mCustomFrameColor.setChecked(customFrameColor);
            mCustomFrameColor.setOnPreferenceChangeListener(this);
        }

        mCircleDotted = (CheckBoxPreference) findPreference(PREF_BATT_STAT_CIRCLE_DOTTED);
        if (batteryStatus == 2) {
            mCircleDotted.setChecked(isCircleDottedEnabled);
            mCircleDotted.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_BATT_STAT_CIRCLE_DOTTED);
        }

        mCircleDotLength = (ListPreference) findPreference(PREF_BATT_STAT_CIRCLE_DOT_LENGTH);
        mCircleDotInterval = (ListPreference) findPreference(PREF_BATT_STAT_CIRCLE_DOT_INTERVAL);
        mCircleDotOffset = (ListPreference) findPreference(PREF_BATT_STAT_CIRCLE_DOT_OFFSET);
        if (batteryStatus == 2 && isCircleDottedEnabled) {
            int circleDotLength = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_DOT_LENGTH, 3);
            mCircleDotLength.setValue(String.valueOf(circleDotLength));
            mCircleDotLength.setSummary(mCircleDotLength.getEntry());
            mCircleDotLength.setOnPreferenceChangeListener(this);

            int circleDotInterval = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_DOT_INTERVAL, 2);
            mCircleDotInterval.setValue(String.valueOf(circleDotInterval));
            mCircleDotInterval.setSummary(mCircleDotInterval.getEntry());
            mCircleDotInterval.setOnPreferenceChangeListener(this);

            int circleDotOffset = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_DOT_OFFSET, 0);
            mCircleDotOffset.setValue(String.valueOf(circleDotOffset));
            mCircleDotOffset.setSummary(mCircleDotOffset.getEntry());
            mCircleDotOffset.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_BATT_STAT_CIRCLE_DOT_LENGTH);
            removePreference(PREF_BATT_STAT_CIRCLE_DOT_INTERVAL);
            removePreference(PREF_BATT_STAT_CIRCLE_DOT_OFFSET);
        }

        mStatusColor = (ColorPickerPreference) findPreference(PREF_BATT_STAT_COLOR);
        if (batteryStatus != 1) {
            intColor = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_BATTERY_STATUS_COLOR, 0xffffffff);
            mStatusColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mStatusColor.setSummary(hexColor);
            mStatusColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_BATT_STAT_COLOR);
        }

        mFrameColor = (ColorPickerPreference) findPreference(PREF_BATT_STAT_FRAME_COLOR);
        if ((batteryStatus == 0 || batteryStatus == 2) && customFrameColor) {
            intColor = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_BATTERY_FRAME_COLOR, 0x66ffffff);
            mFrameColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mFrameColor.setSummary(hexColor);
            mFrameColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_BATT_STAT_FRAME_COLOR);
        }

        mBatteryTextColor = (ColorPickerPreference) findPreference(PREF_BATT_STAT_TEXT_COLOR);
        if (showtext || batteryStatus == 1) {
            intColor = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR, 0xffffffff); 
            mBatteryTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mBatteryTextColor.setSummary(hexColor);
            mBatteryTextColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_BATT_STAT_TEXT_COLOR);
        }

        mBatteryTextChargingColor = (ColorPickerPreference) findPreference(PREF_BATT_STAT_TEXT_CHARGING_COLOR);
        if (showtext || batteryStatus == 1) {
            intColor = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR, 0xff00ff00);
            mBatteryTextChargingColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mBatteryTextChargingColor.setSummary(hexColor);
            mBatteryTextChargingColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_BATT_STAT_TEXT_CHARGING_COLOR);
        }

        mCircleAnimSpeed = (ListPreference) findPreference(PREF_BATT_STAT_CIRCLE_ANIMATIONSPEED);
        if (batteryStatus == 2) {
            int circleAnimSpeed = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED, 3);
            mCircleAnimSpeed.setValue(String.valueOf(circleAnimSpeed));
            mCircleAnimSpeed.setSummary(mCircleAnimSpeed.getEntry());
            mCircleAnimSpeed.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_BATT_STAT_CIRCLE_ANIMATIONSPEED);
        }

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
            case R.id.statusbar_battery_status_android_default:
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_STATUS_STYLE, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_TEXT, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_CUSTOM_FRAME_COLOR, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_DOTTED, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_DOT_LENGTH, 3);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_DOT_INTERVAL, 2);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_DOT_OFFSET, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_STATUS_COLOR, 0xffffffff);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_FRAME_COLOR, 0x66ffffff);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR, 0xffffffff);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR, 0xffffffff);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED, 1);
                refreshSettings();
                return true;
            case R.id.statusbar_battery_status_darkkat_default:
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_STATUS_STYLE, 2);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_TEXT, 1);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_CUSTOM_FRAME_COLOR, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_DOTTED, 1);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_DOT_LENGTH, 3);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_DOT_INTERVAL, 2);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_DOT_OFFSET, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_STATUS_COLOR, 0xff33b5e5);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_FRAME_COLOR, 0x6633b5e5);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR, 0xffffffff);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR, 0xff00ff00);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED, 5);
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
            int batteryStatus = Integer.valueOf((String) newValue);
            index = mBatteryStatusStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_STATUS_STYLE, batteryStatus);
            mBatteryStatusStyle.setSummary(mBatteryStatusStyle.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mShowText) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_TEXT, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mCustomFrameColor) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_CUSTOM_FRAME_COLOR, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mCircleDotted) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_DOTTED, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mCircleDotLength) {
            int circleDotLength = Integer.valueOf((String) newValue);
            index = mCircleDotLength.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_DOT_LENGTH, circleDotLength);
            mCircleDotLength.setSummary(mCircleDotLength.getEntries()[index]);
            return true;
        } else if (preference == mCircleDotInterval) {
            int circleDotInterval = Integer.valueOf((String) newValue);
            index = mCircleDotInterval.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_DOT_INTERVAL, circleDotInterval);
            mCircleDotInterval.setSummary(mCircleDotInterval.getEntries()[index]);
            return true;
        } else if (preference == mCircleDotOffset) {
            int circleDotOffset = Integer.valueOf((String) newValue);
            index = mCircleDotOffset.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CIRCLE_DOT_OFFSET, circleDotOffset);
            mCircleDotOffset.setSummary(mCircleDotOffset.getEntries()[index]);
            return true;
        } else if (preference == mStatusColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_STATUS_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mFrameColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_BATTERY_FRAME_COLOR, intHex);
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

    @Override
    public void onResume() {
        super.onResume();
    }
}
