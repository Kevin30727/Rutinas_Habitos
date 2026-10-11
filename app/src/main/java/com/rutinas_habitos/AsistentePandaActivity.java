package com.rutinas_habitos;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Pantalla de Chat Interactivo del Asistente Panda 🐼
 * 
 * Funcionalidades:
 * - Experiencia 100% estilo Chat con historial de mensajes (burbujas usuario y panda).
 * - Inteligencia local con AsistenteMotor para respuestas naturales, empáticas y conversacionales.
 * - Integración interactiva de rutinas propuestas dentro del flujo del chat.
 * - Chips de acceso rápido y scroll automático suave.
 */
public class AsistentePandaActivity extends AppCompatActivity {

    private ScrollView scrollChat;
    private LinearLayout llChatContainer;
    private EditText etMensajeUsuario;
    private ImageButton btnEnviar;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistente_panda);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        initViews();
        setupChips();
        setupInput();

        // Mensaje de bienvenida inicial del Panda
        String mensajeBienvenida = "¡Hola! Soy tu compañero Panda 🐼. Sé que empezar o mantener una rutina puede ser abrumador. Cuéntame, ¿cómo te sientes hoy o qué te gustaría mejorar?";
        agregarBurbujaPanda(mensajeBienvenida, null, null);
    }

    private void initViews() {
        scrollChat = findViewById(R.id.scroll_chat);
        llChatContainer = findViewById(R.id.ll_chat_container);
        etMensajeUsuario = findViewById(R.id.et_mensaje_usuario);
        btnEnviar = findViewById(R.id.btn_enviar_mensaje);

        ImageView btnVolver = findViewById(R.id.btn_volver_panda);
        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }

        TextView btnListo = findViewById(R.id.btn_saltar_panda);
        if (btnListo != null) {
            btnListo.setOnClickListener(v -> irAInicio());
        }

        // Cargar animación GIF en el avatar superior si está disponible
        ImageView ivAvatarTop = findViewById(R.id.iv_avatar_panda_top);
        if (ivAvatarTop != null) {
            int gifId = getResources().getIdentifier("panda_animado", "drawable", getPackageName());
            if (gifId == 0) gifId = getResources().getIdentifier("asistente_panda", "drawable", getPackageName());
            if (gifId == 0) gifId = getResources().getIdentifier("splash_logo", "drawable", getPackageName());

            if (gifId != 0) {
                Glide.with(this).asGif().load(gifId).fitCenter().into(ivAvatarTop);
            }
        }
    }

    private void setupInput() {
        btnEnviar.setOnClickListener(v -> procesarEnvioUsuario());

        // Enviar con la tecla Enter/Send del teclado virtual
        etMensajeUsuario.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                procesarEnvioUsuario();
                return true;
            }
            return false;
        });
    }

    private void setupChips() {
        conectarChip(R.id.chip_saludo, "¡Hola Panda! ¿Cómo estás?");
        conectarChip(R.id.chip_como_estas, "¿Cómo te encuentras hoy?");
        conectarChip(R.id.chip_estres, "Siento estrés o cansancio");
        conectarChip(R.id.chip_agua, "Quiero beber más agua");
        conectarChip(R.id.chip_lectura, "Quiero cultivar el hábito de leer");
        conectarChip(R.id.chip_ejercicio, "Quiero caminar o hacer ejercicio");
        conectarChip(R.id.chip_productividad, "Quiero mejorar mi productividad y foco");
        conectarChip(R.id.chip_soleado, "Hoy hace un lindo día soleado");
        conectarChip(R.id.chip_consejo, "¿Qué consejo tienes para mí hoy?");
    }

    private void conectarChip(int viewId, String textoPrompt) {
        View chip = findViewById(viewId);
        if (chip != null) {
            chip.setOnClickListener(v -> {
                etMensajeUsuario.setText(textoPrompt);
                procesarEnvioUsuario();
            });
        }
    }

    private void procesarEnvioUsuario() {
        String texto = etMensajeUsuario.getText().toString().trim();
        if (texto.isEmpty()) {
            Toast.makeText(this, "Escribe un mensaje para conversar con el Panda", Toast.LENGTH_SHORT).show();
            return;
        }

        etMensajeUsuario.setText("");

        // 1. Añadir la burbuja del usuario
        agregarBurbujaUsuario(texto);

        // 2. Procesar con el motor conversacional IA local
        AsistenteMotor.ResultadoAsistente resultado = AsistenteMotor.procesarEntrada(texto);

        // 3. Añadir la respuesta del Panda al flujo del chat
        agregarBurbujaPanda(
                resultado.getMensajePrincipal(),
                resultado.getSugerenciaContextual(),
                resultado.getRutinasDetectadas()
        );
    }

    /**
     * Añade una burbuja de mensaje enviada por el Usuario (alineada a la derecha).
     */
    private void agregarBurbujaUsuario(String mensaje) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.END);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(dpToPx(48), 0, 0, dpToPx(12));
        row.setLayoutParams(rowParams);

        LinearLayout bubble = new LinearLayout(this);
        bubble.setOrientation(LinearLayout.VERTICAL);
        bubble.setBackgroundResource(R.drawable.bg_dialogo_usuario);
        // Usar el color primario de la paleta activa
        bubble.setBackgroundTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(this)));
        int pad = dpToPx(14);
        bubble.setPadding(pad, pad, pad, pad);
        bubble.setElevation(dpToPx(1.5f));

        TextView tvMsg = new TextView(this);
        tvMsg.setText(mensaje);
        tvMsg.setTextColor(Color.parseColor("#2D3142"));
        tvMsg.setTextSize(14.5f);
        bubble.addView(tvMsg);

        TextView tvTime = new TextView(this);
        tvTime.setText(timeFormat.format(new Date()));
        tvTime.setTextColor(Color.parseColor("#6A7080"));
        tvTime.setTextSize(10f);
        tvTime.setGravity(Gravity.END);
        tvTime.setPadding(0, dpToPx(4), 0, 0);
        bubble.addView(tvTime);

        row.addView(bubble);
        llChatContainer.addView(row);

        scrollHaciaAbajo();
    }

    /**
     * Añade una burbuja de respuesta del Panda (alineada a la izquierda con su avatar).
     */
    private void agregarBurbujaPanda(String mensaje, String sugerenciaContextual, List<AsistenteMotor.RutinaPropuesta> rutinas) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.START);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(0, 0, dpToPx(36), dpToPx(14));
        row.setLayoutParams(rowParams);

        // Avatar Chibi del Panda al lado del mensaje
        ImageView ivAvatar = new ImageView(this);
        int avatarSize = dpToPx(34);
        LinearLayout.LayoutParams lpAvatar = new LinearLayout.LayoutParams(avatarSize, avatarSize);
        lpAvatar.setMargins(0, dpToPx(2), dpToPx(8), 0);
        ivAvatar.setLayoutParams(lpAvatar);
        ivAvatar.setBackgroundResource(R.drawable.bg_circulo);
        ivAvatar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EAF7F2")));
        ivAvatar.setImageResource(R.drawable.ic_panda);
        int padAvatar = dpToPx(4);
        ivAvatar.setPadding(padAvatar, padAvatar, padAvatar, padAvatar);
        row.addView(ivAvatar);

        // Contenedor de la burbuja del Panda
        LinearLayout bubble = new LinearLayout(this);
        bubble.setOrientation(LinearLayout.VERTICAL);
        bubble.setBackgroundResource(R.drawable.bg_dialogo_burbuja);
        int pad = dpToPx(14);
        bubble.setPadding(pad, pad, pad, pad);
        bubble.setElevation(dpToPx(2));
        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        bubble.setLayoutParams(bubbleParams);

        // Texto principal de respuesta
        TextView tvMsg = new TextView(this);
        tvMsg.setText(mensaje);
        tvMsg.setTextColor(Color.parseColor("#2D3142"));
        tvMsg.setTextSize(14.5f);
        tvMsg.setLineSpacing(dpToPx(3), 1.0f);
        bubble.addView(tvMsg);

        // Sugerencia contextual sinérgica (si aplica)
        if (sugerenciaContextual != null && !sugerenciaContextual.trim().isEmpty()) {
            TextView tvSugerencia = new TextView(this);
            tvSugerencia.setText("💡 " + sugerenciaContextual);
            tvSugerencia.setTextColor(Color.parseColor("#2D3142"));
            tvSugerencia.setTextSize(12.5f);
            tvSugerencia.setTypeface(null, android.graphics.Typeface.BOLD);
            tvSugerencia.setBackgroundResource(R.drawable.bg_tarjeta_redondeada);
            tvSugerencia.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F4F7F6")));
            int padSug = dpToPx(10);
            tvSugerencia.setPadding(padSug, padSug, padSug, padSug);
            LinearLayout.LayoutParams lpSug = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lpSug.setMargins(0, dpToPx(10), 0, 0);
            tvSugerencia.setLayoutParams(lpSug);
            bubble.addView(tvSugerencia);
        }

        // Tarjeta interactiva de rutinas propuestas dentro del chat
        if (rutinas != null && !rutinas.isEmpty()) {
            LinearLayout cardRutinas = new LinearLayout(this);
            cardRutinas.setOrientation(LinearLayout.VERTICAL);
            cardRutinas.setBackgroundResource(R.drawable.bg_card_destacada_menta);
            int padCard = dpToPx(12);
            cardRutinas.setPadding(padCard, padCard, padCard, padCard);
            LinearLayout.LayoutParams lpCard = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lpCard.setMargins(0, dpToPx(12), 0, 0);
            cardRutinas.setLayoutParams(lpCard);

            TextView tvTituloCard = new TextView(this);
            tvTituloCard.setText("RUTINAS PROPUESTAS PARA TI 🌱");
            tvTituloCard.setTextColor(Color.parseColor("#4CA977"));
            tvTituloCard.setTextSize(11f);
            tvTituloCard.setTypeface(null, android.graphics.Typeface.BOLD);
            tvTituloCard.setLetterSpacing(0.08f);
            cardRutinas.addView(tvTituloCard);

            for (AsistenteMotor.RutinaPropuesta r : rutinas) {
                View itemRutina = crearItemRutinaEnChat(r);
                cardRutinas.addView(itemRutina);
            }

            MaterialButton btnAceptar = new MaterialButton(this);
            btnAceptar.setText(rutinas.size() > 1
                    ? "✓ Aceptar " + rutinas.size() + " rutinas en mi día 🌱"
                    : "✓ Aceptar rutina en mi día 🌱");
            btnAceptar.setTextColor(Color.parseColor("#2D3142"));
            btnAceptar.setTextSize(13.5f);
            btnAceptar.setTypeface(null, android.graphics.Typeface.BOLD);
            btnAceptar.setBackgroundTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(this)));
            btnAceptar.setCornerRadius(dpToPx(20));
            LinearLayout.LayoutParams lpBtn = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(48));
            lpBtn.setMargins(0, dpToPx(10), 0, 0);
            btnAceptar.setLayoutParams(lpBtn);

            btnAceptar.setOnClickListener(v -> {
                long userId = sessionManager.getUserId();
                if (userId <= 0) userId = 1;

                int guardadas = 0;
                for (AsistenteMotor.RutinaPropuesta r : rutinas) {
                    Habito habito = r.toHabito(userId);
                    long id = dbHelper.insertarHabito(habito);
                    if (id != -1) guardadas++;
                }

                if (guardadas > 0) {
                    sessionManager.setPandaOnboarded(true);
                    btnAceptar.setText("✓ ¡" + guardadas + " rutina(s) añadidas a tu día! ✨");
                    btnAceptar.setEnabled(false);
                    btnAceptar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4EBE2")));

                    Toast.makeText(this, "¡Hábitos guardados en SQLite con éxito! 🌱", Toast.LENGTH_SHORT).show();

                    // Mensaje de seguimiento del Panda en el chat
                    agregarBurbujaPanda(
                            "¡Hecho! Tus nuevas rutinas ya forman parte de tu día en SQLite. Puedes revisarlas en la pantalla de inicio o seguir conversando conmigo aquí si deseas 🌸🍃",
                            null, null
                    );
                } else {
                    Toast.makeText(this, "Error al guardar hábitos en la base de datos", Toast.LENGTH_SHORT).show();
                }
            });

            cardRutinas.addView(btnAceptar);
            bubble.addView(cardRutinas);
        }

        // Hora del mensaje
        TextView tvTime = new TextView(this);
        tvTime.setText(timeFormat.format(new Date()));
        tvTime.setTextColor(Color.parseColor("#8D909F"));
        tvTime.setTextSize(10f);
        tvTime.setGravity(Gravity.END);
        tvTime.setPadding(0, dpToPx(6), 0, 0);
        bubble.addView(tvTime);

        row.addView(bubble);
        llChatContainer.addView(row);

        scrollHaciaAbajo();
    }

    private View crearItemRutinaEnChat(AsistenteMotor.RutinaPropuesta r) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dpToPx(6), 0, dpToPx(6));

        ImageView iv = new ImageView(this);
        int size = dpToPx(38);
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
        int pad = dpToPx(8);
        iv.setPadding(pad, pad, pad, pad);
        row.addView(iv);

        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpText = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
        lpText.setMarginStart(dpToPx(10));
        textLayout.setLayoutParams(lpText);

        TextView tvNombre = new TextView(this);
        tvNombre.setText(r.getNombre());
        tvNombre.setTextColor(Color.parseColor("#2D3142"));
        tvNombre.setTextSize(14f);
        tvNombre.setTypeface(null, android.graphics.Typeface.BOLD);
        textLayout.addView(tvNombre);

        TextView tvMeta = new TextView(this);
        tvMeta.setText("Meta: " + r.getMetaCantidad() + " " + r.getUnidad() + " al día • " + r.getAreaVida());
        tvMeta.setTextColor(Color.parseColor("#8D909F"));
        tvMeta.setTextSize(11.5f);
        textLayout.addView(tvMeta);

        row.addView(textLayout);
        return row;
    }

    private void scrollHaciaAbajo() {
        scrollChat.post(() -> scrollChat.fullScroll(View.FOCUS_DOWN));
    }

    private void irAInicio() {
        sessionManager.setPandaOnboarded(true);
        Intent intent = new Intent(this, InicioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private int dpToPx(float dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
