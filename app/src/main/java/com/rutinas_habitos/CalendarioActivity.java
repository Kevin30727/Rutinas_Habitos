package com.rutinas_habitos;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CalendarioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        generarCalendarioMes();

        // Configurar navegación
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
                } else if (itemId == R.id.nav_calendario) {
                    return true;
                }
                return false;
            });
        }
    }

    private void generarCalendarioMes() {
        Calendar cal = Calendar.getInstance();
        Locale idioma = new Locale("es", "ES");

        TextView tvMesCalendario = findViewById(R.id.tv_mes_calendario);
        SimpleDateFormat formatoMesAnio = new SimpleDateFormat("MMMM\nyyyy", idioma);
        String textoMes = formatoMesAnio.format(cal.getTime());
        textoMes = textoMes.substring(0, 1).toUpperCase() + textoMes.substring(1);

        if (tvMesCalendario != null) {
            tvMesCalendario.setText(textoMes);
        }

        int diaHoy = cal.get(Calendar.DAY_OF_MONTH);
        int diasEnMes = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        int primerDiaSemana = cal.get(Calendar.DAY_OF_WEEK);

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

                if (dia % 4 == 0) {
                    punto.setBackgroundResource(R.drawable.bg_punto_pendiente);
                } else if (dia % 3 == 0) {
                    punto.setBackgroundResource(R.drawable.bg_punto_parcial);
                } else {
                    punto.setBackgroundResource(R.drawable.bg_punto_completo);
                }

                if (dia == diaHoy) {
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
}