package com.rutinas_habitos;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HabitosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habitos);

        setupNavegacion();
        setupFiltros();
        setupAcciones();
    }

    private void setupNavegacion() {
        BottomNavigationView bottomNav = findViewById(R.id.barra_inferior);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_habitos);
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_inicio) {
                    startActivity(new Intent(this, InicioActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_calendario) {
                    startActivity(new Intent(this, CalendarioActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_perfil) {
                    startActivity(new Intent(this, PerfilActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_finanzas) {
                    Toast.makeText(this, "💰 El módulo de Finanzas Conscientes estará disponible próximamente 🌱", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (itemId == R.id.nav_habitos) {
                    return true;
                }
                return false;
            });
        }

        FloatingActionButton fab = findViewById(R.id.btn_nuevo_habito_progreso);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(this, CrearHabitoActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupFiltros() {
        TextView fSemana = findViewById(R.id.filtro_semana);
        TextView fMes = findViewById(R.id.filtro_mes);
        TextView fAnio = findViewById(R.id.filtro_anio);

        if (fSemana != null && fMes != null && fAnio != null) {
            fSemana.setOnClickListener(v -> {
                fSemana.setBackgroundResource(R.drawable.bg_selector_activo);
                fSemana.setTextColor(Color.parseColor("#2D3142"));
                fSemana.setTypeface(null, android.graphics.Typeface.BOLD);

                fMes.setBackground(null);
                fMes.setTextColor(Color.parseColor("#8D909F"));
                fMes.setTypeface(null, android.graphics.Typeface.NORMAL);

                fAnio.setBackground(null);
                fAnio.setTextColor(Color.parseColor("#8D909F"));
                fAnio.setTypeface(null, android.graphics.Typeface.NORMAL);
            });

            fMes.setOnClickListener(v -> {
                fMes.setBackgroundResource(R.drawable.bg_selector_activo);
                fMes.setTextColor(Color.parseColor("#2D3142"));
                fMes.setTypeface(null, android.graphics.Typeface.BOLD);

                fSemana.setBackground(null);
                fSemana.setTextColor(Color.parseColor("#8D909F"));
                fSemana.setTypeface(null, android.graphics.Typeface.NORMAL);

                fAnio.setBackground(null);
                fAnio.setTextColor(Color.parseColor("#8D909F"));
                fAnio.setTypeface(null, android.graphics.Typeface.NORMAL);
            });

            fAnio.setOnClickListener(v -> {
                fAnio.setBackgroundResource(R.drawable.bg_selector_activo);
                fAnio.setTextColor(Color.parseColor("#2D3142"));
                fAnio.setTypeface(null, android.graphics.Typeface.BOLD);

                fSemana.setBackground(null);
                fSemana.setTextColor(Color.parseColor("#8D909F"));
                fSemana.setTypeface(null, android.graphics.Typeface.NORMAL);

                fMes.setBackground(null);
                fMes.setTextColor(Color.parseColor("#8D909F"));
                fMes.setTypeface(null, android.graphics.Typeface.NORMAL);
            });
        }
    }

    private void setupAcciones() {
        findViewById(R.id.btn_explorar_historico).setOnClickListener(v ->
                Toast.makeText(this, "Visualizando el histórico de tus hábitos ✨", Toast.LENGTH_SHORT).show());

        findViewById(R.id.btn_exportar_reporte).setOnClickListener(v ->
                Toast.makeText(this, "Generando reporte de bienestar en PDF... 📄🌱", Toast.LENGTH_LONG).show());
    }
}
