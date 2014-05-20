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

public class StatusBarBatteryStatusStyle extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_CAT_OPTIONS_GENERAL =
            "status_bar_battery_status_cat_options_general";
    private static final String PREF_CAT_OPTIONS_COLOR =
            "status_bar_battery_status_cat_options_color";
    private static final String PREF_CAT_COLORS =
            "status_bar_battery_status_cat_colors";
    private static final String PREF_SHOW_BATT_STAT =
            "status_bar_show_battery_status";
    private static final String PREF_BATT_STAT_STYLE =
            "battery_status_style";
    private static final String PREF_BATT_STAT_SHOW_MINI_ICON =
            "battery_status_show_mini_icon";
    private static final String PREF_BATT_STAT_SHOW_TEXT =
            "battery_status_show_text";
    private static final String PREF_BATT_STAT_SHOW_PERCENTAGE_SIGN =
            "battery_status_show_percentage_sign";
    private static final String PREF_BATT_STAT_CIRCLE_DOTTED =
            "battery_circle_dotted";
    private static final String PREF_BATT_STAT_CIRCLE_DOT_LENGTH =
            "battery_circle_dot_length";
    private static final String PREF_BATT_STAT_CIRCLE_DOT_INTERVAL =
            "battery_circle_dot_interval";
    private static final String PREF_BATT_STAT_CIRCLE_DOT_OFFSET =
            "battery_circle_dot_offset";
    private static final String PREF_BATT_STAT_CIRCLE_ANIMATIONSPEED =
            "circle_battery_animation_speed";
    private static final String PREF_BATT_STAT_CUSTOM_FRAME_COLOR =
            "battery_status_custom_frame_color";
    private static final String PREF_BATT_STAT_CUSTOM_HIGHT_COLOR =
            "battery_status_custom_text_hight_color";
    private static final String PREF_BATT_STAT_COLOR =
            "battery_status_color";
    private static final String PREF_BATT_STAT_FRAME_COLOR =
            "battery_frame_color";
    private static final String PREF_BATT_STAT_TEXT_COLOR =
            "battery_text_color";
    private static final String PREF_BATT_STAT_TEXT_HIGHT_COLOR =
            "battery_text_hight_color";
    private static final String PREF_BATT_STAT_TEXT_CHARGING_COLOR =
            "battery_text_charging_color";

    private static final int DEFAULT_STATUS_COLOR = 0xffffffff;
    private static final int DEFAULT_FRAME_COLOR = 0x66ffffff;
    private static final int DEFAULT_TEXT_COLOR = 0xffffffff;
    private static final int DEFAULT_TEXT_HIGHT_COLOR = 0xffffffff;
    private static final int DEFAULT_TEXT_CHARGING_COLOR = 0xff00ff00;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private CheckBoxPreference mShowBatteryStatus;
    private ListPreference mBatteryStatusStyle;
    private CheckBoxPreference mShowMiniIcon;
    private CheckBoxPreference mShowText;
    private CheckBoxPreference mShowPercentageSign;
    private CheckBoxPreference mCustomFrameColor;
    private CheckBoxPreference mCustomHightColor;
    private CheckBoxPreference mCircleDotted;
    private ListPreference mCircleDotLength;
    private ListPreference mCircleDotInterval;
    private ListPreference mCircleDotOffset;
    private ListPreference mCircleAnimSpeed;
    private ColorPickerPreference mStatusColor;
    private ColorPickerPreference mFrameColor;
    private ColorPickerPreference mTextColor;
    private ColorPickerPreference mTextHightColor;
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

        addPreferencesFromResource(R.xml.status_bar_battery_status_style);
        mResolver = getActivity().getContentResolver();

        boolean isBatteryStatusEnabled = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_SHOW_BATTERY_STATUS, 1) == 1;
        int batteryStatus = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_STATUS_STYLE, 2);
        boolean showtext = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_TEXT, 1) == 1;
        boolean isCircleDottedEnabled = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_CIRCLE_DOTTED, 0) == 1;
        boolean customFrameColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_CUSTOM_FRAME_COLOR, 0) == 1;
        boolean customHightColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_CUSTOM_TEXT_HIGHT_COLOR, 1) == 1;
        int intColor = 0xffffffff;
        String hexColor = String.format("#%08x", (0xffffffff & 0xffffffff));

        mShowBatteryStatus =
                (CheckBoxPreference) findPreference(PREF_SHOW_BATT_STAT);
        mShowBatteryStatus.setChecked(isBatteryStatusEnabled);
        mShowBatteryStatus.setOnPreferenceChangeListener(this);

        // Handle the appearance of the preferences
        if (isBatteryStatusEnabled) {
            // Append needed settings dependig on battery status type
            if (batteryStatus == 0) {
                addPreferencesFromResource(R.xml.status_bar_battery_status_style_icon);
            } else if (batteryStatus == 1) {
                addPreferencesFromResource(R.xml.status_bar_battery_status_style_text);
            } else {
                addPreferencesFromResource(R.xml.status_bar_battery_status_style_circle);
            }

            mBatteryStatusStyle =
                    (ListPreference) findPreference(PREF_BATT_STAT_STYLE);
            mBatteryStatusStyle.setValue(String.valueOf(batteryStatus));
            mBatteryStatusStyle.setSummary(mBatteryStatusStyle.getEntry());
            mBatteryStatusStyle.setOnPreferenceChangeListener(this);

            PreferenceCategory catOptionColor =
                    (PreferenceCategory) findPreference(PREF_CAT_OPTIONS_COLOR);
            mCustomHightColor =
                    (CheckBoxPreference) findPreference(PREF_BATT_STAT_CUSTOM_HIGHT_COLOR);
            if (showtext || batteryStatus == 1) {
                mCustomHightColor.setChecked(customHightColor);
                mCustomHightColor.setOnPreferenceChangeListener(this);
            } else {
                // Remove the custom text hight color preference
                // if battery style is not text and text is disabled
                catOptionColor.removePreference(mCustomHightColor);
            }

            mTextColor =
                    (ColorPickerPreference) findPreference(PREF_BATT_STAT_TEXT_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR,
                    DEFAULT_TEXT_COLOR); 
            mTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mTextColor.setSummary(hexColor);
            mTextColor.setOnPreferenceChangeListener(this);

            PreferenceCategory catColor =
                    (PreferenceCategory) findPreference(PREF_CAT_COLORS);
            mTextHightColor =
                    (ColorPickerPreference) findPreference(PREF_BATT_STAT_TEXT_HIGHT_COLOR);
            if (customHightColor) {
                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_BATTERY_TEXT_HIGHT_COLOR,
                        DEFAULT_TEXT_HIGHT_COLOR); 
                mTextHightColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mTextHightColor.setSummary(hexColor);
                mTextHightColor.setOnPreferenceChangeListener(this);
            } else {
                // Remove the text hight color preference if custom higt color is disabled
                catColor.removePreference(mTextHightColor);
            }

            mTextChargingColor =
                    (ColorPickerPreference) findPreference(PREF_BATT_STAT_TEXT_CHARGING_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR, 
                        DEFAULT_TEXT_CHARGING_COLOR);
            mTextChargingColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mTextChargingColor.setSummary(hexColor);
            mTextChargingColor.setOnPreferenceChangeListener(this);

            if (batteryStatus == 0) {
                mShowMiniIcon =
                        (CheckBoxPreference) findPreference(PREF_BATT_STAT_SHOW_MINI_ICON);
                mShowMiniIcon.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_MINI_ICON, 0) == 1);
                mShowMiniIcon.setOnPreferenceChangeListener(this);
            }

            if (batteryStatus != 1) {
                mShowText =
                        (CheckBoxPreference) findPreference(PREF_BATT_STAT_SHOW_TEXT);
                mShowText.setChecked(showtext);
                mShowText.setOnPreferenceChangeListener(this);

                mCustomFrameColor =
                        (CheckBoxPreference) findPreference(PREF_BATT_STAT_CUSTOM_FRAME_COLOR);
                mCustomFrameColor.setChecked(customFrameColor);
                mCustomFrameColor.setOnPreferenceChangeListener(this);

                mStatusColor =
                    (ColorPickerPreference) findPreference(PREF_BATT_STAT_COLOR);
                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_BATTERY_STATUS_COLOR,
                        DEFAULT_STATUS_COLOR);
                mStatusColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mStatusColor.setSummary(hexColor);
                mStatusColor.setOnPreferenceChangeListener(this);

                mFrameColor =
                        (ColorPickerPreference) findPreference(PREF_BATT_STAT_FRAME_COLOR);
                if (customFrameColor) {
                    intColor = Settings.System.getInt(mResolver,
                            Settings.System.STATUS_BAR_BATTERY_FRAME_COLOR, 
                            DEFAULT_FRAME_COLOR);
                    mFrameColor.setNewPreviewColor(intColor);
                    hexColor = String.format("#%08x", (0xffffffff & intColor));
                    mFrameColor.setSummary(hexColor);
                    mFrameColor.setOnPreferenceChangeListener(this);
                } else {
                    // Remove the frame color preference if custom frame color is disabled
                    catColor.removePreference(mFrameColor);
                }

                if (!showtext) {
                    // Remove text color preferences if the battery text is hidden
                    catColor.removePreference(mTextColor);
                    if (mTextHightColor != null) {
                        catColor.removePreference(mTextHightColor);
                    }
                    catColor.removePreference(mTextChargingColor);
                }
            }

            if (batteryStatus == 1) {
                mShowPercentageSign =
                        (CheckBoxPreference) findPreference(PREF_BATT_STAT_SHOW_PERCENTAGE_SIGN);
                mShowPercentageSign.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_SHOW_PERCENTAGE_SIGN, 1) == 1);
                mShowPercentageSign.setOnPreferenceChangeListener(this);
            }

            if (batteryStatus == 2) {
                mCircleDotted =
                        (CheckBoxPreference) findPreference(PREF_BATT_STAT_CIRCLE_DOTTED);
                mCircleDotted.setChecked(isCircleDottedEnabled);
                mCircleDotted.setOnPreferenceChangeListener(this);

                PreferenceCategory catOptionsGeneral =
                        (PreferenceCategory) findPreference(PREF_CAT_OPTIONS_GENERAL);
                mCircleDotLength =
                        (ListPreference) findPreference(PREF_BATT_STAT_CIRCLE_DOT_LENGTH);
                mCircleDotInterval =
                        (ListPreference) findPreference(PREF_BATT_STAT_CIRCLE_DOT_INTERVAL);
                mCircleDotOffset =
                        (ListPreference) findPreference(PREF_BATT_STAT_CIRCLE_DOT_OFFSET);
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

                    int circleDotOffset = Settings.System.getInt(mResolver,
                            Settings.System.STATUS_BAR_CIRCLE_DOT_OFFSET, 0);
                    mCircleDotOffset.setValue(String.valueOf(circleDotOffset));
                    mCircleDotOffset.setSummary(mCircleDotOffset.getEntry());
                    mCircleDotOffset.setOnPreferenceChangeListener(this);
                } else {
                    // Remove dot related preferences if the battery ring isn`t drawn dotted
                    catOptionsGeneral.removePreference(mCircleDotLength);
                    catOptionsGeneral.removePreference(mCircleDotInterval);
                    catOptionsGeneral.removePreference(mCircleDotOffset);
                }

                mCircleAnimSpeed =
                        (ListPreference) findPreference(PREF_BATT_STAT_CIRCLE_ANIMATIONSPEED);
                int circleAnimSpeed = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED, 3);
                mCircleAnimSpeed.setValue(String.valueOf(circleAnimSpeed));
                mCircleAnimSpeed.setSummary(mCircleAnimSpeed.getEntry());
                mCircleAnimSpeed.setOnPreferenceChangeListener(this);
            }
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

        if (preference == mShowBatteryStatus) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_SHOW_BATTERY_STATUS, value ?
                    1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mBatteryStatusStyle) {
            intValue = Integer.valueOf((String) newValue);
            index = mBatteryStatusStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_STATUS_STYLE,
                    intValue);
            mBatteryStatusStyle.setSummary(
                    mBatteryStatusStyle.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mShowMiniIcon) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_MINI_ICON,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowText) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_TEXT,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mShowPercentageSign) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_SHOW_PERCENTAGE_SIGN,
                    value ? 1 : 0);
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
        } else if (preference == mCircleDotOffset) {
            intValue = Integer.valueOf((String) newValue);
            index = mCircleDotOffset.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_CIRCLE_DOT_OFFSET,
                    intValue);
            mCircleDotOffset.setSummary(mCircleDotOffset.getEntries()[index]);
            return true;
        } else if (preference == mCircleAnimSpeed) {
            intValue = Integer.valueOf((String) newValue);
            index = mCircleAnimSpeed.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED,
                    intValue);
            mCircleAnimSpeed.setSummary(mCircleAnimSpeed.getEntries()[index]);
            return true;
        } else if (preference == mCustomFrameColor) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_CUSTOM_FRAME_COLOR,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mCustomHightColor) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_CUSTOM_TEXT_HIGHT_COLOR,
                    value ? 1 : 0);
            refreshSettings();
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
        } else if (preference == mFrameColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_FRAME_COLOR,
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
        } else if (preference == mTextHightColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_TEXT_HIGHT_COLOR,
                    intHex);
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

        StatusBarBatteryStatusStyle getOwner() {
            return (StatusBarBatteryStatusStyle) getTargetFragment();
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
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_STYLE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_MINI_ICON, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_TEXT, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_SHOW_PERCENTAGE_SIGN, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CIRCLE_DOTTED, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CIRCLE_DOT_LENGTH, 3);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CIRCLE_DOT_INTERVAL, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CIRCLE_DOT_OFFSET, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_CUSTOM_FRAME_COLOR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_CUSTOM_TEXT_HIGHT_COLOR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_COLOR,
                                    DEFAULT_STATUS_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_FRAME_COLOR,
                                    DEFAULT_FRAME_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR,
                                    DEFAULT_TEXT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_HIGHT_COLOR,
                                    DEFAULT_TEXT_HIGHT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR,
                                    DEFAULT_TEXT_CHARGING_COLOR);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_STYLE, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_MINI_ICON, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_SHOW_TEXT, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_SHOW_PERCENTAGE_SIGN, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CIRCLE_DOTTED, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CIRCLE_DOT_LENGTH, 3);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CIRCLE_DOT_INTERVAL, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CIRCLE_DOT_OFFSET, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_CIRCLE_BATTERY_ANIMATIONSPEED, 5);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_CUSTOM_FRAME_COLOR, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_CUSTOM_TEXT_HIGHT_COLOR, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_STATUS_COLOR,
                                    0xff33b5e5);;
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_FRAME_COLOR,
                                    0x6633b5e5);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_COLOR,
                                    DEFAULT_TEXT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_HIGHT_COLOR,
                                    DEFAULT_TEXT_HIGHT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.STATUS_BAR_BATTERY_TEXT_CHARGING_COLOR,
                                    DEFAULT_TEXT_CHARGING_COLOR);
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
