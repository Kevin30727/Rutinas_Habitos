package com.rutinas_habitos;

import android.util.Log;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper singleton para gestionar la sincronización y operaciones en la nube con Cloud Firestore y Firebase Auth.
 */
public class FirebaseFirestoreHelper {

    private static final String TAG = "FirebaseFirestoreHelper";
    private static FirebaseFirestoreHelper instance;
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;

    private FirebaseFirestoreHelper() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        // Habilitar persistencia offline automática de Firestore
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    public static synchronized FirebaseFirestoreHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseFirestoreHelper();
        }
        return instance;
    }

    public FirebaseFirestore getDb() {
        return db;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    /**
     * Registra el perfil del usuario en Firestore (nombre, correo, fecha)
     */
    public void guardarPerfilUsuario(long usuarioId, String nombre, String correo, String firebaseUid) {
        Map<String, Object> data = new HashMap<>();
        data.put("idLocal", usuarioId);
        data.put("nombre", nombre);
        data.put("correo", correo);
        data.put("firebaseUid", firebaseUid != null ? firebaseUid : "");
        data.put("creadoEn", System.currentTimeMillis());

        db.collection("usuarios")
                .document(String.valueOf(usuarioId))
                .set(data, com.google.firebase.firestore.SetOptions.merge())
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Perfil de usuario sincronizado en Firestore"))
                .addOnFailureListener(e -> Log.e(TAG, "Error sincronizando perfil en Firestore: " + e.getMessage()));
    }

    // Colecciones raíz
    public CollectionReference getHabitosRef(long usuarioId) {
        return db.collection("usuarios")
                .document(String.valueOf(usuarioId))
                .collection("habitos");
    }

    public CollectionReference getRegistrosRef(long usuarioId, long habitoId) {
        return db.collection("usuarios")
                .document(String.valueOf(usuarioId))
                .collection("habitos")
                .document(String.valueOf(habitoId))
                .collection("registros");
    }

    public DocumentReference getAjustesRef(long usuarioId) {
        return db.collection("usuarios")
                .document(String.valueOf(usuarioId))
                .collection("configuracion")
                .document("ajustes");
    }

    /**
     * Sincronizar un hábito en Firestore
     */
    public void guardarHabitoEnNube(long usuarioId, Habito habito, OnCompleteListener listener) {
        if (usuarioId <= 0 || habito == null) return;

        Map<String, Object> data = new HashMap<>();
        data.put("id", habito.getId());
        data.put("nombre", habito.getNombre());
        data.put("descripcion", habito.getDescripcion());
        data.put("icono", habito.getIcono());
        data.put("color", habito.getColor());
        data.put("areaVida", habito.getAreaVida());
        data.put("tipoFrecuencia", habito.getTipoFrecuencia());
        data.put("diasSemana", habito.getDiasSemana());
        data.put("tipoMeta", habito.getTipoMeta());
        data.put("metaCantidad", habito.getMetaCantidad());
        data.put("unidad", habito.getUnidad());
        data.put("horaRecordatorio", habito.getHoraRecordatorio());
        data.put("recordatorioActivo", habito.isRecordatorioActivo());
        data.put("rachaDias", habito.getRachaDias());
        data.put("completadoHoy", habito.isCompletadoHoy());
        data.put("progresoActual", habito.getProgresoActual());
        data.put("fechaCreacion", habito.getFechaCreacion());
        data.put("tipoSonidoNotificacion", habito.getTipoSonidoNotificacion());
        data.put("duracionSonidoSegundos", habito.getDuracionSonidoSegundos());
        data.put("updatedAt", System.currentTimeMillis());

        getHabitosRef(usuarioId)
                .document(String.valueOf(habito.getId()))
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Hábito guardado en Firestore: " + habito.getNombre());
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al guardar hábito en Firestore: " + e.getMessage());
                    if (listener != null) listener.onFailure(e);
                });
    }

    /**
     * Eliminar un hábito de Firestore
     */
    public void eliminarHabitoDeNube(long usuarioId, long habitoId, OnCompleteListener listener) {
        if (usuarioId <= 0 || habitoId <= 0) return;

        getHabitosRef(usuarioId)
                .document(String.valueOf(habitoId))
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Hábito eliminado de Firestore: " + habitoId);
                    if (listener != null) listener.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al eliminar hábito en Firestore: " + e.getMessage());
                    if (listener != null) listener.onFailure(e);
                });
    }

    /**
     * Sincronizar un registro diario en Firestore
     */
    public void guardarRegistroDiarioEnNube(long usuarioId, long habitoId, String fecha, int progreso, boolean completado, String nota) {
        if (usuarioId <= 0 || habitoId <= 0 || fecha == null) return;

        Map<String, Object> data = new HashMap<>();
        data.put("fecha", fecha);
        data.put("progreso", progreso);
        data.put("completado", completado);
        data.put("nota", nota != null ? nota : "");
        data.put("timestamp", System.currentTimeMillis());

        getRegistrosRef(usuarioId, habitoId)
                .document(fecha)
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Registro diario en Firestore actualizado para " + fecha))
                .addOnFailureListener(e -> Log.e(TAG, "Error guardando registro en Firestore: " + e.getMessage()));
    }

    /**
     * Sincronizar ajustes del usuario
     */
    public void guardarAjustesEnNube(long usuarioId, String paleta, boolean vibracion) {
        if (usuarioId <= 0) return;

        Map<String, Object> data = new HashMap<>();
        data.put("paletaColor", paleta);
        data.put("vibracionActivada", vibracion);
        data.put("updatedAt", System.currentTimeMillis());

        getAjustesRef(usuarioId)
                .set(data)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Ajustes de usuario en Firestore guardados"))
                .addOnFailureListener(e -> Log.e(TAG, "Error guardando ajustes en Firestore: " + e.getMessage()));
    }

    public interface OnCompleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }
}
