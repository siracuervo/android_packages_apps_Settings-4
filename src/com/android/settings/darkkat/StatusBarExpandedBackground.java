/*
 * Copyright (C) 2012 Slimroms Project
 *
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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.widget.SeekBarPreference;

import java.io.File;

import net.margaritov.preference.colorpicker.ColorPickerView;

public class StatusBarExpandedBackground extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String TAG = "StatusBarExpandedBackground";

    private static final String PREF_STAT_BAR_EXPANDED_BG = "status_bar_expanded_bg";
    private static final String PREF_STAT_BAR_EXPANDED_BG_LANDSCAPE = "status_bar_expanded_bg_landscape";
    private static final String PREF_STAT_BAR_EXPANDED_BG_ALPHA = "status_bar_expanded_bg_alpha";

    private static final int DLG_PICK_COLOR = 0;

    private ListPreference mBackground;
    private ListPreference mBackgroundLandscape;
    SeekBarPreference mBackgroundAlpha;

    private File mImageTmp;

    private static final int REQUEST_PICK_WALLPAPER = 201;
    private static final int REQUEST_PICK_WALLPAPER_LANDSCAPE = 202;

    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();

        addPreferencesFromResource(R.xml.status_bar_expanded_background);

        PreferenceScreen prefSet = getPreferenceScreen();

        mImageTmp = new File(getActivity().getFilesDir() + "/statbarexp_bg.tmp");

        mBackground = (ListPreference) findPreference(PREF_STAT_BAR_EXPANDED_BG);
        mBackground.setOnPreferenceChangeListener(this);

        mBackgroundLandscape = (ListPreference) findPreference(PREF_STAT_BAR_EXPANDED_BG_LANDSCAPE);
        if (!Utils.isPhone(mActivity)) {
            prefSet.removePreference(mBackgroundLandscape);
        } else {
            mBackgroundLandscape.setOnPreferenceChangeListener(this);
        }

        float backgroundTransparency;
        try {
            backgroundTransparency = Settings.System.getFloat(getContentResolver(),
                    Settings.System.STATUS_BAR_EXPANDED_BG_ALPHA);
        } catch (Exception e) {
            backgroundTransparency = 0;
            Settings.System.putFloat(getContentResolver(),
                    Settings.System.STATUS_BAR_EXPANDED_BG_ALPHA, 0.1f);
        }
        mBackgroundAlpha = (SeekBarPreference) findPreference(PREF_STAT_BAR_EXPANDED_BG_ALPHA);
        mBackgroundAlpha.setInitValue((int) (backgroundTransparency * 100));
        mBackgroundAlpha.setProperty(Settings.System.STATUS_BAR_EXPANDED_BG_ALPHA);
        mBackgroundAlpha.setOnPreferenceChangeListener(this);

        updateCustomBackgroundSummary();
    }


    @Override
    public void onResume() {
        super.onResume();
        updateCustomBackgroundSummary();
    }


    private void updateCustomBackgroundSummary() {
        int resId;
        String value = Settings.System.getString(getContentResolver(),
                Settings.System.STATUS_BAR_EXPANDED_BG);
        if (value == null) {
            resId = R.string.status_bar_expanded_bg_default_wallpaper;
            mBackground.setValueIndex(2);
            mBackgroundLandscape.setEnabled(false);
        } else if (value.startsWith("color=")) {
            resId = R.string.status_bar_expanded_bg_color_fill;
            mBackground.setValueIndex(0);
            mBackgroundLandscape.setEnabled(false);
        } else {
            resId = R.string.status_bar_expanded_bg_custom_image;
            mBackground.setValueIndex(1);
            mBackgroundLandscape.setEnabled(true);
        }
        mBackground.setSummary(getResources().getString(resId));

        value = Settings.System.getString(getContentResolver(),
                Settings.System.STATUS_BAR_EXPANDED_BG_LANDSCAPE);
        if (value == null) {
            resId = R.string.status_bar_expanded_bg_default_wallpaper;
            mBackgroundLandscape.setValueIndex(1);
        } else {
            resId = R.string.status_bar_expanded_bg_custom_image;
            mBackgroundLandscape.setValueIndex(0);
        }
        mBackgroundLandscape.setSummary(getResources().getString(resId));
    }

    public void deleteWallpaper(boolean orientation) {
        String path = Settings.System.getString(getContentResolver(),
                Settings.System.STATUS_BAR_EXPANDED_BG);
        if (path != null && !path.startsWith("color=")) {
            File wallpaperToDelete = new File(Uri.parse(path).getPath());

            if (wallpaperToDelete != null
                    && wallpaperToDelete.exists() && !orientation) {
                wallpaperToDelete.delete();
            }
        }

        path = Settings.System.getString(getContentResolver(),
                Settings.System.STATUS_BAR_EXPANDED_BG_LANDSCAPE);
        if (path != null) {
            File wallpaperToDelete = new File(Uri.parse(path).getPath());

            if (wallpaperToDelete != null
                    && wallpaperToDelete.exists() && orientation) {
                wallpaperToDelete.delete();
            }
            if (orientation) {
                Settings.System.putString(getContentResolver(),
                    Settings.System.STATUS_BAR_EXPANDED_BG_LANDSCAPE, null);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK_WALLPAPER
                    || requestCode == REQUEST_PICK_WALLPAPER_LANDSCAPE) {

                if (mImageTmp.length() == 0 || !mImageTmp.exists()) {
                    Toast.makeText(mActivity,
                            getResources().getString(R.string.image_not_valid),
                            Toast.LENGTH_LONG).show();
                    return;
                }
                File image = new File(mActivity.getFilesDir() + File.separator
                        + "status_bar_expanded_bg_" + System.currentTimeMillis() + ".png");
                String path = image.getAbsolutePath();
                mImageTmp.renameTo(image);
                image.setReadable(true, false);

                if (requestCode == REQUEST_PICK_WALLPAPER) {
                    Settings.System.putString(getContentResolver(),
                        Settings.System.STATUS_BAR_EXPANDED_BG, path);
                } else {
                    Settings.System.putString(getContentResolver(),
                        Settings.System.STATUS_BAR_EXPANDED_BG_LANDSCAPE, path);
                }
            }
        } else {
            if (mImageTmp.exists()) {
                mImageTmp.delete();
            }
        }
        updateCustomBackgroundSummary();
    }

    private void startPictureCrop(int request, boolean landscape) {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        boolean isPortrait = getResources()
            .getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        intent.putExtra("aspectX", (landscape ? !isPortrait : isPortrait)
                ? width : height);
        intent.putExtra("aspectY", (landscape ? !isPortrait : isPortrait)
                ? height : width);
        intent.putExtra("outputX", (landscape ? !isPortrait : isPortrait)
                ? width : height);
        intent.putExtra("outputY", (landscape ? !isPortrait : isPortrait)
                ? height : width);
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        try {
            mImageTmp.createNewFile();
            mImageTmp.setWritable(true, false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mImageTmp));
            startActivityForResult(intent, request);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mBackground) {
            int indexOf = mBackground.findIndexOfValue(newValue.toString());
            switch (indexOf) {
                //Displays color dialog when user has chosen color fill
                case 0:
                    showDialogInner(DLG_PICK_COLOR);
                    break;
                //Launches intent for user to select an image/crop it to set as background
                case 1:
                    startPictureCrop(REQUEST_PICK_WALLPAPER, false);
                    break;
                //Sets background to default
                case 2:
                    deleteWallpaper(false);
                    deleteWallpaper(true);
                    Settings.System.putString(getContentResolver(),
                            Settings.System.STATUS_BAR_EXPANDED_BG, null);
                    updateCustomBackgroundSummary();
                    break;
            }
            return true;
        } else if (preference == mBackgroundLandscape) {
            int indexOf = mBackgroundLandscape.findIndexOfValue(newValue.toString());
            switch (indexOf) {
                //Launches intent for user to select an image/crop it to set as background
                case 0:
                    startPictureCrop(REQUEST_PICK_WALLPAPER_LANDSCAPE, true);
                    break;
                //Sets background to default
                case 1:
                    deleteWallpaper(true);
                    updateCustomBackgroundSummary();
                    break;
            }
            return true;
        } else if (preference == mBackgroundAlpha) {
            float valNav = Float.parseFloat((String) newValue);
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.STATUS_BAR_EXPANDED_BG_ALPHA, valNav / 100);
            return true;
        }
        return false;
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

        StatusBarExpandedBackground getOwner() {
            return (StatusBarExpandedBackground) getTargetFragment();
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int id = getArguments().getInt("id");
            switch (id) {
                case DLG_PICK_COLOR:
                    final ColorPickerView colorView = new ColorPickerView(getOwner().mActivity);
                    String currentColor = Settings.System.getString(
                            getOwner().getContentResolver(),
                            Settings.System.STATUS_BAR_EXPANDED_BG);
                    if (currentColor != null && currentColor.startsWith("color=")) {
                        int color = Color.parseColor(currentColor.substring("color=".length()));
                        colorView.setColor(color);
                    }
                    colorView.setAlphaSliderVisible(false);

                    return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.status_bar_expanded_bg_custom_bg_dialog_title)
                    .setView(colorView)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.dlg_ok,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            getOwner().deleteWallpaper(false);
                            getOwner().deleteWallpaper(true);
                            Settings.System.putString(
                                getOwner().getContentResolver(),
                                Settings.System.STATUS_BAR_EXPANDED_BG,
                                "color=" + String.format("#%06X",
                                (0xFFFFFF & colorView.getColor())));
                            getOwner().updateCustomBackgroundSummary();
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
