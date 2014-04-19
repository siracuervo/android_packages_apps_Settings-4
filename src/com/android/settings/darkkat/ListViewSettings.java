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

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class ListViewSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_LISTVIEW_ANIMATION =
            "listview_animation";
    private static final String PREF_LISTVIEW_INTERPOLATOR =
            "listview_interpolator";

    private ListPreference mListViewAnimation;
    private ListPreference mListViewInterpolator;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.listview_settings);
        mResolver = getActivity().getContentResolver();

        mListViewAnimation = (ListPreference) findPreference(PREF_LISTVIEW_ANIMATION);
        int listviewanimation = Settings.System.getInt(getContentResolver(),
                Settings.System.LISTVIEW_ANIMATION, 0);
        mListViewAnimation.setValue(String.valueOf(listviewanimation));
        mListViewAnimation.setSummary(mListViewAnimation.getEntry());
        mListViewAnimation.setOnPreferenceChangeListener(this);

        mListViewInterpolator = (ListPreference) findPreference(PREF_LISTVIEW_INTERPOLATOR);
        int listviewinterpolator = Settings.System.getInt(getContentResolver(),
                Settings.System.LISTVIEW_INTERPOLATOR, 0);
        mListViewInterpolator.setValue(String.valueOf(listviewinterpolator));
        mListViewInterpolator.setSummary(mListViewInterpolator.getEntry());
        mListViewInterpolator.setOnPreferenceChangeListener(this);
        mListViewInterpolator.setEnabled(listviewanimation > 0);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intValue;
        int index;

        if (preference == mListViewAnimation) {
            intValue = Integer.valueOf((String) newValue);
            index =
                mListViewAnimation.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.LISTVIEW_ANIMATION, intValue);
            mListViewAnimation.setSummary(
                mListViewAnimation.getEntries()[index]);
            mListViewInterpolator.setEnabled(intValue > 0);
            return true;
        } else if (preference == mListViewInterpolator) {
            intValue = Integer.valueOf((String) newValue);
            index =
                mListViewInterpolator.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.LISTVIEW_INTERPOLATOR, intValue);
            mListViewInterpolator.setSummary(
                mListViewInterpolator.getEntries()[index]);
            return true;
        }
        return false;
    }
}
