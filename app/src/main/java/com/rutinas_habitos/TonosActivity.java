package com.rutinas_habitos;

import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TonosActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private long userId;

    private LinearLayout llListaTonosContainer;
    private LinearLayout llEstadoVacioTonos;

    private MediaPlayer mediaPlayer;
    private long tonoIdReproduciendo = -1;
    private TextView tvBotonReproduciendo = null;

    private final ActivityResultLauncher<String> audioPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    procesarAudioSeleccionado(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tonos);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId() > 0 ? sessionManager.getUserId() : 1;

        initViews();
        cargarTonosDesdeSQLite();
    }

    private void initViews() {
        llListaTonosContainer = findViewById(R.id.ll_lista_tonos_container);
        llEstadoVacioTonos = findViewById(R.id.ll_estado_vacio_tonos);

        findViewById(R.id.btn_volver_tonos).setOnClickListener(v -> finish());
        findViewById(R.id.btn_subir_audio).setOnClickListener(v -> audioPickerLauncher.launch("audio/*"));
    }

    private void cargarTonosDesdeSQLite() {
        detenerReproduccion();
        llListaTonosContainer.removeAllViews();

        List<TonoAudio> tonos = dbHelper.obtenerTonosPersonalizados(userId);
        if (tonos == null || tonos.isEmpty()) {
            llEstadoVacioTonos.setVisibility(View.VISIBLE);
            return;
        }

        llEstadoVacioTonos.setVisibility(View.GONE);
        for (TonoAudio tono : tonos) {
            View itemView = crearItemTonoView(tono);
            llListaTonosContainer.addView(itemView);
        }
    }

    private View crearItemTonoView(TonoAudio tono) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackgroundResource(R.drawable.bg_tarjeta_redondeada);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int pad = dpToPx(14);
        row.setPadding(pad, pad, pad, pad);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dpToPx(10));
        row.setLayoutParams(params);

        // Icono musical
        TextView tvIcon = new TextView(this);
        tvIcon.setText("🎵");
        tvIcon.setTextSize(22f);
        tvIcon.setPadding(0, 0, dpToPx(12), 0);
        row.addView(tvIcon);

        // Textos (Nombre y Fecha)
        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpText = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        textLayout.setLayoutParams(lpText);

        TextView tvNombre = new TextView(this);
        tvNombre.setText(tono.getNombre());
        tvNombre.setTextColor(Color.parseColor("#2D3142"));
        tvNombre.setTextSize(14.5f);
        tvNombre.setTypeface(null, android.graphics.Typeface.BOLD);
        textLayout.addView(tvNombre);

        TextView tvFecha = new TextView(this);
        tvFecha.setText("Subido: " + tono.getFechaSubida());
        tvFecha.setTextColor(Color.parseColor("#8D909F"));
        tvFecha.setTextSize(11.5f);
        tvFecha.setPadding(0, dpToPx(2), 0, 0);
        textLayout.addView(tvFecha);

        row.addView(textLayout);

        // Botón Escuchar / Parar
        TextView btnPlay = new TextView(this);
        btnPlay.setText(tonoIdReproduciendo == tono.getId() ? "⏹ Parar" : "▶ Escuchar");
        btnPlay.setTextColor(Color.parseColor("#4CA977"));
        btnPlay.setTextSize(12f);
        btnPlay.setTypeface(null, android.graphics.Typeface.BOLD);
        btnPlay.setBackgroundResource(R.drawable.bg_card_destacada_menta);
        btnPlay.setPadding(dpToPx(10), dpToPx(6), dpToPx(10), dpToPx(6));
        btnPlay.setOnClickListener(v -> toggleReproduccion(tono, btnPlay));
        row.addView(btnPlay);

        // Botón Eliminar
        ImageView btnEliminar = new ImageView(this);
        btnEliminar.setImageResource(R.drawable.ic_delete);
        btnEliminar.setColorFilter(Color.parseColor("#E05D5D"));
        btnEliminar.setPadding(dpToPx(8), dpToPx(6), 0, dpToPx(6));
        btnEliminar.setOnClickListener(v -> confirmarEliminarTono(tono));
        row.addView(btnEliminar);

        return row;
    }

    private void toggleReproduccion(TonoAudio tono, TextView btnPlay) {
        if (tonoIdReproduciendo == tono.getId()) {
            detenerReproduccion();
            btnPlay.setText("▶ Escuchar");
            return;
        }

        detenerReproduccion();

        try {
            File audioFile = new File(tono.getRutaArchivo());
            if (!audioFile.exists()) {
                Toast.makeText(this, "El archivo de audio no está disponible", Toast.LENGTH_SHORT).show();
                return;
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(mp -> {
                detenerReproduccion();
                btnPlay.setText("▶ Escuchar");
            });
            mediaPlayer.start();

            tonoIdReproduciendo = tono.getId();
            tvBotonReproduciendo = btnPlay;
            btnPlay.setText("⏹ Parar");

            Toast.makeText(this, "Reproduciendo: " + tono.getNombre(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al reproducir audio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void detenerReproduccion() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
            } catch (Exception ignored) {
            }
            mediaPlayer = null;
        }
        if (tvBotonReproduciendo != null) {
            tvBotonReproduciendo.setText("▶ Escuchar");
            tvBotonReproduciendo = null;
        }
        tonoIdReproduciendo = -1;
    }

    private void procesarAudioSeleccionado(Uri uri) {
        try {
            String nombreArchivo = obtenerNombreDesdeUri(uri);
            if (nombreArchivo == null || nombreArchivo.isEmpty()) {
                nombreArchivo = "Mi_Tono_" + System.currentTimeMillis() + ".mp3";
            }

            File directorioTonos = new File(getFilesDir(), "tonos_mp3");
            if (!directorioTonos.exists()) {
                directorioTonos.mkdirs();
            }

            File archivoDestino = new File(directorioTonos, System.currentTimeMillis() + "_" + nombreArchivo);

            try (InputStream is = getContentResolver().openInputStream(uri);
                 FileOutputStream fos = new FileOutputStream(archivoDestino)) {
                if (is == null) {
                    Toast.makeText(this, "No se pudo leer el archivo de audio seleccionado", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] buffer = new byte[8192];
                int len;
                while ((len = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
            }

            // Registrar en SQLite
            String fechaHoy = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            TonoAudio nuevoTono = new TonoAudio();
            nuevoTono.setUsuarioId(userId);
            nuevoTono.setNombre(nombreArchivo);
            nuevoTono.setRutaArchivo(archivoDestino.getAbsolutePath());
            nuevoTono.setDuracion("MP3");
            nuevoTono.setFechaSubida(fechaHoy);

            long id = dbHelper.insertarTonoPersonalizado(nuevoTono);
            if (id != -1) {
                Toast.makeText(this, "¡Tono '" + nombreArchivo + "' subido con éxito! 🎵", Toast.LENGTH_SHORT).show();
                cargarTonosDesdeSQLite();
            } else {
                Toast.makeText(this, "Error al registrar el tono en la base de datos", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar audio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String obtenerNombreDesdeUri(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            } catch (Exception ignored) {
            }
        }
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) {
                    result = result.substring(cut + 1);
                }
            }
        }
        return result;
    }

    private void confirmarEliminarTono(TonoAudio tono) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Tono")
                .setMessage("¿Deseas eliminar '" + tono.getNombre() + "' de tus tonos guardados?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    detenerReproduccion();
                    try {
                        File f = new File(tono.getRutaArchivo());
                        if (f.exists()) f.delete();
                    } catch (Exception ignored) {
                    }
                    dbHelper.eliminarTonoPersonalizado(tono.getId());
                    Toast.makeText(this, "Tono eliminado 🗑️", Toast.LENGTH_SHORT).show();
                    cargarTonosDesdeSQLite();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        detenerReproduccion();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detenerReproduccion();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
