/*
 * Copyright (C) 2013 JellyBeer/BeerGang Project
 * 
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

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SeekBarPreference;
import com.android.settings.darkjelly.colorpicker.ColorPickerPreference;

public class StatusBarExpandedBackgroundStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "StatusBarExpandedBackgroundStyle";

    private static final String PREF_ENABLE_THEME_DEFAULT = "status_bar_expanded_background_enable_theme_default";
    private static final String PREF_STATUSBAR_EXPANDED_BACKGROUND_COLOR = "status_bar_expanded_background_color";
    private static final String PREF_STATUSBAR_EXPANDED_BACKGROUND_ALPHA = "status_bar_expanded_background_alpha";
    private static final String PREF_NOTIFICATION_DRAWER_ROW_ALPHA = "notification_drawer_row_alpha";

    private CheckBoxPreference mEnableThemeDefault;
    private ColorPickerPreference mBackgroundColor;
    private SeekBarPreference mBackgroundAlpha;
    private SeekBarPreference mRowAlpha;

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

        mResolver = getActivity().getContentResolver();
        addPreferencesFromResource(R.xml.status_bar_expanded_background_style);

        boolean isThemeDefaultEnabled = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_BACKGROUND_ENABLE_THEME_DEFAULT, 1) == 1;

        mEnableThemeDefault = (CheckBoxPreference) findPreference(PREF_ENABLE_THEME_DEFAULT);
        mEnableThemeDefault.setChecked(isThemeDefaultEnabled);
        mEnableThemeDefault.setOnPreferenceChangeListener(this);

        // Remove uneeded preferences depending on enabled states
        mBackgroundColor = (ColorPickerPreference) findPreference(PREF_STATUSBAR_EXPANDED_BACKGROUND_COLOR);
        if (!isThemeDefaultEnabled) {
            int color = Settings.System.getInt(mResolver,
                    Settings.System.NOTIFICATION_DRAWER_BACKGROUND, 0xe60e0e0e);
            mBackgroundColor.setNewPreviewColor(color);
            String hexColor = String.format("#%08x", (0xffffffff & color));
            mBackgroundColor.setSummary(hexColor);
            mBackgroundColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_STATUSBAR_EXPANDED_BACKGROUND_COLOR);
        }

        mBackgroundAlpha = (SeekBarPreference) findPreference(PREF_STATUSBAR_EXPANDED_BACKGROUND_ALPHA);
        float backgroundTransparency = 0.0f;
        try{
            backgroundTransparency = Settings.System.getFloat(mResolver, Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA);
        }catch (Exception e) {
            backgroundTransparency = 0.0f;
            Settings.System.putFloat(mResolver, Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA, 0.1f);
        }
        mBackgroundAlpha.setInitValue((int) (backgroundTransparency * 100));
        mBackgroundAlpha.setProperty(Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA);
        mBackgroundAlpha.setOnPreferenceChangeListener(this);

        mRowAlpha = (SeekBarPreference) findPreference(PREF_NOTIFICATION_DRAWER_ROW_ALPHA);
        float rowTransparency = 0.0f;
        try{
            rowTransparency = Settings.System.getFloat(mResolver, Settings.System.NOTIFICATION_DRAWER_ROW_ALPHA);
        }catch (Exception e) {
            rowTransparency = 0.0f;
            Settings.System.putFloat(mResolver, Settings.System.NOTIFICATION_DRAWER_ROW_ALPHA, 0.0f);
        }
        mRowAlpha.setInitValue((int) (rowTransparency * 100));
        mRowAlpha.setProperty(Settings.System.NOTIFICATION_DRAWER_ROW_ALPHA);
        mRowAlpha.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.status_bar_expanded_background_style, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.status_bar_expanded_background_cm_default:
                Settings.System.putInt(mResolver, Settings.System.NOTIFICATION_DRAWER_BACKGROUND, 0xe60e0e0e);
                Settings.System.putFloat(mResolver, Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA, 0.0f);
                Settings.System.putFloat(mResolver, Settings.System.NOTIFICATION_DRAWER_ROW_ALPHA, 0.0f);
                refreshSettings();
                return true;
            case R.id.status_bar_expanded_background_dark_jelly_default:
                Settings.System.putInt(mResolver, Settings.System.NOTIFICATION_DRAWER_BACKGROUND, 0xff000000);
                Settings.System.putFloat(mResolver, Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA, 0.4f);
                Settings.System.putFloat(mResolver, Settings.System.NOTIFICATION_DRAWER_ROW_ALPHA, 0.4f);
                refreshSettings();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mEnableThemeDefault) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_EXPANDED_BACKGROUND_ENABLE_THEME_DEFAULT, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mBackgroundColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.NOTIFICATION_DRAWER_BACKGROUND, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mBackgroundAlpha) {
            float valNav = Float.parseFloat((String) newValue);
            Settings.System.putFloat(mResolver, Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA, valNav / 100);
            return true;
        } else if (preference == mRowAlpha) {
            float valNav = Float.parseFloat((String) newValue);
            Settings.System.putFloat(mResolver, Settings.System.NOTIFICATION_DRAWER_ROW_ALPHA, valNav / 100);
            return true;
        }
        return false;
    }
}
