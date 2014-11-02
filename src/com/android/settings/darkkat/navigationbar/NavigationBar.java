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

package com.android.settings.darkkat.navigationbar;

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.SettingsPreferenceFragment;

public class NavigationBar extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_NAV_BAR_LEFT =
            "nav_bar_left";
    private static final String PREF_NAV_BAR_MENU_ARROW_KEYS =
            "nav_bar_menu_arrow_keys";

    private CheckBoxPreference mNavBarLeft;
    private CheckBoxPreference mNavBarMenuArrowKeys;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.navigation_bar);

        mResolver = getActivity().getContentResolver();

        if (!Utils.isPhone(getActivity())) {
            removePreference(PREF_NAV_BAR_LEFT);
        } else {
            mNavBarLeft =
                    (CheckBoxPreference) findPreference(PREF_NAV_BAR_LEFT);
            mNavBarLeft.setChecked(Settings.System.getInt(mResolver,
                   Settings.System.NAVBAR_LEFT_IN_LANDSCAPE, 0) == 1);
            mNavBarLeft.setOnPreferenceChangeListener(this);
        }

        mNavBarMenuArrowKeys =
                (CheckBoxPreference) findPreference(PREF_NAV_BAR_MENU_ARROW_KEYS);
        mNavBarMenuArrowKeys.setChecked(Settings.System.getInt(mResolver,
               Settings.System.NAVIGATION_BAR_MENU_ARROW_KEYS, 1) == 1);
        mNavBarMenuArrowKeys.setOnPreferenceChangeListener(this);

    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;

        if (preference == mNavBarLeft) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.NAVBAR_LEFT_IN_LANDSCAPE,
                    value ? 1 : 0);
            return true;
        } else if (preference == mNavBarMenuArrowKeys) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_MENU_ARROW_KEYS,
                    value ? 1 : 0);
            return true;
        }
        return false;
    }
}
