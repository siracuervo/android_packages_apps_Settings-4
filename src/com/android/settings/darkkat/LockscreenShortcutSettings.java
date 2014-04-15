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
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class LockscreenShortcutSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_LOCKSCREEN_SHORTCUTS =
            "lockscreen_shortcuts";
    private static final String PREF_LOCKSCREEN_SHORTCUT_LONGPRESS =
            "lockscreen_shortcuts_longpress";

    private Preference mShortcuts;
    private CheckBoxPreference mShortcutsLongpress;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.lockscreen_shortcut_settings);

        mResolver = getActivity().getContentResolver();

        mShortcutsLongpress =
                (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_SHORTCUT_LONGPRESS);
        mShortcutsLongpress.setChecked(Settings.System.getInt(mResolver,
                Settings.System.LOCKSCREEN_SHORTCUTS_LONGPRESS, 1) == 1);
        mShortcutsLongpress.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mShortcutsLongpress) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                Settings.System.LOCKSCREEN_SHORTCUTS_LONGPRESS, value ? 1 : 0);
            return true;
        }
        return false;
    }
}
