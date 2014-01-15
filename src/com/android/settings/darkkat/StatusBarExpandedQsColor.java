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
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.widget.SeekBarPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarExpandedQsColor extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String TAG = "StatusBarExpandedQsColor";

    private static final String PREF_QUICK_TILES_CAT_SIGNAL_ACTIVITY =
            "quick_tiles_cat_signal_activity";
    private static final String PREF_QUICK_TILES_CAT_WIFI_ACTIVITY =
            "quick_tiles_cat_wifi_activity";
    private static final String PREF_USE_DIFFERENT_ACTIVITY_COLOR =
            "quick_tiles_use_different_activity_color";
    private static final String PREF_QUICK_TILES_BG_COLOR =
            "quick_tiles_bg_color";
    private static final String PREF_QUICK_TILES_BG_PRESSED_COLOR =
            "quick_tiles_bg_pressed_color";
    private static final String PREF_QUICK_TILES_ALPHA =
            "quick_tiles_alpha";
    private static final String PREF_QUICK_TILES_TEXT_COLOR =
            "quick_tiles_text_color";
    private static final String PREF_QUICK_TILES_ICON_NORMAL_COLOR =
            "quick_tiles_icon_normal_color";
    private static final String PREF_QUICK_TILES_ICON_ENABLED_COLOR =
            "quick_tiles_icon_enabled_color";
    private static final String PREF_QUICK_TILES_ICON_DISABLED_COLOR =
            "quick_tiles_icon_disabled_color";
    private static final String PREF_QUICK_TILES_SIGNAL_ACTIVITY_NORMAL_COLOR =
            "quick_tiles_signal_activity_normal_color";
    private static final String PREF_QUICK_TILES_SIGNAL_ACTIVITY_CONNECTED_COLOR =
            "quick_tiles_signal_activity_connected_color";
    private static final String PREF_QUICK_TILES_WIFI_ACTIVITY_NORMAL_COLOR =
            "quick_tiles_wifi_activity_normal_color";
    private static final String PREF_QUICK_TILES_WIFI_ACTIVITY_CONNECTED_COLOR =
            "quick_tiles_wifi_activity_connected_color";

    private static final int DEFAULT_QUICK_TILES_TEXT_COLOR = 0xffcccccc;
    private static final int DEFAULT_QUICK_TILES_BG_COLOR = 0xff212121;
    private static final int DEFAULT_QUICK_TILES_BG_PRESSED_COLOR = 0xff161616;
    private static final int DEFAULT_QUICK_TILES_ICON_NORMAL_COLOR = 0xffffffff;
    private static final int DEFAULT_QUICK_TILES_ICON_ENABLED_COLOR = 0xffffffff;
    private static final int DEFAULT_QUICK_TILES_ICON_DISABLED_COLOR = 0xff404040;

    private static final int MENU_RESET = Menu.FIRST;

    private static final int DLG_RESET = 0;

    private CheckBoxPreference mUseDifferentActivityColor;
    private ColorPickerPreference mQuickTilesBgColor;
    private ColorPickerPreference mQuickTilesBgPressedColor;
    private ColorPickerPreference mQuickTilesTextColor;
    private SeekBarPreference mQsTileAlpha;
    private ColorPickerPreference mQuickTilesIconNormalColor;
    private ColorPickerPreference mQuickTilesIconEnabledColor;
    private ColorPickerPreference mQuickTilesIconDisabledColor;
    private ColorPickerPreference mSignalActivityNormalColor;
    private ColorPickerPreference mSignalActivityConnectedColor;
    private ColorPickerPreference mWifiActivityNormalColor;
    private ColorPickerPreference mWifiActivityConnectedColor;

    private ContentResolver mResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    private PreferenceScreen refreshSettings() {
        PreferenceScreen prefs = getPreferenceScreen();
        if (prefs != null) {
            prefs.removeAll();
        }

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.status_bar_expanded_qs_color);

        prefs = getPreferenceScreen();

        mResolver = getActivity().getContentResolver();

        boolean useDifferentActivityColor = Settings.System.getInt(mResolver,
                        Settings.System.QUICK_TILES_USE_DIFFERENT_ACTIVITY_COLOR, 1) == 1;
        int intColor;
        String hexColor;

        mUseDifferentActivityColor = (CheckBoxPreference) findPreference(PREF_USE_DIFFERENT_ACTIVITY_COLOR);
        mUseDifferentActivityColor.setChecked(useDifferentActivityColor);
        mUseDifferentActivityColor.setOnPreferenceChangeListener(this);

        mQuickTilesBgColor = (ColorPickerPreference) findPreference(PREF_QUICK_TILES_BG_COLOR);
        mQuickTilesBgColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(mResolver, Settings.System.QUICK_TILES_BG_COLOR, DEFAULT_QUICK_TILES_BG_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mQuickTilesBgColor.setSummary(hexColor);
        mQuickTilesBgColor.setNewPreviewColor(intColor);

        mQuickTilesBgPressedColor = (ColorPickerPreference) findPreference(PREF_QUICK_TILES_BG_PRESSED_COLOR);
        mQuickTilesBgPressedColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(mResolver, Settings.System.QUICK_TILES_BG_PRESSED_COLOR, DEFAULT_QUICK_TILES_BG_PRESSED_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mQuickTilesBgPressedColor.setSummary(hexColor);
        mQuickTilesBgPressedColor.setNewPreviewColor(intColor);

        mQuickTilesTextColor = (ColorPickerPreference) findPreference(PREF_QUICK_TILES_TEXT_COLOR);
        mQuickTilesTextColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(mResolver, Settings.System.QUICK_TILES_TEXT_COLOR, DEFAULT_QUICK_TILES_TEXT_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mQuickTilesTextColor.setSummary(hexColor);
        mQuickTilesTextColor.setNewPreviewColor(intColor);

        float transparency;
        try{
            transparency = Settings.System.getFloat(getContentResolver(),
                    Settings.System.QUICK_TILES_BG_ALPHA);
        } catch (Exception e) {
            transparency = 0;
            Settings.System.putFloat(getContentResolver(),
                    Settings.System.QUICK_TILES_BG_ALPHA, 0.0f);
        }
        mQsTileAlpha = (SeekBarPreference) findPreference(PREF_QUICK_TILES_ALPHA);
        mQsTileAlpha.setInitValue((int) (transparency * 100));
        mQsTileAlpha.setProperty(Settings.System.QUICK_TILES_BG_ALPHA);
        mQsTileAlpha.setOnPreferenceChangeListener(this);

        mQuickTilesIconNormalColor = (ColorPickerPreference) findPreference(PREF_QUICK_TILES_ICON_NORMAL_COLOR);
        mQuickTilesIconNormalColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(mResolver, Settings.System.QUICK_TILES_ICON_NORMAL_COLOR, DEFAULT_QUICK_TILES_ICON_NORMAL_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mQuickTilesIconNormalColor.setSummary(hexColor);
        mQuickTilesIconNormalColor.setNewPreviewColor(intColor);

        mQuickTilesIconEnabledColor = (ColorPickerPreference) findPreference(PREF_QUICK_TILES_ICON_ENABLED_COLOR);
        mQuickTilesIconEnabledColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(mResolver, Settings.System.QUICK_TILES_ICON_ENABLED_COLOR, DEFAULT_QUICK_TILES_ICON_ENABLED_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mQuickTilesIconEnabledColor.setSummary(hexColor);
        mQuickTilesIconEnabledColor.setNewPreviewColor(intColor);

        mQuickTilesIconDisabledColor = (ColorPickerPreference) findPreference(PREF_QUICK_TILES_ICON_DISABLED_COLOR);
        mQuickTilesIconDisabledColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(mResolver, Settings.System.QUICK_TILES_ICON_DISABLED_COLOR, DEFAULT_QUICK_TILES_ICON_DISABLED_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mQuickTilesIconDisabledColor.setSummary(hexColor);
        mQuickTilesIconDisabledColor.setNewPreviewColor(intColor);


        // Remove mobile network related preferences on Wifi only devices
        PreferenceCategory categorySignalActivity = (PreferenceCategory) findPreference(PREF_QUICK_TILES_CAT_SIGNAL_ACTIVITY);
        mSignalActivityNormalColor = (ColorPickerPreference) findPreference(PREF_QUICK_TILES_SIGNAL_ACTIVITY_NORMAL_COLOR);
        mSignalActivityConnectedColor = (ColorPickerPreference) findPreference(PREF_QUICK_TILES_SIGNAL_ACTIVITY_CONNECTED_COLOR);
        if (!Utils.isWifiOnly(getActivity())) {
            // Remove uneeded preferences depending on enabled states
            if (useDifferentActivityColor) {
                intColor = Settings.System.getInt(mResolver, Settings.System.QUICK_TILES_SIGNAL_ACTIVITY_NORMAL_COLOR, 0xffff8800); 
                mSignalActivityNormalColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mSignalActivityNormalColor.setSummary(hexColor);
                mSignalActivityNormalColor.setOnPreferenceChangeListener(this);

                intColor = Settings.System.getInt(mResolver, Settings.System.QUICK_TILES_SIGNAL_ACTIVITY_CONNECTED_COLOR, 0xffffffff); 
                mSignalActivityConnectedColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mSignalActivityConnectedColor.setSummary(hexColor);
                mSignalActivityConnectedColor.setOnPreferenceChangeListener(this);
            } else {
                categorySignalActivity.removePreference(mSignalActivityNormalColor);
                categorySignalActivity.removePreference(mSignalActivityConnectedColor);
                removePreference(PREF_QUICK_TILES_CAT_SIGNAL_ACTIVITY);
            }
        } else if (categorySignalActivity != null) {
            categorySignalActivity.removePreference(mSignalActivityNormalColor);
            categorySignalActivity.removePreference(mSignalActivityConnectedColor);
            removePreference(PREF_QUICK_TILES_CAT_SIGNAL_ACTIVITY);
        }

        // Remove uneeded preferences depending on enabled states
        PreferenceCategory categoryWifiActivity = (PreferenceCategory) findPreference(PREF_QUICK_TILES_CAT_WIFI_ACTIVITY);
        mWifiActivityNormalColor = (ColorPickerPreference) findPreference(PREF_QUICK_TILES_WIFI_ACTIVITY_NORMAL_COLOR);
        mWifiActivityConnectedColor = (ColorPickerPreference) findPreference(PREF_QUICK_TILES_WIFI_ACTIVITY_CONNECTED_COLOR);
        if (useDifferentActivityColor) {
            intColor = Settings.System.getInt(mResolver, Settings.System.QUICK_TILES_WIFI_ACTIVITY_NORMAL_COLOR, 0xffff8800); 
            mWifiActivityNormalColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mWifiActivityNormalColor.setSummary(hexColor);
            mWifiActivityNormalColor.setOnPreferenceChangeListener(this);

            intColor = Settings.System.getInt(mResolver, Settings.System.QUICK_TILES_WIFI_ACTIVITY_CONNECTED_COLOR, 0xffffffff); 
            mWifiActivityConnectedColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mWifiActivityConnectedColor.setSummary(hexColor);
            mWifiActivityConnectedColor.setOnPreferenceChangeListener(this);
        } else {
            categoryWifiActivity.removePreference(mWifiActivityNormalColor);
            categoryWifiActivity.removePreference(mWifiActivityConnectedColor);
            removePreference(PREF_QUICK_TILES_CAT_WIFI_ACTIVITY);
        }

        setHasOptionsMenu(true);
        return prefs;
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
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String hex;
        int intHex;

        if (preference == mUseDifferentActivityColor) {
            Settings.System.putInt(mResolver,
                    Settings.System.QUICK_TILES_USE_DIFFERENT_ACTIVITY_COLOR,
                    (Boolean) newValue ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mQuickTilesBgColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QUICK_TILES_BG_COLOR,
                    intHex);
            return true;
        } else if (preference == mQuickTilesBgPressedColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QUICK_TILES_BG_PRESSED_COLOR,
                    intHex);
            return true;
        } else if (preference == mQuickTilesTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QUICK_TILES_TEXT_COLOR,
                    intHex);
            return true;
        } else if (preference == mQuickTilesIconNormalColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QUICK_TILES_ICON_NORMAL_COLOR,
                    intHex);
            return true;
        } else if (preference == mQuickTilesIconEnabledColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QUICK_TILES_ICON_ENABLED_COLOR,
                    intHex);
            return true;
        } else if (preference == mQuickTilesIconDisabledColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QUICK_TILES_ICON_DISABLED_COLOR,
                    intHex);
            return true;
        } else if (preference == mQsTileAlpha) {
            float valNav = Float.parseFloat((String) newValue);
            Settings.System.putFloat(getContentResolver(),
                    Settings.System.QUICK_TILES_BG_ALPHA, valNav / 100);
            return true;
        } else if (preference == mSignalActivityNormalColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QUICK_TILES_SIGNAL_ACTIVITY_NORMAL_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mSignalActivityConnectedColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QUICK_TILES_SIGNAL_ACTIVITY_CONNECTED_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mWifiActivityNormalColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QUICK_TILES_WIFI_ACTIVITY_NORMAL_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mWifiActivityConnectedColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.QUICK_TILES_WIFI_ACTIVITY_CONNECTED_COLOR,
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

        StatusBarExpandedQsColor getOwner() {
            return (StatusBarExpandedQsColor) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_qs_style_reset_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.dlg_reset_android,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver, Settings.System.QUICK_TILES_BG_COLOR,
                                    DEFAULT_QUICK_TILES_BG_COLOR);
                            Settings.System.putInt(getOwner().mResolver, Settings.System.QUICK_TILES_BG_PRESSED_COLOR,
                                    DEFAULT_QUICK_TILES_BG_PRESSED_COLOR);
                            Settings.System.putInt(getOwner().mResolver, Settings.System.QUICK_TILES_TEXT_COLOR,
                                    DEFAULT_QUICK_TILES_TEXT_COLOR);
                            Settings.System.putInt(getOwner().mResolver, Settings.System.QUICK_TILES_ICON_NORMAL_COLOR,
                                    DEFAULT_QUICK_TILES_ICON_NORMAL_COLOR);
                            Settings.System.putInt(getOwner().mResolver, Settings.System.QUICK_TILES_ICON_ENABLED_COLOR,
                                    DEFAULT_QUICK_TILES_ICON_ENABLED_COLOR);
                            Settings.System.putInt(getOwner().mResolver, Settings.System.QUICK_TILES_ICON_DISABLED_COLOR,
                                    DEFAULT_QUICK_TILES_ICON_DISABLED_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QUICK_TILES_SIGNAL_ACTIVITY_NORMAL_COLOR, 0xffff8800);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QUICK_TILES_SIGNAL_ACTIVITY_CONNECTED_COLOR, 0xffffffff);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QUICK_TILES_WIFI_ACTIVITY_NORMAL_COLOR, 0xffff8800);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QUICK_TILES_WIFI_ACTIVITY_CONNECTED_COLOR, 0xffffffff);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,Settings.System.QUICK_TILES_BG_COLOR,
                                    DEFAULT_QUICK_TILES_BG_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                     Settings.System.QUICK_TILES_BG_PRESSED_COLOR, 0xff33b5e5);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QUICK_TILES_TEXT_COLOR, 0xffffffff);
                            Settings.System.putInt(getOwner().mResolver, Settings.System.QUICK_TILES_ICON_NORMAL_COLOR,
                                    0xff33b5e5);
                            Settings.System.putInt(getOwner().mResolver, Settings.System.QUICK_TILES_ICON_ENABLED_COLOR,
                                    0xff33b5e5);
                            Settings.System.putInt(getOwner().mResolver, Settings.System.QUICK_TILES_ICON_DISABLED_COLOR,
                                    DEFAULT_QUICK_TILES_ICON_NORMAL_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QUICK_TILES_SIGNAL_ACTIVITY_NORMAL_COLOR, 0xffff8800);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QUICK_TILES_SIGNAL_ACTIVITY_CONNECTED_COLOR, 0xff33b5e5);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QUICK_TILES_WIFI_ACTIVITY_NORMAL_COLOR, 0xffff8800);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.QUICK_TILES_WIFI_ACTIVITY_CONNECTED_COLOR, 0xff33b5e5);
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
