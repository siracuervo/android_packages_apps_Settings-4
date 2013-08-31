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

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources; 
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.provider.Settings;
import android.text.Spannable;
import android.view.WindowManagerGlobal;
import android.widget.EditText;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.SettingsPreferenceFragment;

public class General extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "General";

    private static final String KEY_CUSTOM_CARRIER_LABEL = "custom_carrier_label";
    private static final String KEY_LOW_BATTERY_WARNING_POLICY = "low_battery_warning_policy";

    private Preference mCustomLabel;
    private ListPreference mLowBatteryWarning;

    private ContentResolver mResolver;
    private String mCustomLabelText = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.general_settings);

        // Remove show custom carrier label and show wifi ssid preferences on wifi only devices
        if (Utils.isWifiOnly(getActivity())) {
            removePreference(KEY_CUSTOM_CARRIER_LABEL);
        } else {
            mCustomLabel = findPreference(KEY_CUSTOM_CARRIER_LABEL);
        }

        mLowBatteryWarning = (ListPreference) findPreference(KEY_LOW_BATTERY_WARNING_POLICY);
        int lowBatteryWarning = Settings.System.getInt(mResolver,
                Settings.System.POWER_UI_LOW_BATTERY_WARNING_POLICY, 0);
        mLowBatteryWarning.setValue(String.valueOf(lowBatteryWarning));
        mLowBatteryWarning.setSummary(mLowBatteryWarning.getEntry());
        mLowBatteryWarning.setOnPreferenceChangeListener(this);

        if (!Utils.isWifiOnly(getActivity())) {
            updateCustomLabelTextSummary();
        }
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mLowBatteryWarning) {
            int lowBatteryWarning = Integer.valueOf((String) objValue);
            int index = mLowBatteryWarning.findIndexOfValue((String) objValue);
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_UI_LOW_BATTERY_WARNING_POLICY, lowBatteryWarning);
            mLowBatteryWarning.setSummary(mLowBatteryWarning.getEntries()[index]);
            return true;
        }

        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mCustomLabel) {
            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle(R.string.custom_carrier_label_title);
            alert.setMessage(R.string.custom_carrier_label_explain);

            // Set an EditText view to get user input
            final EditText input = new EditText(getActivity());
            input.setText(mCustomLabelText != null ? mCustomLabelText : "");
            alert.setView(input);
            alert.setPositiveButton(getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = ((Spannable) input.getText()).toString();
                    Settings.System.putString(mResolver,
                            Settings.System.CUSTOM_CARRIER_LABEL, value);
                    updateCustomLabelTextSummary();

                    Intent i = new Intent();
                    i.setAction("com.android.settings.LABEL_CHANGED");
                    getActivity().sendBroadcast(i);
                }
            });
            alert.setNegativeButton(getResources().getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();

        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void updateCustomLabelTextSummary() {
        mCustomLabelText = Settings.System.getString(mResolver,
                Settings.System.CUSTOM_CARRIER_LABEL);
        if (mCustomLabelText == null || mCustomLabelText.length() == 0) {
            mCustomLabel.setSummary(R.string.custom_carrier_label_notset);
        } else {
            mCustomLabel.setSummary(mCustomLabelText);
        }
    }

     @Override
     public void onResume() {
         super.onResume();
         if (!Utils.isWifiOnly(getActivity())) {
            updateCustomLabelTextSummary();
        }
     }
 
     @Override
     public void onPause() {
         super.onResume();
         if (!Utils.isWifiOnly(getActivity())) {
            updateCustomLabelTextSummary();
        }
     } 
}
