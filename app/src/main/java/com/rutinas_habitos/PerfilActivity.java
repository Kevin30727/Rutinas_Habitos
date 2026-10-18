package com.rutinas_habitos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

public class PerfilActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private TextView tvNombre;
    private TextView tvCorreo;
    private TextView tvFechaRegistro;
    private TextView tvMetaBienestar;

    private TextView tvTotalHabitos;
    private TextView tvHabitosHoy;
    private TextView tvMejorRacha;
    private TextView tvModoSinCulpa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupNavegacion();
        setupListeners();
        cargarDatosRelacionalesUsuario();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosRelacionalesUsuario();
    }

    private void initViews() {
        tvNombre = findViewById(R.id.tv_perfil_nombre);
        tvCorreo = findViewById(R.id.tv_perfil_correo);
        tvFechaRegistro = findViewById(R.id.tv_perfil_fecha_registro);
        tvMetaBienestar = findViewById(R.id.tv_perfil_meta_bienestar);

        tvTotalHabitos = findViewById(R.id.tv_stat_total_habitos);
        tvHabitosHoy = findViewById(R.id.tv_stat_habitos_hoy);
        tvMejorRacha = findViewById(R.id.tv_stat_mejor_racha);
        tvModoSinCulpa = findViewById(R.id.tv_stat_modo_sin_culpa);

        ImageView btnVolver = findViewById(R.id.btn_volver_perfil);
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }
    }

    private void cargarDatosRelacionalesUsuario() {
        long userId = sessionManager.getUserId();
        if (userId <= 0) userId = 1;

        Usuario usuario = dbHelper.obtenerUsuarioPorId(userId);
        if (usuario != null) {
            if (tvNombre != null) tvNombre.setText(usuario.getNombre());
            if (tvCorreo != null) tvCorreo.setText(usuario.getCorreo());
            if (tvFechaRegistro != null) {
                String fecha = usuario.getFechaRegistro();
                if (fecha != null && fecha.length() >= 10) {
                    fecha = fecha.substring(0, 10);
                }
                tvFechaRegistro.setText("🌱 Miembro desde: " + fecha);
            }
        } else {
            if (tvNombre != null) tvNombre.setText(sessionManager.getUserName());
            if (tvCorreo != null) tvCorreo.setText(sessionManager.getUserEmail());
        }

        String meta = dbHelper.obtenerMetaBienestar(userId);
        if (tvMetaBienestar != null) {
            tvMetaBienestar.setText("Meta activa: " + meta);
        }

        int totalHabitos = dbHelper.obtenerTotalHabitos(userId);
        int habitosCompletadosHoy = dbHelper.obtenerHabitosCompletadosHoy(userId);
        int mejorRacha = dbHelper.obtenerMejorRacha(userId);
        boolean modoSinCulpaActivo = dbHelper.obtenerModoSinCulpa(userId);

        if (tvTotalHabitos != null) tvTotalHabitos.setText(String.valueOf(totalHabitos));
        if (tvHabitosHoy != null) tvHabitosHoy.setText(habitosCompletadosHoy + " / " + totalHabitos);
        if (tvMejorRacha != null) tvMejorRacha.setText(mejorRacha + " días");
        if (tvModoSinCulpa != null) {
            tvModoSinCulpa.setText(modoSinCulpaActivo ? "Activo 🌱" : "Pausado");
        }
    }

    private void setupListeners() {

        View btnConfigTop = findViewById(R.id.btn_abrir_configuracion_top);
        if (btnConfigTop != null) {
            btnConfigTop.setOnClickListener(v -> startActivity(new Intent(this, ConfiguracionActivity.class)));
        }

        View btnConfigCard = findViewById(R.id.btn_abrir_configuracion);
        if (btnConfigCard != null) {
            btnConfigCard.setOnClickListener(v -> startActivity(new Intent(this, ConfiguracionActivity.class)));
        }

        View btnTonos = findViewById(R.id.btn_abrir_tonos_personalizados);
        if (btnTonos != null) {
            btnTonos.setOnClickListener(v -> startActivity(new Intent(this, TonosActivity.class)));
        }

        View btnProbarDefault = findViewById(R.id.btn_probar_notif_default_directo);
        if (btnProbarDefault != null) {
            btnProbarDefault.setOnClickListener(v -> {
                Toast.makeText(this, "🔔 Enviando notificación con sonido estándar...", Toast.LENGTH_SHORT).show();
                NotificationHelper.enviarNotificacionDefault(
                        this,
                        "🌱 Recordatorio Pastel (Estándar)",
                        "Es momento de cuidar de tu rutina. ¡Cada paso suma bienestar!"
                );
            });
        }

        View btnProbarRelajante = findViewById(R.id.btn_probar_notif_relajante_directo);
        if (btnProbarRelajante != null) {
            btnProbarRelajante.setOnClickListener(v -> {
                Toast.makeText(this, "🎵 Reproduciendo melodía relajante y enviando alerta suave...", Toast.LENGTH_SHORT).show();
                NotificationHelper.reproducirMelodiaRelajantePreview(this);
                NotificationHelper.enviarNotificacionMelodiaRelajante(
                        this,
                        "🎋 Campana Relajante de Hábitos",
                        "Inhala paz, exhala tensión. Tómate este momento solo para ti."
                );
            });
        }

        View btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion);
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(v -> confirmarCerrarSesion());
        }
    }

    private void mostrarDialogoConfiguracion() {
        final long userId = sessionManager.getUserId() > 0 ? sessionManager.getUserId() : 1;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("⚙️ Configuración & Bienestar");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = dpToPx(20);
        layout.setPadding(pad, pad, pad, pad);

        SwitchCompat switchSinCulpa = new SwitchCompat(this);
        switchSinCulpa.setText("Modo Sin Culpa (Protección de rachas)");
        switchSinCulpa.setTextSize(14f);
        boolean sinCulpaActual = dbHelper.obtenerModoSinCulpa(userId);
        switchSinCulpa.setChecked(sinCulpaActual);
        layout.addView(switchSinCulpa);

        TextView tvDescSinCulpa = new TextView(this);
        tvDescSinCulpa.setText("Evita que las rachas se reinicien bruscamente si un día necesitas descansar.");
        tvDescSinCulpa.setTextSize(11.5f);
        tvDescSinCulpa.setTextColor(getResources().getColor(R.color.text_muted));
        tvDescSinCulpa.setPadding(0, dpToPx(2), 0, dpToPx(16));
        layout.addView(tvDescSinCulpa);

        TextView lblMeta = new TextView(this);
        lblMeta.setText("Meta de Bienestar Personal:");
        lblMeta.setTextSize(13f);
        lblMeta.setTypeface(null, android.graphics.Typeface.BOLD);
        lblMeta.setTextColor(getResources().getColor(R.color.neutral));
        layout.addView(lblMeta);

        EditText etMeta = new EditText(this);
        etMeta.setHint("Ej: Calma interior y vitalidad");
        etMeta.setText(dbHelper.obtenerMetaBienestar(userId));
        etMeta.setTextSize(14f);
        layout.addView(etMeta);

        MaterialButton btnTestDefault = new MaterialButton(this);
        btnTestDefault.setText("🔔 Probar tono estándar");
        btnTestDefault.setTextSize(12f);
        btnTestDefault.setTextColor(getResources().getColor(R.color.neutral));
        btnTestDefault.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#EBF3FA")));
        btnTestDefault.setCornerRadius(dpToPx(12));
        btnTestDefault.setOnClickListener(v -> {
            NotificationHelper.enviarNotificacionDefault(this, "Prueba de Sonido", "Notificación del canal estándar activa.");
            Toast.makeText(this, "Notificación estándar enviada", Toast.LENGTH_SHORT).show();
        });
        layout.addView(btnTestDefault);

        MaterialButton btnTestRelajante = new MaterialButton(this);
        btnTestRelajante.setText("🎵 Probar melodía relajante (528Hz)");
        btnTestRelajante.setTextSize(12f);
        btnTestRelajante.setTextColor(getResources().getColor(R.color.neutral));
        btnTestRelajante.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#FDE8EF")));
        btnTestRelajante.setCornerRadius(dpToPx(12));
        btnTestRelajante.setOnClickListener(v -> {
            NotificationHelper.reproducirMelodiaRelajantePreview(this);
            NotificationHelper.enviarNotificacionMelodiaRelajante(this, "Melodía Relajante", "Sonido armónico de relajación activo.");
            Toast.makeText(this, "Melodía relajante reproducida", Toast.LENGTH_SHORT).show();
        });
        layout.addView(btnTestRelajante);

        builder.setView(layout);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            boolean nuevoModo = switchSinCulpa.isChecked();
            dbHelper.actualizarModoSinCulpa(userId, nuevoModo);

            String nuevaMeta = etMeta.getText().toString().trim();
            if (!nuevaMeta.isEmpty()) {
                dbHelper.guardarMetaBienestar(userId, nuevaMeta);
            }

            cargarDatosRelacionalesUsuario();
            Toast.makeText(this, "Preferencias guardadas en base de datos local 🌱", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Deseas cerrar tu sesión actual en Rutina Pastel?")
                .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                    sessionManager.cerrarSesion();
                    Toast.makeText(this, "Sesión cerrada correctamente. ¡Hasta pronto! 🌱", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void setupNavegacion() {
        BottomNavigationView bottomNav = findViewById(R.id.barra_inferior);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_perfil);
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_inicio) {
                    startActivity(new Intent(this, InicioActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_habitos) {
                    startActivity(new Intent(this, HabitosActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_calendario) {
                    startActivity(new Intent(this, CalendarioActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_perfil) {
                    return true;
                }
                return false;
            });
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
