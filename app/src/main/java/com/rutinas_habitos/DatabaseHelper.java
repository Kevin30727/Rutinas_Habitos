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

/**
 * SQLiteOpenHelper Dinámico para "Rutinas Pastel".
 * 
 * Cumplimiento de Requerimientos:
 * 1. Tablas oficiales: Usuario, AjustesUsuario, Habito, RegistroDiario, TonoPersonalizado.
 * 2. Cero datos estáticos / hardcodeados: las tablas nacen completamente vacías.
 * 3. Persistencia de datos 100% relacional en SQLite.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "rutinas_habitos.db";
    private static final int DATABASE_VERSION = 2; // Actualizado para esquema dinámico limpio

    // 1. Tabla Usuario
    public static final String TABLE_USUARIO = "Usuario";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NOMBRE = "nombre";
    public static final String COL_USER_CORREO = "correo";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_FECHA = "fecha_registro";

    // 2. Tabla AjustesUsuario
    public static final String TABLE_AJUSTES_USUARIO = "AjustesUsuario";
    public static final String COL_PERFIL_ID = "id";
    public static final String COL_PERFIL_USER_ID = "usuario_id";
    public static final String COL_PERFIL_MODO_SIN_CULPA = "modo_sin_culpa";
    public static final String COL_PERFIL_META_BIENESTAR = "meta_bienestar";
    public static final String COL_PERFIL_ULTIMA_INTERACCION = "ultima_interaccion";
    public static final String COL_PERFIL_VASOS_AGUA = "vasos_agua";
    public static final String COL_PERFIL_META_AGUA = "meta_agua";
    public static final String COL_PERFIL_MONEDA = "moneda";

    // 3. Tabla Habito
    public static final String TABLE_HABITO = "Habito";
    public static final String COL_HAB_ID = "id";
    public static final String COL_HAB_USUARIO_ID = "usuario_id";
    public static final String COL_HAB_NOMBRE = "nombre";
    public static final String COL_HAB_DESCRIPCION = "descripcion";
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
    public static final String COL_HAB_SONIDO = "tipo_sonido";
    public static final String COL_HAB_DURACION_SONIDO = "duracion_sonido";

    // 4. Tabla RegistroDiario
    public static final String TABLE_REGISTRO_DIARIO = "RegistroDiario";
    public static final String COL_REG_ID = "id";
    public static final String COL_REG_HABITO_ID = "habito_id";
    public static final String COL_REG_USUARIO_ID = "usuario_id";
    public static final String COL_REG_FECHA = "fecha";
    public static final String COL_REG_COMPLETADO = "completado";
    public static final String COL_REG_PROGRESO = "progreso_cantidad";
    public static final String COL_REG_NOTAS = "notas";

    // 5. Tabla TonoPersonalizado (Audios MP3 subidos por el usuario)
    public static final String TABLE_TONO_PERSONALIZADO = "TonoPersonalizado";
    public static final String COL_TONO_ID = "id";
    public static final String COL_TONO_USER_ID = "usuario_id";
    public static final String COL_TONO_NOMBRE = "nombre";
    public static final String COL_TONO_RUTA = "ruta_archivo";
    public static final String COL_TONO_DURACION = "duracion";
    public static final String COL_TONO_FECHA = "fecha_subida";

    // Constantes de compatibilidad hacia atrás
    public static final String TABLE_USUARIOS = TABLE_USUARIO;
    public static final String TABLE_HABITOS = TABLE_HABITO;
    public static final String TABLE_AJUSTES = TABLE_AJUSTES_USUARIO;
    public static final String COL_AJUSTE_USER_ID = COL_PERFIL_USER_ID;
    public static final String COL_AJUSTE_MODO_SIN_CULPA = COL_PERFIL_MODO_SIN_CULPA;
    public static final String COL_AJUSTE_MONEDA = COL_PERFIL_MONEDA;
    public static final String COL_AJUSTE_VASOS = COL_PERFIL_VASOS_AGUA;
    public static final String COL_AJUSTE_META_AGUA = COL_PERFIL_META_AGUA;

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
        // Tablas nacen completamente vacías (Cero registros hardcodeados)
        String createUsuario = "CREATE TABLE " + TABLE_USUARIO + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NOMBRE + " TEXT NOT NULL, " +
                COL_USER_CORREO + " TEXT NOT NULL UNIQUE, " +
                COL_USER_PASSWORD + " TEXT NOT NULL, " +
                COL_USER_FECHA + " TEXT NOT NULL" +
                ");";

        String createAjustesUsuario = "CREATE TABLE " + TABLE_AJUSTES_USUARIO + " (" +
                COL_PERFIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PERFIL_USER_ID + " INTEGER NOT NULL, " +
                COL_PERFIL_MODO_SIN_CULPA + " INTEGER DEFAULT 1, " +
                COL_PERFIL_META_BIENESTAR + " TEXT, " +
                COL_PERFIL_ULTIMA_INTERACCION + " TEXT, " +
                COL_PERFIL_VASOS_AGUA + " INTEGER DEFAULT 0, " +
                COL_PERFIL_META_AGUA + " INTEGER DEFAULT 8, " +
                COL_PERFIL_MONEDA + " TEXT DEFAULT 'USD', " +
                "FOREIGN KEY(" + COL_PERFIL_USER_ID + ") REFERENCES " + TABLE_USUARIO + "(" + COL_USER_ID + ") ON DELETE CASCADE" +
                ");";

        String createHabito = "CREATE TABLE " + TABLE_HABITO + " (" +
                COL_HAB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_HAB_USUARIO_ID + " INTEGER NOT NULL, " +
                COL_HAB_NOMBRE + " TEXT NOT NULL, " +
                COL_HAB_DESCRIPCION + " TEXT, " +
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
                COL_HAB_SONIDO + " TEXT DEFAULT 'default', " +
                COL_HAB_DURACION_SONIDO + " INTEGER DEFAULT 30, " +
                "FOREIGN KEY(" + COL_HAB_USUARIO_ID + ") REFERENCES " + TABLE_USUARIO + "(" + COL_USER_ID + ") ON DELETE CASCADE" +
                ");";

        String createRegistroDiario = "CREATE TABLE " + TABLE_REGISTRO_DIARIO + " (" +
                COL_REG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_REG_HABITO_ID + " INTEGER NOT NULL, " +
                COL_REG_USUARIO_ID + " INTEGER NOT NULL, " +
                COL_REG_FECHA + " TEXT NOT NULL, " +
                COL_REG_COMPLETADO + " INTEGER NOT NULL DEFAULT 0, " +
                COL_REG_PROGRESO + " INTEGER DEFAULT 0, " +
                COL_REG_NOTAS + " TEXT, " +
                "FOREIGN KEY(" + COL_REG_HABITO_ID + ") REFERENCES " + TABLE_HABITO + "(" + COL_HAB_ID + ") ON DELETE CASCADE, " +
                "FOREIGN KEY(" + COL_REG_USUARIO_ID + ") REFERENCES " + TABLE_USUARIO + "(" + COL_USER_ID + ") ON DELETE CASCADE" +
                ");";

        String createTonoPersonalizado = "CREATE TABLE " + TABLE_TONO_PERSONALIZADO + " (" +
                COL_TONO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_TONO_USER_ID + " INTEGER NOT NULL, " +
                COL_TONO_NOMBRE + " TEXT NOT NULL, " +
                COL_TONO_RUTA + " TEXT NOT NULL, " +
                COL_TONO_DURACION + " TEXT, " +
                COL_TONO_FECHA + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COL_TONO_USER_ID + ") REFERENCES " + TABLE_USUARIO + "(" + COL_USER_ID + ") ON DELETE CASCADE" +
                ");";

        db.execSQL(createUsuario);
        db.execSQL(createAjustesUsuario);
        db.execSQL(createHabito);
        db.execSQL(createRegistroDiario);
        db.execSQL(createTonoPersonalizado);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        try {
            db.execSQL("ALTER TABLE " + TABLE_HABITO + " ADD COLUMN " + COL_HAB_SONIDO + " TEXT DEFAULT 'default'");
        } catch (Exception ignored) {
        }
        try {
            db.execSQL("ALTER TABLE " + TABLE_HABITO + " ADD COLUMN " + COL_HAB_DURACION_SONIDO + " INTEGER DEFAULT 30");
        } catch (Exception ignored) {
        }
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_TONO_PERSONALIZADO + " (" +
                    COL_TONO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_TONO_USER_ID + " INTEGER NOT NULL, " +
                    COL_TONO_NOMBRE + " TEXT NOT NULL, " +
                    COL_TONO_RUTA + " TEXT NOT NULL, " +
                    COL_TONO_DURACION + " TEXT, " +
                    COL_TONO_FECHA + " TEXT NOT NULL, " +
                    "FOREIGN KEY(" + COL_TONO_USER_ID + ") REFERENCES " + TABLE_USUARIO + "(" + COL_USER_ID + ") ON DELETE CASCADE" +
                    ");");
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTRO_DIARIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AJUSTES_USUARIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);
        onCreate(db);
    }



    // =========================================================================
    // GESTIÓN DE USUARIOS
    // =========================================================================

    public long registrarUsuario(String nombre, String correo, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_NOMBRE, nombre);
        cv.put(COL_USER_CORREO, correo.trim().toLowerCase());
        cv.put(COL_USER_PASSWORD, password);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        cv.put(COL_USER_FECHA, sdf.format(new Date()));

        long id = db.insert(TABLE_USUARIO, null, cv);
        if (id != -1) {
            ContentValues cvPerfil = new ContentValues();
            cvPerfil.put(COL_PERFIL_USER_ID, id);
            cvPerfil.put(COL_PERFIL_MODO_SIN_CULPA, 1);
            cvPerfil.put(COL_PERFIL_META_BIENESTAR, "Equilibrio y calma");
            cvPerfil.put(COL_PERFIL_ULTIMA_INTERACCION, sdf.format(new Date()));
            cvPerfil.put(COL_PERFIL_VASOS_AGUA, 0);
            cvPerfil.put(COL_PERFIL_META_AGUA, 8);
            cvPerfil.put(COL_PERFIL_MONEDA, "USD");
            db.insert(TABLE_AJUSTES_USUARIO, null, cvPerfil);
        }
        return id;
    }

    public Usuario autenticarUsuario(String correo, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIO + " WHERE LOWER(" + COL_USER_CORREO + ") = ? AND " + COL_USER_PASSWORD + " = ?";
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
        String query = "SELECT " + COL_USER_ID + " FROM " + TABLE_USUARIO + " WHERE LOWER(" + COL_USER_CORREO + ") = ?";
        Cursor cursor = db.rawQuery(query, new String[]{correo.trim().toLowerCase()});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    // =========================================================================
    // GESTIÓN DE HÁBITOS DINÁMICOS
    // =========================================================================

    public long insertarHabito(Habito habito) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_HAB_USUARIO_ID, habito.getUsuarioId());
        cv.put(COL_HAB_NOMBRE, habito.getNombre());
        cv.put(COL_HAB_DESCRIPCION, habito.getDescripcion());
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
        cv.put(COL_HAB_SONIDO, habito.getTipoSonidoNotificacion());
        cv.put(COL_HAB_DURACION_SONIDO, habito.getDuracionSonidoSegundos());

        if (habito.getFechaCreacion() == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            cv.put(COL_HAB_FECHA_CREACION, sdf.format(new Date()));
        } else {
            cv.put(COL_HAB_FECHA_CREACION, habito.getFechaCreacion());
        }

        return db.insert(TABLE_HABITO, null, cv);
    }

    public List<Habito> obtenerHabitosPorUsuario(long usuarioId) {
        List<Habito> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_HABITO + " WHERE " + COL_HAB_USUARIO_ID + " = ? ORDER BY " + COL_HAB_ID + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(usuarioId)});

        if (cursor.moveToFirst()) {
            do {
                Habito h = new Habito();
                h.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_HAB_ID)));
                h.setUsuarioId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_HAB_USUARIO_ID)));
                h.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(COL_HAB_NOMBRE)));
                h.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(COL_HAB_DESCRIPCION)));
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
                try {
                    int colSonido = cursor.getColumnIndex(COL_HAB_SONIDO);
                    if (colSonido != -1) {
                        h.setTipoSonidoNotificacion(cursor.getString(colSonido));
                    }
                    int colDur = cursor.getColumnIndex(COL_HAB_DURACION_SONIDO);
                    if (colDur != -1 && !cursor.isNull(colDur)) {
                        h.setDuracionSonidoSegundos(cursor.getInt(colDur));
                    }
                } catch (Exception ignored) {}
                lista.add(h);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public Habito obtenerHabitoPorId(long habitoId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_HABITO + " WHERE " + COL_HAB_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(habitoId)});
        Habito h = null;
        if (cursor.moveToFirst()) {
            h = new Habito();
            h.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_HAB_ID)));
            h.setUsuarioId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_HAB_USUARIO_ID)));
            h.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(COL_HAB_NOMBRE)));
            h.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(COL_HAB_DESCRIPCION)));
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
            try {
                int colSonido = cursor.getColumnIndex(COL_HAB_SONIDO);
                if (colSonido != -1) {
                    h.setTipoSonidoNotificacion(cursor.getString(colSonido));
                }
                int colDur = cursor.getColumnIndex(COL_HAB_DURACION_SONIDO);
                if (colDur != -1 && !cursor.isNull(colDur)) {
                    h.setDuracionSonidoSegundos(cursor.getInt(colDur));
                }
            } catch (Exception ignored) {}
        }
        cursor.close();
        return h;
    }

    public boolean actualizarHabito(Habito habito) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_HAB_NOMBRE, habito.getNombre());
        cv.put(COL_HAB_DESCRIPCION, habito.getDescripcion());
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
        cv.put(COL_HAB_SONIDO, habito.getTipoSonidoNotificacion());
        cv.put(COL_HAB_DURACION_SONIDO, habito.getDuracionSonidoSegundos());
        int rows = db.update(TABLE_HABITO, cv, COL_HAB_ID + " = ?", new String[]{String.valueOf(habito.getId())});
        return rows > 0;
    }

    public void toggleHabitoCompletado(long habitoId, boolean completado) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_HAB_COMPLETADO, completado ? 1 : 0);
        db.update(TABLE_HABITO, cv, COL_HAB_ID + " = ?", new String[]{String.valueOf(habitoId)});
    }

    public void eliminarHabito(long habitoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HABITO, COL_HAB_ID + " = ?", new String[]{String.valueOf(habitoId)});
    }

    public int vaciarTodosLosHabitos(long usuarioId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_HABITO, COL_HAB_USUARIO_ID + " = ?", new String[]{String.valueOf(usuarioId)});
    }

    // Método mantenido para compatibilidad pero no inserta datos hardcodeados
    public void insertarHabitosPorDefecto(long usuarioId) {
        // Cero datos estáticos: la app no inserta datos fijos por defecto.
        // Los hábitos nacen a partir de la creación personalizada del usuario.
    }

    // =========================================================================
    // AJUSTES Y PREFERENCIAS DE BIENESTAR
    // =========================================================================

    public boolean obtenerModoSinCulpa(long usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_PERFIL_MODO_SIN_CULPA + " FROM " + TABLE_AJUSTES_USUARIO + " WHERE " + COL_PERFIL_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(usuarioId)});
        boolean modo = true;
        if (cursor.moveToFirst()) {
            modo = cursor.getInt(0) == 1;
        }
        cursor.close();
        return modo;
    }

    public void guardarModoSinCulpa(long usuarioId, boolean activo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PERFIL_MODO_SIN_CULPA, activo ? 1 : 0);
        int rows = db.update(TABLE_AJUSTES_USUARIO, cv, COL_PERFIL_USER_ID + " = ?", new String[]{String.valueOf(usuarioId)});
        if (rows == 0) {
            cv.put(COL_PERFIL_USER_ID, usuarioId);
            db.insert(TABLE_AJUSTES_USUARIO, null, cv);
        }
    }

    public String obtenerMoneda(long usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_PERFIL_MONEDA + " FROM " + TABLE_AJUSTES_USUARIO + " WHERE " + COL_PERFIL_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(usuarioId)});
        String moneda = "USD";
        if (cursor.moveToFirst()) {
            moneda = cursor.getString(0);
        }
        cursor.close();
        return moneda;
    }

    public void guardarMoneda(long usuarioId, String moneda) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PERFIL_MONEDA, moneda);
        db.update(TABLE_AJUSTES_USUARIO, cv, COL_PERFIL_USER_ID + " = ?", new String[]{String.valueOf(usuarioId)});
    }

    public int obtenerVasosAgua(long usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_PERFIL_VASOS_AGUA + " FROM " + TABLE_AJUSTES_USUARIO + " WHERE " + COL_PERFIL_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(usuarioId)});
        int vasos = 0;
        if (cursor.moveToFirst()) {
            vasos = cursor.getInt(0);
        }
        cursor.close();
        return vasos;
    }

    public void guardarVasosAgua(long usuarioId, int vasos) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PERFIL_VASOS_AGUA, vasos);
        int rows = db.update(TABLE_AJUSTES_USUARIO, cv, COL_PERFIL_USER_ID + " = ?", new String[]{String.valueOf(usuarioId)});
        if (rows == 0) {
            cv.put(COL_PERFIL_USER_ID, usuarioId);
            db.insert(TABLE_AJUSTES_USUARIO, null, cv);
        }
    }

    public void actualizarVasosAgua(long usuarioId, int vasos) {
        guardarVasosAgua(usuarioId, vasos);
    }

    public void actualizarModoSinCulpa(long usuarioId, boolean activo) {
        guardarModoSinCulpa(usuarioId, activo);
    }

    // =========================================================================
    // CONSULTAS RELACIONALES DINÁMICAS (PERFIL Y MÉTRICAS REALES)
    // =========================================================================

    public Usuario obtenerUsuarioPorId(long usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIO + " WHERE " + COL_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(usuarioId)});
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

    public int obtenerTotalHabitos(long usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_HABITO + " WHERE " + COL_HAB_USUARIO_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(usuarioId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int obtenerHabitosCompletadosHoy(long usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_HABITO + " WHERE " + COL_HAB_USUARIO_ID + " = ? AND " + COL_HAB_COMPLETADO + " = 1";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(usuarioId)});
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public int obtenerMejorRacha(long usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT MAX(" + COL_HAB_RACHA + ") FROM " + TABLE_HABITO + " WHERE " + COL_HAB_USUARIO_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(usuarioId)});
        int max = 0;
        if (cursor.moveToFirst()) {
            max = cursor.getInt(0);
        }
        cursor.close();
        return max;
    }

    public String obtenerMetaBienestar(long usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_PERFIL_META_BIENESTAR + " FROM " + TABLE_AJUSTES_USUARIO + " WHERE " + COL_PERFIL_USER_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(usuarioId)});
        String meta = "Equilibrio y calma interior";
        if (cursor.moveToFirst()) {
            String val = cursor.getString(0);
            if (val != null && !val.trim().isEmpty()) {
                meta = val;
            }
        }
        cursor.close();
        return meta;
    }

    public void guardarMetaBienestar(long usuarioId, String meta) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_PERFIL_META_BIENESTAR, meta);
        int rows = db.update(TABLE_AJUSTES_USUARIO, cv, COL_PERFIL_USER_ID + " = ?", new String[]{String.valueOf(usuarioId)});
        if (rows == 0) {
            cv.put(COL_PERFIL_USER_ID, usuarioId);
            db.insert(TABLE_AJUSTES_USUARIO, null, cv);
        }
    }

    // =========================================================================
    // MÉTODOS PARA TONOS PERSONALIZADOS (MP3)
    // =========================================================================

    public long insertarTonoPersonalizado(TonoAudio tono) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_TONO_USER_ID, tono.getUsuarioId());
        cv.put(COL_TONO_NOMBRE, tono.getNombre());
        cv.put(COL_TONO_RUTA, tono.getRutaArchivo());
        cv.put(COL_TONO_DURACION, tono.getDuracion());
        cv.put(COL_TONO_FECHA, tono.getFechaSubida());
        return db.insert(TABLE_TONO_PERSONALIZADO, null, cv);
    }

    public List<TonoAudio> obtenerTonosPersonalizados(long usuarioId) {
        List<TonoAudio> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TONO_PERSONALIZADO, null,
                COL_TONO_USER_ID + " = ?", new String[]{String.valueOf(usuarioId)},
                null, null, COL_TONO_ID + " DESC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                TonoAudio t = new TonoAudio();
                t.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_TONO_ID)));
                t.setUsuarioId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_TONO_USER_ID)));
                t.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(COL_TONO_NOMBRE)));
                t.setRutaArchivo(cursor.getString(cursor.getColumnIndexOrThrow(COL_TONO_RUTA)));
                t.setDuracion(cursor.getString(cursor.getColumnIndexOrThrow(COL_TONO_DURACION)));
                t.setFechaSubida(cursor.getString(cursor.getColumnIndexOrThrow(COL_TONO_FECHA)));
                lista.add(t);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return lista;
    }

    public TonoAudio obtenerTonoPorId(long tonoId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TONO_PERSONALIZADO, null,
                COL_TONO_ID + " = ?", new String[]{String.valueOf(tonoId)},
                null, null, null);
        TonoAudio t = null;
        if (cursor != null && cursor.moveToFirst()) {
            t = new TonoAudio();
            t.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_TONO_ID)));
            t.setUsuarioId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_TONO_USER_ID)));
            t.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(COL_TONO_NOMBRE)));
            t.setRutaArchivo(cursor.getString(cursor.getColumnIndexOrThrow(COL_TONO_RUTA)));
            t.setDuracion(cursor.getString(cursor.getColumnIndexOrThrow(COL_TONO_DURACION)));
            t.setFechaSubida(cursor.getString(cursor.getColumnIndexOrThrow(COL_TONO_FECHA)));
            cursor.close();
        }
        return t;
    }

    public boolean eliminarTonoPersonalizado(long tonoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_TONO_PERSONALIZADO, COL_TONO_ID + " = ?", new String[]{String.valueOf(tonoId)});
        return rows > 0;
    }
}
