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
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class PowerMenu extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "PowerMenu";

    private static final String KEY_POWER_MENU_SCREENSHOT = "power_menu_screenshot";
    private static final String KEY_POWER_MENU_SCREEN_RECORD = "power_menu_screen_record";
    private static final String KEY_POWER_MENU_EXPANDED_DESKTOP = "power_menu_expanded_desktop";

    private CheckBoxPreference mShowScreenshot;
    private CheckBoxPreference mShowScreenRecord;
    private CheckBoxPreference mShowExpandedDesktop;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.power_menu);

        mResolver = getActivity().getContentResolver();

        mShowScreenshot = (CheckBoxPreference) findPreference(KEY_POWER_MENU_SCREENSHOT);
        mShowScreenshot.setChecked(Settings.System.getInt(mResolver,
                Settings.System.SCREENSHOT_IN_POWER_MENU, 0) == 1);
        mShowScreenshot.setOnPreferenceChangeListener(this);

        mShowScreenRecord = (CheckBoxPreference) findPreference(KEY_POWER_MENU_SCREEN_RECORD);
        mShowScreenRecord.setChecked(Settings.System.getInt(mResolver,
                Settings.System.SCREENRECORD_IN_POWER_MENU, 0) == 1);
        mShowScreenRecord.setOnPreferenceChangeListener(this);

        mShowExpandedDesktop = (CheckBoxPreference) findPreference(KEY_POWER_MENU_EXPANDED_DESKTOP);
        mShowExpandedDesktop.setChecked(Settings.System.getInt(mResolver,
                Settings.System.EXPANDED_DESKTOP_IN_POWER_MENU, 0) == 1);
        mShowExpandedDesktop.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {

        if (preference == mShowScreenshot) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.SCREENSHOT_IN_POWER_MENU, value ? 1 : 0);
            return true;
        } else if (preference == mShowScreenRecord) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.SCREENRECORD_IN_POWER_MENU, value ? 1 : 0);
            return true;
        } else if (preference == mShowExpandedDesktop) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.EXPANDED_DESKTOP_IN_POWER_MENU, value ? 1 : 0);
            return true;
        }

        return false;
    }
}
