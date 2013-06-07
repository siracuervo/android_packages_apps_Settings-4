/*
 * Copyright (C) 2013 JellyBeer/BeerGang Project
 * 
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
import android.app.AlertDialog;
import android.database.Cursor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.widget.SeekBarPreference;
import com.android.settings.darkjelly.colorpicker.ColorPickerView;

import java.util.ArrayList;
import java.util.List;

public class NotificationDrawerBackgroundStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "NotificationDrawerBackgroundStyle";

    private static final String PREF_NOTIFICATION_DRAWER_BACKGROUND = "notification_drawer_background";
    private static final String PREF_NOTIFICATION_DRAWER_BACKGROUND_LANDSCAPE = "notification_drawer_background_landscape";
    private static final String PREF_NOTIFICATION_DRAWER_BACKGROUND_ALPHA = "notification_drawer_background_alpha";
    private static final String PREF_NOTIFICATION_DRAWER_ROW_ALPHA = "notification_drawer_row_alpha";

    private ListPreference mNotificationDrawerBackground;
    private ListPreference mNotificationDrawerBackgroundLandscape;
    SeekBarPreference mNotificationDrawerBackgroundAlpha;
    SeekBarPreference mNotificationDrawerRowAlpha;

    private File customnavTemp;
    private File customnavTempLandscape;

    private static final int REQUEST_PICK_WALLPAPER = 201;
    private static final int REQUEST_PICK_WALLPAPER_LANDSCAPE = 202;
    private static final String WALLPAPER_NAME = "notification_wallpaper.jpg";
    private static final String WALLPAPER_NAME_LANDSCAPE = "notification_wallpaper_landscape.jpg";

    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();

        addPreferencesFromResource(R.xml.notification_drawer_background_style);

        customnavTemp = new File(getActivity().getFilesDir()+"/notification_wallpaper_temp.jpg");
        customnavTempLandscape = new File(getActivity().getFilesDir()+"/notification_wallpaper_temp_landscape.jpg");

        mNotificationDrawerBackground = (ListPreference) findPreference(PREF_NOTIFICATION_DRAWER_BACKGROUND);
        mNotificationDrawerBackgroundLandscape = (ListPreference) findPreference(PREF_NOTIFICATION_DRAWER_BACKGROUND_LANDSCAPE);
        mNotificationDrawerBackgroundAlpha = (SeekBarPreference) findPreference(PREF_NOTIFICATION_DRAWER_BACKGROUND_ALPHA);
        mNotificationDrawerRowAlpha = (SeekBarPreference) findPreference(PREF_NOTIFICATION_DRAWER_ROW_ALPHA);

        mNotificationDrawerBackground.setOnPreferenceChangeListener(this);

        mNotificationDrawerBackgroundLandscape.setOnPreferenceChangeListener(this);

        float BackgroundTransparency = 0.1f;
        try{
            BackgroundTransparency = Settings.System.getFloat(getActivity().getContentResolver(), Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA);
        }catch (Exception e) {
            BackgroundTransparency = 0.1f;
            Settings.System.putFloat(getActivity().getContentResolver(), Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA, 0.1f);
        }
        mNotificationDrawerBackgroundAlpha.setInitValue((int) (BackgroundTransparency * 100));
        mNotificationDrawerBackgroundAlpha.setProperty(Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA);
        mNotificationDrawerBackgroundAlpha.setOnPreferenceChangeListener(this);

        float RowTransparency = 0.0f;
        try{
            RowTransparency = Settings.System.getFloat(getActivity().getContentResolver(), Settings.System.NOTIFICATION_DRAWER_ROW_ALPHA);
        }catch (Exception e) {
            RowTransparency = 0.0f;
            Settings.System.putFloat(getActivity().getContentResolver(), Settings.System.NOTIFICATION_DRAWER_ROW_ALPHA, 0.0f);
        }
        mNotificationDrawerRowAlpha.setInitValue((int) (RowTransparency * 100));
        mNotificationDrawerRowAlpha.setProperty(Settings.System.NOTIFICATION_DRAWER_ROW_ALPHA);
        mNotificationDrawerRowAlpha.setOnPreferenceChangeListener(this);

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
                Settings.System.NOTIFICATION_DRAWER_BACKGROUND);
        if (value == null) {
            resId = R.string.notification_drawer_background_default_wallpaper;
            mNotificationDrawerBackground.setValueIndex(2);
            mNotificationDrawerBackgroundLandscape.setEnabled(false);
        } else if (value.isEmpty()) {
            resId = R.string.notification_drawer_background_custom_image;
            mNotificationDrawerBackground.setValueIndex(1);
            mNotificationDrawerBackgroundLandscape.setEnabled(true);
        } else {
            resId = R.string.notification_drawer_background_color_fill;
            mNotificationDrawerBackground.setValueIndex(0);
            mNotificationDrawerBackgroundLandscape.setEnabled(false);
        }
        mNotificationDrawerBackground.setSummary(getResources().getString(resId));

        value = Settings.System.getString(getContentResolver(),
                Settings.System.NOTIFICATION_DRAWER_BACKGROUND_LANDSCAPE);
        if (value == null) {
            resId = R.string.notification_drawer_background_default_wallpaper;
            mNotificationDrawerBackgroundLandscape.setValueIndex(1);
        } else {
            resId = R.string.notification_drawer_background_custom_image;
            mNotificationDrawerBackgroundLandscape.setValueIndex(0);
        }
        mNotificationDrawerBackgroundLandscape.setSummary(getResources().getString(resId));
    }

    public void deleteWallpaper (boolean orientation) {
      File wallpaperToDelete = new File(getActivity().getFilesDir()+"/notification_wallpaper.jpg");
      File wallpaperToDeleteLandscape = new File(getActivity().getFilesDir()+"/notification_wallpaper_landscape.jpg");

      if (wallpaperToDelete.exists() && !orientation) {
         wallpaperToDelete.delete();
      }

      if (wallpaperToDeleteLandscape.exists() && orientation) {
         wallpaperToDeleteLandscape.delete();
      }

      if (orientation) {
         Settings.System.putString(getContentResolver(),
            Settings.System.NOTIFICATION_DRAWER_BACKGROUND_LANDSCAPE, null);
      }
    }

    public void observerResourceHelper() {
       float helper;
       float first = Settings.System.getFloat(getActivity().getContentResolver(),
                    Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA, 0.1f);
        if (first < 0.9f) {
            helper = first + 0.1f;
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA, helper);
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA, first);
        }else {
            helper = first - 0.1f;
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.NOTIFICATION_DRAWER_BACKGROUND, helper);
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA, first);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
          if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK_WALLPAPER) {
              FileOutputStream wallpaperStream = null;
              Settings.System.putString(getContentResolver(),
                      Settings.System.NOTIFICATION_DRAWER_BACKGROUND,"");
              try {
                 wallpaperStream = getActivity().getApplicationContext().openFileOutput(WALLPAPER_NAME,
                         Context.MODE_WORLD_READABLE);
                 Uri selectedImageUri = Uri.fromFile(customnavTemp);
                 Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath());
                 bitmap.compress(Bitmap.CompressFormat.PNG, 100, wallpaperStream);
                 wallpaperStream.close();
                 customnavTemp.delete();
               } catch (Exception e) {
                     Log.e(TAG, e.getMessage(), e);
               }
            }else if (requestCode == REQUEST_PICK_WALLPAPER_LANDSCAPE) {
              FileOutputStream wallpaperStream = null;
              Settings.System.putString(getContentResolver(),
                      Settings.System.NOTIFICATION_DRAWER_BACKGROUND_LANDSCAPE,"");
              try {
                 wallpaperStream = getActivity().getApplicationContext().openFileOutput(WALLPAPER_NAME_LANDSCAPE,
                         Context.MODE_WORLD_READABLE);
                 Uri selectedImageUri = Uri.fromFile(customnavTempLandscape);
                 Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath());
                 bitmap.compress(Bitmap.CompressFormat.PNG, 100, wallpaperStream);
                 wallpaperStream.close();
                 customnavTempLandscape.delete();
               } catch (Exception e) {
                     Log.e(TAG, e.getMessage(), e);
               }
            }
        }
        observerResourceHelper();
        updateCustomBackgroundSummary();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNotificationDrawerBackgroundAlpha) {
            float valNav = Float.parseFloat((String) newValue);
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA, valNav / 100);
            return true;
        }else if (preference == mNotificationDrawerRowAlpha) {
            float valNav = Float.parseFloat((String) newValue);
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.NOTIFICATION_DRAWER_ROW_ALPHA, valNav / 100);
            return true;
        }else if (preference == mNotificationDrawerBackground) {
            int indexOf = mNotificationDrawerBackground.findIndexOfValue(newValue.toString());
            switch (indexOf) {
            //Displays color dialog when user has chosen color fill
            case 0:
                final ColorPickerView colorView = new ColorPickerView(mActivity);
                int currentColor = Settings.System.getInt(getContentResolver(),
                        Settings.System.NOTIFICATION_DRAWER_BACKGROUND, -1);
                if (currentColor != -1) {
                    colorView.setColor(currentColor);
                }
                colorView.setAlphaSliderVisible(false);
                new AlertDialog.Builder(mActivity)
                .setTitle(R.string.notification_drawer_background_custom_dialog_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Settings.System.putInt(getContentResolver(), Settings.System.NOTIFICATION_DRAWER_BACKGROUND, colorView.getColor());
                        updateCustomBackgroundSummary();
                        deleteWallpaper(false);
                        deleteWallpaper(true);
                        observerResourceHelper();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setView(colorView).show();
                break;
            //Launches intent for user to select an image/crop it to set as background
            case 1:
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                int width = display.getWidth();
                int height = display.getHeight();
                Rect rect = new Rect();
                Window window = getActivity().getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(rect);
                int statusBarHeight = rect.top;
                int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
                int titleBarHeight = contentViewTop - statusBarHeight;
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra("crop", "true");
                boolean isPortrait = getResources()
                        .getConfiguration().orientation
                        == Configuration.ORIENTATION_PORTRAIT;
                intent.putExtra("aspectX", isPortrait ? width : height - titleBarHeight);
                intent.putExtra("aspectY", isPortrait ? height - titleBarHeight : width);
                intent.putExtra("outputX", isPortrait ? width : height);
                intent.putExtra("outputY", isPortrait ? height : width);
                intent.putExtra("scale", true);
                intent.putExtra("scaleUpIfNeeded", true);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
                try {
                     customnavTemp.createNewFile();
                     customnavTemp.setWritable(true, false);
                     intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(customnavTemp));
                     startActivityForResult(intent, REQUEST_PICK_WALLPAPER);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                break;
            //Sets background to default
            case 2:
                Settings.System.putString(getContentResolver(),
                        Settings.System.NOTIFICATION_DRAWER_BACKGROUND, null);
                deleteWallpaper(false);
                deleteWallpaper(true);
                observerResourceHelper();
                updateCustomBackgroundSummary();
                break;
            }
            return true;
        }else if (preference == mNotificationDrawerBackgroundLandscape) {

            int indexOf = mNotificationDrawerBackgroundLandscape.findIndexOfValue(newValue.toString());
            switch (indexOf) {
            //Launches intent for user to select an image/crop it to set as background
            case 0:
                Display display = getActivity().getWindowManager().getDefaultDisplay();
                int width = display.getWidth();
                int height = display.getHeight();
                Rect rect = new Rect();
                Window window = getActivity().getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(rect);
                int statusBarHeight = rect.top;
                int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
                int titleBarHeight = contentViewTop - statusBarHeight;
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra("crop", "true");
                boolean isPortrait = getResources()
                        .getConfiguration().orientation
                        == Configuration.ORIENTATION_PORTRAIT;
                intent.putExtra("aspectX", isPortrait ? height - titleBarHeight : width);
                intent.putExtra("aspectY", isPortrait ? width : height - titleBarHeight);
                intent.putExtra("outputX", isPortrait ? height : width);
                intent.putExtra("outputY", isPortrait ? width : height);
                intent.putExtra("scale", true);
                intent.putExtra("scaleUpIfNeeded", true);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
                try {
                     customnavTempLandscape.createNewFile();
                     customnavTempLandscape.setWritable(true, false);
                     intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(customnavTempLandscape));
                     startActivityForResult(intent, REQUEST_PICK_WALLPAPER_LANDSCAPE);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                break;
            //Sets background to default
            case 1:
                deleteWallpaper(true);
                observerResourceHelper();
                updateCustomBackgroundSummary();
                break;
            }
            return true;
        }
        return false;
    }
}

