package com.rutinas_habitos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class ConfiguracionActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "rutinas_pastel_config";
    private static final String KEY_PALETA = "paleta_seleccionada";
    private static final String KEY_DESCANSO_VISUAL = "modo_descanso_visual";
    private static final String KEY_NOTIF_ACTIVAS = "notificaciones_activas";

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private SharedPreferences configPrefs;
    private long userId;

    private SwitchCompat switchSinCulpa;
    private TextView tvMetaSubtitulo;
    private SwitchCompat switchRecordatorios;

    private LinearLayout optMenta;
    private LinearLayout optSakura;
    private LinearLayout optOceano;
    private LinearLayout optLavanda;
    private TextView tvCheckMenta;
    private TextView tvCheckSakura;
    private TextView tvCheckOceano;
    private TextView tvCheckLavanda;
    private SwitchCompat switchDescansoVisual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);
        configPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        userId = sessionManager.getUserId();
        if (userId <= 0) userId = 1;

        initViews();
        cargarConfiguracionActual();
        setupListeners();
    }

    private void initViews() {
        findViewById(R.id.btn_volver_config).setOnClickListener(v -> finish());

        switchSinCulpa = findViewById(R.id.switch_config_sin_culpa);
        tvMetaSubtitulo = findViewById(R.id.tv_config_meta_subtitulo);
        switchRecordatorios = findViewById(R.id.switch_config_recordatorios);

        optMenta = findViewById(R.id.opt_paleta_menta);
        optSakura = findViewById(R.id.opt_paleta_sakura);
        optOceano = findViewById(R.id.opt_paleta_oceano);
        optLavanda = findViewById(R.id.opt_paleta_lavanda);

        tvCheckMenta = findViewById(R.id.tv_check_paleta_menta);
        tvCheckSakura = findViewById(R.id.tv_check_paleta_sakura);
        tvCheckOceano = findViewById(R.id.tv_check_paleta_oceano);
        tvCheckLavanda = findViewById(R.id.tv_check_paleta_lavanda);

        switchDescansoVisual = findViewById(R.id.switch_config_descanso_visual);
    }

    private void cargarConfiguracionActual() {

        boolean sinCulpa = dbHelper.obtenerModoSinCulpa(userId);
        switchSinCulpa.setChecked(sinCulpa);

        String meta = dbHelper.obtenerMetaBienestar(userId);
        tvMetaSubtitulo.setText(meta != null && !meta.isEmpty() ? meta : "Paz interior y consistencia");

        boolean notifActivas = configPrefs.getBoolean(KEY_NOTIF_ACTIVAS, true);
        switchRecordatorios.setChecked(notifActivas);

        String paleta = configPrefs.getString(KEY_PALETA, "menta");
        actualizarUIPaleta(paleta);

        boolean descanso = configPrefs.getBoolean(KEY_DESCANSO_VISUAL, false);
        switchDescansoVisual.setChecked(descanso);
    }

    private void setupListeners() {

        switchSinCulpa.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dbHelper.actualizarModoSinCulpa(userId, isChecked);
            Toast.makeText(this, isChecked ?
                    "Modo Sin Culpa activado 🌱 Tus rachas se pausan con amor" :
                    "Modo Sin Culpa desactivado", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.card_config_meta).setOnClickListener(v -> mostrarDialogoEditarMeta());

        switchRecordatorios.setOnCheckedChangeListener((buttonView, isChecked) -> {
            configPrefs.edit().putBoolean(KEY_NOTIF_ACTIVAS, isChecked).apply();
            Toast.makeText(this, isChecked ? "Recordatorios conscientes activados 🔔" : "Recordatorios pausados", Toast.LENGTH_SHORT).show();
        });

        optMenta.setOnClickListener(v -> seleccionarPaleta("menta", "Pastel Neo (Menta)"));
        optSakura.setOnClickListener(v -> seleccionarPaleta("sakura", "Dulce Sakura (Rosa)"));
        optOceano.setOnClickListener(v -> seleccionarPaleta("oceano", "Océano Calmo (Azul)"));
        optLavanda.setOnClickListener(v -> seleccionarPaleta("lavanda", "Lavanda Suave"));

        switchDescansoVisual.setOnCheckedChangeListener((buttonView, isChecked) -> {
            configPrefs.edit().putBoolean(KEY_DESCANSO_VISUAL, isChecked).apply();
            Toast.makeText(this, isChecked ? "Descanso visual activado ✨ Tonos relajantes" : "Descanso visual desactivado", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_exportar_habitos).setOnClickListener(v -> exportarHabitosJSON());

        findViewById(R.id.btn_vaciar_habitos).setOnClickListener(v -> confirmarVaciarHabitos());

        findViewById(R.id.btn_test_notif_default).setOnClickListener(v -> {
            NotificationHelper.reproducirDefaultPreview(this);
            NotificationHelper.enviarNotificacionDefault(
                    this,
                    "🔔 Tono Estándar - Rutina Pastel",
                    "Recordatorio amable: Dedica unos minutos a cuidar de ti hoy."
            );
            Toast.makeText(this, "Enviando alerta con sonido estándar de Android", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_test_notif_relajante).setOnClickListener(v -> {
            NotificationHelper.reproducirMelodiaRelajantePreview(this);
            NotificationHelper.enviarNotificacionMelodiaRelajante(
                    this,
                    "🎋 Melodía Relajante (528Hz)",
                    "Inhala paz, exhala tensión. Este momento de bienestar es solo tuyo."
            );
            Toast.makeText(this, "🎵 Reproduciendo melodía relajante armónica", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btn_config_cerrar_sesion).setOnClickListener(v -> confirmarCerrarSesion());
    }

    private void seleccionarPaleta(String clave, String nombreAmigable) {
        ThemeHelper.guardarPaleta(this, clave);
        configPrefs.edit().putString(KEY_PALETA, clave).apply();
        actualizarUIPaleta(clave);
        Toast.makeText(this, "Paleta " + nombreAmigable + " aplicada ✨", Toast.LENGTH_SHORT).show();
    }

    private void actualizarUIPaleta(String clave) {

        resetPaletaCard(optMenta, tvCheckMenta);
        resetPaletaCard(optSakura, tvCheckSakura);
        resetPaletaCard(optOceano, tvCheckOceano);
        resetPaletaCard(optLavanda, tvCheckLavanda);

        if ("sakura".equals(clave)) {
            activarPaletaCard(optSakura, tvCheckSakura, R.drawable.bg_card_destacada_rosa, "#E8A9C2");
        } else if ("oceano".equals(clave)) {
            activarPaletaCard(optOceano, tvCheckOceano, R.drawable.bg_tarjeta_redondeada, "#92BED4");
        } else if ("lavanda".equals(clave)) {
            activarPaletaCard(optLavanda, tvCheckLavanda, R.drawable.bg_tarjeta_redondeada, "#8F94D6");
        } else {

            activarPaletaCard(optMenta, tvCheckMenta, R.drawable.bg_card_destacada_menta, "#4CA977");
        }
    }

    private void resetPaletaCard(LinearLayout layout, TextView tvCheck) {
        if (layout != null) {
            layout.setBackgroundResource(R.drawable.bg_tarjeta_redondeada);
            layout.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#FAFAFC")));
        }
        if (tvCheck != null) {
            tvCheck.setText("Elegir");
            tvCheck.setTextColor(Color.parseColor("#8D909F"));
        }
    }

    private void activarPaletaCard(LinearLayout layout, TextView tvCheck, int bgRes, String colorHex) {
        if (layout != null) {
            layout.setBackgroundResource(bgRes);
            layout.setBackgroundTintList(null);
        }
        if (tvCheck != null) {
            tvCheck.setText("✓ Activa");
            tvCheck.setTextColor(Color.parseColor(colorHex));
        }
    }

    private void mostrarDialogoEditarMeta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Meta de Bienestar");

        final EditText input = new EditText(this);
        input.setHint("Ej: Calma interior, hidratación y lectura");
        input.setText(tvMetaSubtitulo.getText().toString());
        input.setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14));
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String texto = input.getText().toString().trim();
            if (!texto.isEmpty()) {
                dbHelper.guardarMetaBienestar(userId, texto);
                tvMetaSubtitulo.setText(texto);
                Toast.makeText(this, "Meta de bienestar actualizada 🌱", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void exportarHabitosJSON() {
        List<Habito> habitos = dbHelper.obtenerHabitosPorUsuario(userId);
        if (habitos == null || habitos.isEmpty()) {
            Toast.makeText(this, "No tienes hábitos registrados para exportar todavía.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONArray arr = new JSONArray();
            for (Habito h : habitos) {
                JSONObject obj = new JSONObject();
                obj.put("id", h.getId());
                obj.put("nombre", h.getNombre());
                obj.put("icono", h.getIcono());
                obj.put("color", h.getColor());
                obj.put("areaVida", h.getAreaVida());
                obj.put("tipoFrecuencia", h.getTipoFrecuencia());
                obj.put("diasSemana", h.getDiasSemana());
                obj.put("tipoMeta", h.getTipoMeta());
                obj.put("metaCantidad", h.getMetaCantidad());
                obj.put("unidad", h.getUnidad());
                obj.put("horaRecordatorio", h.getHoraRecordatorio());
                obj.put("tipoSonidoNotificacion", h.getTipoSonidoNotificacion());
                obj.put("rachaDias", h.getRachaDias());
                obj.put("completadoHoy", h.isCompletadoHoy());
                arr.put(obj);
            }

            JSONObject root = new JSONObject();
            root.put("app", "Rutinas Pastel");
            root.put("version", "2.0");
            root.put("fechaExportacion", java.text.DateFormat.getDateTimeInstance().format(new java.util.Date()));
            root.put("totalHabitos", habitos.size());
            root.put("habitos", arr);

            String jsonString = root.toString(2);

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, jsonString);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Exportar Copia de Seguridad JSON"));

        } catch (Exception e) {
            Toast.makeText(this, "Error al generar archivo JSON: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void confirmarVaciarHabitos() {
        new AlertDialog.Builder(this)
                .setTitle("⚠️ Reiniciar Hábitos")
                .setMessage("¿Estás completamente seguro de que deseas eliminar TODOS tus hábitos creados en SQLite?\n\nEsta acción borrará tus registros y estadísticas de racha para comenzar de nuevo.")
                .setPositiveButton("Sí, vaciar todo", (dialog, which) -> {
                    dbHelper.vaciarTodosLosHabitos(userId);
                    Toast.makeText(this, "Base de datos reiniciada. Se han eliminado todos los hábitos 🌱", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Deseas cerrar tu sesión actual en Rutinas Pastel?")
                .setPositiveButton("Cerrar Sesión", (dialog, which) -> {
                    sessionManager.cerrarSesion();
                    Toast.makeText(this, "Sesión cerrada correctamente. ¡Hasta pronto! 🌱", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ConfiguracionActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
