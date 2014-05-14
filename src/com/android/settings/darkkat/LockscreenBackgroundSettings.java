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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SeekBarPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class LockscreenBackgroundSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_CAT_OPTIONS =
            "lockscreen_bg_cat_options";
    private static final String PREF_CAT_COLORS =
            "lockscreen_bg_cat_colors";
    private static final String PREF_LOCKSCREEN_SEE_THROUGH =
            "lockscreen_see_through";
    private static final String PREF_LOCKSCREEN_CUSTOM_IMAGE =
            "lockscreen_custom_image";
    private static final String PREF_LOCKSCREEN_BLUR_RADIUS =
            "lockscreen_blur_radius";
    private static final String PREF_LOCKSCREEN_COLORIZE_BACKGROUND =
            "lockscreen_colorize_background";
    private static final String PREF_LOCKSCREEN_BACKGROUND_COLOR =
            "lockscreen_background_color";

    private static final int DEFAULT_BACKGROUND_COLOR =
            0x70000000;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private CheckBoxPreference mSeeThrough;
    private Preference mCustomImage;
    private SeekBarPreference mBlurRadius;
    private CheckBoxPreference mColorize;
    private ColorPickerPreference mBackgroundColor;

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

        addPreferencesFromResource(R.xml.lockscreen_background_settings);

        mResolver = getActivity().getContentResolver();

        boolean seeThroughEnabled = Settings.System.getInt(mResolver,
               Settings.System.LOCKSCREEN_SEE_THROUGH, 0) == 1;
        mSeeThrough =
                (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_SEE_THROUGH);
        mSeeThrough.setChecked(seeThroughEnabled);
        mSeeThrough.setOnPreferenceChangeListener(this);

        // Handle background setting depending on enabled states
        PreferenceCategory catOptions =
                (PreferenceCategory) findPreference(PREF_CAT_OPTIONS);
        mCustomImage =
                (Preference) findPreference(PREF_LOCKSCREEN_CUSTOM_IMAGE);
        mBlurRadius =
                (SeekBarPreference) findPreference(PREF_LOCKSCREEN_BLUR_RADIUS);
        if (seeThroughEnabled) {
            mCustomImage.setEnabled(false);
            mCustomImage.setSummary(getResources().getString(R.string.lockscreen_custom_image_disabled));

            mBlurRadius.setInitValue(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_BLUR_RADIUS, 48));
            mBlurRadius.setProperty(String.valueOf(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_BLUR_RADIUS, 48) / 100));
            mBlurRadius.setOnPreferenceChangeListener(this);
        } else {
            mCustomImage.setEnabled(true);

            catOptions.removePreference(mBlurRadius);
        }

        boolean colorize = Settings.System.getInt(mResolver,
               Settings.System.LOCKSCREEN_COLORIZE_BACKGROUND, 1) == 1;
        mColorize =
                (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_COLORIZE_BACKGROUND);
        mColorize.setChecked(colorize);
        mColorize.setOnPreferenceChangeListener(this);

        // Remove the background color preference if colorize is disabled
        PreferenceCategory catColors =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);
        mBackgroundColor =
                (ColorPickerPreference) findPreference(PREF_LOCKSCREEN_BACKGROUND_COLOR);
        if (colorize) {
            int color = Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_BACKGROUND_COLOR, DEFAULT_BACKGROUND_COLOR);
            mBackgroundColor.setNewPreviewColor(color);
            String hexColor = String.format("#%08x", (0xffffffff & color));
            mBackgroundColor.setSummary(hexColor);
            mBackgroundColor.setAlphaSliderEnabled(true);
            mBackgroundColor.setOnPreferenceChangeListener(this);
        } else {
            catColors.removePreference(mBackgroundColor);
            removePreference(PREF_CAT_COLORS);
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
                showDialogInner(DLG_RESET, true);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {

        if (preference == mSeeThrough) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_SEE_THROUGH, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mBlurRadius) {
            int value = Integer.parseInt((String) objValue);
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_BLUR_RADIUS, value);
            return true;
        } else if (preference == mColorize) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_COLORIZE_BACKGROUND, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mBackgroundColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.LOCKSCREEN_BACKGROUND_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }
        return false;
    }

    private void showDialogInner(int id, boolean state) {
        DialogFragment newFragment = MyAlertDialogFragment.newInstance(id, state);
        newFragment.setTargetFragment(this, 0);
        newFragment.show(getFragmentManager(), "dialog " + id);
    }

    public static class MyAlertDialogFragment extends DialogFragment {

        public static MyAlertDialogFragment newInstance(int id, boolean state) {
            MyAlertDialogFragment frag = new MyAlertDialogFragment();
            Bundle args = new Bundle();
            args.putInt("id", id);
            args.putBoolean("state", state);
            frag.setArguments(args);
            return frag;
        }

        LockscreenBackgroundSettings getOwner() {
            return (LockscreenBackgroundSettings) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            final boolean state = getArguments().getBoolean("state");
            switch (id) {
                case DLG_RESET:
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.reset)
                    .setMessage(R.string.reset_message)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.dlg_ok,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_SEE_THROUGH, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_COLORIZE_BACKGROUND, 1);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_BACKGROUND_COLOR,
                                DEFAULT_BACKGROUND_COLOR);
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
