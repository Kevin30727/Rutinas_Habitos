package com.rutinas_habitos;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;

public class VibrationHelper {

    public static void vibrarClick(Context context) {
        vibrar(context, 40);
    }

    public static void vibrarExito(Context context) {
        if (context == null) return;
        try {
            Vibrator v = getVibrator(context);
            if (v != null && v.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    long[] pattern = {0, 60, 80, 100};
                    int[] amplitudes = {0, 180, 0, 255};
                    v.vibrate(VibrationEffect.createWaveform(pattern, amplitudes, -1));
                } else {
                    v.vibrate(new long[]{0, 60, 80, 100}, -1);
                }
            }
        } catch (Exception ignored) {}
    }

    public static void vibrarBloqueo(Context context) {
        if (context == null) return;
        try {
            Vibrator v = getVibrator(context);
            if (v != null && v.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(80);
                }
            }
        } catch (Exception ignored) {}
    }

    private static void vibrar(Context context, long ms) {
        if (context == null) return;
        try {
            Vibrator v = getVibrator(context);
            if (v != null && v.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(ms);
                }
            }
        } catch (Exception ignored) {}
    }

    private static Vibrator getVibrator(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager vm = (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            return vm != null ? vm.getDefaultVibrator() : null;
        } else {
            return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }
}
