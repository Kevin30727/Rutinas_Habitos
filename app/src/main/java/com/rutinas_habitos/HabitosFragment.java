package com.rutinas_habitos;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HabitosFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private CircularProgressIndicator cpiRendimiento;
    private TextView tvPorcentajeExito;
    private TextView tvRachaDias;
    private LinearLayout llListaHabitosEstrella;

    private TextView tvPctSalud, tvPctMente, tvPctProductividad;
    private ProgressBar pbSalud, pbMente, pbProductividad;

    private View barLunes, barMartes, barMiercoles, barJueves, barViernes, barSabado, barDomingo;
    private TextView tvHighlightSemanal;
    private TextView tvRangoFechasSemana;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_habitos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context ctx = requireContext();
        dbHelper = DatabaseHelper.getInstance(ctx);
        sessionManager = new SessionManager(ctx);

        initViews(view);
        setupFiltros(view);
        setupAcciones(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarMetricasYHabitosSQLite();
    }

    private void initViews(View root) {
        cpiRendimiento = root.findViewById(R.id.cpi_rendimiento_habitos);
        tvPorcentajeExito = root.findViewById(R.id.tv_porcentaje_exito_habitos);
        tvRachaDias = root.findViewById(R.id.tv_racha_dias_habitos);
        llListaHabitosEstrella = root.findViewById(R.id.ll_lista_habitos_estrella);

        tvPctSalud = root.findViewById(R.id.tv_pct_salud);
        pbSalud = root.findViewById(R.id.pb_salud);

        tvPctMente = root.findViewById(R.id.tv_pct_mente);
        pbMente = root.findViewById(R.id.pb_mente);

        tvPctProductividad = root.findViewById(R.id.tv_pct_productividad);
        pbProductividad = root.findViewById(R.id.pb_productividad);

        barLunes = root.findViewById(R.id.bar_lunes);
        barMartes = root.findViewById(R.id.bar_martes);
        barMiercoles = root.findViewById(R.id.bar_miercoles);
        barJueves = root.findViewById(R.id.bar_jueves);
        barViernes = root.findViewById(R.id.bar_viernes);
        barSabado = root.findViewById(R.id.bar_sabado);
        barDomingo = root.findViewById(R.id.bar_domingo);

        tvHighlightSemanal = root.findViewById(R.id.tv_highlight_semanal);
        tvRangoFechasSemana = root.findViewById(R.id.tv_rango_fechas_semana);
    }

    private void cargarMetricasYHabitosSQLite() {
        if (getContext() == null) return;
        long userId = sessionManager.getUserId();
        if (userId <= 0) userId = 1;

        int totalHabitos = dbHelper.obtenerTotalHabitos(userId);
        int habitosCompletados = dbHelper.obtenerHabitosCompletadosHoy(userId);
        int mejorRacha = dbHelper.obtenerMejorRacha(userId);

        int tasaExito = totalHabitos > 0 ? (habitosCompletados * 100 / totalHabitos) : 0;
        if (cpiRendimiento != null) {
            cpiRendimiento.setProgress(tasaExito);
            cpiRendimiento.setIndicatorColor(ThemeHelper.getColorPrimario(requireContext()));
        }
        if (tvPorcentajeExito != null) {
            tvPorcentajeExito.setText(tasaExito + "%");
        }

        if (tvRachaDias != null) {
            tvRachaDias.setText(mejorRacha + " días de racha activa");
        }

        actualizarGraficoSemanal(userId);

        List<Habito> lista = dbHelper.obtenerHabitosPorUsuario(userId);
        if (llListaHabitosEstrella != null) {
            llListaHabitosEstrella.removeAllViews();
            if (lista == null || lista.isEmpty()) {
                TextView tvVacio = new TextView(requireContext());
                tvVacio.setText("Aún no tienes hábitos para mostrar.");
                tvVacio.setTextColor(Color.parseColor("#8D909F"));
                tvVacio.setGravity(Gravity.CENTER);
                tvVacio.setPadding(0, dpToPx(16), 0, dpToPx(16));
                llListaHabitosEstrella.addView(tvVacio);
            } else {
                for (Habito h : lista) {
                    View itemCard = crearItemHabitoEstrella(h);
                    llListaHabitosEstrella.addView(itemCard);
                }
            }
        }

        calcularMetricasPorAreas(lista);
    }

    private void actualizarGraficoSemanal(long userId) {
        int[] pcts = dbHelper.obtenerCompletadosSemanaActual(userId);
        View[] bars = {barLunes, barMartes, barMiercoles, barJueves, barViernes, barSabado, barDomingo};
        String[] diasNombre = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

        int maxIdx = 0;
        int maxVal = -1;
        int suma = 0;

        for (int i = 0; i < 7; i++) {
            int pct = pcts[i];
            suma += pct;
            if (pct > maxVal) {
                maxVal = pct;
                maxIdx = i;
            }
            if (bars[i] != null) {
                int minHeight = dpToPx(16);
                int maxHeight = dpToPx(96);
                int h = minHeight + (int) ((maxHeight - minHeight) * (pct / 100.0f));
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bars[i].getLayoutParams();
                lp.height = h;
                bars[i].setLayoutParams(lp);

                if (pct >= 80) {
                    bars[i].setBackgroundTintList(ColorStateList.valueOf(ThemeHelper.getColorAcentoOscuro(requireContext())));
                } else {
                    bars[i].setBackgroundTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(requireContext())));
                }
            }
        }

        int promedio = suma / 7;
        if (tvHighlightSemanal != null) {
            if (maxVal > 0) {
                tvHighlightSemanal.setText("⭐ " + diasNombre[maxIdx] + ": " + maxVal + "%");
            } else {
                tvHighlightSemanal.setText("🌱 Semana activa");
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM", new Locale("es", "ES"));
        String f1 = sdf.format(cal.getTime());
        cal.add(Calendar.DAY_OF_MONTH, 6);
        String f2 = sdf.format(cal.getTime());
        if (tvRangoFechasSemana != null) {
            tvRangoFechasSemana.setText(f1 + " - " + f2 + " • Promedio: " + promedio + "%");
        }
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

        int pSalud = totSalud > 0 ? (compSalud * 100 / totSalud) : 0;
        int pMente = totMente > 0 ? (compMente * 100 / totMente) : 0;
        int pProd = totProd > 0 ? (compProd * 100 / totProd) : 0;

        if (tvPctSalud != null) tvPctSalud.setText(pSalud + "%");
        if (pbSalud != null) pbSalud.setProgress(pSalud);

        if (tvPctMente != null) tvPctMente.setText(pMente + "%");
        if (pbMente != null) pbMente.setProgress(pMente);

        if (tvPctProductividad != null) tvPctProductividad.setText(pProd + "%");
        if (pbProductividad != null) pbProductividad.setProgress(pProd);
    }

    private View crearItemHabitoEstrella(Habito h) {
        Context ctx = requireContext();
        LinearLayout row = new LinearLayout(ctx);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dpToPx(16), dpToPx(16), dpToPx(16), dpToPx(16));
        row.setBackgroundResource(R.drawable.bg_tarjeta_redondeada);
        row.setElevation(dpToPx(1));

        LinearLayout.LayoutParams marginParams = (LinearLayout.LayoutParams) row.getLayoutParams();
        marginParams.bottomMargin = dpToPx(12);
        row.setLayoutParams(marginParams);

        ImageView iv = new ImageView(ctx);
        int iconSize = dpToPx(44);
        iv.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
        iv.setBackgroundResource(R.drawable.bg_circulo);

        try {
            iv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(h.getColor())));
        } catch (Exception ignored) {
            iv.setBackgroundTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(ctx)));
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

        LinearLayout textLayout = new LinearLayout(ctx);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpText = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        lpText.setMarginStart(dpToPx(14));
        textLayout.setLayoutParams(lpText);

        TextView tvNombre = new TextView(ctx);
        tvNombre.setText(h.getNombre());
        tvNombre.setTextColor(Color.parseColor("#2D3142"));
        tvNombre.setTextSize(15f);
        tvNombre.setTypeface(null, android.graphics.Typeface.BOLD);
        textLayout.addView(tvNombre);

        TextView tvEstado = new TextView(ctx);
        String estadoTexto = h.isCompletadoHoy() ? "✓ Completado hoy" : "Pendiente de cumplir";
        tvEstado.setText(estadoTexto);
        tvEstado.setTextColor(Color.parseColor("#8D909F"));
        tvEstado.setTextSize(12f);
        textLayout.addView(tvEstado);
        row.addView(textLayout);

        TextView tvRachaChip = new TextView(ctx);
        tvRachaChip.setText("🔥 " + h.getRachaDias() + " D");
        tvRachaChip.setTextColor(Color.parseColor("#2D3142"));
        tvRachaChip.setTextSize(11f);
        tvRachaChip.setTypeface(null, android.graphics.Typeface.BOLD);
        tvRachaChip.setPadding(dpToPx(10), dpToPx(5), dpToPx(10), dpToPx(5));
        tvRachaChip.setBackgroundResource(R.drawable.bg_card_destacada_menta);
        row.addView(tvRachaChip);

        row.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, CrearHabitoActivity.class);
            intent.putExtra("EXTRA_HABITO_ID", h.getId());
            startActivity(intent);
        });

        return row;
    }

    private void setupFiltros(View root) {
        TextView fSemana = root.findViewById(R.id.filtro_semana);
        TextView fMes = root.findViewById(R.id.filtro_mes);
        TextView fAnio = root.findViewById(R.id.filtro_anio);

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
                actualizarBotonFiltro(fAnio, fSemana, fAnio);
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

    private void setupAcciones(View root) {
    }

    private int dpToPx(int dp) {
        if (getContext() == null) return dp * 2;
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
