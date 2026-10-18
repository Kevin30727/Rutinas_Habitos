package com.rutinas_habitos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private EditText etCorreo;
    private EditText etPassword;
    private MaterialButton btnIniciarSesion;
    private MaterialButton btnAccesoDemo;
    private TextView tvIrARegistro;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        etCorreo = findViewById(R.id.et_login_correo);
        etPassword = findViewById(R.id.et_login_password);
        btnIniciarSesion = findViewById(R.id.btn_iniciar_sesion);
        btnAccesoDemo = findViewById(R.id.btn_acceso_demo);
        tvIrARegistro = findViewById(R.id.tv_ir_a_registro);

        btnIniciarSesion.setOnClickListener(v -> intentarLogin());

        btnAccesoDemo.setOnClickListener(v -> ingresarDemo());

        tvIrARegistro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });
    }

    private void intentarLogin() {
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (correo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Usuario usuario = dbHelper.autenticarUsuario(correo, password);
        if (usuario != null) {
            sessionManager.crearSesion(usuario.getId(), usuario.getNombre(), usuario.getCorreo());
            dbHelper.insertarHabitosPorDefecto(usuario.getId());
            Toast.makeText(this, "¡Bienvenida de nuevo, " + usuario.getNombre() + "! ✨", Toast.LENGTH_SHORT).show();
            irAInicio();
        } else {
            Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }

    private void ingresarDemo() {

        String correoDemo = "sofia@rutinapastel.com";
        Usuario demo = dbHelper.autenticarUsuario(correoDemo, "123456");
        long userId;
        if (demo == null) {
            userId = dbHelper.registrarUsuario("Sofía", correoDemo, "123456");
        } else {
            userId = demo.getId();
        }

        dbHelper.insertarHabitosPorDefecto(userId);
        sessionManager.crearSesion(userId, "Sofía", correoDemo);
        Toast.makeText(this, "Ingresaste en modo demo ✨", Toast.LENGTH_SHORT).show();
        irAInicio();
    }

    private void irAInicio() {
        Intent intent = new Intent(LoginActivity.this, InicioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
