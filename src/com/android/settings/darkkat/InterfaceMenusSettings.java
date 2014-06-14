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
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.darkkat.util.CMDProcessor;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.widget.SeekBarPreference;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class InterfaceMenusSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_POWER_MENU_CAT_COLORS =
            "power_menu_cat_colors";
    private static final String PREF_POWER_MENU_ICON_COLOR_MODE =
            "power_menu_icon_color_mode";
    private static final String PREF_POWER_MENU_ICON_COLOR =
            "power_menu_icon_color";
    private static final String PREF_POWER_MENU_TEXT_COLOR =
            "power_menu_text_color";
    private static final String PREF_RECENT_APPS_TYPE =
            "recent_apps_type";
    private static final String PREF_RECENTS_PANEL_SCALE =
            "recents_panel_scale";
    private static final String PREF_RECENTS_PANEL_EXPANDED_MODE =
            "recents_panel_expanded_mode";
    private static final String PREF_RECENTS_PANEL_LEFTY_MODE =
            "recents_panel_lefty_mode";
    private static final String PREF_RECENTS_PANEL_BG_COLOR =
            "recents_panel_bg_color";
    private static final String PREF_RECENTS_PANEL_EMPTY_ICON_COLOR =
            "recents_panel_empty_icon_color";
    private static final String PREF_RECENTS_RAM_BAR =
            "recents_ram_bar";
    private static final String PREF_RECENTS_CLEAR_ALL_BTN_POS =
            "recents_clear_all_button_position";
    private static final String PREF_RECENTS_BG_COLOR =
            "recents_bg_color";
    private static final String PREF_RECENTS_CLEAR_ALL_BTN_COLOR =
            "recents_clear_all_button_color";

    private static final int DEFAULT_POWER_MENU_ICON_COLOR =
            0xffffffff;
    private static final int DEFAULT_POWER_MENU_TEXT_COLOR =
            0xffffffff;
    private static final int DEFAULT_RECENTS_PANEL_BG_COLOR =
            0xe6000000;
    private static final int DEFAULT_RECENTS_PANEL_EMPTY_ICON_COLOR =
            0xffcdcdcd;
    private static final int DEFAULT_RECENTS_BG_COLOR =
            0xe0000000;
    private static final int DEFAULT_RECENTS_CLEAR_ALL_BTN_COLOR =
            0xffffffff;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private ListPreference mPowerMenuIconColorMode;
    private ColorPickerPreference mPowerMenuIconColor;
    private ColorPickerPreference mPowerMenuTextColor;
    private CheckBoxPreference mRecentAppsType;
    private ListPreference mRecentsPanelScale;
    private ListPreference mRecentsPanelExpandedMode;
    private CheckBoxPreference mRecentsPanelLeftyMode;
    private ColorPickerPreference mRecentsPanelBgColor;
    private ColorPickerPreference mRecentsPanelIconColor;
    private Preference mRamBar;
    private ListPreference mClearAllBtnPosition;
    private ColorPickerPreference mRecentsBgColor;
    private ColorPickerPreference mRecentsClearAllBtnColor;

    private ContentResolver mResolver;

    private boolean mSlimRecents;

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

        int intColor;
        String hexColor;

        addPreferencesFromResource(R.xml.interface_menus_settings);

        mResolver = getActivity().getContentResolver();

        mSlimRecents = Settings.System.getInt(mResolver,
                Settings.System.RECENT_APPS_TYPE, 1) == 1;

        mPowerMenuIconColorMode =
            (ListPreference) findPreference(PREF_POWER_MENU_ICON_COLOR_MODE);
        int powerMenuIconColorMode = Settings.System.getInt(getContentResolver(),
                Settings.System.POWER_MENU_ICON_COLOR_MODE, 0);
        mPowerMenuIconColorMode.setValue(String.valueOf(powerMenuIconColorMode));
        mPowerMenuIconColorMode.setSummary(mPowerMenuIconColorMode.getEntry());
        mPowerMenuIconColorMode.setOnPreferenceChangeListener(this);

        PreferenceCategory powerMenuCatColors =
                (PreferenceCategory) findPreference(PREF_POWER_MENU_CAT_COLORS);
        mPowerMenuIconColor =
                (ColorPickerPreference) findPreference(PREF_POWER_MENU_ICON_COLOR);
        // Remove color preferences if color mode is set do disabled
        if (powerMenuIconColorMode != 3) {
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.POWER_MENU_ICON_COLOR,
                    DEFAULT_POWER_MENU_ICON_COLOR);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mPowerMenuIconColor.setSummary(hexColor);
            mPowerMenuIconColor.setNewPreviewColor(intColor);
            mPowerMenuIconColor.setOnPreferenceChangeListener(this);
        } else {
            powerMenuCatColors.removePreference(mPowerMenuIconColor);
        }

        mPowerMenuTextColor =
                (ColorPickerPreference) findPreference(PREF_POWER_MENU_TEXT_COLOR);
        intColor = Settings.System.getInt(mResolver,
                Settings.System.POWER_MENU_TEXT_COLOR, DEFAULT_POWER_MENU_TEXT_COLOR);
        hexColor = String.format("#%08x", (0xffffffff & intColor));
        mPowerMenuTextColor.setSummary(hexColor);
        mPowerMenuTextColor.setNewPreviewColor(intColor);
        mPowerMenuTextColor.setOnPreferenceChangeListener(this);

        if (mSlimRecents) {
            // Slim recents enabled, append needed settings
            addPreferencesFromResource(R.xml.recents_slim_settings);

            mRecentsPanelScale =
                    (ListPreference) findPreference(PREF_RECENTS_PANEL_SCALE);
            int recentsScale = Settings.System.getInt(getContentResolver(),
                    Settings.System.RECENT_PANEL_SCALE_FACTOR, 100);
            mRecentsPanelScale.setValue(recentsScale + "");
            mRecentsPanelScale.setSummary(mRecentsPanelScale.getEntry());
            mRecentsPanelScale.setOnPreferenceChangeListener(this);

            mRecentsPanelExpandedMode =
                    (ListPreference) findPreference(PREF_RECENTS_PANEL_EXPANDED_MODE);
            int recentsExpandedMode = Settings.System.getInt(getContentResolver(),
                    Settings.System.RECENT_PANEL_EXPANDED_MODE, 0);
            mRecentsPanelExpandedMode.setValue(recentsExpandedMode + "");
            mRecentsPanelExpandedMode.setSummary(mRecentsPanelExpandedMode.getEntry());
            mRecentsPanelExpandedMode.setOnPreferenceChangeListener(this);

            mRecentsPanelLeftyMode =
                    (CheckBoxPreference) findPreference(PREF_RECENTS_PANEL_LEFTY_MODE);
            boolean recentsLeftyMode = Settings.System.getInt(getContentResolver(),
                    Settings.System.RECENT_PANEL_GRAVITY, Gravity.RIGHT) == Gravity.LEFT;
            mRecentsPanelLeftyMode.setChecked(recentsLeftyMode);
            mRecentsPanelLeftyMode.setOnPreferenceChangeListener(this);

            mRecentsPanelBgColor =
                    (ColorPickerPreference) findPreference(PREF_RECENTS_PANEL_BG_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.RECENT_PANEL_BG_COLOR,
                    DEFAULT_RECENTS_PANEL_BG_COLOR);
            mRecentsPanelBgColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mRecentsPanelBgColor.setSummary(hexColor);
            mRecentsPanelBgColor.setAlphaSliderEnabled(true);
            mRecentsPanelBgColor.setOnPreferenceChangeListener(this);

            mRecentsPanelIconColor =
                    (ColorPickerPreference) findPreference(PREF_RECENTS_PANEL_EMPTY_ICON_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.RECENT_PANEL_EMPTY_ICON_COLOR,
                    DEFAULT_RECENTS_PANEL_EMPTY_ICON_COLOR);
            mRecentsPanelIconColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mRecentsPanelIconColor.setSummary(hexColor);
            mRecentsPanelIconColor.setOnPreferenceChangeListener(this);
        } else {
            // Default recents enabled, append needed settings
            addPreferencesFromResource(R.xml.recents_default_settings);

            mRamBar = findPreference(PREF_RECENTS_RAM_BAR);

            mClearAllBtnPosition =
                    (ListPreference) findPreference(PREF_RECENTS_CLEAR_ALL_BTN_POS);
            int clearAllBtnPosition = Settings.System.getInt(mResolver,
                    Settings.System.RECENTS_CLEAR_ALL_BTN_POS, 2);
            mClearAllBtnPosition.setValue(String.valueOf(clearAllBtnPosition));
            mClearAllBtnPosition.setSummary(mClearAllBtnPosition.getEntry());
            mClearAllBtnPosition.setOnPreferenceChangeListener(this);

            mRecentsBgColor =
                    (ColorPickerPreference) findPreference(PREF_RECENTS_BG_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.RECENT_BG_COLOR,
                    DEFAULT_RECENTS_BG_COLOR);
            mRecentsBgColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mRecentsBgColor.setSummary(hexColor);
            mRecentsBgColor.setAlphaSliderEnabled(true);
            mRecentsBgColor.setOnPreferenceChangeListener(this);

            mRecentsClearAllBtnColor =
                    (ColorPickerPreference) findPreference(PREF_RECENTS_CLEAR_ALL_BTN_COLOR);
            intColor = Settings.System.getInt(mResolver,
                    Settings.System.RECENT_CLEAR_ALL_BTN_COLOR,
                    DEFAULT_RECENTS_CLEAR_ALL_BTN_COLOR);
            mRecentsClearAllBtnColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mRecentsClearAllBtnColor.setSummary(hexColor);
            mRecentsClearAllBtnColor.setOnPreferenceChangeListener(this);

            updateRamBar();
        }

        mRecentAppsType = (CheckBoxPreference) findPreference(PREF_RECENT_APPS_TYPE);
        mRecentAppsType.setChecked(Settings.System.getInt(mResolver,
                Settings.System.RECENT_APPS_TYPE, 1) == 1);
        mRecentAppsType.setOnPreferenceChangeListener(this);

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
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        String hex;
        int intHex;
        int index;
        int value;

        if (preference == mPowerMenuIconColorMode) {
            index = mPowerMenuIconColorMode.findIndexOfValue((String) objValue);
            value = Integer.valueOf((String) objValue);
            Settings.System.putInt(mResolver,
                    Settings.System.POWER_MENU_ICON_COLOR_MODE, value);
            mPowerMenuIconColorMode.setSummary(
                mPowerMenuIconColorMode.getEntries()[index]);
            refreshSettings();
            return true;
        } else if (preference == mPowerMenuIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_ICON_COLOR,
                    intHex);
            return true;
        } else if (preference == mPowerMenuTextColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.POWER_MENU_TEXT_COLOR,
                    intHex);
            return true;
        } else if (preference == mRecentAppsType) {
            Settings.System.putInt(mResolver,
                    Settings.System.RECENT_APPS_TYPE,
                    ((Boolean) objValue) ? 1 : 0);
            restartSystemUI();
            refreshSettings();
            return true;
        } else if (preference == mRecentsPanelScale) {
            index =  mRecentsPanelScale.findIndexOfValue((String) objValue);
            value = Integer.parseInt((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.RECENT_PANEL_SCALE_FACTOR, value);
            mRecentsPanelScale.setSummary(
                mRecentsPanelScale.getEntries()[index]);
            return true;
        } else if (preference == mRecentsPanelExpandedMode) {
            index = mRecentsPanelExpandedMode.findIndexOfValue((String) objValue);
            value = Integer.parseInt((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.RECENT_PANEL_EXPANDED_MODE, value);
            mRecentsPanelExpandedMode.setSummary(
                mRecentsPanelExpandedMode.getEntries()[index]);
            return true;
        } else if (preference == mRecentsPanelLeftyMode) {
            Settings.System.putInt(getContentResolver(),
                    Settings.System.RECENT_PANEL_GRAVITY,
                    ((Boolean) objValue) ? Gravity.LEFT : Gravity.RIGHT);
            return true;
        } else if (preference == mRecentsPanelBgColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.RECENT_PANEL_BG_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mRecentsPanelIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.RECENT_PANEL_EMPTY_ICON_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mClearAllBtnPosition) {
            index = mClearAllBtnPosition.findIndexOfValue((String) objValue);
            value = Integer.parseInt((String) objValue);
            Settings.System.putInt(mResolver,
                    Settings.System.RECENTS_CLEAR_ALL_BTN_POS, value);
            mClearAllBtnPosition.setSummary(mClearAllBtnPosition.getEntries()[index]);
            return true;
        } else if (preference == mRecentsBgColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.RECENT_BG_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mRecentsClearAllBtnColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.RECENT_CLEAR_ALL_BTN_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        }

        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mSlimRecents) {
            updateRamBar();
        }
    }
 
    @Override
    public void onPause() {
        super.onResume();
        if (!mSlimRecents) {
            updateRamBar();
        }
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

        InterfaceMenusSettings getOwner() {
            return (InterfaceMenusSettings) getTargetFragment();
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
                                    Settings.System.POWER_MENU_ICON_COLOR_MODE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_ICON_COLOR,
                                    DEFAULT_POWER_MENU_ICON_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_TEXT_COLOR,
                                    DEFAULT_POWER_MENU_TEXT_COLOR);
                            if (getOwner().mSlimRecents) {
                                Settings.System.putInt(getOwner().mResolver,
                                        Settings.System.RECENT_PANEL_BG_COLOR,
                                        DEFAULT_RECENTS_PANEL_BG_COLOR);
                                Settings.System.putInt(getOwner().mResolver,
                                        Settings.System.RECENT_PANEL_EMPTY_ICON_COLOR,
                                        DEFAULT_RECENTS_PANEL_EMPTY_ICON_COLOR);
                            } else {
                                Settings.System.putInt(getOwner().mResolver,
                                        Settings.System.RECENT_BG_COLOR,
                                        DEFAULT_RECENTS_BG_COLOR);
                                Settings.System.putInt(getOwner().mResolver,
                                        Settings.System.RECENT_CLEAR_ALL_BTN_COLOR,
                                        DEFAULT_RECENTS_CLEAR_ALL_BTN_COLOR);
                            }
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_ICON_COLOR_MODE, 0);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_ICON_COLOR,
                                    0xff33b5e5);
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.POWER_MENU_TEXT_COLOR,
                                    DEFAULT_POWER_MENU_TEXT_COLOR);
                            if (getOwner().mSlimRecents) {
                                Settings.System.putInt(getOwner().mResolver,
                                        Settings.System.RECENT_PANEL_BG_COLOR,
                                        DEFAULT_RECENTS_PANEL_BG_COLOR);
                                Settings.System.putInt(getOwner().mResolver,
                                        Settings.System.RECENT_PANEL_EMPTY_ICON_COLOR,
                                        DEFAULT_RECENTS_PANEL_EMPTY_ICON_COLOR);
                            } else {
                                Settings.System.putInt(getOwner().mResolver,
                                        Settings.System.RECENT_BG_COLOR,
                                        DEFAULT_RECENTS_BG_COLOR);
                                Settings.System.putInt(getOwner().mResolver,
                                        Settings.System.RECENT_CLEAR_ALL_BTN_COLOR,
                                        0xff33b5e5);
                            }
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

    private void updateRamBar() {
        int ramBarMode = Settings.System.getInt(mResolver,
                Settings.System.RECENTS_RAM_BAR_MODE, 0);
        if (ramBarMode != 0)
            mRamBar.setSummary(getResources().getString(R.string.enabled));
        else
            mRamBar.setSummary(getResources().getString(R.string.disabled));
    }

    public static void restartSystemUI() {
        CMDProcessor.startSuCommand("pkill -TERM -f com.android.systemui");
    }
}
