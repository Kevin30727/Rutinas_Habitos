package com.rutinas_habitos;

import android.Manifest;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

/**
 * Gestor Central de Notificaciones para "Rutinas Pastel".
 * 
 * Soporta dos modalidades:
 * 1. Canal por defecto: Sonido nativo de notificación de Android.
 * 2. Canal personalizable: Melodía Zen relajante (WAV/MP3 en res/raw/melodia_zen)
 *    diseñada con frecuencias armónicas (528Hz Solfeggio) para bienestar y calma mental.
 */
public class NotificationHelper {

    public static final String CHANNEL_DEFAULT_ID = "canal_rutinas_default";
    public static final String CHANNEL_MELODIA_ZEN_ID = "canal_rutinas_melodia_zen";

    public static final int NOTIF_ID_DEFAULT = 1001;
    public static final int NOTIF_ID_ZEN = 1002;

    private static MediaPlayer previewPlayer;

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

            // 2. Canal personalizable (Melodía Zen MP3 / Armónica)
            CharSequence nombreZen = "Recordatorios Zen (Melodía Relajante)";
            String descZen = "Notificaciones suaves y armoniosas con campana tibetana para cultivar la paz mental.";
            NotificationChannel canalZen = new NotificationChannel(
                    CHANNEL_MELODIA_ZEN_ID,
                    nombreZen,
                    NotificationManager.IMPORTANCE_HIGH
            );
            canalZen.setDescription(descZen);
            canalZen.enableVibration(true);
            canalZen.setVibrationPattern(new long[]{0, 200, 150, 200});

            Uri soundUriZen = getZenSoundUri(context);
            if (soundUriZen != null) {
                AudioAttributes audioAttrZen = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                canalZen.setSound(soundUriZen, audioAttrZen);
            }
            manager.createNotificationChannel(canalZen);
        }
    }

    /**
     * Retorna el URI del sonido Zen en res/raw/melodia_zen.
     */
    public static Uri getZenSoundUri(Context context) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getPackageName() + "/" + R.raw.melodia_zen);
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
     * Envía una notificación usando el canal con melodía Zen personalizada (Audio MP3/WAV 528Hz).
     */
    public static void enviarNotificacionMelodiaZen(Context context, String titulo, String mensaje) {
        inicializarCanales(context);

        Intent intent = new Intent(context, InicioActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 1, intent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0
        );

        Uri soundUri = getZenSoundUri(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_MELODIA_ZEN_ID)
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
                notificationManager.notify(NOTIF_ID_ZEN, builder.build());
            }
        } catch (SecurityException ignored) {
        }
    }

    /**
     * Reproduce inmediatamente la melodía zen en el dispositivo (modo preview/prueba).
     */
    public static void reproducirMelodiaZenPreview(Context context) {
        try {
            if (previewPlayer != null) {
                previewPlayer.release();
                previewPlayer = null;
            }
            previewPlayer = MediaPlayer.create(context, R.raw.melodia_zen);
            if (previewPlayer != null) {
                previewPlayer.setOnCompletionListener(mp -> {
                    mp.release();
                    previewPlayer = null;
                });
                previewPlayer.start();
            }
        } catch (Exception ignored) {
        }
    }
}
