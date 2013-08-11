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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.darkjelly.colorpicker.ColorPickerPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SeekBarPreference;

public class PieControlColor extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "PieControlColor";

    private static final String PREF_ENABLE_THEME_DEFAULT = "enable_theme_default";
    private static final String PREF_DISABLE_ICON_OVERLAY = "disable_icon_overlay";
    private static final String PREF_PIE_SNAP_COLOR = "pie_snap_color";
    private static final String PREF_PIE_OVERLAY_COLOR = "pie_overlay_color";
    private static final String PREF_PIE_TEXT_COLOR = "pie_text_color";
    private static final String PREF_BUTTON_BG_NORMAL_COLOR = "button_background_normal_color";
    private static final String PREF_BUTTON_BG_SELECTED_COLOR = "button_background_selected_color";
    private static final String PREF_BUTTON_BG_LONG_PRESSED_COLOR = "button_background_long_pressed_color";
    private static final String PREF_BUTTON_OUTLINE_COLOR = "button_outline_color";
    private static final String PREF_BUTTON_ICON_COLOR = "button_icon_color";

    private CheckBoxPreference mEnableThemeDefault;
    private CheckBoxPreference mDisableIconOverlay;
    private ColorPickerPreference mPieSnapColor;
    private ColorPickerPreference mPieOverlayColor;
    private ColorPickerPreference mPieTextColor;
    private ColorPickerPreference mButtonBgNormalColor;
    private ColorPickerPreference mButtonBgSelectedColor;
    private ColorPickerPreference mButtonBgLongPressedColor;
    private ColorPickerPreference mButtonOutlineColor;
    private ColorPickerPreference mButtonIconColor;

    Resources mSystemUiResources;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pie_control_color);

        PackageManager pm = getActivity().getPackageManager();

        if (pm != null) {
            try {
                mSystemUiResources = pm.getResourcesForApplication("com.android.systemui");
            } catch (Exception e) {
                mSystemUiResources = null;
                Log.e("PieControlColor:", "can't access systemui resources",e);
            }
        }

        mEnableThemeDefault = (CheckBoxPreference) findPreference(PREF_ENABLE_THEME_DEFAULT);
        mEnableThemeDefault.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.PIE_ENABLE_THEME_DEFAULT, 1) == 1);

        mDisableIconOverlay = (CheckBoxPreference) findPreference(PREF_DISABLE_ICON_OVERLAY);
        mDisableIconOverlay.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.PIE_DISABLE_ICON_OVERLAY, 1) == 1);

        mPieSnapColor = (ColorPickerPreference) findPreference(PREF_PIE_SNAP_COLOR);
        mPieSnapColor.setOnPreferenceChangeListener(this);
        mPieSnapColor.setAlphaSliderEnabled(true);

        mPieOverlayColor = (ColorPickerPreference) findPreference(PREF_PIE_OVERLAY_COLOR);
        mPieOverlayColor.setOnPreferenceChangeListener(this);
        mPieOverlayColor.setAlphaSliderEnabled(true);

        mPieTextColor = (ColorPickerPreference) findPreference(PREF_PIE_TEXT_COLOR);
        mPieTextColor.setOnPreferenceChangeListener(this);
        mPieTextColor.setAlphaSliderEnabled(true);

        mButtonBgNormalColor = (ColorPickerPreference) findPreference(PREF_BUTTON_BG_NORMAL_COLOR);
        mButtonBgNormalColor.setOnPreferenceChangeListener(this);
        mButtonBgNormalColor.setAlphaSliderEnabled(true);

        mButtonBgSelectedColor = (ColorPickerPreference) findPreference(PREF_BUTTON_BG_SELECTED_COLOR);
        mButtonBgSelectedColor.setOnPreferenceChangeListener(this);
        mButtonBgSelectedColor.setAlphaSliderEnabled(true);

        mButtonBgLongPressedColor = (ColorPickerPreference) findPreference(PREF_BUTTON_BG_LONG_PRESSED_COLOR);
        mButtonBgLongPressedColor.setOnPreferenceChangeListener(this);
        mButtonBgLongPressedColor.setAlphaSliderEnabled(true);

        mButtonOutlineColor = (ColorPickerPreference) findPreference(PREF_BUTTON_OUTLINE_COLOR);
        mButtonOutlineColor.setOnPreferenceChangeListener(this);
        mButtonOutlineColor.setAlphaSliderEnabled(true);

        mButtonIconColor = (ColorPickerPreference) findPreference(PREF_BUTTON_ICON_COLOR);
        mButtonIconColor.setOnPreferenceChangeListener(this);
        mButtonIconColor.setAlphaSliderEnabled(true);

        updatePreferences();
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.pie_control_color, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pie_control_cm_default:
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.PIE_SNAP_COLOR, 0xff33b5e5);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.PIE_OVERLAY_COLOR, 0xcc000000);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.PIE_TEXT_COLOR, 0xffffffff);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_BG_NORMAL_COLOR, 0xdd0099cc);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_BG_SELECTED_COLOR, 0xff33b5e5);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_BG_LONG_PRESSED_COLOR, 0xff8ad5f0);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_OUTLINE_COLOR, 0xdd0099cc);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_ICON_COLOR, 0xffffffff);
                updatePreferences();
                return true;
            case R.id.pie_control_dark_jelly_default:
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.PIE_SNAP_COLOR, 0xffff0000);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.PIE_OVERLAY_COLOR, 0xaa000000);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.PIE_TEXT_COLOR, 0xff33b5e5);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_BG_NORMAL_COLOR, 0xaa202020);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_BG_SELECTED_COLOR, 0xaa33b5e5);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_BG_LONG_PRESSED_COLOR, 0xaa8ad5f0);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_OUTLINE_COLOR, 0x30ff0000);
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_ICON_COLOR, 0xffffffff);
                updatePreferences();
                return true;
            case R.id.pie_control_backup_color:
                backupAndRestore(true);
                return true;
            case R.id.pie_control_restore_color:
                backupAndRestore(false);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mPieSnapColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_SNAP_COLOR, intHex);
            return true;
        } else if (preference == mPieOverlayColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_OVERLAY_COLOR, intHex);
            return true;
        } else if (preference == mPieTextColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_TEXT_COLOR, intHex);
            return true;
        } else if (preference == mButtonBgNormalColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_BUTTON_BG_NORMAL_COLOR, intHex);
            return true;
        } else if (preference == mButtonBgSelectedColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_BUTTON_BG_SELECTED_COLOR, intHex);
            return true;
        } else if (preference == mButtonBgLongPressedColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_BUTTON_BG_LONG_PRESSED_COLOR, intHex);
            return true;
        } else if (preference == mButtonOutlineColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_BUTTON_OUTLINE_COLOR, intHex);
            return true;
        } else if (preference == mButtonIconColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_BUTTON_ICON_COLOR, intHex);
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mEnableThemeDefault) {
            value = mEnableThemeDefault.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.PIE_ENABLE_THEME_DEFAULT, value ? 1 : 0);
            updatePreferences();
            return true;
        } else if (preference == mDisableIconOverlay) {
            value = mDisableIconOverlay.isChecked();
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.PIE_DISABLE_ICON_OVERLAY, value ? 1 : 0);
            updatePreferences();
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePreferences();
    }

    public void updatePreferences() {
        int themeDefaultIntColor;
        int customIntColor;
        String customHexColor;

        boolean isThemeDefaultEnabled = mEnableThemeDefault.isChecked();
        boolean isIconOverlayDisabled = mDisableIconOverlay.isChecked();
        String themeDefaultColorSummary = getResources().getString(R.string.theme_default_color);

        if (isThemeDefaultEnabled) {
            themeDefaultIntColor = mSystemUiResources.getColor(
                    mSystemUiResources.getIdentifier("pie_snap_color", "color", "com.android.systemui"));
            mPieSnapColor.setNewPreviewColor(themeDefaultIntColor);
            mPieSnapColor.setSummary(themeDefaultColorSummary);
            mPieSnapColor.setEnabled(false);
        } else {
            mPieSnapColor.setEnabled(true);
            customIntColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_SNAP_COLOR, 0xff33b5e5);
            customHexColor = String.format("#%08x", (0xffffffff & customIntColor));
            mPieSnapColor.setNewPreviewColor(customIntColor);
            mPieSnapColor.setSummary(customHexColor);
        }

        if (isThemeDefaultEnabled) {
            themeDefaultIntColor = mSystemUiResources.getColor(
                    mSystemUiResources.getIdentifier("pie_overlay_color", "color", "com.android.systemui"));
            mPieOverlayColor.setNewPreviewColor(themeDefaultIntColor);
            mPieOverlayColor.setSummary(themeDefaultColorSummary);
            mPieOverlayColor.setEnabled(false);
        } else {
            mPieOverlayColor.setEnabled(true);
            customIntColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_OVERLAY_COLOR, 0xcc000000);
            customHexColor = String.format("#%08x", (0xffffffff & customIntColor));
            mPieOverlayColor.setNewPreviewColor(customIntColor);
            mPieOverlayColor.setSummary(customHexColor);
        }

        if (isThemeDefaultEnabled) {
            themeDefaultIntColor = mSystemUiResources.getColor(
                    mSystemUiResources.getIdentifier("pie_text_color", "color", "com.android.systemui"));
            mPieTextColor.setNewPreviewColor(themeDefaultIntColor);
            mPieTextColor.setSummary(themeDefaultColorSummary);
            mPieTextColor.setEnabled(false);
        } else {
            mPieTextColor.setEnabled(true);
            customIntColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_TEXT_COLOR, 0xffffffff);
            customHexColor = String.format("#%08x", (0xffffffff & customIntColor));
            mPieTextColor.setNewPreviewColor(customIntColor);
            mPieTextColor.setSummary(customHexColor);
        }

        if (isThemeDefaultEnabled) {
            themeDefaultIntColor = mSystemUiResources.getColor(
                    mSystemUiResources.getIdentifier("pie_background_color", "color", "com.android.systemui"));
            mButtonBgNormalColor.setNewPreviewColor(themeDefaultIntColor);
            mButtonBgNormalColor.setSummary(themeDefaultColorSummary);
            mButtonBgNormalColor.setEnabled(false);
        } else {
            mButtonBgNormalColor.setEnabled(true);
            customIntColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_BG_NORMAL_COLOR, 0xdd0099cc);
            customHexColor = String.format("#%08x", (0xffffffff & customIntColor));
            mButtonBgNormalColor.setNewPreviewColor(customIntColor);
            mButtonBgNormalColor.setSummary(customHexColor);
        }

        if (isThemeDefaultEnabled) {
            themeDefaultIntColor = mSystemUiResources.getColor(
                    mSystemUiResources.getIdentifier("pie_selected_color", "color", "com.android.systemui"));
            mButtonBgSelectedColor.setNewPreviewColor(themeDefaultIntColor);
            mButtonBgSelectedColor.setSummary(themeDefaultColorSummary);
            mButtonBgSelectedColor.setEnabled(false);
        } else {
            mButtonBgSelectedColor.setEnabled(true);
            customIntColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_BG_SELECTED_COLOR, 0xff33b5e5);
            customHexColor = String.format("#%08x", (0xffffffff & customIntColor));
            mButtonBgSelectedColor.setNewPreviewColor(customIntColor);
            mButtonBgSelectedColor.setSummary(customHexColor);
        }

        if (isThemeDefaultEnabled) {
            themeDefaultIntColor = mSystemUiResources.getColor(
                    mSystemUiResources.getIdentifier("pie_long_pressed_color", "color", "com.android.systemui"));
            mButtonBgLongPressedColor.setNewPreviewColor(themeDefaultIntColor);
            mButtonBgLongPressedColor.setSummary(themeDefaultColorSummary);
            mButtonBgLongPressedColor.setEnabled(false);
        } else {
            mButtonBgLongPressedColor.setEnabled(true);
            customIntColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_BG_LONG_PRESSED_COLOR, 0xff8ad5f0);
            customHexColor = String.format("#%08x", (0xffffffff & customIntColor));
            mButtonBgLongPressedColor.setNewPreviewColor(customIntColor);
            mButtonBgLongPressedColor.setSummary(customHexColor);
        }

        if (isThemeDefaultEnabled) {
            themeDefaultIntColor = mSystemUiResources.getColor(
                    mSystemUiResources.getIdentifier("pie_outline_color", "color", "com.android.systemui"));
            mButtonOutlineColor.setNewPreviewColor(themeDefaultIntColor);
            mButtonOutlineColor.setSummary(themeDefaultColorSummary);
            mButtonOutlineColor.setEnabled(false);
        } else {
            mButtonOutlineColor.setEnabled(true);
            customIntColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_OUTLINE_COLOR, 0xdd0099cc);
            customHexColor = String.format("#%08x", (0xffffffff & customIntColor));
            mButtonOutlineColor.setNewPreviewColor(customIntColor);
            mButtonOutlineColor.setSummary(customHexColor);
        }

        if (!isThemeDefaultEnabled && !isIconOverlayDisabled) {
            mButtonIconColor.setEnabled(true);
        }
        if (isThemeDefaultEnabled && !isIconOverlayDisabled) {
            themeDefaultIntColor = mSystemUiResources.getColor(
                    mSystemUiResources.getIdentifier("pie_foreground_color", "color", "com.android.systemui"));
            mButtonIconColor.setNewPreviewColor(themeDefaultIntColor);
            mButtonIconColor.setSummary(getResources().getString(R.string.button_icon_default_pie_color_summary));
        } else if (!isThemeDefaultEnabled && !isIconOverlayDisabled){
            customIntColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.PIE_BUTTON_ICON_COLOR, 0xffffffff);
            customHexColor = String.format("#%08x", (0xffffffff & customIntColor));
            mButtonIconColor.setNewPreviewColor(customIntColor);
            mButtonIconColor.setSummary(customHexColor);
        } else {
            int pieDefaultIntColor = mSystemUiResources.getColor(
                mSystemUiResources.getIdentifier("pie_foreground_color", "color", "com.android.systemui"));
            mButtonIconColor.setNewPreviewColor(pieDefaultIntColor);
            mButtonIconColor.setSummary(themeDefaultColorSummary);
        }

        if (isThemeDefaultEnabled) {
           mButtonIconColor.setEnabled(false);
        } else if (!isThemeDefaultEnabled && isIconOverlayDisabled) {
            mButtonIconColor.setEnabled(false);
        }
    }

    public void backupAndRestore(boolean backup) {
        int intColor;
        String hex;

        if (backup) {
            intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.PIE_SNAP_COLOR, 0xff33b5e5);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_SNAP_COLOR_B, intColor);
            intColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_OVERLAY_COLOR, 0xcc000000);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_OVERLAY_COLOR_B, intColor);
            intColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_TEXT_COLOR, 0xffffffff);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_TEXT_COLOR_B, intColor);
            intColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_BG_NORMAL_COLOR, 0xdd0099cc);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_BUTTON_BG_NORMAL_COLOR_B, intColor);
            intColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_BG_SELECTED_COLOR, 0xff33b5e5);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_BUTTON_BG_SELECTED_COLOR_B, intColor);
            intColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_BG_LONG_PRESSED_COLOR, 0xff8ad5f0);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_BUTTON_BG_LONG_PRESSED_COLOR_B, intColor);
            intColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_OUTLINE_COLOR, 0xdd0099cc);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_BUTTON_OUTLINE_COLOR_B, intColor);
            intColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_ICON_COLOR, 0xffffffff);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_BUTTON_ICON_COLOR_B, intColor);
        } else {
            intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.PIE_SNAP_COLOR_B, 0xff33b5e5);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_SNAP_COLOR, intColor);
            intColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_OVERLAY_COLOR_B, 0xcc000000);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_OVERLAY_COLOR, intColor);
            intColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_TEXT_COLOR_B, 0xffffffff);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_TEXT_COLOR, intColor);
            intColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_BG_NORMAL_COLOR_B, 0xdd0099cc);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_BUTTON_BG_NORMAL_COLOR, intColor);
            intColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_BG_SELECTED_COLOR_B, 0xff33b5e5);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_BUTTON_BG_SELECTED_COLOR, intColor);
            intColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_BG_LONG_PRESSED_COLOR_B, 0xff8ad5f0);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_BUTTON_BG_LONG_PRESSED_COLOR, intColor);
            intColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_OUTLINE_COLOR_B, 0xdd0099cc);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_BUTTON_OUTLINE_COLOR, intColor);
            intColor = Settings.System.getInt(getActivity().getContentResolver(),
                        Settings.System.PIE_BUTTON_ICON_COLOR_B, 0xffffffff);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.PIE_BUTTON_ICON_COLOR, intColor);
        }
        updatePreferences();
    }
}

