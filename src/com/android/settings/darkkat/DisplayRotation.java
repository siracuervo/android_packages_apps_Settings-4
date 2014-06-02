/*
 * Copyright (C) 2013 DarkKat
 *
 * Copyright (C) 2012 The CyanogenMod Project
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

import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.internal.util.darkkat.DeviceUtils;
import com.android.internal.view.RotationPolicy;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class DisplayRotation extends SettingsPreferenceFragment {
    private static final String TAG = "DisplayRotation";

    private static final String KEY_ACCELEROMETER = "accelerometer";
    private static final String KEY_LOCKSCREEN_ROTATION = "lockscreen_rotation";
    private static final String ROTATION_0_PREF = "display_rotation_0";
    private static final String ROTATION_90_PREF = "display_rotation_90";
    private static final String ROTATION_180_PREF = "display_rotation_180";
    private static final String ROTATION_270_PREF = "display_rotation_270";
    private static final String KEY_SWAP_VOLUME_BUTTONS = "swap_volume_buttons";

    private CheckBoxPreference mAccelerometer;
    private CheckBoxPreference mLockscreenRotation;
    private CheckBoxPreference mRotation0Pref;
    private CheckBoxPreference mRotation90Pref;
    private CheckBoxPreference mRotation180Pref;
    private CheckBoxPreference mRotation270Pref;
    private CheckBoxPreference mSwapVolumeButtons;

    public static final int ROTATION_0_MODE = 1;
    public static final int ROTATION_90_MODE = 2;
    public static final int ROTATION_180_MODE = 4;
    public static final int ROTATION_270_MODE = 8;

    private ContentObserver mAccelerometerRotationObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            refreshSettings();
        }
    };

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

        addPreferencesFromResource(R.xml.display_rotation);

        PreferenceScreen prefSet = getPreferenceScreen();

        mAccelerometer = (CheckBoxPreference) findPreference(KEY_ACCELEROMETER);
        mAccelerometer.setChecked(!RotationPolicy.isRotationLocked(getActivity()));
        mAccelerometer.setPersistent(false);

        PreferenceCategory catDisplayRotation =
                (PreferenceCategory) findPreference("display_rotation_category");
        mLockscreenRotation = (CheckBoxPreference) findPreference(KEY_LOCKSCREEN_ROTATION);
        mRotation0Pref = (CheckBoxPreference) findPreference(ROTATION_0_PREF);
        mRotation90Pref = (CheckBoxPreference) findPreference(ROTATION_90_PREF);
        mRotation180Pref = (CheckBoxPreference) findPreference(ROTATION_180_PREF);
        mRotation270Pref = (CheckBoxPreference) findPreference(ROTATION_270_PREF);
        mSwapVolumeButtons = (CheckBoxPreference) findPreference(KEY_SWAP_VOLUME_BUTTONS);
        if (!RotationPolicy.isRotationLocked(getActivity())) {
            mLockscreenRotation.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_ROTATION, 0) == 1);

            int mode = Settings.System.getInt(getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION_ANGLES,
                    ROTATION_0_MODE | ROTATION_90_MODE | ROTATION_270_MODE);

            mRotation0Pref.setChecked((mode & ROTATION_0_MODE) != 0);
            mRotation90Pref.setChecked((mode & ROTATION_90_MODE) != 0);
            mRotation180Pref.setChecked((mode & ROTATION_180_MODE) != 0);
            mRotation270Pref.setChecked((mode & ROTATION_270_MODE) != 0);

            int swapVolumeKeys = Settings.System.getInt(getContentResolver(),
                    Settings.System.SWAP_VOLUME_KEYS_ON_ROTATION, 0);
            mSwapVolumeButtons.setChecked(swapVolumeKeys > 0);
        } else {
            removePreference(KEY_LOCKSCREEN_ROTATION);
            catDisplayRotation.removePreference(mRotation0Pref);
            catDisplayRotation.removePreference(mRotation90Pref);
            catDisplayRotation.removePreference(mRotation180Pref);
            catDisplayRotation.removePreference(mRotation270Pref);
            removePreference(KEY_SWAP_VOLUME_BUTTONS);
            removePreference("display_rotation_category");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getContentResolver().registerContentObserver(
                Settings.System.getUriFor(Settings.System.ACCELEROMETER_ROTATION), true,
                mAccelerometerRotationObserver);

        refreshSettings();
    }

    @Override
    public void onPause() {
        super.onPause();

        getContentResolver().unregisterContentObserver(mAccelerometerRotationObserver);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mAccelerometer) {
            RotationPolicy.setRotationLockForAccessibility(getActivity(),
                    !mAccelerometer.isChecked());
            return true;
        } else if (preference == mLockscreenRotation) {
            boolean value = mLockscreenRotation.isChecked();
            Settings.System.putInt(getContentResolver(), Settings.System.LOCKSCREEN_ROTATION,
                    value ? 1 : 0);
            return true;
        } else if (preference == mRotation0Pref ||
                preference == mRotation90Pref ||
                preference == mRotation180Pref ||
                preference == mRotation270Pref) {
            int mode = 0;
            if (mRotation0Pref.isChecked())
                mode |= ROTATION_0_MODE;
            if (mRotation90Pref.isChecked())
                mode |= ROTATION_90_MODE;
            if (mRotation180Pref.isChecked())
                mode |= ROTATION_180_MODE;
            if (mRotation270Pref.isChecked())
                mode |= ROTATION_270_MODE;
            if (mode == 0) {
                mode |= ROTATION_0_MODE;
                mRotation0Pref.setChecked(true);
            }
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION_ANGLES, mode);
            return true;
        } else if (preference == mSwapVolumeButtons) {
            int value = mSwapVolumeButtons.isChecked()
                    ? (DeviceUtils.isTablet(getActivity()) ? 2 : 1) : 0;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.SWAP_VOLUME_KEYS_ON_ROTATION, value);
            return true;
        }

        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}