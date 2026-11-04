package com.rutinas_habitos;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class InicioActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNav;
    private FloatingActionButton btnNuevoHabito;
    private String ultimaPaleta;
    private boolean ultimoDescansoVisual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.aplicarTema(this);
        super.onCreate(savedInstanceState);
        ultimaPaleta = ThemeHelper.getPaletaActual(this);
        ultimoDescansoVisual = ThemeHelper.isDescansoVisual(this);

        setContentView(R.layout.activity_inicio);

        NotificationHelper.inicializarCanales(this);

        viewPager = findViewById(R.id.view_pager_principal);
        bottomNav = findViewById(R.id.barra_inferior);
        btnNuevoHabito = findViewById(R.id.btn_nuevo_habito);

        MainPagerAdapter adapter = new MainPagerAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                int navId = R.id.nav_inicio;
                if (position == 1) navId = R.id.nav_habitos;
                else if (position == 2) navId = R.id.nav_calendario;
                else if (position == 3) navId = R.id.nav_perfil;

                if (bottomNav.getSelectedItemId() != navId) {
                    bottomNav.setSelectedItemId(navId);
                }

                if (position == 3) {
                    btnNuevoHabito.hide();
                } else {
                    btnNuevoHabito.show();
                }
            }
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_inicio) {
                viewPager.setCurrentItem(0, true);
                return true;
            } else if (itemId == R.id.nav_habitos) {
                viewPager.setCurrentItem(1, true);
                return true;
            } else if (itemId == R.id.nav_calendario) {
                viewPager.setCurrentItem(2, true);
                return true;
            } else if (itemId == R.id.nav_perfil) {
                viewPager.setCurrentItem(3, true);
                return true;
            }
            return false;
        });

        btnNuevoHabito.setOnClickListener(v -> {
            Intent intent = new Intent(InicioActivity.this, CrearHabitoActivity.class);
            startActivity(intent);
        });

        if (savedInstanceState != null) {
            int savedTab = savedInstanceState.getInt("saved_current_tab", 0);
            viewPager.setCurrentItem(savedTab, false);
        } else if (getIntent() != null) {
            int openTab = getIntent().getIntExtra("open_tab", -1);
            if (openTab >= 0 && openTab < 4) {
                viewPager.setCurrentItem(openTab, false);
            }
        }

        if (getIntent() != null && getIntent().getBooleanExtra("from_notification", false)) {
            NotificationHelper.detenerAudio();
            viewPager.setCurrentItem(0, false);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (viewPager != null) {
            outState.putInt("saved_current_tab", viewPager.getCurrentItem());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String paleta = ThemeHelper.getPaletaActual(this);
        boolean descanso = ThemeHelper.isDescansoVisual(this);
        if (!paleta.equals(ultimaPaleta) || descanso != ultimoDescansoVisual) {
            ultimaPaleta = paleta;
            ultimoDescansoVisual = descanso;
            recreate();
            return;
        }

        ThemeHelper.aplicarEstiloBottomNav(this, bottomNav);
        ThemeHelper.aplicarPaletaEnVista(this, btnNuevoHabito, null);

        if (getIntent() != null && getIntent().getBooleanExtra("from_notification", false)) {
            NotificationHelper.detenerAudio();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null) {
            if (intent.getBooleanExtra("from_notification", false)) {
                NotificationHelper.detenerAudio();
                if (viewPager != null) viewPager.setCurrentItem(0, true);
            }
            int openTab = intent.getIntExtra("open_tab", -1);
            if (openTab >= 0 && openTab < 4 && viewPager != null) {
                viewPager.setCurrentItem(openTab, true);
            }
        }
    }

    public void navegarATab(int position) {
        if (viewPager != null && position >= 0 && position < 4) {
            viewPager.setCurrentItem(position, true);
        }
    }

    private static class MainPagerAdapter extends FragmentStateAdapter {
        public MainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new InicioFragment();
                case 1: return new HabitosFragment();
                case 2: return new CalendarioFragment();
                case 3: return new PerfilFragment();
                default: return new InicioFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}
