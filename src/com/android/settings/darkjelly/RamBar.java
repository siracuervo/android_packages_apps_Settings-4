/*
* Copyright (C) 2012 Slimroms Project
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

package com.android.settings.darkjelly;

import android.app.Activity;
import android.content.Intent;
import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.darkjelly.colorpicker.ColorPickerPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import java.util.Date;

public class RamBar extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "RamBar";

    private static final String RAM_BAR_CAT_STYLE = "ram_bar_style";
    private static final String RAM_BAR_MODE = "ram_bar_mode";
    private static final String RAM_BAR_COLOR_APP_MEM = "ram_bar_color_app_mem";
    private static final String RAM_BAR_COLOR_CACHE_MEM = "ram_bar_color_cache_mem";
    private static final String RAM_BAR_COLOR_TOTAL_MEM = "ram_bar_color_total_mem";

    static final int DEFAULT_MEM_COLOR = 0xff8d8d8d;
    static final int DEFAULT_CACHE_COLOR = 0xff00aa00;
    static final int DEFAULT_ACTIVE_APPS_COLOR = 0xff33b5e5;

    private ListPreference mRamBarMode;
    private ColorPickerPreference mRamBarAppMemColor;
    private ColorPickerPreference mRamBarCacheMemColor;
    private ColorPickerPreference mRamBarTotalMemColor;

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

        int intColor;
        String hexColor;

        addPreferencesFromResource(R.xml.ram_bar);
        mResolver = getActivity().getContentResolver();

        PreferenceScreen prefSet = getPreferenceScreen();

        mRamBarMode = (ListPreference) prefSet.findPreference(RAM_BAR_MODE);
        int ramBarMode = Settings.System.getInt(mResolver,
                Settings.System.RECENTS_RAM_BAR_MODE, 0);
        mRamBarMode.setValue(String.valueOf(ramBarMode));
        mRamBarMode.setSummary(mRamBarMode.getEntry());
        mRamBarMode.setOnPreferenceChangeListener(this);

        PreferenceCategory ramBarStyleCategory = (PreferenceCategory) findPreference(RAM_BAR_CAT_STYLE);

        // Remove uneeded preferences depending on enabled states
        mRamBarAppMemColor = (ColorPickerPreference) findPreference(RAM_BAR_COLOR_APP_MEM);
        mRamBarCacheMemColor = (ColorPickerPreference) findPreference(RAM_BAR_COLOR_CACHE_MEM);
        mRamBarTotalMemColor = (ColorPickerPreference) findPreference(RAM_BAR_COLOR_TOTAL_MEM);
        if (ramBarMode == 0 || ramBarMode == 1) {
            ramBarStyleCategory.removePreference(mRamBarCacheMemColor);
        } else {
            mRamBarCacheMemColor.setOnPreferenceChangeListener(this);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.RECENTS_RAM_BAR_CACHE_COLOR, DEFAULT_CACHE_COLOR);
            mRamBarCacheMemColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mRamBarCacheMemColor.setSummary(hexColor);
        }
        if (ramBarMode != 3) {
            ramBarStyleCategory.removePreference(mRamBarTotalMemColor);
        } else {
           mRamBarTotalMemColor.setOnPreferenceChangeListener(this);
           intColor = Settings.System.getInt(mResolver,
                    Settings.System.RECENTS_RAM_BAR_MEM_COLOR, DEFAULT_MEM_COLOR);
           mRamBarTotalMemColor.setNewPreviewColor(intColor);
           hexColor = String.format("#%08x", (0xffffffff & intColor));
           mRamBarTotalMemColor.setSummary(hexColor);
        }
        if (ramBarMode == 0) {
            ramBarStyleCategory.removePreference(mRamBarAppMemColor);
            removePreference("ram_bar_style");
        } else {
            mRamBarAppMemColor.setOnPreferenceChangeListener(this);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.RECENTS_RAM_BAR_ACTIVE_APPS_COLOR, DEFAULT_ACTIVE_APPS_COLOR);
            mRamBarAppMemColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mRamBarAppMemColor.setSummary(hexColor);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.ram_bar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_RAM_BAR_ACTIVE_APPS_COLOR, DEFAULT_ACTIVE_APPS_COLOR);
                Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_RAM_BAR_CACHE_COLOR, DEFAULT_CACHE_COLOR);
                Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_RAM_BAR_MEM_COLOR, DEFAULT_MEM_COLOR);
                refreshSettings();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean result = false;

        if (preference == mRamBarMode) {
            int ramBarMode = Integer.valueOf((String) newValue);
            int index = mRamBarMode.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_RAM_BAR_MODE, ramBarMode);
            mRamBarMode.setSummary(mRamBarMode.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mRamBarAppMemColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_RAM_BAR_ACTIVE_APPS_COLOR, intHex);
            return true;
        } else if (preference == mRamBarCacheMemColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_RAM_BAR_CACHE_COLOR, intHex);
            return true;
        } else if (preference == mRamBarTotalMemColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer
                    .valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_RAM_BAR_MEM_COLOR, intHex);
            return true;
        }
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
