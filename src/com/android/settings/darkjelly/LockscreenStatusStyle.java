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

public class LockscreenStatusStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "LockscreenStatusStyle";

    private static final String PREF_ENABLE_THEME_DEFAULT = "lockscreen_status_enable_theme_default";
    private static final String PREF_LOCKSCREEN_STATUS_COLOR = "lockscreen_status_color";

    private CheckBoxPreference mEnableThemeDefault;
    private ColorPickerPreference mLockscreenStatusColor;

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

        addPreferencesFromResource(R.xml.lockscreen_status_style);
        mResolver = getActivity().getContentResolver();

        mEnableThemeDefault = (CheckBoxPreference) findPreference(PREF_ENABLE_THEME_DEFAULT);
        mEnableThemeDefault.setChecked(Settings.System.getInt(mResolver,
                Settings.System.LOCKSCREEN_STATUS_ENABLE_THEME_DEFAULT, 1) == 1);
        mEnableThemeDefault.setOnPreferenceChangeListener(this);

        mLockscreenStatusColor = (ColorPickerPreference) findPreference(PREF_LOCKSCREEN_STATUS_COLOR);
        mLockscreenStatusColor.setOnPreferenceChangeListener(this);

        updatePreferences();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.lockscreen_status_style, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.lockscreen_status_cm_default:
                Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_STATUS_COLOR, 0xffbebebe);
                refreshSettings();
                return true;
            case R.id.lockscreen_status_dark_jelly_default:
                Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_STATUS_COLOR, 0xff33b5e5);
                refreshSettings();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mEnableThemeDefault) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_STATUS_ENABLE_THEME_DEFAULT, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mLockscreenStatusColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_STATUS_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void updatePreferences() {
        boolean isThemeDefaultEnabled = mEnableThemeDefault.isChecked();
        int color = Settings.System.getInt(mResolver, Settings.System.LOCKSCREEN_STATUS_COLOR, 0xffbebebe);

        if (isThemeDefaultEnabled) {
            mLockscreenStatusColor.setNewPreviewColor(color);
            String themeDefaultColorSummary = getResources().getString(R.string.theme_default_color);
            mLockscreenStatusColor.setSummary(themeDefaultColorSummary);
            mLockscreenStatusColor.setEnabled(false);
        } else {
            mLockscreenStatusColor.setEnabled(true);
            mLockscreenStatusColor.setNewPreviewColor(color);
            String hexColor = String.format("#%08x", (0xffffffff & color));
            mLockscreenStatusColor.setSummary(hexColor);
        }
    }
}
