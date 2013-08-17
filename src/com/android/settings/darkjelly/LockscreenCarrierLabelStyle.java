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
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
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
import com.android.settings.widget.SeekBarPreference;

public class LockscreenCarrierLabelStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "LockscreenCarrierLabelStyle";

    private static final String PREF_LOCKSCREEN_SHOW_CUSTOM_CARRIER_LABEL = "lockscreen_show_custom_carrier_label";
    private static final String PREF_LOCKSCREEN_CUSTOM_LABEL_COLOR = "lockscreen_carrier_label_color";

    private CheckBoxPreference mLockscreenShowCustomCarrierLabel;
    private ColorPickerPreference mLockscreenCustomLabelColor;

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

        addPreferencesFromResource(R.xml.lockscreen_carrier_label_style);
        mResolver = getActivity().getContentResolver();
 
        mLockscreenShowCustomCarrierLabel = (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_SHOW_CUSTOM_CARRIER_LABEL);
        mLockscreenShowCustomCarrierLabel.setChecked(Settings.System.getInt(mResolver,
                Settings.System.LOCKSCREEN_SHOW_CUSTOM_CARRIER_LABEL, 1) == 1);
        String customLabelText = Settings.System.getString(mResolver,
                Settings.System.CUSTOM_CARRIER_LABEL);
        if (customLabelText == null || customLabelText.length() == 0) {
            mLockscreenShowCustomCarrierLabel.setSummary(R.string.custom_carrier_label_notset);
            mLockscreenShowCustomCarrierLabel.setEnabled(false);
        } else {
            mLockscreenShowCustomCarrierLabel.setSummary(R.string.show_custom_carrier_label_enabled_summary);
            mLockscreenShowCustomCarrierLabel.setEnabled(true);
        }
        mLockscreenShowCustomCarrierLabel.setOnPreferenceChangeListener(this);

        mLockscreenCustomLabelColor = (ColorPickerPreference) findPreference(PREF_LOCKSCREEN_CUSTOM_LABEL_COLOR);
        int intColor = Settings.System.getInt(mResolver,
                Settings.System.LOCKSCREEN_CARRIER_LABEL_COLOR, 0xffbebebe);
        mLockscreenCustomLabelColor.setNewPreviewColor(intColor);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mLockscreenCustomLabelColor.setSummary(hexColor);
        mLockscreenCustomLabelColor.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.lockscreen_carrier_label_style, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset_lockscreen_carrier_label_style:
                Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_SHOW_CUSTOM_CARRIER_LABEL, 1);
                Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_CARRIER_LABEL_COLOR, 0xffbebebe);
                refreshSettings();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mLockscreenShowCustomCarrierLabel) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_SHOW_CUSTOM_CARRIER_LABEL, value ? 1 : 0);
            return true;
        } else if (preference == mLockscreenCustomLabelColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_CARRIER_LABEL_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
