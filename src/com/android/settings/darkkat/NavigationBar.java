/*
 * Copyright (C) 2014 DarkKat
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
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.internal.util.darkkat.DeviceUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class NavigationBar extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String ENABLE_NAVIGATION_BAR =
            "enable_nav_bar";
    private static final String PREF_NAVIGATION_BAR_CAN_MOVE =
            "navbar_can_move";
    private static final String PREF_STYLE_DIMEN =
            "navbar_style_dimen_settings";
    private static final String PREF_BUTTON =
            "navbar_buttons";
    private static final String PREF_RING =
            "navbar_targets_settings";

    CheckBoxPreference mEnableNavigationBar;
    CheckBoxPreference mNavigationBarCanMove;
    PreferenceScreen mButtonPreference;
    PreferenceScreen mStyleDimenPreference;
    PreferenceScreen mRingPreference;


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

        addPreferencesFromResource(R.xml.navigation_bar);

        boolean hasNavBarByDefault = getResources().getBoolean(
                com.android.internal.R.bool.config_showNavigationBar);
        boolean enableNavigationBar = Settings.System.getInt(getContentResolver(),
                Settings.System.NAVIGATION_BAR_SHOW, hasNavBarByDefault ? 1 : 0) == 1;
        mEnableNavigationBar = (CheckBoxPreference) findPreference(ENABLE_NAVIGATION_BAR);
        mEnableNavigationBar.setChecked(enableNavigationBar);
        mEnableNavigationBar.setOnPreferenceChangeListener(this);

        if (enableNavigationBar) {
            mNavigationBarCanMove =
                    (CheckBoxPreference) findPreference(PREF_NAVIGATION_BAR_CAN_MOVE);
            mNavigationBarCanMove.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.NAVIGATION_BAR_CAN_MOVE,
                    DeviceUtils.isPhone(getActivity()) ? 1 : 0) == 0);
            mNavigationBarCanMove.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_NAVIGATION_BAR_CAN_MOVE);
            removePreference(PREF_STYLE_DIMEN);
            removePreference(PREF_BUTTON);
            removePreference(PREF_RING);
        }

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mEnableNavigationBar) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_SHOW,
                    ((Boolean) newValue) ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mNavigationBarCanMove) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_CAN_MOVE,
                    ((Boolean) newValue) ? 0 : 1);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
