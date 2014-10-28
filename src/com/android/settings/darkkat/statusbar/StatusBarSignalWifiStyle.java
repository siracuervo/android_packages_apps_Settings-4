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
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.telephony.MSimTelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarSignalWifiStyle extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener { 

    private static final String PREF_STAT_BAR_SIGNAL =
            "status_bar_signal";
    private static final String PREF_STAT_BAR_NETWORK_ICONS_NORMAL_COLOR =
            "status_bar_network_icons_normal_color";
    private static final String PREF_STAT_BAR_NETWORK_ICONS_FULLY_COLOR =
            "status_bar_network_icons_fully_color";
    private static final String PREF_STAT_BAR_AIRPLANE_MODE_ICON_COLOR =
            "status_bar_airplane_mode_icon_color";

    private static final int DEFAULT_COLOR = 0xffffffff;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private ListPreference mStatusBarCmSignal;
    private ColorPickerPreference mNormalColor;
    private ColorPickerPreference mFullyColor;
    private ColorPickerPreference mAirplaneModeIconColor;

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

        addPreferencesFromResource(R.xml.status_bar_signal_wifi_style);
        mResolver = getActivity().getContentResolver();

        int intColor;
        String hexColor;

        mStatusBarCmSignal =
                (ListPreference) findPreference(PREF_STAT_BAR_SIGNAL);
        if (Utils.isWifiOnly(getActivity())
                || (MSimTelephonyManager.getDefault().isMultiSimEnabled())) {
            removePreference(PREF_STAT_BAR_SIGNAL);
        } else {
            int signalStyle = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_SIGNAL_TEXT, 0);
            mStatusBarCmSignal.setValue(String.valueOf(signalStyle));
            mStatusBarCmSignal.setSummary(mStatusBarCmSignal.getEntry());
            mStatusBarCmSignal.setOnPreferenceChangeListener(this);
        }

        mNormalColor =
                (ColorPickerPreference) findPreference(
                        PREF_STAT_BAR_NETWORK_ICONS_NORMAL_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_ICONS_NORMAL_COLOR,
                DEFAULT_COLOR); 
        mNormalColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mNormalColor.setSummary(hexColor);
        mNormalColor.setOnPreferenceChangeListener(this);

        mFullyColor =
                (ColorPickerPreference) findPreference(
                        PREF_STAT_BAR_NETWORK_ICONS_FULLY_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_ICONS_FULLY_COLOR,
                DEFAULT_COLOR); 
        mFullyColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mFullyColor.setSummary(hexColor);
        mFullyColor.setOnPreferenceChangeListener(this);

        mAirplaneModeIconColor =
                (ColorPickerPreference) findPreference(
                        PREF_STAT_BAR_AIRPLANE_MODE_ICON_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_AIRPLANE_MODE_ICON_COLOR,
                DEFAULT_COLOR); 
        mAirplaneModeIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mAirplaneModeIconColor.setSummary(hexColor);
        mAirplaneModeIconColor.setOnPreferenceChangeListener(this);

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
        String hex;
        int intHex;

        if (preference == mStatusBarCmSignal) {
            int signalStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarCmSignal.findIndexOfValue((String)
                    newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_SIGNAL_TEXT, signalStyle);
            mStatusBarCmSignal.setSummary(mStatusBarCmSignal.getEntries()[index]);
            return true;
        } else if (preference == mNormalColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_ICONS_NORMAL_COLOR,
                intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mFullyColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_ICONS_FULLY_COLOR,
                intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mAirplaneModeIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_AIRPLANE_MODE_ICON_COLOR,
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

        StatusBarSignalWifiStyle getOwner() {
            return (StatusBarSignalWifiStyle) getTargetFragment();
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
                                Settings.System.STATUS_BAR_SIGNAL_TEXT, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_NETWORK_ICONS_NORMAL_COLOR,
                                DEFAULT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_NETWORK_ICONS_FULLY_COLOR,
                                DEFAULT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_AIRPLANE_MODE_ICON_COLOR,
                                DEFAULT_COLOR);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_SIGNAL_TEXT, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_NETWORK_ICONS_NORMAL_COLOR,
                                DEFAULT_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_NETWORK_ICONS_FULLY_COLOR,
                                0xff33b5e5);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_AIRPLANE_MODE_ICON_COLOR,
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
