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
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.internal.util.darkkat.DeviceUtils;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.SettingsPreferenceFragment;

public class LockscreenSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String KEY_LOCKSCREEN_SHOW_BATTERY_STATUS_RING =
            "lockscreen_show_battery_status_ring";
    private static final String KEY_LOCKSCREEN_SHOW_CUSTOM_CARRIER_LABEL =
            "lockscreen_show_custom_carrier_label";
    private static final String KEY_LOCKSCREEN_ALWAYS_SHOW_BATTERY_STATUS =
            "lockscreen_always_show_battery_status";
    private static final String KEY_LOCKSCREEN_MAXIMIMIZE_WIDGETS =
            "lockscreen_maximize_widgets";
    private static final String KEY_LOCK_CLOCK = "lock_clock";

    private CheckBoxPreference mShowBatteryStatusRing;
    private CheckBoxPreference mShowCustomCarrierLabel;
    private CheckBoxPreference mAlwaysShowBatteryStatus;
    private CheckBoxPreference mMaximizeWidgets;

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

        addPreferencesFromResource(R.xml.lockscreen_settings);

        mResolver = getActivity().getContentResolver();

        boolean isbatteryStatusRingEnabled = Settings.System.getInt(mResolver,
               Settings.System.LOCKSCREEN_SHOW_BATTERY_STATUS_RING, 0) == 1;
        mShowBatteryStatusRing =
                (CheckBoxPreference) findPreference(KEY_LOCKSCREEN_SHOW_BATTERY_STATUS_RING);
        mShowBatteryStatusRing.setChecked(isbatteryStatusRingEnabled);
        mShowBatteryStatusRing.setOnPreferenceChangeListener(this);

        // Remove battery status ring style screen depending on enabled states
        if (!isbatteryStatusRingEnabled) {
            removePreference("lockscreen_battery_status_ring_style");
        }

        // Remove carrier label preferences on wifi only devices
        if (Utils.isWifiOnly(getActivity())) {
            removePreference(KEY_LOCKSCREEN_SHOW_CUSTOM_CARRIER_LABEL);
            removePreference("lockscreen_carrier_label_style");
        } else {
            mShowCustomCarrierLabel =
                (CheckBoxPreference) findPreference(KEY_LOCKSCREEN_SHOW_CUSTOM_CARRIER_LABEL);
            mShowCustomCarrierLabel.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_SHOW_CUSTOM_CARRIER_LABEL, 1) == 1);
            mShowCustomCarrierLabel.setOnPreferenceChangeListener(this);

            String customLabelText = Settings.System.getString(mResolver,
                    Settings.System.CUSTOM_CARRIER_LABEL);
            if (customLabelText == null || customLabelText.length() == 0) {
                mShowCustomCarrierLabel.setSummary(R.string.custom_carrier_label_notset);
                mShowCustomCarrierLabel.setEnabled(false);
            } else {
                mShowCustomCarrierLabel.setSummary(
                        R.string.show_custom_carrier_label_enabled_summary);
                mShowCustomCarrierLabel.setEnabled(true);
            }
        }

        mAlwaysShowBatteryStatus =
                (CheckBoxPreference) findPreference(KEY_LOCKSCREEN_ALWAYS_SHOW_BATTERY_STATUS);
        mAlwaysShowBatteryStatus.setChecked(Settings.System.getInt(mResolver,
               Settings.System.LOCKSCREEN_ALWAYS_SHOW_BATTERY_STATUS, 0) == 1);
        mAlwaysShowBatteryStatus.setOnPreferenceChangeListener(this);

        // Remove Maximize widgets checkbox on hybrid/tablet
        if (!DeviceUtils.isPhone(getActivity())) {
            removePreference(KEY_LOCKSCREEN_MAXIMIMIZE_WIDGETS);
        } else {
            mMaximizeWidgets = (CheckBoxPreference) findPreference(KEY_LOCKSCREEN_MAXIMIMIZE_WIDGETS);
            mMaximizeWidgets.setChecked(Settings.System.getInt(mResolver,
                   Settings.System.LOCKSCREEN_MAXIMIZE_WIDGETS, 0) == 1);
            mMaximizeWidgets.setOnPreferenceChangeListener(this);
        }

        // Remove the lock clock preference if its not installed
        if (!isPackageInstalled("com.cyanogenmod.lockclock")) {
            removePreference(KEY_LOCK_CLOCK);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {

        if (preference == mShowBatteryStatusRing) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_SHOW_BATTERY_STATUS_RING, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowCustomCarrierLabel) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_SHOW_CUSTOM_CARRIER_LABEL, value ? 1 : 0);
            return true;
        } else if (preference == mAlwaysShowBatteryStatus) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_ALWAYS_SHOW_BATTERY_STATUS, value ? 1 : 0);
            return true;
        } else if (preference == mMaximizeWidgets) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_MAXIMIZE_WIDGETS, value ? 1 : 0);
            return true;
        }

        return false;
    }

    private boolean isPackageInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        boolean installed = false;
        try {
           pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
           installed = true;
        } catch (PackageManager.NameNotFoundException e) {
           installed = false;
        }
        return installed;
    }
}
