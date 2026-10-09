package com.rutinas_habitos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "rutinas_habitos.db";
    private static final int DATABASE_VERSION = 1;

    // Tabla Usuarios
    public static final String TABLE_USUARIOS = "usuarios";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NOMBRE = "nombre";
    public static final String COL_USER_CORREO = "correo";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_FECHA = "fecha_registro";

    // Tabla Habitos
    public static final String TABLE_HABITOS = "habitos";
    public static final String COL_HAB_ID = "id";
    public static final String COL_HAB_USUARIO_ID = "usuario_id";
    public static final String COL_HAB_NOMBRE = "nombre";
    public static final String COL_HAB_ICONO = "icono";
    public static final String COL_HAB_COLOR = "color";
    public static final String COL_HAB_AREA = "area_vida";
    public static final String COL_HAB_FRECUENCIA = "tipo_frecuencia";
    public static final String COL_HAB_DIAS = "dias_semana";
    public static final String COL_HAB_TIPO_META = "tipo_meta";
    public static final String COL_HAB_META_CANTIDAD = "meta_cantidad";
    public static final String COL_HAB_UNIDAD = "unidad";
    public static final String COL_HAB_HORA = "hora_recordatorio";
    public static final String COL_HAB_RECORDATORIO = "recordatorio_activo";
    public static final String COL_HAB_RACHA = "racha_dias";
    public static final String COL_HAB_COMPLETADO = "completado_hoy";
    public static final String COL_HAB_PROGRESO = "progreso_actual";
    public static final String COL_HAB_FECHA_CREACION = "fecha_creacion";

    // Tabla Ajustes
    public static final String TABLE_AJUSTES = "ajustes_usuario";
    public static final String COL_AJUSTE_USER_ID = "usuario_id";
    public static final String COL_AJUSTE_VASOS = "vasos_agua";
    public static final String COL_AJUSTE_META_AGUA = "meta_agua";
    public static final String COL_AJUSTE_MODO_SIN_CULPA = "modo_sin_culpa";
    public static final String COL_AJUSTE_MONEDA = "moneda";
    public static final String COL_AJUSTE_PRESUPUESTO = "presupuesto";
    public static final String COL_AJUSTE_OCULTAR_BALANCES = "ocultar_balances";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsuarios = "CREATE TABLE " + TABLE_USUARIOS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NOMBRE + " TEXT NOT NULL, " +
                COL_USER_CORREO + " TEXT NOT NULL UNIQUE, " +
                COL_USER_PASSWORD + " TEXT NOT NULL, " +
                COL_USER_FECHA + " TEXT NOT NULL" +
                ");";

        String createHabitos = "CREATE TABLE " + TABLE_HABITOS + " (" +
                COL_HAB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_HAB_USUARIO_ID + " INTEGER NOT NULL, " +
                COL_HAB_NOMBRE + " TEXT NOT NULL, " +
                COL_HAB_ICONO + " TEXT NOT NULL, " +
                COL_HAB_COLOR + " TEXT NOT NULL, " +
                COL_HAB_AREA + " TEXT NOT NULL, " +
                COL_HAB_FRECUENCIA + " TEXT NOT NULL, " +
                COL_HAB_DIAS + " TEXT NOT NULL, " +
                COL_HAB_TIPO_META + " TEXT NOT NULL, " +
                COL_HAB_META_CANTIDAD + " INTEGER NOT NULL, " +
                COL_HAB_UNIDAD + " TEXT NOT NULL, " +
                COL_HAB_HORA + " TEXT, " +
                COL_HAB_RECORDATORIO + " INTEGER DEFAULT 1, " +
                COL_HAB_RACHA + " INTEGER DEFAULT 0, " +
                COL_HAB_COMPLETADO + " INTEGER DEFAULT 0, " +
                COL_HAB_PROGRESO + " INTEGER DEFAULT 0, " +
                COL_HAB_FECHA_CREACION + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COL_HAB_USUARIO_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COL_USER_ID + ")" +
                ");";

        String createAjustes = "CREATE TABLE " + TABLE_AJUSTES + " (" +
                COL_AJUSTE_USER_ID + " INTEGER PRIMARY KEY, " +
                COL_AJUSTE_VASOS + " INTEGER DEFAULT 6, " +
                COL_AJUSTE_META_AGUA + " INTEGER DEFAULT 8, " +
                COL_AJUSTE_MODO_SIN_CULPA + " INTEGER DEFAULT 1, " +
                COL_AJUSTE_MONEDA + " TEXT DEFAULT 'USD', " +
                COL_AJUSTE_PRESUPUESTO + " REAL DEFAULT 1200.0, " +
                COL_AJUSTE_OCULTAR_BALANCES + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + COL_AJUSTE_USER_ID + ") REFERENCES " + TABLE_USUARIOS + "(" + COL_USER_ID + ")" +
                ");";

        db.execSQL(createUsuarios);
        db.execSQL(createHabitos);
        db.execSQL(createAjustes);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AJUSTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    // ==================== USUARIOS ====================

    public long registrarUsuario(String nombre, String correo, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_NOMBRE, nombre);
        cv.put(COL_USER_CORREO, correo.trim().toLowerCase());
        cv.put(COL_USER_PASSWORD, password);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        cv.put(COL_USER_FECHA, sdf.format(new Date()));

        long id = db.insert(TABLE_USUARIOS, null, cv);
        if (id != -1) {
            // Inicializar ajustes para este usuario
            ContentValues cvAjustes = new ContentValues();
            cvAjustes.put(COL_AJUSTE_USER_ID, id);
            cvAjustes.put(COL_AJUSTE_VASOS, 6);
            cvAjustes.put(COL_AJUSTE_META_AGUA, 8);
            cvAjustes.put(COL_AJUSTE_MODO_SIN_CULPA, 1);
            cvAjustes.put(COL_AJUSTE_MONEDA, "USD");
            cvAjustes.put(COL_AJUSTE_PRESUPUESTO, 1200.0);
            cvAjustes.put(COL_AJUSTE_OCULTAR_BALANCES, 0);
            db.insert(TABLE_AJUSTES, null, cvAjustes);
        }
        return id;
    }

    public Usuario autenticarUsuario(String correo, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIOS + " WHERE LOWER(" + COL_USER_CORREO + ") = ? AND " + COL_USER_PASSWORD + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{correo.trim().toLowerCase(), password});

        Usuario usuario = null;
        if (cursor.moveToFirst()) {
            usuario = new Usuario();
            usuario.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_ID)));
            usuario.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NOMBRE)));
            usuario.setCorreo(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_CORREO)));
            usuario.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_PASSWORD)));
            usuario.setFechaRegistro(cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_FECHA)));
        }
        cursor.close();
        return usuario;
    }

    public boolean existeCorreo(String correo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_USER_ID + " FROM " + TABLE_USUARIOS + " WHERE LOWER(" + COL_USER_CORREO + ") = ?";
        Cursor cursor = db.rawQuery(query, new String[]{correo.trim().toLowerCase()});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    // ==================== HÁBITOS ====================

    public long insertarHabito(Habito habito) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_HAB_USUARIO_ID, habito.getUsuarioId());
        cv.put(COL_HAB_NOMBRE, habito.getNombre());
        cv.put(COL_HAB_ICONO, habito.getIcono());
        cv.put(COL_HAB_COLOR, habito.getColor());
        cv.put(COL_HAB_AREA, habito.getAreaVida());
        cv.put(COL_HAB_FRECUENCIA, habito.getTipoFrecuencia());
        cv.put(COL_HAB_DIAS, habito.getDiasSemana());
        cv.put(COL_HAB_TIPO_META, habito.getTipoMeta());
        cv.put(COL_HAB_META_CANTIDAD, habito.getMetaCantidad());
        cv.put(COL_HAB_UNIDAD, habito.getUnidad());
        cv.put(COL_HAB_HORA, habito.getHoraRecordatorio());
        cv.put(COL_HAB_RECORDATORIO, habito.isRecordatorioActivo() ? 1 : 0);
        cv.put(COL_HAB_RACHA, habito.getRachaDias());
        cv.put(COL_HAB_COMPLETADO, habito.isCompletadoHoy() ? 1 : 0);
        cv.put(COL_HAB_PROGRESO, habito.getProgresoActual());

        if (habito.getFechaCreacion() == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            cv.put(COL_HAB_FECHA_CREACION, sdf.format(new Date()));
        } else {
            cv.put(COL_HAB_FECHA_CREACION, habito.getFechaCreacion());
        }

        return db.insert(TABLE_HABITOS, null, cv);
    }

    public List<Habito> obtenerHabitosPorUsuario(long usuarioId) {
        List<Habito> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_HABITOS + " WHERE " + COL_HAB_USUARIO_ID + " = ? ORDER BY " + COL_HAB_ID + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(usuarioId)});

        if (cursor.moveToFirst()) {
            do {
                Habito h = new Habito();
                h.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_HAB_ID)));
                h.setUsuarioId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_HAB_USUARIO_ID)));
                h.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(COL_HAB_NOMBRE)));
                h.setIcono(cursor.getString(cursor.getColumnIndexOrThrow(COL_HAB_ICONO)));
                h.setColor(cursor.getString(cursor.getColumnIndexOrThrow(COL_HAB_COLOR)));
                h.setAreaVida(cursor.getString(cursor.getColumnIndexOrThrow(COL_HAB_AREA)));
                h.setTipoFrecuencia(cursor.getString(cursor.getColumnIndexOrThrow(COL_HAB_FRECUENCIA)));
                h.setDiasSemana(cursor.getString(cursor.getColumnIndexOrThrow(COL_HAB_DIAS)));
                h.setTipoMeta(cursor.getString(cursor.getColumnIndexOrThrow(COL_HAB_TIPO_META)));
                h.setMetaCantidad(cursor.getInt(cursor.getColumnIndexOrThrow(COL_HAB_META_CANTIDAD)));
                h.setUnidad(cursor.getString(cursor.getColumnIndexOrThrow(COL_HAB_UNIDAD)));
                h.setHoraRecordatorio(cursor.getString(cursor.getColumnIndexOrThrow(COL_HAB_HORA)));
                h.setRecordatorioActivo(cursor.getInt(cursor.getColumnIndexOrThrow(COL_HAB_RECORDATORIO)) == 1);
                h.setRachaDias(cursor.getInt(cursor.getColumnIndexOrThrow(COL_HAB_RACHA)));
                h.setCompletadoHoy(cursor.getInt(cursor.getColumnIndexOrThrow(COL_HAB_COMPLETADO)) == 1);
                h.setProgresoActual(cursor.getInt(cursor.getColumnIndexOrThrow(COL_HAB_PROGRESO)));
                h.setFechaCreacion(cursor.getString(cursor.getColumnIndexOrThrow(COL_HAB_FECHA_CREACION)));
                lista.add(h);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public void toggleHabitoCompletado(long habitoId, boolean completado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_HAB_COMPLETADO, completado ? 1 : 0);
        db.update(TABLE_HABITOS, cv, COL_HAB_ID + " = ?", new String[]{String.valueOf(habitoId)});
    }

    public void insertarHabitosPorDefecto(long usuarioId) {
        List<Habito> existentes = obtenerHabitosPorUsuario(usuarioId);
        if (!existentes.isEmpty()) return;

        // 1. Meditación
        Habito h1 = new Habito();
        h1.setUsuarioId(usuarioId);
        h1.setNombre("Meditación matutina");
        h1.setIcono("meditacion");
        h1.setColor("#B8E0D2");
        h1.setAreaVida("Salud");
        h1.setTipoFrecuencia("Diario");
        h1.setDiasSemana("L,M,M,J,V,S");
        h1.setTipoMeta("cantidad");
        h1.setMetaCantidad(15);
        h1.setUnidad("min");
        h1.setHoraRecordatorio("08:00 AM");
        h1.setCompletadoHoy(true);
        h1.setRachaDias(21);
        insertarHabito(h1);

        // 2. Beber 2L de agua
        Habito h2 = new Habito();
        h2.setUsuarioId(usuarioId);
        h2.setNombre("Beber 2L de agua");
        h2.setIcono("agua");
        h2.setColor("#B8D8E8");
        h2.setAreaVida("Salud");
        h2.setTipoFrecuencia("Diario");
        h2.setDiasSemana("L,M,M,J,V,S,D");
        h2.setTipoMeta("cantidad");
        h2.setMetaCantidad(8);
        h2.setUnidad("vasos");
        h2.setHoraRecordatorio("10:00 AM");
        h2.setCompletadoHoy(false);
        h2.setRachaDias(12);
        insertarHabito(h2);

        // 3. Lectura consciente
        Habito h3 = new Habito();
        h3.setUsuarioId(usuarioId);
        h3.setNombre("Lectura consciente");
        h3.setIcono("lectura");
        h3.setColor("#F8C8DC");
        h3.setAreaVida("Mente");
        h3.setTipoFrecuencia("Diario");
        h3.setDiasSemana("L,M,M,J,V,S,D");
        h3.setTipoMeta("cantidad");
        h3.setMetaCantidad(20);
        h3.setUnidad("páginas");
        h3.setHoraRecordatorio("09:00 PM");
        h3.setCompletadoHoy(false);
        h3.setRachaDias(8);
        insertarHabito(h3);

        // 4. Paseo al aire libre
        Habito h4 = new Habito();
        h4.setUsuarioId(usuarioId);
        h4.setNombre("Paseo al aire libre");
        h4.setIcono("ejercicio");
        h4.setColor("#D7D9F8");
        h4.setAreaVida("Salud");
        h4.setTipoFrecuencia("Diario");
        h4.setDiasSemana("L,M,M,J,V");
        h4.setTipoMeta("cantidad");
        h4.setMetaCantidad(30);
        h4.setUnidad("min");
        h4.setHoraRecordatorio("06:30 PM");
        h4.setCompletadoHoy(false);
        h4.setRachaDias(5);
        insertarHabito(h4);
    }

    // ==================== AJUSTES ====================

    public void actualizarVasosAgua(long usuarioId, int vasos) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_AJUSTE_VASOS, vasos);
        int rows = db.update(TABLE_AJUSTES, cv, COL_AJUSTE_USER_ID + " = ?", new String[]{String.valueOf(usuarioId)});
        if (rows == 0) {
            cv.put(COL_AJUSTE_USER_ID, usuarioId);
            db.insert(TABLE_AJUSTES, null, cv);
        }
    }

    public int obtenerVasosAgua(long usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_AJUSTE_VASOS + " FROM " + TABLE_AJUSTES + " WHERE " + COL_AJUSTE_USER_ID + " = ?",
                new String[]{String.valueOf(usuarioId)});
        int vasos = 6;
        if (cursor.moveToFirst()) {
            vasos = cursor.getInt(0);
        }
        cursor.close();
        return vasos;
    }

    public void actualizarModoSinCulpa(long usuarioId, boolean activo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_AJUSTE_MODO_SIN_CULPA, activo ? 1 : 0);
        db.update(TABLE_AJUSTES, cv, COL_AJUSTE_USER_ID + " = ?", new String[]{String.valueOf(usuarioId)});
    }

    public boolean obtenerModoSinCulpa(long usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_AJUSTE_MODO_SIN_CULPA + " FROM " + TABLE_AJUSTES + " WHERE " + COL_AJUSTE_USER_ID + " = ?",
                new String[]{String.valueOf(usuarioId)});
        boolean activo = true;
        if (cursor.moveToFirst()) {
            activo = cursor.getInt(0) == 1;
        }
        cursor.close();
        return activo;
    }
}
