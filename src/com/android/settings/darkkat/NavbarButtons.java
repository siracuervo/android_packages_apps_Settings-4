/*
 * Copyright (C) 2012 Slimroms
 *
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class NavbarButtons extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String TAG = "NavbarButtons";
    private static final String PREF_NAV_BUTTON_COLOR_CAT =
            "navbar_button_color_cat";
    private static final String PREF_NAV_BUTTON_MENU_CAT =
            "navbar_button_menu_cat";
    private static final String PREF_NAV_BUTTON_COLOR =
            "nav_button_color";
    private static final String PREF_NAV_BUTTON_COLOR_MODE =
            "nav_button_color_mode";
    private static final String PREF_NAV_GLOW_COLOR =
            "nav_button_glow_color";
    private static final String PREF_MENU_LOCATION =
            "pref_navbar_menu_location";
    private static final String PREF_NAVBAR_MENU_DISPLAY =
            "pref_navbar_menu_display";

    private static int DEFAULT_BUTTON_COLOR;
    private static int DEFAULT_BUTTON_GLOW_COLOR;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    ColorPickerPreference mNavigationBarButtonColor;
    ColorPickerPreference mNavigationBarGlowColor;
    ListPreference mNavigationBarButtonColorMode;
    ListPreference mMenuDisplayLocation;
    ListPreference mNavBarMenuDisplay;

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

        addPreferencesFromResource(R.xml.navbar_buttons);

        mResolver = getActivity().getContentResolver();

        PackageManager pm = getPackageManager();
        Resources systemUiResources = null;
        try {
            systemUiResources = pm.getResourcesForApplication("com.android.systemui");
        } catch (Exception e) {
            Log.e(TAG, "can't access systemui resources",e);
        }

        DEFAULT_BUTTON_COLOR = systemUiResources.getColor(systemUiResources.getIdentifier(
                "com.android.systemui:color/navigationbar_button_default_color", null, null));
        DEFAULT_BUTTON_GLOW_COLOR = systemUiResources.getColor(systemUiResources.getIdentifier(
                "com.android.systemui:color/navigationbar_button_glow_default_color", null, null));

        int intColor;
        int intDefaultColorColor;
        String hexColor;

        int navigationBarButtonColorMode = Settings.System.getInt(getContentResolver(),
                Settings.System.NAVIGATION_BAR_BUTTON_TINT_MODE, 0);
        PreferenceCategory navbarButtonColorCat =
                (PreferenceCategory) findPreference(PREF_NAV_BUTTON_COLOR_CAT);
        mNavigationBarButtonColor =
                (ColorPickerPreference) findPreference(PREF_NAV_BUTTON_COLOR);
        if (navigationBarButtonColorMode == 3) {
            navbarButtonColorCat.removePreference(mNavigationBarButtonColor);
        } else {
            mNavigationBarButtonColor.setOnPreferenceChangeListener(this);
            intColor = Settings.System.getInt(getContentResolver(),
                        Settings.System.NAVIGATION_BAR_BUTTON_TINT,
                        DEFAULT_BUTTON_COLOR);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mNavigationBarButtonColor.setSummary(hexColor);
            mNavigationBarButtonColor.setNewPreviewColor(intColor);
        }

        mNavigationBarGlowColor =
                (ColorPickerPreference) findPreference(PREF_NAV_GLOW_COLOR);
        mNavigationBarGlowColor.setOnPreferenceChangeListener(this);
        intColor = Settings.System.getInt(getContentResolver(),
                    Settings.System.NAVIGATION_BAR_GLOW_TINT,
                    DEFAULT_BUTTON_GLOW_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mNavigationBarGlowColor.setSummary(hexColor);
        mNavigationBarGlowColor.setNewPreviewColor(intColor);

        mNavigationBarButtonColorMode =
            (ListPreference) findPreference(PREF_NAV_BUTTON_COLOR_MODE);
        mNavigationBarButtonColorMode.setValue(String.valueOf(navigationBarButtonColorMode));
        mNavigationBarButtonColorMode.setSummary(mNavigationBarButtonColorMode.getEntry());
        mNavigationBarButtonColorMode.setOnPreferenceChangeListener(this);

        mNavBarMenuDisplay =
                (ListPreference) findPreference(PREF_NAVBAR_MENU_DISPLAY);
        int navBarMenuDisplay = Settings.System.getInt(mResolver,
                Settings.System.MENU_VISIBILITY, 2);
        mNavBarMenuDisplay.setValue(String.valueOf(navBarMenuDisplay));
        mNavBarMenuDisplay.setSummary(mNavBarMenuDisplay.getEntry());
        mNavBarMenuDisplay.setOnPreferenceChangeListener(this);

        PreferenceCategory navbarButtonMenuCat =
                (PreferenceCategory) findPreference(PREF_NAV_BUTTON_MENU_CAT);
        mMenuDisplayLocation = (ListPreference) findPreference(PREF_MENU_LOCATION);
        if (navBarMenuDisplay == 1) {
            navbarButtonMenuCat.removePreference(mMenuDisplayLocation);
        } else {
            int menuDisplayLocation = Settings.System.getInt(getContentResolver(),
                Settings.System.MENU_LOCATION, 0);
            mMenuDisplayLocation.setValue(String.valueOf(menuDisplayLocation));
            mMenuDisplayLocation.setSummary(mMenuDisplayLocation.getEntry());
            mMenuDisplayLocation.setOnPreferenceChangeListener(this);
        }

        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(R.drawable.ic_settings_backup) // use the backup icon
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                showDialogInner(DLG_RESET);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String hex;
        int intHex;
        int index;
        int value;

        if (preference == mNavigationBarButtonColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_BUTTON_TINT, intHex);
            return true;
        } else if (preference == mNavigationBarGlowColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_GLOW_TINT, intHex);
            return true;
        } else if (preference == mNavigationBarButtonColorMode) {
            index = mNavigationBarButtonColorMode.findIndexOfValue((String) newValue);
            value = Integer.valueOf((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.NAVIGATION_BAR_BUTTON_TINT_MODE, value);
            mNavigationBarButtonColorMode.setSummary(
                mNavigationBarButtonColorMode.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mNavBarMenuDisplay) {
            index = mNavBarMenuDisplay.findIndexOfValue((String) newValue);
            value = Integer.valueOf((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.MENU_VISIBILITY, value);
            mNavBarMenuDisplay.setSummary(
                mNavBarMenuDisplay.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mMenuDisplayLocation) {
            index = mMenuDisplayLocation.findIndexOfValue((String) newValue);
            value = Integer.valueOf((String) newValue);
            Settings.System.putInt(mResolver,
                    Settings.System.MENU_LOCATION, value);
            mMenuDisplayLocation.setSummary(
                mMenuDisplayLocation.getEntries()[index]);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void showDialogInner(int id) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            frag.setArguments(args);
            return frag;
        }

        NavbarButtons getOwner() {
            return (NavbarButtons) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_reset_values_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.dlg_reset_android,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.NAVIGATION_BAR_BUTTON_TINT,
                                    DEFAULT_BUTTON_GLOW_COLOR);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.NAVIGATION_BAR_GLOW_TINT,
                                    DEFAULT_BUTTON_GLOW_COLOR);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                   Settings.System.NAVIGATION_BAR_BUTTON_TINT_MODE, 0);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.NAVIGATION_BAR_BUTTON_TINT, 0xff33b5e5);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                    Settings.System.NAVIGATION_BAR_GLOW_TINT, 0xff33b5e5);
                            Settings.System.putInt(getActivity().getContentResolver(),
                                   Settings.System.NAVIGATION_BAR_BUTTON_TINT_MODE, 0);
                            getOwner().refreshSettings();
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
