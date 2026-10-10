package com.rutinas_habitos;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class AsistentePandaActivity extends AppCompatActivity {

    private TextView tvMensajePanda;
    private EditText etMensajeUsuario;
    private ImageButton btnEnviar;
    private LinearLayout cardPropuesta;
    private TextView tvSugerenciaContextual;
    private LinearLayout llRutinasAcumuladasContainer;
    private MaterialButton btnAceptarPropuesta;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private List<AsistenteMotor.RutinaPropuesta> rutinasPropuestasActuales = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistente_panda);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        tvMensajePanda = findViewById(R.id.tv_mensaje_panda);
        etMensajeUsuario = findViewById(R.id.et_mensaje_usuario);
        btnEnviar = findViewById(R.id.btn_enviar_mensaje);
        cardPropuesta = findViewById(R.id.card_propuesta_panda);
        tvSugerenciaContextual = findViewById(R.id.tv_sugerencia_contextual);
        llRutinasAcumuladasContainer = findViewById(R.id.ll_rutinas_acumuladas_container);
        btnAceptarPropuesta = findViewById(R.id.btn_aceptar_propuesta);

        ImageView btnVolver = findViewById(R.id.btn_volver_panda);
        TextView btnSaltar = findViewById(R.id.btn_saltar_panda);

        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }

        if (btnSaltar != null) {
            btnSaltar.setOnClickListener(v -> irAInicio());
        }

        // Cargar GIF animado para el avatar del Panda si está presente en res/drawable
        ImageView ivAvatarPanda = findViewById(R.id.iv_avatar_panda);
        if (ivAvatarPanda != null) {
            int gifId = getResources().getIdentifier("panda_animado", "drawable", getPackageName());
            if (gifId == 0) {
                gifId = getResources().getIdentifier("asistente_panda", "drawable", getPackageName());
            }
            if (gifId == 0) {
                gifId = getResources().getIdentifier("panda_gif", "drawable", getPackageName());
            }

            if (gifId != 0) {
                com.bumptech.glide.Glide.with(this)
                        .asGif()
                        .load(gifId)
                        .fitCenter()
                        .into(ivAvatarPanda);
            }
        }
    }

    private void setupListeners() {
        // Enviar texto libre
        btnEnviar.setOnClickListener(v -> {
            String texto = etMensajeUsuario.getText().toString().trim();
            if (!texto.isEmpty()) {
                procesarEntrada(texto);
            } else {
                Toast.makeText(this, "Escribe cómo te sientes o qué te gustaría mejorar", Toast.LENGTH_SHORT).show();
            }
        });

        // Chips de entrada rápida
        setupChip(R.id.chip_estres, "Siento estrés y cansancio");
        setupChip(R.id.chip_productividad, "Quiero mejorar mi productividad y foco");
        setupChip(R.id.chip_agua, "Quiero tomar más agua");
        setupChip(R.id.chip_lectura, "Quiero leer más libros");
        setupChip(R.id.chip_ejercicio, "Quiero caminar y hacer ejercicio");

        // Aceptar todas las rutinas acumuladas
        btnAceptarPropuesta.setOnClickListener(v -> {
            if (!rutinasPropuestasActuales.isEmpty()) {
                long userId = sessionManager.getUserId();
                if (userId <= 0) userId = 1;

                int guardadas = 0;
                for (AsistenteMotor.RutinaPropuesta r : rutinasPropuestasActuales) {
                    Habito habito = r.toHabito(userId);
                    long id = dbHelper.insertarHabito(habito);
                    if (id != -1) guardadas++;
                }

                if (guardadas > 0) {
                    sessionManager.setPandaOnboarded(true);
                    Toast.makeText(this, "¡" + guardadas + " rutina(s) añadida(s) con éxito! 🌱✨", Toast.LENGTH_SHORT).show();
                    irAInicio();
                } else {
                    Toast.makeText(this, "Error al guardar las rutinas en la base de datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupChip(int chipId, String texto) {
        Chip chip = findViewById(chipId);
        if (chip != null) {
            chip.setOnClickListener(v -> {
                etMensajeUsuario.setText(texto);
                procesarEntrada(texto);
            });
        }
    }

    private void procesarEntrada(String texto) {
        // Procesamiento a través del motor inteligente multi-intención
        AsistenteMotor.ResultadoAsistente resultado = AsistenteMotor.procesarEntrada(texto);
        rutinasPropuestasActuales = resultado.getRutinasDetectadas();

        // 1. Mensaje personalizado del Panda
        tvMensajePanda.setText(resultado.getMensajePrincipal());

        // 2. Sugerencia contextual sinérgica
        if (resultado.getSugerenciaContextual() != null && !resultado.getSugerenciaContextual().isEmpty()) {
            tvSugerenciaContextual.setText(resultado.getSugerenciaContextual());
            tvSugerenciaContextual.setVisibility(View.VISIBLE);
        } else {
            tvSugerenciaContextual.setVisibility(View.GONE);
        }

        // 3. Renderizado dinámico de todas las rutinas acumuladas
        llRutinasAcumuladasContainer.removeAllViews();
        for (AsistenteMotor.RutinaPropuesta r : rutinasPropuestasActuales) {
            View itemRutina = crearItemRutinaView(r);
            llRutinasAcumuladasContainer.addView(itemRutina);
        }

        btnAceptarPropuesta.setText(rutinasPropuestasActuales.size() > 1
                ? "✓ Aceptar " + rutinasPropuestasActuales.size() + " rutinas y empezar 🌱"
                : "✓ Aceptar hábito y empezar 🌱");

        cardPropuesta.setVisibility(View.VISIBLE);
    }

    private View crearItemRutinaView(AsistenteMotor.RutinaPropuesta r) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 10, 0, 10);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);

        ImageView iv = new ImageView(this);
        int size = (int) (44 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams lpIv = new LinearLayout.LayoutParams(size, size);
        iv.setLayoutParams(lpIv);
        iv.setBackgroundResource(R.drawable.bg_circulo);

        try {
            iv.getBackground().setTint(Color.parseColor(r.getColorHex()));
        } catch (Exception ignored) {
            iv.getBackground().setTint(Color.parseColor("#B8E0D2"));
        }

        int iconRes = R.drawable.ic_meditacion;
        if ("agua".equals(r.getIcono())) iconRes = R.drawable.ic_agua;
        else if ("lectura".equals(r.getIcono())) iconRes = R.drawable.ic_lectura;
        else if ("ejercicio".equals(r.getIcono())) iconRes = R.drawable.ic_ejercicio;
        else if ("noche".equals(r.getIcono())) iconRes = R.drawable.ic_noche;
        else if ("corazon".equals(r.getIcono())) iconRes = R.drawable.ic_corazon;

        iv.setImageResource(iconRes);
        int pad = (int) (10 * getResources().getDisplayMetrics().density);
        iv.setPadding(pad, pad, pad, pad);
        row.addView(iv);

        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpText = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        lpText.setMarginStart((int) (12 * getResources().getDisplayMetrics().density));
        textLayout.setLayoutParams(lpText);

        TextView tvNombre = new TextView(this);
        tvNombre.setText(r.getNombre());
        tvNombre.setTextColor(getResources().getColor(R.color.neutral));
        tvNombre.setTextSize(15f);
        tvNombre.setTypeface(null, android.graphics.Typeface.BOLD);
        textLayout.addView(tvNombre);

        TextView tvMeta = new TextView(this);
        tvMeta.setText("Meta: " + r.getMetaCantidad() + " " + r.getUnidad() + " al día • " + r.getAreaVida());
        tvMeta.setTextColor(getResources().getColor(R.color.text_muted));
        tvMeta.setTextSize(12.5f);
        textLayout.addView(tvMeta);

        row.addView(textLayout);
        return row;
    }

    private void irAInicio() {
        sessionManager.setPandaOnboarded(true);
        Intent intent = new Intent(this, InicioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
