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

public class NotificationDrawerBackgroundStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "NotificationDrawerBackgroundStyle";

    private static final String PREF_ENABLE_THEME_DEFAULT = "notification_drawer_background_enable_theme_default";
    private static final String PREF_NOTIFICATION_DRAWER_BACKGROUND_COLOR = "notification_drawer_background_color";
    private static final String PREF_NOTIFICATION_DRAWER_BACKGROUND_ALPHA = "notification_drawer_background_alpha";
    private static final String PREF_NOTIFICATION_DRAWER_ROW_ALPHA = "notification_drawer_row_alpha";

    private CheckBoxPreference mEnableThemeDefault;
    private ColorPickerPreference mNotificationDrawerBackgroundColor;
    private SeekBarPreference mNotificationDrawerBackgroundAlpha;
    private SeekBarPreference mNotificationDrawerRowAlpha;

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

        addPreferencesFromResource(R.xml.notification_drawer_background_style);

        mEnableThemeDefault = (CheckBoxPreference) findPreference(PREF_ENABLE_THEME_DEFAULT);
        mEnableThemeDefault.setChecked(Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_BACKGROUND_ENABLE_THEME_DEFAULT, 1) == 1);
        mEnableThemeDefault.setOnPreferenceChangeListener(this);

        mNotificationDrawerBackgroundColor = (ColorPickerPreference) findPreference(PREF_NOTIFICATION_DRAWER_BACKGROUND_COLOR);
        mNotificationDrawerBackgroundColor.setOnPreferenceChangeListener(this);

        mNotificationDrawerBackgroundAlpha = (SeekBarPreference) findPreference(PREF_NOTIFICATION_DRAWER_BACKGROUND_ALPHA);
        float BackgroundTransparency = 0.0f;
        try{
            BackgroundTransparency = Settings.System.getFloat(mResolver, Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA);
        }catch (Exception e) {
            BackgroundTransparency = 0.0f;
            Settings.System.putFloat(mResolver, Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA, 0.1f);
        }
        mNotificationDrawerBackgroundAlpha.setInitValue((int) (BackgroundTransparency * 100));
        mNotificationDrawerBackgroundAlpha.setProperty(Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA);
        mNotificationDrawerBackgroundAlpha.setOnPreferenceChangeListener(this);

        mNotificationDrawerRowAlpha = (SeekBarPreference) findPreference(PREF_NOTIFICATION_DRAWER_ROW_ALPHA);
        float RowTransparency = 0.0f;
        try{
            RowTransparency = Settings.System.getFloat(mResolver, Settings.System.NOTIFICATION_DRAWER_ROW_ALPHA);
        }catch (Exception e) {
            RowTransparency = 0.0f;
            Settings.System.putFloat(mResolver, Settings.System.NOTIFICATION_DRAWER_ROW_ALPHA, 0.0f);
        }
        mNotificationDrawerRowAlpha.setInitValue((int) (RowTransparency * 100));
        mNotificationDrawerRowAlpha.setProperty(Settings.System.NOTIFICATION_DRAWER_ROW_ALPHA);
        mNotificationDrawerRowAlpha.setOnPreferenceChangeListener(this);

        updatePreferences();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.notification_drawer_background_style, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notification_drawer_background_cm_default:
                Settings.System.putInt(mResolver, Settings.System.NOTIFICATION_DRAWER_BACKGROUND, 0xe60e0e0e);
                Settings.System.putFloat(mResolver, Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA, 0.0f);
                Settings.System.putFloat(mResolver, Settings.System.NOTIFICATION_DRAWER_ROW_ALPHA, 0.0f);
                refreshSettings();
                return true;
            case R.id.notification_drawer_background_dark_jelly_default:
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
        } else if (preference == mNotificationDrawerBackgroundColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.NOTIFICATION_DRAWER_BACKGROUND, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mNotificationDrawerBackgroundAlpha) {
            float valNav = Float.parseFloat((String) newValue);
            Settings.System.putFloat(mResolver, Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA, valNav / 100);
            return true;
        } else if (preference == mNotificationDrawerRowAlpha) {
            float valNav = Float.parseFloat((String) newValue);
            Settings.System.putFloat(mResolver, Settings.System.NOTIFICATION_DRAWER_ROW_ALPHA, valNav / 100);
            return true;
        }
        return false;
    }

    public void updatePreferences() {
        boolean isThemeDefaultEnabled = mEnableThemeDefault.isChecked();
        int color = Settings.System.getInt(mResolver, Settings.System.NOTIFICATION_DRAWER_BACKGROUND, 0xe60e0e0e);

        if (isThemeDefaultEnabled) {
            mNotificationDrawerBackgroundColor.setNewPreviewColor(color);
            String themeDefaultColorSummary = getResources().getString(R.string.theme_default_color);
            mNotificationDrawerBackgroundColor.setSummary(themeDefaultColorSummary);
            mNotificationDrawerBackgroundColor.setEnabled(false);
        } else {
            mNotificationDrawerBackgroundColor.setEnabled(true);
            mNotificationDrawerBackgroundColor.setNewPreviewColor(color);
            String hexColor = String.format("#%08x", (0xffffffff & color));
            mNotificationDrawerBackgroundColor.setSummary(hexColor);
        }
    }
}
