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
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.widget.SeekBarPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class InterfaceMenusSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "PowerMenu";
    private static final String PREF_POWER_MENU_CAT =
            "power_menu_cat";
    private static final String PREF_POWER_MENU_TEXT_COLOR =
            "power_menu_text_color";
    private static final String PREF_POWER_MENU_ICON_COLOR_MODE =
            "power_menu_icon_color_mode";
    private static final String PREF_POWER_MENU_ICON_COLOR =
            "power_menu_icon_color";
    private static final String PREF_RECENTS_SCREEN_BG_COLOR =
            "recents_screen_bg_color";
    private static final String PREF_RECENTS_SCREEN_EMPTY_ICON_COLOR =
            "recents_screen_empty_icon_color";
    private static final String PREF_RECENT_SCREEN_LEFTY_MODE =
            "recent_screen_lefty_mode";
    private static final String PREF_RECENT_SCREEN_SCALE =
            "recent_screen_scale";

    private static final int DEFAULT_POWER_MENU_TEXT_COLOR =
            0xffffffff;
    private static final int DEFAULT_POWER_MENU_ICON_COLOR =
            0xffffffff;
    private static final int DEFAULT_RECENTS_SCREEN_BG_COLOR =
            0xe6000000;
    private static final int DEFAULT_RECENTS_SCREEN_EMPTY_ICON_COLOR =
            0xffcdcdcd;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private ColorPickerPreference mPowerMenuTextColor;
    private ListPreference mPowerMenuIconColorMode;
    private ColorPickerPreference mPowerMenuIconColor;
    private ColorPickerPreference mRecentsScreenBgColor;
    private ColorPickerPreference mRecentsScreenIconColor;
    private CheckBoxPreference mRecentScreenLeftyMode;
    private ListPreference mRecentScreenScale;

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

        int intColor;
        String hexColor;

        addPreferencesFromResource(R.xml.interface_menus_settings);

        mResolver = getActivity().getContentResolver();

        mPowerMenuTextColor =
                (ColorPickerPreference) findPreference(PREF_POWER_MENU_TEXT_COLOR);
        mPowerMenuTextColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.POWER_MENU_TEXT_COLOR, DEFAULT_POWER_MENU_TEXT_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mPowerMenuTextColor.setSummary(hexColor);
        mPowerMenuTextColor.setNewPreviewColor(intColor);

        mPowerMenuIconColorMode =
            (ListPreference) findPreference(PREF_POWER_MENU_ICON_COLOR_MODE);
        int powerMenuIconColorMode = Settings.System.getInt(getContentResolver(),
                Settings.System.POWER_MENU_ICON_COLOR_MODE, 0);
        mPowerMenuIconColorMode.setValue(String.valueOf(powerMenuIconColorMode));
        mPowerMenuIconColorMode.setSummary(mPowerMenuIconColorMode.getEntry());
        mPowerMenuIconColorMode.setOnPreferenceChangeListener(this);

        PreferenceCategory powerMenuCat =
                (PreferenceCategory) findPreference(PREF_POWER_MENU_CAT);
        mPowerMenuIconColor =
                (ColorPickerPreference) findPreference(PREF_POWER_MENU_ICON_COLOR);
        // Remove color preferences if color mode is set do disabled
        if (powerMenuIconColorMode != 3) {
            mPowerMenuIconColor.setOnPreferenceChangeListener(this);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.POWER_MENU_ICON_COLOR,
                    DEFAULT_POWER_MENU_ICON_COLOR);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mPowerMenuIconColor.setSummary(hexColor);
            mPowerMenuIconColor.setNewPreviewColor(intColor);
        } else {
            powerMenuCat.removePreference(mPowerMenuIconColor);
        }

        mRecentsScreenBgColor =
                (ColorPickerPreference) findPreference(PREF_RECENTS_SCREEN_BG_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.RECENTS_SCREEN_BG_COLOR,
                DEFAULT_RECENTS_SCREEN_BG_COLOR);
        mRecentsScreenBgColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mRecentsScreenBgColor.setSummary(hexColor);
        mRecentsScreenBgColor.setAlphaSliderEnabled(true);
        mRecentsScreenBgColor.setOnPreferenceChangeListener(this);

        mRecentsScreenIconColor =
                (ColorPickerPreference) findPreference(PREF_RECENTS_SCREEN_EMPTY_ICON_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.RECENTS_SCREEN_EMPTY_ICON_COLOR,
                DEFAULT_RECENTS_SCREEN_EMPTY_ICON_COLOR);
        mRecentsScreenIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mRecentsScreenIconColor.setSummary(hexColor);
        mRecentsScreenIconColor.setOnPreferenceChangeListener(this);

        mRecentScreenLeftyMode =
                (CheckBoxPreference) findPreference(PREF_RECENT_SCREEN_LEFTY_MODE);
        final boolean recentLeftyMode = Settings.System.getInt(getContentResolver(),
                Settings.System.RECENT_PANEL_GRAVITY, Gravity.RIGHT) == Gravity.LEFT;
        mRecentScreenLeftyMode.setChecked(recentLeftyMode);
        mRecentScreenLeftyMode.setOnPreferenceChangeListener(this);

        mRecentScreenScale =
                (ListPreference) findPreference(PREF_RECENT_SCREEN_SCALE);
        final int recentScale = Settings.System.getInt(getContentResolver(),
                Settings.System.RECENT_PANEL_SCALE_FACTOR, 100);
        mRecentScreenScale.setValue(recentScale + "");
        mRecentScreenScale.setOnPreferenceChangeListener(this);

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
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        String hex;
        int intHex;

        if (preference == mPowerMenuTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_TEXT_COLOR,
                    intHex);
            return true;
        } else if (preference == mPowerMenuIconColorMode) {
            int index = mPowerMenuIconColorMode.findIndexOfValue((String) objValue);
            int value = Integer.valueOf((String) objValue);
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_ICON_COLOR_MODE, value);
            mPowerMenuIconColorMode.setSummary(
                mPowerMenuIconColorMode.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mPowerMenuIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_ICON_COLOR,
                    intHex);
            return true;
        } else if (preference == mRecentsScreenBgColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.RECENTS_SCREEN_BG_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mRecentsScreenIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.RECENTS_SCREEN_EMPTY_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mRecentScreenScale) {
            int value = Integer.parseInt((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.RECENT_PANEL_SCALE_FACTOR, value);
            return true;
        } else if (preference == mRecentScreenLeftyMode) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.RECENT_PANEL_GRAVITY,
                    ((Boolean) objValue) ? Gravity.LEFT : Gravity.RIGHT);
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

        InterfaceMenusSettings getOwner() {
            return (InterfaceMenusSettings) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_reset_color_values_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.dlg_reset_android,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_TEXT_COLOR,
                                    DEFAULT_POWER_MENU_TEXT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_ICON_COLOR_MODE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_ICON_COLOR,
                                    DEFAULT_POWER_MENU_ICON_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_SCREEN_BG_COLOR,
                                    DEFAULT_RECENTS_SCREEN_BG_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_SCREEN_EMPTY_ICON_COLOR,
                                    DEFAULT_RECENTS_SCREEN_EMPTY_ICON_COLOR);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_TEXT_COLOR,
                                    DEFAULT_POWER_MENU_TEXT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_ICON_COLOR_MODE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_ICON_COLOR,
                                    0xff33b5e5);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_SCREEN_BG_COLOR,
                                    DEFAULT_RECENTS_SCREEN_BG_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.RECENTS_SCREEN_EMPTY_ICON_COLOR,
                                    DEFAULT_RECENTS_SCREEN_EMPTY_ICON_COLOR);
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
