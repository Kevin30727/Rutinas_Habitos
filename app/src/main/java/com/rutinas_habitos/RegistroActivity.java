package com.rutinas_habitos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre;
    private EditText etCorreo;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private MaterialButton btnCrearCuenta;
    private TextView tvIrALogin;

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        dbHelper = DatabaseHelper.getInstance(this);
        sessionManager = new SessionManager(this);

        etNombre = findViewById(R.id.et_reg_nombre);
        etCorreo = findViewById(R.id.et_reg_correo);
        etPassword = findViewById(R.id.et_reg_password);
        etConfirmPassword = findViewById(R.id.et_reg_confirm_password);
        btnCrearCuenta = findViewById(R.id.btn_crear_cuenta);
        tvIrALogin = findViewById(R.id.tv_ir_a_login);

        btnCrearCuenta.setOnClickListener(v -> intentarRegistro());

        tvIrALogin.setOnClickListener(v -> finish());
    }

    private void intentarRegistro() {
        String nombre = etNombre.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }

        if (dbHelper.existeCorreo(correo)) {
            Toast.makeText(this, "Ya existe una cuenta con este correo", Toast.LENGTH_SHORT).show();
            return;
        }

        long userId = dbHelper.registrarUsuario(nombre, correo, password);
        if (userId != -1) {
            dbHelper.insertarHabitosPorDefecto(userId);
            sessionManager.crearSesion(userId, nombre, correo);
            Toast.makeText(this, "¡Cuenta creada con éxito! Bienvenido 🌱", Toast.LENGTH_SHORT).show();

            // Llevar al Asistente Panda para su bienvenida interactiva
            Intent intent = new Intent(RegistroActivity.this, AsistentePandaActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error al registrar la cuenta, intenta nuevamente", Toast.LENGTH_SHORT).show();
        }
    }
}
