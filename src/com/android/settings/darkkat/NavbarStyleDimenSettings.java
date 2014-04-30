/*
 * Copyright (C) 2012 Slimroms
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
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.internal.util.darkkat.DeviceUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class NavbarStyleDimenSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_CAT_LANDSCAPE =
            "navigation_bar_cat_landscape";
    private static final String PREF_NAVIGATION_BAR_HEIGHT =
            "navigation_bar_height";
    private static final String PREF_NAVIGATION_BAR_POSITION =
            "navigation_bar_position";
    private static final String PREF_NAVIGATION_BAR_HEIGHT_LANDSCAPE =
            "navigation_bar_height_landscape";
    private static final String PREF_NAVIGATION_BAR_WIDTH =
            "navigation_bar_width";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    ListPreference mNavigationBarHeight;
    CheckBoxPreference mNavigationBarPosition;
    ListPreference mNavigationBarHeightLandscape;
    ListPreference mNavigationBarWidth;

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

        addPreferencesFromResource(R.xml.navbar_style_dimen_settings);

        mNavigationBarHeight =
            (ListPreference) findPreference(PREF_NAVIGATION_BAR_HEIGHT);
        mNavigationBarHeight.setSummary(
                mNavigationBarHeight.getEntry());
        mNavigationBarHeight.setOnPreferenceChangeListener(this);

        boolean navbarShowBottom = Settings.System.getInt(getContentResolver(),
                Settings.System.NAVIGATION_BAR_CAN_MOVE,
                DeviceUtils.isPhone(getActivity()) ? 1 : 0) == 0;

        PreferenceCategory catLandscape =
                (PreferenceCategory) findPreference(PREF_CAT_LANDSCAPE);
        mNavigationBarPosition =
                (CheckBoxPreference) findPreference(PREF_NAVIGATION_BAR_POSITION);
        if (DeviceUtils.isPhone(getActivity())) {
            mNavigationBarPosition.setChecked(navbarShowBottom);
            mNavigationBarPosition.setOnPreferenceChangeListener(this);
        } else {
            catLandscape.removePreference(mNavigationBarPosition);
            if (!navbarShowBottom) {
                Settings.System.putInt(getContentResolver(),
                        Settings.System.NAVIGATION_BAR_CAN_MOVE, 0);
            }
        }

        mNavigationBarHeightLandscape =
            (ListPreference) findPreference(PREF_NAVIGATION_BAR_HEIGHT_LANDSCAPE);
        mNavigationBarWidth =
            (ListPreference) findPreference(PREF_NAVIGATION_BAR_WIDTH);
        if (navbarShowBottom) {
            mNavigationBarHeightLandscape.setSummary(
                    mNavigationBarHeightLandscape.getEntry());
            mNavigationBarHeightLandscape.setOnPreferenceChangeListener(this);
            catLandscape.removePreference(mNavigationBarWidth);
        } else {
            mNavigationBarWidth.setSummary(
                    mNavigationBarWidth.getEntry());
            mNavigationBarWidth.setOnPreferenceChangeListener(this);
            catLandscape.removePreference(mNavigationBarHeightLandscape);
        }

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

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int index;
        String newVal;
        int dp;
        int value;

        if (preference == mNavigationBarHeight) {
            index = mNavigationBarHeight.findIndexOfValue(
                    (String) newValue);
            newVal = (String) newValue;
            dp = Integer.parseInt(newVal);
            value = mapChosenDpToPixels(dp);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NAVIGATION_BAR_HEIGHT, value);
            mNavigationBarHeight.setSummary(
                mNavigationBarHeight.getEntries()[index]);
            return true;
        } else if (preference == mNavigationBarPosition) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_CAN_MOVE,
                    ((Boolean) newValue) ? 0 : 1);
            refreshSettings();
            return true;
        } else if (preference == mNavigationBarWidth) {
            index = mNavigationBarWidth.findIndexOfValue(
                    (String) newValue);
            newVal = (String) newValue;
            dp = Integer.parseInt(newVal);
            value = mapChosenDpToPixels(dp);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NAVIGATION_BAR_WIDTH, value);
            mNavigationBarWidth.setSummary(
                mNavigationBarWidth.getEntries()[index]);
            return true;
        } else if (preference == mNavigationBarHeightLandscape) {
            index = mNavigationBarHeightLandscape.findIndexOfValue(
                    (String) newValue);
            newVal = (String) newValue;
            dp = Integer.parseInt(newVal);
            value = mapChosenDpToPixels(dp);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.NAVIGATION_BAR_HEIGHT_LANDSCAPE, value);
            mNavigationBarHeightLandscape.setSummary(
                mNavigationBarHeightLandscape.getEntries()[index]);
            return true;
        }
        return false;
    }

    public int mapChosenDpToPixels(int dp) {
        switch (dp) {
            case 48:
                return getResources().getDimensionPixelSize(R.dimen.navigation_bar_48);
            case 44:
                return getResources().getDimensionPixelSize(R.dimen.navigation_bar_44);
            case 42:
                return getResources().getDimensionPixelSize(R.dimen.navigation_bar_42);
            case 40:
                return getResources().getDimensionPixelSize(R.dimen.navigation_bar_40);
            case 36:
                return getResources().getDimensionPixelSize(R.dimen.navigation_bar_36);
            case 30:
                return getResources().getDimensionPixelSize(R.dimen.navigation_bar_30);
            case 24:
                return getResources().getDimensionPixelSize(R.dimen.navigation_bar_24);
            case 0:
                return 0;
        }
        return -1;
    }

    @Override
    public void onResume() {
        super.onResume();
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

        NavbarStyleDimenSettings getOwner() {
            return (NavbarStyleDimenSettings) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.navbar_dimensions_reset_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.dlg_ok,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            int height = getOwner().mapChosenDpToPixels(48);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.NAVIGATION_BAR_HEIGHT_LANDSCAPE,
                                    height);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.NAVIGATION_BAR_HEIGHT,
                                    height);
                            height = getOwner().mapChosenDpToPixels(42);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.NAVIGATION_BAR_WIDTH,
                                    height);
                            getOwner().mNavigationBarHeight.setValue("48");
                            getOwner().mNavigationBarHeightLandscape.setValue("48");
                            getOwner().mNavigationBarWidth.setValue("42");
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
}
