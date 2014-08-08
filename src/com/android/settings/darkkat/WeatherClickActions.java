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

import android.app.Activity;
import android.app.AlarmManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import com.android.internal.util.darkkat.AppHelper;
import com.android.internal.util.darkkat.ButtonsConstants;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.darkkat.util.ShortcutPickerHelper;

public class WeatherClickActions extends SettingsPreferenceFragment implements
        ShortcutPickerHelper.OnPickListener, Preference.OnPreferenceChangeListener {

    private static final String PREF_WEATHER_CLICK_TOP_BAR_SETTINGS =
            "weather_click_top_bar_settings";
    private static final String PREF_WEATHER_CLICK_LEFT_PANEL =
            "weather_click_left_panel";
    private static final String PREF_WEATHER_LONG_CLICK_LEFT_PANEL =
            "weather_long_click_left_panel";
    private static final String PREF_WEATHER_CLICK_IMAGE =
            "weather_click_image";
    private static final String PREF_WEATHER_LONG_CLICK_IMAGE =
            "weather_long_click_image";
    private static final String PREF_WEATHER_CLICK_RIGHT_PANEL =
            "weather_click_right_panel";
    private static final String PREF_WEATHER_LONG_CLICK_RIGHT_PANEL =
            "weather_long_click_right_panel";

    private ListPreference mClickSettings;
    private ListPreference mClickLeftPanel;
    private ListPreference mLongClickLeftPanel;
    private ListPreference mClickImage;
    private ListPreference mLongClickImage;
    private ListPreference mClickRightPanel;
    private ListPreference mLongClickRightPanel;

    private Activity mActivity;
    private PackageManager mPm;
    private ShortcutPickerHelper mPicker;
    private Preference mPreference;
    private String mString;
    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.weather_click_actions);

        mActivity = getActivity();
        mPm = mActivity.getPackageManager();
        mPicker = new ShortcutPickerHelper(mActivity, this);
        mResolver = getActivity().getContentResolver();

        mClickSettings =
                (ListPreference) findPreference(PREF_WEATHER_CLICK_TOP_BAR_SETTINGS);
        setClickPreferenceSummary(mClickSettings);
        mClickSettings.setOnPreferenceChangeListener(this);

        mClickLeftPanel =
                (ListPreference) findPreference(PREF_WEATHER_CLICK_LEFT_PANEL);
        setClickPreferenceSummary(mClickLeftPanel);
        mClickLeftPanel.setOnPreferenceChangeListener(this);

        mLongClickLeftPanel =
                (ListPreference) findPreference(PREF_WEATHER_LONG_CLICK_LEFT_PANEL);
        setClickPreferenceSummary(mLongClickLeftPanel);
        mLongClickLeftPanel.setOnPreferenceChangeListener(this);

        mClickImage =
                (ListPreference) findPreference(PREF_WEATHER_CLICK_IMAGE);
        setClickPreferenceSummary(mClickImage);
        mClickImage.setOnPreferenceChangeListener(this);

        mLongClickImage =
                (ListPreference) findPreference(PREF_WEATHER_LONG_CLICK_IMAGE);
        setClickPreferenceSummary(mLongClickImage);
        mLongClickImage.setOnPreferenceChangeListener(this);

        mClickRightPanel =
                (ListPreference) findPreference(PREF_WEATHER_CLICK_RIGHT_PANEL);
        setClickPreferenceSummary(mClickRightPanel);
        mClickRightPanel.setOnPreferenceChangeListener(this);

        mLongClickRightPanel =
                (ListPreference) findPreference(PREF_WEATHER_LONG_CLICK_RIGHT_PANEL);
        setClickPreferenceSummary(mLongClickRightPanel);
        mLongClickRightPanel.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean result = false;

        if (preference == mClickSettings) {
            Settings.System.putString(getContentResolver(),
                    Settings.System.STATUS_BAR_EXPANDED_WEATHER_CLICK_TOP_BAR_SETTINGS,
                    (String) newValue);
            setClickPreferenceSummary(preference);
            return true;
        } else if (preference == mClickLeftPanel) {
            mPreference = preference;
            mString = Settings.System.STATUS_BAR_EXPANDED_WEATHER_CLICK_LEFT_PANEL;
            if (newValue.equals(ButtonsConstants.ACTION_APP)) {
                mPicker.pickShortcut(getId());
            } else {
                result = Settings.System.putString(getContentResolver(),
                        Settings.System.STATUS_BAR_EXPANDED_WEATHER_CLICK_LEFT_PANEL,
                        (String) newValue);
                setClickPreferenceSummary(preference);
            }
            return true;
        } else if (preference == mLongClickLeftPanel) {
            mPreference = preference;
            mString = Settings.System.STATUS_BAR_EXPANDED_WEATHER_LONG_CLICK_LEFT_PANEL;
            if (newValue.equals(ButtonsConstants.ACTION_APP)) {
                mPicker.pickShortcut(getId());
            } else {
                result = Settings.System.putString(getContentResolver(),
                        Settings.System.STATUS_BAR_EXPANDED_WEATHER_LONG_CLICK_LEFT_PANEL,
                        (String) newValue);
                setClickPreferenceSummary(preference);
            }
            return true;
        } else if (preference == mClickImage) {
            mPreference = preference;
            mString = Settings.System.STATUS_BAR_EXPANDED_WEATHER_CLICK_IMAGE;
            if (newValue.equals(ButtonsConstants.ACTION_APP)) {
                mPicker.pickShortcut(getId());
            } else {
                result = Settings.System.putString(getContentResolver(),
                        Settings.System.STATUS_BAR_EXPANDED_WEATHER_CLICK_IMAGE,
                        (String) newValue);
                setClickPreferenceSummary(preference);
            }
            return true;
        } else if (preference == mLongClickImage) {
            mPreference = preference;
            mString = Settings.System.STATUS_BAR_EXPANDED_WEATHER_LONG_CLICK_IMAGE;
            if (newValue.equals(ButtonsConstants.ACTION_APP)) {
                mPicker.pickShortcut(getId());
            } else {
                result = Settings.System.putString(getContentResolver(),
                        Settings.System.STATUS_BAR_EXPANDED_WEATHER_LONG_CLICK_IMAGE,
                        (String) newValue);
                setClickPreferenceSummary(preference);
            }
            return true;
       } else if (preference == mClickRightPanel) {
            mPreference = preference;
            mString = Settings.System.STATUS_BAR_EXPANDED_WEATHER_CLICK_RIGHT_PANEL;
            if (newValue.equals(ButtonsConstants.ACTION_APP)) {
                mPicker.pickShortcut(getId());
            } else {
                result = Settings.System.putString(getContentResolver(),
                        Settings.System.STATUS_BAR_EXPANDED_WEATHER_CLICK_RIGHT_PANEL,
                        (String) newValue);
                setClickPreferenceSummary(preference);
            }
            return true;
        } else if (preference == mLongClickRightPanel) {
            mPreference = preference;
            mString = Settings.System.STATUS_BAR_EXPANDED_WEATHER_LONG_CLICK_RIGHT_PANEL;
            if (newValue.equals(ButtonsConstants.ACTION_APP)) {
                mPicker.pickShortcut(getId());
            } else {
                result = Settings.System.putString(getContentResolver(),
                        Settings.System.STATUS_BAR_EXPANDED_WEATHER_LONG_CLICK_RIGHT_PANEL,
                        (String) newValue);
                setClickPreferenceSummary(preference);
            }
            return true;
         }
         return false;
    }

    @Override
    public void shortcutPicked(String uri, String friendlyName, Bitmap bmp, boolean isApplication) {
          Settings.System.putString(getContentResolver(), mString, (String) uri);
          mPreference.setSummary(friendlyName);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == mActivity.RESULT_OK) {
            if (requestCode == ShortcutPickerHelper.REQUEST_PICK_SHORTCUT
                    || requestCode == ShortcutPickerHelper.REQUEST_PICK_APPLICATION
                    || requestCode == ShortcutPickerHelper.REQUEST_CREATE_SHORTCUT) {
                mPicker.onActivityResult(requestCode, resultCode, data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setClickPreferenceSummary(Preference preference) {
        if (preference == mClickSettings) {
            mString = Settings.System.STATUS_BAR_EXPANDED_WEATHER_CLICK_TOP_BAR_SETTINGS;
        } else if (preference == mClickLeftPanel) {
            mString = Settings.System.STATUS_BAR_EXPANDED_WEATHER_CLICK_LEFT_PANEL;
        } else if (preference == mLongClickLeftPanel) {
            mString = Settings.System.STATUS_BAR_EXPANDED_WEATHER_LONG_CLICK_LEFT_PANEL;
        } else if (preference == mClickImage) {
            mString = Settings.System.STATUS_BAR_EXPANDED_WEATHER_CLICK_IMAGE;
        } else if (preference == mLongClickImage) {
            mString = Settings.System.STATUS_BAR_EXPANDED_WEATHER_LONG_CLICK_IMAGE;
        } else if (preference == mClickRightPanel) {
            mString = Settings.System.STATUS_BAR_EXPANDED_WEATHER_CLICK_RIGHT_PANEL;
        } else if (preference == mLongClickRightPanel) {
            mString = Settings.System.STATUS_BAR_EXPANDED_WEATHER_LONG_CLICK_RIGHT_PANEL;
        }

        String uri = Settings.System.getString(mResolver, mString);

        if (preference == mClickSettings) {
            preference.setSummary(AppHelper.getProperSummary(
                    mActivity, mPm, getResources(), uri,
                    "weather_click_top_bar_settings_values",
                    "weather_click_top_bar_settings_entries"));
        } else {
            preference.setSummary(AppHelper.getProperSummary(
                    mActivity, mPm, getResources(), uri,
                    "weather_click_values", "weather_click_entries"));
        }
    }
}
