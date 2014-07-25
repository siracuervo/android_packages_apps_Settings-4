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
import android.database.ContentObserver;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
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

public class StatusBarExpandedWeatherOptions extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_GENERAL =
            "weather_cat_general";
    private static final String PREF_CAT_ADDITIONALS =
            "weather_cat_additionals";
    private static final String PREF_WEATHER_ENABLE =
            "enable_weather";
    private static final String PREF_WEATHER_STYLE =
            "weather_style";
    private static final String PREF_WEATHER_ICON_STYLE =
            "weather_icon_style";
    private static final String PREF_WEATHER_USE_CELCIUS =
            "weather_use_celcius";
    private static final String PREF_WEATHER_USE_CUSTOM_LOCATION =
            "weather_use_custom_location";
    private static final String PREF_WEATHER_CUSTOM_LOCATION =
            "weather_custom_location";
    private static final String PREF_WEATHER_REFRESH_INTERVAL =
            "weather_refresh_interval";
    private static final String PREF_WEATHER_SHOW_LOCATION =
            "weather_show_location";
    private static final String PREF_WEATHER_SHOW_HUMIDITY =
            "weather_show_humidity";
    private static final String PREF_WEATHER_SHOW_WIND =
            "weather_show_wind";
    private static final String PREF_WEATHER_SHOW_TIMESTAMP =
            "weather_show_timestamp";

    private static final int MENU_REFRESH = Menu.FIRST;
    private static final int DLG_LOC_WARNING = 1;

    private CheckBoxPreference mEnableWeather;
    private ListPreference mWeatherStyle;
    private ListPreference mWeatherIconStyle;
    private CheckBoxPreference mUseCelcius;
    private CheckBoxPreference mUseCustomLoc;
    private EditTextPreference mCustomLoc;
    private ListPreference mRefreshInterval;
    private CheckBoxPreference mShowLoc;
    private CheckBoxPreference mShowHumidity;
    private CheckBoxPreference mShowWind;
    private CheckBoxPreference mShowTimestamp;

    private ContentResolver mResolver;

    SharedPreferences prefs;

    private ContentObserver mWeatherStyleObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            refreshSettings();
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

        addPreferencesFromResource(R.xml.status_bar_expanded_weather_options);

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

        PreferenceCategory catGeneral =
                (PreferenceCategory) findPreference(PREF_CAT_GENERAL);
        PreferenceCategory catAdditionals =
                (PreferenceCategory) findPreference(PREF_CAT_ADDITIONALS);
        mWeatherStyle =
                (ListPreference) findPreference(PREF_WEATHER_STYLE);
        mWeatherIconStyle =
                (ListPreference) findPreference(PREF_WEATHER_ICON_STYLE);
        mUseCelcius =
                (CheckBoxPreference) findPreference(PREF_WEATHER_USE_CELCIUS);
        mUseCustomLoc =
                (CheckBoxPreference) findPreference(PREF_WEATHER_USE_CUSTOM_LOCATION);
        mCustomLoc =
                (EditTextPreference) findPreference(PREF_WEATHER_CUSTOM_LOCATION);
        mRefreshInterval =
                (ListPreference) findPreference(PREF_WEATHER_REFRESH_INTERVAL);
        mShowLoc =
                (CheckBoxPreference) findPreference(PREF_WEATHER_SHOW_LOCATION);
        mShowWind =
                (CheckBoxPreference) findPreference(PREF_WEATHER_SHOW_WIND);
        mShowHumidity =
                (CheckBoxPreference) findPreference(PREF_WEATHER_SHOW_HUMIDITY);
        mShowTimestamp =
                (CheckBoxPreference) findPreference(PREF_WEATHER_SHOW_TIMESTAMP);

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
                catGeneral.removePreference(mWeatherIconStyle);
            }

            mUseCelcius.setChecked(WeatherPrefs.getUseCelcius(getActivity()));
            mUseCelcius.setOnPreferenceChangeListener(this);

            mUseCustomLoc.setChecked(useCustomLoc);
            mUseCustomLoc.setOnPreferenceChangeListener(this);

            if (useCustomLoc) {
                mCustomLoc
                        .setSummary(WeatherPrefs.getCustomLocation(getActivity()));
                mCustomLoc.setOnPreferenceChangeListener(this);
            } else {
                catGeneral.removePreference(mCustomLoc);
            }

            mRefreshInterval.setSummary(
                    Integer.toString(WeatherPrefs.getRefreshInterval(getActivity()))
                    + getResources().getString(R.string.weather_refresh_interval_minutes));
            mRefreshInterval.setOnPreferenceChangeListener(this);

            mShowLoc.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.STATUS_BAR_EXPANDED_WEATHER_SHOW_LOCATION, 1) == 1);
            mShowLoc.setOnPreferenceChangeListener(this);

            if (usePanel) {
                mShowHumidity.setChecked(Settings.System.getInt(getContentResolver(),
                        Settings.System.STATUS_BAR_EXPANDED_WEATHER_SHOW_HUMIDITY, 0) == 1);
                mShowHumidity.setOnPreferenceChangeListener(this);

                mShowWind.setChecked(Settings.System.getInt(getContentResolver(),
                        Settings.System.STATUS_BAR_EXPANDED_WEATHER_SHOW_WIND, 0) == 1);
                mShowWind.setOnPreferenceChangeListener(this);

                mShowTimestamp.setChecked(Settings.System.getInt(getContentResolver(),
                        Settings.System.STATUS_BAR_EXPANDED_WEATHER_SHOW_TIMESTAMP, 0) == 1);
                mShowTimestamp.setOnPreferenceChangeListener(this);
            } else {
                catAdditionals.removePreference(mShowHumidity);
                catAdditionals.removePreference(mShowWind);
                catAdditionals.removePreference(mShowTimestamp);
            }

            if (!Settings.Secure.isLocationProviderEnabled(
                    getContentResolver(), LocationManager.NETWORK_PROVIDER)
                    && !mUseCustomLoc.isChecked()) {
                showDialogInner(DLG_LOC_WARNING, true);
            }
        } else {
            catGeneral.removePreference(mWeatherStyle);
            if (mWeatherIconStyle != null) {
                catGeneral.removePreference(mWeatherIconStyle);
            }
            catGeneral.removePreference(mUseCelcius);
            catGeneral.removePreference(mUseCustomLoc);
            if (mCustomLoc != null) {
                catGeneral.removePreference(mCustomLoc);
            }
            catGeneral.removePreference(mRefreshInterval);
            catAdditionals.removePreference(mShowLoc);
            if (mShowHumidity != null) {
                catAdditionals.removePreference(mShowHumidity);
                catAdditionals.removePreference(mShowWind);
                catAdditionals.removePreference(mShowTimestamp);
            }
            removePreference(PREF_CAT_ADDITIONALS);

        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.STATUS_BAR_EXPANDED_WEATHER_STYLE), true,
                mWeatherStyleObserver);
        refreshSettings();
    }

    @Override
    public void onPause() {
        super.onPause();
        getContentResolver().unregisterContentObserver(mWeatherStyleObserver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_REFRESH, 0, R.string.menu_stats_refresh)
                .setIcon(R.drawable.ic_menu_refresh_holo_dark)
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
        } else if (preference == mUseCelcius) {
            WeatherPrefs.setUseCelcius(getActivity(),
                    (Boolean) newValue);
            Intent i = new Intent(getActivity().getApplicationContext(),
                    WeatherRefreshService.class);
            i.setAction(WeatherService.INTENT_WEATHER_REQUEST);
            i.putExtra(WeatherService.INTENT_EXTRA_ISMANUAL, true);
            getActivity().getApplicationContext().startService(i);
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
        } else if (preference == mRefreshInterval) {
            int newVal = Integer.parseInt((String) newValue);
            preference.setSummary(newValue
                    + getResources().getString(
                    R.string.weather_refresh_interval_minutes));
            WeatherPrefs.setRefreshInterval(getActivity(), newVal);
            return true;
        } else if (preference == mShowLoc) {
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_WEATHER_SHOW_LOCATION,
                    ((Boolean) newValue) ? 1 : 0);
            return true;
        } else if (preference == mShowHumidity) {
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_WEATHER_SHOW_HUMIDITY,
                    ((Boolean) newValue) ? 1 : 0);
            return true;
        } else if (preference == mShowWind) {
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_WEATHER_SHOW_WIND,
                    ((Boolean) newValue) ? 1 : 0);
            return true;
        } else if (preference == mShowTimestamp) {
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_EXPANDED_WEATHER_SHOW_TIMESTAMP,
                    ((Boolean) newValue) ? 1 : 0);
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

        StatusBarExpandedWeatherOptions getOwner() {
            return (StatusBarExpandedWeatherOptions) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            final boolean state = getArguments().getBoolean("state");
            switch (id) {
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
