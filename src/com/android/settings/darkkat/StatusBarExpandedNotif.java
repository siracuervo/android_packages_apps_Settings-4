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

import android.os.Bundle;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SeekBarPreference;

public class StatusBarExpandedNotif extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String TAG = "StatusBarExpandedNotif";

    private static final String PREF_NOTIFICATION_ALPHA = "notification_alpha";

    SeekBarPreference mNotificationAlpha;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar_expanded_notif);

        float notifTransparency;
        try{
            notifTransparency = Settings.System.getFloat(getContentResolver(),
                    Settings.System.NOTIFICATION_ALPHA);
        } catch (Exception e) {
            notifTransparency = 0;
            Settings.System.putFloat(getContentResolver(),
                    Settings.System.NOTIFICATION_ALPHA, 0.0f);
        }
        mNotificationAlpha = (SeekBarPreference) findPreference(PREF_NOTIFICATION_ALPHA);
        mNotificationAlpha.setInitValue((int) (notifTransparency * 100));
        mNotificationAlpha.setProperty(Settings.System.NOTIFICATION_ALPHA);
        mNotificationAlpha.setOnPreferenceChangeListener(this);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNotificationAlpha) {
            float valNav = Float.parseFloat((String) newValue);
            Settings.System.putFloat(getContentResolver(),
                    Settings.System.NOTIFICATION_ALPHA, valNav / 100);
            return true;
        }
        return false;
    }
}
