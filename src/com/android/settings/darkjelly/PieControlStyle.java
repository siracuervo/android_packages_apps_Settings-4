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

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.SeekBarDialogPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class PieControlStyle extends SettingsPreferenceFragment {

    private static final String TAG = "PieControlStyle";

    private static final String PREF_PIE_SIZE = "pie_control_size";
    private static final String PREF_SHOW_CUSTOM_CARRIER_LABEL = "show_custom_carrier_label";

    private SeekBarDialogPreference mPieSize;
    private CheckBoxPreference mShowCustomCarrierLabel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addPreferencesFromResource(R.xml.pie_control_style);

        mPieSize = (SeekBarDialogPreference) findPreference(PREF_PIE_SIZE);

        mShowCustomCarrierLabel = (CheckBoxPreference) findPreference(PREF_SHOW_CUSTOM_CARRIER_LABEL);
        mShowCustomCarrierLabel.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.PIE_ENABLE_CUSTOM_CARRIER_LABEL, 0) == 1);
        String customLabelText = Settings.System.getString(getActivity().getContentResolver(),
                Settings.System.CUSTOM_CARRIER_LABEL);
        if (customLabelText == null || customLabelText.length() == 0) {
            mShowCustomCarrierLabel.setSummary(R.string.custom_carrier_label_notset);
            mShowCustomCarrierLabel.setEnabled(false);
        } else {
            mShowCustomCarrierLabel.setSummary(R.string.show_custom_carrier_label_enabled_summary);
            mShowCustomCarrierLabel.setEnabled(true);
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mShowCustomCarrierLabel) {
            value = mShowCustomCarrierLabel.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.PIE_ENABLE_CUSTOM_CARRIER_LABEL, value ? 1 : 0);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
