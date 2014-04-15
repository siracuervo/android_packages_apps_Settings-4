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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.internal.util.darkkat.DeviceUtils;
import com.android.internal.widget.LockPatternUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import java.io.File;

public class LockscreenLockRingSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String TAG = "LockscreenLockRingSettings";

    private static final String PREF_CAT_OPTIONS =
            "lockscreen_lock_ring_cat_options";
    private static final String PREF_CAT_TARGETS =
            "lockscreen_lock_ring_cat_targets";
    private static final String PREF_CAT_COLORS =
            "lockscreen_lock_ring_cat_colors";
    private static final String PREF_LOCKSCREEN_LOCK_BEFORE_UNLOCK =
            "lockscreen_lock_before_unlock";
    private static final String PREF_LOCKSCREEN_TORCH =
            "lockscreen_torch";
    private static final String PREF_LOCKSCREEN_EIGHT_TARGETS =
            "lockscreen_eight_targets";
    private static final String PREF_LOCKSCREEN_COLORIZE_TARGETS_ICON =
            "lockscreen_colorize_targets_icon";
    private static final String PREF_LOCKSCREEN_LOCK_ICON =
            "lockscreen_lock_icon";
    private static final String PREF_LOCKSCREEN_COLORIZE_CUSTOM_LOCK_ICON =
            "lockscreen_colorize_custom_lock_icon";
    private static final String PREF_LOCKSCREEN_TARGETS_COLOR =
            "lockscreen_targets_color";
    private static final String PREF_LOCKSCREEN_TARGETS_RING_COLOR =
            "lockscreen_targets_ring_color";
    private static final String PREF_LOCKSCREEN_LOCK_ICON_COLOR =
            "lockscreen_lock_icon_color";
    private static final String PREF_LOCKSCREEN_DOTS_COLOR =
            "lockscreen_dots_color";

    private static final int DEFAULT_TARGETS_COLOR =
            0xffffffff;
    private static final int DEFAULT_TARGETS_RING_COLOR =
            0x1affffff;
    private static final int DEFAULT_LOCK_ICON_COLOR =
            0xffffffff;
    private static final int DEFAULT_DOTS_COLOR =
            0xffffffff;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;
    private static final int DLG_ENABLE_EIGHT_TARGETS = 1;
    private static final int REQUEST_PICK_LOCK_ICON = 100;

    private File mLockImage;

    private CheckBoxPreference mLockBeforeUnlock;
    private CheckBoxPreference mGlowpadTorch;
    private CheckBoxPreference mEightTargets;
    private CheckBoxPreference mColorizeTargetsIcon;
    private ListPreference mLockIcon;
    private CheckBoxPreference mColorizeCustomLockIcon;
    private ColorPickerPreference mTargetsColor;
    private ColorPickerPreference mTargetsRingColor;
    private ColorPickerPreference mLockIconColor;
    private ColorPickerPreference mDotsColor;

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

        addPreferencesFromResource(R.xml.lockscreen_lock_ring_settings);

        mResolver = getActivity().getContentResolver();
        mLockImage = new File(getActivity().getFilesDir() + "/lock_icon.tmp");

        boolean isSecure = new LockPatternUtils(getActivity()).isSecure();
        boolean dotsDisabled = isSecure && Settings.System.getInt(
                getContentResolver(),Settings.System.LOCK_BEFORE_UNLOCK, 0) == 0;
        boolean colorizeTargetsIcon = Settings.System.getInt(
                getContentResolver(),Settings.System.LOCKSCREEN_COLORIZE_TARGETS_ICON,
                1) == 1;
        boolean imageExists = Settings.System.getString(getContentResolver(),
                Settings.System.LOCKSCREEN_LOCK_ICON) != null;

        int color;
        String hexColor;

        mLockBeforeUnlock =
                (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_LOCK_BEFORE_UNLOCK);
        mLockBeforeUnlock.setChecked(Settings.System.getInt(mResolver,
                Settings.System.LOCK_BEFORE_UNLOCK, 0) == 1);
        if (isSecure) {
            mLockBeforeUnlock.setSummary(
                    R.string.lockscreen_lock_before_unlock_summary);
            mLockBeforeUnlock.setEnabled(true);
        } else {
            mLockBeforeUnlock.setSummary(
                    R.string.lockscreen_lock_before_unlock_disabled_summary);
            mLockBeforeUnlock.setEnabled(false);
        }
        mLockBeforeUnlock.setEnabled(isSecure);
        mLockBeforeUnlock.setOnPreferenceChangeListener(this);

        // Remove glowpad torch function on devices without torch support
        PreferenceCategory catOptions =
                (PreferenceCategory) findPreference(PREF_CAT_OPTIONS);
        mGlowpadTorch =
                (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_TORCH);
        if (DeviceUtils.deviceSupportsTorch(getActivity())) {
            mGlowpadTorch.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_GLOWPAD_TORCH, 0) == 1);
            mGlowpadTorch.setOnPreferenceChangeListener(this);
        } else {
            catOptions.removePreference(mGlowpadTorch);
        }

        // Show preference on phones only
        PreferenceCategory catTargets =
                (PreferenceCategory) findPreference(PREF_CAT_TARGETS);
        mEightTargets =
                (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_EIGHT_TARGETS);
        if (DeviceUtils.isPhone(getActivity())) {
            mEightTargets.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_EIGHT_TARGETS, 0) == 1);
            mEightTargets.setOnPreferenceChangeListener(this);
        } else {
            catTargets.removePreference(mEightTargets);
        }

        mColorizeTargetsIcon =
            (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_COLORIZE_TARGETS_ICON);
        mColorizeTargetsIcon.setChecked(colorizeTargetsIcon);
        mColorizeTargetsIcon.setOnPreferenceChangeListener(this);

        mLockIcon = (ListPreference) findPreference(PREF_LOCKSCREEN_LOCK_ICON);
        mLockIcon.setEnabled(!dotsDisabled);
        mLockIcon.setOnPreferenceChangeListener(this);

        mColorizeCustomLockIcon =
                (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_COLORIZE_CUSTOM_LOCK_ICON);
        mColorizeCustomLockIcon.setChecked(Settings.System.getInt(mResolver,
                Settings.System.LOCKSCREEN_COLORIZE_LOCK, 0) == 1);
        mColorizeCustomLockIcon.setEnabled(!dotsDisabled && imageExists);
        mColorizeCustomLockIcon.setOnPreferenceChangeListener(this);

        // Remove targets color preference, if colorizing targets icons is disabled
        PreferenceCategory catColor =
                (PreferenceCategory) findPreference(PREF_CAT_COLORS);
        mTargetsColor =
                (ColorPickerPreference) findPreference(PREF_LOCKSCREEN_TARGETS_COLOR);
        if (colorizeTargetsIcon) {
            color = Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_TARGETS_COLOR, DEFAULT_TARGETS_COLOR);
            mTargetsColor.setNewPreviewColor(color);
            hexColor = String.format("#%08x", (0xffffffff & color));
            mTargetsColor.setSummary(hexColor);
            mTargetsColor.setOnPreferenceChangeListener(this);
        } else {
            catColor.removePreference(mTargetsColor);
        }

        mTargetsRingColor =
                (ColorPickerPreference) findPreference(PREF_LOCKSCREEN_TARGETS_RING_COLOR);
        color = Settings.System.getInt(mResolver,
                Settings.System.LOCKSCREEN_TARGETS_RING_COLOR, DEFAULT_TARGETS_RING_COLOR);
        mTargetsRingColor.setNewPreviewColor(color);
        hexColor = String.format("#%08x", (0xffffffff & color));
        mTargetsRingColor.setSummary(hexColor);
        mTargetsRingColor.setAlphaSliderEnabled(true);
        mTargetsRingColor.setOnPreferenceChangeListener(this);

        mLockIconColor =
                (ColorPickerPreference) findPreference(PREF_LOCKSCREEN_LOCK_ICON_COLOR);
        color = Settings.System.getInt(mResolver,
                Settings.System.LOCKSCREEN_LOCK_COLOR, DEFAULT_LOCK_ICON_COLOR);
        mLockIconColor.setNewPreviewColor(color);
        hexColor = String.format("#%08x", (0xffffffff & color));
        mLockIconColor.setSummary(hexColor);
        mLockIconColor.setOnPreferenceChangeListener(this);

        mDotsColor =
                (ColorPickerPreference) findPreference(PREF_LOCKSCREEN_DOTS_COLOR);
        color = Settings.System.getInt(mResolver,
                Settings.System.LOCKSCREEN_DOTS_COLOR, DEFAULT_DOTS_COLOR);
        mDotsColor.setNewPreviewColor(color);
        hexColor = String.format("#%08x", (0xffffffff & color));
        mDotsColor.setSummary(hexColor);
        mDotsColor.setOnPreferenceChangeListener(this);

        updateLockSummary();

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

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        int index;
        int intHex;
        String hex;
        boolean value;

        if (preference == mLockBeforeUnlock) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCK_BEFORE_UNLOCK, value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mGlowpadTorch) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                Settings.System.LOCKSCREEN_GLOWPAD_TORCH, value ? 1 : 0);
            return true;
        } else if (preference == mEightTargets) {
            showDialogInner(DLG_ENABLE_EIGHT_TARGETS, (Boolean) newValue);
            return true;
        } else if (preference == mColorizeTargetsIcon) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_COLORIZE_TARGETS_ICON,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mLockIcon) {
            index = mLockIcon.findIndexOfValue(newValue.toString());
            if (index == 1) {
                deleteLockIcon();
                Settings.System.putInt(mResolver,
                        Settings.System.LOCKSCREEN_USE_DK_LOCK_ICON, 1);
                updateLockSummary();
            } else if (index == 2) {
                Settings.System.putInt(mResolver,
                        Settings.System.LOCKSCREEN_USE_DK_LOCK_ICON, 0);
                requestLockImage();
            } else {
                Settings.System.putInt(mResolver,
                        Settings.System.LOCKSCREEN_USE_DK_LOCK_ICON, 0);
                deleteLockIcon();
            }
            return true;
        } else if (preference == mColorizeCustomLockIcon) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                Settings.System.LOCKSCREEN_COLORIZE_LOCK, value ? 1 : 0);
            return true;
        } else if (preference == mTargetsColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.LOCKSCREEN_TARGETS_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mTargetsRingColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.LOCKSCREEN_TARGETS_RING_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mLockIconColor) {
            hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.LOCKSCREEN_LOCK_COLOR, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mDotsColor) {
            hex = ColorPickerPreference.convertToARGB(Integer.valueOf(
                    String.valueOf(newValue)));
            intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.LOCKSCREEN_DOTS_COLOR, intHex);
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

        LockscreenLockRingSettings getOwner() {
            return (LockscreenLockRingSettings) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            final boolean state = getArguments().getBoolean("state");
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
                                Settings.System.LOCKSCREEN_TARGETS_COLOR,
                                DEFAULT_TARGETS_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_TARGETS_RING_COLOR,
                                DEFAULT_TARGETS_RING_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_LOCK_COLOR,
                                DEFAULT_LOCK_ICON_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_DOTS_COLOR,
                                DEFAULT_DOTS_COLOR);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_TARGETS_COLOR,
                                0xff33b5e5);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_TARGETS_RING_COLOR,
                                0x1a33b5e5);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_LOCK_COLOR,
                                DEFAULT_LOCK_ICON_COLOR);
                            Settings.System.putInt(getOwner().mResolver,
                                Settings.System.LOCKSCREEN_DOTS_COLOR,
                                0xffff0000);
                            getOwner().refreshSettings();
                        }
                    })
                    .create();
                case DLG_ENABLE_EIGHT_TARGETS:
                    String message = getOwner().getResources()
                                .getString(R.string.lockscreen_enable_eight_targets_dialog);
                    if (state) {
                        message = message + " " + getOwner().getResources().getString(
                                R.string.lockscreen_enable_eight_targets_enabled_dialog);
                    }
                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.attention)
                    .setMessage(message)
                    .setNegativeButton(R.string.dlg_cancel,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton(R.string.dlg_ok,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().getContentResolver(),
                                    Settings.System.LOCKSCREEN_EIGHT_TARGETS, state ? 1 : 0);
                            Settings.System.putString(getOwner().getContentResolver(),
                                    Settings.System.LOCKSCREEN_TARGETS, null);
                            for (File pic : getOwner().getActivity().getFilesDir().listFiles()) {
                                if (pic.getName().startsWith("lockscreen_")) {
                                    pic.delete();
                                }
                            }
                            if (state) {
                                Toast.makeText(getOwner().getActivity(),
                                        R.string.lockscreen_target_reset,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .create();
            }
            throw new IllegalArgumentException("unknown id " + id);
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            int id = getArguments().getInt("id");
            boolean state = getArguments().getBoolean("state");
            switch (id) {
                case DLG_ENABLE_EIGHT_TARGETS:
                    getOwner().mEightTargets.setChecked(!state);
                    break;
             }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK_LOCK_ICON) {

                if (mLockImage.length() == 0 || !mLockImage.exists()) {
                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.shortcut_image_not_valid),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                File image = new File(getActivity().getFilesDir() + File.separator
                        + "lock_icon" + System.currentTimeMillis() + ".png");
                String path = image.getAbsolutePath();
                mLockImage.renameTo(image);
                image.setReadable(true, false);

                Settings.System.putString(getContentResolver(),
                        Settings.System.LOCKSCREEN_LOCK_ICON, path);

                mColorizeCustomLockIcon.setEnabled(path != null);
            }
        } else {
            if (mLockImage.exists()) {
                mLockImage.delete();
            }
        }
        updateLockSummary();
    }

    private void requestLockImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 68, getResources().getDisplayMetrics());

        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", px);
        intent.putExtra("aspectY", px);
        intent.putExtra("outputX", px);
        intent.putExtra("outputY", px);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());

        try {
            mLockImage.createNewFile();
            mLockImage.setWritable(true, false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mLockImage));
            startActivityForResult(intent, REQUEST_PICK_LOCK_ICON);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void deleteLockIcon() {
        String path = Settings.System.getString(getContentResolver(),
                Settings.System.LOCKSCREEN_LOCK_ICON);

        if (path != null) {
            File f = new File(path);

            if (f != null && f.exists()) {
                f.delete();
            }
        }

        Settings.System.putString(getContentResolver(),
                Settings.System.LOCKSCREEN_LOCK_ICON, null);

        mColorizeCustomLockIcon.setEnabled(false);
        updateLockSummary();
    }

    private void updateLockSummary() {
        int resId;
        boolean useDkLockIcon = Settings.System.getInt(mResolver,
                Settings.System.LOCKSCREEN_USE_DK_LOCK_ICON, 0) == 1;
        String value = Settings.System.getString(getContentResolver(),
                Settings.System.LOCKSCREEN_LOCK_ICON);

        if (useDkLockIcon) {
            resId = R.string.lockscreen_icon_dk;
            mLockIcon.setValueIndex(1);
        } else {
            if (value == null) {
                resId = R.string.icon_default;
                mLockIcon.setValueIndex(0);
            } else {
                resId = R.string.icon_custom;
                mLockIcon.setValueIndex(2);
            }
        }
        mLockIcon.setSummary(getResources().getString(resId));
    }
}
