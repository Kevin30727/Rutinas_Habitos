package com.rutinas_habitos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ThemeHelper {

    public static final String PREFS_NAME = "rutinas_pastel_config";
    public static final String KEY_PALETA = "paleta_seleccionada";
    public static final String KEY_DESCANSO_VISUAL = "modo_descanso_visual";

    public static String getPaletaActual(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_PALETA, "menta");
    }

    public static void guardarPaleta(Context context, String paleta) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_PALETA, paleta).apply();
    }

    public static boolean isDescansoVisual(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_DESCANSO_VISUAL, false);
    }

    public static void guardarDescansoVisual(Context context, boolean activo) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(KEY_DESCANSO_VISUAL, activo).apply();
        AppCompatDelegate.setDefaultNightMode(activo ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    public static int getThemeResId(Context context) {
        String p = getPaletaActual(context);
        if ("sakura".equals(p)) return R.style.Theme_Rutinas_Habitos_Sakura;
        if ("oceano".equals(p)) return R.style.Theme_Rutinas_Habitos_Oceano;
        if ("lavanda".equals(p)) return R.style.Theme_Rutinas_Habitos_Lavanda;
        return R.style.Theme_Rutinas_Habitos_Menta;
    }

    public static void aplicarTema(Activity activity) {
        if (activity == null) return;
        boolean descanso = isDescansoVisual(activity);
        int currentNight = AppCompatDelegate.getDefaultNightMode();
        int expectedNight = descanso ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        if (currentNight != expectedNight) {
            AppCompatDelegate.setDefaultNightMode(expectedNight);
        }
        activity.setTheme(getThemeResId(activity));
    }

    public static int getColorPrimario(Context context) {
        String p = getPaletaActual(context);
        if ("sakura".equals(p)) return Color.parseColor("#FF4081");
        if ("oceano".equals(p)) return Color.parseColor("#00B0FF");
        if ("lavanda".equals(p)) return Color.parseColor("#7C4DFF");
        return Color.parseColor("#00C853");
    }

    public static int getColorAcentoOscuro(Context context) {
        String p = getPaletaActual(context);
        if ("sakura".equals(p)) return Color.parseColor("#C2185B");
        if ("oceano".equals(p)) return Color.parseColor("#0288D1");
        if ("lavanda".equals(p)) return Color.parseColor("#512DA8");
        return Color.parseColor("#007E33");
    }

    public static int getColorFondoSuave(Context context) {
        String p = getPaletaActual(context);
        if ("sakura".equals(p)) return Color.parseColor("#FFE4EC");
        if ("oceano".equals(p)) return Color.parseColor("#E1F5FE");
        if ("lavanda".equals(p)) return Color.parseColor("#EDE7F6");
        return Color.parseColor("#E8F5E9");
    }

    public static void aplicarPaletaEnVista(Context context, FloatingActionButton fab, View bannerOHeader) {
        int colorPrimario = getColorPrimario(context);
        int colorFondoSuave = getColorFondoSuave(context);

        if (fab != null) {
            fab.setBackgroundTintList(ColorStateList.valueOf(colorPrimario));
        }

        if (bannerOHeader != null) {
            bannerOHeader.setBackgroundTintList(ColorStateList.valueOf(colorFondoSuave));
        }
    }

    public static void aplicarEstiloBottomNav(Context context, BottomNavigationView nav) {
        if (nav == null) return;
        int colorAcento = getColorPrimario(context);
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{-android.R.attr.state_checked}
        };
        int[] colors = new int[]{
                colorAcento,
                Color.parseColor("#8D909F")
        };
        ColorStateList csl = new ColorStateList(states, colors);
        nav.setItemIconTintList(csl);
        nav.setItemTextColor(csl);
    }
}
