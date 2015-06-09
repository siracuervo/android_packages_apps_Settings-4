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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class RecentAppsSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener { 

    private static final String PREF_CAT_CLEAR_ALL =
            "recent_apps_cat_clear_all_button";
    private static final String PREF_SHOW_SEARCH_BAR =
            "recent_apps_show_search_bar";
    private static final String PREF_SHOW_CLEAR_ALL =
            "recent_apps_show_clear_all";
    private static final String PREF_CLEAR_ALL_POSITION_HORIZONTAL =
            "recent_apps_clear_all_position_horizontal";
    private static final String PREF_CLEAR_ALL_POSITION_VERTICAL =
            "recent_apps_clear_all_position_vertical";
    private static final String PREF_CLEAR_ALL_BG_COLOR =
            "recent_apps_clear_all_bg_color";
    private static final String PREF_CLEAR_ALL_ICON_COLOR =
            "recent_apps_clear_all_icon_color";

    private static final int DEEP_TEAL_500 = 0xff009688;
    private static final int WHITE = 0xffffffff;
    private static final int HOLO_BLUE_LIGHT = 0xff33b5e5;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private SwitchPreference mShowSearchBar;
    private SwitchPreference mShowClearAll;
    private ListPreference mClearAllPositionHorizontal;
    private ListPreference mClearAllPositionVertical;
    private ColorPickerPreference mClearAllIconColor;
    private ColorPickerPreference mClearAllBgColor;

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

        addPreferencesFromResource(R.xml.recent_apps_settings);

        mResolver = getActivity().getContentResolver();
        int intvalue;
        int intColor;
        String hexColor;

        boolean showClearAll = Settings.System.getInt(mResolver,
               Settings.System.RECENT_APPS_SHOW_CLEAR_ALL, 0) == 1;

        mShowSearchBar = (SwitchPreference) findPreference(PREF_SHOW_SEARCH_BAR);
        mShowSearchBar.setChecked(Settings.System.getInt(mResolver,
               Settings.System.RECENT_APPS_SHOW_SEARCH_BAR, 1) == 1);
        mShowSearchBar.setOnPreferenceChangeListener(this);

        mShowClearAll = (SwitchPreference) findPreference(PREF_SHOW_CLEAR_ALL);
        mShowClearAll.setChecked(showClearAll);
        mShowClearAll.setOnPreferenceChangeListener(this);

        PreferenceCategory catClearAll =
                (PreferenceCategory) findPreference(PREF_CAT_CLEAR_ALL);
        mClearAllPositionHorizontal =
                (ListPreference) findPreference(PREF_CLEAR_ALL_POSITION_HORIZONTAL);
        mClearAllPositionVertical =
                (ListPreference) findPreference(PREF_CLEAR_ALL_POSITION_VERTICAL);
        mClearAllBgColor =
                (ColorPickerPreference) findPreference(PREF_CLEAR_ALL_BG_COLOR);
        mClearAllIconColor =
                (ColorPickerPreference) findPreference(PREF_CLEAR_ALL_ICON_COLOR);

        if (showClearAll) {
            intvalue = Settings.System.getInt(mResolver,
                    Settings.System.RECENT_APPS_CLEAR_ALL_POSITION_HORIZONTAL, 2);
            mClearAllPositionHorizontal.setValue(String.valueOf(intvalue));
            mClearAllPositionHorizontal.setSummary(mClearAllPositionHorizontal.getEntry());
            mClearAllPositionHorizontal.setOnPreferenceChangeListener(this);

            intvalue = Settings.System.getInt(mResolver,
                    Settings.System.RECENT_APPS_CLEAR_ALL_POSITION_VERTICAL, 0);
            mClearAllPositionVertical.setValue(String.valueOf(intvalue));
            mClearAllPositionVertical.setSummary(mClearAllPositionVertical.getEntry());
            mClearAllPositionVertical.setOnPreferenceChangeListener(this);

            intColor = Settings.System.getInt(mResolver,
                    Settings.System.RECENT_APPS_CLEAR_ALL_BG_COLOR, DEEP_TEAL_500); 
            mClearAllBgColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mClearAllBgColor.setSummary(hexColor);
            mClearAllBgColor.setDefaultColors(DEEP_TEAL_500, DEEP_TEAL_500);
            mClearAllBgColor.setOnPreferenceChangeListener(this);

            intColor = Settings.System.getInt(mResolver,
                    Settings.System.RECENT_APPS_CLEAR_ALL_ICON_COLOR, WHITE); 
            mClearAllIconColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mClearAllIconColor.setSummary(hexColor);
            mClearAllIconColor.setDefaultColors(WHITE, HOLO_BLUE_LIGHT);
            mClearAllIconColor.setOnPreferenceChangeListener(this);
        } else {
            catClearAll.removePreference(mClearAllPositionHorizontal);
            catClearAll.removePreference(mClearAllPositionVertical);
            catClearAll.removePreference(mClearAllBgColor);
            catClearAll.removePreference(mClearAllIconColor);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_menu_reset) // use the KitKat backup icon
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
        boolean value;
        int intvalue;
        int index;
        String hex;
        int intHex;

        if (preference == mShowSearchBar) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.RECENT_APPS_SHOW_SEARCH_BAR,
                    value ? 1 : 0);
            return true;
        } else if (preference == mShowClearAll) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.RECENT_APPS_SHOW_CLEAR_ALL,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mClearAllPositionHorizontal) {
            intvalue = Integer.valueOf((String) newValue);
            index = mClearAllPositionHorizontal.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENT_APPS_CLEAR_ALL_POSITION_HORIZONTAL,
                    intvalue);
            preference.setSummary(mClearAllPositionHorizontal.getEntries()[index]);
            return true;
        } else if (preference == mClearAllPositionVertical) {
            intvalue = Integer.valueOf((String) newValue);
            index = mClearAllPositionVertical.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENT_APPS_CLEAR_ALL_POSITION_VERTICAL,
                    intvalue);
            preference.setSummary(mClearAllPositionVertical.getEntries()[index]);
            return true;
        } else if (preference == mClearAllBgColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENT_APPS_CLEAR_ALL_BG_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mClearAllIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENT_APPS_CLEAR_ALL_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
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

        RecentAppsSettings getOwner() {
            return (RecentAppsSettings) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_reset_values_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.dlg_reset_android,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_SHOW_SEARCH_BAR, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_SHOW_CLEAR_ALL, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_CLEAR_ALL_POSITION_HORIZONTAL, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_CLEAR_ALL_POSITION_VERTICAL, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_CLEAR_ALL_BG_COLOR,
                                    DEEP_TEAL_500);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_CLEAR_ALL_ICON_COLOR,
                                    WHITE);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_SHOW_SEARCH_BAR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_SHOW_CLEAR_ALL, 1);
                             Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_CLEAR_ALL_POSITION_HORIZONTAL, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_CLEAR_ALL_POSITION_VERTICAL, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_CLEAR_ALL_BG_COLOR,
                                    DEEP_TEAL_500);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENT_APPS_CLEAR_ALL_ICON_COLOR,
                                    HOLO_BLUE_LIGHT);
                            getOwner().refreshSettings();
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
