package com.rutinas_habitos;

import android.app.TimePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CrearHabitoActivity extends AppCompatActivity {

    // Vistas de Encabezado y Acción
    private TextView tvTituloPantallaCrear;
    private MaterialButton btnGuardarHabito;
    private MaterialButton btnEliminarHabito;

    // Vistas de Preview
    private View vPreviewColorCirculo;
    private ImageView ivPreviewIcono;
    private TextView tvPreviewArea;
    private TextView tvPreviewFrecuencia;
    private TextView tvPreviewNombre;
    private TextView tvPreviewMeta;

    // Formulario
    private EditText etNombre;
    private TextView tvMetaNumero;
    private TextView tvMetaUnidadLabel;
    private TextView tvHoraRecordatorio;
    private SwitchCompat switchRecordatorio;
    private TextView tvResumenDias;

    // Selector de Sonido de Notificación
    private TextView btnSonidoDefault;
    private TextView btnSonidoZen;
    private TextView btnPreviewSonidoHabito;

    // Botones de Frecuencia y Meta
    private TextView btnFrecDiario;
    private TextView btnFrecFlexible;
    private TextView btnMetaSimple;
    private TextView btnMetaCantidad;

    // Estado del Hábito
    private long editHabitoId = -1;
    private String iconoSeleccionado = "meditacion";
    private String colorSeleccionado = "#B8E0D2";
    private String areaSeleccionada = "Salud & Calma";
    private String frecuenciaSeleccionada = "Diario";
    private String tipoMetaSeleccionada = "cantidad";
    private int cantidadMeta = 15;
    private String unidadSeleccionada = "minutos";
    private String horaRecordatorio = "08:00 AM";
    private String tipoSonidoSeleccionado = "default";
    private final List<String> diasActivos = new ArrayList<>();

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_habito);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupDiasIniciales();
        setupListeners();

        // Verificar si es modo edición
        editHabitoId = getIntent().getLongExtra("EXTRA_HABITO_ID", -1);
        if (editHabitoId != -1) {
            cargarHabitoParaEdicion(editHabitoId);
        } else {
            actualizarPreview();
        }
    }

    private void initViews() {
        tvTituloPantallaCrear = findViewById(R.id.tv_titulo_pantalla_crear);
        btnGuardarHabito = findViewById(R.id.btn_guardar_habito);
        btnEliminarHabito = findViewById(R.id.btn_eliminar_habito);

        vPreviewColorCirculo = findViewById(R.id.v_preview_color_circulo);
        ivPreviewIcono = findViewById(R.id.iv_preview_icono);
        tvPreviewArea = findViewById(R.id.tv_preview_area);
        tvPreviewFrecuencia = findViewById(R.id.tv_preview_frecuencia);
        tvPreviewNombre = findViewById(R.id.tv_preview_nombre);
        tvPreviewMeta = findViewById(R.id.tv_preview_meta);

        etNombre = findViewById(R.id.et_crear_nombre);
        tvMetaNumero = findViewById(R.id.tv_meta_numero);
        tvMetaUnidadLabel = findViewById(R.id.tv_meta_unidad_label);
        tvHoraRecordatorio = findViewById(R.id.tv_hora_recordatorio);
        switchRecordatorio = findViewById(R.id.switch_recordatorio);
        tvResumenDias = findViewById(R.id.tv_resumen_dias);

        btnSonidoDefault = findViewById(R.id.btn_sonido_default);
        btnSonidoZen = findViewById(R.id.btn_sonido_zen);
        btnPreviewSonidoHabito = findViewById(R.id.btn_preview_sonido_habito);

        btnFrecDiario = findViewById(R.id.btn_frec_diario);
        btnFrecFlexible = findViewById(R.id.btn_frec_flexible);
        btnMetaSimple = findViewById(R.id.btn_meta_simple);
        btnMetaCantidad = findViewById(R.id.btn_meta_cantidad);

        findViewById(R.id.btn_volver_crear).setOnClickListener(v -> finish());
        findViewById(R.id.btn_reiniciar_form).setOnClickListener(v -> reiniciarFormulario());
    }

    private void setupDiasIniciales() {
        diasActivos.add("L");
        diasActivos.add("M");
        diasActivos.add("X");
        diasActivos.add("J");
        diasActivos.add("V");
        diasActivos.add("S");
        actualizarResumenDias();
    }

    private void setupListeners() {
        // Actualizar nombre en vivo
        etNombre.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString().trim();
                tvPreviewNombre.setText(text.isEmpty() ? "Nombre del Hábito" : text);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Selección de Iconos
        setupIconSelector(R.id.ic_sel_meditacion, "meditacion", R.drawable.ic_meditacion);
        setupIconSelector(R.id.ic_sel_agua, "agua", R.drawable.ic_agua);
        setupIconSelector(R.id.ic_sel_lectura, "lectura", R.drawable.ic_lectura);
        setupIconSelector(R.id.ic_sel_ejercicio, "ejercicio", R.drawable.ic_ejercicio);
        setupIconSelector(R.id.ic_sel_comida, "comida", R.drawable.ic_comida);
        setupIconSelector(R.id.ic_sel_noche, "noche", R.drawable.ic_noche);
        setupIconSelector(R.id.ic_sel_corazon, "corazon", R.drawable.ic_corazon);
        setupIconSelector(R.id.ic_sel_finanzas, "finanzas", R.drawable.ic_finanzas);

        // Selección de Colores
        setupColorSelector(R.id.color_sel_1, "#B8E0D2");
        setupColorSelector(R.id.color_sel_2, "#B8D8E8");
        setupColorSelector(R.id.color_sel_3, "#F8C8DC");
        setupColorSelector(R.id.color_sel_4, "#D7D9F8");
        setupColorSelector(R.id.color_sel_5, "#C3E8D7");
        setupColorSelector(R.id.color_sel_6, "#F8D4D4");

        // Selección de Áreas
        setupAreaSelector(R.id.chip_area_salud, "Salud & Calma");
        setupAreaSelector(R.id.chip_area_mente, "Mente & Serenidad");
        setupAreaSelector(R.id.chip_area_foco, "Foco & Productividad");
        setupAreaSelector(R.id.chip_area_finanzas, "Finanzas Conscientes");

        // Frecuencia
        btnFrecDiario.setOnClickListener(v -> {
            frecuenciaSeleccionada = "Diario";
            actualizarVistaFrecuencia();
        });

        btnFrecFlexible.setOnClickListener(v -> {
            frecuenciaSeleccionada = "Flexible";
            actualizarVistaFrecuencia();
        });

        // Días de la semana
        setupDiaToggle(R.id.btn_dia_l, "L");
        setupDiaToggle(R.id.btn_dia_m, "M");
        setupDiaToggle(R.id.btn_dia_x, "X");
        setupDiaToggle(R.id.btn_dia_j, "J");
        setupDiaToggle(R.id.btn_dia_v, "V");
        setupDiaToggle(R.id.btn_dia_s, "S");
        setupDiaToggle(R.id.btn_dia_d, "D");

        // Medición de Meta (Simple vs Cantidad)
        btnMetaSimple.setOnClickListener(v -> {
            tipoMetaSeleccionada = "simple";
            actualizarVistaMetaTipo();
        });

        btnMetaCantidad.setOnClickListener(v -> {
            tipoMetaSeleccionada = "cantidad";
            actualizarVistaMetaTipo();
        });

        // Contador + y -
        findViewById(R.id.btn_meta_menos).setOnClickListener(v -> {
            if (cantidadMeta > 1) {
                cantidadMeta--;
                tvMetaNumero.setText(String.valueOf(cantidadMeta));
                actualizarPreview();
            }
        });

        findViewById(R.id.btn_meta_mas).setOnClickListener(v -> {
            cantidadMeta++;
            tvMetaNumero.setText(String.valueOf(cantidadMeta));
            actualizarPreview();
        });

        // Unidades
        setupUnidad(R.id.u_min, "minutos");
        setupUnidad(R.id.u_paginas, "páginas");
        setupUnidad(R.id.u_vasos, "vasos");
        setupUnidad(R.id.u_ml, "ml");
        setupUnidad(R.id.u_veces, "veces");

        // Selector de hora
        tvHoraRecordatorio.setOnClickListener(v -> {
            TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                String amPm = hourOfDay >= 12 ? "PM" : "AM";
                int h = hourOfDay % 12;
                if (h == 0) h = 12;
                horaRecordatorio = String.format(Locale.getDefault(), "%02d:%02d %s", h, minute, amPm);
                tvHoraRecordatorio.setText(horaRecordatorio);
            }, 8, 0, false);
            dialog.show();
        });

        // Selector de Sonido de Notificación
        btnSonidoDefault.setOnClickListener(v -> {
            tipoSonidoSeleccionado = "default";
            actualizarVistaSonido();
        });

        btnSonidoZen.setOnClickListener(v -> {
            tipoSonidoSeleccionado = "zen";
            actualizarVistaSonido();
        });

        btnPreviewSonidoHabito.setOnClickListener(v -> {
            if ("zen".equals(tipoSonidoSeleccionado)) {
                NotificationHelper.reproducirMelodiaZenPreview(this);
                Toast.makeText(this, "🎵 Escuchando Melodía Zen (528Hz Solfeggio)", Toast.LENGTH_SHORT).show();
            } else {
                NotificationHelper.reproducirDefaultPreview(this);
                Toast.makeText(this, "🔔 Escuchando tono de notificación estándar", Toast.LENGTH_SHORT).show();
            }
        });

        // Guardar o Actualizar
        btnGuardarHabito.setOnClickListener(v -> guardarHabito());

        // Eliminar Hábito (Modo Edición)
        btnEliminarHabito.setOnClickListener(v -> confirmarEliminarHabito());
    }

    private void cargarHabitoParaEdicion(long id) {
        Habito h = dbHelper.obtenerHabitoPorId(id);
        if (h == null) {
            Toast.makeText(this, "No se encontró el hábito para editar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Títulos de modo edición
        if (tvTituloPantallaCrear != null) tvTituloPantallaCrear.setText("Editar Hábito");
        btnGuardarHabito.setText("✓ Guardar Cambios");
        btnEliminarHabito.setVisibility(View.VISIBLE);

        // Cargar campos
        etNombre.setText(h.getNombre());
        iconoSeleccionado = (h.getIcono() != null && !h.getIcono().isEmpty()) ? h.getIcono() : "meditacion";
        colorSeleccionado = (h.getColor() != null && !h.getColor().isEmpty()) ? h.getColor() : "#B8E0D2";
        areaSeleccionada = (h.getAreaVida() != null && !h.getAreaVida().isEmpty()) ? h.getAreaVida() : "Salud & Calma";
        frecuenciaSeleccionada = (h.getTipoFrecuencia() != null && !h.getTipoFrecuencia().isEmpty()) ? h.getTipoFrecuencia() : "Diario";

        diasActivos.clear();
        if (h.getDiasSemana() != null && !h.getDiasSemana().isEmpty()) {
            for (String d : h.getDiasSemana().split(",")) {
                String trimmed = d.trim();
                if (!trimmed.isEmpty()) diasActivos.add(trimmed);
            }
        }

        tipoMetaSeleccionada = (h.getTipoMeta() != null && !h.getTipoMeta().isEmpty()) ? h.getTipoMeta() : "cantidad";
        cantidadMeta = h.getMetaCantidad() > 0 ? h.getMetaCantidad() : 15;
        tvMetaNumero.setText(String.valueOf(cantidadMeta));
        unidadSeleccionada = (h.getUnidad() != null && !h.getUnidad().isEmpty()) ? h.getUnidad() : "minutos";
        tvMetaUnidadLabel.setText(" " + unidadSeleccionada);

        if (h.getHoraRecordatorio() != null && !h.getHoraRecordatorio().isEmpty()) {
            horaRecordatorio = h.getHoraRecordatorio();
            tvHoraRecordatorio.setText(horaRecordatorio);
        }
        switchRecordatorio.setChecked(h.isRecordatorioActivo());

        tipoSonidoSeleccionado = "zen".equals(h.getTipoSonidoNotificacion()) ? "zen" : "default";

        // Sincronizar UI
        actualizarVistaIcono();
        vPreviewColorCirculo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colorSeleccionado)));
        actualizarVistaArea();
        actualizarVistaFrecuencia();
        actualizarVistaDias();
        actualizarVistaMetaTipo();
        actualizarVistaSonido();
        actualizarPreview();
        actualizarResumenDias();
    }

    private void actualizarVistaIcono() {
        int resId = R.drawable.ic_meditacion;
        int viewId = R.id.ic_sel_meditacion;

        if ("agua".equals(iconoSeleccionado)) {
            resId = R.drawable.ic_agua;
            viewId = R.id.ic_sel_agua;
        } else if ("lectura".equals(iconoSeleccionado)) {
            resId = R.drawable.ic_lectura;
            viewId = R.id.ic_sel_lectura;
        } else if ("ejercicio".equals(iconoSeleccionado)) {
            resId = R.drawable.ic_ejercicio;
            viewId = R.id.ic_sel_ejercicio;
        } else if ("comida".equals(iconoSeleccionado)) {
            resId = R.drawable.ic_comida;
            viewId = R.id.ic_sel_comida;
        } else if ("noche".equals(iconoSeleccionado)) {
            resId = R.drawable.ic_noche;
            viewId = R.id.ic_sel_noche;
        } else if ("corazon".equals(iconoSeleccionado)) {
            resId = R.drawable.ic_corazon;
            viewId = R.id.ic_sel_corazon;
        } else if ("finanzas".equals(iconoSeleccionado)) {
            resId = R.drawable.ic_finanzas;
            viewId = R.id.ic_sel_finanzas;
        }

        ivPreviewIcono.setImageResource(resId);
        resetIconDrawables();
        ImageView selView = findViewById(viewId);
        if (selView != null) {
            selView.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colorSeleccionado)));
        }
    }

    private void actualizarVistaArea() {
        tvPreviewArea.setText(areaSeleccionada);
        int[] chips = {R.id.chip_area_salud, R.id.chip_area_mente, R.id.chip_area_foco, R.id.chip_area_finanzas};
        for (int id : chips) {
            TextView chip = findViewById(id);
            if (chip != null) {
                if (chip.getText().toString().equalsIgnoreCase(areaSeleccionada)) {
                    chip.setBackgroundResource(R.drawable.bg_chip_seleccionado);
                } else {
                    chip.setBackgroundResource(R.drawable.bg_chip_normal);
                }
            }
        }
    }

    private void actualizarVistaFrecuencia() {
        if ("Diario".equalsIgnoreCase(frecuenciaSeleccionada)) {
            btnFrecDiario.setBackgroundResource(R.drawable.bg_selector_activo);
            btnFrecDiario.setTextColor(Color.parseColor("#2D3142"));
            btnFrecFlexible.setBackground(null);
            btnFrecFlexible.setTextColor(Color.parseColor("#8D909F"));
            tvPreviewFrecuencia.setText("🔄 Diario");
        } else {
            btnFrecFlexible.setBackgroundResource(R.drawable.bg_selector_activo);
            btnFrecFlexible.setTextColor(Color.parseColor("#2D3142"));
            btnFrecDiario.setBackground(null);
            btnFrecDiario.setTextColor(Color.parseColor("#8D909F"));
            tvPreviewFrecuencia.setText("🌿 Flexible");
        }
    }

    private void actualizarVistaDias() {
        int[] dayIds = {R.id.btn_dia_l, R.id.btn_dia_m, R.id.btn_dia_x, R.id.btn_dia_j, R.id.btn_dia_v, R.id.btn_dia_s, R.id.btn_dia_d};
        String[] dayKeys = {"L", "M", "X", "J", "V", "S", "D"};

        for (int i = 0; i < dayIds.length; i++) {
            TextView tv = findViewById(dayIds[i]);
            if (tv != null) {
                if (diasActivos.contains(dayKeys[i])) {
                    tv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B8E0D2")));
                    tv.setTextColor(Color.parseColor("#2D3142"));
                } else {
                    tv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F1F2FD")));
                    tv.setTextColor(Color.parseColor("#8D909F"));
                }
            }
        }
    }

    private void actualizarVistaMetaTipo() {
        if ("simple".equalsIgnoreCase(tipoMetaSeleccionada)) {
            btnMetaSimple.setBackgroundResource(R.drawable.bg_tarjeta_redondeada);
            btnMetaSimple.setTextColor(Color.parseColor("#2D3142"));
            btnMetaCantidad.setBackground(null);
            btnMetaCantidad.setTextColor(Color.parseColor("#8D909F"));
        } else {
            btnMetaCantidad.setBackgroundResource(R.drawable.bg_tarjeta_redondeada);
            btnMetaCantidad.setTextColor(Color.parseColor("#2D3142"));
            btnMetaSimple.setBackground(null);
            btnMetaSimple.setTextColor(Color.parseColor("#8D909F"));
        }
        actualizarPreview();
    }

    private void actualizarVistaSonido() {
        if ("zen".equals(tipoSonidoSeleccionado)) {
            btnSonidoZen.setBackgroundResource(R.drawable.bg_chip_seleccionado);
            btnSonidoZen.setTextColor(Color.parseColor("#2D3142"));
            btnSonidoDefault.setBackgroundResource(R.drawable.bg_chip_normal);
            btnSonidoDefault.setTextColor(Color.parseColor("#8D909F"));
        } else {
            btnSonidoDefault.setBackgroundResource(R.drawable.bg_chip_seleccionado);
            btnSonidoDefault.setTextColor(Color.parseColor("#2D3142"));
            btnSonidoZen.setBackgroundResource(R.drawable.bg_chip_normal);
            btnSonidoZen.setTextColor(Color.parseColor("#8D909F"));
        }
    }

    private void setupIconSelector(int viewId, String nombreIcono, int drawableRes) {
        ImageView iv = findViewById(viewId);
        if (iv != null) {
            iv.setOnClickListener(v -> {
                iconoSeleccionado = nombreIcono;
                ivPreviewIcono.setImageResource(drawableRes);
                resetIconDrawables();
                iv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colorSeleccionado)));
            });
        }
    }

    private void resetIconDrawables() {
        int[] iconIds = {
                R.id.ic_sel_meditacion, R.id.ic_sel_agua, R.id.ic_sel_lectura, R.id.ic_sel_ejercicio,
                R.id.ic_sel_comida, R.id.ic_sel_noche, R.id.ic_sel_corazon, R.id.ic_sel_finanzas
        };
        for (int id : iconIds) {
            ImageView iv = findViewById(id);
            if (iv != null) {
                iv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F2F4F8")));
            }
        }
    }

    private void setupColorSelector(int viewId, String hexColor) {
        View v = findViewById(viewId);
        if (v != null) {
            v.setOnClickListener(view -> {
                colorSeleccionado = hexColor;
                vPreviewColorCirculo.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(hexColor)));
                actualizarVistaIcono();
            });
        }
    }

    private void setupAreaSelector(int viewId, String areaNombre) {
        TextView tv = findViewById(viewId);
        if (tv != null) {
            tv.setOnClickListener(v -> {
                areaSeleccionada = areaNombre;
                actualizarVistaArea();
            });
        }
    }

    private void setupDiaToggle(int viewId, String diaLetra) {
        TextView tv = findViewById(viewId);
        if (tv != null) {
            tv.setOnClickListener(v -> {
                if (diasActivos.contains(diaLetra)) {
                    diasActivos.remove(diaLetra);
                    tv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F1F2FD")));
                    tv.setTextColor(Color.parseColor("#8D909F"));
                } else {
                    diasActivos.add(diaLetra);
                    tv.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B8E0D2")));
                    tv.setTextColor(Color.parseColor("#2D3142"));
                }
                actualizarResumenDias();
            });
        }
    }

    private void actualizarResumenDias() {
        int count = diasActivos.size();
        tvResumenDias.setText("✓ " + count + " días por semana");
    }

    private void setupUnidad(int viewId, String unidad) {
        TextView tv = findViewById(viewId);
        if (tv != null) {
            tv.setOnClickListener(v -> {
                unidadSeleccionada = unidad;
                tvMetaUnidadLabel.setText(" " + unidad);

                int[] units = {R.id.u_min, R.id.u_paginas, R.id.u_vasos, R.id.u_ml, R.id.u_veces};
                for (int id : units) {
                    TextView u = findViewById(id);
                    if (u != null) u.setBackgroundResource(R.drawable.bg_chip_normal);
                }
                tv.setBackgroundResource(R.drawable.bg_chip_seleccionado);
                actualizarPreview();
            });
        }
    }

    private void actualizarPreview() {
        String nombre = etNombre.getText().toString().trim();
        tvPreviewNombre.setText(nombre.isEmpty() ? "Nombre del Hábito" : nombre);

        if ("simple".equals(tipoMetaSeleccionada)) {
            tvPreviewMeta.setText("Meta: Completar cada día");
        } else {
            tvPreviewMeta.setText("Meta: " + cantidadMeta + " " + unidadSeleccionada + " al día");
        }
    }

    private void reiniciarFormulario() {
        etNombre.setText("");
        cantidadMeta = 15;
        tvMetaNumero.setText("15");
        unidadSeleccionada = "minutos";
        tipoSonidoSeleccionado = "default";
        actualizarVistaSonido();
        actualizarPreview();
        Toast.makeText(this, "Formulario restablecido", Toast.LENGTH_SHORT).show();
    }

    private void confirmarEliminarHabito() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Hábito")
                .setMessage("¿Estás seguro de que deseas eliminar este hábito? Perderás su registro y estadísticas.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    if (editHabitoId != -1) {
                        dbHelper.eliminarHabito(editHabitoId);
                        Toast.makeText(this, "Hábito eliminado correctamente 🗑️", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void guardarHabito() {
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa un nombre para el hábito", Toast.LENGTH_SHORT).show();
            return;
        }

        long userId = sessionManager.getUserId();
        if (userId <= 0) userId = 1;

        Habito habito;
        if (editHabitoId != -1) {
            habito = dbHelper.obtenerHabitoPorId(editHabitoId);
            if (habito == null) {
                habito = new Habito();
            }
        } else {
            habito = new Habito();
            habito.setRachaDias(0);
            habito.setCompletadoHoy(false);
        }

        habito.setUsuarioId(userId);
        habito.setNombre(nombre);
        habito.setIcono(iconoSeleccionado);
        habito.setColor(colorSeleccionado);
        habito.setAreaVida(areaSeleccionada);
        habito.setTipoFrecuencia(frecuenciaSeleccionada);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < diasActivos.size(); i++) {
            sb.append(diasActivos.get(i));
            if (i < diasActivos.size() - 1) sb.append(",");
        }
        habito.setDiasSemana(sb.toString());

        habito.setTipoMeta(tipoMetaSeleccionada);
        habito.setMetaCantidad(cantidadMeta);
        habito.setUnidad(unidadSeleccionada);
        habito.setHoraRecordatorio(horaRecordatorio);
        habito.setRecordatorioActivo(switchRecordatorio.isChecked());
        habito.setTipoSonidoNotificacion(tipoSonidoSeleccionado);

        if (editHabitoId != -1) {
            habito.setId(editHabitoId);
            boolean actualizado = dbHelper.actualizarHabito(habito);
            if (actualizado) {
                Toast.makeText(this, "¡Hábito '" + nombre + "' actualizado con éxito! ✨", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Error al actualizar el hábito en la base de datos", Toast.LENGTH_SHORT).show();
            }
        } else {
            long id = dbHelper.insertarHabito(habito);
            if (id != -1) {
                Toast.makeText(this, "¡Hábito '" + nombre + "' guardado con éxito! 🌱", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "Error al guardar el hábito en la base de datos", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
