package com.rutinas_habitos;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.List;

public class HabitosActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private CircularProgressIndicator cpiRendimiento;
    private TextView tvPorcentajeExito;
    private TextView tvRachaDias;
    private LinearLayout llListaHabitosEstrella;
    private LinearLayout llEstadoVacio;

    private TextView tvPctSalud, tvPctMente, tvPctProductividad;
    private ProgressBar pbSalud, pbMente, pbProductividad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habitos);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupNavegacion();
        setupFiltros();
        setupAcciones();
        cargarMetricasYHabitosSQLite();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarMetricasYHabitosSQLite();

        FloatingActionButton fab = findViewById(R.id.btn_nuevo_habito_progreso);
        if (fab != null) {
            fab.setBackgroundTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(this)));
        }
    }

    private void initViews() {
        cpiRendimiento = findViewById(R.id.cpi_rendimiento_habitos);
        tvPorcentajeExito = findViewById(R.id.tv_porcentaje_exito_habitos);
        tvRachaDias = findViewById(R.id.tv_racha_dias_habitos);
        llListaHabitosEstrella = findViewById(R.id.ll_lista_habitos_estrella);
        llEstadoVacio = findViewById(R.id.ll_estado_vacio_habitos);

        tvPctSalud = findViewById(R.id.tv_pct_salud);
        pbSalud = findViewById(R.id.pb_salud);

        tvPctMente = findViewById(R.id.tv_pct_mente);
        pbMente = findViewById(R.id.pb_mente);

        tvPctProductividad = findViewById(R.id.tv_pct_productividad);
        pbProductividad = findViewById(R.id.pb_productividad);
    }

    private void cargarMetricasYHabitosSQLite() {
        long userId = sessionManager.getUserId();
        if (userId <= 0) userId = 1;

        int totalHabitos = dbHelper.obtenerTotalHabitos(userId);
        int habitosCompletados = dbHelper.obtenerHabitosCompletadosHoy(userId);
        int mejorRacha = dbHelper.obtenerMejorRacha(userId);

        int tasaExito = totalHabitos > 0 ? (habitosCompletados * 100 / totalHabitos) : 0;
        if (cpiRendimiento != null) {
            cpiRendimiento.setProgress(tasaExito);
        }
        if (tvPorcentajeExito != null) {
            tvPorcentajeExito.setText(tasaExito + "%");
        }

        if (tvRachaDias != null) {
            tvRachaDias.setText(mejorRacha + " días consecutivos");
        }

        List<Habito> lista = dbHelper.obtenerHabitosPorUsuario(userId);

        if (llListaHabitosEstrella != null) {
            llListaHabitosEstrella.removeAllViews();

            if (lista == null || lista.isEmpty()) {
                if (llEstadoVacio != null) llEstadoVacio.setVisibility(View.VISIBLE);
                llListaHabitosEstrella.addView(llEstadoVacio);
            } else {
                if (llEstadoVacio != null) llEstadoVacio.setVisibility(View.GONE);

                for (Habito h : lista) {
                    View itemCard = crearItemHabitoEstrella(h);
                    llListaHabitosEstrella.addView(itemCard);
                }
            }
        }

        calcularMetricasPorAreas(lista);
    }

    private void calcularMetricasPorAreas(List<Habito> lista) {
        int totSalud = 0, compSalud = 0;
        int totMente = 0, compMente = 0;
        int totProd = 0, compProd = 0;

        if (lista != null) {
            for (Habito h : lista) {
                String area = h.getAreaVida() != null ? h.getAreaVida().toLowerCase() : "";
                boolean comp = h.isCompletadoHoy();

                if (area.contains("salud") || area.contains("cuerpo")) {
                    totSalud++;
                    if (comp) compSalud++;
                } else if (area.contains("mente") || area.contains("bienestar") || area.contains("calma")) {
                    totMente++;
                    if (comp) compMente++;
                } else if (area.contains("foco") || area.contains("productiv")) {
                    totProd++;
                    if (comp) compProd++;
                }
            }
        }

        int pSalud = totSalud > 0 ? (compSalud * 100 / totSalud) : (totSalud > 0 ? 0 : 50);
        int pMente = totMente > 0 ? (compMente * 100 / totMente) : (totMente > 0 ? 0 : 50);
        int pProd = totProd > 0 ? (compProd * 100 / totProd) : (totProd > 0 ? 0 : 50);

        if (tvPctSalud != null) tvPctSalud.setText(pSalud + "%");
        if (pbSalud != null) pbSalud.setProgress(pSalud);

        if (tvPctMente != null) tvPctMente.setText(pMente + "%");
        if (pbMente != null) pbMente.setProgress(pMente);

        if (tvPctProductividad != null) tvPctProductividad.setText(pProd + "%");
        if (pbProductividad != null) pbProductividad.setProgress(pProd);
    }

    private View crearItemHabitoEstrella(Habito h) {
        LinearLayout row = new LinearLayout(this);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        row.setBackgroundResource(R.drawable.bg_tarjeta_redondeada);
        row.setElevation(dpToPx(1));

        LinearLayout.LayoutParams marginParams = (LinearLayout.LayoutParams) row.getLayoutParams();
        marginParams.bottomMargin = dpToPx(12);
        row.setLayoutParams(marginParams);

        ImageView iv = new ImageView(this);
        int iconSize = dpToPx(44);
        iv.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
        iv.setBackgroundResource(R.drawable.bg_circulo);

        try {
            iv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(h.getColor())));
        } catch (Exception ignored) {
            iv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B8E0D2")));
        }

        int iconRes = R.drawable.ic_meditacion;
        if ("agua".equals(h.getIcono())) iconRes = R.drawable.ic_agua;
        else if ("lectura".equals(h.getIcono())) iconRes = R.drawable.ic_lectura;
        else if ("ejercicio".equals(h.getIcono())) iconRes = R.drawable.ic_ejercicio;
        else if ("comida".equals(h.getIcono())) iconRes = R.drawable.ic_comida;
        else if ("noche".equals(h.getIcono())) iconRes = R.drawable.ic_noche;
        else if ("corazon".equals(h.getIcono())) iconRes = R.drawable.ic_corazon;
        else if ("sol".equals(h.getIcono())) iconRes = R.drawable.ic_sun;

        iv.setImageResource(iconRes);
        iv.setPadding(dpToPx(11), dpToPx(11), dpToPx(11), dpToPx(11));
        row.addView(iv);

        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpText = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        lpText.setMarginStart(dpToPx(14));
        textLayout.setLayoutParams(lpText);

        TextView tvNombre = new TextView(this);
        tvNombre.setText(h.getNombre());
        tvNombre.setTextColor(Color.parseColor("#2D3142"));
        tvNombre.setTextSize(15f);
        tvNombre.setTypeface(null, android.graphics.Typeface.BOLD);
        textLayout.addView(tvNombre);

        TextView tvEstado = new TextView(this);
        String estadoTexto = h.isCompletadoHoy() ? "✓ Completado hoy" : "Pendiente de cumplir";
        tvEstado.setText(estadoTexto);
        tvEstado.setTextColor(Color.parseColor("#8D909F"));
        tvEstado.setTextSize(12f);
        LinearLayout.LayoutParams lpEstado = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lpEstado.topMargin = dpToPx(2);
        tvEstado.setLayoutParams(lpEstado);
        textLayout.addView(tvEstado);
        row.addView(textLayout);

        TextView tvRachaChip = new TextView(this);
        tvRachaChip.setText("🔥 " + h.getRachaDias() + " DÍAS");
        tvRachaChip.setTextColor(Color.parseColor("#2D3142"));
        tvRachaChip.setTextSize(11f);
        tvRachaChip.setTypeface(null, android.graphics.Typeface.BOLD);
        tvRachaChip.setPadding(dpToPx(10), dpToPx(5), dpToPx(10), dpToPx(5));
        tvRachaChip.setBackgroundResource(R.drawable.bg_card_destacada_menta);
        row.addView(tvRachaChip);

        row.setOnClickListener(v -> {
            Intent intent = new Intent(this, CrearHabitoActivity.class);
            intent.putExtra("EXTRA_HABITO_ID", h.getId());
            startActivity(intent);
        });

        return row;
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
                actualizarBotonFiltro(fSemana, fMes, fAnio);
                cargarMetricasYHabitosSQLite();
            });

            fMes.setOnClickListener(v -> {
                actualizarBotonFiltro(fMes, fSemana, fAnio);
                cargarMetricasYHabitosSQLite();
            });

            fAnio.setOnClickListener(v -> {
                actualizarBotonFiltro(fAnio, fSemana, fMes);
                cargarMetricasYHabitosSQLite();
            });
        }
    }

    private void actualizarBotonFiltro(TextView activo, TextView inactivo1, TextView inactivo2) {
        activo.setBackgroundResource(R.drawable.bg_selector_activo);
        activo.setTextColor(Color.parseColor("#2D3142"));
        activo.setTypeface(null, android.graphics.Typeface.BOLD);

        inactivo1.setBackground(null);
        inactivo1.setTextColor(Color.parseColor("#8D909F"));
        inactivo1.setTypeface(null, android.graphics.Typeface.NORMAL);

        inactivo2.setBackground(null);
        inactivo2.setTextColor(Color.parseColor("#8D909F"));
        inactivo2.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    private void setupAcciones() {
        View btnHistorico = findViewById(R.id.btn_explorar_historico);
        if (btnHistorico != null) {
            btnHistorico.setOnClickListener(v ->
                    Toast.makeText(this, "Visualizando el histórico de tus hábitos en SQLite ✨", Toast.LENGTH_SHORT).show());
        }

        View btnReporte = findViewById(R.id.btn_exportar_reporte);
        if (btnReporte != null) {
            btnReporte.setOnClickListener(v ->
                    Toast.makeText(this, "Generando reporte de bienestar en PDF... 📄🌱", Toast.LENGTH_LONG).show());
        }

        FloatingActionButton fab = findViewById(R.id.btn_nuevo_habito_progreso);
        if (fab != null) {
            fab.setOnClickListener(v -> startActivity(new Intent(HabitosActivity.this, CrearHabitoActivity.class)));
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
