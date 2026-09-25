package com.rutinas_habitos;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarioActivity extends AppCompatActivity {

    private Calendar calendarioActual;
    private final Locale idioma = new Locale("es", "ES");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        calendarioActual = Calendar.getInstance();

        generarCalendarioMes();
        configurarBotonesNavegacionMes();
        configurarSelectoresVista();
        configurarNavegacionInferior();
    }

    private void configurarBotonesNavegacionMes() {
        ImageView btnAnterior = findViewById(R.id.btn_mes_anterior);
        ImageView btnSiguiente = findViewById(R.id.btn_mes_siguiente);

        if (btnAnterior != null) {
            btnAnterior.setOnClickListener(v -> {
                calendarioActual.add(Calendar.MONTH, -1);
                generarCalendarioMes();
            });
        }

        if (btnSiguiente != null) {
            btnSiguiente.setOnClickListener(v -> {
                calendarioActual.add(Calendar.MONTH, 1);
                generarCalendarioMes();
            });
        }
    }

    private void configurarSelectoresVista() {
        TextView btnMes = findViewById(R.id.btn_vista_mes);
        TextView btnSemana = findViewById(R.id.btn_vista_semana);
        TextView btnDia = findViewById(R.id.btn_vista_dia);

        View.OnClickListener selectorListener = v -> {
            resetearEstiloBoton(btnMes);
            resetearEstiloBoton(btnSemana);
            resetearEstiloBoton(btnDia);

            TextView seleccionado = (TextView) v;
            // Usamos la base reutilizable y la teñimos de verde pastel
            seleccionado.setBackgroundResource(R.drawable.bg_tarjeta_redondeada);
            seleccionado.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B8E0D2")));
            seleccionado.setTextColor(Color.parseColor("#2D3142"));
            seleccionado.setTypeface(null, android.graphics.Typeface.BOLD);
        };

        if (btnMes != null) btnMes.setOnClickListener(selectorListener);
        if (btnSemana != null) btnSemana.setOnClickListener(selectorListener);
        if (btnDia != null) btnDia.setOnClickListener(selectorListener);
    }

    private void resetearEstiloBoton(TextView btn) {
        if (btn != null) {
            btn.setBackground(null);
            btn.setBackgroundTintList(null); // Limpiamos cualquier tinte previo
            btn.setTextColor(Color.parseColor("#8D909F"));
            btn.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
    }

    private void generarCalendarioMes() {
        TextView tvMesCalendario = findViewById(R.id.tv_mes_calendario);
        SimpleDateFormat formatoMesAnio = new SimpleDateFormat("MMMM\nyyyy", idioma);
        String textoMes = formatoMesAnio.format(calendarioActual.getTime());
        textoMes = textoMes.substring(0, 1).toUpperCase() + textoMes.substring(1);

        if (tvMesCalendario != null) {
            tvMesCalendario.setText(textoMes);
        }

        Calendar hoyReal = Calendar.getInstance();
        boolean esMesActual = (hoyReal.get(Calendar.YEAR) == calendarioActual.get(Calendar.YEAR) &&
                hoyReal.get(Calendar.MONTH) == calendarioActual.get(Calendar.MONTH));
        int diaHoyReal = hoyReal.get(Calendar.DAY_OF_MONTH);

        int diasEnMes = calendarioActual.getActualMaximum(Calendar.DAY_OF_MONTH);

        Calendar calClon = (Calendar) calendarioActual.clone();
        calClon.set(Calendar.DAY_OF_MONTH, 1);
        int primerDiaSemana = calClon.get(Calendar.DAY_OF_WEEK);

        int espaciosVacios = primerDiaSemana - 2;
        if (espaciosVacios < 0) {
            espaciosVacios += 7;
        }

        GridLayout grid = findViewById(R.id.grid_calendario_dinamico);
        if (grid == null) return;

        grid.removeAllViews();

        for (int i = 0; i < 42; i++) {
            LinearLayout celda = new LinearLayout(this);
            celda.setOrientation(LinearLayout.VERTICAL);
            celda.setGravity(Gravity.CENTER);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(0, 0, 0, 32);
            celda.setLayoutParams(params);

            TextView tvNum = new TextView(this);
            tvNum.setTextSize(14);
            tvNum.setTextColor(Color.parseColor("#2D3142"));
            tvNum.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams txtParams = new LinearLayout.LayoutParams(90, 90);
            tvNum.setLayoutParams(txtParams);

            View punto = new View(this);
            LinearLayout.LayoutParams puntoParams = new LinearLayout.LayoutParams(18, 18);
            puntoParams.setMargins(0, 12, 0, 0);
            punto.setLayoutParams(puntoParams);

            if (i >= espaciosVacios && i < (espaciosVacios + diasEnMes)) {
                int dia = i - espaciosVacios + 1;
                tvNum.setText(String.valueOf(dia));

                // Estos sí los mantuvimos en la lista de drawables
                if (dia % 4 == 0) {
                    punto.setBackgroundResource(R.drawable.bg_punto_pendiente);
                } else if (dia % 3 == 0) {
                    punto.setBackgroundResource(R.drawable.bg_punto_parcial);
                } else {
                    punto.setBackgroundResource(R.drawable.bg_punto_completo);
                }

                // Día seleccionado: base universal circular con tinte morado pastel
                if (esMesActual && dia == diaHoyReal) {
                    tvNum.setBackgroundResource(R.drawable.bg_circulo);
                    tvNum.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E2DDF8")));
                // Resaltar si es exactamente HOY
                if (esMesActual && dia == diaHoyReal) {
                    tvNum.setBackgroundResource(R.drawable.bg_dia_seleccionado_calendario);
                    tvNum.setTypeface(null, android.graphics.Typeface.BOLD);
                }
            } else {
                tvNum.setVisibility(View.INVISIBLE);
                punto.setVisibility(View.INVISIBLE);
            }

            celda.addView(tvNum);
            celda.addView(punto);
            grid.addView(celda);
        }
    }

    private void configurarNavegacionInferior() {
        BottomNavigationView bottomNav = findViewById(R.id.barra_inferior);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_calendario);
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_inicio) {
                    Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_perfil) {
                    Intent intent = new Intent(getApplicationContext(), PerfilActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_calendario) {
                    return true;
                }
                return false;
            });
        }
    }
}