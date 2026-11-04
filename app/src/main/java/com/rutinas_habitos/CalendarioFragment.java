package com.rutinas_habitos;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CalendarioFragment extends Fragment {

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendario, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context ctx = requireContext();
        dbHelper = DatabaseHelper.getInstance(ctx);
        sessionManager = new SessionManager(ctx);

        calActual = Calendar.getInstance();
        diaSeleccionadoActual = calActual.get(Calendar.DAY_OF_MONTH);

        tvFechaSeleccionada = view.findViewById(R.id.tv_fecha_seleccionada);
        tvMesCalendario = view.findViewById(R.id.tv_mes_calendario);
        tvContadorHabitos = view.findViewById(R.id.tv_contador_habitos_cal);
        grid = view.findViewById(R.id.grid_calendario_dinamico);
        llContenedorHabitos = view.findViewById(R.id.ll_contenedor_habitos_calendario);
        llEstadoVacio = view.findViewById(R.id.ll_estado_vacio_calendario);

        setupControlesMes(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        actualizarCabeceraFecha(diaSeleccionadoActual);
        generarCalendarioMes();
        cargarHabitosDelDia();
    }

    private void cargarHabitosDelDia() {
        if (llContenedorHabitos == null || getContext() == null) return;
        llContenedorHabitos.removeAllViews();

        long userId = sessionManager.getUserId();
        if (userId <= 0) userId = 1;

        int anio = calActual.get(Calendar.YEAR);
        int mes = calActual.get(Calendar.MONTH) + 1;
        String fechaSeleccionada = String.format(Locale.ROOT, "%04d-%02d-%02d", anio, mes, diaSeleccionadoActual);

        List<Habito> habitos = dbHelper.obtenerHabitosConEstadoFecha(userId, fechaSeleccionada);

        if (habitos == null || habitos.isEmpty()) {
            if (llEstadoVacio != null) llEstadoVacio.setVisibility(View.VISIBLE);
            if (tvContadorHabitos != null) tvContadorHabitos.setText("0/0 Completados");
            return;
        }

        if (llEstadoVacio != null) llEstadoVacio.setVisibility(View.GONE);

        int completados = 0;
        for (Habito h : habitos) {
            if (h.isCompletadoHoy()) completados++;
            View card = crearTarjetaHabito(h, fechaSeleccionada, userId);
            llContenedorHabitos.addView(card);
        }

        if (tvContadorHabitos != null) {
            tvContadorHabitos.setText(completados + "/" + habitos.size() + " Completados");
        }
    }

    private View crearTarjetaHabito(Habito h, String fecha, long userId) {
        Context ctx = requireContext();
        LinearLayout row = new LinearLayout(ctx);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14));
        row.setBackgroundResource(R.drawable.bg_tarjeta_redondeada);

        boolean completado = h.isCompletadoHoy();
        int colorFondo = completado ? ThemeHelper.getColorFondoSuave(ctx) : Color.parseColor("#F6F5FA");
        row.setBackgroundTintList(ColorStateList.valueOf(colorFondo));

        LinearLayout.LayoutParams marginParams = (LinearLayout.LayoutParams) row.getLayoutParams();
        marginParams.bottomMargin = dpToPx(10);
        row.setLayoutParams(marginParams);

        ImageView ivCheck = new ImageView(ctx);
        int checkSize = dpToPx(34);
        ivCheck.setLayoutParams(new LinearLayout.LayoutParams(checkSize, checkSize));
        ivCheck.setBackgroundResource(R.drawable.bg_circulo);

        if (completado) {
            ivCheck.setBackgroundTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(ctx)));
            ivCheck.setImageResource(R.drawable.ic_check);
        } else {
            ivCheck.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E3E8")));
            ivCheck.setImageDrawable(null);
        }
        ivCheck.setPadding(dpToPx(7), dpToPx(7), dpToPx(7), dpToPx(7));

        LinearLayout textLayout = new LinearLayout(ctx);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpText = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        lpText.setMarginStart(dpToPx(14));
        textLayout.setLayoutParams(lpText);

        TextView tvTitulo = new TextView(ctx);
        tvTitulo.setText(h.getNombre());
        tvTitulo.setTextColor(Color.parseColor(completado ? "#7B8094" : "#2D3142"));
        tvTitulo.setTextSize(15f);
        tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);
        if (completado) {
            tvTitulo.setPaintFlags(tvTitulo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        textLayout.addView(tvTitulo);

        TextView tvSubtitulo = new TextView(ctx);
        String estadoTexto = completado ? "✓ Completado" : "Pendiente";
        if ("cantidad".equalsIgnoreCase(h.getTipoMeta())) {
            estadoTexto += " (" + (completado ? h.getMetaCantidad() : h.getProgresoActual()) + "/" + h.getMetaCantidad() + " " + h.getUnidad() + ")";
        }
        if (h.getHoraRecordatorio() != null && !h.getHoraRecordatorio().isEmpty()) {
            estadoTexto += " • " + h.getHoraRecordatorio();
        }
        tvSubtitulo.setText(estadoTexto);
        tvSubtitulo.setTextColor(completado ? ThemeHelper.getColorPrimario(ctx) : Color.parseColor("#8D909F"));
        tvSubtitulo.setTextSize(12f);
        textLayout.addView(tvSubtitulo);

        row.addView(ivCheck);
        row.addView(textLayout);

        row.setOnClickListener(v -> {
            boolean nuevoEstado = !h.isCompletadoHoy();
            h.setCompletadoHoy(nuevoEstado);
            dbHelper.toggleHabitoEnFecha(h.getId(), userId, fecha, nuevoEstado);

            if (nuevoEstado) {
                VibrationHelper.vibrarExito(ctx);
                Toast.makeText(ctx, "¡" + h.getNombre() + " completado! 🎉✨", Toast.LENGTH_SHORT).show();
            } else {
                VibrationHelper.vibrarClick(ctx);
                Toast.makeText(ctx, h.getNombre() + " marcado como pendiente", Toast.LENGTH_SHORT).show();
            }

            cargarHabitosDelDia();
            generarCalendarioMes();
        });

        return row;
    }

    private void setupControlesMes(View root) {
        View btnAnterior = root.findViewById(R.id.btn_mes_anterior);
        View btnSiguiente = root.findViewById(R.id.btn_mes_siguiente);

        if (btnAnterior != null) {
            btnAnterior.setOnClickListener(v -> {
                VibrationHelper.vibrarClick(requireContext());
                calActual.add(Calendar.MONTH, -1);
                diaSeleccionadoActual = 1;
                actualizarCabeceraFecha(diaSeleccionadoActual);
                generarCalendarioMes();
                cargarHabitosDelDia();
            });
        }

        if (btnSiguiente != null) {
            btnSiguiente.setOnClickListener(v -> {
                VibrationHelper.vibrarClick(requireContext());
                calActual.add(Calendar.MONTH, 1);
                diaSeleccionadoActual = 1;
                actualizarCabeceraFecha(diaSeleccionadoActual);
                generarCalendarioMes();
                cargarHabitosDelDia();
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
        if (grid == null || getContext() == null) return;

        SimpleDateFormat formatoMesAnio = new SimpleDateFormat("MMMM yyyy", idioma);
        String textoMes = formatoMesAnio.format(calActual.getTime());
        textoMes = textoMes.substring(0, 1).toUpperCase() + textoMes.substring(1);

        if (tvMesCalendario != null) {
            tvMesCalendario.setText(textoMes);
        }

        long userId = sessionManager.getUserId() > 0 ? sessionManager.getUserId() : 1;
        int anio = calActual.get(Calendar.YEAR);
        int mes = calActual.get(Calendar.MONTH) + 1;
        Set<Integer> diasConCompletados = dbHelper.obtenerDiasConRegistroMes(userId, anio, mes);

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
            LinearLayout celda = new LinearLayout(requireContext());
            celda.setOrientation(LinearLayout.VERTICAL);
            celda.setGravity(Gravity.CENTER);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(0, 0, 0, dpToPx(10));
            celda.setLayoutParams(params);

            TextView tvNum = new TextView(requireContext());
            tvNum.setTextSize(14);
            tvNum.setTextColor(Color.parseColor("#2D3142"));
            tvNum.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams txtParams = new LinearLayout.LayoutParams(cellDimension, cellDimension);
            tvNum.setLayoutParams(txtParams);

            View punto = new View(requireContext());
            LinearLayout.LayoutParams puntoParams = new LinearLayout.LayoutParams(dotDimension, dotDimension);
            puntoParams.setMargins(0, dpToPx(4), 0, 0);
            punto.setLayoutParams(puntoParams);

            if (i >= espaciosVacios && i < (espaciosVacios + diasEnMes)) {
                int dia = i - espaciosVacios + 1;
                tvNum.setText(String.valueOf(dia));

                if (dia == diaSeleccionadoActual) {
                    tvNum.setBackgroundResource(R.drawable.bg_circulo);
                    tvNum.setBackgroundTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(requireContext())));
                    tvNum.setTextColor(Color.WHITE);
                    tvNum.setTypeface(null, android.graphics.Typeface.BOLD);
                }

                if (diasConCompletados.contains(dia)) {
                    punto.setBackgroundResource(R.drawable.bg_punto_completo);
                    punto.setBackgroundTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(requireContext())));
                    punto.setVisibility(View.VISIBLE);
                } else {
                    punto.setVisibility(View.INVISIBLE);
                }

                final int diaTocado = dia;
                celda.setOnClickListener(v -> {
                    VibrationHelper.vibrarClick(requireContext());
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
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
