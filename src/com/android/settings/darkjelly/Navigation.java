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
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class Navigation extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String PREF_CATEGORY_NAVIGATION_BAR = "category_nav_bar";
    private static final String PREF_CATEGORY_PIE_CONTROL = "category_pie_control";
    private static final String PREF_SHOW_NAVIGATION_BAR = "show_navigation_bar";
    private static final String PREF_SHOW_PIE_CONTROL = "show_pie_control";

    private CheckBoxPreference mShowNavigationBar;
    private CheckBoxPreference mShowPieControl;
    private PreferenceScreen mPieControl;
    private PreferenceScreen mPieControlStyle;

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

        addPreferencesFromResource(R.xml.navigation);

        mResolver = getActivity().getContentResolver();

        boolean hasNavBarByDefault = getActivity().getResources().getBoolean(
                com.android.internal.R.bool.config_showNavigationBar);
        boolean isNavigationBarEnabled = Settings.System.getInt(mResolver,
               Settings.System.NAVIGATION_BAR_SHOW, 0) == 1;
        boolean isPieControlEnabled = Settings.System.getInt(mResolver,
               Settings.System.PIE_CONTROLS, 0) == 1;

        // Remove the Show navigation bar checkbox on devices without hardware keys
        PreferenceCategory navBarCategory = (PreferenceCategory) findPreference(PREF_CATEGORY_NAVIGATION_BAR);
        mShowNavigationBar = (CheckBoxPreference) findPreference(PREF_SHOW_NAVIGATION_BAR);
        if (!hasNavBarByDefault) {
            mShowNavigationBar.setChecked(isNavigationBarEnabled);
            mShowNavigationBar.setOnPreferenceChangeListener(this);
        } else {
            navBarCategory.removePreference(mShowNavigationBar);
        }

        mShowPieControl = (CheckBoxPreference) findPreference(PREF_SHOW_PIE_CONTROL);
        mShowPieControl.setChecked(isPieControlEnabled);
        mShowPieControl.setOnPreferenceChangeListener(this);

        // Remove uneeded preferences depending on enabled states
        // (navigation bar preferences only on devices with hardware keys)
        PreferenceCategory pieControlCategory = (PreferenceCategory) findPreference(PREF_CATEGORY_PIE_CONTROL);
        PreferenceScreen navButtonsEdit = (PreferenceScreen) findPreference("nav_buttons_edit");
        PreferenceScreen navBarStyle = (PreferenceScreen) findPreference("navigation_bar_style");
        PreferenceScreen pieControl = (PreferenceScreen) findPreference("pie_control");
        PreferenceScreen pieControlStyle = (PreferenceScreen) findPreference("pie_control_style");

        if (!hasNavBarByDefault && !isNavigationBarEnabled) {
            navBarCategory.removePreference(navButtonsEdit);
            navBarCategory.removePreference(navBarStyle);
        }

        if (!isPieControlEnabled) {
            pieControlCategory.removePreference(pieControl);
            pieControlCategory.removePreference(pieControlStyle);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mShowNavigationBar) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.NAVIGATION_BAR_SHOW, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowPieControl) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.PIE_CONTROLS, value ? 1 : 0);
            refreshSettings();
            return true;
        }
        return false;
    }
}
