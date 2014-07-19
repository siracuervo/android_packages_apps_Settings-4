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

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
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
import com.android.settings.darkkat.service.WeatherRefreshService;
import com.android.settings.darkkat.service.WeatherService;
import com.android.settings.darkkat.util.Helpers;
import com.android.settings.darkkat.weather.WeatherPrefs;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class StatusBarExpandedWeather extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_OPTIONS =
            "status_bar_expanded_weather_cat_options";
    private static final String PREF_CAT_COLORS =
            "status_bar_expanded_weather_cat_colors";
    private static final String PREF_WEATHER_ENABLE =
            "enable_weather";
    private static final String PREF_WEATHER_STYLE =
            "weather_style";
    private static final String PREF_WEATHER_ICON_STYLE =
            "weather_icon_style";
    private static final String PREF_WEATHER_USE_CUSTOM_LOCATION =
            "weather_use_custom_location";
    private static final String PREF_WEATHER_CUSTOM_LOCATION =
            "weather_custom_location";
    private static final String PREF_WEATHER_SHOW_LOCATION =
            "weather_show_location";
    private static final String PREF_WEATHER_USE_CELCIUS =
            "weather_use_celcius";
    private static final String PREF_WEATHER_REFRESH_INTERVAL =
            "weather_refresh_interval";
    private static final String PREF_WEATHER_PANEL_BACKGROUND_COLOR =
            "weather_panel_background_color";
    private static final String PREF_WEATHER_PANEL_BACKGROUND_PRESSED_COLOR =
            "weather_panel_background_pressed_color";
    private static final String PREF_WEATHER_ICON_COLOR =
            "weather_icon_color";
    private static final String PREF_WEATHER_TEXT_COLOR =
            "weather_text_color";

    private static final int DEFAULT_BACKGROUND_COLOR =
            0xff191919;
    private static final int DEFAULT_BACKGROUND_PRESSED_COLOR =
            0xff323232;
    private static final int DEFAULT_ICON_COLOR =
            0xffffffff;
    private static final int DEFAULT_TEXT_COLOR =
            0xffffffff;

    private static final int MENU_REFRESH = Menu.FIRST;
    private static final int MENU_RESET   = MENU_REFRESH + 1;

    private static final int DLG_RESET       = 0;
    private static final int DLG_LOC_WARNING = 1;

    private CheckBoxPreference mEnableWeather;
    private ListPreference mWeatherStyle;
    private ListPreference mWeatherIconStyle;
    private CheckBoxPreference mUseCustomLoc;
    private EditTextPreference mCustomLoc;
    private CheckBoxPreference mShowLoc;
    private CheckBoxPreference mUseCelcius;
    private ListPreference mRefreshInterval;
    private ColorPickerPreference mBackgroundColor;
    private ColorPickerPreference mBackgroundPressedColor;
    private ColorPickerPreference mIconColor;
    private ColorPickerPreference mTextColor;

    private ContentResolver mResolver;

    SharedPreferences prefs;

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

        addPreferencesFromResource(R.xml.status_bar_expanded_weather);

        mResolver = getActivity().getContentResolver();
        prefs = getActivity().getSharedPreferences(
                "weather", Context.MODE_WORLD_WRITEABLE);

        int intColor = 0xffffffff;
        String hexColor = String.format("#%08x", (0xffffffff & 0xffffffff));

        boolean enabled = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_EXPANDED_ENABLE_WEATHER, 0) == 1;

        mEnableWeather =
                (CheckBoxPreference) findPreference(PREF_WEATHER_ENABLE);
        mEnableWeather.setChecked(enabled);
        mEnableWeather.setOnPreferenceChangeListener(this);

       // Remove preferences depending on enabled states
        PreferenceCategory catOptions =
                (PreferenceCategory) findPreference(PREF_CAT_OPTIONS);
        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);
        mWeatherStyle =
                (ListPreference) findPreference(PREF_WEATHER_STYLE);
        mWeatherIconStyle =
                (ListPreference) findPreference(PREF_WEATHER_ICON_STYLE);
        mUseCustomLoc =
                (CheckBoxPreference) findPreference(PREF_WEATHER_USE_CUSTOM_LOCATION);
        mCustomLoc =
                (EditTextPreference) findPreference(PREF_WEATHER_CUSTOM_LOCATION);
        mShowLoc =
                (CheckBoxPreference) findPreference(PREF_WEATHER_SHOW_LOCATION);
        mUseCelcius =
                (CheckBoxPreference) findPreference(PREF_WEATHER_USE_CELCIUS);
        mRefreshInterval =
                (ListPreference) findPreference(PREF_WEATHER_REFRESH_INTERVAL);
        mBackgroundColor =
                (ColorPickerPreference) findPreference(PREF_WEATHER_PANEL_BACKGROUND_COLOR);
        mBackgroundPressedColor =
                (ColorPickerPreference) findPreference(PREF_WEATHER_PANEL_BACKGROUND_PRESSED_COLOR);
        mIconColor =
                (ColorPickerPreference) findPreference(PREF_WEATHER_ICON_COLOR);
        mTextColor =
                (ColorPickerPreference) findPreference(PREF_WEATHER_TEXT_COLOR);

        if (enabled) {
            boolean usePanel = Settings.System.getInt(mResolver,
                   Settings.System.STATUS_BAR_EXPANDED_WEATHER_STYLE, 0) == 0;
            boolean isStyleMonochrome = Settings.System.getInt(mResolver,
                   Settings.System.STATUS_BAR_EXPANDED_WEATHER_ICON, 0) == 0;
            boolean useCustomLoc = WeatherPrefs.getUseCustomLocation(getActivity());

            mWeatherStyle.setValue(Settings.System.getInt(getContentResolver(),
                    Settings.System.STATUS_BAR_EXPANDED_WEATHER_STYLE, 0) + "");
            mWeatherStyle.setSummary(mWeatherStyle.getEntry());
            mWeatherStyle.setOnPreferenceChangeListener(this);

            if (usePanel) {
                mWeatherIconStyle.setValue(Settings.System.getInt(getContentResolver(),
                        Settings.System.STATUS_BAR_EXPANDED_WEATHER_ICON, 0) + "");
                mWeatherIconStyle.setSummary(mWeatherIconStyle.getEntry());
                mWeatherIconStyle.setOnPreferenceChangeListener(this);
            } else {
                catOptions.removePreference(mWeatherIconStyle);
            }

            mUseCustomLoc.setChecked(useCustomLoc);
            mUseCustomLoc.setOnPreferenceChangeListener(this);

            if (useCustomLoc) {
                mCustomLoc
                        .setSummary(WeatherPrefs.getCustomLocation(getActivity()));
                mCustomLoc.setOnPreferenceChangeListener(this);
            } else {
                catOptions.removePreference(mCustomLoc);
            }

            mShowLoc.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.STATUS_BAR_EXPANDED_WEATHER_SHOW_LOCATION, 1) == 1);
            mShowLoc.setOnPreferenceChangeListener(this);

            mUseCelcius.setChecked(WeatherPrefs.getUseCelcius(getActivity()));
            mUseCelcius.setOnPreferenceChangeListener(this);

            mRefreshInterval.setSummary(
                    Integer.toString(WeatherPrefs.getRefreshInterval(getActivity()))
                    + getResources().getString(R.string.weather_refresh_interval_minutes));
            mRefreshInterval.setOnPreferenceChangeListener(this);

            if (usePanel) {
                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_EXPANDED_WEATHER_BACKGROUND_COLOR,
                        DEFAULT_BACKGROUND_COLOR); 
                mBackgroundColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mBackgroundColor.setSummary(hexColor);
                mBackgroundColor.setAlphaSliderEnabled(true);
                mBackgroundColor.setOnPreferenceChangeListener(this);

                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_EXPANDED_WEATHER_BACKGROUND_PRESSED_COLOR,
                        DEFAULT_BACKGROUND_PRESSED_COLOR); 
                mBackgroundPressedColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mBackgroundPressedColor.setSummary(hexColor);
                mBackgroundPressedColor.setAlphaSliderEnabled(true);
                mBackgroundPressedColor.setOnPreferenceChangeListener(this);
            } else {
                catColors.removePreference(mBackgroundColor);
                catColors.removePreference(mBackgroundPressedColor);
            }

            if (usePanel && isStyleMonochrome) {
                intColor = Settings.System.getInt(mResolver,
                        Settings.System.STATUS_BAR_EXPANDED_WEATHER_ICON_COLOR,
                        DEFAULT_ICON_COLOR); 
                mIconColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mIconColor.setSummary(hexColor);
                mIconColor.setOnPreferenceChangeListener(this);
            } else {
                catColors.removePreference(mIconColor);
            }

            intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_WEATHER_TEXT_COLOR,
                    DEFAULT_TEXT_COLOR); 
            mTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mTextColor.setSummary(hexColor);
            mTextColor.setOnPreferenceChangeListener(this);

            if (!Settings.Secure.isLocationProviderEnabled(
                    getContentResolver(), LocationManager.NETWORK_PROVIDER)
                    && !mUseCustomLoc.isChecked()) {
                showDialogInner(DLG_LOC_WARNING, true);
            }
        } else {
            catOptions.removePreference(mWeatherStyle);
            if (mWeatherIconStyle != null) {
                catOptions.removePreference(mWeatherIconStyle);
            }
            catOptions.removePreference(mUseCustomLoc);
            if (mCustomLoc != null) {
                catOptions.removePreference(mCustomLoc);
            }
            catOptions.removePreference(mRefreshInterval);
            catOptions.removePreference(mShowLoc);
            catOptions.removePreference(mUseCelcius);
            if (mBackgroundColor != null) {
                catColors.removePreference(mBackgroundColor);
                catColors.removePreference(mBackgroundPressedColor);
            }
            if (mIconColor != null) {
                catColors.removePreference(mIconColor);
            }
            catColors.removePreference(mTextColor);
            removePreference(PREF_CAT_COLORS);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_REFRESH, 0, R.string.menu_stats_refresh)
                .setIcon(R.drawable.ic_menu_refresh_holo_dark)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_settings_backup) // use the backup icon
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_REFRESH:
                Intent i = new Intent(getActivity().getApplicationContext(),
                        WeatherRefreshService.class);
                i.setAction(WeatherService.INTENT_WEATHER_REQUEST);
                i.putExtra(WeatherService.INTENT_EXTRA_ISMANUAL, true);
                getActivity().getApplicationContext().startService(i);
                Helpers.msgShort(getActivity().getApplicationContext(),
                        getString(R.string.weather_refreshing));
                return true;
            case MENU_RESET:
                showDialogInner(DLG_RESET, true);
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int index;
        int value;
        int intHex;
        String hex;

        if (preference == mEnableWeather) {
            boolean check = ((CheckBoxPreference) preference).isChecked();
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_ENABLE_WEATHER,
                    ((Boolean) newValue) ? 1 : 0);

            Intent i = new Intent(getActivity().getApplicationContext(),
                    WeatherRefreshService.class);
            i.setAction(WeatherService.INTENT_WEATHER_REQUEST);
            i.putExtra(WeatherService.INTENT_EXTRA_ISMANUAL, true);
            PendingIntent weatherRefreshIntent =
                    PendingIntent.getService(getActivity(), 0, i, 0);
            if (!check) {
                AlarmManager alarms = (AlarmManager) getActivity().getSystemService(
                        Context.ALARM_SERVICE);
                alarms.cancel(weatherRefreshIntent);
            } else {
                getActivity().startService(i);
            }
            refreshSettings();
            return true;
        } else if (preference == mWeatherStyle) {
             index =  mWeatherStyle.findIndexOfValue((String) newValue);
             value = Integer.parseInt((String) newValue);
             Settings.System.putInt(mResolver,
                     Settings.System.STATUS_BAR_EXPANDED_WEATHER_STYLE, value);
             mWeatherStyle.setSummary(
                 mWeatherStyle.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mWeatherIconStyle) {
             index =  mWeatherIconStyle.findIndexOfValue((String) newValue);
             value = Integer.parseInt((String) newValue);
             Settings.System.putInt(mResolver,
                     Settings.System.STATUS_BAR_EXPANDED_WEATHER_ICON, value);
             mWeatherIconStyle.setSummary(
                 mWeatherIconStyle.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mUseCustomLoc) {
            WeatherPrefs.setUseCustomLocation(getActivity(),
                    (Boolean) newValue);
            refreshSettings();
            return true;
        } else if (preference == mCustomLoc) {
            String newVal = (String) newValue;

            Intent i = new Intent(getActivity().getApplicationContext(),
                    WeatherRefreshService.class);
            getActivity().getApplicationContext().startService(i);
            preference.setSummary(newVal);
            WeatherPrefs.setCustomLocation(getActivity(), newVal);
            return true;
        } else if (preference == mShowLoc) {
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_WEATHER_SHOW_LOCATION,
                    ((Boolean) newValue) ? 1 : 0);
            return true;
        } else if (preference == mUseCelcius) {
            WeatherPrefs.setUseCelcius(getActivity(),
                    (Boolean) newValue);
            Intent i = new Intent(getActivity().getApplicationContext(),
                    WeatherRefreshService.class);
            i.setAction(WeatherService.INTENT_WEATHER_REQUEST);
            i.putExtra(WeatherService.INTENT_EXTRA_ISMANUAL, true);
            getActivity().getApplicationContext().startService(i);
            return true;
        } else if (preference == mRefreshInterval) {
            int newVal = Integer.parseInt((String) newValue);
            preference.setSummary(newValue
                    + getResources().getString(
                    R.string.weather_refresh_interval_minutes));
            WeatherPrefs.setRefreshInterval(getActivity(), newVal);
            return true;
        } else if (preference == mBackgroundColor) {
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

        StatusBarExpandedWeather getOwner() {
            return (StatusBarExpandedWeather) getTargetFragment();
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
                                Settings.System.STATUS_BAR_EXPANDED_WEATHER_TEXT_COLOR,
                                0xffff0000);
                            getOwner().refreshSettings();
                        }
                    })
                    .create();
            case DLG_LOC_WARNING:
                return new AlertDialog.Builder(getActivity())
                        .setTitle(getResources().getString(R.string.weather_loc_warning_title))
                        .setMessage(getResources().getString(R.string.weather_loc_warning_msg))
                        .setCancelable(false)
                        .setPositiveButton(
                                getResources().getString(R.string.weather_loc_warning_positive),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        Settings.Secure.setLocationProviderEnabled(
                                                getOwner().getContentResolver(),
                                                LocationManager.NETWORK_PROVIDER, true);
                                    }
                                })
                        .setNegativeButton(
                                getResources().getString(R.string.weather_loc_warning_negative),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
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
