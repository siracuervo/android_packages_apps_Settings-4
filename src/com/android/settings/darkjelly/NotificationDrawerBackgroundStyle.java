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
import com.android.settings.darkjelly.colorpicker.ColorPickerPreference;

import java.util.ArrayList;
import java.util.List;

public class NotificationDrawerBackgroundStyle extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "NotificationDrawerBackgroundStyle";

    private static final String PREF_NOTIFICATION_DRAWER_BACKGROUND_COLOR = "notification_drawer_background_color";
    private static final String PREF_NOTIFICATION_DRAWER_BACKGROUND_ALPHA = "notification_drawer_background_alpha";
    private static final String PREF_NOTIFICATION_DRAWER_ROW_ALPHA = "notification_drawer_row_alpha";

    private ColorPickerPreference mNotificationDrawerBackgroundColor;
    private SeekBarPreference mNotificationDrawerBackgroundAlpha;
    private SeekBarPreference mNotificationDrawerRowAlpha;

    private Activity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = getActivity();

        addPreferencesFromResource(R.xml.notification_drawer_background_style);

        mNotificationDrawerBackgroundColor = (ColorPickerPreference) findPreference(PREF_NOTIFICATION_DRAWER_BACKGROUND_COLOR);
        int intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.NOTIFICATION_DRAWER_BACKGROUND, 0xe60e0e0e); 
        mNotificationDrawerBackgroundColor.setNewPreviewColor(intColor);
        String hexColor = String.format("#%08x", (0xffffffff & intColor));
        mNotificationDrawerBackgroundColor.setSummary(hexColor);
        mNotificationDrawerBackgroundColor.setOnPreferenceChangeListener(this);

        mNotificationDrawerBackgroundAlpha = (SeekBarPreference) findPreference(PREF_NOTIFICATION_DRAWER_BACKGROUND_ALPHA);
        float BackgroundTransparency = 0.0f;
        try{
            BackgroundTransparency = Settings.System.getFloat(getActivity().getContentResolver(), Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA);
        }catch (Exception e) {
            BackgroundTransparency = 0.0f;
            Settings.System.putFloat(getActivity().getContentResolver(), Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA, 0.1f);
        }
        mNotificationDrawerBackgroundAlpha.setInitValue((int) (BackgroundTransparency * 100));
        mNotificationDrawerBackgroundAlpha.setProperty(Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA);
        mNotificationDrawerBackgroundAlpha.setOnPreferenceChangeListener(this);

        mNotificationDrawerRowAlpha = (SeekBarPreference) findPreference(PREF_NOTIFICATION_DRAWER_ROW_ALPHA);
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
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mNotificationDrawerBackgroundColor) {
            String hex = ColorPickerPreference.convertToARGB(Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NOTIFICATION_DRAWER_BACKGROUND, intHex);
            preference.setSummary(hex);
            return true;
        } else if (preference == mNotificationDrawerBackgroundAlpha) {
            float valNav = Float.parseFloat((String) newValue);
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.NOTIFICATION_DRAWER_BACKGROUND_ALPHA, valNav / 100);
            return true;
        } else if (preference == mNotificationDrawerRowAlpha) {
            float valNav = Float.parseFloat((String) newValue);
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.NOTIFICATION_DRAWER_ROW_ALPHA, valNav / 100);
            return true;
        }
        return false;
    }
}

