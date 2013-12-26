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

package com.android.settings.darkkat;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
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

public class LockscreenCarrierLabelStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "LockscreenCarrierLabelStyle";

    private static final String PREF_LOCKSCREEN_CUSTOM_LABEL_COLOR = "lockscreen_carrier_label_color";

    private ColorPickerPreference mLockscreenCustomLabelColor;

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

        addPreferencesFromResource(R.xml.lockscreen_carrier_label_style);
        mResolver = getActivity().getContentResolver();

        mLockscreenCustomLabelColor = (ColorPickerPreference) findPreference(PREF_LOCKSCREEN_CUSTOM_LABEL_COLOR);
        int color = Settings.System.getInt(mResolver,
                Settings.System.LOCKSCREEN_CARRIER_LABEL_COLOR, 0xffbebebe);
        mLockscreenCustomLabelColor.setNewPreviewColor(color);
        String hexColor = String.format("#%08x", (0xffffffff & color));
        mLockscreenCustomLabelColor.setSummary(hexColor);
        mLockscreenCustomLabelColor.setOnPreferenceChangeListener(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.lockscreen_carrier_label_style, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.lockscreen_cl_android_default:
                Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_CARRIER_LABEL_COLOR, 0xffbebebe);
                refreshSettings();
                return true;
            case R.id.lockscreen_cl_darkkat_default:
                Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_CARRIER_LABEL_COLOR, 0xff33b5e5);
                refreshSettings();
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mLockscreenCustomLabelColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver, Settings.System.LOCKSCREEN_CARRIER_LABEL_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
