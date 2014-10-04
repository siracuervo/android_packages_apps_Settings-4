/*
 * Copyright (C) 2014 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.darkkat.headsup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class HeadsUpSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_HEADS_UP_EXPANDED =
            "heads_up_expanded";
    private static final String PREF_HEADS_UP_SHOW_UPDATE =
            "heads_up_show_update";
    private static final String PREF_HEADS_UP_GRAVITY =
            "heads_up_gravity";
    private static final String PREF_HEADS_UP_SNOOZE_TIME =
            "heads_up_snooze_time";
    private static final String PREF_HEADS_UP_TIMEOUT =
            "heads_up_timeout";
    private static final String PREF_HEADS_UP_USE_CUSTOM_TIMEOUT_FS =
            "heads_up_use_custom_timeout_fs";
    private static final String PREF_HEADS_UP_BG_COLOR =
            "heads_up_bg_color";
    private static final String PREF_HEADS_UP_BG_PRESSED_COLOR =
            "heads_up_bg_pressed_color";
    private static final String PREF_HEADS_UP_COLORIZE_NOTIF_ICONS =
            "heads_up_colorize_notif_icons";
    private static final String PREF_HEADS_UP_ICON_COLOR =
            "heads_up_icon_color";
    private static final String PREF_HEADS_UP_TEXT_COLOR =
            "heads_up_text_color";

    protected static final int DEFAULT_TIME_HEADS_UP_SNOOZE = 300000;
    private static final int DEFAULT_BACKGROUND_COLOR = 0xff181818;
    private static final int DEFAULT_BACKGROUND_PRESSED_COLOR = 0xff454545;
    private static final int DEFAULT_WHITE_COLOR = 0xffffffff;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private CheckBoxPreference mHeadsUpExpanded;
    private CheckBoxPreference mHeadsUpShowUpdates;
    private CheckBoxPreference mHeadsUpGravity;
    private ListPreference mHeadsUpSnoozeTime;
    private ListPreference mTimeout;
    private CheckBoxPreference mUseCustomTimeoutFs;
    private ColorPickerPreference mHeadsUpBgColor;
    private ColorPickerPreference mHeadsUpBgPressedColor;
    private CheckBoxPreference mHeadsUpColorizeNotifIcons;
    private ColorPickerPreference mHeadsUpIconColor;
    private ColorPickerPreference mHeadsUpTextColor;

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
        addPreferencesFromResource(R.xml.heads_up);
        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        mHeadsUpExpanded =
                (CheckBoxPreference) findPreference(PREF_HEADS_UP_EXPANDED);
        mHeadsUpExpanded.setChecked(Settings.System.getInt(mResolver,
                Settings.System.HEADS_UP_EXPANDED, 0) == 1);
        mHeadsUpExpanded.setOnPreferenceChangeListener(this);

        mHeadsUpShowUpdates =
                (CheckBoxPreference) findPreference(PREF_HEADS_UP_SHOW_UPDATE);
        mHeadsUpShowUpdates.setChecked(Settings.System.getInt(mResolver,
                Settings.System.HEADS_UP_SHOW_UPDATE, 0) == 1);
        mHeadsUpShowUpdates.setOnPreferenceChangeListener(this);

        mHeadsUpGravity =
                (CheckBoxPreference) findPreference(PREF_HEADS_UP_GRAVITY);
        mHeadsUpGravity.setChecked(Settings.System.getInt(mResolver,
                Settings.System.HEADS_UP_GRAVITY_BOTTOM, 0) == 1);
        mHeadsUpGravity.setOnPreferenceChangeListener(this);

        mHeadsUpSnoozeTime =
                (ListPreference) findPreference(PREF_HEADS_UP_SNOOZE_TIME);
        int headsUpSnoozeTime = Settings.System.getInt(mResolver,
                Settings.System.HEADS_UP_SNOOZE_TIME,
                DEFAULT_TIME_HEADS_UP_SNOOZE);
        mHeadsUpSnoozeTime.setValue(String.valueOf(headsUpSnoozeTime));
        updateHeadsUpSnoozeTimeSummary(headsUpSnoozeTime);
        mHeadsUpSnoozeTime.setOnPreferenceChangeListener(this);

        mTimeout =
                (ListPreference) findPreference(PREF_HEADS_UP_TIMEOUT);
        int timeout = Settings.System.getInt(mResolver,
                Settings.System.HEADS_UP_TIMEOUT, 4000);
        mTimeout.setValue(String.valueOf(timeout));
        updateHeadsUpTimeoutSummary(timeout);
        mTimeout.setOnPreferenceChangeListener(this);

        mUseCustomTimeoutFs =
                (CheckBoxPreference) findPreference(PREF_HEADS_UP_USE_CUSTOM_TIMEOUT_FS);
        mUseCustomTimeoutFs.setChecked(Settings.System.getInt(mResolver,
                Settings.System.HEADS_UP_USE_CUSTOM_TIMEOUT_FS, 0) == 1);
        mUseCustomTimeoutFs.setOnPreferenceChangeListener(this);

        mHeadsUpBgColor =
                (ColorPickerPreference) findPreference(PREF_HEADS_UP_BG_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.HEADS_UP_BG_COLOR,
                DEFAULT_BACKGROUND_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mHeadsUpBgColor.setSummary(hexColor);
        mHeadsUpBgColor.setNewPreviewColor(intColor);
        mHeadsUpBgColor.setAlphaSliderEnabled(true);
        mHeadsUpBgColor.setOnPreferenceChangeListener(this);

        mHeadsUpBgPressedColor =
                (ColorPickerPreference) findPreference(PREF_HEADS_UP_BG_PRESSED_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.HEADS_UP_BG_PRESSED_COLOR,
                DEFAULT_BACKGROUND_PRESSED_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mHeadsUpBgPressedColor.setSummary(hexColor);
        mHeadsUpBgPressedColor.setNewPreviewColor(intColor);
        mHeadsUpBgPressedColor.setAlphaSliderEnabled(true);
        mHeadsUpBgPressedColor.setOnPreferenceChangeListener(this);

        mHeadsUpColorizeNotifIcons =
                (CheckBoxPreference) findPreference(PREF_HEADS_UP_COLORIZE_NOTIF_ICONS);
        mHeadsUpColorizeNotifIcons.setChecked(Settings.System.getInt(mResolver,
                Settings.System.HEADS_UP_COLORIZE_NOTIF_ICONS, 0) == 1);
        mHeadsUpColorizeNotifIcons.setOnPreferenceChangeListener(this);

        mHeadsUpIconColor =
                (ColorPickerPreference) findPreference(PREF_HEADS_UP_ICON_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.HEADS_UP_ICON_COLOR,
                DEFAULT_WHITE_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mHeadsUpIconColor.setSummary(hexColor);
        mHeadsUpIconColor.setNewPreviewColor(intColor);
        mHeadsUpIconColor.setAlphaSliderEnabled(true);
        mHeadsUpIconColor.setOnPreferenceChangeListener(this);

        mHeadsUpTextColor =
                (ColorPickerPreference) findPreference(PREF_HEADS_UP_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.HEADS_UP_TEXT_COLOR,
                DEFAULT_WHITE_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mHeadsUpTextColor.setSummary(hexColor);
        mHeadsUpTextColor.setNewPreviewColor(intColor);
        mHeadsUpTextColor.setOnPreferenceChangeListener(this);

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
                showDialogInner(DLG_RESET, true);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int index;
        int intHex;
        String hex;

        if (preference == mHeadsUpExpanded) {
            Settings.System.putInt(mResolver,
                    Settings.System.HEADS_UP_EXPANDED,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mHeadsUpShowUpdates) {
            Settings.System.putInt(mResolver,
                    Settings.System.HEADS_UP_SHOW_UPDATE,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mHeadsUpGravity) {
            Settings.System.putInt(mResolver,
                    Settings.System.HEADS_UP_GRAVITY_BOTTOM,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mHeadsUpSnoozeTime) {
            int headsUpSnoozeTime = Integer.valueOf((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.HEADS_UP_SNOOZE_TIME,
                    headsUpSnoozeTime);
            updateHeadsUpSnoozeTimeSummary(headsUpSnoozeTime);
            return true;
        } else if (preference == mTimeout) {
            int timeout = Integer.valueOf((String) newValue);
            index = mTimeout.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.HEADS_UP_TIMEOUT, timeout);
            updateHeadsUpTimeoutSummary(timeout);
            return true;
        } else if (preference == mUseCustomTimeoutFs) {
            Settings.System.putInt(mResolver,
                    Settings.System.HEADS_UP_USE_CUSTOM_TIMEOUT_FS,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mHeadsUpBgColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.HEADS_UP_BG_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mHeadsUpBgPressedColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.HEADS_UP_BG_PRESSED_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mHeadsUpColorizeNotifIcons) {
            Settings.System.putInt(mResolver,
                    Settings.System.HEADS_UP_COLORIZE_NOTIF_ICONS,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mHeadsUpIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.HEADS_UP_ICON_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mHeadsUpTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.HEADS_UP_TEXT_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    private void updateHeadsUpSnoozeTimeSummary(int value) {
        String summary = value != 0
                ? getResources().getString(R.string.heads_up_snooze_summary, value / 60 / 1000)
                : getResources().getString(R.string.heads_up_snooze_disabled_summary);
        mHeadsUpSnoozeTime.setSummary(summary);
    }

    private void updateHeadsUpTimeoutSummary(int value) {
        String summary = getResources().getString(R.string.heads_up_timeout_summary,
                value / 1000);
        if (value == 0) {
            mTimeout.setSummary(
                    getResources().getString(R.string.heads_up_timeout_never_summary));
        } else {
            mTimeout.setSummary(summary);
        }
    }

    private void showDialogInner(int id, boolean state) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id, state);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id, boolean state) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            args.putBoolean("state", state);
            frag.setArguments(args);
            return frag;
        }

        HeadsUpSettings getOwner() {
            return (HeadsUpSettings) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            final boolean state = getArguments().getBoolean("state");
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
                                Settings.System.HEADS_UP_BG_COLOR,
                                DEFAULT_BACKGROUND_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.HEADS_UP_BG_PRESSED_COLOR,
                                DEFAULT_BACKGROUND_PRESSED_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.HEADS_UP_ICON_COLOR,
                                DEFAULT_WHITE_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.HEADS_UP_TEXT_COLOR,
                                DEFAULT_WHITE_COLOR);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.HEADS_UP_BG_COLOR,
                                DEFAULT_BACKGROUND_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.HEADS_UP_BG_PRESSED_COLOR,
                                0x4033b5e5);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.HEADS_UP_ICON_COLOR,
                                0xff33b5e5);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.HEADS_UP_TEXT_COLOR,
                                0xffff0000);
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
