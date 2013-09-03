/*
 * Copyright (C) 2013 Dark Jelly
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

package com.android.settings.darkjelly;

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
import android.provider.Settings.SettingNotFoundException;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText; 

import com.android.settings.darkjelly.colorpicker.ColorPickerPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.util.Date;

public class StatusBarClockStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener { 

    private static final String TAG = "StatusBarClockStyle";

    private static final String PREF_STAT_BAR_CAT_CLOCK = "status_bar_cat_clock";
    private static final String PREF_STAT_BAR_CLOCK_POSITION = "status_bar_clock_position";
    private static final String PREF_STAT_BAR_CLOCK_COLOR = "status_bar_clock_color";
    private static final String PREF_STAT_BAR_AM_PM = "status_bar_am_pm";
    private static final String PREF_STAT_BAR_DATE_SIZE = "status_bar_date_size";
    private static final String PREF_STAT_BAR_DATE_STYLE = "status_bar_date_style";
    private static final String PREF_STAT_BAR_DATE_FORMAT = "status_bar_date_format";

    public static final int STAT_BAR_DATE_STYLE_LOWERCASE = 1;
    public static final int STAT_BAR_DATE_STYLE_UPPERCASE = 2;
    private static final int STAT_BAR_CUSTOM_DATE_FORMAT_INDEX = 18;

    private CheckBoxPreference mStatusBarClockPosition;
    private ColorPickerPreference mStatusBarClockColor;
    private ListPreference mStatusBarAmPm;
    private CheckBoxPreference mStatusBarDateSize;
    private ListPreference mStatusBarDateStyle;
    private ListPreference mStatusBarDateFormat;

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

        addPreferencesFromResource(R.xml.status_bar_clock_style);
        mResolver = getActivity().getContentResolver();

        boolean isClockEnabled = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_SHOW_CLOCK, 1) == 1;
        boolean isDateEnabled = Settings.System.getInt(mResolver,
               Settings.System.STATUS_BAR_SHOW_DATE, 0) == 1;

        PreferenceCategory statusBarCatClock = (PreferenceCategory) findPreference(PREF_STAT_BAR_CAT_CLOCK);

        mStatusBarClockPosition = (CheckBoxPreference) findPreference(PREF_STAT_BAR_CLOCK_POSITION);
        mStatusBarClockPosition.setChecked((Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_CLOCK_POSITION, 0) == 1));
        mStatusBarClockPosition.setOnPreferenceChangeListener(this);

        mStatusBarClockColor = (ColorPickerPreference) findPreference(PREF_STAT_BAR_CLOCK_COLOR);
        int intColor = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_CLOCK_COLOR, 0xff33b5e5); 
        mStatusBarClockColor.setNewPreviewColor(intColor);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mStatusBarClockColor.setSummary(hexColor);
        mStatusBarClockColor.setOnPreferenceChangeListener(this);

        mStatusBarAmPm = (ListPreference) findPreference(PREF_STAT_BAR_AM_PM);
        if (DateFormat.is24HourFormat(getActivity())) {
            statusBarCatClock.removePreference(mStatusBarAmPm);
        } else {
            mStatusBarAmPm = (ListPreference) findPreference(PREF_STAT_BAR_AM_PM);
            int statusBarAmPm = Settings.System.getInt(mResolver,
                    Settings.System.STATUS_BAR_AM_PM, 2);

            mStatusBarAmPm.setValue(String.valueOf(statusBarAmPm));
            mStatusBarAmPm.setSummary(mStatusBarAmPm.getEntry());
            mStatusBarAmPm.setOnPreferenceChangeListener(this);
        }

        mStatusBarDateSize = (CheckBoxPreference) findPreference(PREF_STAT_BAR_DATE_SIZE);
        mStatusBarDateSize.setChecked((Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_DATE_SIZE, 0) == 1));
        mStatusBarDateSize.setOnPreferenceChangeListener(this);

        mStatusBarDateStyle = (ListPreference) findPreference(PREF_STAT_BAR_DATE_STYLE);
        int statusBarDateStyle = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_DATE_STYLE, 0);
        mStatusBarDateStyle.setValue(String.valueOf(statusBarDateStyle));
        mStatusBarDateStyle.setSummary(mStatusBarDateStyle.getEntry());
        mStatusBarDateStyle.setOnPreferenceChangeListener(this);

        mStatusBarDateFormat = (ListPreference) findPreference(PREF_STAT_BAR_DATE_FORMAT);
        if (mStatusBarDateFormat.getValue() == null) {
            mStatusBarDateFormat.setValue("EEE");
        } 
        mStatusBarDateFormat.setOnPreferenceChangeListener(this);

        if (isClockEnabled) {
            mStatusBarClockPosition.setEnabled(true);
            mStatusBarClockColor.setEnabled(true);
            if (mStatusBarAmPm != null) {
                mStatusBarAmPm.setEnabled(true);
            }
        } else {
            mStatusBarClockPosition.setEnabled(false);
            mStatusBarClockColor.setEnabled(false);
            if (mStatusBarAmPm != null) {
                mStatusBarAmPm.setEnabled(false);
            }
        }

        if (isDateEnabled) {
            mStatusBarDateSize.setEnabled(true);
            mStatusBarDateStyle.setEnabled(true);
            mStatusBarDateFormat.setEnabled(true);
        } else {
            mStatusBarDateSize.setEnabled(false);
            mStatusBarDateStyle.setEnabled(false);
            mStatusBarDateFormat.setEnabled(false);
        }

        parseClockDateFormats();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.status_bar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset_statusbar:
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CLOCK_POSITION, 0);
                Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CLOCK_COLOR, 0xff33b5e5);
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

        if (preference == mStatusBarClockPosition) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CLOCK_POSITION, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarAmPm) {
            int statusBarAmPm = Integer.valueOf((String) newValue);
            int index = mStatusBarAmPm.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_AM_PM, statusBarAmPm);
            mStatusBarAmPm.setSummary(mStatusBarAmPm.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarClockColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_CLOCK_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mStatusBarDateSize) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_DATE_SIZE, value ? 1 : 0);
            return true;
        } else if (preference == mStatusBarDateStyle) {
            int statusBarDateStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarDateStyle.findIndexOfValue((String) newValue);
            Settings.System.putInt(mResolver, Settings.System.STATUS_BAR_DATE_STYLE, statusBarDateStyle);
            mStatusBarDateStyle.setSummary(mStatusBarDateStyle.getEntries()[index]);
            return true;
        }  else if (preference == mStatusBarDateFormat) {
            int index = mStatusBarDateFormat.findIndexOfValue((String) newValue);

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
        mStatusBarDateFormat.setEntries(parsedDateEntries);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
