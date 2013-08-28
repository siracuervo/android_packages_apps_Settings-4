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
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.darkjelly.colorpicker.ColorPickerPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SeekBarPreference;

public class QuickAccessSettingsStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "QuickAccessSettingsStyle";

    private static final String PREF_ENABLE_DEFAULTS = "qas_enable_defaults";
    private static final String PREF_TILE_BACKGROUND_COLOR = "qas_tile_background_color";
    private static final String PREF_TILE_BACKGROUND_ALPHA = "qas_tile_background_alpha";

    private ListPreference mEnableDefaults;
    private ColorPickerPreference mTileBackgroundColor;
    private SeekBarPreference mTileBackgroundAlpha;

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

        addPreferencesFromResource(R.xml.quick_access_settings_style);

        mResolver = getActivity().getContentResolver();

        mEnableDefaults = (ListPreference) findPreference(PREF_ENABLE_DEFAULTS);
        int enableDefaults = Settings.System.getInt(mResolver,
                Settings.System.QAS_ENABLE_THEME_DEFAULT, 0);
        mEnableDefaults.setValue(String.valueOf(enableDefaults));
        mEnableDefaults.setSummary(mEnableDefaults.getEntry());
        mEnableDefaults.setOnPreferenceChangeListener(this);

        mTileBackgroundColor = (ColorPickerPreference) findPreference(PREF_TILE_BACKGROUND_COLOR);
        mTileBackgroundColor.setOnPreferenceChangeListener(this);
        mTileBackgroundColor.setAlphaSliderEnabled(true);

        mTileBackgroundAlpha = (SeekBarPreference) findPreference(PREF_TILE_BACKGROUND_ALPHA);
        float tileBackgroundAlpha = 0.0f;
        try{
            tileBackgroundAlpha = Settings.System.getFloat(mResolver, Settings.System.QAS_TILE_BACKGROUND_ALPHA);
        } catch (Exception e) {
            tileBackgroundAlpha = 0.0f;
            Settings.System.putFloat(mResolver, Settings.System.QAS_TILE_BACKGROUND_ALPHA, 0.0f);
        }
        mTileBackgroundAlpha.setProperty(Settings.System.QAS_TILE_BACKGROUND_ALPHA);
        mTileBackgroundAlpha.setInitValue((int) (tileBackgroundAlpha * 100));
        mTileBackgroundAlpha.setOnPreferenceChangeListener(this);

        updatePreferences();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mEnableDefaults) {
            int enableDefaults = Integer.valueOf((String) newValue);
            int index = mEnableDefaults.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.QAS_ENABLE_THEME_DEFAULT, enableDefaults);
            mEnableDefaults.setSummary(mEnableDefaults.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mTileBackgroundColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.QAS_TILE_BACKGROUND_COLOR, intHex);
            return true;
        } else if (preference == mTileBackgroundAlpha) {
            float valStat = Float.parseFloat((String) newValue);
            Settings.System.putFloat(mResolver, Settings.System.QAS_TILE_BACKGROUND_ALPHA, valStat / 100);
            return true; 
        }
        return false;
    }

    public void updatePreferences() {

        boolean themeDefault = Settings.System.getInt(mResolver,
                Settings.System.QAS_ENABLE_THEME_DEFAULT, 0) == 0;
        boolean systemDefault = Settings.System.getInt(mResolver,
                Settings.System.QAS_ENABLE_THEME_DEFAULT, 0) == 1;
        boolean customColor = Settings.System.getInt(mResolver,
                Settings.System.QAS_ENABLE_THEME_DEFAULT, 0) == 2;
        String themeDefaultColorSummary = getResources().getString(R.string.theme_default_color);
        String systemDefaultColorSummary = getResources().getString(R.string.system_default_color);
        int color = 0x00000000;

        if (themeDefault || systemDefault) {
            color = 0x00000000;
            mTileBackgroundColor.setNewPreviewColor(color);
            if (themeDefault) {
                mTileBackgroundColor.setSummary(themeDefaultColorSummary);
            } else {
                mTileBackgroundColor.setSummary(systemDefaultColorSummary);
            }
            mTileBackgroundColor.setEnabled(false);
        } else if (customColor){
            mTileBackgroundColor.setEnabled(true);
            color = Settings.System.getInt(mResolver, Settings.System.QAS_TILE_BACKGROUND_COLOR, 0xff202020);
            String hexColor = String.format("#%08x", (0xffffffff & color));
            mTileBackgroundColor.setNewPreviewColor(color);
            mTileBackgroundColor.setSummary(hexColor);
        }
    }
}

