package com.rutinas_habitos;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarioActivity extends AppCompatActivity {

    private Calendar calActual;
    private final Locale idioma = new Locale("es", "ES");
    private TextView tvFechaSeleccionada;
    private TextView tvMesCalendario;
    private GridLayout grid;
    private int diaSeleccionadoActual = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        calActual = Calendar.getInstance();
        diaSeleccionadoActual = calActual.get(Calendar.DAY_OF_MONTH);

        tvFechaSeleccionada = findViewById(R.id.tv_fecha_seleccionada);
        tvMesCalendario = findViewById(R.id.tv_mes_calendario);
        grid = findViewById(R.id.grid_calendario_dinamico);

        actualizarCabeceraFecha(calActual.get(Calendar.DAY_OF_MONTH));
        generarCalendarioMes();
        setupControlesMes();
        setupNavegacion();
    }

    private void setupControlesMes() {
        TextView btnAnterior = findViewById(R.id.btn_mes_anterior);
        TextView btnSiguiente = findViewById(R.id.btn_mes_siguiente);

        if (btnAnterior != null) {
            btnAnterior.setOnClickListener(v -> {
                calActual.add(Calendar.MONTH, -1);
                diaSeleccionadoActual = 1;
                actualizarCabeceraFecha(diaSeleccionadoActual);
                generarCalendarioMes();
            });
        }

        if (btnSiguiente != null) {
            btnSiguiente.setOnClickListener(v -> {
                calActual.add(Calendar.MONTH, 1);
                diaSeleccionadoActual = 1;
                actualizarCabeceraFecha(diaSeleccionadoActual);
                generarCalendarioMes();
            });
        }
    }

    private void setupNavegacion() {
        BottomNavigationView bottomNav = findViewById(R.id.barra_inferior);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_calendario);
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
                } else if (itemId == R.id.nav_perfil) {
                    startActivity(new Intent(this, PerfilActivity.class));
                    finish();
                    return true;
                } else if (itemId == R.id.nav_finanzas) {
                    Toast.makeText(this, "💰 El módulo de Finanzas Conscientes estará disponible próximamente 🌱", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (itemId == R.id.nav_calendario) {
                    return true;
                }
                return false;
            });
        }

        FloatingActionButton fab = findViewById(R.id.btn_nuevo_habito_cal);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(this, CrearHabitoActivity.class);
                startActivity(intent);
            });
        }
    }

    private void actualizarCabeceraFecha(int dia) {
        Calendar calAux = (Calendar) calActual.clone();
        calAux.set(Calendar.DAY_OF_MONTH, dia);

        SimpleDateFormat fmtDiaSemana = new SimpleDateFormat("EEEE", idioma);
        SimpleDateFormat fmtMes = new SimpleDateFormat("MMMM", idioma);

        String diaSemana = fmtDiaSemana.format(calAux.getTime());
        diaSemana = diaSemana.substring(0, 1).toUpperCase() + diaSemana.substring(1);

        String mes = fmtMes.format(calAux.getTime());
        mes = mes.substring(0, 1).toUpperCase() + mes.substring(1);

        if (tvFechaSeleccionada != null) {
            tvFechaSeleccionada.setText(diaSemana + ", " + dia + " " + mes);
        }
    }

    private void generarCalendarioMes() {
        if (grid == null) return;

        SimpleDateFormat formatoMesAnio = new SimpleDateFormat("MMMM\nyyyy", idioma);
        String textoMes = formatoMesAnio.format(calActual.getTime());
        textoMes = textoMes.substring(0, 1).toUpperCase() + textoMes.substring(1);

        if (tvMesCalendario != null) {
            tvMesCalendario.setText(textoMes);
        }

        Calendar calClon = (Calendar) calActual.clone();
        int diasEnMes = calClon.getActualMaximum(Calendar.DAY_OF_MONTH);

        calClon.set(Calendar.DAY_OF_MONTH, 1);
        int primerDiaSemana = calClon.get(Calendar.DAY_OF_WEEK);

        int espaciosVacios = primerDiaSemana - 2;
        if (espaciosVacios < 0) {
            espaciosVacios += 7;
        }

        grid.removeAllViews();

        int cellDimension = dpToPx(38);
        int dotDimension = dpToPx(6);

        for (int i = 0; i < 42; i++) {
            LinearLayout celda = new LinearLayout(this);
            celda.setOrientation(LinearLayout.VERTICAL);
            celda.setGravity(Gravity.CENTER);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(0, 0, 0, dpToPx(12));
            celda.setLayoutParams(params);

            TextView tvNum = new TextView(this);
            tvNum.setTextSize(14);
            tvNum.setTextColor(Color.parseColor("#2D3142"));
            tvNum.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams txtParams = new LinearLayout.LayoutParams(cellDimension, cellDimension);
            tvNum.setLayoutParams(txtParams);

            View punto = new View(this);
            LinearLayout.LayoutParams puntoParams = new LinearLayout.LayoutParams(dotDimension, dotDimension);
            puntoParams.setMargins(0, dpToPx(4), 0, 0);
            punto.setLayoutParams(puntoParams);

            if (i >= espaciosVacios && i < (espaciosVacios + diasEnMes)) {
                int dia = i - espaciosVacios + 1;
                tvNum.setText(String.valueOf(dia));

                // Indicador visual de cumplimiento de hábitos
                if (dia % 4 == 0) {
                    punto.setBackgroundResource(R.drawable.bg_punto_pendiente);
                } else if (dia % 3 == 0) {
                    punto.setBackgroundResource(R.drawable.bg_punto_parcial);
                } else {
                    punto.setBackgroundResource(R.drawable.bg_punto_completo);
                }

                if (dia == diaSeleccionadoActual) {
                    tvNum.setBackgroundResource(R.drawable.bg_dia_seleccionado_calendario);
                    tvNum.setTypeface(null, android.graphics.Typeface.BOLD);
                }

                // Interactividad al tocar un día
                final int diaTocado = dia;
                celda.setOnClickListener(v -> {
                    diaSeleccionadoActual = diaTocado;
                    actualizarCabeceraFecha(diaTocado);
                    generarCalendarioMes();
                });
            } else {
                tvNum.setVisibility(View.INVISIBLE);
                punto.setVisibility(View.INVISIBLE);
            }

            celda.addView(tvNum);
            celda.addView(punto);
            grid.addView(celda);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}