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
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.util.Date;

public class StatusBarClockDateStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener { 

    private static final String TAG = "StatusBarClockDateStyle";

    private static final String PREF_STAT_BAR_CAT_CLOCK = "status_bar_cat_clock";
    private static final String PREF_STAT_BAR_CAT_DATE = "status_bar_cat_date";
    private static final String PREF_STAT_BAR_CLOCK_DATE_POSITION = "status_bar_clock_date_position";
    private static final String PREF_STAT_BAR_CLOCK_DATE_COLOR = "status_bar_clock_date_color";
    private static final String PREF_STAT_BAR_AM_PM = "status_bar_am_pm";
    private static final String PREF_STAT_BAR_DATE_SIZE = "status_bar_date_size";
    private static final String PREF_STAT_BAR_DATE_STYLE = "status_bar_date_style";
    private static final String PREF_STAT_BAR_DATE_FORMAT = "status_bar_date_format";

    public static final int STAT_BAR_DATE_STYLE_LOWERCASE = 1;
    public static final int STAT_BAR_DATE_STYLE_UPPERCASE = 2;
    private static final int STAT_BAR_CUSTOM_DATE_FORMAT_INDEX = 18;

    private CheckBoxPreference mClockDatePosition;
    private ColorPickerPreference mClockDateColor;
    private ListPreference mClockAmPm;
    private CheckBoxPreference mDateSize;
    private ListPreference mDateStyle;
    private ListPreference mDateFormat;

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

        addPreferencesFromResource(R.xml.status_bar_clock_date_style);

        mResolver = getActivity().getContentResolver();

        boolean isDateEnabled = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_SHOW_DATE, 0) == 1;

        mClockDatePosition = (CheckBoxPreference) findPreference(PREF_STAT_BAR_CLOCK_DATE_POSITION);
        mClockDatePosition.setChecked(Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_CLOCK_POSITION, 0) == 1);
        mClockDatePosition.setOnPreferenceChangeListener(this);

        mClockDateColor = (ColorPickerPreference) findPreference(PREF_STAT_BAR_CLOCK_DATE_COLOR);
        int intColor = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_CLOCK_DATE_COLOR, 0xffffffff); 
        mClockDateColor.setNewPreviewColor(intColor);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mClockDateColor.setSummary(hexColor);
        mClockDateColor.setOnPreferenceChangeListener(this);

        PreferenceCategory statusBarCatClock = (PreferenceCategory) findPreference(PREF_STAT_BAR_CAT_CLOCK);

        // Remove "AM/PM Style" if 24 hour mode is enabled
        mClockAmPm = (ListPreference) findPreference(PREF_STAT_BAR_AM_PM);
        if (DateFormat.is24HourFormat(getActivity())) {
            statusBarCatClock.removePreference(mClockAmPm);
            removePreference(PREF_STAT_BAR_CAT_CLOCK);
        } else {
            mClockAmPm = (ListPreference) findPreference(PREF_STAT_BAR_AM_PM);
            int clockAmPm = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_AM_PM, 2);
            mClockAmPm.setValue(String.valueOf(clockAmPm));
            mClockAmPm.setSummary(mClockAmPm.getEntry());
            mClockAmPm.setOnPreferenceChangeListener(this);
        }

        PreferenceCategory statusBarDateCategory = (PreferenceCategory) findPreference(PREF_STAT_BAR_CAT_DATE);

        // Remove uneeded preferences depending on enabled states
        mDateSize = (CheckBoxPreference) findPreference(PREF_STAT_BAR_DATE_SIZE);
        mDateStyle = (ListPreference) findPreference(PREF_STAT_BAR_DATE_STYLE);
        mDateFormat = (ListPreference) findPreference(PREF_STAT_BAR_DATE_FORMAT);
        if (isDateEnabled) {
            mDateSize.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_DATE_SIZE, 0) == 1);
            mDateSize.setOnPreferenceChangeListener(this);

            int dateStyle = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_DATE_STYLE, 0);
            mDateStyle.setValue(String.valueOf(dateStyle));
            mDateStyle.setSummary(mDateStyle.getEntry());
            mDateStyle.setOnPreferenceChangeListener(this);

            if (mDateFormat.getValue() == null) {
                mDateFormat.setValue("EEE");
            } 
            mDateFormat.setOnPreferenceChangeListener(this);
            parseClockDateFormats();
        } else {
            statusBarDateCategory.removePreference(mDateSize);
            statusBarDateCategory.removePreference(mDateStyle);
            statusBarDateCategory.removePreference(mDateFormat);
            removePreference(PREF_STAT_BAR_CAT_DATE);
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.status_bar_clock_date_style, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.statusbar_clock_date_android_default:
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CLOCK_POSITION, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CLOCK_DATE_COLOR, 0xffffffff);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_AM_PM, 2);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_DATE_SIZE, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_DATE_STYLE, 0);
                Settings.System.putString(mResolver, Settings.System.STATUS_BAR_DATE_FORMAT, "EEE");
                refreshSettings();
                return true;
            case R.id.statusbar_clock_date_darkkat_default:
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CLOCK_POSITION, 1);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CLOCK_DATE_COLOR, 0xffff0000);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_AM_PM, 2);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_DATE_SIZE, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_DATE_STYLE, 0);
                Settings.System.putString(mResolver, Settings.System.STATUS_BAR_DATE_FORMAT, "EEE");
                refreshSettings();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean result = false;

        AlertDialog dialog;

        if (preference == mClockDatePosition) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CLOCK_POSITION, value ? 1 : 0);
            return true;
        } else if (preference == mClockDateColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CLOCK_DATE_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mClockAmPm) {
            int clockAmPm = Integer.valueOf((String) newValue);
            int index = mClockAmPm.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_AM_PM, clockAmPm);
            mClockAmPm.setSummary(mClockAmPm.getEntries()[index]);
            return true;
        } else if (preference == mDateSize) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_DATE_SIZE, value ? 1 : 0);
            return true;
        } else if (preference == mDateStyle) {
            int dateStyle = Integer.valueOf((String) newValue);
            int index = mDateStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_DATE_STYLE, dateStyle);
            mDateStyle.setSummary(mDateStyle.getEntries()[index]);
            return true;
        }  else if (preference == mDateFormat) {
            int index = mDateFormat.findIndexOfValue((String) newValue);

           if (index == STAT_BAR_CUSTOM_DATE_FORMAT_INDEX) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle(R.string.status_bar_date_string_edittext_title);
                alert.setMessage(R.string.status_bar_date_string_edittext_summary);

                final EditText input = new EditText(getActivity());
                String oldText = Settings.System.getString(mResolver, Settings.System.STATUS_BAR_DATE_FORMAT);
                if (oldText != null) {
                    input.setText(oldText);
                }
                alert.setView(input);

                alert.setPositiveButton(R.string.menu_save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int whichButton) {
                        String value = input.getText().toString();
                        if (value.equals("")) {
                            return;
                        }
                        Settings.System.putString(mResolver, Settings.System.STATUS_BAR_DATE_FORMAT, value);

                        return;
                    }
                });

                alert.setNegativeButton(R.string.menu_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        return;
                    }
                });
                dialog = alert.create();
                dialog.show();
            } else {
                if ((String) newValue != null) {
                    Settings.System.putString(mResolver, Settings.System.STATUS_BAR_DATE_FORMAT, (String) newValue);
                }
            }
            return true;
        } 
        return false;
    }

    private void parseClockDateFormats() {
        // Parse and repopulate mStatusBarDateFormat's entries based on current date.
        String[] dateEntries = getResources().getStringArray(R.array.status_bar_date_format_entries);
        CharSequence parsedDateEntries[];
        parsedDateEntries = new String[dateEntries.length];
        Date now = new Date();

        int lastEntry = dateEntries.length - 1;
        int dateFormat = Settings.System.getInt(mResolver, Settings.System.STATUS_BAR_DATE_STYLE, 2);
        for (int i = 0; i < dateEntries.length; i++) {
            if (i == lastEntry) {
                parsedDateEntries[i] = dateEntries[i];
            } else {
                String newDate;
                CharSequence dateString = DateFormat.format(dateEntries[i], now);
                if (dateFormat == STAT_BAR_DATE_STYLE_LOWERCASE) {
                    newDate = dateString.toString().toLowerCase();
                } else if (dateFormat == STAT_BAR_DATE_STYLE_UPPERCASE) {
                    newDate = dateString.toString().toUpperCase();
                } else {
                    newDate = dateString.toString();
                }

                parsedDateEntries[i] = newDate;
            }
        }
        mDateFormat.setEntries(parsedDateEntries);
    }
}
