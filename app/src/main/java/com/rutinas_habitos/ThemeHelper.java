package com.rutinas_habitos;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ThemeHelper {

    public static final String PREFS_NAME = "rutinas_pastel_config";
    public static final String KEY_PALETA = "paleta_seleccionada";

    public static String getPaletaActual(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_PALETA, "menta");
    }

    public static void guardarPaleta(Context context, String paleta) {
        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_PALETA, paleta).apply();
    }

    public static int getColorPrimario(Context context) {
        String p = getPaletaActual(context);
        if ("sakura".equals(p)) return Color.parseColor("#F8C8DC");
        if ("oceano".equals(p)) return Color.parseColor("#B8D8E8");
        if ("lavanda".equals(p)) return Color.parseColor("#D7D9F8");
        return Color.parseColor("#B8E0D2");
    }

    public static int getColorAcentoOscuro(Context context) {
        String p = getPaletaActual(context);
        if ("sakura".equals(p)) return Color.parseColor("#E8A9C2");
        if ("oceano".equals(p)) return Color.parseColor("#6DAFD0");
        if ("lavanda".equals(p)) return Color.parseColor("#8A8FD4");
        return Color.parseColor("#4CA977");
    }

    public static int getColorFondoSuave(Context context) {
        String p = getPaletaActual(context);
        if ("sakura".equals(p)) return Color.parseColor("#FDEDF3");
        if ("oceano".equals(p)) return Color.parseColor("#EBF4F9");
        if ("lavanda".equals(p)) return Color.parseColor("#F1F2FD");
        return Color.parseColor("#EAF7F2");
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
}
