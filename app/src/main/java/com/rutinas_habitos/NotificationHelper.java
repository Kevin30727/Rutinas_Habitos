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

import java.io.File;

/**
 * Gestor Central de Notificaciones para "Rutinas Pastel".
 * 
 * Soporta modalidades de sonido:
 * 1. Canal por defecto: Sonido nativo de notificación de Android.
 * 2. Canal con Melodía Relajante: Frecuencias armónicas (528Hz Solfeggio) y campana tibetana.
 * 3. Audios personalizados: Archivos MP3 o canciones subidas por el usuario.
 */
public class NotificationHelper {

    public static final String CHANNEL_DEFAULT_ID = "canal_rutinas_default";
    public static final String CHANNEL_MELODIA_ARMONICA_ID = "canal_rutinas_melodia_armonica";

    public static final int NOTIF_ID_DEFAULT = 1001;
    public static final int NOTIF_ID_ARMONICA = 1002;

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
     * Reproduce inmediatamente la melodía relajante en el dispositivo (modo preview/prueba).
     */
    public static void reproducirMelodiaRelajantePreview(Context context) {
        try {
            if (previewPlayer != null) {
                previewPlayer.release();
                previewPlayer = null;
            }
            previewPlayer = MediaPlayer.create(context, R.raw.melodia_relajante);
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

    /**
     * Reproduce un archivo de audio personalizado subido por el usuario.
     */
    public static void reproducirAudioPersonalizado(Context context, String rutaArchivo) {
        try {
            if (previewPlayer != null) {
                previewPlayer.release();
                previewPlayer = null;
            }
            File f = new File(rutaArchivo);
            if (f.exists()) {
                previewPlayer = new MediaPlayer();
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
}
