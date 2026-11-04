package com.rutinas_habitos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.List;

public class HabitoReminderReceiver extends BroadcastReceiver {

    private static final String TAG = "HabitoReminderReceiver";

    public static final String ACTION_CANCELAR_NOTIFICACION = "com.rutinas_habitos.ACTION_CANCELAR_NOTIFICACION";

    public static final String EXTRA_HABITO_ID = "extra_habito_id";
    public static final String EXTRA_HABITO_NOMBRE = "extra_habito_nombre";
    public static final String EXTRA_TIPO_SONIDO = "extra_tipo_sonido";
    public static final String EXTRA_DURACION_SEGUNDOS = "extra_duracion_segundos";
    public static final String EXTRA_HORA = "extra_hora";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (context == null || intent == null) return;

        if (ACTION_CANCELAR_NOTIFICACION.equals(intent.getAction())) {
            long habitoId = intent.getLongExtra(EXTRA_HABITO_ID, -1);
            Log.d(TAG, "Cancelando notificación y silenciando música para hábito id=" + habitoId);
            try {
                NotificationHelper.detenerAudio();
                NotificationManagerCompat manager = NotificationManagerCompat.from(context);
                if (habitoId > 0) {
                    manager.cancel((int) habitoId);
                } else {
                    manager.cancelAll();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error al cancelar notificación: " + e.getMessage());
            }
            return;
        }

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            try {
                DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
                SessionManager sessionManager = new SessionManager(context);
                long userId = sessionManager.getUserId();
                if (userId <= 0) userId = 1;
                List<Habito> habitos = dbHelper.obtenerHabitosPorUsuario(userId);
                for (Habito h : habitos) {
                    if (h.isRecordatorioActivo()) {
                        NotificationHelper.programarAlarmaHabito(context, h);
                    }
                }
                Log.d(TAG, "Alarmas de hábitos restauradas tras reinicio del sistema");
            } catch (Exception e) {
                Log.e(TAG, "Error reprogramando alarmas tras reinicio: " + e.getMessage());
            }
            return;
        }

        SharedPreferences configPrefs = context.getSharedPreferences(ThemeHelper.PREFS_NAME, Context.MODE_PRIVATE);
        boolean notificacionesActivas = configPrefs.getBoolean("notificaciones_activas", true);
        if (!notificacionesActivas) {
            Log.d(TAG, "Notificaciones pausadas desde configuración general.");
            return;
        }

        long habitoId = intent.getLongExtra(EXTRA_HABITO_ID, -1);
        String nombre = intent.getStringExtra(EXTRA_HABITO_NOMBRE);
        String tipoSonido = intent.getStringExtra(EXTRA_TIPO_SONIDO);
        int duracionSegundos = intent.getIntExtra(EXTRA_DURACION_SEGUNDOS, 30);
        String hora = intent.getStringExtra(EXTRA_HORA);

        DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
        Habito habito = dbHelper.obtenerHabitoPorId(habitoId);

        boolean tocaHoy = true;
        if (habito != null) {
            tocaHoy = esDiaActivo(habito.getDiasSemana(), Calendar.getInstance());
        }

        if (tocaHoy) {
            if (nombre == null || nombre.isEmpty()) {
                nombre = habito != null ? habito.getNombre() : "Mi Hábito Pastel";
            }
            NotificationHelper.dispararNotificacionHabito(context, habitoId, nombre, tipoSonido, duracionSegundos);
        } else {
            Log.d(TAG, "Hoy no está marcado en los días activos del hábito '" + nombre + "'");
        }

        try {
            if (habito != null && habito.isRecordatorioActivo()) {
                NotificationHelper.programarAlarmaHabito(context, habito);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error re-programando alarma diaria: " + e.getMessage());
        }
    }

    public static boolean esDiaActivo(String diasSemana, Calendar cal) {
        if (diasSemana == null || diasSemana.trim().isEmpty()) {
            return true;
        }
        int day = cal.get(Calendar.DAY_OF_WEEK);
        String letra = "L";
        switch (day) {
            case Calendar.MONDAY: letra = "L"; break;
            case Calendar.TUESDAY: letra = "M"; break;
            case Calendar.WEDNESDAY: letra = "X"; break;
            case Calendar.THURSDAY: letra = "J"; break;
            case Calendar.FRIDAY: letra = "V"; break;
            case Calendar.SATURDAY: letra = "S"; break;
            case Calendar.SUNDAY: letra = "D"; break;
        }
        String[] partes = diasSemana.split(",");
        for (String p : partes) {
            if (p.trim().equalsIgnoreCase(letra)) return true;
        }
        return false;
    }
}
