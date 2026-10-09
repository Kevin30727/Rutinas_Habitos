package com.rutinas_habitos;

import android.content.Intent;
import android.os.Bundle;
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

public class AsistentePandaActivity extends AppCompatActivity {

    private TextView tvMensajePanda;
    private EditText etMensajeUsuario;
    private ImageButton btnEnviar;
    private LinearLayout cardPropuesta;
    private ImageView ivPropuestaIcono;
    private TextView tvPropuestaNombre;
    private TextView tvPropuestaMeta;
    private MaterialButton btnAceptarPropuesta;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private Habito habitoPropuestoActual;

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
        ivPropuestaIcono = findViewById(R.id.iv_propuesta_icono);
        tvPropuestaNombre = findViewById(R.id.tv_propuesta_nombre);
        tvPropuestaMeta = findViewById(R.id.tv_propuesta_meta);
        btnAceptarPropuesta = findViewById(R.id.btn_aceptar_propuesta);

        ImageView btnVolver = findViewById(R.id.btn_volver_panda);
        TextView btnSaltar = findViewById(R.id.btn_saltar_panda);

        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }

        if (btnSaltar != null) {
            btnSaltar.setOnClickListener(v -> irAInicio());
        }
    }

    private void setupListeners() {
        // Enviar texto libre
        btnEnviar.setOnClickListener(v -> {
            String texto = etMensajeUsuario.getText().toString().trim();
            if (!texto.isEmpty()) {
                procesarEntrada(texto);
            } else {
                Toast.makeText(this, "Escribe cómo te sientes o selecciona una opción", Toast.LENGTH_SHORT).show();
            }
        });

        // Chips rápidos
        setupChip(R.id.chip_estres, "Siento estrés y cansancio");
        setupChip(R.id.chip_productividad, "Quiero mejorar mi productividad y foco");
        setupChip(R.id.chip_agua, "Quiero tomar más agua");
        setupChip(R.id.chip_lectura, "Quiero leer más libros");
        setupChip(R.id.chip_ejercicio, "Quiero caminar y hacer ejercicio");

        // Aceptar propuesta
        btnAceptarPropuesta.setOnClickListener(v -> {
            if (habitoPropuestoActual != null) {
                long userId = sessionManager.getUserId();
                if (userId <= 0) userId = 1;
                habitoPropuestoActual.setUsuarioId(userId);

                long id = dbHelper.insertarHabito(habitoPropuestoActual);
                if (id != -1) {
                    sessionManager.setPandaOnboarded(true);
                    Toast.makeText(this, "¡Hábito añadido a tu rutina! 🌱✨", Toast.LENGTH_SHORT).show();
                    irAInicio();
                } else {
                    Toast.makeText(this, "Error al guardar el hábito", Toast.LENGTH_SHORT).show();
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
        long userId = sessionManager.getUserId();
        if (userId <= 0) userId = 1;

        PandaAssistantEngine.PropuestaPanda propuesta = PandaAssistantEngine.analizarYProponer(texto, userId);
        habitoPropuestoActual = propuesta.habitoSugerido;

        // Actualizar mensaje del Panda
        tvMensajePanda.setText(propuesta.mensajePanda);

        // Mostrar tarjeta de propuesta
        tvPropuestaNombre.setText(habitoPropuestoActual.getNombre());
        tvPropuestaMeta.setText("Meta: " + habitoPropuestoActual.getMetaCantidad() + " " + habitoPropuestoActual.getUnidad() + " al día");

        // Ajustar icono
        int iconRes = R.drawable.ic_meditacion;
        String iconStr = habitoPropuestoActual.getIcono();
        if ("agua".equals(iconStr)) iconRes = R.drawable.ic_agua;
        else if ("lectura".equals(iconStr)) iconRes = R.drawable.ic_lectura;
        else if ("ejercicio".equals(iconStr)) iconRes = R.drawable.ic_ejercicio;
        else if ("noche".equals(iconStr)) iconRes = R.drawable.ic_noche;
        else if ("corazon".equals(iconStr)) iconRes = R.drawable.ic_corazon;

        ivPropuestaIcono.setImageResource(iconRes);
        cardPropuesta.setVisibility(View.VISIBLE);
    }

    private void irAInicio() {
        sessionManager.setPandaOnboarded(true);
        Intent intent = new Intent(this, InicioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
