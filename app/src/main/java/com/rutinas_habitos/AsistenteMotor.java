package com.rutinas_habitos;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Motor de Inteligencia Local Offline para "Rutinas Pastel".
 * 
 * Arquitectura de Detección:
 * 1. Procesamiento Múltiple Acumulativo: No interrumpe con if-else mutuamente excluyentes;
 *    escanea el texto completo del usuario y recolecta todas las intenciones presentes.
 * 2. Evaluación de Sinergia Contextual: Analiza el conjunto combinado de rutinas detectadas
 *    para generar consejos cruzados prácticos (ej: agua + lectura, estrés + sueño).
 */
public class AsistenteMotor {

    // Modelo de datos para una rutina sugerida por el motor
    public static class RutinaPropuesta {
        private String intentId;
        private String nombre;
        private String descripcion;
        private String icono;
        private String colorHex;
        private String areaVida;
        private int metaCantidad;
        private String unidad;
        private String frecuencia;
        private String horaSugerida;

        public RutinaPropuesta(String intentId, String nombre, String descripcion, String icono,
                               String colorHex, String areaVida, int metaCantidad, String unidad,
                               String frecuencia, String horaSugerida) {
            this.intentId = intentId;
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.icono = icono;
            this.colorHex = colorHex;
            this.areaVida = areaVida;
            this.metaCantidad = metaCantidad;
            this.unidad = unidad;
            this.frecuencia = frecuencia;
            this.horaSugerida = horaSugerida;
        }

        public String getIntentId() { return intentId; }
        public String getNombre() { return nombre; }
        public String getDescripcion() { return descripcion; }
        public String getIcono() { return icono; }
        public String getColorHex() { return colorHex; }
        public String getAreaVida() { return areaVida; }
        public int getMetaCantidad() { return metaCantidad; }
        public String getUnidad() { return unidad; }
        public String getFrecuencia() { return frecuencia; }
        public String getHoraSugerida() { return horaSugerida; }

        public Habito toHabito(long usuarioId) {
            Habito h = new Habito();
            h.setUsuarioId(usuarioId);
            h.setNombre(nombre);
            h.setIcono(icono);
            h.setColor(colorHex);
            h.setAreaVida(areaVida);
            h.setTipoFrecuencia(frecuencia);
            h.setDiasSemana("L,M,M,J,V,S,D");
            h.setTipoMeta("cantidad");
            h.setMetaCantidad(metaCantidad);
            h.setUnidad(unidad);
            h.setHoraRecordatorio(horaSugerida);
            h.setRecordatorioActivo(true);
            return h;
        }
    }

    // Resultado consolidado para la UI
    public static class ResultadoAsistente {
        private final List<RutinaPropuesta> rutinasDetectadas;
        private final String mensajePrincipal;
        private final String sugerenciaContextual;

        public ResultadoAsistente(List<RutinaPropuesta> rutinasDetectadas, String mensajePrincipal, String sugerenciaContextual) {
            this.rutinasDetectadas = rutinasDetectadas;
            this.mensajePrincipal = mensajePrincipal;
            this.sugerenciaContextual = sugerenciaContextual;
        }

        public List<RutinaPropuesta> getRutinasDetectadas() { return rutinasDetectadas; }
        public String getMensajePrincipal() { return mensajePrincipal; }
        public String getSugerenciaContextual() { return sugerenciaContextual; }
    }

    /**
     * Método principal de detección: escanea acumulativamente toda la frase del usuario
     * y genera recomendaciones contextuales en función de las intenciones encontradas.
     */
    public static ResultadoAsistente procesarEntrada(String entradaUsuario) {
        String texto = normalizar(entradaUsuario);
        List<RutinaPropuesta> acumulador = new ArrayList<>();
        Set<String> intentsDetectados = new HashSet<>();

        // 1. ESCANEO ACUMULATIVO SIN DETENCIÓN TEMPRANA (No if-else de salida única)

        // Intención: Hidratación / Agua
        if (contieneAlguna(texto, "agua", "hidrat", "vaso", "sed", "beber", "tomar agua", "botella")) {
            acumulador.add(new RutinaPropuesta(
                    "AGUA",
                    "Hidratación consciente",
                    "Beber agua a lo largo del día para mantener energía y ligereza.",
                    "agua",
                    "#B8D8E8", // Azul pastel
                    "Salud",
                    8,
                    "vasos",
                    "Diario",
                    "08:00 AM"
            ));
            intentsDetectados.add("AGUA");
        }

        // Intención: Lectura / Aprendizaje
        if (contieneAlguna(texto, "leer", "lectura", "libro", "paginas", "novela", "estudiar", "capitulo")) {
            acumulador.add(new RutinaPropuesta(
                    "LECTURA",
                    "Lectura serena",
                    "Avanzar al menos 5 páginas diarias sin prisa y con atención plena.",
                    "lectura",
                    "#F8C8DC", // Rosa pastel
                    "Mente",
                    5,
                    "páginas",
                    "Diario",
                    "09:00 PM"
            ));
            intentsDetectados.add("LECTURA");
        }

        // Intención: Calma / Alivio de Estrés / Respiración
        if (contieneAlguna(texto, "estres", "cansad", "ansiedad", "agobiad", "abrumad", "paz", "calma", "relaj", "agotad", "respirar", "meditar", "presion")) {
            acumulador.add(new RutinaPropuesta(
                    "MEDITACION",
                    "Pausa de respiración y calma",
                    "10 minutos de respiración diafragmática para restaurar tu paz interior.",
                    "meditacion",
                    "#B8E0D2", // Menta pastel
                    "Salud",
                    10,
                    "min",
                    "Diario",
                    "08:30 AM"
            ));
            intentsDetectados.add("MEDITACION");
        }

        // Intención: Ejercicio / Movimiento
        if (contieneAlguna(texto, "ejercicio", "caminar", "correr", "gym", "moverse", "sedentari", "entrenar", "deporte", "yoga", "estiramiento")) {
            acumulador.add(new RutinaPropuesta(
                    "EJERCICIO",
                    "Caminata o estiramiento amable",
                    "20 minutos de movimiento suave para activar la circulación.",
                    "ejercicio",
                    "#D7D9F8", // Lavanda pastel
                    "Cuerpo",
                    20,
                    "min",
                    "Diario",
                    "06:00 PM"
            ));
            intentsDetectados.add("EJERCICIO");
        }

        // Intención: Sueño / Descanso nocturno
        if (contieneAlguna(texto, "dormir", "insomnio", "sueno", "descans", "despertar", "noche", "madrugar", "pantallas")) {
            acumulador.add(new RutinaPropuesta(
                    "SUENO",
                    "Higiene del sueño sin pantallas",
                    "Desconectar dispositivos 30 minutos antes de dormir para un descanso profundo.",
                    "noche",
                    "#E2D4F0", // Lila pastel
                    "Descanso",
                    30,
                    "min",
                    "Diario",
                    "10:00 PM"
            ));
            intentsDetectados.add("SUENO");
        }

        // Intención: Productividad / Foco
        if (contieneAlguna(texto, "productiv", "foco", "enfoque", "concentra", "procrastin", "tiempo", "organiz", "trabajo", "tareas")) {
            acumulador.add(new RutinaPropuesta(
                    "PRODUCTIVIDAD",
                    "Bloque Pomodoro sereno",
                    "25 minutos de trabajo focalizado libre de notificaciones.",
                    "lectura",
                    "#FCE2CE", // Melocotón pastel
                    "Foco",
                    25,
                    "min",
                    "Diario",
                    "09:30 AM"
            ));
            intentsDetectados.add("PRODUCTIVIDAD");
        }

        // Si la frase no contiene ninguna coincidencia explícita, proponemos una rutina base amable
        if (acumulador.isEmpty()) {
            acumulador.add(new RutinaPropuesta(
                    "PAUSA_BASE",
                    "Momento de pausa consciente",
                    "5 minutos para reconectar contigo mismo y tomar aire.",
                    "corazon",
                    "#B8E0D2",
                    "Bienestar",
                    5,
                    "min",
                    "Diario",
                    "10:00 AM"
            ));
            intentsDetectados.add("PAUSA_BASE");
        }

        // 2. SUGERENCIAS CONTEXTUALES CRUZADAS (SINERGIA ENTRE HÁBITOS)
        String sugerenciaContextual = generarSugerenciaContextual(intentsDetectados);
        String mensajePanda = construirMensajePanda(acumulador);

        return new ResultadoAsistente(acumulador, mensajePanda, sugerenciaContextual);
    }

    /**
     * Evalúa las relaciones entre múltiples intenciones acumuladas
     * para entregar una sugerencia de valor añadido única.
     */
    private static String generarSugerenciaContextual(Set<String> intents) {
        // Cruce: Agua + Lectura
        if (intents.contains("AGUA") && intents.contains("LECTURA")) {
            return "💡 Sugerencia: Deja tu botella junto al libro en tu mesa de noche; así te hidratarás con cada capítulo.";
        }
        // Cruce: Agua + Ejercicio
        if (intents.contains("AGUA") && intents.contains("EJERCICIO")) {
            return "💡 Sugerencia: Llena tu botella antes de iniciar el ejercicio para hidratarte en cada pausa sin cortar tu ritmo.";
        }
        // Cruce: Lectura + Sueño
        if (intents.contains("LECTURA") && intents.contains("SUENO")) {
            return "💡 Sugerencia: Cambia el brillo del teléfono por 10 páginas de tu libro en la cama para inducir un sueño profundo y natural.";
        }
        // Cruce: Estrés/Meditación + Sueño
        if (intents.contains("MEDITACION") && intents.contains("SUENO")) {
            return "💡 Sugerencia: Haz tu pausa de respiración justo al acostarte; calmará tu pulso y disipará las tensiones del día.";
        }
        // Cruce: Estrés/Meditación + Ejercicio
        if (intents.contains("MEDITACION") && intents.contains("EJERCICIO")) {
            return "💡 Sugerencia: Inicia tu caminata dedicando los primeros 3 minutos a observar tu respiración y el entorno.";
        }
        // Cruce: Productividad + Agua
        if (intents.contains("PRODUCTIVIDAD") && intents.contains("AGUA")) {
            return "💡 Sugerencia: Cada vez que concluyas un bloque Pomodoro de 25 min, tómate un vaso de agua para oxigenar tu mente.";
        }
        // Cruce: Productividad + Meditación
        if (intents.contains("PRODUCTIVIDAD") && intents.contains("MEDITACION")) {
            return "💡 Sugerencia: Si sientes bloqueo mental, no fuerces la tarea: 3 minutos de respiración restauran la claridad.";
        }

        // Sugerencias unitarias si solo se detectó una intención
        if (intents.contains("AGUA")) {
            return "💡 Sugerencia: Mantén un termo bonito a la vista en tu escritorio; la visibilidad es el mejor recordatorio.";
        }
        if (intents.contains("LECTURA")) {
            return "💡 Sugerencia: Lleva siempre tu libro o e-reader en el bolso; leer pequeños párrafos en tiempos de espera suma mucho.";
        }
        if (intents.contains("MEDITACION")) {
            return "💡 Sugerencia: No intentes dejar la mente en blanco; simplemente observa tus pensamientos como nubes que pasan.";
        }
        if (intents.contains("EJERCICIO")) {
            return "💡 Sugerencia: Deja tu ropa cómoda preparada la noche anterior para reducir la fricción al empezar.";
        }
        if (intents.contains("SUENO")) {
            return "💡 Sugerencia: Mantén tu habitación fresca y en penumbra para que tu cuerpo entienda que es hora del reposo.";
        }
        if (intents.contains("PRODUCTIVIDAD")) {
            return "💡 Sugerencia: Elige solo una prioridad principal por jornada. Menos dispersión equivale a mayor paz mental.";
        }

        return "💡 Sugerencia: Recuerda que la constancia gentil vale más que la exigencia. Da un paso a la vez a tu propio ritmo 🌱.";
    }

    private static String construirMensajePanda(List<RutinaPropuesta> rutinas) {
        if (rutinas.size() == 1) {
            RutinaPropuesta r = rutinas.get(0);
            return "Te escucho con cariño 🌿. Para lo que me cuentas, te propongo comenzar con \"" + r.getNombre() + "\". Al avanzar con pasos pequeños no sentirás agobio.";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("¡Excelente! He detectado ").append(rutinas.size()).append(" metas en tus palabras 🐼✨:\n");
            for (int i = 0; i < rutinas.size(); i++) {
                sb.append("• ").append(rutinas.get(i).getNombre());
                if (i < rutinas.size() - 1) sb.append("\n");
            }
            sb.append("\nAmbas rutinas se complementan muy bien para tu bienestar diario.");
            return sb.toString();
        }
    }

    private static boolean contieneAlguna(String texto, String... palabras) {
        for (String p : palabras) {
            if (texto.contains(p)) {
                return true;
            }
        }
        return false;
    }

    private static String normalizar(String input) {
        if (input == null) return "";
        String limpio = Normalizer.normalize(input, Normalizer.Form.NFD);
        limpio = limpio.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        return limpio.toLowerCase(Locale.ROOT).trim();
    }
}
