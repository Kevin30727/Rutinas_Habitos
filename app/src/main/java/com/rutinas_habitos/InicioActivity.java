package com.rutinas_habitos;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class InicioActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private LinearLayout llHabitosDinamicosContainer;
    private LinearLayout llEstadoVacioHabitos;

    // Métricas dinámicas superiores
    private TextView tvHabitosListosResumen;
    private TextView tvDescripcionResumen;
    private CircularProgressIndicator cpiHoyFlorece;
    private TextView tvPorcentajeHoy;
    private TextView tvContadorHabitosBadge;

    // Burbuja de saludo del Panda Chibi y auto-desaparición a los 10 segundos
    private View llBurbujaPandaNav;
    private final Handler burbujaHandler = new Handler(Looper.getMainLooper());
    private final Runnable ocultarBurbujaRunnable = new Runnable() {
        @Override
        public void run() {
            if (llBurbujaPandaNav != null && llBurbujaPandaNav.getVisibility() == View.VISIBLE) {
                llBurbujaPandaNav.animate()
                        .alpha(0f)
                        .setDuration(400)
                        .withEndAction(() -> llBurbujaPandaNav.setVisibility(View.GONE))
                        .start();
            }
        }
    };

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        NotificationHelper.inicializarCanales(this);

        initViews();
        setupPandaAvatars();
        configurarCalendario();
        configurarNavegacion();

        long userId = sessionManager.getUserId();
        if (userId <= 0) userId = 1;
        cargarHabitosDesdeSQLite(userId);
    }

    private void initViews() {
        llHabitosDinamicosContainer = findViewById(R.id.ll_habitos_dinamicos_container);
        llEstadoVacioHabitos = findViewById(R.id.ll_estado_vacio_habitos);

        tvHabitosListosResumen = findViewById(R.id.tv_habitos_listos_resumen);
        tvDescripcionResumen = findViewById(R.id.tv_descripcion_resumen);
        cpiHoyFlorece = findViewById(R.id.cpi_hoy_florece);
        tvPorcentajeHoy = findViewById(R.id.tv_porcentaje_hoy);
        tvContadorHabitosBadge = findViewById(R.id.tv_contador_habitos_badge);

        // Saludo personalizado
        TextView tvSaludo = findViewById(R.id.tv_saludo_usuario);
        if (tvSaludo != null) {
            tvSaludo.setText("¡Hola, " + sessionManager.getUserName() + "! ✨");
        }

        // Banner del Panda
        LinearLayout bannerPanda = findViewById(R.id.card_banner_panda);
        if (bannerPanda != null) {
            bannerPanda.setOnClickListener(v -> {
                Intent intent = new Intent(InicioActivity.this, AsistentePandaActivity.class);
                startActivity(intent);
            });
        }

        // Mascota persistente Panda Chibi (apoyando patas sobre el BottomNavigationView)
        ImageView ivPandaChibiNav = findViewById(R.id.iv_panda_chibi_nav);
        if (ivPandaChibiNav != null) {
            ivPandaChibiNav.setOnClickListener(v -> {
                v.animate().scaleX(1.18f).scaleY(0.85f).setDuration(120).withEndAction(() -> {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).withEndAction(() -> {
                        Intent intent = new Intent(InicioActivity.this, AsistentePandaActivity.class);
                        startActivity(intent);
                    }).start();
                }).start();
            });
        }

        // Burbuja de Diálogo sobre el Panda en el navegador (según hora del día)
        llBurbujaPandaNav = findViewById(R.id.ll_burbuja_panda_nav);
        actualizarYMostrarBurbujaPanda();

        // Floating Action Button
        FloatingActionButton fab = findViewById(R.id.btn_nuevo_habito);
        if (fab != null) {
            fab.setBackgroundTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(this)));
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(InicioActivity.this, CrearHabitoActivity.class);
                startActivity(intent);
            });
        }
    }

    /**
     * Carga el GIF animado del Panda si el usuario lo colocó en res/drawable
     */
    private void setupPandaAvatars() {
        int gifId = getResources().getIdentifier("panda_animado", "drawable", getPackageName());
        if (gifId == 0) gifId = getResources().getIdentifier("asistente_panda", "drawable", getPackageName());
        if (gifId == 0) gifId = getResources().getIdentifier("panda_gif", "drawable", getPackageName());

        if (gifId != 0) {
            ImageView ivPandaChibiNav = findViewById(R.id.iv_panda_chibi_nav);
            if (ivPandaChibiNav != null) {
                Glide.with(this).asGif().load(gifId).fitCenter().into(ivPandaChibiNav);
            }

            ImageView ivBannerAvatar = findViewById(R.id.iv_banner_panda_avatar);
            if (ivBannerAvatar != null) {
                Glide.with(this).asGif().load(gifId).fitCenter().into(ivBannerAvatar);
            }

            ImageView ivPandaVacio = findViewById(R.id.iv_panda_vacio);
            if (ivPandaVacio != null) {
                Glide.with(this).asGif().load(gifId).fitCenter().into(ivPandaVacio);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        long userId = sessionManager.getUserId();
        if (userId <= 0) userId = 1;

        TextView tvSaludo = findViewById(R.id.tv_saludo_usuario);
        if (tvSaludo != null) {
            tvSaludo.setText("¡Hola, " + sessionManager.getUserName() + "! ✨");
        }

        setupPandaAvatars();
        actualizarYMostrarBurbujaPanda();
        cargarHabitosDesdeSQLite(userId);

        // Aplicar paleta activa de colores al FloatingActionButton
        FloatingActionButton fab = findViewById(R.id.btn_nuevo_habito);
        if (fab != null) {
            fab.setBackgroundTintList(ColorStateList.valueOf(ThemeHelper.getColorPrimario(this)));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (burbujaHandler != null) {
            burbujaHandler.removeCallbacks(ocultarBurbujaRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (burbujaHandler != null) {
            burbujaHandler.removeCallbacks(ocultarBurbujaRunnable);
        }
    }

    /**
     * Muestra la burbuja de diálogo sobre el Panda Chibi con un saludo dinámico según la hora.
     * La burbuja permanece visible durante exactamente 10 segundos (10,000 ms) y luego desaparece suavemente.
     */
    private void actualizarYMostrarBurbujaPanda() {
        if (llBurbujaPandaNav == null) return;

        TextView tvSaludoBurbuja = findViewById(R.id.tv_saludo_burbuja_panda);
        if (tvSaludoBurbuja != null) {
            Calendar cal = Calendar.getInstance();
            int hora = cal.get(Calendar.HOUR_OF_DAY);
            String saludo;
            if (hora >= 5 && hora < 12) {
                saludo = "¡Buen día! ☀️";
            } else if (hora >= 12 && hora < 19) {
                saludo = "¡Buena tarde! ⛅";
            } else {
                saludo = "¡Buenas noches! 🌙";
            }
            tvSaludoBurbuja.setText(saludo);
        }

        llBurbujaPandaNav.setAlpha(1f);
        llBurbujaPandaNav.setVisibility(View.VISIBLE);
        llBurbujaPandaNav.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, AsistentePandaActivity.class);
            startActivity(intent);
        });

        // Temporizador de 10 segundos para desvanecer la burbuja
        burbujaHandler.removeCallbacks(ocultarBurbujaRunnable);
        burbujaHandler.postDelayed(ocultarBurbujaRunnable, 10000);
    }

    private void configurarNavegacion() {
        BottomNavigationView bottomNav = findViewById(R.id.barra_inferior);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_inicio);
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_habitos) {
                    startActivity(new Intent(InicioActivity.this, HabitosActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_calendario) {
                    startActivity(new Intent(InicioActivity.this, CalendarioActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_perfil) {
                    startActivity(new Intent(InicioActivity.this, PerfilActivity.class));
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

    /**
     * Carga de hábitos dinámica desde SQLite (Cero datos estáticos).
     * Calcula métricas reales y actualiza las tarjetas superiores.
     */
    private void cargarHabitosDesdeSQLite(long userId) {
        if (llHabitosDinamicosContainer == null) return;
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
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackgroundResource(R.drawable.bg_tarjeta_redondeada);
        row.setElevation(dpToPx(2));
        row.setGravity(Gravity.CENTER_VERTICAL);
        int pad = dpToPx(16);
        row.setPadding(pad, pad, pad, pad);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dpToPx(14));
        row.setLayoutParams(params);

        // Icono circular
        ImageView iv = new ImageView(this);
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
        else if ("noche".equalsIgnoreCase(h.getIcono())) iconRes = R.drawable.ic_noche;
        else if ("corazon".equalsIgnoreCase(h.getIcono())) iconRes = R.drawable.ic_corazon;
        else if ("meditacion".equalsIgnoreCase(h.getIcono())) iconRes = R.drawable.ic_meditacion;

        iv.setImageResource(iconRes);
        iv.setColorFilter(Color.parseColor("#2D3142"));

        // Textos del hábito
        LinearLayout llText = new LinearLayout(this);
        llText.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        textParams.setMargins(dpToPx(16), 0, dpToPx(16), 0);
        llText.setLayoutParams(textParams);

        TextView tvTitulo = new TextView(this);
        tvTitulo.setText(h.getNombre());
        tvTitulo.setTextSize(16);
        tvTitulo.setTextColor(Color.parseColor(h.isCompletadoHoy() ? "#8D909F" : "#2D3142"));
        tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);
        if (h.isCompletadoHoy()) {
            tvTitulo.setPaintFlags(tvTitulo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        TextView tvSub = new TextView(this);
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

        // Estado Badge ("Hecho" / "Pendiente")
        TextView tvBadge = new TextView(this);
        tvBadge.setTextSize(12);
        tvBadge.setTypeface(null, android.graphics.Typeface.BOLD);
        tvBadge.setPadding(dpToPx(14), dpToPx(8), dpToPx(14), dpToPx(8));
        actualizarBadgeEstado(tvBadge, h.isCompletadoHoy());

        // Toggle interactivo con recálculo dinámico de estadísticas
        row.setOnClickListener(v -> {
            boolean nuevoEstado = !h.isCompletadoHoy();
            h.setCompletadoHoy(nuevoEstado);
            dbHelper.toggleHabitoCompletado(h.getId(), nuevoEstado);

            iv.setBackgroundResource(nuevoEstado ? R.drawable.bg_circulo : R.drawable.bg_circulo_inactivo);
            tvTitulo.setTextColor(Color.parseColor(nuevoEstado ? "#8D909F" : "#2D3142"));
            if (nuevoEstado) {
                tvTitulo.setPaintFlags(tvTitulo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tvTitulo.setPaintFlags(tvTitulo.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
            actualizarBadgeEstado(tvBadge, nuevoEstado);

            // Recalcular el porcentaje superior
            List<Habito> actuales = dbHelper.obtenerHabitosPorUsuario(userId);
            int comp = 0;
            for (Habito hab : actuales) {
                if (hab.isCompletadoHoy()) comp++;
            }
            actualizarResumenSuperior(comp, actuales.size());

            Toast.makeText(this, nuevoEstado ? "¡Hábito cumplido! 🌱✨" : "Hábito marcado como pendiente", Toast.LENGTH_SHORT).show();
        });

        // Opciones de edición y eliminación (CRUD completo)
        row.setOnLongClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(h.getNombre())
                    .setItems(new CharSequence[]{"✏️ Editar hábito", "🗑️ Eliminar hábito"}, (dialog, which) -> {
                        if (which == 0) {
                            Intent intent = new Intent(this, CrearHabitoActivity.class);
                            intent.putExtra("EXTRA_HABITO_ID", h.getId());
                            startActivity(intent);
                        } else if (which == 1) {
                            dbHelper.eliminarHabito(h.getId());
                            Toast.makeText(this, "Hábito eliminado 🗑️", Toast.LENGTH_SHORT).show();
                            cargarHabitosDesdeSQLite(userId);
                        }
                    })
                    .show();
            return true;
        });

        row.addView(iv);
        row.addView(llText);
        row.addView(tvBadge);

        return row;
    }

    private void actualizarBadgeEstado(TextView tvBadge, boolean completado) {
        if (completado) {
            tvBadge.setText("Hecho");
            tvBadge.setTextColor(Color.parseColor("#2D3142"));
            tvBadge.setBackgroundResource(R.drawable.bg_card_destacada_menta);
        } else {
            tvBadge.setText("Pendiente");
            tvBadge.setTextColor(Color.parseColor("#2D3142"));
            tvBadge.setBackgroundResource(R.drawable.bg_etiqueta_pendiente);
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
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

            TextView tvNumero = findViewById(IDS_NUM_DIA[i]);
            TextView tvLetra = findViewById(IDS_LETRA_DIA[i]);
            LinearLayout bgDia = findViewById(IDS_BG_DIA[i]);

            if (tvNumero != null) {
                tvNumero.setText(String.valueOf(numeroDia));
            }

            boolean esHoy = (hoy.get(Calendar.YEAR) == iterador.get(Calendar.YEAR) &&
                    hoy.get(Calendar.DAY_OF_YEAR) == iterador.get(Calendar.DAY_OF_YEAR));

            if (esHoy && bgDia != null && tvLetra != null) {
                bgDia.setBackgroundResource(R.drawable.bg_tarjeta_redondeada);
                bgDia.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#B8E0D2")));
                tvLetra.setTextColor(Color.parseColor("#2D3142"));
                tvLetra.setTypeface(null, android.graphics.Typeface.BOLD);
            }

            iterador.add(Calendar.DAY_OF_MONTH, 1);
        }
    }
}