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

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class StyleSettings extends SettingsPreferenceFragment {

    private static final String KEY_NAVIGATION_BAR_STYLE = "navigation_bar_style";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.style_settings);
        PreferenceScreen prefScreen = getPreferenceScreen();

        boolean removeKeys = false;
        boolean removeNavbar = false;

        IWindowManager windowManager = IWindowManager.Stub.asInterface(
                ServiceManager.getService(Context.WINDOW_SERVICE));
        try {
            if (windowManager.hasNavigationBar()) {
                removeKeys = true;
            } else {
                removeNavbar = true;
            }
        } catch (RemoteException e) {
            // Do nothing
        }

        // Remove the Navigationbar Style Preference on a device that does not have a navbar
        if (removeNavbar) {
            prefScreen.removePreference(findPreference(KEY_NAVIGATION_BAR_STYLE));
        }
    }
}
