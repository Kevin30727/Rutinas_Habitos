# 🌸 Rutinas Pastel — App de Salud Mental, Hábitos & Bienestar Emocional

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Java](https://img.shields.io/badge/Language-Java%2017-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![SQLite](https://img.shields.io/badge/Database-SQLite%20Local-003B57?style=for-the-badge&logo=sqlite&logoColor=white)](https://www.sqlite.org/)
[![UI/UX](https://img.shields.io/badge/Design-Pastel%20Theme%20%26%20Wellbeing-F8C8DC?style=for-the-badge)](https://material.io/)

**Rutinas Pastel** es una aplicación móvil nativa para Android diseñada bajo un enfoque de **salud mental, compasión y bienestar holístico**. Su filosofía se aleja de la presión y la culpa de los gestores de hábitos tradicionales, ofreciendo una experiencia visual relajante en tonos pastel, persistencia de datos 100% relacional en **SQLite** y un sistema avanzado de **notificaciones personalizadas con música MP3 y duración configurable**.

---

## 📑 Tabla de Contenidos

- [🌸 Rutinas Pastel — App de Salud Mental, Hábitos & Bienestar Emocional](#-rutinas-pastel--app-de-salud-mental-hábitos--bienestar-emocional)
  - [📑 Tabla de Contenidos](#-tabla-de-contenidos)
  - [🧘 Filosofía de Bienestar: "Modo Sin Culpa"](#-filosofía-de-bienestar-modo-sin-culpa)
  - [🔔 Sistema de Notificaciones \& Gestor de Tonos MP3](#-sistema-de-notificaciones--gestor-de-tonos-mp3)
  - [🎨 Paletas de Colores Pastel Dinámicas](#-paletas-de-colores-pastel-dinámicas)
  - [📱 Módulos y Pantallas de la Aplicación](#-módulos-y-pantallas-de-la-aplicación)
  - [🗄️ Arquitectura de Base de Datos (SQLite)](#️-arquitectura-de-base-de-datos-sqlite)
  - [🛠️ Stack Tecnológico](#️-stack-tecnológico)
  - [🚀 Instalación y Compilación](#-instalación-y-compilación)
  - [📁 Estructura del Proyecto](#-estructura-del-proyecto)

---

## 🧘 Filosofía de Bienestar: "Modo Sin Culpa"

La mayoría de aplicaciones penalizan al usuario cuando tiene un mal día o no puede cumplir con sus hábitos, lo que genera ansiedad y abandono. En **Rutinas Pastel**:
- **Tolerancia y Pausa con Amor:** Si no puedes completar un hábito, tus rachas no se rompen de forma punitiva. El sistema te anima a retomar con calma al día siguiente.
- **Micro-hábitos Sostenibles:** La app promueve metas que se sienten ridículamente fáciles de iniciar (ej. 1 vaso de agua, leer 5 minutos, respirar 2 minutos).
- **Ambiente Visual Armónico:** Ausencia de rojos agresivos o alarmas estridentes; cada componente utiliza curvas suaves, sombras sutiles y colores pastel que descansan la vista.

---

## 🔔 Sistema de Notificaciones & Gestor de Tonos MP3

Rutinas Pastel incluye un sistema sonoro adaptado a la calma mental:

1. **Canal Estándar:** Sonido predeterminado del sistema Android.
2. **Canal Relajante Armónico (528Hz Solfeggio):** Sonido suave de campana tibetana y respiración consciente (`melodia_relajante.wav`) para evitar sobresaltos.
3. **Compartimento de Audio MP3 Personalizado:**
   - En la creación de hábitos (`CrearHabitoActivity`), el usuario puede escoger la opción **📁 Mi MP3**.
   - Permite seleccionar un tono previamente guardado o abrir el explorador de archivos para subir cualquier archivo `.mp3`, `.wav` o de audio.
   - Botón `▶ Escuchar muestra` para previsualizar el sonido antes de guardar el hábito.
4. **Duración de Música Configurable & Botón de Cancelación:**
   - El usuario puede elegir el tiempo exacto que sonará la melodía de notificación: **10 seg, 30 seg, 1 min, 3 min o 5 min**.
   - La notificación persistente incluye un botón de acción rápida: **⏹ Cancelar Notificación**, permitiendo detener el audio al instante desde la barra de notificaciones o la pantalla de bloqueo sin tener que abrir la app.
5. **Pantalla Dedicada de Tonos (`TonosActivity`):**
   - Accesible desde el perfil del usuario.
   - Permite subir audios desde el almacenamiento del dispositivo, copiándolos de forma segura al directorio privado interno de la aplicación (`tonos_mp3/`).
   - Muestra la lista de tonos subidos con controles interactivos para **▶ Escuchar / ⏹ Parar** y **🗑 Eliminar** de SQLite y disco.
6. **Selector Dedicado de Hora de Notificación:**
   - En la creación de hábitos (`CrearHabitoActivity`), un compartimento interactivo permite programar con precisión el momento del día en que se disparará la alerta.
   - Tarjeta destacada con reloj y botón `Elegir hora ✏️` que despliega el selector `TimePickerDialog` nativo.
   - Opciones rápidas frecuentes de un solo toque: 🌅 Mañana (08:00 AM), ☀️ Mediodía (12:30 PM), ⛅ Tarde (05:00 PM) y 🌙 Noche (09:00 PM).
   - Interruptor para activar/desactivar recordatorios por hábito individual con retroalimentación visual inmediata.

---

## 🎨 Paletas de Colores Pastel Dinámicas

Mediante el gestor `ThemeHelper`, los usuarios pueden cambiar la paleta visual desde la pantalla de Configuración, aplicándose de inmediato en los Floating Action Buttons (FAB), encabezados y tarjetas:

| Paleta | Nombre | Color Primario | Color Suave | Acento |
| :--- | :--- | :---: | :---: | :---: |
| 🌿 **Menta** | Pastel Neo | `#B8E0D2` | `#EAF7F2` | `#4CA977` |
| 🌸 **Sakura** | Dulce Rosa | `#F8C8DC` | `#FDEDF3` | `#E8A9C2` |
| 🌊 **Océano** | Cielo Calmo | `#B8D8E8` | `#EBF4F9` | `#6DAFD0` |
| 💜 **Lavanda** | Lavanda Suave | `#D7D9F8` | `#F1F2FD` | `#8A8FD4` |

---

## 📱 Módulos y Pantallas de la Aplicación

```
Rutinas Pastel
 ├── 🎬 SplashActivity (Animación GIF en pantalla completa por 3s)
 ├── 🔐 LoginActivity & RegistroActivity (Autenticación local SQLite)
 ├── 🏠 InicioActivity (Resumen diario, progreso de hábitos y calendario semanal)
 ├── ➕ CrearHabitoActivity (CRUD completo: meta simple/cantidad, días, audio MP3 o estándar)
 ├── 📊 HabitosActivity (Progreso circular, métricas por áreas: Salud, Mente, Productividad)
 ├── 📅 CalendarioActivity (Cuadrícula mensual interactiva y desglose por días)
 ├── 👤 PerfilActivity (Estadísticas consolidadas, tarjeta de inspiración diaria, tonos y configuración)
 ├── ⚙️ ConfiguracionActivity (Paletas dinámicas, modo sin culpa, exportación JSON, reset SQLite)
 └── 🎵 TonosActivity (Gestor de biblioteca de archivos MP3 para recordatorios)
```

> **📌 Estructura de Ramas del Repositorio:**
> - `main` (esta rama): Versión enfocada y limpia de hábitos y bienestar diario, navegación de 4 pestañas (Inicio, Hábitos, Calendario, Perfil) y áreas de vida (Salud, Mente, Productividad).
> - `version-con-ia`: Rama dedicada que resguarda la versión con el acompañante conversacional.

---

## 🗄️ Arquitectura de Base de Datos (SQLite)

La persistencia de datos es completamente relacional, offline y sin variables estáticas. Se administra a través de `DatabaseHelper.java`:

```mermaid
erDiagram
    USUARIO ||--o{ HABITO : "crea"
    USUARIO ||--|| AJUSTES_BIENESTAR : "posee"
    USUARIO ||--o{ TONO_PERSONALIZADO : "sube"
    HABITO ||--o{ REGISTRO_DIARIO : "registra"

    USUARIO {
        INTEGER id PK
        TEXT nombre
        TEXT correo
        TEXT contrasena
        TEXT fecha_registro
    }

    AJUSTES_BIENESTAR {
        INTEGER id PK
        INTEGER usuario_id FK
        INTEGER modo_sin_culpa
        TEXT meta_bienestar
        INTEGER vasos_agua
        INTEGER meta_agua
        TEXT moneda
    }

    HABITO {
        INTEGER id PK
        INTEGER usuario_id FK
        TEXT nombre
        TEXT icono
        TEXT color
        TEXT area_vida
        TEXT tipo_frecuencia
        TEXT dias_semana
        TEXT tipo_meta
        INTEGER meta_cantidad
        TEXT unidad
        TEXT hora_recordatorio
        INTEGER recordatorio_activo
        TEXT tipo_sonido_notificacion
        INTEGER racha_dias
        INTEGER completado_hoy
        INTEGER progreso_actual
        TEXT fecha_creacion
    }

    REGISTRO_DIARIO {
        INTEGER id PK
        INTEGER habito_id FK
        INTEGER usuario_id FK
        TEXT fecha
        INTEGER completado
        INTEGER valor_registrado
        TEXT notas
    }

    TONO_PERSONALIZADO {
        INTEGER id PK
        INTEGER usuario_id FK
        TEXT nombre
        TEXT ruta_archivo
        INTEGER duracion
        TEXT fecha_subida
    }
```

---

## 🛠️ Stack Tecnológico

- **Lenguaje Principal:** Java 17
- **Plataforma:** Android SDK (minSdkVersion 26, targetSdkVersion 34)
- **UI Framework:** AndroidX AppCompat, Material Components 3, ConstraintLayout
- **Animaciones & Multimedia:**
  - [Glide 4.16.0](https://github.com/bumptech/glide) (decodificación optimizada `RGB_565` para GIFs fluidos)
  - Android `MediaPlayer` & `RingtoneManager` para reproducción de audio
- **Base de Datos Local:** SQLiteOpenHelper (transacciones ACID locales)
- **Preferencias:** Android `SharedPreferences` con `SessionManager`
- **Build System:** Gradle 8.x con JavaCompile & R8 optimizado

---

## 🚀 Instalación y Compilación

### Requisitos Previos
1. **Android Studio** (Hedgehog, Iguana, Koala o superior).
2. **JDK 17** instalado y configurado en `JAVA_HOME`.
3. Dispositivo físico o emulador con **Android 8.0 (API 26)** o superior.

### Pasos para Ejecutar
1. Clona el repositorio:
   ```bash
   git clone https://github.com/Kevin30727/Rutinas_Habitos.git
   cd Rutinas_Habitos
   ```
2. Compila el código Java y recursos:
   ```bash
   ./gradlew compileDebugJavaWithJavac
   ```
3. Genera el APK en modo depuración:
   ```bash
   ./gradlew assembleDebug
   ```
   *El APK compilado se encontrará en: `app/build/outputs/apk/debug/app-debug.apk`.*
4. Para instalarlo directamente en tu dispositivo conectado vía ADB:
   ```bash
   ./gradlew installDebug
   ```

---

## 📁 Estructura del Proyecto

```
Rutinas_Habitos/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml
│   │       ├── java/com/rutinas_habitos/
│   │       │   ├── CalendarioActivity.java      # Vista mensual y diaria de cumplimiento
│   │       │   ├── ConfiguracionActivity.java   # Ajustes, paletas de colores, JSON y reset
│   │       │   ├── CrearHabitoActivity.java     # Formulario CRUD de hábitos y selector MP3
│   │       │   ├── DatabaseHelper.java          # Helper SQLite con 5 tablas relacionales
│   │       │   ├── Habito.java                  # Modelo de datos de hábito
│   │       │   ├── HabitosActivity.java         # Progreso general, métricas y áreas
│   │       │   ├── InicioActivity.java          # Dashboard diario, hábitos y calendario semanal
│   │       │   ├── LoginActivity.java           # Inicio de sesión
│   │       │   ├── NotificationHelper.java      # Canales y despachador de notificaciones
│   │       │   ├── PerfilActivity.java          # Resumen del perfil e inspiración diaria
│   │       │   ├── RegistroActivity.java        # Registro de nuevas cuentas
│   │       │   ├── SessionManager.java          # Gestor de sesiones activas
│   │       │   ├── SplashActivity.java          # Pantalla de bienvenida con GIF de 3s
│   │       │   ├── ThemeHelper.java             # Gestor dinámico de paletas pasteles
│   │       │   ├── TonoAudio.java               # Modelo de tonos MP3 personalizados
│   │       │   └── TonosActivity.java           # Gestor interactivo de archivos de audio
│   │       └── res/
│   │           ├── drawable/                    # Shape Drawables vectoriales pastel
│   │           ├── layout/                      # Layouts XML responsivos
│   │           ├── raw/
│   │           │   └── melodia_relajante.wav    # Tono armónico tibetano (528Hz)
│   │           └── values/                      # Colores pastel, temas y estilos
├── build.gradle
├── settings.gradle
└── README.md
```

---

*Desarrollado con amor y serenidad para cultivar hábitos conscientes sin prisa ni culpa 🌱✨.*