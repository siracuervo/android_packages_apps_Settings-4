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

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class LockscreenBatteryStatusRingStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "LockscreenBatteryStatusRingStyle";

    private static final String PREF_ENABLE_THEME_DEFAULT = "lockscreen_battery_status_ring_style_enable_theme_default";
    private static final String PREF_BATT_STAT_RING_DOTTED = "lockscreen_battery_status_ring_dotted";
    private static final String PREF_BATT_STAT_RING_DOT_LENGTH = "lockscreen_battery_status_ring_dot_length";
    private static final String PREF_BATT_STAT_RING_DOT_INTERVAL = "lockscreen_battery_status_ring_dot_interval";
    private static final String PREF_BATT_STAT_RING_DOT_OFFSET = "lockscreen_battery_status_ring_dot_offset";
    private static final String PREF_BATT_STAT_RING_COLOR = "lockscreen_battery_status_ring_color";
    private static final String PREF_BATT_STAT_RING_CHARGING_COLOR = "lockscreen_battery_status_ring_charging_color";

    private CheckBoxPreference mEnableThemeDefault;
    private CheckBoxPreference mRingDotted;
    private ListPreference mRingDotLength;
    private ListPreference mRingDotInterval;
    private ListPreference mRingDotOffset;
    private ColorPickerPreference mRingColor;
    private ColorPickerPreference mRingChargingColor;

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

        addPreferencesFromResource(R.xml.lockscreen_battery_status_ring_style);
        mResolver = getActivity().getContentResolver();

        boolean isThemeDefaultEnabled = Settings.System.getInt(mResolver,
                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_ENABLE_THEME_DEFAULT, 1) == 1;

        mEnableThemeDefault = (CheckBoxPreference) findPreference(PREF_ENABLE_THEME_DEFAULT);
        mEnableThemeDefault.setChecked(isThemeDefaultEnabled);
        mEnableThemeDefault.setOnPreferenceChangeListener(this);

        boolean isRingDottedEnabled = Settings.System.getInt(mResolver,
                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOTTED, 0) == 1;

        mRingDotted = (CheckBoxPreference) findPreference(PREF_BATT_STAT_RING_DOTTED);
        mRingDotted.setChecked(isRingDottedEnabled);
        mRingDotted.setOnPreferenceChangeListener(this);

        mRingDotLength = (ListPreference) findPreference(PREF_BATT_STAT_RING_DOT_LENGTH);
        mRingDotInterval = (ListPreference) findPreference(PREF_BATT_STAT_RING_DOT_INTERVAL);
        mRingDotOffset = (ListPreference) findPreference(PREF_BATT_STAT_RING_DOT_OFFSET);
        if (isRingDottedEnabled) {
            int ringDotLength = Settings.System.getInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_LENGTH, 5);
            mRingDotLength.setValue(String.valueOf(ringDotLength));
            mRingDotLength.setSummary(mRingDotLength.getEntry());
            mRingDotLength.setOnPreferenceChangeListener(this);

            int ringDotInterval = Settings.System.getInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_INTERVAL, 4);
            mRingDotInterval.setValue(String.valueOf(ringDotInterval));
            mRingDotInterval.setSummary(mRingDotInterval.getEntry());
            mRingDotInterval.setOnPreferenceChangeListener(this);

            int ringDotOffset = Settings.System.getInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_OFFSET, 0);
            mRingDotOffset.setValue(String.valueOf(ringDotOffset));
            mRingDotOffset.setSummary(mRingDotOffset.getEntry());
            mRingDotOffset.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_BATT_STAT_RING_DOT_LENGTH);
            removePreference(PREF_BATT_STAT_RING_DOT_INTERVAL);
            removePreference(PREF_BATT_STAT_RING_DOT_OFFSET);
        }

        int intColor = 0xff0099cc;
        String hexColor = String.format("#%08x", (0xffffffff & 0xff0099cc));

        mRingColor = (ColorPickerPreference) findPreference(PREF_BATT_STAT_RING_COLOR);
        mRingChargingColor = (ColorPickerPreference) findPreference(PREF_BATT_STAT_RING_CHARGING_COLOR);
        if (!isThemeDefaultEnabled) {
            intColor = Settings.System.getInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_COLOR, 0xff0099cc);
            mRingColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mRingColor.setSummary(hexColor);
            mRingColor.setOnPreferenceChangeListener(this);

            intColor = Settings.System.getInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_CHARGING_COLOR, 0xff00ff00);
            mRingChargingColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mRingChargingColor.setSummary(hexColor);
            mRingChargingColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_BATT_STAT_RING_COLOR);
            removePreference(PREF_BATT_STAT_RING_CHARGING_COLOR);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.lockscreen_battery_status_ring_style, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.lockscreen_battery_status_ring_cm_default:
                Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOTTED, 0);
                Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_LENGTH, 5);
                Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_INTERVAL, 4);
                Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_OFFSET, 0);
                Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_COLOR, 0xff0099cc);
                Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_CHARGING_COLOR, 0xff00ff00);
                refreshSettings();
                return true;
            case R.id.lockscreen_battery_status_ring_dark_jelly_default:
                Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOTTED, 1);
                Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_LENGTH, 5);
                Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_INTERVAL, 4);
                Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_OFFSET, 0);
                Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_COLOR, 0xff33b5e5);
                Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_CHARGING_COLOR, 0xff00ff00);
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

        if (preference == mEnableThemeDefault) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_ENABLE_THEME_DEFAULT, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mRingDotted) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOTTED, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mRingDotLength) {
            int ringDotLength = Integer.valueOf((String) newValue);
            index = mRingDotLength.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_LENGTH, ringDotLength);
            mRingDotLength.setSummary(mRingDotLength.getEntries()[index]);
            return true;
        } else if (preference == mRingDotInterval) {
            int ringDotInterval = Integer.valueOf((String) newValue);
            index = mRingDotInterval.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_INTERVAL, ringDotInterval);
            mRingDotInterval.setSummary(mRingDotInterval.getEntries()[index]);
            return true;
        } else if (preference == mRingDotOffset) {
            int ringDotOffset = Integer.valueOf((String) newValue);
            index = mRingDotOffset.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_OFFSET, ringDotOffset);
            mRingDotOffset.setSummary(mRingDotOffset.getEntries()[index]);
            return true;
        } else if (preference == mRingColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mRingChargingColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_CHARGING_COLOR, intHex);
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
