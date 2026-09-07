package com.rutinas_habitos;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class InicioActivity extends AppCompatActivity {

    private int vasosActuales = 6;
    private final int vasosMaximos = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        configurarCalendario();

        TextView tvMeditacion = findViewById(R.id.tv_meditacion_titulo);
        if (tvMeditacion != null) {
            tvMeditacion.setPaintFlags(tvMeditacion.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        configurarBotonesAgua();

        // Configurar navegación
        BottomNavigationView bottomNav = findViewById(R.id.barra_inferior);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_inicio);
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_calendario) {
                    Intent intent = new Intent(getApplicationContext(), CalendarioActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_inicio) {
                    return true;
                }
                return false;
            });
        }
    }

    private void configurarBotonesAgua() {
        TextView btnMenos = findViewById(R.id.btn_agua_menos);
        TextView btnMas = findViewById(R.id.btn_agua_mas);
        TextView tvTextoAgua = findViewById(R.id.tv_texto_agua);
        ProgressBar pbAgua = findViewById(R.id.pb_agua);

        if (btnMenos != null) {
            btnMenos.setOnClickListener(v -> {
                if (vasosActuales > 0) {
                    vasosActuales--;
                    actualizarVistaAgua(tvTextoAgua, pbAgua);
                }
            });
        }

        if (btnMas != null) {
            btnMas.setOnClickListener(v -> {
                if (vasosActuales < vasosMaximos) {
                    vasosActuales++;
                    actualizarVistaAgua(tvTextoAgua, pbAgua);
                }
            });
        }
    }

    private void actualizarVistaAgua(TextView tvTextoAgua, ProgressBar pbAgua) {
        if (tvTextoAgua != null) {
            tvTextoAgua.setText(vasosActuales + " de " + vasosMaximos + " vasos");
        }
        if (pbAgua != null) {
            pbAgua.setProgress(vasosActuales);
        }
    }

    private void configurarCalendario() {
        Calendar hoy = Calendar.getInstance();
        Locale idioma = new Locale("es", "ES");

        TextView tvMesSemana = findViewById(R.id.tv_mes_semana);
        TextView tvIndicadorHoy = findViewById(R.id.tv_indicador_hoy);

        SimpleDateFormat formatoMes = new SimpleDateFormat("MMMM", idioma);
        String nombreMes = formatoMes.format(hoy.getTime());
        nombreMes = nombreMes.substring(0, 1).toUpperCase() + nombreMes.substring(1);

        hoy.setFirstDayOfWeek(Calendar.MONDAY);
        int semanaDelMes = hoy.get(Calendar.WEEK_OF_MONTH);

        if (tvMesSemana != null) {
            tvMesSemana.setText(nombreMes + ", Semana " + semanaDelMes);
        }

        SimpleDateFormat formatoDia = new SimpleDateFormat("EEEE", idioma);
        String nombreDiaHoy = formatoDia.format(hoy.getTime()).toUpperCase();

        if (tvIndicadorHoy != null) {
            tvIndicadorHoy.setText("HOY ES " + nombreDiaHoy);
        }

        Calendar iterador = (Calendar) hoy.clone();
        iterador.setFirstDayOfWeek(Calendar.MONDAY);
        iterador.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        for (int i = 0; i < 7; i++) {
            int numeroDia = iterador.get(Calendar.DAY_OF_MONTH);
            int indexLayout = i + 1;

            int idNumero = getResources().getIdentifier("tv_num_dia_" + indexLayout, "id", getPackageName());
            int idLetra = getResources().getIdentifier("tv_letra_dia_" + indexLayout, "id", getPackageName());
            int idFondo = getResources().getIdentifier("bg_dia_" + indexLayout, "id", getPackageName());

            TextView tvNumero = findViewById(idNumero);
            TextView tvLetra = findViewById(idLetra);
            LinearLayout bgDia = findViewById(idFondo);

            if (tvNumero != null) {
                tvNumero.setText(String.valueOf(numeroDia));
            }

            boolean esHoy = (hoy.get(Calendar.YEAR) == iterador.get(Calendar.YEAR) &&
                    hoy.get(Calendar.DAY_OF_YEAR) == iterador.get(Calendar.DAY_OF_YEAR));

            if (esHoy && bgDia != null && tvLetra != null) {
                bgDia.setBackgroundResource(R.drawable.bg_dia_activo);
                tvLetra.setTextColor(Color.parseColor("#2D3142"));
                tvLetra.setTypeface(null, android.graphics.Typeface.BOLD);
            }

            iterador.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
}