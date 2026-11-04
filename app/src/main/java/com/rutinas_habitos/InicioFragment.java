package com.rutinas_habitos;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class InicioFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private LinearLayout llHabitosDinamicosContainer;
    private LinearLayout llEstadoVacioHabitos;

    private TextView tvHabitosListosResumen;
    private TextView tvDescripcionResumen;
    private CircularProgressIndicator cpiHoyFlorece;
    private TextView tvPorcentajeHoy;
    private TextView tvContadorHabitosBadge;
    private TextView tvRachaDiasHeader;
    private TextView tvSaludo;

    private static final int[] IDS_NUM_DIA = {
            R.id.tv_num_dia_1, R.id.tv_num_dia_2, R.id.tv_num_dia_3,
            R.id.tv_num_dia_4, R.id.tv_num_dia_5, R.id.tv_num_dia_6, R.id.tv_num_dia_7
    };
    private static final int[] IDS_LETRA_DIA = {
            R.id.tv_letra_dia_1, R.id.tv_letra_dia_2, R.id.tv_letra_dia_3,
            R.id.tv_letra_dia_4, R.id.tv_letra_dia_5, R.id.tv_letra_dia_6, R.id.tv_letra_dia_7
    };
    private static final int[] IDS_BG_DIA = {
            R.id.bg_dia_1, R.id.bg_dia_2, R.id.bg_dia_3,
            R.id.bg_dia_4, R.id.bg_dia_5, R.id.bg_dia_6, R.id.bg_dia_7
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inicio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Context ctx = requireContext();
        dbHelper = DatabaseHelper.getInstance(ctx);
        sessionManager = new SessionManager(ctx);

        initViews(view);
        configurarCalendario(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() == null) return;
        long userId = sessionManager.getUserId();
        if (userId <= 0) userId = 1;

        dbHelper.verificarYResetearDia(userId);

        if (tvSaludo != null) {
            tvSaludo.setText("¡Hola, " + sessionManager.getUserName() + "! ✨");
        }

        int racha = dbHelper.obtenerMejorRacha(userId);
        if (tvRachaDiasHeader != null) {
            tvRachaDiasHeader.setText("🔥 " + racha + " DÍAS");
        }

        if (cpiHoyFlorece != null) {
            cpiHoyFlorece.setIndicatorColor(ThemeHelper.getColorPrimario(requireContext()));
        }

        cargarHabitosDesdeSQLite(userId);
    }

    private void initViews(View root) {
        llHabitosDinamicosContainer = root.findViewById(R.id.ll_habitos_dinamicos_container);
        llEstadoVacioHabitos = root.findViewById(R.id.ll_estado_vacio_habitos);

        tvHabitosListosResumen = root.findViewById(R.id.tv_habitos_listos_resumen);
        tvDescripcionResumen = root.findViewById(R.id.tv_descripcion_resumen);
        cpiHoyFlorece = root.findViewById(R.id.cpi_hoy_florece);
        tvPorcentajeHoy = root.findViewById(R.id.tv_porcentaje_hoy);
        tvContadorHabitosBadge = root.findViewById(R.id.tv_contador_habitos_badge);
        tvRachaDiasHeader = root.findViewById(R.id.tv_racha_dias_header);
        tvSaludo = root.findViewById(R.id.tv_saludo_usuario);
    }

    private void cargarHabitosDesdeSQLite(long userId) {
        if (llHabitosDinamicosContainer == null || getContext() == null) return;
        llHabitosDinamicosContainer.removeAllViews();

        List<Habito> habitos = dbHelper.obtenerHabitosPorUsuario(userId);

        if (habitos.isEmpty()) {
            if (llEstadoVacioHabitos != null) llEstadoVacioHabitos.setVisibility(View.VISIBLE);
            actualizarResumenSuperior(0, 0);
            return;
        }

        if (llEstadoVacioHabitos != null) llEstadoVacioHabitos.setVisibility(View.GONE);

        int completados = 0;
        for (Habito h : habitos) {
            if (h.isCompletadoHoy()) completados++;
            View tarjeta = crearVistaHabito(h, userId);
            llHabitosDinamicosContainer.addView(tarjeta);
        }

        actualizarResumenSuperior(completados, habitos.size());
    }

    private void actualizarResumenSuperior(int completados, int total) {
        if (tvHabitosListosResumen != null) {
            tvHabitosListosResumen.setText(completados + " de " + total + " hábitos listos");
        }

        int porcentaje = total == 0 ? 0 : (completados * 100) / total;

        if (cpiHoyFlorece != null) {
            cpiHoyFlorece.setProgress(porcentaje);
        }

        if (tvPorcentajeHoy != null) {
            tvPorcentajeHoy.setText(porcentaje + "%");
        }

        if (tvContadorHabitosBadge != null) {
            tvContadorHabitosBadge.setText(completados + " DE " + total);
        }

        if (tvDescripcionResumen != null) {
            if (total == 0) {
                tvDescripcionResumen.setText("Aún no tienes hábitos registrados hoy.");
            } else if (porcentaje == 100) {
                tvDescripcionResumen.setText("¡Día pleno! Has completado todas tus metas con serenidad 🌱✨");
            } else {
                tvDescripcionResumen.setText("Llevas el " + porcentaje + "% de tus metas diarias cumplidas con calma.");
            }
        }
    }

    private View crearVistaHabito(Habito h, long userId) {
        Context ctx = requireContext();
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setBackgroundResource(R.drawable.bg_tarjeta_redondeada);
        row.setElevation(dpToPx(2));
        int pad = dpToPx(16);
        row.setPadding(pad, pad, pad, pad);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dpToPx(14));
        row.setLayoutParams(params);

        LinearLayout topRow = new LinearLayout(ctx);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);
        topRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        ImageView iv = new ImageView(ctx);
        int size = dpToPx(48);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(size, size);
        iv.setLayoutParams(ivParams);
        iv.setBackgroundResource(h.isCompletadoHoy() ? R.drawable.bg_circulo : R.drawable.bg_circulo_inactivo);
        int p = dpToPx(12);
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

        LinearLayout llText = new LinearLayout(ctx);
        llText.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        textParams.setMargins(dpToPx(14), 0, dpToPx(14), 0);
        llText.setLayoutParams(textParams);

        TextView tvTitulo = new TextView(ctx);
        tvTitulo.setText(h.getNombre());
        tvTitulo.setTextSize(16);
        tvTitulo.setTextColor(Color.parseColor(h.isCompletadoHoy() ? "#8D909F" : "#2D3142"));
        tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);
        if (h.isCompletadoHoy()) {
            tvTitulo.setPaintFlags(tvTitulo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        TextView tvSub = new TextView(ctx);
        String sub = h.getMetaCantidad() + " " + h.getUnidad() + " • " + h.getAreaVida();
        if (h.getHoraRecordatorio() != null && !h.getHoraRecordatorio().isEmpty()) {
            sub += " • " + h.getHoraRecordatorio();
        }
        tvSub.setText(sub);
        tvSub.setTextSize(13);
        tvSub.setTextColor(Color.parseColor("#8D909F"));
        tvSub.setPadding(0, dpToPx(2), 0, 0);

        llText.addView(tvTitulo);
        llText.addView(tvSub);

        TextView tvBadge = new TextView(ctx);
        tvBadge.setTextSize(12);
        tvBadge.setTypeface(null, android.graphics.Typeface.BOLD);
        tvBadge.setPadding(dpToPx(14), dpToPx(8), dpToPx(14), dpToPx(8));
        actualizarBadgeEstado(tvBadge, h.isCompletadoHoy());

        topRow.addView(iv);
        topRow.addView(llText);
        topRow.addView(tvBadge);
        row.addView(topRow);

        boolean esCantidad = "cantidad".equalsIgnoreCase(h.getTipoMeta());
        if (esCantidad) {
            LinearLayout counterRow = new LinearLayout(ctx);
            counterRow.setOrientation(LinearLayout.HORIZONTAL);
            counterRow.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams lpCounter = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lpCounter.setMargins(dpToPx(62), dpToPx(10), 0, 0);
            counterRow.setLayoutParams(lpCounter);

            TextView btnMenos = new TextView(ctx);
            btnMenos.setText("－");
            btnMenos.setTextSize(18f);
            btnMenos.setTextColor(Color.parseColor("#2D3142"));
            btnMenos.setGravity(Gravity.CENTER);
            btnMenos.setBackgroundResource(R.drawable.bg_circulo);
            btnMenos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E9ECF2")));
            LinearLayout.LayoutParams pBtnMenos = new LinearLayout.LayoutParams(dpToPx(34), dpToPx(34));
            btnMenos.setLayoutParams(pBtnMenos);

            TextView tvProgreso = new TextView(ctx);
            tvProgreso.setText(h.getProgresoActual() + " / " + h.getMetaCantidad() + " " + h.getUnidad());
            tvProgreso.setTextColor(Color.parseColor("#2D3142"));
            tvProgreso.setTextSize(13f);
            tvProgreso.setTypeface(null, android.graphics.Typeface.BOLD);
            tvProgreso.setPadding(dpToPx(12), 0, dpToPx(12), 0);

            TextView btnMas = new TextView(ctx);
            LinearLayout.LayoutParams pBtnMas = new LinearLayout.LayoutParams(dpToPx(34), dpToPx(34));
            btnMas.setLayoutParams(pBtnMas);
            btnMas.setGravity(Gravity.CENTER);
            btnMas.setBackgroundResource(R.drawable.bg_circulo);

            final Runnable refrescarBotonBloqueo = () -> {
                boolean estaBloqueado = h.getProgresoActual() >= h.getMetaCantidad();
                if (estaBloqueado) {
                    btnMas.setText("🔒");
                    btnMas.setTextSize(14f);
                    btnMas.setTextColor(Color.parseColor("#8D909F"));
                    btnMas.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E4E7EB")));
                    btnMas.setAlpha(0.7f);
                } else {
                    btnMas.setText("＋");
                    btnMas.setTextSize(18f);
                    btnMas.setTextColor(Color.parseColor("#2D3142"));
                    btnMas.setBackgroundTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(ctx)));
                    btnMas.setAlpha(1.0f);
                }
            };

            refrescarBotonBloqueo.run();

            btnMenos.setOnClickListener(v -> {
                if (h.getProgresoActual() <= 0) {
                    VibrationHelper.vibrarClick(ctx);
                    return;
                }
                VibrationHelper.vibrarClick(ctx);
                int nuevoP = dbHelper.ajustarProgresoHabito(h.getId(), userId, -1);
                h.setProgresoActual(nuevoP);
                boolean comp = nuevoP >= h.getMetaCantidad();
                h.setCompletadoHoy(comp);

                tvProgreso.setText(nuevoP + " / " + h.getMetaCantidad() + " " + h.getUnidad());
                actualizarBadgeEstado(tvBadge, comp);
                iv.setBackgroundResource(comp ? R.drawable.bg_circulo : R.drawable.bg_circulo_inactivo);
                tvTitulo.setTextColor(Color.parseColor(comp ? "#8D909F" : "#2D3142"));
                if (comp) {
                    tvTitulo.setPaintFlags(tvTitulo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    tvTitulo.setPaintFlags(tvTitulo.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }

                refrescarBotonBloqueo.run();
                actualizarResumenGlobal(userId);
            });

            btnMas.setOnClickListener(v -> {
                if (h.getProgresoActual() >= h.getMetaCantidad()) {
                    VibrationHelper.vibrarBloqueo(ctx);
                    Toast.makeText(ctx, "🔒 Meta completada (" + h.getMetaCantidad() + "/" + h.getMetaCantidad() + " " + h.getUnidad() + "). Bloqueado al alcanzar el objetivo.", Toast.LENGTH_SHORT).show();
                    return;
                }

                int nuevoP = dbHelper.ajustarProgresoHabito(h.getId(), userId, 1);
                h.setProgresoActual(nuevoP);
                boolean comp = nuevoP >= h.getMetaCantidad();
                h.setCompletadoHoy(comp);

                tvProgreso.setText(nuevoP + " / " + h.getMetaCantidad() + " " + h.getUnidad());
                actualizarBadgeEstado(tvBadge, comp);
                iv.setBackgroundResource(comp ? R.drawable.bg_circulo : R.drawable.bg_circulo_inactivo);
                tvTitulo.setTextColor(Color.parseColor(comp ? "#8D909F" : "#2D3142"));

                if (comp) {
                    tvTitulo.setPaintFlags(tvTitulo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    VibrationHelper.vibrarExito(ctx);
                    Toast.makeText(ctx, "¡Meta de " + h.getNombre() + " alcanzada (" + nuevoP + "/" + h.getMetaCantidad() + ")! 🎉🔒", Toast.LENGTH_SHORT).show();
                } else {
                    VibrationHelper.vibrarClick(ctx);
                    tvTitulo.setPaintFlags(tvTitulo.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                }

                refrescarBotonBloqueo.run();
                actualizarResumenGlobal(userId);
            });

            counterRow.addView(btnMenos);
            counterRow.addView(tvProgreso);
            counterRow.addView(btnMas);
            row.addView(counterRow);
        }

        topRow.setOnClickListener(v -> {
            boolean nuevoEstado = !h.isCompletadoHoy();
            h.setCompletadoHoy(nuevoEstado);
            dbHelper.toggleHabitoCompletado(h.getId(), userId, nuevoEstado);

            if (nuevoEstado) {
                h.setProgresoActual(h.getMetaCantidad() > 0 ? h.getMetaCantidad() : 1);
                VibrationHelper.vibrarExito(ctx);
                Toast.makeText(ctx, "¡" + h.getNombre() + " cumplido! 🎉✨", Toast.LENGTH_SHORT).show();
            } else {
                h.setProgresoActual(0);
                VibrationHelper.vibrarClick(ctx);
                Toast.makeText(ctx, h.getNombre() + " marcado como pendiente", Toast.LENGTH_SHORT).show();
            }

            cargarHabitosDesdeSQLite(userId);
        });

        row.setOnLongClickListener(v -> {
            new AlertDialog.Builder(ctx)
                    .setTitle(h.getNombre())
                    .setItems(new CharSequence[]{"✏️ Editar hábito", "🗑️ Eliminar hábito"}, (dialog, which) -> {
                        if (which == 0) {
                            Intent intent = new Intent(ctx, CrearHabitoActivity.class);
                            intent.putExtra("EXTRA_HABITO_ID", h.getId());
                            startActivity(intent);
                        } else if (which == 1) {
                            dbHelper.eliminarHabito(h.getId());
                            Toast.makeText(ctx, "Hábito eliminado 🗑️", Toast.LENGTH_SHORT).show();
                            cargarHabitosDesdeSQLite(userId);
                        }
                    })
                    .show();
            return true;
        });

        return row;
    }

    private void actualizarResumenGlobal(long userId) {
        List<Habito> actuales = dbHelper.obtenerHabitosPorUsuario(userId);
        int comp = 0;
        for (Habito hab : actuales) {
            if (hab.isCompletadoHoy()) comp++;
        }
        actualizarResumenSuperior(comp, actuales.size());
    }

    private void actualizarBadgeEstado(TextView tvBadge, boolean completado) {
        Context ctx = getContext();
        if (ctx == null) return;
        if (completado) {
            tvBadge.setText("✓ Hecho");
            tvBadge.setTextColor(Color.WHITE);
            tvBadge.setBackgroundResource(R.drawable.bg_circulo);
            tvBadge.setBackgroundTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(ctx)));
        } else {
            tvBadge.setText("Pendiente");
            tvBadge.setTextColor(Color.parseColor("#8D909F"));
            tvBadge.setBackgroundResource(R.drawable.bg_chip_normal);
            tvBadge.setBackgroundTintList(null);
        }
    }

    private void configurarCalendario(View root) {
        Calendar hoy = Calendar.getInstance();
        Locale idioma = new Locale("es", "ES");

        TextView tvMesSemana = root.findViewById(R.id.tv_mes_semana);
        TextView tvIndicadorHoy = root.findViewById(R.id.tv_indicador_hoy);

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

            TextView tvNumero = root.findViewById(IDS_NUM_DIA[i]);
            TextView tvLetra = root.findViewById(IDS_LETRA_DIA[i]);
            LinearLayout bgDia = root.findViewById(IDS_BG_DIA[i]);

            if (tvNumero != null) {
                tvNumero.setText(String.valueOf(numeroDia));
            }

            boolean esHoy = (hoy.get(Calendar.YEAR) == iterador.get(Calendar.YEAR) &&
                    hoy.get(Calendar.DAY_OF_YEAR) == iterador.get(Calendar.DAY_OF_YEAR));

            if (esHoy && bgDia != null && tvLetra != null) {
                bgDia.setBackgroundResource(R.drawable.bg_tarjeta_redondeada);
                bgDia.setBackgroundTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(requireContext())));
                tvLetra.setTextColor(Color.WHITE);
                tvLetra.setTypeface(null, android.graphics.Typeface.BOLD);
                if (tvNumero != null) {
                    tvNumero.setTextColor(Color.WHITE);
                    tvNumero.setTypeface(null, android.graphics.Typeface.BOLD);
                }
            }

            iterador.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
