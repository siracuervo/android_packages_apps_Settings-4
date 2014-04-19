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
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarBatteryBarStyle extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_SHOW_BATT_BAR =
            "status_bar_show_battery_bar";
    private static final String PREF_BATT_BAR_POSITION =
            "battery_bar_position";
    private static final String PREF_BATT_BAR_CENTER =
            "battery_bar_center";
    private static final String PREF_BATT_ANIMATE =
            "battery_bar_animate";
    private static final String PREF_BATT_BAR_WIDTH =
            "battery_bar_thickness";
    private static final String PREF_BATT_BAR_COLOR =
            "battery_bar_color";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private CheckBoxPreference mShowBatteryBar;
    private ListPreference mBatteryBarPosition;
    private CheckBoxPreference mBatteryBarCenter;
    private CheckBoxPreference mBatteryBarChargingAnimation;
    private ListPreference mBatteryBarThickness;
    private ColorPickerPreference mBatteryBarColor;

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

        addPreferencesFromResource(R.xml.status_bar_battery_bar_style);
        mResolver = getActivity().getContentResolver();

        boolean isBatteryBarEnabled = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_SHOW_BATTERY_BAR, 0) == 1;

        mShowBatteryBar =
                (CheckBoxPreference) findPreference(PREF_SHOW_BATT_BAR);
        mShowBatteryBar.setChecked(isBatteryBarEnabled);
        mShowBatteryBar.setOnPreferenceChangeListener(this);

        if (isBatteryBarEnabled) {
            mBatteryBarPosition =
                    (ListPreference) findPreference(PREF_BATT_BAR_POSITION);
            int batteryBarPosition = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_BAR_POSITION, 1);
            mBatteryBarPosition.setValue(String.valueOf(batteryBarPosition));
            mBatteryBarPosition.setSummary(mBatteryBarPosition.getEntry());
            mBatteryBarPosition.setOnPreferenceChangeListener(this);

            mBatteryBarCenter =
                    (CheckBoxPreference) findPreference(PREF_BATT_BAR_CENTER);
            mBatteryBarCenter.setChecked((Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_BAR_STYLE, 0) == 1));
            mBatteryBarCenter.setOnPreferenceChangeListener(this);

            mBatteryBarChargingAnimation =
                    (CheckBoxPreference) findPreference(PREF_BATT_ANIMATE);
            mBatteryBarChargingAnimation.setChecked((Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_BAR_ANIMATE, 0) == 1));
            mBatteryBarChargingAnimation.setOnPreferenceChangeListener(this);

            mBatteryBarThickness =
                    (ListPreference) findPreference(PREF_BATT_BAR_WIDTH);
            int batteryBarThickness = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_BAR_THICKNESS, 0);
            mBatteryBarThickness.setValue(String.valueOf(batteryBarThickness));
            mBatteryBarThickness.setSummary(mBatteryBarThickness.getEntry());
            mBatteryBarThickness.setOnPreferenceChangeListener(this);

            mBatteryBarColor =
                    (ColorPickerPreference) findPreference(PREF_BATT_BAR_COLOR);
            int color = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_BATTERY_BAR_COLOR, 0xffffffff);
            mBatteryBarColor.setNewPreviewColor(color);
            String hexColor = String.format("#%08x", (0xffffffff & color));
            mBatteryBarColor.setSummary(hexColor);
            mBatteryBarColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_BATT_BAR_POSITION);
            removePreference(PREF_BATT_BAR_CENTER);
            removePreference(PREF_BATT_ANIMATE);
            removePreference(PREF_BATT_BAR_WIDTH);
            removePreference(PREF_BATT_BAR_COLOR);
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

        if (preference == mShowBatteryBar) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_SHOW_BATTERY_BAR, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mBatteryBarPosition) {
            intValue = Integer.valueOf((String) newValue);
            index =
                    mBatteryBarPosition.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_BAR_POSITION,
                intValue);
            mBatteryBarPosition.setSummary(
                mBatteryBarPosition.getEntries()[index]);
            return true;
        } else if (preference == mBatteryBarCenter) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_BAR_STYLE,
                value ? 1 : 0);
            return true;
        } else if (preference == mBatteryBarChargingAnimation) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_BAR_ANIMATE,
                value ? 1 : 0);
            return true;
        } else if (preference == mBatteryBarThickness) {
            intValue = Integer.valueOf((String) newValue);
            index =
                mBatteryBarThickness.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_BAR_THICKNESS,
                intValue);
            mBatteryBarThickness.setSummary(
                mBatteryBarThickness.getEntries()[index]);
            return true;
        } else if (preference == mBatteryBarColor) {
            String hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_BATTERY_BAR_COLOR, intHex);
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

        StatusBarBatteryBarStyle getOwner() {
            return (StatusBarBatteryBarStyle) getTargetFragment();
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
                                Settings.System.STATUS_BAR_BATTERY_BAR_POSITION, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_BATTERY_BAR_STYLE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_BATTERY_BAR_ANIMATE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_BATTERY_BAR_THICKNESS, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_BATTERY_BAR_COLOR,
                                0xffffffff);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_BATTERY_BAR_POSITION, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_BATTERY_BAR_STYLE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_BATTERY_BAR_ANIMATE, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_BATTERY_BAR_THICKNESS, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_BATTERY_BAR_COLOR,
                                0xff33b5e5);
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
