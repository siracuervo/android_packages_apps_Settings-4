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
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
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

public class WeatherColors extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_WEATHER_PANEL_BACKGROUND_COLOR =
            "weather_panel_background_color";
    private static final String PREF_WEATHER_PANEL_BACKGROUND_PRESSED_COLOR =
            "weather_panel_background_pressed_color";
    private static final String PREF_WEATHER_ICON_COLOR =
            "weather_icon_color";
    private static final String PREF_WEATHER_BUTTON_ICON_COLOR =
            "weather_button_icon_color";
    private static final String PREF_WEATHER_TEXT_COLOR =
            "weather_text_color";

    private static final int DEFAULT_BACKGROUND_COLOR =
            0xff191919;
    private static final int DEFAULT_BACKGROUND_PRESSED_COLOR =
            0xffffffff;
    private static final int DEFAULT_ICON_COLOR =
            0xffffffff;
    private static final int DEFAULT_BUTTON_ICON_COLOR =
            0xffffffff;
    private static final int DEFAULT_TEXT_COLOR =
            0xffffffff;

    private static final int MENU_RESET   = Menu.FIRST;
    private static final int DLG_RESET       = 0;

    private ColorPickerPreference mBackgroundColor;
    private ColorPickerPreference mBackgroundPressedColor;
    private ColorPickerPreference mIconColor;
    private ColorPickerPreference mButtonIconColor;
    private ColorPickerPreference mTextColor;

    private ContentResolver mResolver;

    private ContentObserver mWeatherStyleObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            updatePreferences();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        refreshSettings();
    }

    public void refreshSettings() {
        PreferenceScreen prefScreen = getPreferenceScreen();
        if (prefScreen != null) {
            prefScreen.removeAll();
        }

        addPreferencesFromResource(R.xml.weather_colors);
        mResolver = getActivity().getContentResolver();

        int intColor = 0xffffffff;
        String hexColor = String.format("#%08x", (0xffffffff & 0xffffffff));

        mBackgroundColor =
                (ColorPickerPreference) findPreference(PREF_WEATHER_PANEL_BACKGROUND_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_WEATHER_BACKGROUND_COLOR,
                DEFAULT_BACKGROUND_COLOR); 
        mBackgroundColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBackgroundColor.setSummary(hexColor);
        mBackgroundColor.setAlphaSliderEnabled(true);
        mBackgroundColor.setOnPreferenceChangeListener(this);

        mBackgroundPressedColor =
                (ColorPickerPreference) findPreference(PREF_WEATHER_PANEL_BACKGROUND_PRESSED_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_WEATHER_BACKGROUND_PRESSED_COLOR,
                DEFAULT_BACKGROUND_PRESSED_COLOR); 
        mBackgroundPressedColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mBackgroundPressedColor.setSummary(hexColor);
        mBackgroundPressedColor.setAlphaSliderEnabled(true);
        mBackgroundPressedColor.setOnPreferenceChangeListener(this);

        mIconColor =
                (ColorPickerPreference) findPreference(PREF_WEATHER_ICON_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_WEATHER_ICON_COLOR,
                DEFAULT_ICON_COLOR); 
        mIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mIconColor.setSummary(hexColor);
        mIconColor.setOnPreferenceChangeListener(this);

        mButtonIconColor =
                (ColorPickerPreference) findPreference(PREF_WEATHER_BUTTON_ICON_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_WEATHER_BUTTON_ICON_COLOR,
                DEFAULT_ICON_COLOR); 
        mButtonIconColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mButtonIconColor.setSummary(hexColor);
        mButtonIconColor.setOnPreferenceChangeListener(this);

        mTextColor =
                (ColorPickerPreference) findPreference(PREF_WEATHER_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_EXPANDED_WEATHER_TEXT_COLOR,
                DEFAULT_TEXT_COLOR); 
        mTextColor.setNewPreviewColor(intColor);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mTextColor.setSummary(hexColor);
        mTextColor.setOnPreferenceChangeListener(this);

        updatePreferences();
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
                return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.STATUS_BAR_EXPANDED_WEATHER_STYLE), true,
                mWeatherStyleObserver);
        getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.STATUS_BAR_EXPANDED_WEATHER_ICON), true,
                mWeatherStyleObserver);
        updatePreferences();
    }

    @Override
    public void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(mWeatherStyleObserver);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int intHex;
        String hex;

        if (preference == mBackgroundColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_WEATHER_BACKGROUND_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mBackgroundPressedColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_WEATHER_BACKGROUND_PRESSED_COLOR,
                    intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_WEATHER_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mButtonIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_WEATHER_BUTTON_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_WEATHER_TEXT_COLOR, intHex);
            preference.setSummary(hex);
            return true;
         }
         return false;
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

        WeatherColors getOwner() {
            return (WeatherColors) getTargetFragment();
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
                                Settings.System.STATUS_BAR_EXPANDED_WEATHER_BACKGROUND_COLOR,
                                DEFAULT_BACKGROUND_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_EXPANDED_WEATHER_BACKGROUND_PRESSED_COLOR,
                                DEFAULT_BACKGROUND_PRESSED_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_EXPANDED_WEATHER_ICON_COLOR,
                                DEFAULT_ICON_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_EXPANDED_WEATHER_BUTTON_ICON_COLOR,
                                DEFAULT_BUTTON_ICON_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_EXPANDED_WEATHER_TEXT_COLOR,
                                DEFAULT_TEXT_COLOR);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_EXPANDED_WEATHER_BACKGROUND_COLOR,
                                DEFAULT_BACKGROUND_COLOR);
                           Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_EXPANDED_WEATHER_BACKGROUND_PRESSED_COLOR,
                                0xff33b5e5);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_EXPANDED_WEATHER_ICON_COLOR,
                                0xff33b5e5);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_EXPANDED_WEATHER_BUTTON_ICON_COLOR,
                                0xff33b5e5);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.STATUS_BAR_EXPANDED_WEATHER_TEXT_COLOR,
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

    private void updatePreferences() {
        boolean usePanel = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_WEATHER_STYLE, 0) == 0;
        boolean isStyleMonochrome = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_WEATHER_ICON, 0) == 0;

        mIconColor.setEnabled(usePanel && isStyleMonochrome);
        mButtonIconColor.setEnabled(usePanel);
    }
}
