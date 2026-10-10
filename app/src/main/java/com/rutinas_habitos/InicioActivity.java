package com.rutinas_habitos;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class InicioActivity extends AppCompatActivity {

    private int vasosActuales = 6;
    private final int vasosMaximos = 8;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;
    private LinearLayout llHabitosDinamicosContainer;

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

        llHabitosDinamicosContainer = findViewById(R.id.ll_habitos_dinamicos_container);

        long userId = sessionManager.getUserId();
        if (userId <= 0) {
            userId = 1;
        }

        // Saludo personalizado
        TextView tvSaludo = findViewById(R.id.tv_saludo_usuario);
        if (tvSaludo != null) {
            tvSaludo.setText("¡Hola, " + sessionManager.getUserName() + "! ✨");
        }

        // Banner del Panda Zen
        LinearLayout bannerPanda = findViewById(R.id.card_banner_panda);
        if (bannerPanda != null) {
            bannerPanda.setOnClickListener(v -> {
                Intent intent = new Intent(InicioActivity.this, AsistentePandaActivity.class);
                startActivity(intent);
            });
        }

        // Mascota persistente Panda Chibi (rompe la cuarta pared sobre el BottomNavigationView)
        ImageView ivPandaChibiNav = findViewById(R.id.iv_panda_chibi_nav);
        if (ivPandaChibiNav != null) {
            ivPandaChibiNav.setOnClickListener(v -> {
                // Microinteracción táctil orgánica: efecto squish & bounce
                v.animate().scaleX(1.18f).scaleY(0.85f).setDuration(120).withEndAction(() -> {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150).withEndAction(() -> {
                        Intent intent = new Intent(InicioActivity.this, AsistentePandaActivity.class);
                        startActivity(intent);
                    }).start();
                }).start();
            });
        }

        // Floating Action Button
        FloatingActionButton fab = findViewById(R.id.btn_nuevo_habito);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(InicioActivity.this, CrearHabitoActivity.class);
                startActivity(intent);
            });
        }

        configurarCalendario();

        TextView tvMeditacion = findViewById(R.id.tv_meditacion_titulo);
        if (tvMeditacion != null) {
            tvMeditacion.setPaintFlags(tvMeditacion.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // Cargar progreso de agua desde SQLite
        vasosActuales = dbHelper.obtenerVasosAgua(userId);
        configurarBotonesAgua(userId);

        configurarNavegacion();
        cargarHabitosDesdeSQLite(userId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        long userId = sessionManager.getUserId();
        if (userId <= 0) userId = 1;

        // Actualizar saludo por si cambió el perfil
        TextView tvSaludo = findViewById(R.id.tv_saludo_usuario);
        if (tvSaludo != null) {
            tvSaludo.setText("¡Hola, " + sessionManager.getUserName() + "! ✨");
        }

        // Recargar hábitos
        cargarHabitosDesdeSQLite(userId);
    }

    private void configurarNavegacion() {
        BottomNavigationView bottomNav = findViewById(R.id.barra_inferior);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_inicio);
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_calendario) {
                    startActivity(new Intent(this, CalendarioActivity.class));
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
                } else if (itemId == R.id.nav_finanzas) {
                    Toast.makeText(this, "💰 El módulo de Finanzas Conscientes estará disponible próximamente 🌱", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (itemId == R.id.nav_inicio) {
                    return true;
                }
                return false;
            });
        }
    }

    private void configurarBotonesAgua(long userId) {
        TextView btnMenos = findViewById(R.id.btn_agua_menos);
        TextView btnMas = findViewById(R.id.btn_agua_mas);
        TextView tvTextoAgua = findViewById(R.id.tv_texto_agua);
        ProgressBar pbAgua = findViewById(R.id.pb_agua);

        actualizarVistaAgua(tvTextoAgua, pbAgua);

        if (btnMenos != null) {
            btnMenos.setOnClickListener(v -> {
                if (vasosActuales > 0) {
                    vasosActuales--;
                    dbHelper.actualizarVasosAgua(userId, vasosActuales);
                    actualizarVistaAgua(tvTextoAgua, pbAgua);
                }
            });
        }

        if (btnMas != null) {
            btnMas.setOnClickListener(v -> {
                if (vasosActuales < vasosMaximos) {
                    vasosActuales++;
                    dbHelper.actualizarVasosAgua(userId, vasosActuales);
                    actualizarVistaAgua(tvTextoAgua, pbAgua);
                }
            });
        }
    }

    private void actualizarVistaAgua(TextView tvTextoAgua, ProgressBar pbAgua) {
        if (tvTextoAgua != null) {
            tvTextoAgua.setText(vasosActuales + " de " + vasosMaximos + " vasos");
        }
        if (pbAgua != null) {
            pbAgua.setProgress(vasosActuales);
        }
    }

    private void cargarHabitosDesdeSQLite(long userId) {
        if (llHabitosDinamicosContainer == null) return;
        llHabitosDinamicosContainer.removeAllViews();

        List<Habito> habitos = dbHelper.obtenerHabitosPorUsuario(userId);
        if (habitos.isEmpty()) return;

        // Renderizar hábitos creados dinámicamente
        for (Habito h : habitos) {
            // Evitar duplicar los que ya están fijos en el XML inicial si coinciden por nombre
            if ("Meditación matutina".equalsIgnoreCase(h.getNombre()) ||
                "Beber 2L de agua".equalsIgnoreCase(h.getNombre()) ||
                "Lectura consciente".equalsIgnoreCase(h.getNombre()) ||
                "Paseo al aire libre".equalsIgnoreCase(h.getNombre())) {
                continue;
            }

            View tarjeta = crearVistaHabito(h);
            llHabitosDinamicosContainer.addView(tarjeta);
        }
    }

    private View crearVistaHabito(Habito h) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setBackgroundResource(R.drawable.bg_tarjeta_redondeada);
        row.setElevation(dpToPx(2));
        row.setGravity(Gravity.CENTER_VERTICAL);
        int pad = dpToPx(16);
        row.setPadding(pad, pad, pad, pad);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, dpToPx(16));
        row.setLayoutParams(params);

        // Icono
        ImageView iv = new ImageView(this);
        int size = dpToPx(48);
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(size, size);
        iv.setLayoutParams(ivParams);
        iv.setBackgroundResource(h.isCompletadoHoy() ? R.drawable.bg_circulo : R.drawable.bg_circulo_inactivo);
        int p = dpToPx(12);
        iv.setPadding(p, p, p, p);
        iv.setImageResource(R.drawable.ic_check);
        iv.setColorFilter(Color.parseColor("#2D3142"));

        // Textos
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
        String sub = h.getMetaCantidad() + " " + h.getUnidad() + " (" + h.getAreaVida() + ")";
        tvSub.setText(sub);
        tvSub.setTextSize(14);
        tvSub.setTextColor(Color.parseColor("#8D909F"));

        llText.addView(tvTitulo);
        llText.addView(tvSub);

        // Estado Badge
        TextView tvBadge = new TextView(this);
        tvBadge.setTextSize(12);
        tvBadge.setTypeface(null, android.graphics.Typeface.BOLD);
        tvBadge.setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8));
        actualizarBadgeEstado(tvBadge, h.isCompletadoHoy());

        // Toggle al tocar
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

            Toast.makeText(this, nuevoEstado ? "¡Hábito cumplido! 🌱✨" : "Hábito marcado como pendiente", Toast.LENGTH_SHORT).show();
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