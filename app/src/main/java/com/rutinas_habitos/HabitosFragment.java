package com.rutinas_habitos;

import android.content.Context;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HabitosFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private ProgressBar pbRendimiento;
    private TextView tvPorcentajeExito;
    private TextView tvRachaDias;
    private LinearLayout llListaHabitosEstrella;

    private TextView tvPctSalud, tvPctMente, tvPctProductividad;
    private ProgressBar pbSalud, pbMente, pbProductividad;

    private View barLunes, barMartes, barMiercoles, barJueves, barViernes, barSabado, barDomingo;
    private TextView tvValLunes, tvValMartes, tvValMiercoles, tvValJueves, tvValViernes, tvValSabado, tvValDomingo;
    private TextView tvHighlightSemanal, tvRangoFechasSemana;

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
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarMetricasYHabitosSQLite();
    }

    private void initViews(View root) {
        pbRendimiento = root.findViewById(R.id.pb_rendimiento_habitos);
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

        tvValLunes = root.findViewById(R.id.tv_val_lunes);
        tvValMartes = root.findViewById(R.id.tv_val_martes);
        tvValMiercoles = root.findViewById(R.id.tv_val_miercoles);
        tvValJueves = root.findViewById(R.id.tv_val_jueves);
        tvValViernes = root.findViewById(R.id.tv_val_viernes);
        tvValSabado = root.findViewById(R.id.tv_val_sabado);
        tvValDomingo = root.findViewById(R.id.tv_val_domingo);

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
        if (pbRendimiento != null) {
            pbRendimiento.setProgress(tasaExito);
            pbRendimiento.setProgressTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(requireContext())));
        }
        if (tvPorcentajeExito != null) {
            tvPorcentajeExito.setText(tasaExito + "%");
            tvPorcentajeExito.setTextColor(ThemeHelper.getColorPrimario(requireContext()));
        }

        if (tvRachaDias != null) {
            tvRachaDias.setText("🔥 " + mejorRacha + " días de racha activa");
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
        TextView[] valTexts = {tvValLunes, tvValMartes, tvValMiercoles, tvValJueves, tvValViernes, tvValSabado, tvValDomingo};
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

            if (valTexts[i] != null) {
                valTexts[i].setText(pct + "%");
                valTexts[i].setTextColor(pct > 0 ? ThemeHelper.getColorPrimario(requireContext()) : Color.parseColor("#8D909F"));
            }

            if (bars[i] != null) {
                int minHeight = dpToPx(18);
                int maxHeight = dpToPx(110);
                int h = minHeight + (int) ((maxHeight - minHeight) * (pct / 100.0f));
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bars[i].getLayoutParams();
                lp.height = h;
                bars[i].setLayoutParams(lp);

                if (pct >= 80) {
                    bars[i].setBackgroundTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(requireContext())));
                } else if (pct > 0) {
                    bars[i].setBackgroundTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(requireContext())));
                    bars[i].setAlpha(0.8f);
                } else {
                    bars[i].setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E0E3E8")));
                    bars[i].setAlpha(1.0f);
                }
            }
        }

        int promedio = suma / 7;
        if (tvHighlightSemanal != null) {
            if (maxVal > 0) {
                tvHighlightSemanal.setText("⭐ " + diasNombre[maxIdx] + ": " + maxVal + "%");
                tvHighlightSemanal.setTextColor(Color.WHITE);
                tvHighlightSemanal.setBackgroundTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(requireContext())));
            } else {
                tvHighlightSemanal.setText("🌱 Semana en curso");
                tvHighlightSemanal.setTextColor(Color.parseColor("#2D3142"));
                tvHighlightSemanal.setBackgroundTintList(null);
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
        if (pbSalud != null) {
            pbSalud.setProgress(pSalud);
            pbSalud.setProgressTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(requireContext())));
        }

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
            int c = Color.parseColor(h.getColor());
            iv.setBackgroundTintList(ColorStateList.valueOf(c));
        } catch (Exception e) {
            iv.setBackgroundTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(ctx)));
        }

        int p = dpToPx(11);
        iv.setPadding(p, p, p, p);

        int iconRes = R.drawable.ic_check;
        if ("agua".equalsIgnoreCase(h.getIcono())) iconRes = R.drawable.ic_agua;
        else if ("lectura".equalsIgnoreCase(h.getIcono())) iconRes = R.drawable.ic_lectura;
        else if ("ejercicio".equalsIgnoreCase(h.getIcono())) iconRes = R.drawable.ic_ejercicio;
        else if ("comida".equalsIgnoreCase(h.getIcono())) iconRes = R.drawable.ic_comida;
        else if ("noche".equalsIgnoreCase(h.getIcono())) iconRes = R.drawable.ic_noche;
        else if ("corazon".equalsIgnoreCase(h.getIcono())) iconRes = R.drawable.ic_corazon;
        else if ("sol".equalsIgnoreCase(h.getIcono())) iconRes = R.drawable.ic_sun;
        else if ("meditacion".equalsIgnoreCase(h.getIcono())) iconRes = R.drawable.ic_meditacion;

        iv.setImageResource(iconRes);
        iv.setColorFilter(Color.parseColor("#2D3142"));

        LinearLayout textLayout = new LinearLayout(ctx);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpText = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        lpText.setMarginStart(dpToPx(14));
        textLayout.setLayoutParams(lpText);

        TextView tvTitulo = new TextView(ctx);
        tvTitulo.setText(h.getNombre());
        tvTitulo.setTextColor(Color.parseColor("#2D3142"));
        tvTitulo.setTextSize(15f);
        tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);
        textLayout.addView(tvTitulo);

        TextView tvSub = new TextView(ctx);
        String sub = h.getAreaVida();
        if (h.getHoraRecordatorio() != null && !h.getHoraRecordatorio().isEmpty()) {
            sub += " • " + h.getHoraRecordatorio();
        }
        tvSub.setText(sub);
        tvSub.setTextColor(Color.parseColor("#8D909F"));
        tvSub.setTextSize(12f);
        textLayout.addView(tvSub);

        TextView tvRacha = new TextView(ctx);
        tvRacha.setText("🔥 " + h.getRachaDias() + " d");
        tvRacha.setTextColor(Color.parseColor("#2D3142"));
        tvRacha.setTextSize(12f);
        tvRacha.setTypeface(null, android.graphics.Typeface.BOLD);
        tvRacha.setPadding(dpToPx(10), dpToPx(6), dpToPx(10), dpToPx(6));
        tvRacha.setBackgroundResource(R.drawable.bg_chip_normal);

        row.addView(iv);
        row.addView(textLayout);
        row.addView(tvRacha);

        return row;
    }

    private void setupFiltros(View root) {
        TextView fSemana = root.findViewById(R.id.filtro_semana);
        TextView fMes = root.findViewById(R.id.filtro_mes);
        TextView fAnio = root.findViewById(R.id.filtro_anio);

        View.OnClickListener listener = v -> {
            VibrationHelper.vibrarClick(requireContext());
            resetFiltro(fSemana);
            resetFiltro(fMes);
            resetFiltro(fAnio);
            if (v instanceof TextView) {
                ((TextView) v).setTextColor(Color.parseColor("#2D3142"));
                ((TextView) v).setTypeface(null, android.graphics.Typeface.BOLD);
                v.setBackgroundResource(R.drawable.bg_chip_normal);
            }
        };

        if (fSemana != null) fSemana.setOnClickListener(listener);
        if (fMes != null) fMes.setOnClickListener(listener);
        if (fAnio != null) fAnio.setOnClickListener(listener);
    }

    private void resetFiltro(TextView tv) {
        if (tv == null) return;
        tv.setTextColor(Color.parseColor("#8D909F"));
        tv.setTypeface(null, android.graphics.Typeface.NORMAL);
        tv.setBackground(null);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
