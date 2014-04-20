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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.darkkat.AppMultiSelectListPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ListViewSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_LISTVIEW_ANIMATION =
            "listview_animation";
    private static final String PREF_LISTVIEW_INTERPOLATOR =
            "listview_interpolator";
    private static final String PREF_LISTVIEW_EXCLUDED_APPS =
            "listview_blacklist";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private ListPreference mListViewAnimation;
    private ListPreference mListViewInterpolator;
    private AppMultiSelectListPreference mExcludedApps;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.listview_settings);
        mResolver = getActivity().getContentResolver();

        mListViewAnimation =
                (ListPreference) findPreference(PREF_LISTVIEW_ANIMATION);
        int listviewanimation = Settings.System.getInt(getContentResolver(),
                Settings.System.LISTVIEW_ANIMATION, 0);
        mListViewAnimation.setValue(String.valueOf(listviewanimation));
        mListViewAnimation.setSummary(mListViewAnimation.getEntry());
        mListViewAnimation.setOnPreferenceChangeListener(this);

        mListViewInterpolator =
                (ListPreference) findPreference(PREF_LISTVIEW_INTERPOLATOR);
        int listviewinterpolator = Settings.System.getInt(getContentResolver(),
                Settings.System.LISTVIEW_INTERPOLATOR, 0);
        mListViewInterpolator.setValue(String.valueOf(listviewinterpolator));
        mListViewInterpolator.setSummary(mListViewInterpolator.getEntry());
        mListViewInterpolator.setEnabled(listviewanimation > 0);
        mListViewInterpolator.setOnPreferenceChangeListener(this);

        mExcludedApps =
                (AppMultiSelectListPreference) findPreference(PREF_LISTVIEW_EXCLUDED_APPS);
        Set<String> excludedApps = getExcludedApps();
        if (excludedApps != null) {
            mExcludedApps.setValues(excludedApps);
        }
        mExcludedApps.setEnabled(listviewanimation > 0);
        mExcludedApps.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_settings_backup) // use the backup icon
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showDialogInner(DLG_RESET);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
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
            mExcludedApps.setEnabled(intValue > 0);
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
        } else if (preference == mExcludedApps) {
            storeExcludedApps((Set<String>) newValue);
        }
        return false;
    }

    private void showDialogInner(int id) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            frag.setArguments(args);
            return frag;
        }

        ListViewSettings getOwner() {
            return (ListViewSettings) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_reset_blacklist_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putString(getOwner().mResolver,
                                    Settings.System.LISTVIEW_ANIMATION_EXCLUDED_APPS, "");
                            getOwner().mExcludedApps.setClearValues();
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {

        }
    }

    private Set<String> getExcludedApps() {
        String excluded = Settings.System.getString(getContentResolver(),
                Settings.System.LISTVIEW_ANIMATION_EXCLUDED_APPS);
        if (TextUtils.isEmpty(excluded))
            return null;

        return new HashSet<String>(Arrays.asList(excluded.split("\\|")));
    }

    private void storeExcludedApps(Set<String> values) {
        StringBuilder builder = new StringBuilder();
        String delimiter = "";
        for (String value : values) {
            builder.append(delimiter);
            builder.append(value);
            delimiter = "|";
        }
        Settings.System.putString(getContentResolver(),
                Settings.System.LISTVIEW_ANIMATION_EXCLUDED_APPS, builder.toString());
    }
}
