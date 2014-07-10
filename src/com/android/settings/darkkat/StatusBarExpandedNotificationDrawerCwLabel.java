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
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarExpandedNotificationDrawerCwLabel
        extends SettingsPreferenceFragment
        implements OnPreferenceChangeListener {

    private static final String PREF_CW_LABEL_SHOW_CUSTOM_CARRIER_LABEL =
        "cw_label_show_custom_carrier_label";
    private static final String PREF_CW_LABEL_SHOW_WIFI_SSID =
        "cw_label_show_wifi_ssid";
    private static final String PREF_CW_LABEL_COLOR =
        "cw_label_color";

    private static final int DEFAULT_CW_LABEL_COLOR = 0xff999999;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private CheckBoxPreference mCwLabelShowCustomCarrierLabel;
    private CheckBoxPreference mCwLabelShowWifiSsid;
    private ColorPickerPreference mCwLabelColor;

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

        addPreferencesFromResource(R.xml.status_bar_expanded_notification_drawer_cw_label);
        mResolver = getActivity().getContentResolver();

        mCwLabelShowCustomCarrierLabel =
                (CheckBoxPreference) findPreference(PREF_CW_LABEL_SHOW_CUSTOM_CARRIER_LABEL);
        mCwLabelShowCustomCarrierLabel.setChecked(Settings.System.getInt(mResolver,
                Settings.System.NOTIFICATION_SHOW_CUSTOM_CARRIER_LABEL, 1) == 1);
        String customLabelText = Settings.System.getString(mResolver,
                Settings.System.CUSTOM_CARRIER_LABEL);
        if (customLabelText == null || customLabelText.length() == 0) {
            mCwLabelShowCustomCarrierLabel.setSummary(
                    R.string.custom_carrier_label_notset);
            mCwLabelShowCustomCarrierLabel.setEnabled(false);
        } else {
            mCwLabelShowCustomCarrierLabel.setSummary(
                    R.string.show_custom_carrier_label_enabled_summary);
            mCwLabelShowCustomCarrierLabel.setEnabled(true);
        }
        mCwLabelShowCustomCarrierLabel.setOnPreferenceChangeListener(this);

        // Remove show wifi ssid preferences on wifi only devices
        if (Utils.isWifiOnly(getActivity())) {
            removePreference(PREF_CW_LABEL_SHOW_WIFI_SSID);
        } else {
            mCwLabelShowWifiSsid =
                    (CheckBoxPreference) findPreference(PREF_CW_LABEL_SHOW_WIFI_SSID);
            mCwLabelShowWifiSsid.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.NOTIFICATION_SHOW_WIFI_SSID, 0) == 1);
            mCwLabelShowWifiSsid.setOnPreferenceChangeListener(this);
        }

        mCwLabelColor =
                (ColorPickerPreference) findPreference(PREF_CW_LABEL_COLOR);
        int cwLabelColor = Settings.System.getInt(mResolver,
                Settings.System.NOTIFICATION_CARRIER_WIFI_LABEL_COLOR,
                DEFAULT_CW_LABEL_COLOR);
        mCwLabelColor.setNewPreviewColor(cwLabelColor);
        String cwLabelHexColor = String.format("#%08x", (0xffffffff & cwLabelColor));
        mCwLabelColor.setSummary(cwLabelHexColor);
        mCwLabelColor.setOnPreferenceChangeListener(this);

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
        if (preference == mCwLabelShowCustomCarrierLabel) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                Settings.System.NOTIFICATION_SHOW_CUSTOM_CARRIER_LABEL,
                value ? 1 : 0);
            Intent i = new Intent();
            i.setAction("com.android.settings.LABEL_VISIBILITY_CHANGED");
            getActivity().sendBroadcast(i);
            return true;
        } else if (preference == mCwLabelShowWifiSsid) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                Settings.System.NOTIFICATION_SHOW_WIFI_SSID,
                value ? 1 : 0);
            Intent i = new Intent();
            i.setAction("com.android.settings.LABEL_VISIBILITY_CHANGED");
            getActivity().sendBroadcast(i);
            return true;
        } else if (preference == mCwLabelColor) {
            String hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.NOTIFICATION_CARRIER_WIFI_LABEL_COLOR,
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

        StatusBarExpandedNotificationDrawerCwLabel getOwner() {
            return (StatusBarExpandedNotificationDrawerCwLabel) getTargetFragment();
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
                                Settings.System.NOTIFICATION_SHOW_CUSTOM_CARRIER_LABEL, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.NOTIFICATION_SHOW_WIFI_SSID, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.NOTIFICATION_CARRIER_WIFI_LABEL_COLOR,
                                DEFAULT_CW_LABEL_COLOR);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.NOTIFICATION_SHOW_CUSTOM_CARRIER_LABEL, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.NOTIFICATION_SHOW_WIFI_SSID, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.NOTIFICATION_CARRIER_WIFI_LABEL_COLOR,
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
