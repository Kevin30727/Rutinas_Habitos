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
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarioActivity extends AppCompatActivity {

    private Calendar calActual;
    private final Locale idioma = new Locale("es", "ES");
    private TextView tvFechaSeleccionada;
    private TextView tvMesCalendario;
    private TextView tvContadorHabitos;
    private GridLayout grid;
    private LinearLayout llContenedorHabitos;
    private LinearLayout llEstadoVacio;
    private int diaSeleccionadoActual = -1;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        calActual = Calendar.getInstance();
        diaSeleccionadoActual = calActual.get(Calendar.DAY_OF_MONTH);

        tvFechaSeleccionada = findViewById(R.id.tv_fecha_seleccionada);
        tvMesCalendario = findViewById(R.id.tv_mes_calendario);
        tvContadorHabitos = findViewById(R.id.tv_contador_habitos_cal);
        grid = findViewById(R.id.grid_calendario_dinamico);
        llContenedorHabitos = findViewById(R.id.ll_contenedor_habitos_calendario);
        llEstadoVacio = findViewById(R.id.ll_estado_vacio_calendario);

        actualizarCabeceraFecha(calActual.get(Calendar.DAY_OF_MONTH));
        generarCalendarioMes();
        setupControlesMes();
        configurarSelectoresVista();
        setupNavegacion();
        cargarHabitosDelDia();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarHabitosDelDia();

        FloatingActionButton fab = findViewById(R.id.btn_nuevo_habito_cal);
        if (fab != null) {
            fab.setBackgroundTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(this)));
        }
    }

    private void cargarHabitosDelDia() {
        if (llContenedorHabitos == null) return;
        llContenedorHabitos.removeAllViews();

        long userId = sessionManager.getUserId();
        if (userId <= 0) userId = 1;

        List<Habito> habitos = dbHelper.obtenerHabitosPorUsuario(userId);

        if (habitos == null || habitos.isEmpty()) {
            if (llEstadoVacio != null) llEstadoVacio.setVisibility(View.VISIBLE);
            if (tvContadorHabitos != null) tvContadorHabitos.setText("0/0 Completados");
            return;
        }

        if (llEstadoVacio != null) llEstadoVacio.setVisibility(View.GONE);

        int completados = 0;
        for (Habito h : habitos) {
            if (h.isCompletadoHoy()) completados++;
            View card = crearTarjetaHabito(h);
            llContenedorHabitos.addView(card);
        }

        if (tvContadorHabitos != null) {
            tvContadorHabitos.setText(completados + "/" + habitos.size() + " Completados");
        }
    }

    private View crearTarjetaHabito(Habito h) {
        LinearLayout row = new LinearLayout(this);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12));
        row.setBackgroundResource(R.drawable.bg_tarjeta_redondeada);
        row.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F6F5FA")));

        LinearLayout.LayoutParams marginParams = (LinearLayout.LayoutParams) row.getLayoutParams();
        marginParams.bottomMargin = dpToPx(10);
        row.setLayoutParams(marginParams);

        ImageView ivCheck = new ImageView(this);
        int checkSize = dpToPx(32);
        ivCheck.setLayoutParams(new LinearLayout.LayoutParams(checkSize, checkSize));
        ivCheck.setBackgroundResource(R.drawable.bg_circulo);

        boolean completado = h.isCompletadoHoy();
        if (completado) {
            ivCheck.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B8E0D2")));
            ivCheck.setImageResource(R.drawable.ic_check);
        } else {
            ivCheck.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E4E7EB")));
            ivCheck.setImageDrawable(null);
        }
        ivCheck.setPadding(dpToPx(7), dpToPx(7), dpToPx(7), dpToPx(7));

        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpText = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        lpText.setMarginStart(dpToPx(12));
        textLayout.setLayoutParams(lpText);

        TextView tvTitulo = new TextView(this);
        tvTitulo.setText(h.getNombre());
        tvTitulo.setTextColor(Color.parseColor("#2D3142"));
        tvTitulo.setTextSize(14f);
        tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);
        textLayout.addView(tvTitulo);

        TextView tvSubtitulo = new TextView(this);
        String estadoTexto = completado ? "✓ Completado" : "Pendiente";
        if (h.getHoraRecordatorio() != null && !h.getHoraRecordatorio().isEmpty()) {
            estadoTexto += " • " + h.getHoraRecordatorio();
        }
        tvSubtitulo.setText(estadoTexto);
        tvSubtitulo.setTextColor(Color.parseColor("#8D909F"));
        tvSubtitulo.setTextSize(12f);
        textLayout.addView(tvSubtitulo);

        TextView tvEmoji = new TextView(this);
        String emoji = "🌱";
        if ("Salud".equalsIgnoreCase(h.getAreaVida())) emoji = "💧";
        else if ("Mente".equalsIgnoreCase(h.getAreaVida())) emoji = "📖";
        else if ("Cuerpo".equalsIgnoreCase(h.getAreaVida())) emoji = "🧘";
        else if ("Descanso".equalsIgnoreCase(h.getAreaVida())) emoji = "🌙";
        else if ("Foco".equalsIgnoreCase(h.getAreaVida())) emoji = "🎯";
        tvEmoji.setText(emoji);
        tvEmoji.setTextSize(16f);

        row.setOnClickListener(v -> {
            boolean nuevoEstado = !h.isCompletadoHoy();
            h.setCompletadoHoy(nuevoEstado);
            dbHelper.toggleHabitoCompletado(h.getId(), nuevoEstado);
            cargarHabitosDelDia();
            String msg = nuevoEstado ? "¡Hábito cumplido! 🌱✨" : "Hábito marcado como pendiente";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });

        row.addView(ivCheck);
        row.addView(textLayout);
        row.addView(tvEmoji);
        return row;
    }

    private void setupControlesMes() {
        View btnAnterior = findViewById(R.id.btn_mes_anterior);
        View btnSiguiente = findViewById(R.id.btn_mes_siguiente);

        if (btnAnterior != null) {
            btnAnterior.setOnClickListener(v -> {
                calActual.add(Calendar.MONTH, -1);
                diaSeleccionadoActual = 1;
                actualizarCabeceraFecha(diaSeleccionadoActual);
                generarCalendarioMes();
                cargarHabitosDelDia();
            });
        }

        if (btnSiguiente != null) {
            btnSiguiente.setOnClickListener(v -> {
                calActual.add(Calendar.MONTH, 1);
                diaSeleccionadoActual = 1;
                actualizarCabeceraFecha(diaSeleccionadoActual);
                generarCalendarioMes();
                cargarHabitosDelDia();
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
            btn.setBackgroundTintList(null);
            btn.setTextColor(Color.parseColor("#8D909F"));
            btn.setTypeface(null, android.graphics.Typeface.NORMAL);
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

                if (dia == diaSeleccionadoActual) {
                    tvNum.setBackgroundResource(R.drawable.bg_dia_seleccionado_calendario);
                    tvNum.setTypeface(null, android.graphics.Typeface.BOLD);
                } else {
                    punto.setBackgroundResource(R.drawable.bg_punto_completo);
                }

                final int diaTocado = dia;
                celda.setOnClickListener(v -> {
                    diaSeleccionadoActual = diaTocado;
                    actualizarCabeceraFecha(diaTocado);
                    generarCalendarioMes();
                    cargarHabitosDelDia();
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
