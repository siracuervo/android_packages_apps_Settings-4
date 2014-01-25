/*
* Copyright (C) 2014 DarkKat
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

package com.android.settings.darkkat;

import android.content.ContentResolver;
import android.content.res.Resources; 
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class RecentsAppsSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String KEY_RECENTS_RAM_BAR =
            "recents_ram_bar";
    private static final String KEY_RECENTS_CLEAR_ALL_BTN_POS =
            "recents_clear_all_button_position";

    private Preference mRamBar;
    private ListPreference mClearAllBtnPosition;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResolver = getActivity().getContentResolver();

        addPreferencesFromResource(R.xml.recents_apps_settings);

        mRamBar = findPreference(KEY_RECENTS_RAM_BAR);

        mClearAllBtnPosition =
                (ListPreference) findPreference(KEY_RECENTS_CLEAR_ALL_BTN_POS);
        int clearAllBtnPosition = Settings.System.getInt(mResolver,
                Settings.System.RECENTS_CLEAR_ALL_BTN_POS, 2);
        mClearAllBtnPosition.setValue(String.valueOf(clearAllBtnPosition));
        mClearAllBtnPosition.setSummary(mClearAllBtnPosition.getEntry());
        mClearAllBtnPosition.setOnPreferenceChangeListener(this);

        updateRamBar();
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mClearAllBtnPosition) {
            int clearAllBtnPosition = Integer.valueOf((String) objValue);
            int index = mClearAllBtnPosition.findIndexOfValue((String) objValue);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_CLEAR_ALL_BTN_POS, clearAllBtnPosition);
            mClearAllBtnPosition.setSummary(mClearAllBtnPosition.getEntries()[index]);
            return true;
        }

        return false;
    }

    private void updateRamBar() {
        int ramBarMode = Settings.System.getInt(mResolver,
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
