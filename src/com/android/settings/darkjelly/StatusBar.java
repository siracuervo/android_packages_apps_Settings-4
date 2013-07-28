/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
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
import android.provider.Settings.SettingNotFoundException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class StatusBar extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "StatusBar";

    private static final String STATUS_BAR_SHOW_CLOCK = "status_bar_show_clock";
    private static final String STATUS_BAR_SHOW_BATTERY_STATUS = "status_bar_show_battery_status";
    private static final String STATUS_BAR_SHOW_BATTERY_BAR = "status_bar_show_battery_bar";
    private static final String STATUS_BAR_ENABLE_NETWORK_SPEED_INDICATOR = "status_bar_enable_network_speed_indicator";
    private static final String STATUS_BAR_SIGNAL = "status_bar_signal";
    private static final String STATUS_BAR_BRIGHTNESS_CONTROL = "status_bar_brightness_control";
    private static final String STATUS_BAR_NOTIF_COUNT = "status_bar_notif_count";
    private static final String STATUS_BAR_CATEGORY_GENERAL = "status_bar_general";

    private CheckBoxPreference mStatusBarShowClock;
    private CheckBoxPreference mStatusBarShowBatteryStatus;
    private CheckBoxPreference mStatusBarShowBatteryBar;
    private CheckBoxPreference mStatusBarNetworkSpeed;
    private ListPreference mStatusBarCmSignal;
    private CheckBoxPreference mStatusBarBrightnessControl;
    private CheckBoxPreference mStatusBarNotifCount;
    private PreferenceCategory mPrefCategoryGeneral;

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

        addPreferencesFromResource(R.xml.status_bar);

        mStatusBarShowClock = (CheckBoxPreference) findPreference(STATUS_BAR_SHOW_CLOCK);
        mStatusBarShowBatteryStatus = (CheckBoxPreference) findPreference(STATUS_BAR_SHOW_BATTERY_STATUS);
        mStatusBarShowBatteryBar = (CheckBoxPreference) findPreference(STATUS_BAR_SHOW_BATTERY_BAR);
        mStatusBarNetworkSpeed = (CheckBoxPreference) findPreference(STATUS_BAR_ENABLE_NETWORK_SPEED_INDICATOR);
        mStatusBarCmSignal = (ListPreference) findPreference(STATUS_BAR_SIGNAL);
        mStatusBarBrightnessControl = (CheckBoxPreference) findPreference(STATUS_BAR_BRIGHTNESS_CONTROL);
        mStatusBarNotifCount = (CheckBoxPreference) findPreference(STATUS_BAR_NOTIF_COUNT);
        mPrefCategoryGeneral = (PreferenceCategory) findPreference(STATUS_BAR_CATEGORY_GENERAL);

        mStatusBarShowClock.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_CLOCK, 1) == 1));

        mStatusBarShowBatteryStatus.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_BATTERY_STATUS, 1) == 1));

        mStatusBarShowBatteryBar.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_BATTERY_BAR, 0) == 1));

        mStatusBarNetworkSpeed.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_ENABLE_NETWORK_SPEED_INDICATOR, 0) == 1));

        int signalStyle = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_SIGNAL_TEXT, 0);
        mStatusBarCmSignal.setValue(String.valueOf(signalStyle));
        mStatusBarCmSignal.setSummary(mStatusBarCmSignal.getEntry());
        mStatusBarCmSignal.setOnPreferenceChangeListener(this);

        try {
            if (Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                mStatusBarBrightnessControl.setEnabled(false);
                mStatusBarBrightnessControl.setSummary(R.string.status_bar_toggle_info);
            }
        } catch (SettingNotFoundException e) {
        }

        mStatusBarBrightnessControl.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, 0) == 1));

        mStatusBarNotifCount.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_NOTIF_COUNT, 0) == 1));

        if (Utils.isWifiOnly(getActivity())) {
            mPrefCategoryGeneral.removePreference(mStatusBarCmSignal);
        }

        if (Utils.isTablet(getActivity())) {
            mPrefCategoryGeneral.removePreference(mStatusBarBrightnessControl);
        }

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
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_SHOW_CLOCK, 1);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_SHOW_BATTERY_STATUS, 1);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_SHOW_BATTERY_BAR, 0);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_ENABLE_NETWORK_SPEED_INDICATOR, 0);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_SIGNAL_TEXT, 0);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, 0);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_NOTIF_COUNT, 0);
                refreshSettings();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mStatusBarCmSignal) {
            int signalStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarCmSignal.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_SIGNAL_TEXT, signalStyle);
            mStatusBarCmSignal.setSummary(mStatusBarCmSignal.getEntries()[index]);
            return true;
        }
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mStatusBarShowClock) {
            value = mStatusBarShowClock.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_SHOW_CLOCK, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarShowBatteryStatus) {
            value = mStatusBarShowBatteryStatus.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_SHOW_BATTERY_STATUS, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarShowBatteryBar) {
            value = mStatusBarShowBatteryBar.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_SHOW_BATTERY_BAR, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarNetworkSpeed) {
            value = mStatusBarNetworkSpeed.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_ENABLE_NETWORK_SPEED_INDICATOR, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarBrightnessControl) {
            value = mStatusBarBrightnessControl.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarNotifCount) {
            value = mStatusBarNotifCount.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_NOTIF_COUNT, value ? 1 : 0);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
