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
import android.content.Context;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.settings.darkjelly.colorpicker.ColorPickerPreference;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SeekBarPreference;

public class PieControlColor extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "PieControlColor";

    private static final String PREF_ENABLE_THEME_DEFAULT = "pie_enable_theme_default";
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

        addPreferencesFromResource(R.xml.pie_control_color);

        mResolver = getActivity().getContentResolver();

        mEnableThemeDefault = (CheckBoxPreference) findPreference(PREF_ENABLE_THEME_DEFAULT);
        mEnableThemeDefault.setChecked(Settings.System.getInt(mResolver,
                Settings.System.PIE_ENABLE_THEME_DEFAULT, 1) == 1);
        mEnableThemeDefault.setOnPreferenceChangeListener(this);

        mDisableIconOverlay = (CheckBoxPreference) findPreference(PREF_DISABLE_ICON_OVERLAY);
        mDisableIconOverlay.setChecked(Settings.System.getInt(mResolver,
                Settings.System.PIE_DISABLE_ICON_OVERLAY, 1) == 1);
        mDisableIconOverlay.setOnPreferenceChangeListener(this);

        mPieSnapColor = (ColorPickerPreference) findPreference(PREF_PIE_SNAP_COLOR);
        mPieSnapColor.setAlphaSliderEnabled(true);
        mPieSnapColor.setOnPreferenceChangeListener(this);

        mPieOverlayColor = (ColorPickerPreference) findPreference(PREF_PIE_OVERLAY_COLOR);
        mPieOverlayColor.setAlphaSliderEnabled(true);
        mPieOverlayColor.setOnPreferenceChangeListener(this);

        mPieTextColor = (ColorPickerPreference) findPreference(PREF_PIE_TEXT_COLOR);
        mPieTextColor.setAlphaSliderEnabled(true);
        mPieTextColor.setOnPreferenceChangeListener(this);

        mButtonBgNormalColor = (ColorPickerPreference) findPreference(PREF_BUTTON_BG_NORMAL_COLOR);
        mButtonBgNormalColor.setAlphaSliderEnabled(true);
        mButtonBgNormalColor.setOnPreferenceChangeListener(this);

        mButtonBgSelectedColor = (ColorPickerPreference) findPreference(PREF_BUTTON_BG_SELECTED_COLOR);
        mButtonBgSelectedColor.setAlphaSliderEnabled(true);
        mButtonBgSelectedColor.setOnPreferenceChangeListener(this);

        mButtonBgLongPressedColor = (ColorPickerPreference) findPreference(PREF_BUTTON_BG_LONG_PRESSED_COLOR);
        mButtonBgLongPressedColor.setAlphaSliderEnabled(true);
        mButtonBgLongPressedColor.setOnPreferenceChangeListener(this);

        mButtonOutlineColor = (ColorPickerPreference) findPreference(PREF_BUTTON_OUTLINE_COLOR);
        mButtonOutlineColor.setAlphaSliderEnabled(true);
        mButtonOutlineColor.setOnPreferenceChangeListener(this);

        mButtonIconColor = (ColorPickerPreference) findPreference(PREF_BUTTON_ICON_COLOR);
        mButtonIconColor.setAlphaSliderEnabled(true);
        mButtonIconColor.setOnPreferenceChangeListener(this);

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
                Settings.System.putInt(mResolver, Settings.System.PIE_SNAP_COLOR, 0xff33b5e5);
                Settings.System.putInt(mResolver, Settings.System.PIE_OVERLAY_COLOR, 0xcc000000);
                Settings.System.putInt(mResolver, Settings.System.PIE_TEXT_COLOR, 0xffffffff);
                Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_BG_NORMAL_COLOR, 0xdd0099cc);
                Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_BG_SELECTED_COLOR, 0xff33b5e5);
                Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_BG_LONG_PRESSED_COLOR, 0xff8ad5f0);
                Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_OUTLINE_COLOR, 0xdd0099cc);
                Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_ICON_COLOR, 0xffffffff);
                refreshSettings();
                return true;
            case R.id.pie_control_dark_jelly_default:
                Settings.System.putInt(mResolver, Settings.System.PIE_SNAP_COLOR, 0xffff0000);
                Settings.System.putInt(mResolver, Settings.System.PIE_OVERLAY_COLOR, 0xaa000000);
                Settings.System.putInt(mResolver, Settings.System.PIE_TEXT_COLOR, 0xff33b5e5);
                Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_BG_NORMAL_COLOR, 0xaa202020);
                Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_BG_SELECTED_COLOR, 0xaa33b5e5);
                Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_BG_LONG_PRESSED_COLOR, 0xaa8ad5f0);
                Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_OUTLINE_COLOR, 0x30ff0000);
                Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_ICON_COLOR, 0xffffffff);
                refreshSettings();
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
        if (preference == mEnableThemeDefault) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.PIE_ENABLE_THEME_DEFAULT, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mDisableIconOverlay) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(mResolver, Settings.System.PIE_DISABLE_ICON_OVERLAY, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mPieSnapColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.PIE_SNAP_COLOR, intHex);
            return true;
        } else if (preference == mPieOverlayColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.PIE_OVERLAY_COLOR, intHex);
            return true;
        } else if (preference == mPieTextColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.PIE_TEXT_COLOR, intHex);
            return true;
        } else if (preference == mButtonBgNormalColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.PIE_BUTTON_BG_NORMAL_COLOR, intHex);
            return true;
        } else if (preference == mButtonBgSelectedColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.PIE_BUTTON_BG_SELECTED_COLOR, intHex);
            return true;
        } else if (preference == mButtonBgLongPressedColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.PIE_BUTTON_BG_LONG_PRESSED_COLOR, intHex);
            return true;
        } else if (preference == mButtonOutlineColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.PIE_BUTTON_OUTLINE_COLOR, intHex);
            return true;
        } else if (preference == mButtonIconColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                    Settings.System.PIE_BUTTON_ICON_COLOR, intHex);
            return true;
        }
        return false;
    }

    public void updatePreferences() {
        boolean isThemeDefaultEnabled = mEnableThemeDefault.isChecked();
        boolean isIconOverlayDisabled = mDisableIconOverlay.isChecked();
        String themeDefaultColorSummary = getResources().getString(R.string.theme_default_color);
        int intColor = 0xff33b5e5;
        String hexColor = String.format("#%08x", (0xffffffff & 0xff33b5e5));

        if (isThemeDefaultEnabled) {
            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_SNAP_COLOR, 0xff33b5e5);
            mPieSnapColor.setNewPreviewColor(intColor);
            mPieSnapColor.setSummary(themeDefaultColorSummary);
            mPieSnapColor.setEnabled(false);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_OVERLAY_COLOR, 0xcc000000);
            mPieOverlayColor.setNewPreviewColor(intColor);
            mPieOverlayColor.setSummary(themeDefaultColorSummary);
            mPieOverlayColor.setEnabled(false);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_TEXT_COLOR, 0xffffffff);
            mPieTextColor.setNewPreviewColor(intColor);
            mPieTextColor.setSummary(themeDefaultColorSummary);
            mPieTextColor.setEnabled(false);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_BG_NORMAL_COLOR, 0xdd0099cc);
            mButtonBgNormalColor.setNewPreviewColor(intColor);
            mButtonBgNormalColor.setSummary(themeDefaultColorSummary);
            mButtonBgNormalColor.setEnabled(false);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_BG_SELECTED_COLOR, 0xff33b5e5);
            mButtonBgSelectedColor.setNewPreviewColor(intColor);
            mButtonBgSelectedColor.setSummary(themeDefaultColorSummary);
            mButtonBgSelectedColor.setEnabled(false);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_BG_LONG_PRESSED_COLOR, 0xff8ad5f0);
            mButtonBgLongPressedColor.setNewPreviewColor(intColor);
            mButtonBgLongPressedColor.setSummary(themeDefaultColorSummary);
            mButtonBgLongPressedColor.setEnabled(false);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_OUTLINE_COLOR, 0xdd0099cc);
            mButtonOutlineColor.setNewPreviewColor(intColor);
            mButtonOutlineColor.setSummary(themeDefaultColorSummary);
            mButtonOutlineColor.setEnabled(false);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_ICON_COLOR, 0xffffffff);
            mButtonIconColor.setNewPreviewColor(intColor);
            String pieDefaultIconColorSummary = getResources().getString(R.string.button_icon_default_pie_color_summary);
            mButtonIconColor.setSummary(isIconOverlayDisabled ? themeDefaultColorSummary : pieDefaultIconColorSummary);
            mButtonIconColor.setEnabled(false);
        } else {
            mPieSnapColor.setEnabled(true);
            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_SNAP_COLOR, 0xff33b5e5);
            mPieSnapColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mPieSnapColor.setSummary(hexColor);

            mPieOverlayColor.setEnabled(true);
            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_OVERLAY_COLOR, 0xcc000000);
            mPieOverlayColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mPieOverlayColor.setSummary(hexColor);

            mPieTextColor.setEnabled(true);
            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_TEXT_COLOR, 0xffffffff);
            mPieTextColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mPieTextColor.setSummary(hexColor);

            mButtonBgNormalColor.setEnabled(true);
            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_BG_NORMAL_COLOR, 0xdd0099cc);
            mButtonBgNormalColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mButtonBgNormalColor.setSummary(hexColor);

            mButtonBgSelectedColor.setEnabled(true);
            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_BG_SELECTED_COLOR, 0xff33b5e5);
            mButtonBgSelectedColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mButtonBgSelectedColor.setSummary(hexColor);

            mButtonBgLongPressedColor.setEnabled(true);
            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_BG_LONG_PRESSED_COLOR, 0xff8ad5f0);
            mButtonBgLongPressedColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mButtonBgLongPressedColor.setSummary(hexColor);

            mButtonOutlineColor.setEnabled(true);
            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_OUTLINE_COLOR, 0xdd0099cc);
            mButtonOutlineColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mButtonOutlineColor.setSummary(hexColor);

            mButtonIconColor.setEnabled(isIconOverlayDisabled ? false : true);
            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_ICON_COLOR, 0xffffffff);
            mButtonIconColor.setNewPreviewColor(intColor);
            hexColor = String.format("#%08x", (0xffffffff & intColor));
            mButtonIconColor.setSummary(isIconOverlayDisabled ? hexColor : themeDefaultColorSummary);
        }
    }

    public void backupAndRestore(boolean backup) {
        int intColor;
        String hex;

        if (backup) {
            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_SNAP_COLOR, 0xff33b5e5);
            Settings.System.putInt(mResolver, Settings.System.PIE_SNAP_COLOR_B, intColor);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_OVERLAY_COLOR, 0xcc000000);
            Settings.System.putInt(mResolver, Settings.System.PIE_OVERLAY_COLOR_B, intColor);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_TEXT_COLOR, 0xffffffff);
            Settings.System.putInt(mResolver, Settings.System.PIE_TEXT_COLOR_B, intColor);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_BG_NORMAL_COLOR, 0xdd0099cc);
            Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_BG_NORMAL_COLOR_B, intColor);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_BG_SELECTED_COLOR, 0xff33b5e5);
            Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_BG_SELECTED_COLOR_B, intColor);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_BG_LONG_PRESSED_COLOR, 0xff8ad5f0);
            Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_BG_LONG_PRESSED_COLOR_B, intColor);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_OUTLINE_COLOR, 0xdd0099cc);
            Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_OUTLINE_COLOR_B, intColor);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_ICON_COLOR, 0xffffffff);
            Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_ICON_COLOR_B, intColor);
        } else {
            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_SNAP_COLOR_B, 0xff33b5e5);
            Settings.System.putInt(mResolver, Settings.System.PIE_SNAP_COLOR, intColor);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_OVERLAY_COLOR_B, 0xcc000000);
            Settings.System.putInt(mResolver, Settings.System.PIE_OVERLAY_COLOR, intColor);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_TEXT_COLOR_B, 0xffffffff);
            Settings.System.putInt(mResolver, Settings.System.PIE_TEXT_COLOR, intColor);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_BG_NORMAL_COLOR_B, 0xdd0099cc);
            Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_BG_NORMAL_COLOR, intColor);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_BG_SELECTED_COLOR_B, 0xff33b5e5);
            Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_BG_SELECTED_COLOR, intColor);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_BG_LONG_PRESSED_COLOR_B, 0xff8ad5f0);
            Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_BG_LONG_PRESSED_COLOR, intColor);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_OUTLINE_COLOR_B, 0xdd0099cc);
            Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_OUTLINE_COLOR, intColor);

            intColor = Settings.System.getInt(mResolver, Settings.System.PIE_BUTTON_ICON_COLOR_B, 0xffffffff);
            Settings.System.putInt(mResolver, Settings.System.PIE_BUTTON_ICON_COLOR, intColor);
        }
        refreshSettings();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
