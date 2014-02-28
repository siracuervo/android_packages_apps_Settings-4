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

public class StatusBarNetworkSpeedStyle extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_NETWORK_SPEED_SHOW_INDICATOR =
            "network_speed_show_indicator";
    private static final String PREF_TRAFFIC_SUMMARY =
            "traffic_summary";
    private static final String PREF_NETWORK_SPEED_BIT_BYTE =
            "network_speed_bit_byte";
    private static final String PREF_NETWORK_SPEED_HIDE_TRAFFIC =
            "network_speed_hide_traffic";
    private static final String PREF_NETWORK_SPEED_TEXT_COLOR =
            "network_speed_text_color";
    private static final String PREF_NETWORK_SPEED_ICON_COLOR =
            "network_speed_icon_color";

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private ListPreference mNetworkSpeedIndicator;
    private ListPreference mTrafficSummary; 
    private CheckBoxPreference mNetworkSpeedBitByte;
    private CheckBoxPreference mNetworkSpeedHide;
    private ColorPickerPreference mNetworkSpeedTextColor;
    private ColorPickerPreference mNetworkSpeedIconColor;

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

        int intColor = 0xffffffff;
        String hexColor = String.format("#%08x", (0xffffffff & 0xffffffff));

        addPreferencesFromResource(R.xml.status_bar_network_speed);
        mResolver = getActivity().getContentResolver();

        int networkSpeedIndicator = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_NETWORK_SPEED_INDICATOR, 2);

        mNetworkSpeedIndicator =
                (ListPreference) findPreference(PREF_NETWORK_SPEED_SHOW_INDICATOR);
        mNetworkSpeedIndicator.setValue(String.valueOf(networkSpeedIndicator));
        mNetworkSpeedIndicator.setSummary(mNetworkSpeedIndicator.getEntry());
        mNetworkSpeedIndicator.setOnPreferenceChangeListener(this);

        mTrafficSummary =
                (ListPreference) findPreference(PREF_TRAFFIC_SUMMARY);
        int trafficSummary = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_TRAFFIC_SUMMARY, 3000);
        mTrafficSummary.setValue(String.valueOf(trafficSummary));
        mTrafficSummary.setSummary(mTrafficSummary.getEntry());
        mTrafficSummary.setOnPreferenceChangeListener(this);

        mNetworkSpeedBitByte =
                (CheckBoxPreference) findPreference(PREF_NETWORK_SPEED_BIT_BYTE);
        mNetworkSpeedBitByte.setChecked((Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_SPEED_BIT_BYTE, 0) == 1));
        mNetworkSpeedBitByte.setOnPreferenceChangeListener(this);

        mNetworkSpeedHide =
                (CheckBoxPreference) findPreference(PREF_NETWORK_SPEED_HIDE_TRAFFIC);
        mNetworkSpeedHide.setChecked((Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_SPEED_HIDE_TRAFFIC, 1) == 1));
        mNetworkSpeedHide.setOnPreferenceChangeListener(this);

        // Remove uneeded preferences depending on enabled states
        mNetworkSpeedTextColor =
                (ColorPickerPreference) findPreference(PREF_NETWORK_SPEED_TEXT_COLOR);
        if (networkSpeedIndicator != 1) {
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_SPEED_TEXT_COLOR, 0xffffffff); 
            mNetworkSpeedTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mNetworkSpeedTextColor.setSummary(hexColor);
            mNetworkSpeedTextColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_NETWORK_SPEED_TEXT_COLOR);
        }

        // Remove uneeded preferences depending on enabled states
        mNetworkSpeedIconColor =
                (ColorPickerPreference) findPreference(PREF_NETWORK_SPEED_ICON_COLOR);
        if (networkSpeedIndicator != 0) {
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_NETWORK_SPEED_ICON_COLOR, 0xffffffff); 
            mNetworkSpeedIconColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mNetworkSpeedIconColor.setSummary(hexColor);
            mNetworkSpeedIconColor.setOnPreferenceChangeListener(this);
        } else {
            removePreference(PREF_NETWORK_SPEED_ICON_COLOR);
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
        if (preference == mNetworkSpeedIndicator) {
            int state = Integer.valueOf((String) newValue);
            int index = mNetworkSpeedIndicator.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_SPEED_INDICATOR, state);
            mNetworkSpeedIndicator.setSummary(mNetworkSpeedIndicator.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mTrafficSummary) {
            int trafficSummary = Integer.valueOf((String) newValue);
            int index = mTrafficSummary.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_TRAFFIC_SUMMARY, trafficSummary);
            mTrafficSummary.setSummary(mTrafficSummary.getEntries()[index]);
            return true;
        } else if (preference == mNetworkSpeedBitByte) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_SPEED_BIT_BYTE,
                value ? 1 : 0);
            return true;
        } else if (preference == mNetworkSpeedHide) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_SPEED_HIDE_TRAFFIC,
                value ? 1 : 0);
            return true;
        } else if (preference == mNetworkSpeedTextColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_SPEED_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference ==  mNetworkSpeedIconColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.STATUS_BAR_NETWORK_SPEED_ICON_COLOR, intHex);
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

        StatusBarNetworkSpeedStyle getOwner() {
            return (StatusBarNetworkSpeedStyle) getTargetFragment();
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
                                Settings.System.STATUS_BAR_NETWORK_SPEED_INDICATOR, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_TRAFFIC_SUMMARY, 3000);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_NETWORK_SPEED_BIT_BYTE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_NETWORK_SPEED_HIDE_TRAFFIC, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_NETWORK_SPEED_TEXT_COLOR,
                                0xffffffff);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_NETWORK_SPEED_ICON_COLOR,
                                0xffffffff);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_NETWORK_SPEED_INDICATOR, 2);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_TRAFFIC_SUMMARY, 3000);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_NETWORK_SPEED_BIT_BYTE, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_NETWORK_SPEED_HIDE_TRAFFIC, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_NETWORK_SPEED_TEXT_COLOR,
                                0xffff0000);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_NETWORK_SPEED_ICON_COLOR,
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
