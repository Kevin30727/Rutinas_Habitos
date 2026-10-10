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
 * 1. Tablas oficiales: Usuario, PerfilAsistente, Habito, RegistroDiario.
 * 2. Cero datos estáticos / hardcodeados: las tablas nacen completamente vacías.
 * 3. Inserción dinámica por transacción basada exclusivamente en los datos captados por el Asistente.
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

    // 2. Tabla PerfilAsistente
    public static final String TABLE_PERFIL_ASISTENTE = "PerfilAsistente";
    public static final String COL_PERFIL_ID = "id";
    public static final String COL_PERFIL_USER_ID = "usuario_id";
    public static final String COL_PERFIL_TONO = "tono_asistente";
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

    // 4. Tabla RegistroDiario
    public static final String TABLE_REGISTRO_DIARIO = "RegistroDiario";
    public static final String COL_REG_ID = "id";
    public static final String COL_REG_HABITO_ID = "habito_id";
    public static final String COL_REG_USUARIO_ID = "usuario_id";
    public static final String COL_REG_FECHA = "fecha";
    public static final String COL_REG_COMPLETADO = "completado";
    public static final String COL_REG_PROGRESO = "progreso_cantidad";
    public static final String COL_REG_NOTAS = "notas";

    // Constantes de compatibilidad hacia atrás
    public static final String TABLE_USUARIOS = TABLE_USUARIO;
    public static final String TABLE_HABITOS = TABLE_HABITO;
    public static final String TABLE_AJUSTES = TABLE_PERFIL_ASISTENTE;
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

        String createPerfilAsistente = "CREATE TABLE " + TABLE_PERFIL_ASISTENTE + " (" +
                COL_PERFIL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PERFIL_USER_ID + " INTEGER NOT NULL, " +
                COL_PERFIL_TONO + " TEXT DEFAULT 'Zen Amable', " +
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

        db.execSQL(createUsuario);
        db.execSQL(createPerfilAsistente);
        db.execSQL(createHabito);
        db.execSQL(createRegistroDiario);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGISTRO_DIARIO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HABITO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERFIL_ASISTENTE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIO);
        onCreate(db);
    }

    // =========================================================================
    // REQUERIMIENTO 3: INSERCIÓN DINÁMICA DE USUARIO Y HÁBITOS DESDE EL ASISTENTE
    // =========================================================================

    /**
     * Inserta dinámicamente el primer usuario y todos los hábitos detectados por el Asistente
     * utilizando una única transacción atómica con parámetros seguros.
     * Cero datos hardcodeados: todo proviene de las variables captadas en la interacción.
     */
    public long registrarUsuarioYHabitosDesdeAsistente(
            String nombreUsuario,
            String correo,
            String password,
            String metaBienestar,
            List<AsistenteMotor.RutinaPropuesta> rutinasDetectadas
    ) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        long usuarioId = -1;

        try {
            SimpleDateFormat sdfCompleto = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat sdfDia = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String ahora = sdfCompleto.format(new Date());
            String hoy = sdfDia.format(new Date());

            // 1. Insertar Usuario
            ContentValues cvUser = new ContentValues();
            cvUser.put(COL_USER_NOMBRE, nombreUsuario.trim());
            cvUser.put(COL_USER_CORREO, correo.trim().toLowerCase());
            cvUser.put(COL_USER_PASSWORD, password);
            cvUser.put(COL_USER_FECHA, ahora);
            usuarioId = db.insertOrThrow(TABLE_USUARIO, null, cvUser);

            // 2. Insertar PerfilAsistente vinculado
            ContentValues cvPerfil = new ContentValues();
            cvPerfil.put(COL_PERFIL_USER_ID, usuarioId);
            cvPerfil.put(COL_PERFIL_TONO, "Zen Amable");
            cvPerfil.put(COL_PERFIL_MODO_SIN_CULPA, 1);
            cvPerfil.put(COL_PERFIL_META_BIENESTAR, metaBienestar != null ? metaBienestar : "Bienestar integral");
            cvPerfil.put(COL_PERFIL_ULTIMA_INTERACCION, ahora);
            cvPerfil.put(COL_PERFIL_VASOS_AGUA, 0);
            cvPerfil.put(COL_PERFIL_META_AGUA, 8);
            cvPerfil.put(COL_PERFIL_MONEDA, "USD");
            db.insertOrThrow(TABLE_PERFIL_ASISTENTE, null, cvPerfil);

            // 3. Insertar dinámicamente cada hábito detectado por el Asistente
            if (rutinasDetectadas != null && !rutinasDetectadas.isEmpty()) {
                for (AsistenteMotor.RutinaPropuesta r : rutinasDetectadas) {
                    ContentValues cvHab = new ContentValues();
                    cvHab.put(COL_HAB_USUARIO_ID, usuarioId);
                    cvHab.put(COL_HAB_NOMBRE, r.getNombre());
                    cvHab.put(COL_HAB_DESCRIPCION, r.getDescripcion());
                    cvHab.put(COL_HAB_ICONO, r.getIcono());
                    cvHab.put(COL_HAB_COLOR, r.getColorHex());
                    cvHab.put(COL_HAB_AREA, r.getAreaVida());
                    cvHab.put(COL_HAB_FRECUENCIA, r.getFrecuencia());
                    cvHab.put(COL_HAB_DIAS, "L,M,M,J,V,S,D");
                    cvHab.put(COL_HAB_TIPO_META, "cantidad");
                    cvHab.put(COL_HAB_META_CANTIDAD, r.getMetaCantidad());
                    cvHab.put(COL_HAB_UNIDAD, r.getUnidad());
                    cvHab.put(COL_HAB_HORA, r.getHoraSugerida());
                    cvHab.put(COL_HAB_RECORDATORIO, 1);
                    cvHab.put(COL_HAB_RACHA, 0);
                    cvHab.put(COL_HAB_COMPLETADO, 0);
                    cvHab.put(COL_HAB_PROGRESO, 0);
                    cvHab.put(COL_HAB_FECHA_CREACION, hoy);

                    long habitoId = db.insertOrThrow(TABLE_HABITO, null, cvHab);

                    // 4. Inicializar primer RegistroDiario para seguimiento de hoy
                    ContentValues cvReg = new ContentValues();
                    cvReg.put(COL_REG_HABITO_ID, habitoId);
                    cvReg.put(COL_REG_USUARIO_ID, usuarioId);
                    cvReg.put(COL_REG_FECHA, hoy);
                    cvReg.put(COL_REG_COMPLETADO, 0);
                    cvReg.put(COL_REG_PROGRESO, 0);
                    cvReg.put(COL_REG_NOTAS, "Iniciado con Asistente Panda");
                    db.insert(TABLE_REGISTRO_DIARIO, null, cvReg);
                }
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return usuarioId;
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
            cvPerfil.put(COL_PERFIL_TONO, "Zen Amable");
            cvPerfil.put(COL_PERFIL_MODO_SIN_CULPA, 1);
            cvPerfil.put(COL_PERFIL_META_BIENESTAR, "Equilibrio y calma");
            cvPerfil.put(COL_PERFIL_ULTIMA_INTERACCION, sdf.format(new Date()));
            cvPerfil.put(COL_PERFIL_VASOS_AGUA, 0);
            cvPerfil.put(COL_PERFIL_META_AGUA, 8);
            cvPerfil.put(COL_PERFIL_MONEDA, "USD");
            db.insert(TABLE_PERFIL_ASISTENTE, null, cvPerfil);
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
        db.update(TABLE_HABITO, cv, COL_HAB_ID + " = ?", new String[]{String.valueOf(habitoId)});
    }

    public void eliminarHabito(long habitoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HABITO, COL_HAB_ID + " = ?", new String[]{String.valueOf(habitoId)});
    }

    // Método mantenido para compatibilidad pero no inserta datos hardcodeados
    public void insertarHabitosPorDefecto(long usuarioId) {
        // Cero datos estáticos: la app no inserta datos fijos por defecto.
        // Los hábitos nacen a partir de la interacción con el Asistente o creación manual del usuario.
    }

    // =========================================================================
    // AJUSTES Y PERFIL ASISTENTE
    // =========================================================================

    public boolean obtenerModoSinCulpa(long usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_PERFIL_MODO_SIN_CULPA + " FROM " + TABLE_PERFIL_ASISTENTE + " WHERE " + COL_PERFIL_USER_ID + " = ?";
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
        int rows = db.update(TABLE_PERFIL_ASISTENTE, cv, COL_PERFIL_USER_ID + " = ?", new String[]{String.valueOf(usuarioId)});
        if (rows == 0) {
            cv.put(COL_PERFIL_USER_ID, usuarioId);
            db.insert(TABLE_PERFIL_ASISTENTE, null, cv);
        }
    }

    public String obtenerMoneda(long usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_PERFIL_MONEDA + " FROM " + TABLE_PERFIL_ASISTENTE + " WHERE " + COL_PERFIL_USER_ID + " = ?";
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
        db.update(TABLE_PERFIL_ASISTENTE, cv, COL_PERFIL_USER_ID + " = ?", new String[]{String.valueOf(usuarioId)});
    }

    public int obtenerVasosAgua(long usuarioId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COL_PERFIL_VASOS_AGUA + " FROM " + TABLE_PERFIL_ASISTENTE + " WHERE " + COL_PERFIL_USER_ID + " = ?";
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
        int rows = db.update(TABLE_PERFIL_ASISTENTE, cv, COL_PERFIL_USER_ID + " = ?", new String[]{String.valueOf(usuarioId)});
        if (rows == 0) {
            cv.put(COL_PERFIL_USER_ID, usuarioId);
            db.insert(TABLE_PERFIL_ASISTENTE, null, cv);
        }
    }

    public void actualizarVasosAgua(long usuarioId, int vasos) {
        guardarVasosAgua(usuarioId, vasos);
    }

    public void actualizarModoSinCulpa(long usuarioId, boolean activo) {
        guardarModoSinCulpa(usuarioId, activo);
    }
}
