package com.rutinas_habitos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * DatabaseHelper - Gestor de persistencia relacional local con SQLite.
 * 
 * Implementa la creación, configuración y actualización del esquema de base de datos
 * para la aplicación 'Rutinas Pastel', administrando las tablas:
 * - Usuario: Credenciales y datos del perfil local.
 * - AjustesUsuario: Preferencias de bienestar (Modo Sin Culpa, metas).
 * - Habito: Entidad principal para la gestión y seguimiento de hábitos (CRUD).
 * - RegistroDiario: Historial detallado de cumplimiento por fecha.
 * - TonoPersonalizado: Archivos de audio MP3 locales para recordatorios.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "rutinas_habitos.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_USUARIO = "Usuario";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_NOMBRE = "nombre";
    public static final String COL_USER_CORREO = "correo";
    public static final String COL_USER_PASSWORD = "password";
    public static final String COL_USER_FECHA = "fecha_registro";

    public static final String TABLE_AJUSTES_USUARIO = "AjustesUsuario";
    public static final String COL_PERFIL_ID = "id";
    public static final String COL_PERFIL_USER_ID = "usuario_id";
    public static final String COL_PERFIL_MODO_SIN_CULPA = "modo_sin_culpa";
    public static final String COL_PERFIL_META_BIENESTAR = "meta_bienestar";
    public static final String COL_PERFIL_ULTIMA_INTERACCION = "ultima_interaccion";
    public static final String COL_PERFIL_VASOS_AGUA = "vasos_agua";
    public static final String COL_PERFIL_META_AGUA = "meta_agua";

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

    public static final String TABLE_REGISTRO_DIARIO = "RegistroDiario";
    public static final String COL_REG_ID = "id";
    public static final String COL_REG_HABITO_ID = "habito_id";
    public static final String COL_REG_USUARIO_ID = "usuario_id";
    public static final String COL_REG_FECHA = "fecha";
    public static final String COL_REG_COMPLETADO = "completado";
    public static final String COL_REG_PROGRESO = "progreso_cantidad";
    public static final String COL_REG_NOTAS = "notas";

    public static final String TABLE_TONO_PERSONALIZADO = "TonoPersonalizado";
    public static final String COL_TONO_ID = "id";
    public static final String COL_TONO_USER_ID = "usuario_id";
    public static final String COL_TONO_NOMBRE = "nombre";
    public static final String COL_TONO_RUTA = "ruta_archivo";
    public static final String COL_TONO_DURACION = "duracion";
    public static final String COL_TONO_FECHA = "fecha_subida";

    public static final String TABLE_USUARIOS = TABLE_USUARIO;
    public static final String TABLE_HABITOS = TABLE_HABITO;
    public static final String TABLE_AJUSTES = TABLE_AJUSTES_USUARIO;
    public static final String COL_AJUSTE_USER_ID = COL_PERFIL_USER_ID;
    public static final String COL_AJUSTE_MODO_SIN_CULPA = COL_PERFIL_MODO_SIN_CULPA;
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
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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
        } catch (Exception ignored) {}
        try {
            db.execSQL("ALTER TABLE " + TABLE_HABITO + " ADD COLUMN " + COL_HAB_DURACION_SONIDO + " INTEGER DEFAULT 30");
        } catch (Exception ignored) {}
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
        } catch (Exception ignored) {}
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_REGISTRO_DIARIO + " (" +
                    COL_REG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_REG_HABITO_ID + " INTEGER NOT NULL, " +
                    COL_REG_USUARIO_ID + " INTEGER NOT NULL, " +
                    COL_REG_FECHA + " TEXT NOT NULL, " +
                    COL_REG_COMPLETADO + " INTEGER NOT NULL DEFAULT 0, " +
                    COL_REG_PROGRESO + " INTEGER DEFAULT 0, " +
                    COL_REG_NOTAS + " TEXT, " +
                    "FOREIGN KEY(" + COL_REG_HABITO_ID + ") REFERENCES " + TABLE_HABITO + "(" + COL_HAB_ID + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY(" + COL_REG_USUARIO_ID + ") REFERENCES " + TABLE_USUARIO + "(" + COL_USER_ID + ") ON DELETE CASCADE" +
                    ");");
        } catch (Exception ignored) {}
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTRO_DIARIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AJUSTES_USUARIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TONO_PERSONALIZADO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);
        onCreate(db);
    }

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

    /**
     * Valida si un correo electrónico ya está registrado en la base de datos.
     */
    public boolean existeCorreo(String correo) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_USER_ID + " FROM " + TABLE_USUARIO + " WHERE LOWER(" + COL_USER_CORREO + ") = ?";
        Cursor cursor = db.rawQuery(query, new String[]{correo.trim().toLowerCase()});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    /**
     * Valida si ya existe un hábito con el mismo nombre para el usuario actual.
     * Requisito de la rúbrica: Prevención de registros duplicados e integridad de datos.
     * @param usuarioId ID del usuario propietario.
     * @param nombre Nombre del hábito a validar.
     * @param excludeHabitoId ID a excluir (útil durante la actualización/UPDATE).
     * @return true si ya existe un hábito idéntico, false si está disponible.
     */
    public boolean existeHabitoConNombre(long usuarioId, String nombre, long excludeHabitoId) {
        if (nombre == null || nombre.trim().isEmpty()) return false;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_HAB_ID + " FROM " + TABLE_HABITO +
                " WHERE " + COL_HAB_USUARIO_ID + " = ? AND LOWER(TRIM(" + COL_HAB_NOMBRE + ")) = ? AND " + COL_HAB_ID + " != ?";
        Cursor cursor = db.rawQuery(query, new String[]{
                String.valueOf(usuarioId),
                nombre.trim().toLowerCase(),
                String.valueOf(excludeHabitoId)
        });
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

        /**
     * OPERACIÓN CRUD: CREATE (INSERTAR)
     * Inserta un nuevo registro de hábito en la tabla 'Habito' mediante ContentValues.
     * Se captura cualquier excepción de SQLite para prevenir caídas de la app.
     * @param habito Objeto con los datos del nuevo hábito.
     * @return ID autogenerado del nuevo registro o -1 en caso de error.
     */
    public long insertarHabito(Habito habito) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL_HAB_USUARIO_ID, habito.getUsuarioId());
            cv.put(COL_HAB_NOMBRE, habito.getNombre().trim());
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
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * OPERACIÓN CRUD: READ (CONSULTAR LISTADO)
     * Recupera todos los hábitos registrados por el usuario mediante un Cursor de SQLite,
     * ordenados cronológicamente de forma descendente.
     * @param usuarioId Identificador del usuario.
     * @return Lista de objetos Habito recuperados desde la base de datos.
     */
    public List<Habito> obtenerHabitosPorUsuario(long usuarioId) {
        List<Habito> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_HABITO + " WHERE " + COL_HAB_USUARIO_ID + " = ? ORDER BY " + COL_HAB_ID + " DESC";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(usuarioId)});
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return lista;
    }

    /**
     * OPERACIÓN CRUD: READ (CONSULTAR INDIVIDUAL)
     * Recupera un hábito específico a partir de su ID para cargar su información en formularios o vistas.
     * @param habitoId Clave primaria del hábito.
     * @return Objeto Habito con los datos recuperados o null si no existe.
     */
    public Habito obtenerHabitoPorId(long habitoId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_HABITO + " WHERE " + COL_HAB_ID + " = ?";
        Cursor cursor = null;
        Habito h = null;
        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(habitoId)});
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }
        return h;
    }

    /**
     * OPERACIÓN CRUD: UPDATE (ACTUALIZAR)
     * Modifica los atributos de un hábito existente identificado por su clave primaria (ID),
     * conservando su identificador y su historial.
     * @param habito Objeto con los datos modificados.
     * @return true si la actualización afectó al menos un registro, false si hubo error.
     */
    public boolean actualizarHabito(Habito habito) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL_HAB_NOMBRE, habito.getNombre().trim());
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
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Alterna el estado de completado de un hábito para la fecha de hoy.
     */
    public void toggleHabitoCompletado(long habitoId, long usuarioId, boolean completado) {
        String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        toggleHabitoEnFecha(habitoId, usuarioId, hoy, completado);
    }

    /**
     * Actualiza el estado de cumplimiento y racha de un hábito para una fecha determinada,
     * persistiendo el registro en la tabla 'RegistroDiario'.
     */
    public void toggleHabitoEnFecha(long habitoId, long usuarioId, String fecha, boolean completado) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Habito h = obtenerHabitoPorId(habitoId);
            int nuevaRacha = 0;
            int nuevoProgreso = 0;

            if (h != null) {
                nuevaRacha = h.getRachaDias();
                int meta = h.getMetaCantidad() > 0 ? h.getMetaCantidad() : 1;
                if (completado) {
                    nuevaRacha = Math.max(1, nuevaRacha + 1);
                    nuevoProgreso = meta;
                } else {
                    nuevaRacha = Math.max(0, nuevaRacha - 1);
                    nuevoProgreso = 0;
                }
            }

            String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            if (fecha != null && fecha.equals(hoy)) {
                ContentValues cv = new ContentValues();
                cv.put(COL_HAB_COMPLETADO, completado ? 1 : 0);
                cv.put(COL_HAB_PROGRESO, nuevoProgreso);
                cv.put(COL_HAB_RACHA, nuevaRacha);
                db.update(TABLE_HABITO, cv, COL_HAB_ID + " = ?", new String[]{String.valueOf(habitoId)});
            }

            registrarProgresoDiario(habitoId, usuarioId, fecha, completado, nuevoProgreso, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toggleHabitoCompletado(long habitoId, boolean completado) {
        Habito h = obtenerHabitoPorId(habitoId);
        long userId = h != null ? h.getUsuarioId() : 1;
        toggleHabitoCompletado(habitoId, userId, completado);
    }

    /**
     * Ajusta el progreso cuantitativo de un hábito (+1 o -1), respetando el límite estricto de la meta.
     * Si el progreso alcanza la meta, se marca como completado y se bloquea de superar el límite.
     */
    public int ajustarProgresoHabito(long habitoId, long usuarioId, int delta) {
        try {
            Habito h = obtenerHabitoPorId(habitoId);
            if (h == null) return 0;

            int meta = h.getMetaCantidad() > 0 ? h.getMetaCantidad() : 1;
            int nuevoProgreso = Math.min(meta, Math.max(0, h.getProgresoActual() + delta));
            boolean completado = nuevoProgreso >= meta;

            int nuevaRacha = h.getRachaDias();
            if (completado && !h.isCompletadoHoy()) {
                nuevaRacha = Math.max(1, nuevaRacha + 1);
            } else if (!completado && h.isCompletadoHoy()) {
                nuevaRacha = Math.max(0, nuevaRacha - 1);
            }

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL_HAB_PROGRESO, nuevoProgreso);
            cv.put(COL_HAB_COMPLETADO, completado ? 1 : 0);
            cv.put(COL_HAB_RACHA, nuevaRacha);
            db.update(TABLE_HABITO, cv, COL_HAB_ID + " = ?", new String[]{String.valueOf(habitoId)});

            String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            registrarProgresoDiario(habitoId, usuarioId, hoy, completado, nuevoProgreso, "");

            return nuevoProgreso;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void registrarProgresoDiario(long habitoId, long usuarioId, String fecha, boolean completado, int progreso, String notas) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL_REG_HABITO_ID, habitoId);
            cv.put(COL_REG_USUARIO_ID, usuarioId);
            cv.put(COL_REG_FECHA, fecha);
            cv.put(COL_REG_COMPLETADO, completado ? 1 : 0);
            cv.put(COL_REG_PROGRESO, progreso);
            cv.put(COL_REG_NOTAS, notas != null ? notas : "");

            int rows = db.update(TABLE_REGISTRO_DIARIO, cv,
                    COL_REG_HABITO_ID + " = ? AND " + COL_REG_FECHA + " = ?",
                    new String[]{String.valueOf(habitoId), fecha});
            if (rows == 0) {
                db.insert(TABLE_REGISTRO_DIARIO, null, cv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void verificarYResetearDia(long usuarioId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            String ultimaFecha = "";
            Cursor c = db.rawQuery("SELECT " + COL_PERFIL_ULTIMA_INTERACCION + " FROM " + TABLE_AJUSTES_USUARIO +
                    " WHERE " + COL_PERFIL_USER_ID + " = ?", new String[]{String.valueOf(usuarioId)});
            if (c.moveToFirst()) {
                ultimaFecha = c.getString(0);
            }
            c.close();

            if (ultimaFecha != null && !ultimaFecha.isEmpty() && ultimaFecha.length() >= 10) {
                String subFecha = ultimaFecha.substring(0, 10);
                if (!subFecha.equals(hoy)) {
                    boolean modoSinCulpa = obtenerModoSinCulpa(usuarioId);
                    List<Habito> habitos = obtenerHabitosPorUsuario(usuarioId);
                    for (Habito h : habitos) {
                        ContentValues cv = new ContentValues();
                        if (!h.isCompletadoHoy() && !modoSinCulpa) {
                            cv.put(COL_HAB_RACHA, 0);
                        }
                        cv.put(COL_HAB_COMPLETADO, 0);
                        cv.put(COL_HAB_PROGRESO, 0);
                        db.update(TABLE_HABITO, cv, COL_HAB_ID + " = ?", new String[]{String.valueOf(h.getId())});
                    }
                }
            }

            ContentValues cvPerfil = new ContentValues();
            cvPerfil.put(COL_PERFIL_ULTIMA_INTERACCION, hoy);
            int rows = db.update(TABLE_AJUSTES_USUARIO, cvPerfil, COL_PERFIL_USER_ID + " = ?", new String[]{String.valueOf(usuarioId)});
            if (rows == 0) {
                cvPerfil.put(COL_PERFIL_USER_ID, usuarioId);
                db.insert(TABLE_AJUSTES_USUARIO, null, cvPerfil);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Habito> obtenerHabitosConEstadoFecha(long usuarioId, String fecha) {
        List<Habito> lista = obtenerHabitosPorUsuario(usuarioId);
        String hoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        if (fecha.equals(hoy)) {
            return lista;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        for (Habito h : lista) {
            Cursor c = null;
            try {
                c = db.rawQuery("SELECT " + COL_REG_COMPLETADO + ", " + COL_REG_PROGRESO +
                        " FROM " + TABLE_REGISTRO_DIARIO +
                        " WHERE " + COL_REG_HABITO_ID + " = ? AND " + COL_REG_FECHA + " = ?",
                        new String[]{String.valueOf(h.getId()), fecha});
                if (c.moveToFirst()) {
                    h.setCompletadoHoy(c.getInt(0) == 1);
                    h.setProgresoActual(c.getInt(1));
                } else {
                    h.setCompletadoHoy(false);
                    h.setProgresoActual(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null) c.close();
            }
        }
        return lista;
    }

    public Set<Integer> obtenerDiasConRegistroMes(long usuarioId, int anio, int mes) {
        Set<Integer> dias = new HashSet<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String mesStr = String.format(Locale.ROOT, "%04d-%02d", anio, mes);
        Cursor c = null;
        try {
            c = db.rawQuery("SELECT DISTINCT " + COL_REG_FECHA + " FROM " + TABLE_REGISTRO_DIARIO +
                    " WHERE " + COL_REG_USUARIO_ID + " = ? AND " + COL_REG_FECHA + " LIKE ? AND " + COL_REG_COMPLETADO + " = 1",
                    new String[]{String.valueOf(usuarioId), mesStr + "%"});
            if (c.moveToFirst()) {
                do {
                    String f = c.getString(0);
                    if (f != null && f.length() >= 10) {
                        try {
                            int d = Integer.parseInt(f.substring(8, 10));
                            dias.add(d);
                        } catch (Exception ignored) {}
                    }
                } while (c.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null) c.close();
        }
        return dias;
    }

    public int[] obtenerCompletadosSemanaActual(long usuarioId) {
        int[] resultado = new int[7];
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        SQLiteDatabase db = this.getReadableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String hoy = sdf.format(new Date());

        int totalHabitos = obtenerTotalHabitos(usuarioId);
        if (totalHabitos == 0) return resultado;

        for (int i = 0; i < 7; i++) {
            String fecha = sdf.format(cal.getTime());
            int completados = 0;
            if (fecha.equals(hoy)) {
                completados = obtenerHabitosCompletadosHoy(usuarioId);
            } else {
                Cursor c = null;
                try {
                    c = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_REGISTRO_DIARIO +
                            " WHERE " + COL_REG_USUARIO_ID + " = ? AND " + COL_REG_FECHA + " = ? AND " + COL_REG_COMPLETADO + " = 1",
                            new String[]{String.valueOf(usuarioId), fecha});
                    if (c.moveToFirst()) {
                        completados = c.getInt(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (c != null) c.close();
                }
            }
            int pct = (completados * 100) / totalHabitos;
            resultado[i] = Math.min(100, pct);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return resultado;
    }

    /**
     * OPERACIÓN CRUD: DELETE (ELIMINAR)
     * Elimina el registro del hábito de la base de datos y sus registros asociados en SQLite.
     * Retorna true si la eliminación fue exitosa.
     * @param habitoId ID del hábito a eliminar.
     * @return true si se eliminó el registro de la tabla Habito, false en caso de fallo.
     */
    public boolean eliminarHabito(long habitoId) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_REGISTRO_DIARIO, COL_REG_HABITO_ID + " = ?", new String[]{String.valueOf(habitoId)});
            int rows = db.delete(TABLE_HABITO, COL_HAB_ID + " = ?", new String[]{String.valueOf(habitoId)});
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int vaciarTodosLosHabitos(long usuarioId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REGISTRO_DIARIO, COL_REG_USUARIO_ID + " = ?", new String[]{String.valueOf(usuarioId)});
        return db.delete(TABLE_HABITO, COL_HAB_USUARIO_ID + " = ?", new String[]{String.valueOf(usuarioId)});
    }

    public void insertarHabitosPorDefecto(long usuarioId) {
    }

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

    public void actualizarModoSinCulpa(long usuarioId, boolean activo) {
        guardarModoSinCulpa(usuarioId, activo);
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
