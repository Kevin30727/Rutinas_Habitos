package com.rutinas_habitos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        configurarNavegacionInferior();
        configurarBotones();
    }

    private void configurarBotones() {
        TextView btnEditar = findViewById(R.id.btn_editar_perfil);
        if (btnEditar != null) {
            btnEditar.setOnClickListener(v ->
                    Toast.makeText(this, "Editar perfil próximamente", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void configurarNavegacionInferior() {
        BottomNavigationView bottomNav = findViewById(R.id.barra_inferior);
        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_perfil);
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_inicio) {
                    Intent intent = new Intent(getApplicationContext(), InicioActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_calendario) {
                    Intent intent = new Intent(getApplicationContext(), CalendarioActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (itemId == R.id.nav_perfil) {
                    return true;
                }
                return false;
            });
        }
    }
}