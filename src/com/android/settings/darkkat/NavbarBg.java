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
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
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

public class NavbarBg extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String PREF_OPAQUE_COLOR =
            "navbar_opaque_color";
    private static final String PREF_SEMI_TRANS_COLOR =
            "navbar_semi_trans_color";
    private static final String PREF_GRADIENT_COLOR =
            "navbar_gradient_color";

    private static final int DEFAULT_OPAQUE_COLOR =
            0xff000000;
    private static final int DEFAULT_SEMI_TRANS_COLOR =
            0x66000000;
    private static final int DEFAULT_GRADIENT_COLOR =
            0xff000000;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private ColorPickerPreference mOpaqueColor;
    private ColorPickerPreference mSemiTransColor;
    private ColorPickerPreference mGradientColor;

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

        addPreferencesFromResource(R.xml.navbar_bg);
        mResolver = getActivity().getContentResolver();

        int color;
        String hexColor;

        mOpaqueColor =
                (ColorPickerPreference) findPreference(PREF_OPAQUE_COLOR);
        color = Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_OPAQUE_COLOR,
                DEFAULT_OPAQUE_COLOR);
        mOpaqueColor.setNewPreviewColor(color);
        hexColor = String.format("#%08x", (0xffffffff & color));
        mOpaqueColor.setSummary(hexColor);
        mOpaqueColor.setOnPreferenceChangeListener(this);

        mSemiTransColor =
                (ColorPickerPreference) findPreference(PREF_SEMI_TRANS_COLOR);
        color = Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_SEMI_TRANS_COLOR,
                DEFAULT_SEMI_TRANS_COLOR);
        mSemiTransColor.setNewPreviewColor(color);
        hexColor = String.format("#%08x", (0xffffffff & color));
        mSemiTransColor.setSummary(hexColor);
        mSemiTransColor.setOnPreferenceChangeListener(this);

        mGradientColor =
                (ColorPickerPreference) findPreference(PREF_GRADIENT_COLOR);
        color = Settings.System.getInt(mResolver,
                Settings.System.NAVIGATION_BAR_GRADIENT_COLOR,
                DEFAULT_GRADIENT_COLOR);
        mGradientColor.setNewPreviewColor(color);
        hexColor = String.format("#%08x", (0xffffffff & color));
        mGradientColor.setSummary(hexColor);
        mGradientColor.setAlphaSliderEnabled(true);
        mGradientColor.setOnPreferenceChangeListener(this);

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

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String hex;
        String fixedHex;
        int intHex;
        int fixedColor;

        if (preference == mOpaqueColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            fixedColor = getFixedColor(intHex, preference, true);
            Settings.System.putInt(mResolver,
                Settings.System.NAVIGATION_BAR_OPAQUE_COLOR, fixedColor);
            fixedHex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(fixedColor)));
            preference.setSummary(fixedHex);
            return true;
        } else if (preference == mSemiTransColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            fixedColor = getFixedColor(intHex, preference, false);
            Settings.System.putInt(mResolver,
                Settings.System.NAVIGATION_BAR_SEMI_TRANS_COLOR, fixedColor);
            fixedHex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(fixedColor)));
            preference.setSummary(fixedHex);
            return true;
        } else if (preference == mGradientColor) {
            hex = ColorPickerPreference.convertToARGB(
                Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.NAVIGATION_BAR_GRADIENT_COLOR, intHex);
            preference.setSummary(hex);
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

        NavbarBg getOwner() {
            return (NavbarBg) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.dlg_reset_color_values_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.dlg_reset_android,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.NAVIGATION_BAR_OPAQUE_COLOR,
                                DEFAULT_OPAQUE_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.NAVIGATION_BAR_SEMI_TRANS_COLOR,
                                DEFAULT_SEMI_TRANS_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.NAVIGATION_BAR_GRADIENT_COLOR,
                                DEFAULT_GRADIENT_COLOR);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.NAVIGATION_BAR_OPAQUE_COLOR,
                                DEFAULT_OPAQUE_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.NAVIGATION_BAR_SEMI_TRANS_COLOR,
                                DEFAULT_SEMI_TRANS_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.NAVIGATION_BAR_GRADIENT_COLOR,
                                0x6633b5e5);
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

    private int getFixedColor(int color, Preference preference, boolean useOpaqueColor) {
        int currentColor = color;
        int fixedColor;
        int currentAlpha = Color.alpha(currentColor);
        int defaultAlpha;

        if (useOpaqueColor) {
            // The opaque color should be always 100% opaque
            // so check the current opacity, and change it to 100% if needed.
            defaultAlpha = 255;
        } else {
            // The semi transparent color has a default transparency of 40%,
            // in my opinion, it makes no sense to change the default transparency at all,
            // so check the current transparency, and change it to 40% if needed.
            defaultAlpha = 102;
        }
        if (currentAlpha != defaultAlpha) {
            int r = Color.red(currentColor);
            int g = Color.green(currentColor);
            int b = Color.blue(currentColor);

            fixedColor = (defaultAlpha << 24) + (r << 16) + (g << 8) + b;
            ((ColorPickerPreference)preference).setNewPreviewColor(fixedColor);
        } else {
            fixedColor = currentColor;
        }
        return fixedColor;
    }
}
