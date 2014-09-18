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

import android.os.Bundle;
import android.content.ContentResolver;
import android.preference.CheckBoxPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.Preference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StatusBarExpandedNotificationDrawerQar
        extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String PREF_QAR_SHOW_TILES =
            "qar_show_tiles";
    private static final String PREF_QAR_TILES_LINKED =
            "qar_tiles_linked";
    private static final String PREF_QAR_TILE_PICKER =
            "qar_tile_picker";

    private CheckBoxPreference mQarShowTiles;
    private CheckBoxPreference mQarTilesLinked;

    private ContentResolver mResolver;

    private boolean mQarHideTiles;
    private boolean mQarTilesNotLinked;

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

        addPreferencesFromResource(R.xml.status_bar_expanded_notification_drawer_qar);
        mResolver = getActivity().getContentResolver();

        mQarHideTiles = Settings.System.getInt(mResolver,
                Settings.System.QAR_SHOW_TILES, 1) == 0;

        mQarTilesNotLinked = Settings.System.getInt(mResolver,
                Settings.System.QAR_TILES_LINKED, 1) == 0;

        mQarShowTiles =
                (CheckBoxPreference) findPreference(PREF_QAR_SHOW_TILES);
        mQarShowTiles.setChecked(!mQarHideTiles);
        mQarShowTiles.setOnPreferenceChangeListener(this);

        mQarTilesLinked =
                (CheckBoxPreference) findPreference(PREF_QAR_TILES_LINKED);
        mQarTilesLinked.setChecked(!mQarTilesNotLinked);
        mQarTilesLinked.setOnPreferenceChangeListener(this);

        updateQuarPreferences();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mQarShowTiles) {
            Settings.System.putInt(mResolver,
                    Settings.System.QAR_SHOW_TILES,
                    (Boolean) newValue ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mQarTilesLinked) {
            Settings.System.putInt(mResolver,
                    Settings.System.QAR_TILES_LINKED,
                    (Boolean) newValue ? 1 : 0);
            refreshSettings();
            return true;
        }
        return false;
    }

    private void updateQuarPreferences() {
        String qsConfig = Settings.System.getString(mResolver,
                Settings.System.QUICK_SETTINGS_TILES);
        boolean removeQuarPreferences = qsConfig != null && qsConfig.isEmpty();


        if (removeQuarPreferences) { 
            mQarShowTiles.setSummary(R.string.qar_show_tiles_deactivated_summary);
        } else {
            mQarShowTiles.setSummary(R.string.qar_show_tiles_summary);
        }
        if (mQarHideTiles || removeQuarPreferences) {
            removePreference(PREF_QAR_TILES_LINKED);
        }
        if (mQarHideTiles || !mQarTilesNotLinked || removeQuarPreferences) {
            removePreference(PREF_QAR_TILE_PICKER);
        }
        mQarShowTiles.setEnabled(!removeQuarPreferences);
    }
}