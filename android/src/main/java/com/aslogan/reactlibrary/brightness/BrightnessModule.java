package com.aslogan.reactlibrary.brightness;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.WindowManager;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class BrightnessModule extends ReactContextBaseJavaModule implements ActivityEventListener {
    /**
     * The name of the module for the JS context to reference.
     */
    public static final String MODULE_NAME = "Brightness";

    private static final String PERMISSION_EVENT_NAME = "screenBrightnessPermission";
    private static final int BRIGHTNESS_MAX = 255;
    private static final int BRIGHTNESS_MIN = 0;
    private static final int writeSettingsRequestCode = 4411;

    /**
     * Constructor
     *
     * @param reactApplicationContext The application context provided by the
     *                                ReactPackage.
     */
    public BrightnessModule(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
        reactApplicationContext.addActivityEventListener(this);
    }

    /**
     * Gets the name of the module for the JS context to reference.
     *
     * @return The module name.
     */
    @Override
    public String getName() {
        return MODULE_NAME;
    }

    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
        if (requestCode == writeSettingsRequestCode) {
            onPermissionResult();
        }
    }

    public void onNewIntent(Intent intent) {

    }

    /**
     * Called by the main activity when the ACTION_MANAGE_WRITE_SETTINGS result is
     * received. Emits the result into the JS context.
     */
    public void onPermissionResult() {
        WritableMap payload = new WritableNativeMap();
        boolean hasPermission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && Settings.System.canWrite(getReactApplicationContext());

        payload.putBoolean("hasPermission", hasPermission);

        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(PERMISSION_EVENT_NAME, payload);
    }

    /**
     * Returns whether the device has granted the application permission to write
     * settings.
     *
     * @return True if WRITE_SETTINGS are granted.
     */
    private boolean hasSettingsPermission() {
        // By default, Android versions earlier than 6 have permission.
        boolean hasPermission = true;

        // Check for permisions if >= Android 6
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasPermission = Settings.System.canWrite(getReactApplicationContext());
        }

        return hasPermission;
    }

    /**
     * Invokes the request permission activity to request access for WRITE_SETTINGS.
     */
    private void requestSettingsPermission() {
        ReactApplicationContext application = getReactApplicationContext();

        if (!hasSettingsPermission()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                    Uri.parse("package:" + application.getPackageName()));
            application.startActivityForResult(intent, writeSettingsRequestCode, null);
        }
    }

    /**
     * Gets the brightness level of the device settings.
     *
     * @return The brightness level.
     */
    private Float getSystemBrightness() {
        Float brightness = null;
        Integer sysBrightness;
        try {
            sysBrightness = Settings.System.getInt(getReactApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            sysBrightness = null;
        }
        if (sysBrightness != null && sysBrightness > 1) {
            brightness = (float)sysBrightness/(float)BRIGHTNESS_MAX;
        } else {
            brightness = (float)sysBrightness;
        }
        return brightness;
    }

    /**
     * Sets the brightness level to the device settings.
     *
     * @param brightness The brightness level between 0-255.
     * @return True if the brightness has been set.
     */
    private boolean setSystemBrightness(int brightness) {
        if (hasSettingsPermission()) {
            // ensure brightness is bound between range 0-255
            brightness = Math.max(BRIGHTNESS_MIN, Math.min(brightness, BRIGHTNESS_MAX));
            Settings.System.putInt(getReactApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
            Settings.System.putInt(getReactApplicationContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                    brightness);
            return true;
        }
        return false;
    }

    /**
     * Determines if the application has been granted WRITE_SETTINGS permissions.
     * This method is callable from the JS context.
     *
     * @param promise A promise resolving if the application has WRITE_SETTINGS
     *                granted.
     */
    @ReactMethod
    public void hasPermission(final Promise promise) {
        promise.resolve(hasSettingsPermission());
    }

    /**
     * Invokes the permission request flow. This method is callable from the JS
     * context.
     */
    @ReactMethod
    public void requestPermission(final Promise promise) {
        requestSettingsPermission();
        promise.resolve(true);
    }

    /**
     * Updates the device brightness. This method is callable from the JS context.
     *
     * @param brightness The brightness level between 0-1.
     * @param promise    A promise resolving if the brightness was updated.
     */
    @ReactMethod
    public void setSystemBrightness(float brightness, final Promise promise) {
        if (setSystemBrightness((int) (brightness * BRIGHTNESS_MAX))) {
            promise.resolve(brightness);
        } else {
            promise.reject(new Error("Unable to set system brightness"));
        }
    }

    /**
     * Gets the brightness level of the device. This method is callable from the JS
     * context.
     *
     * @param promise A promise resolving the brightness level.
     */
    @ReactMethod
    public void getSystemBrightness(final Promise promise) {
        promise.resolve(getSystemBrightness());
    }

    /**
     * Gets the application brightness level. This method is callable from the JS
     * context.
     *
     * @param promise A promise resolving the app brightness level.
     */
    @ReactMethod
    public void getAppBrightness(Promise promise) {
        Activity activity = getCurrentActivity();
        if (activity != null) {
            float brightness = getCurrentActivity().getWindow().getAttributes().screenBrightness;
            promise.resolve(brightness);
        } else {
            promise.reject(new Error("Unable to access the current window"));
        }
    }

    /**
     * Sets the application brightness level. This method is callable from the JS
     * context.
     *
     * @param brightness The brightness level.
     * @param promise    A promise resolving the updated brightness level.
     */
    @ReactMethod
    public void setAppBrightness(final float brightness, final Promise promise) {
        final Activity activity = getCurrentActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                    lp.screenBrightness = brightness;
                    activity.getWindow().setAttributes(lp);
                    promise.resolve(brightness);
                }
            });
        } else {
            promise.reject(new Error("Unable to access the current window"));
        }
    }

    @ReactMethod
    public void getBrightnessMode(final Promise promise) {
        Integer mode;
        try {
            mode = Settings.System.getInt(getReactApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Settings.SettingNotFoundException e) {
            mode = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        }
        if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL) {
            promise.resolve("manual");
        } else {
            promise.resolve("auto");
        }
    }

    @ReactMethod
    public void setBrightnessMode(final String mode, final Promise promise) {
        if (hasSettingsPermission()) {
            // ensure brightness is bound between range 0-255
            int writeMode = Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
            if ("manual" == mode) {
                writeMode = Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL;
            }
            Settings.System.putInt(getReactApplicationContext().getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, writeMode);
            promise.resolve(true);
        } else {
            promise.resolve(false);
        }
    }

    /**
     * Gets the brightness level of the device. This method is callable from the JS
     * context.
     *
     * @deprecated Use {@link #getSystemBrightness()} instead.
     * @param promise A promise resolving the brightness level.
     */
    @ReactMethod
    public void getBrightness(final Promise promise) {
        getSystemBrightness(promise);
    }

    /**
     * Updates the device brightness. This method is callable from the JS context.
     *
     * @deprecated Use {@link #setSystemBrightness(int)} instead.
     * @param brightness The brightness level between 0-1.
     * @param promise    A promise resolving if the brightness was updated.
     */
    @ReactMethod
    public void setBrightness(float brightness, final Promise promise) {
        setSystemBrightness(brightness, promise);
    }
}
