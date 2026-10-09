package com.rutinas_habitos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class PerfilActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private SwitchCompat switchModoSinCulpa;
    private SwitchCompat switchOcultarBalance;
    private SwitchCompat switchBiometria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupNavegacion();
        setupListeners();
    }

    private void initViews() {
        switchModoSinCulpa = findViewById(R.id.switch_modo_sin_culpa);
        switchOcultarBalance = findViewById(R.id.switch_ocultar_balance);
        switchBiometria = findViewById(R.id.switch_biometria);

        long userId = sessionManager.getUserId();
        if (userId > 0) {
            boolean sinCulpa = dbHelper.obtenerModoSinCulpa(userId);
            if (switchModoSinCulpa != null) {
                switchModoSinCulpa.setChecked(sinCulpa);
            }
        }

        ImageView btnVolver = findViewById(R.id.btn_volver_perfil);
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }
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
                } else if (itemId == R.id.nav_finanzas) {
                    Toast.makeText(this, "💰 El módulo de Finanzas Conscientes estará disponible próximamente 🌱", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (itemId == R.id.nav_perfil) {
                    return true;
                }
                return false;
            });
        }

        FloatingActionButton fab = findViewById(R.id.btn_nuevo_habito_perfil);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(this, CrearHabitoActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupListeners() {
        long userId = sessionManager.getUserId();

        if (switchModoSinCulpa != null) {
            switchModoSinCulpa.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (userId > 0) {
                    dbHelper.actualizarModoSinCulpa(userId, isChecked);
                }
                String estado = isChecked ? "activado (sin penalización)" : "desactivado";
                Toast.makeText(this, "Modo sin culpa " + estado + " 🌱", Toast.LENGTH_SHORT).show();
            });
        }

        if (switchOcultarBalance != null) {
            switchOcultarBalance.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String estado = isChecked ? "Balances ocultos" : "Balances visibles";
                Toast.makeText(this, estado, Toast.LENGTH_SHORT).show();
            });
        }

        if (switchBiometria != null) {
            switchBiometria.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String estado = isChecked ? "Protección biométrica activada" : "Protección biométrica desactivada";
                Toast.makeText(this, estado, Toast.LENGTH_SHORT).show();
            });
        }

        findViewById(R.id.ll_horario_notificaciones).setOnClickListener(v ->
                Toast.makeText(this, "Horario de notificaciones: 08:30 AM", Toast.LENGTH_SHORT).show());

        findViewById(R.id.ll_dia_inicio_semana).setOnClickListener(v ->
                Toast.makeText(this, "Día de inicio configurado: Lunes", Toast.LENGTH_SHORT).show());

        findViewById(R.id.ll_modo_oscuro).setOnClickListener(v ->
                Toast.makeText(this, "Modo nocturno sincronizado con el sistema", Toast.LENGTH_SHORT).show());

        findViewById(R.id.ll_exportar_datos).setOnClickListener(v ->
                Toast.makeText(this, "Exportando tus datos en formato JSON... 📄", Toast.LENGTH_SHORT).show());

        findViewById(R.id.ll_soporte).setOnClickListener(v ->
                Toast.makeText(this, "Soporte: contacto@rutinapastel.com", Toast.LENGTH_LONG).show());

        findViewById(R.id.ll_terminos).setOnClickListener(v ->
                Toast.makeText(this, "Tus datos están protegidos y permanecen locales.", Toast.LENGTH_LONG).show());

        // Cerrar sesión
        LinearLayout btnCerrarSesion = findViewById(R.id.btn_cerrar_sesion);
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(v -> confirmarCerrarSesion());
        }
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
}
