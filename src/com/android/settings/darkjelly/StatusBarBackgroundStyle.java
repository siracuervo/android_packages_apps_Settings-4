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
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference; 
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

public class StatusBarBackgroundStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "StatusBarBackgroundStyle";

    private static final String PREF_ENABLE_THEME_DEFAULT = "status_bar_enable_theme_default";
    private static final String PREF_STATUS_BAR_COLOR = "status_bar_color";
    private static final String PREF_STATUS_BAR_ALPHA = "status_bar_alpha";
    private static final String PREF_STATUS_BAR_ALPHA_MODE = "status_bar_alpha_mode";

    private CheckBoxPreference mEnableThemeDefault;
    private ColorPickerPreference mStatusBarColor;
    private SeekBarPreference mStatusbarTransparency;
    private ListPreference mStatusbarAlphaMode;

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

        addPreferencesFromResource(R.xml.status_bar_background_style);

        mResolver = getActivity().getContentResolver();

        mEnableThemeDefault = (CheckBoxPreference) findPreference(PREF_ENABLE_THEME_DEFAULT);
        mEnableThemeDefault.setChecked(Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_ENABLE_THEME_DEFAULT, 1) == 1);
        mEnableThemeDefault.setOnPreferenceChangeListener(this);

        mStatusBarColor = (ColorPickerPreference) findPreference(PREF_STATUS_BAR_COLOR);
        mStatusBarColor.setOnPreferenceChangeListener(this);

        mStatusbarTransparency = (SeekBarPreference) findPreference(PREF_STATUS_BAR_ALPHA);
        float statBarTransparency = 0.0f;
        try{
            statBarTransparency = Settings.System.getFloat(getActivity()
                 .getContentResolver(), Settings.System.STATUS_BAR_ALPHA);
        } catch (Exception e) {
            statBarTransparency = 0.0f;
            Settings.System.putFloat(mResolver, Settings.System.STATUS_BAR_ALPHA, 0.0f);
        }
        mStatusbarTransparency.setProperty(Settings.System.STATUS_BAR_ALPHA);
        mStatusbarTransparency.setInitValue((int) (statBarTransparency * 100));
        mStatusbarTransparency.setOnPreferenceChangeListener(this);

        mStatusbarAlphaMode = (ListPreference) findPreference(PREF_STATUS_BAR_ALPHA_MODE);
        int statusbarAlphaMode = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_ALPHA_MODE, 1);
        mStatusbarAlphaMode.setValue(String.valueOf(statusbarAlphaMode));
        mStatusbarAlphaMode.setSummary(mStatusbarAlphaMode.getEntry());
        mStatusbarAlphaMode.setOnPreferenceChangeListener(this);

        updatePreferences();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.status_bar_background_style, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.statusbar_bg_cm_default:
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_COLOR, 0xff000000);
                Settings.System.putFloat(mResolver, Settings.System.STATUS_BAR_ALPHA, 0.0f);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_ALPHA_MODE, 1);
                updatePreferences();
                return true;
            case R.id.statusbar_bg_dark_jelly_default:
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_COLOR, 0xff202020);
                Settings.System.putFloat(mResolver, Settings.System.STATUS_BAR_ALPHA, 0.4f);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_ALPHA_MODE, 1);
                updatePreferences();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mEnableThemeDefault) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_ENABLE_THEME_DEFAULT, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mStatusBarColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mStatusbarTransparency) {
            float valStat = Float.parseFloat((String) newValue);
            Settings.System.putFloat(mResolver,
                    Settings.System.STATUS_BAR_ALPHA,
                    valStat / 100);
            return true; 
        } else if (preference == mStatusbarAlphaMode) {
            int statusbarAlphaMode = Integer.valueOf((String) newValue);
            int index = mStatusbarAlphaMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_ALPHA_MODE, statusbarAlphaMode);
            mStatusbarAlphaMode.setSummary(mStatusbarAlphaMode.getEntries()[index]);
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

    public void updatePreferences() {
        boolean isThemeDefaultEnabled = mEnableThemeDefault.isChecked();
        int color = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_COLOR, 0xff000000);;

        if (isThemeDefaultEnabled) {
            mStatusBarColor.setNewPreviewColor(color);
            String themeDefaultColorSummary = getResources().getString(R.string.theme_default_color);
            mStatusBarColor.setSummary(themeDefaultColorSummary);
            mStatusBarColor.setEnabled(false);
        } else {
            mStatusBarColor.setEnabled(true);
            String hexColor = String.format("#%08x", (0xffffffff & color));
            mStatusBarColor.setNewPreviewColor(color);
            mStatusBarColor.setSummary(hexColor);
        }
    }
}
