/*
 * Copyright (C) 2014 DarkKat
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
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

import com.android.internal.util.darkkat.DeviceUtils;
import com.android.internal.widget.LockPatternUtils;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SeekBarPreference;

public class InterfaceLockscreenSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String KEY_LOCKSCREEN_CAT_INFO_AREA =
            "lockscreen_category_info_area";
    private static final String KEY_LOCKSCREEN_CAT_BACKGROUND =
            "lockscreen_category_background";
    private static final String KEY_LOCKSCREEN_CAT_WIDGETS =
            "lockscreen_category_widgets";
    private static final String KEY_LOCKSCREEN_SHOW_BATTERY_STATUS_RING =
            "lockscreen_show_battery_status_ring";
    private static final String KEY_LOCKSCREEN_SHOW_CUSTOM_CARRIER_LABEL =
            "lockscreen_show_custom_carrier_label";
    private static final String KEY_LOCKSCREEN_ALWAYS_SHOW_BATTERY_STATUS =
            "lockscreen_always_show_battery_status";
    private static final String KEY_LOCKSCREEN_SEE_THROUGH =
            "lockscreen_see_through";
    private static final String KEY_LOCKSCREEN_BLUR_RADIUS =
            "lockscreen_blur_radius";
    private static final String KEY_ENABLE_WIDGETS =
            "lockscreen_enable_widgets";
    private static final String KEY_LOCKSCREEN_MAXIMIMIZE_WIDGETS =
            "lockscreen_maximize_widgets";

    private CheckBoxPreference mShowBatteryStatusRing;
    private CheckBoxPreference mShowCustomCarrierLabel;
    private CheckBoxPreference mAlwaysShowBatteryStatus;
    private CheckBoxPreference mSeeThrough;
    private SeekBarPreference  mBlurRadius;
    private CheckBoxPreference mEnableWidgets;
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

        addPreferencesFromResource(R.xml.interface_lockscreen_settings);

        mResolver = getActivity().getContentResolver();

        boolean isbatteryStatusRingEnabled = Settings.System.getInt(mResolver,
               Settings.System.LOCKSCREEN_SHOW_BATTERY_STATUS_RING, 0) == 1;
        mShowBatteryStatusRing =
                (CheckBoxPreference) findPreference(KEY_LOCKSCREEN_SHOW_BATTERY_STATUS_RING);
        mShowBatteryStatusRing.setChecked(isbatteryStatusRingEnabled);
        mShowBatteryStatusRing.setOnPreferenceChangeListener(this);

        // Remove battery status ring style screen depending on enabled states
        PreferenceCategory catInfoArea =
                (PreferenceCategory) findPreference(KEY_LOCKSCREEN_CAT_INFO_AREA);
        PreferenceScreen batteryStatusRingStyle =
                (PreferenceScreen) findPreference("lockscreen_battery_status_ring_style");
        if (!isbatteryStatusRingEnabled) {
            catInfoArea.removePreference(batteryStatusRingStyle);
        }

        // Remove carrier label preferences on wifi only devices
        mShowCustomCarrierLabel =
                (CheckBoxPreference) findPreference(KEY_LOCKSCREEN_SHOW_CUSTOM_CARRIER_LABEL);
        PreferenceScreen carrierLabelStyle =
                (PreferenceScreen) findPreference("lockscreen_carrier_label_style");
        if (Utils.isWifiOnly(getActivity())) {
            catInfoArea.removePreference(mShowCustomCarrierLabel);
            catInfoArea.removePreference(carrierLabelStyle);
        } else {
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

        boolean seeThroughEnabled = Settings.System.getInt(mResolver,
               Settings.System.LOCKSCREEN_SEE_THROUGH, 0) == 1;
        mSeeThrough =
                (CheckBoxPreference) findPreference(KEY_LOCKSCREEN_SEE_THROUGH);
        mSeeThrough.setChecked(seeThroughEnabled);
        mSeeThrough.setOnPreferenceChangeListener(this);

        // Remove blur radius setting depending on enabled states
        PreferenceCategory catBackground =
                (PreferenceCategory) findPreference(KEY_LOCKSCREEN_CAT_BACKGROUND);
        mBlurRadius =
                (SeekBarPreference) findPreference(KEY_LOCKSCREEN_BLUR_RADIUS);
        if (seeThroughEnabled) {
            mBlurRadius.setInitValue(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_BLUR_RADIUS, 48));
            mBlurRadius.setProperty(String.valueOf(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_BLUR_RADIUS, 48) / 100));
            mBlurRadius.setOnPreferenceChangeListener(this);
        } else {
            catBackground.removePreference(mBlurRadius);
        }

        mEnableWidgets = (CheckBoxPreference) findPreference(KEY_ENABLE_WIDGETS);
        final boolean enabled = new LockPatternUtils(getActivity()).getWidgetsEnabled();
        if (!enabled) {
            mEnableWidgets.setSummary(R.string.disabled);
        } else {
            mEnableWidgets.setSummary(R.string.enabled);
        }
        mEnableWidgets.setChecked(enabled);
        mEnableWidgets.setOnPreferenceChangeListener(this);

        // Remove Maximize widgets checkbox on hybrid/tablet
        // or on phones if widgets are disabled
        PreferenceCategory catWidgets =
                (PreferenceCategory) findPreference(KEY_LOCKSCREEN_CAT_WIDGETS);
        mMaximizeWidgets =
                (CheckBoxPreference) findPreference(KEY_LOCKSCREEN_MAXIMIMIZE_WIDGETS);
        if (!DeviceUtils.isPhone(getActivity()) || !enabled) {
            catWidgets.removePreference(mMaximizeWidgets);
        } else {
            mMaximizeWidgets.setChecked(Settings.System.getInt(mResolver,
                   Settings.System.LOCKSCREEN_MAXIMIZE_WIDGETS, 0) == 1);
            mMaximizeWidgets.setOnPreferenceChangeListener(this);
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
        } else if (preference == mSeeThrough) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_SEE_THROUGH, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mBlurRadius) {
            int value = Integer.parseInt((String) objValue);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_BLUR_RADIUS, value);
            return true;
        } else if (preference == mEnableWidgets) {
            new LockPatternUtils(getActivity()).setWidgetsEnabled((Boolean) objValue);
            mEnableWidgets.setSummary((Boolean) objValue ? R.string.enabled : R.string.disabled);
            refreshSettings();
            return true;
        } else if (preference == mMaximizeWidgets) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_MAXIMIZE_WIDGETS, value ? 1 : 0);
            return true;
        }

        return false;
    }
}
