package com.sahdeepsingh.Bop.controls;

import android.media.AudioManager;
import android.util.Log;

import java.lang.reflect.Method;


//NOTE (READ THIS PLEASE):
//
//This file was directly copied from the Android
//sample project "Random Music Player".
//
//I use it to implement the Lock Screen "Widget"
//(control the music player on the lock-screen)
//
//Take a look at my service that plays songs to
//see how I use this class.


/**
 * Contains methods to handle registering/unregistering
 * remote control clients.
 * <p>
 * These methods only run on ICS devices.
 * On previous devices, all methods are no-ops.
 */
@SuppressWarnings({"rawtypes"})
public class RemoteControlHelper {

    private static final String TAG = "RemoteControlHelper";

    private static boolean sHasRemoteControlAPIs = false;

    private static Method sRegisterRemoteControlClientMethod;
    private static Method sUnregisterRemoteControlClientMethod;

    static {
        try {
            ClassLoader classLoader = RemoteControlHelper.class.getClassLoader();
            Class sRemoteControlClientClass =
                    RemoteControlClientCompat.getActualRemoteControlClientClass(classLoader);

            sRegisterRemoteControlClientMethod = AudioManager.class.getMethod(
                    "registerRemoteControlClient", sRemoteControlClientClass);

            sUnregisterRemoteControlClientMethod = AudioManager.class.getMethod(
                    "unregisterRemoteControlClient", sRemoteControlClientClass);

            sHasRemoteControlAPIs = true;

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalArgumentException | SecurityException e) {
            // Silently fail when running on an OS before ICS.
        }
    }

    public static void registerRemoteControlClient(AudioManager audioManager,
                                                   RemoteControlClientCompat remoteControlClient) {

        if (!sHasRemoteControlAPIs) {
            return;
        }

        try {
            sRegisterRemoteControlClientMethod.invoke(audioManager,
                    remoteControlClient.getActualRemoteControlClientObject());

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static void unregisterRemoteControlClient(AudioManager audioManager,
                                                     RemoteControlClientCompat remoteControlClient) {

        if (!sHasRemoteControlAPIs) {
            return;
        }

        try {
            sUnregisterRemoteControlClientMethod.invoke(audioManager,
                    remoteControlClient.getActualRemoteControlClientObject());

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}

