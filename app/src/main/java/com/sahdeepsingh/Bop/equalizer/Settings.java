package com.sahdeepsingh.Bop.equalizer;

public class Settings {
    public static double ratio = 1.0;
    static boolean isEqualizerEnabled = true;
    static boolean isEqualizerReloaded = true;
    static int[] seekbarpos = new int[5];
    static int presetPos;
    static short reverbPreset = -1, bassStrength = -1;
    static EqualizerFragment.EqualizerModel equalizerModel;
}
