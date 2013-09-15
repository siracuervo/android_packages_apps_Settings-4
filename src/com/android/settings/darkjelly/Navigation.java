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
    private static final String PREF_PIE_CONTROL = "pie_control";
    private static final String PREF_PIE_CONTROL_STYLE = "pie_control_style";
    private static final String PREF_SHOW_NAVIGATION_BAR = "show_navigation_bar";

    private PreferenceScreen mPieControl;
    private PreferenceScreen mPieControlStyle;
    private CheckBoxPreference mShowNavigationBar;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.navigation);

        mResolver = getActivity().getContentResolver();

        boolean hasNavBarByDefault = getActivity().getResources().getBoolean(
                com.android.internal.R.bool.config_showNavigationBar);

        PreferenceCategory navBarCategory = (PreferenceCategory) findPreference(PREF_CATEGORY_NAVIGATION_BAR);
        mPieControl = (PreferenceScreen) findPreference(PREF_PIE_CONTROL);
        mPieControlStyle = (PreferenceScreen) findPreference(PREF_PIE_CONTROL_STYLE);

        mShowNavigationBar = (CheckBoxPreference) findPreference(PREF_SHOW_NAVIGATION_BAR);
        if (!hasNavBarByDefault) {
            mShowNavigationBar.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.NAVIGATION_BAR_SHOW, 0) == 1);
            mShowNavigationBar.setOnPreferenceChangeListener(this);
        } else {
            if (mShowNavigationBar != null) {
                navBarCategory.removePreference(mShowNavigationBar);
            }
        }

        updatePieControlStateAndDescription();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mShowNavigationBar) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.NAVIGATION_BAR_SHOW, value ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mPieControl != null) {
            updatePieControlStateAndDescription();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void updatePieControlStateAndDescription() {
        if (Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.PIE_CONTROLS, 0) == 1) {
            mPieControl.setSummary(getString(R.string.pie_control_enabled));
            mPieControlStyle.setEnabled(true);
        } else {
            mPieControl.setSummary(getString(R.string.pie_control_disabled));
            mPieControlStyle.setEnabled(false);
        }
    }
}
