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

import android.os.Bundle; 
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.darkjelly.colorpicker.ColorPickerPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class NotificationHeaderStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "NotificationHeaderStyle";

    private static final String PREF_NOTIFICATION_HEADER_CLOCK_COLOR = "notification_header_clock_color";

    private ColorPickerPreference mNotificationHeaderClockColor;

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

        addPreferencesFromResource(R.xml.notification_drawer_header_style);

        mNotificationHeaderClockColor = (ColorPickerPreference) findPreference(PREF_NOTIFICATION_HEADER_CLOCK_COLOR);

        mNotificationHeaderClockColor.setOnPreferenceChangeListener(this);

        int notificationHeaderClockColor = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.NOTIFICATION_HEADER_CLOCK_COLOR, 0xffffffff);
        mNotificationHeaderClockColor.setNewPreviewColor(notificationHeaderClockColor);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.notification_drawer_header_style, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset_notification_drawer_header_style:
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.NOTIFICATION_HEADER_CLOCK_COLOR, 0xffffffff);
                refreshSettings();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNotificationHeaderClockColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NOTIFICATION_HEADER_CLOCK_COLOR, intHex);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
