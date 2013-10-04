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

import android.content.ContentResolver;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class NotificationDrawer extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {

    private static final String TAG = "NotificationDrawer";

    private static final String PREF_NOTIFICATION_DRAWER_COLLAPSE_BEHAVIOUR = "notification_drawer_collapse_on_dismiss";
    private static final String PREF_NOTIFICATION_DRAWER_ENABLE_QUICK_ACCESS = "notification_drawer_enable_quick_access";

    private ListPreference mCollapseOnDismiss;
    private CheckBoxPreference mEnableQuickAccess;

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

        addPreferencesFromResource(R.xml.notification_drawer);
        mResolver = getActivity().getContentResolver();
        boolean isQuickAccessEnabled = Settings.System.getInt(mResolver,
                Settings.System.QS_QUICK_ACCESS, 0) == 1;

        mCollapseOnDismiss = (ListPreference) findPreference(PREF_NOTIFICATION_DRAWER_COLLAPSE_BEHAVIOUR);
        int collapseBehaviour = Settings.System.getInt(mResolver,
                Settings.System.STATUS_BAR_COLLAPSE_ON_DISMISS,
                Settings.System.STATUS_BAR_COLLAPSE_IF_NO_CLEARABLE);
        mCollapseOnDismiss.setValue(String.valueOf(collapseBehaviour));
        updateCollapseBehaviourSummary(collapseBehaviour);
        mCollapseOnDismiss.setOnPreferenceChangeListener(this);

        mEnableQuickAccess = (CheckBoxPreference) findPreference(PREF_NOTIFICATION_DRAWER_ENABLE_QUICK_ACCESS);
        mEnableQuickAccess.setChecked(isQuickAccessEnabled);
        mEnableQuickAccess.setOnPreferenceChangeListener(this);

        // Remove uneeded preferences depending on enabled states
        if (!isQuickAccessEnabled) {
            removePreference("notification_drawer_quick_access_settings");
        }
    }

    private void updateCollapseBehaviourSummary(int setting) {
        String[] summaries = getResources().getStringArray(
                R.array.notification_drawer_collapse_on_dismiss_summaries);
        mCollapseOnDismiss.setSummary(summaries[setting]);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        if (preference == mCollapseOnDismiss) {
            int value = Integer.valueOf((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.STATUS_BAR_COLLAPSE_ON_DISMISS, value);
            updateCollapseBehaviourSummary(value);
            return true;
        } else if (preference == mEnableQuickAccess) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.QS_QUICK_ACCESS, value ? 1 : 0);
            refreshSettings();
            return true;
        }
        return false;
    }
}
