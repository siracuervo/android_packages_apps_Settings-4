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

package com.android.settings.darkkat.statusbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarBatteryStatus extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_CAT_OPTIONS_GENERAL =
            "battery_status_cat_options_general";
    private static final String PREF_CAT_OPTIONS_RING =
            "battery_status_cat_options_ring";
    private static final String PREF_CAT_COLORS =
            "battery_status_cat_colors";
    private static final String PREF_BATT_STAT_TYPE =
            "battery_status_type";
    private static final String PREF_BATT_STAT_SHOW_TEXT =
            "battery_status_show_text";
    private static final String PREF_BATT_STAT_ANIMATION_SPEED =
            "battery_status_animation_speed";
    private static final String PREF_BATT_STAT_CIRCLE_DOTTED =
            "battery_circle_dotted";
    private static final String PREF_BATT_STAT_CIRCLE_DOT_LENGTH =
            "battery_circle_dot_length";
    private static final String PREF_BATT_STAT_CIRCLE_DOT_INTERVAL =
            "battery_circle_dot_interval";
    private static final String PREF_BATT_STAT_COLOR =
            "battery_status_color";
    private static final String PREF_BATT_STAT_TEXT_COLOR =
            "battery_text_color";
    private static final String PREF_BATT_STAT_TEXT_CHARGING_COLOR =
            "battery_text_charging_color";

    private static final int BATTERY_STATUS_ICON_PORTRAIT  = 0;
    private static final int BATTERY_STATUS_ICON_LANDSCAPE = 1;
    private static final int BATTERY_STATUS_RING           = 2;
    private static final int BATTERY_STATUS_TEXT           = 3;
    private static final int BATTERY_STATUS_HIDDEN         = 4;

    private static final int DEFAULT_COLOR        = 0xffffffff;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private ListPreference mBatteryStatusType;
    private CheckBoxPreference mShowText;
    private ListPreference mAnimSpeed;
    private CheckBoxPreference mCircleDotted;
    private ListPreference mCircleDotLength;
    private ListPreference mCircleDotInterval;
    private ColorPickerPreference mStatusColor;
    private ColorPickerPreference mTextColor;
    private ColorPickerPreference mTextChargingColor;

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

        addPreferencesFromResource(R.xml.status_bar_battery_status);
        mResolver = getActivity().getContentResolver();

        PreferenceCategory catOptionsGeneral =
                (PreferenceCategory) findPreference(PREF_CAT_OPTIONS_GENERAL);
        PreferenceCategory catOptionsRing =
                (PreferenceCategory) findPreference(PREF_CAT_OPTIONS_RING);
        PreferenceCategory catColor =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);

        mBatteryStatusType =
                (ListPreference) findPreference(PREF_BATT_STAT_TYPE);
        mShowText =
                (CheckBoxPreference) findPreference(PREF_BATT_STAT_SHOW_TEXT);
        mAnimSpeed =
                (ListPreference) findPreference(PREF_BATT_STAT_ANIMATION_SPEED);
        mCircleDotted =
                (CheckBoxPreference) findPreference(PREF_BATT_STAT_CIRCLE_DOTTED);
        mCircleDotLength =
                (ListPreference) findPreference(PREF_BATT_STAT_CIRCLE_DOT_LENGTH);
        mCircleDotInterval =
                (ListPreference) findPreference(PREF_BATT_STAT_CIRCLE_DOT_INTERVAL);
        mStatusColor =
                (ColorPickerPreference) findPreference(PREF_BATT_STAT_COLOR);
        mTextColor =
                (ColorPickerPreference) findPreference(PREF_BATT_STAT_TEXT_COLOR);
        mTextChargingColor =
                (ColorPickerPreference) findPreference(PREF_BATT_STAT_TEXT_CHARGING_COLOR);

        int batteryStatus = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_STATUS_STYLE,
                BATTERY_STATUS_RING);
        boolean isBatteryStatusEnabled = batteryStatus != BATTERY_STATUS_HIDDEN;
        boolean showtext = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_TEXT, 1) == 1;
        boolean isCircleDottedEnabled = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_CIRCLE_DOTTED, 0) == 1;
        int intColor;
        String hexColor;

        mBatteryStatusType.setValue(String.valueOf(batteryStatus));
        mBatteryStatusType.setSummary(mBatteryStatusType.getEntry());
        mBatteryStatusType.setOnPreferenceChangeListener(this);

        if (isBatteryStatusEnabled) {
            if (batteryStatus != BATTERY_STATUS_TEXT) {
                mShowText.setChecked(showtext);
                mShowText.setOnPreferenceChangeListener(this);
            } else {
                catOptionsGeneral.removePreference(mShowText);
            }

            int animSpeed = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_ANIMATION_SPEED, 3);
            mAnimSpeed.setValue(String.valueOf(animSpeed));
            mAnimSpeed.setSummary(mAnimSpeed.getEntry());
            mAnimSpeed.setOnPreferenceChangeListener(this);

            if (batteryStatus == BATTERY_STATUS_RING) {
                mCircleDotted.setChecked(isCircleDottedEnabled);
                mCircleDotted.setOnPreferenceChangeListener(this);

                if (isCircleDottedEnabled) {
                    int circleDotLength = Settings.System.getInt(mResolver,
                            Settings.System.STATUS_BAR_CIRCLE_DOT_LENGTH, 3);
                    mCircleDotLength.setValue(String.valueOf(circleDotLength));
                    mCircleDotLength.setSummary(mCircleDotLength.getEntry());
                    mCircleDotLength.setOnPreferenceChangeListener(this);

                    int circleDotInterval = Settings.System.getInt(mResolver,
                            Settings.System.STATUS_BAR_CIRCLE_DOT_INTERVAL, 2);
                    mCircleDotInterval.setValue(String.valueOf(circleDotInterval));
                    mCircleDotInterval.setSummary(mCircleDotInterval.getEntry());
                    mCircleDotInterval.setOnPreferenceChangeListener(this);
                } else {
                    catOptionsRing.removePreference(mCircleDotLength);
                    catOptionsRing.removePreference(mCircleDotInterval);
                }
            } else {
                catOptionsRing.removePreference(mCircleDotted);
                if (isCircleDottedEnabled) {
                    catOptionsRing.removePreference(mCircleDotLength);
                    catOptionsRing.removePreference(mCircleDotInterval);
                }
                removePreference(PREF_CAT_OPTIONS_RING);
            }
            if (batteryStatus != BATTERY_STATUS_TEXT) {
                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_BATTERY_STATUS_COLOR,
                        DEFAULT_COLOR);
                mStatusColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mStatusColor.setSummary(hexColor);
                mStatusColor.setOnPreferenceChangeListener(this);
            } else {
                catColor.removePreference(mStatusColor);
            }
            if (!showtext && batteryStatus != BATTERY_STATUS_TEXT) {
                catColor.removePreference(mTextColor);
                catColor.removePreference(mTextChargingColor);
            } else {
                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR,
                        DEFAULT_COLOR); 
                mTextColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mTextColor.setSummary(hexColor);
                mTextColor.setOnPreferenceChangeListener(this);

                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR, 
                            DEFAULT_COLOR);
                mTextChargingColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mTextChargingColor.setSummary(hexColor);
                mTextChargingColor.setOnPreferenceChangeListener(this);
            }
        } else {
            catOptionsGeneral.removePreference(mShowText);
            catOptionsGeneral.removePreference(mAnimSpeed);
            catOptionsRing.removePreference(mCircleDotted);
            catOptionsRing.removePreference(mCircleDotLength);
            catOptionsRing.removePreference(mCircleDotInterval);
            catColor.removePreference(mStatusColor);
            catColor.removePreference(mTextColor);
            catColor.removePreference(mTextChargingColor);
            removePreference(PREF_CAT_OPTIONS_RING);
            removePreference(PREF_CAT_COLORS);
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

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;
        int intValue;
        int index;
        int intHex;
        String hex;

        if (preference == mBatteryStatusType) {
            intValue = Integer.valueOf((String) newValue);
            index = mBatteryStatusType.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_STATUS_STYLE,
                    intValue);
            mBatteryStatusType.setSummary(
                    mBatteryStatusType.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mShowText) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_TEXT,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mAnimSpeed) {
            intValue = Integer.valueOf((String) newValue);
            index = mAnimSpeed.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_ANIMATION_SPEED,
                    intValue);
            mAnimSpeed.setSummary(mAnimSpeed.getEntries()[index]);
            return true;
        } else if (preference == mCircleDotted) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_CIRCLE_DOTTED,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mCircleDotLength) {
            intValue = Integer.valueOf((String) newValue);
            index = mCircleDotLength.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_CIRCLE_DOT_LENGTH,
                    intValue);
            mCircleDotLength.setSummary(
                    mCircleDotLength.getEntries()[index]);
            return true;
        } else if (preference == mCircleDotInterval) {
            intValue = Integer.valueOf((String) newValue);
            index = mCircleDotInterval.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_CIRCLE_DOT_INTERVAL,
                    intValue);
            mCircleDotInterval.setSummary(
                    mCircleDotInterval.getEntries()[index]);
            return true;
        } else if (preference == mStatusColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_STATUS_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTextChargingColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
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

        StatusBarBatteryStatus getOwner() {
            return (StatusBarBatteryStatus) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_STYLE,
                                    BATTERY_STATUS_RING);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_TEXT, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_ANIMATION_SPEED, 3);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CIRCLE_DOTTED, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CIRCLE_DOT_LENGTH, 3);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CIRCLE_DOT_INTERVAL, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_COLOR,
                                    DEFAULT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR,
                                    DEFAULT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR,
                                    DEFAULT_COLOR);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_STYLE,
                                    BATTERY_STATUS_RING);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_TEXT, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_ANIMATION_SPEED, 5);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CIRCLE_DOTTED, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CIRCLE_DOT_LENGTH, 3);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CIRCLE_DOT_INTERVAL, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_COLOR,
                                    0xff33b5e5);;
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR,
                                    DEFAULT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR,
                                    0xff00ff00);
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
