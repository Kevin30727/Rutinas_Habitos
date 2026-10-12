package com.rutinas_habitos;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationManagerCompat;

import java.util.List;

/**
 * Receptor de Alarma para Recordatorios de Hábitos.
 * 
 * Se ejecuta de manera exacta a la hora configurada por el usuario para:
 * 1. Disparar una notificación real en la barra de estado con el tono configurado
 *    (estándar, melodía armónica 528Hz o música MP3 personalizada) y duración elegida.
 * 2. Permitir al usuario cancelar la notificación y silenciar la música al instante.
 * 3. Re-programar automáticamente la alarma para el día siguiente si el hábito sigue activo.
 * 4. Restaurar las alarmas tras el reinicio del dispositivo (BOOT_COMPLETED).
 */
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

        // 1. Manejo del botón "Cancelar Notificación" (detiene la música y cancela la notificación)
        if (ACTION_CANCELAR_NOTIFICACION.equals(intent.getAction())) {
            long habitoId = intent.getLongExtra(EXTRA_HABITO_ID, -1);
            Log.d(TAG, "Cancelando notificación y silenciando música para hábito id=" + habitoId);
            try {
                // Detener inmediatamente cualquier reproducción de audio en curso
                NotificationHelper.detenerAudio();

                // Quitar la notificación de la barra de estado del sistema
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

        // 2. Soporte para restauración automática tras reinicio del teléfono
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

        long habitoId = intent.getLongExtra(EXTRA_HABITO_ID, -1);
        String nombre = intent.getStringExtra(EXTRA_HABITO_NOMBRE);
        String tipoSonido = intent.getStringExtra(EXTRA_TIPO_SONIDO);
        int duracionSegundos = intent.getIntExtra(EXTRA_DURACION_SEGUNDOS, 30);
        String hora = intent.getStringExtra(EXTRA_HORA);

        Log.d(TAG, "Recordatorio recibido para hábito id=" + habitoId + " nombre=" + nombre
                + " duracion=" + duracionSegundos + "s hora=" + hora);

        if (nombre == null || nombre.isEmpty()) {
            nombre = "Mi Hábito Pastel";
        }

        // 3. Disparar la notificación real en el teléfono con el sonido y duración elegidos
        NotificationHelper.dispararNotificacionHabito(context, habitoId, nombre, tipoSonido, duracionSegundos);

        // 4. Re-programar para el día siguiente si el hábito existe y tiene recordatorio activo
        try {
            DatabaseHelper dbHelper = DatabaseHelper.getInstance(context);
            Habito habito = dbHelper.obtenerHabitoPorId(habitoId);
            if (habito != null && habito.isRecordatorioActivo()) {
                NotificationHelper.programarAlarmaHabito(context, habito);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error re-programando alarma diaria: " + e.getMessage());
        }
    }
}
