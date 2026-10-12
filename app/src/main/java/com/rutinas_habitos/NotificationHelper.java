package com.rutinas_habitos;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

/**
 * Gestor Central de Notificaciones y Alarmas para "Rutinas Pastel".
 * 
 * Funcionalidades clave:
 * 1. Programación exacta de alarmas diarias (AlarmManager + HabitoReminderReceiver) según la hora configurada.
 * 2. Soporte para 3 modalidades de sonido reales:
 *    - Estándar: Tono por defecto de Android.
 *    - Relajante: Melodía armónica 528Hz Solfeggio / campana tibetana (res/raw/melodia_relajante).
 *    - Personalizado: Canción / pista de audio MP3 subida por el usuario.
 * 3. Temporizador de duración de reproducción de música (10s, 30s, 1m, 3m, 5m).
 * 4. Botón de acción directo en la notificación para cancelar la alerta y silenciar el sonido de inmediato.
 * 5. Cancelación y reprogramación de alarmas al editar o eliminar hábitos.
 */
public class NotificationHelper {

    private static final String TAG = "NotificationHelper";

    public static final String CHANNEL_DEFAULT_ID = "canal_rutinas_default";
    public static final String CHANNEL_MELODIA_ARMONICA_ID = "canal_rutinas_melodia_armonica";
    public static final String CHANNEL_CUSTOM_ID = "canal_rutinas_custom_mp3";

    public static final int NOTIF_ID_DEFAULT = 1001;
    public static final int NOTIF_ID_ARMONICA = 1002;

    private static MediaPlayer previewPlayer;
    private static Handler audioTimeoutHandler;
    private static Runnable audioTimeoutRunnable;

    /**
     * Inicializa los canales de notificación en Android 8.0 (API 26) o superior.
     */
    public static void inicializarCanales(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager == null) return;

            // 1. Canal por defecto (Sonido de sistema)
            CharSequence nombreDefault = "Recordatorios de Hábitos (Estándar)";
            String descDefault = "Notificaciones con el tono clásico del sistema Android para tus hábitos diarios.";
            NotificationChannel canalDefault = new NotificationChannel(
                    CHANNEL_DEFAULT_ID,
                    nombreDefault,
                    NotificationManager.IMPORTANCE_HIGH
            );
            canalDefault.setDescription(descDefault);
            canalDefault.enableVibration(true);
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            AudioAttributes audioAttrDefault = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            canalDefault.setSound(defaultSoundUri, audioAttrDefault);
            manager.createNotificationChannel(canalDefault);

            // 2. Canal con Melodía Relajante (Armónica 528Hz)
            CharSequence nombreArmonica = "Recordatorios Relajantes (Melodía Armónica)";
            String descArmonica = "Notificaciones suaves y armoniosas para cultivar la calma y constancia.";
            NotificationChannel canalArmonica = new NotificationChannel(
                    CHANNEL_MELODIA_ARMONICA_ID,
                    nombreArmonica,
                    NotificationManager.IMPORTANCE_HIGH
            );
            canalArmonica.setDescription(descArmonica);
            canalArmonica.enableVibration(true);
            canalArmonica.setVibrationPattern(new long[]{0, 200, 150, 200});

            Uri soundUriArmonica = getMelodiaRelajanteSoundUri(context);
            if (soundUriArmonica != null) {
                AudioAttributes audioAttr = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                canalArmonica.setSound(soundUriArmonica, audioAttr);
            }
            manager.createNotificationChannel(canalArmonica);

            // 3. Canal con Música MP3 Personalizada
            CharSequence nombreCustom = "Recordatorios con Música Personalizada (MP3)";
            String descCustom = "Notificaciones que reproducen tu propia música o canciones MP3 elegidas.";
            NotificationChannel canalCustom = new NotificationChannel(
                    CHANNEL_CUSTOM_ID,
                    nombreCustom,
                    NotificationManager.IMPORTANCE_HIGH
            );
            canalCustom.setDescription(descCustom);
            canalCustom.enableVibration(true);
            // El canal no reproduce ding genérico para dejar que el archivo MP3 suene con claridad
            canalCustom.setSound(null, null);
            manager.createNotificationChannel(canalCustom);
        }
    }

    /**
     * Retorna el URI del sonido en res/raw/melodia_relajante.
     */
    public static Uri getMelodiaRelajanteSoundUri(Context context) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getPackageName() + "/" + R.raw.melodia_relajante);
    }

    /**
     * Verifica si se cuenta con permiso de notificación (Android 13+).
     */
    public static boolean tienePermisoNotificaciones(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /**
     * Parsea una cadena de hora ("08:00 AM", "05:30 PM", "14:30") y retorna [horaEn24h, minuto].
     */
    public static int[] parsearHoraMinuto(String horaStr) {
        int hora = 8;
        int minuto = 0;
        if (horaStr == null || horaStr.trim().isEmpty()) {
            return new int[]{hora, minuto};
        }
        try {
            String limpia = horaStr.trim().toUpperCase(Locale.ROOT);
            boolean esPM = limpia.contains("PM");
            boolean esAM = limpia.contains("AM");

            String soloNumeros = limpia.replace("AM", "").replace("PM", "").trim();
            String[] partes = soloNumeros.split(":");
            if (partes.length >= 2) {
                hora = Integer.parseInt(partes[0].trim());
                minuto = Integer.parseInt(partes[1].trim());

                if (esPM && hora < 12) {
                    hora += 12;
                } else if (esAM && hora == 12) {
                    hora = 0;
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "No se pudo parsear hora: '" + horaStr + "', usando 08:00 AM");
            hora = 8;
            minuto = 0;
        }
        return new int[]{hora, minuto};
    }

    /**
     * Formatea los segundos a texto amigable ("10 seg", "30 seg", "1 min", "3 min", "5 min").
     */
    public static String formatearTextoDuracion(int segundos) {
        if (segundos == 10) return "10 seg";
        if (segundos == 30) return "30 seg";
        if (segundos == 60) return "1 min";
        if (segundos == 180) return "3 min";
        if (segundos == 300) return "5 min";
        if (segundos < 60) return segundos + " seg";
        return (segundos / 60) + " min";
    }

    /**
     * Programa una alarma exacta con AlarmManager para un hábito específico.
     * Si la hora ya pasó hoy, se programa para mañana a la misma hora.
     */
    public static void programarAlarmaHabito(Context context, Habito habito) {
        if (context == null || habito == null) return;

        // Si el hábito tiene el recordatorio desactivado, cancelamos la alarma
        if (!habito.isRecordatorioActivo()) {
            cancelarAlarmaHabito(context, (int) habito.getId());
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        int[] horaMinuto = parsearHoraMinuto(habito.getHoraRecordatorio());
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, horaMinuto[0]);
        calendar.set(Calendar.MINUTE, horaMinuto[1]);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Si la hora ya pasó en el día de hoy, programar para mañana a la misma hora
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        Intent intent = new Intent(context, HabitoReminderReceiver.class);
        intent.putExtra(HabitoReminderReceiver.EXTRA_HABITO_ID, habito.getId());
        intent.putExtra(HabitoReminderReceiver.EXTRA_HABITO_NOMBRE, habito.getNombre());
        intent.putExtra(HabitoReminderReceiver.EXTRA_TIPO_SONIDO, habito.getTipoSonidoNotificacion());
        intent.putExtra(HabitoReminderReceiver.EXTRA_DURACION_SEGUNDOS, habito.getDuracionSonidoSegundos());
        intent.putExtra(HabitoReminderReceiver.EXTRA_HORA, habito.getHoraRecordatorio());

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        int requestCode = (int) habito.getId();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, flags);
        long triggerTime = calendar.getTimeInMillis();

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
            Log.d(TAG, "Alarma programada con éxito para hábito '" + habito.getNombre() + "' a las " + calendar.getTime()
                    + " con duración de sonido: " + habito.getDuracionSonidoSegundos() + "s");
        } catch (SecurityException se) {
            Log.w(TAG, "Sin permiso de alarma exacta; usando alarma aproximada: " + se.getMessage());
            try {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } catch (Exception ignored) {}
        } catch (Exception e) {
            Log.e(TAG, "Error programando alarma: " + e.getMessage());
        }
    }

    /**
     * Cancela la alarma programada para un hábito.
     */
    public static void cancelarAlarmaHabito(Context context, int habitoId) {
        if (context == null || habitoId <= 0) return;
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager == null) return;

            Intent intent = new Intent(context, HabitoReminderReceiver.class);
            int flags = PendingIntent.FLAG_NO_CREATE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, habitoId, intent, flags);
            if (pendingIntent != null) {
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
                Log.d(TAG, "Alarma cancelada para hábito id=" + habitoId);
            }
        } catch (Exception e) {
            Log.w(TAG, "Error cancelando alarma id=" + habitoId + ": " + e.getMessage());
        }
    }

    /**
     * Dispara una notificación real en el teléfono con el sonido y duración seleccionados.
     * Incluye botón interactivo directo en la barra de notificación para cancelar la alerta y silenciar la música.
     */
    public static void dispararNotificacionHabito(Context context, long habitoId, String nombreHabito, String tipoSonido, int duracionSegundos) {
        if (context == null) return;
        inicializarCanales(context);

        // 1. PendingIntent al hacer clic sobre el cuerpo de la notificación (abre InicioActivity y silencia)
        Intent intent = new Intent(context, InicioActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("from_notification", true);
        intent.putExtra("habito_id", habitoId);

        int pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingFlags |= PendingIntent.FLAG_IMMUTABLE;
        }
        int notifId = (int) (habitoId > 0 ? habitoId : System.currentTimeMillis() % 100000);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notifId, intent, pendingFlags);

        // 2. PendingIntent para el BOTÓN DE ACCIÓN: "Cancelar Notificación" (silencia música y descarta notificación)
        Intent cancelIntent = new Intent(context, HabitoReminderReceiver.class);
        cancelIntent.setAction(HabitoReminderReceiver.ACTION_CANCELAR_NOTIFICACION);
        cancelIntent.putExtra(HabitoReminderReceiver.EXTRA_HABITO_ID, habitoId);
        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(
                context,
                (int) (habitoId > 0 ? habitoId + 900000 : 999999),
                cancelIntent,
                pendingFlags
        );

        String channelId = CHANNEL_DEFAULT_ID;
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        String mensaje = "Es momento de cumplir tu hábito: " + nombreHabito + " 🌿✨";
        String tiempoTxt = formatearTextoDuracion(duracionSegundos);
        String detalle = "Duración programada: " + tiempoTxt + ". ¡Tómate este instante para ti!";

        long duracionMs = (duracionSegundos > 0 ? duracionSegundos : 30) * 1000L;

        if (tipoSonido != null && tipoSonido.startsWith("custom:")) {
            channelId = CHANNEL_CUSTOM_ID;
            String ruta = tipoSonido.substring("custom:".length()).trim();
            reproducirAudioPersonalizadoConDuracion(context, ruta, duracionMs);
            detalle = "Música activa (" + tiempoTxt + ") 🎵. Toca 'Cancelar' para silenciar.";
        } else if ("relajante".equalsIgnoreCase(tipoSonido)) {
            channelId = CHANNEL_MELODIA_ARMONICA_ID;
            soundUri = getMelodiaRelajanteSoundUri(context);
            reproducirMelodiaRelajanteConDuracion(context, duracionMs);
            detalle = "🎋 Melodía 528Hz activa (" + tiempoTxt + "). Toca 'Cancelar' para silenciar.";
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("⏰ Recordatorio: " + nombreHabito)
                .setContentText(mensaje)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle("⏰ Recordatorio: " + nombreHabito)
                        .bigText(mensaje + "\n\n" + detalle))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setDeleteIntent(cancelPendingIntent) // Si desliza para borrar, también se detiene la música
                .setAutoCancel(true)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "⏹ Cancelar Notificación", cancelPendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.DEFAULT_LIGHTS);

        if (CHANNEL_DEFAULT_ID.equals(channelId) && soundUri != null) {
            builder.setSound(soundUri);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            if (tienePermisoNotificaciones(context)) {
                notificationManager.notify(notifId, builder.build());
                Log.d(TAG, "Notificación disparada exitosamente para id=" + notifId + " con sonido=" + tipoSonido + " y duración=" + tiempoTxt);
            } else {
                Log.w(TAG, "No se puede disparar notificación: Permiso POST_NOTIFICATIONS denegado");
            }
        } catch (SecurityException se) {
            Log.e(TAG, "Permiso denegado al disparar notificación: " + se.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Error al disparar notificación: " + e.getMessage());
        }
    }

    /**
     * Sobrecarga de compatibilidad para dispararNotificacionHabito con duración estándar de 30 segundos.
     */
    public static void dispararNotificacionHabito(Context context, long habitoId, String nombreHabito, String tipoSonido) {
        dispararNotificacionHabito(context, habitoId, nombreHabito, tipoSonido, 30);
    }

    /**
     * Envía una notificación usando el canal por defecto (Sonido estándar de Android).
     */
    public static void enviarNotificacionDefault(Context context, String titulo, String mensaje) {
        inicializarCanales(context);

        Intent intent = new Intent(context, InicioActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_DEFAULT_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            if (tienePermisoNotificaciones(context)) {
                notificationManager.notify(NOTIF_ID_DEFAULT, builder.build());
            }
        } catch (SecurityException ignored) {
        }
    }

    /**
     * Envía una notificación usando el canal con melodía relajante personalizada (Audio 528Hz).
     */
    public static void enviarNotificacionMelodiaRelajante(Context context, String titulo, String mensaje) {
        inicializarCanales(context);

        Intent intent = new Intent(context, InicioActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 1, intent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0
        );

        Uri soundUri = getMelodiaRelajanteSoundUri(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_MELODIA_ARMONICA_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(titulo)
                        .bigText(mensaje + "\n\n🎋 Respira hondo: este momento es solo tuyo."))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(soundUri);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            if (tienePermisoNotificaciones(context)) {
                notificationManager.notify(NOTIF_ID_ARMONICA, builder.build());
            }
        } catch (SecurityException ignored) {
        }
    }

    /**
     * Reproduce inmediatamente el tono estándar de notificación en el dispositivo (modo preview/prueba).
     */
    public static void reproducirDefaultPreview(Context context) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            android.media.Ringtone r = RingtoneManager.getRingtone(context, notification);
            if (r != null) {
                r.play();
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Reproduce inmediatamente la melodía relajante en modo preview (una sola reproducción breve).
     */
    public static void reproducirMelodiaRelajantePreview(Context context) {
        try {
            detenerAudio();
            previewPlayer = MediaPlayer.create(context, R.raw.melodia_relajante);
            if (previewPlayer != null) {
                AudioAttributes attr = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                previewPlayer.setAudioAttributes(attr);
                previewPlayer.setOnCompletionListener(mp -> {
                    mp.release();
                    previewPlayer = null;
                });
                previewPlayer.start();
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Reproduce la melodía relajante durante un tiempo específico en milisegundos (en bucle hasta timeout).
     */
    public static void reproducirMelodiaRelajanteConDuracion(Context context, long duracionMs) {
        try {
            detenerAudio();
            previewPlayer = MediaPlayer.create(context, R.raw.melodia_relajante);
            if (previewPlayer != null) {
                AudioAttributes attr = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                previewPlayer.setAudioAttributes(attr);
                previewPlayer.setLooping(true);
                previewPlayer.start();

                programarDetencionAudio(duracionMs);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reproduciendo melodía relajante con duración: " + e.getMessage());
        }
    }

    /**
     * Reproduce un archivo de audio personalizado en modo preview (una sola reproducción).
     */
    public static void reproducirAudioPersonalizado(Context context, String rutaArchivo) {
        try {
            detenerAudio();
            if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) return;

            File f = new File(rutaArchivo);
            if (f.exists()) {
                previewPlayer = new MediaPlayer();
                AudioAttributes attr = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                previewPlayer.setAudioAttributes(attr);
                previewPlayer.setDataSource(f.getAbsolutePath());
                previewPlayer.prepare();
                previewPlayer.setOnCompletionListener(mp -> {
                    mp.release();
                    previewPlayer = null;
                });
                previewPlayer.start();
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Reproduce un archivo de audio personalizado en bucle durante la duración especificada.
     */
    public static void reproducirAudioPersonalizadoConDuracion(Context context, String rutaArchivo, long duracionMs) {
        try {
            detenerAudio();
            if (rutaArchivo == null || rutaArchivo.trim().isEmpty()) return;

            File f = new File(rutaArchivo);
            if (f.exists()) {
                previewPlayer = new MediaPlayer();
                AudioAttributes attr = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build();
                previewPlayer.setAudioAttributes(attr);
                previewPlayer.setDataSource(f.getAbsolutePath());
                previewPlayer.setLooping(true);
                previewPlayer.prepare();
                previewPlayer.start();

                programarDetencionAudio(duracionMs);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error reproduciendo audio personalizado con duración: " + e.getMessage());
        }
    }

    /**
     * Programa la detención automática del audio al cumplirse el tiempo seleccionado.
     */
    public static void programarDetencionAudio(long duracionMs) {
        cancelarTimeoutAudio();
        if (duracionMs <= 0) return;

        if (audioTimeoutHandler == null) {
            audioTimeoutHandler = new Handler(Looper.getMainLooper());
        }
        audioTimeoutRunnable = () -> {
            Log.d(TAG, "Duración de sonido de notificación completada (" + duracionMs + " ms). Silenciando audio.");
            detenerAudio();
        };
        audioTimeoutHandler.postDelayed(audioTimeoutRunnable, duracionMs);
    }

    /**
     * Cancela cualquier temporizador pendiente de detención de audio.
     */
    private static void cancelarTimeoutAudio() {
        if (audioTimeoutHandler != null && audioTimeoutRunnable != null) {
            audioTimeoutHandler.removeCallbacks(audioTimeoutRunnable);
            audioTimeoutRunnable = null;
        }
    }

    /**
     * Detiene inmediatamente cualquier reproducción de audio en curso y cancela temporizadores.
     */
    public static void detenerAudio() {
        cancelarTimeoutAudio();
        try {
            if (previewPlayer != null) {
                try { previewPlayer.stop(); } catch (Exception ignored) {}
                previewPlayer.release();
                previewPlayer = null;
                Log.d(TAG, "Audio silenciado y detenido.");
            }
        } catch (Exception ignored) {
        }
    }
}
