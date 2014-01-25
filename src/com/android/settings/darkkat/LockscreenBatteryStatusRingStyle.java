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

public class LockscreenBatteryStatusRingStyle extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_BATT_STAT_RING_DOTTED =
            "lockscreen_battery_status_ring_dotted";
    private static final String PREF_BATT_STAT_RING_DOT_LENGTH =
            "lockscreen_battery_status_ring_dot_length";
    private static final String PREF_BATT_STAT_RING_DOT_INTERVAL =
            "lockscreen_battery_status_ring_dot_interval";
    private static final String PREF_BATT_STAT_RING_DOT_OFFSET =
            "lockscreen_battery_status_ring_dot_offset";
    private static final String PREF_BATT_STAT_RING_COLOR =
            "lockscreen_battery_status_ring_color";
    private static final String PREF_BATT_STAT_RING_CHARGING_COLOR =
            "lockscreen_battery_status_ring_charging_color";

    private static final int DEFAULT_BATT_STAT_RING_DOTTED = 0;
    private static final int DEFAULT_BATT_STAT_RING_DOT_LENGTH = 5;
    private static final int DEFAULT_BATT_STAT_RING_DOT_INTERVAL = 4;
    private static final int DEFAULT_BATT_STAT_RING_DOT_OFFSET = 0;
    private static final int DEFAULT_BATT_STAT_RING_COLOR = 0xff0099cc;
    private static final int DEFAULT_BATT_STAT_RING_CHARGING_COLOR = 0xff00ff00;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private CheckBoxPreference mRingDotted;
    private ListPreference mRingDotLength;
    private ListPreference mRingDotInterval;
    private ListPreference mRingDotOffset;
    private ColorPickerPreference mRingColor;
    private ColorPickerPreference mRingChargingColor;

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

        addPreferencesFromResource(R.xml.lockscreen_battery_status_ring_style);
        mResolver = getActivity().getContentResolver();

        boolean isRingDottedEnabled = Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOTTED,
                    DEFAULT_BATT_STAT_RING_DOTTED) == 1;

        mRingDotted = (CheckBoxPreference) findPreference(PREF_BATT_STAT_RING_DOTTED);
        mRingDotted.setChecked(isRingDottedEnabled);
        mRingDotted.setOnPreferenceChangeListener(this);

        mRingDotLength = (ListPreference) findPreference(PREF_BATT_STAT_RING_DOT_LENGTH);
        mRingDotInterval = (ListPreference) findPreference(PREF_BATT_STAT_RING_DOT_INTERVAL);
        mRingDotOffset = (ListPreference) findPreference(PREF_BATT_STAT_RING_DOT_OFFSET);
        if (isRingDottedEnabled) {
            int ringDotLength = Settings.System.getInt(mResolver,
                        Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_LENGTH,
                        DEFAULT_BATT_STAT_RING_DOT_LENGTH);
            mRingDotLength.setValue(String.valueOf(ringDotLength));
            mRingDotLength.setSummary(mRingDotLength.getEntry());
            mRingDotLength.setOnPreferenceChangeListener(this);

            int ringDotInterval = Settings.System.getInt(mResolver,
                        Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_INTERVAL,
                        DEFAULT_BATT_STAT_RING_DOT_INTERVAL);
            mRingDotInterval.setValue(String.valueOf(ringDotInterval));
            mRingDotInterval.setSummary(mRingDotInterval.getEntry());
            mRingDotInterval.setOnPreferenceChangeListener(this);

            int ringDotOffset = Settings.System.getInt(mResolver,
                        Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_OFFSET,
                        DEFAULT_BATT_STAT_RING_DOT_OFFSET);
            mRingDotOffset.setValue(String.valueOf(ringDotOffset));
            mRingDotOffset.setSummary(mRingDotOffset.getEntry());
            mRingDotOffset.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_BATT_STAT_RING_DOT_LENGTH);
            removePreference(PREF_BATT_STAT_RING_DOT_INTERVAL);
            removePreference(PREF_BATT_STAT_RING_DOT_OFFSET);
        }

        int intColor = 0xff0099cc;
        String hexColor = String.format("#%08x", (0xffffffff & 0xff0099cc));

        mRingColor = (ColorPickerPreference) findPreference(PREF_BATT_STAT_RING_COLOR);
        intColor = Settings.System.getInt(mResolver,
                        Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_COLOR,
                        DEFAULT_BATT_STAT_RING_COLOR);
        mRingColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mRingColor.setSummary(hexColor);
        mRingColor.setOnPreferenceChangeListener(this);

        mRingChargingColor =
                (ColorPickerPreference) findPreference(PREF_BATT_STAT_RING_CHARGING_COLOR);
        intColor = Settings.System.getInt(mResolver,
                        Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_CHARGING_COLOR,
                        DEFAULT_BATT_STAT_RING_CHARGING_COLOR);
        mRingChargingColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mRingChargingColor.setSummary(hexColor);
        mRingChargingColor.setOnPreferenceChangeListener(this);

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
        int index;
        int intHex;
        String hex;

        if (preference == mRingDotted) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOTTED, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mRingDotLength) {
            int ringDotLength = Integer.valueOf((String) newValue);
            index = mRingDotLength.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_LENGTH, ringDotLength);
            mRingDotLength.setSummary(mRingDotLength.getEntries()[index]);
            return true;
        } else if (preference == mRingDotInterval) {
            int ringDotInterval = Integer.valueOf((String) newValue);
            index = mRingDotInterval.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_INTERVAL, ringDotInterval);
            mRingDotInterval.setSummary(mRingDotInterval.getEntries()[index]);
            return true;
        } else if (preference == mRingDotOffset) {
            int ringDotOffset = Integer.valueOf((String) newValue);
            index = mRingDotOffset.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_OFFSET, ringDotOffset);
            mRingDotOffset.setSummary(mRingDotOffset.getEntries()[index]);
            return true;
        } else if (preference == mRingColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mRingChargingColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_CHARGING_COLOR, intHex);
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

        LockscreenBatteryStatusRingStyle getOwner() {
            return (LockscreenBatteryStatusRingStyle) getTargetFragment();
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
                                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOTTED,
                                DEFAULT_BATT_STAT_RING_DOTTED);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_LENGTH,
                                DEFAULT_BATT_STAT_RING_DOT_LENGTH);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_INTERVAL,
                                DEFAULT_BATT_STAT_RING_DOT_INTERVAL);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_OFFSET,
                                DEFAULT_BATT_STAT_RING_DOT_OFFSET);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_COLOR,
                                DEFAULT_BATT_STAT_RING_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_CHARGING_COLOR,
                                DEFAULT_BATT_STAT_RING_CHARGING_COLOR);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOTTED, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_LENGTH,
                                DEFAULT_BATT_STAT_RING_DOT_LENGTH);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_INTERVAL,
                                DEFAULT_BATT_STAT_RING_DOT_INTERVAL);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_DOT_OFFSET,
                                DEFAULT_BATT_STAT_RING_DOT_OFFSET);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_COLOR, 0xff33b5e5);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_BATTERY_STATUS_RING_CHARGING_COLOR,
                                DEFAULT_BATT_STAT_RING_CHARGING_COLOR);
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
