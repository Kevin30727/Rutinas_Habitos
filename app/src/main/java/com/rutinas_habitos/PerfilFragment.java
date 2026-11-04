package com.rutinas_habitos;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class PerfilFragment extends Fragment {

    private DatabaseHelper dbHelper;
    private SessionManager sessionManager;

    private TextView tvNombre;
    private TextView tvCorreo;
    private TextView tvMetaBienestar;
    private TextView tvTotalHabitos;
    private TextView tvHabitosHoy;
    private TextView tvMejorRacha;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context ctx = requireContext();
        dbHelper = DatabaseHelper.getInstance(ctx);
        sessionManager = new SessionManager(ctx);

        initViews(view);
        setupListeners(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarDatosRelacionalesUsuario();
    }

    private void initViews(View root) {
        tvNombre = root.findViewById(R.id.tv_perfil_nombre);
        tvCorreo = root.findViewById(R.id.tv_perfil_correo);
        tvMetaBienestar = root.findViewById(R.id.tv_perfil_meta_bienestar);

        tvTotalHabitos = root.findViewById(R.id.tv_stat_total_habitos);
        tvHabitosHoy = root.findViewById(R.id.tv_stat_habitos_hoy);
        tvMejorRacha = root.findViewById(R.id.tv_stat_mejor_racha);
    }

    private void cargarDatosRelacionalesUsuario() {
        if (getContext() == null) return;
        long userId = sessionManager.getUserId();
        if (userId <= 0) userId = 1;

        Usuario usuario = dbHelper.obtenerUsuarioPorId(userId);
        if (usuario != null) {
            if (tvNombre != null) tvNombre.setText(usuario.getNombre());
            if (tvCorreo != null) tvCorreo.setText(usuario.getCorreo());
        } else {
            if (tvNombre != null) tvNombre.setText(sessionManager.getUserName());
            if (tvCorreo != null) tvCorreo.setText(sessionManager.getUserEmail());
        }

        String meta = dbHelper.obtenerMetaBienestar(userId);
        if (tvMetaBienestar != null) {
            tvMetaBienestar.setText("Meta activa: " + meta);
        }

        int totalHabitos = dbHelper.obtenerTotalHabitos(userId);
        int habitosCompletadosHoy = dbHelper.obtenerHabitosCompletadosHoy(userId);
        int mejorRacha = dbHelper.obtenerMejorRacha(userId);

        if (tvTotalHabitos != null) tvTotalHabitos.setText(String.valueOf(totalHabitos));
        if (tvHabitosHoy != null) tvHabitosHoy.setText(habitosCompletadosHoy + " / " + totalHabitos);
        if (tvMejorRacha != null) tvMejorRacha.setText(mejorRacha + " d");
    }

    private void setupListeners(View root) {
        View btnConfigTop = root.findViewById(R.id.btn_abrir_configuracion_top);
        if (btnConfigTop != null) {
            btnConfigTop.setOnClickListener(v -> startActivity(new Intent(requireContext(), ConfiguracionActivity.class)));
        }

        View btnConfigCard = root.findViewById(R.id.btn_abrir_configuracion);
        if (btnConfigCard != null) {
            btnConfigCard.setOnClickListener(v -> startActivity(new Intent(requireContext(), ConfiguracionActivity.class)));
        }

        View btnTonos = root.findViewById(R.id.btn_abrir_tonos_personalizados);
        if (btnTonos != null) {
            btnTonos.setOnClickListener(v -> startActivity(new Intent(requireContext(), TonosActivity.class)));
        }

        View btnCerrarSesion = root.findViewById(R.id.btn_cerrar_sesion);
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(v -> confirmarCerrarSesion());
        }
    }

    private void confirmarCerrarSesion() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Deseas cerrar tu sesión actual en Rutinas Pastel?")
                .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                    sessionManager.cerrarSesion();
                    Toast.makeText(requireContext(), "Sesión cerrada correctamente. ¡Hasta pronto! 🌱", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
