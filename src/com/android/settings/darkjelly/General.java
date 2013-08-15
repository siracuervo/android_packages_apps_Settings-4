/*
* Copyright (C) 2013 Dark Jelly
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.android.settings.darkjelly;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources; 
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.provider.Settings;
import android.view.WindowManagerGlobal; 

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class General extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "General";

    private static final String KEY_RECENTS_RAM_BAR = "recents_ram_bar";
    private static final String KEY_LOW_BATTERY_WARNING_POLICY = "low_battery_warning_policy";

    private Preference mRamBar;
    private ListPreference mLowBatteryWarning;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver resolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.general_settings);

        mRamBar = findPreference(KEY_RECENTS_RAM_BAR);

        mLowBatteryWarning = (ListPreference) findPreference(KEY_LOW_BATTERY_WARNING_POLICY);
        int lowBatteryWarning = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.POWER_UI_LOW_BATTERY_WARNING_POLICY, 0);
        mLowBatteryWarning.setValue(String.valueOf(lowBatteryWarning));
        mLowBatteryWarning.setSummary(mLowBatteryWarning.getEntry());
        mLowBatteryWarning.setOnPreferenceChangeListener(this);

        updateRamBar();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mLowBatteryWarning) {
            int lowBatteryWarning = Integer.valueOf((String) objValue);
            int index = mLowBatteryWarning.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.POWER_UI_LOW_BATTERY_WARNING_POLICY, lowBatteryWarning);
            mLowBatteryWarning.setSummary(mLowBatteryWarning.getEntries()[index]);
            return true;
        }

        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void updateRamBar() {
        int ramBarMode = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.RECENTS_RAM_BAR_MODE, 0);
        if (ramBarMode != 0)
            mRamBar.setSummary(getResources().getString(R.string.ram_bar_color_enabled));
        else
            mRamBar.setSummary(getResources().getString(R.string.ram_bar_color_disabled));
    }

     @Override
     public void onResume() {
         super.onResume();
         updateRamBar();
     }
 
     @Override
     public void onPause() {
         super.onResume();
         updateRamBar();
     } 
}
