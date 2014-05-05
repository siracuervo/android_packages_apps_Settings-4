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

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.admin.DevicePolicyManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.util.Log;

import com.android.internal.util.darkkat.DeviceUtils;
import com.android.internal.widget.LockPatternUtils;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class LockscreenWidgetsSettings extends SettingsPreferenceFragment implements
        OnPreferenceChangeListener {

    private static final String TAG = "LockscreenWidgetsSettings";

    private static final String PREF_LOCKSCREEN_WIDGETS_CAT_OPTIONS =
            "lockscreen_widgets_cat_options";
    private static final String PREF_LOCKSCREEN_WIDGETS_CAT_COLOR =
            "lockscreen_widgets_cat_color";
    private static final String PREF_LOCKSCREEN_ENABLE_WIDGETS =
            "lockscreen_enable_widgets";
    private static final String PREF_LOCKSCREEN_MAXIMIMIZE_WIDGETS =
            "lockscreen_maximize_widgets";
    private static final String PREF_LOCKSCREEN_ENABLE_CAMERA =
            "lockscreen_enable_camera";
    private static final String PREF_LOCKSCREEN_USE_WIDGET_CAROUSEL =
            "lockscreen_use_widget_container_carousel";
    private static final String PREF_LOCKSCREEN_DISABLE_WIDGET_FRAME =
            "lockscreen_disable_widget_frame";
    private static final String PREF_LOCKSCREEN_FRAME_COLOR =
            "lockscreen_frame_color";

    private static final int DEFAULT_FRAME_COLOR = 0xffffffff;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DLG_RESET = 0;

    private CheckBoxPreference mEnableWidgets;
    private CheckBoxPreference mMaximizeWidgets;
    private CheckBoxPreference mEnableCameraWidget;
    private CheckBoxPreference mUseWidgetCarousel;
    private CheckBoxPreference mDisableWidgetFrame;
    private ColorPickerPreference mFrameColor;

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

        addPreferencesFromResource(R.xml.lockscreen_widgets_settings);
        mResolver = getActivity().getContentResolver();

        final boolean widgetsEnabled =
                new LockPatternUtils(getActivity()).getWidgetsEnabled();

        mEnableWidgets =
                (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_ENABLE_WIDGETS);
        mEnableWidgets.setChecked(widgetsEnabled);
        mEnableWidgets.setOnPreferenceChangeListener(this);

        // Remove Maximize widgets checkbox on hybrid/tablet
        PreferenceCategory catOptions =
                (PreferenceCategory) findPreference(PREF_LOCKSCREEN_WIDGETS_CAT_OPTIONS);
        mMaximizeWidgets =
                (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_MAXIMIMIZE_WIDGETS);
        if (DeviceUtils.isPhone(getActivity())) {
            mMaximizeWidgets.setChecked(Settings.System.getInt(mResolver,
                   Settings.System.LOCKSCREEN_MAXIMIZE_WIDGETS, 0) == 1);
            mMaximizeWidgets.setOnPreferenceChangeListener(this);
        } else {
            catOptions.removePreference(mMaximizeWidgets);
        }

        // Show or hide camera widget settings based on device and
        // on widget enabled/disabled state
        mEnableCameraWidget =
                (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_ENABLE_CAMERA);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)
                 || Camera.getNumberOfCameras() == 0
                 || isCameraDisabledByDpm()  || !widgetsEnabled) {
            catOptions.removePreference(mEnableCameraWidget);
        } else {
            mEnableCameraWidget.setChecked(Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_ENABLE_CAMERA, 1) == 1);
            mEnableCameraWidget.setOnPreferenceChangeListener(this);
        }

        // Show or hide widget carousel settings based on widget enabled/disabled state
        mUseWidgetCarousel =
                (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_USE_WIDGET_CAROUSEL);
        if (widgetsEnabled) {
            mUseWidgetCarousel.setChecked(Settings.System.getInt(getContentResolver(),
                    Settings.System.LOCKSCREEN_USE_WIDGET_CONTAINER_CAROUSEL, 0) == 1);
            mUseWidgetCarousel.setOnPreferenceChangeListener(this);
        } else {
            catOptions.removePreference(mUseWidgetCarousel);
        }

        boolean widgetFrameDisabled = Settings.System.getInt(mResolver,
               Settings.System.LOCKSCREEN_WIDGET_FRAME_ENABLED, 0) == 1;
        mDisableWidgetFrame =
                (CheckBoxPreference) findPreference(PREF_LOCKSCREEN_DISABLE_WIDGET_FRAME);
        mDisableWidgetFrame.setChecked(widgetFrameDisabled);
        mDisableWidgetFrame.setOnPreferenceChangeListener(this);

        // Show or hide Frame color settings based on widget frame enabled/disabled state
        PreferenceCategory catColor =
                (PreferenceCategory) findPreference(PREF_LOCKSCREEN_WIDGETS_CAT_COLOR);
        mFrameColor =
                (ColorPickerPreference) findPreference(PREF_LOCKSCREEN_FRAME_COLOR);
        if (!widgetFrameDisabled) {
            int color = Settings.System.getInt(mResolver,
                    Settings.System.LOCKSCREEN_FRAME_COLOR, DEFAULT_FRAME_COLOR);
            mFrameColor.setNewPreviewColor(color);
            String hexColor = String.format("#%08x", (0xffffffff & color));
            mFrameColor.setSummary(hexColor);
            mFrameColor.setOnPreferenceChangeListener(this);
        } else {
            catColor.removePreference(mFrameColor);
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
                showDialogInner(DLG_RESET);
                return true;
             default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean value;

        if (preference == mEnableWidgets) {
            new LockPatternUtils(getActivity()).setWidgetsEnabled(
                    (Boolean) newValue);
            mEnableWidgets.setSummary((Boolean) newValue ?
                    R.string.enabled : R.string.disabled);
            refreshSettings();
            return true;
        } else if (preference == mMaximizeWidgets) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_MAXIMIZE_WIDGETS,
                    value ? 1 : 0);
            return true;
        } else if (preference == mEnableCameraWidget) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_ENABLE_CAMERA,
                    value ? 1 : 0);
            return true;
        } else if (preference == mUseWidgetCarousel) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_USE_WIDGET_CONTAINER_CAROUSEL,
                    value ? 1 : 0);
            return true;
        } else if (preference == mDisableWidgetFrame) {
            value = (Boolean) newValue;
            Settings.System.putInt(mResolver,
                    Settings.System.LOCKSCREEN_WIDGET_FRAME_ENABLED,
                    value ? 1 : 0);
            refreshSettings();
            return true;
        } else if (preference == mFrameColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(mResolver,
                Settings.System.LOCKSCREEN_FRAME_COLOR, intHex);
            preference.setSummary(hex);
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

        LockscreenWidgetsSettings getOwner() {
            return (LockscreenWidgetsSettings) getTargetFragment();
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
                                    Settings.System.LOCKSCREEN_FRAME_COLOR,
                                    DEFAULT_FRAME_COLOR);
                            getOwner().refreshSettings();
                        }
                    })
                    .setPositiveButton(R.string.dlg_reset_darkkat,
                        new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Settings.System.putInt(getOwner().mResolver,
                                    Settings.System.LOCKSCREEN_FRAME_COLOR,
                                    0xff33b5e5);
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

    private boolean isCameraDisabledByDpm() {
        final DevicePolicyManager dpm =
                (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (dpm != null) {
            try {
                final int userId = ActivityManagerNative.getDefault().getCurrentUser().id;
                final int disabledFlags = dpm.getKeyguardDisabledFeatures(null, userId);
                final  boolean disabledBecauseKeyguardSecure =
                        (disabledFlags & DevicePolicyManager.KEYGUARD_DISABLE_SECURE_CAMERA) != 0;
                return dpm.getCameraDisabled(null) || disabledBecauseKeyguardSecure;
            } catch (RemoteException e) {
                Log.e(TAG, "Can't get userId", e);
            }
        }
        return false;
    }
}
