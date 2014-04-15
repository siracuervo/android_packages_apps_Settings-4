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

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.internal.util.darkkat.DeviceUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class InterfaceLockscreenSettings extends SettingsPreferenceFragment {

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

        addPreferencesFromResource(R.xml.interface_lockscreen_settings);

        ContentResolver resolver = getActivity().getContentResolver();

        boolean lockscreenEightTargetsEnabled = Settings.System.getInt(resolver,
                        Settings.System.LOCKSCREEN_EIGHT_TARGETS, 0) == 1;

        // Update the lockscreen shortcuts screen enabled/disabled state
        // and update the summary, or remove it on hybrid/tablet
        if (!DeviceUtils.isPhone(getActivity())) {
            removePreference("lockscreen_shortcut_settings");
        } else {
            PreferenceScreen lockscreenShortcutSettings =
                    (PreferenceScreen) findPreference("lockscreen_shortcut_settings");
            lockscreenShortcutSettings.setEnabled(!lockscreenEightTargetsEnabled);
            if (lockscreenEightTargetsEnabled) {
                lockscreenShortcutSettings.setSummary(
                        R.string.lockscreen_shortcut_settings_disabled_summary);
            } else {
                lockscreenShortcutSettings.setSummary(
                        R.string.lockscreen_shortcut_settings_summary);
            }
        }

        // update the summary for mobile network and wifi only devices
        PreferenceScreen lockscreenMiskSettings =
                (PreferenceScreen) findPreference("lockscreen_misk_settings");
        if (!Utils.isWifiOnly(getActivity())) {
            lockscreenMiskSettings.setSummary(
                    R.string.lockscreen_misk_settings_summary);
        } else {
            lockscreenMiskSettings.setSummary(
                    R.string.lockscreen_misk_settings_wifi_only_summary);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshSettings();
    }
}
