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
 * Capacidades:
 * 1. Procesamiento Conversacional Inteligente: Responde de forma cálida, inteligente
 *    y empática a cualquier saludo (ej: "ola", "hola"), preguntas del día a día, clima,
 *    emociones o reflexiones sin requerir conexión a internet.
 * 2. Procesamiento Múltiple Acumulativo de Hábitos: Escanea toda la frase del usuario,
 *    detecta todas las intenciones de hábitos presentes (agua, lectura, ejercicio, etc.) y las acumula.
 * 3. Sugerencias Contextuales Sinérgicas: Evalúa las relaciones entre los hábitos detectados
 *    para entregar consejos prácticos de alto valor.
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
            h.setDiasSemana("L,M,X,J,V,S,D");
            h.setTipoMeta("cantidad");
            h.setMetaCantidad(metaCantidad);
            h.setUnidad(unidad);
            h.setHoraRecordatorio(horaSugerida);
            h.setRecordatorioActivo(true);
            h.setTipoSonidoNotificacion("default");
            return h;
        }
    }

    // Resultado consolidado para la interfaz
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
     * Procesa cualquier entrada del usuario: saludos, clima, emociones,
     * charlas casuales o peticiones de hábitos.
     */
    public static ResultadoAsistente procesarEntrada(String entradaUsuario) {
        String texto = normalizar(entradaUsuario);
        List<RutinaPropuesta> acumulador = new ArrayList<>();
        Set<String> intentsDetectados = new HashSet<>();

        // 1. ESCANEO ACUMULATIVO DE INTENCIONES DE HÁBITOS
        // Hidratación / Agua
        if (contieneAlguna(texto, "agua", "hidrat", "vaso", "sed", "beber", "tomar agua", "botella")) {
            acumulador.add(new RutinaPropuesta(
                    "AGUA", "Hidratación consciente",
                    "Beber agua a lo largo del día para mantener energía y ligereza.",
                    "agua", "#B8D8E8", "Salud", 8, "vasos", "Diario", "08:00 AM"
            ));
            intentsDetectados.add("AGUA");
        }

        // Lectura / Aprendizaje
        if (contieneAlguna(texto, "leer", "lectura", "libro", "paginas", "novela", "estudiar", "capitulo")) {
            acumulador.add(new RutinaPropuesta(
                    "LECTURA", "Lectura serena",
                    "Avanzar al menos 5 páginas diarias sin prisa y con atención plena.",
                    "lectura", "#F8C8DC", "Mente", 5, "páginas", "Diario", "09:00 PM"
            ));
            intentsDetectados.add("LECTURA");
        }

        // Calma / Estrés / Respiración / Meditación
        if (contieneAlguna(texto, "estres", "ansiedad", "agobiad", "abrumad", "paz", "calma", "relaj", "agotad", "respirar", "meditar", "presion", "meditacion")) {
            acumulador.add(new RutinaPropuesta(
                    "MEDITACION", "Pausa de respiración y calma",
                    "10 minutos de respiración diafragmática para restaurar tu paz interior.",
                    "meditacion", "#B8E0D2", "Salud", 10, "min", "Diario", "08:30 AM"
            ));
            intentsDetectados.add("MEDITACION");
        }

        // Ejercicio / Movimiento
        if (contieneAlguna(texto, "ejercicio", "caminar", "correr", "gym", "moverse", "sedentari", "entrenar", "deporte", "yoga", "estiramiento", "paseo")) {
            acumulador.add(new RutinaPropuesta(
                    "EJERCICIO", "Caminata o estiramiento amable",
                    "20 minutos de movimiento suave para activar la circulación.",
                    "ejercicio", "#D7D9F8", "Cuerpo", 20, "min", "Diario", "06:00 PM"
            ));
            intentsDetectados.add("EJERCICIO");
        }

        // Sueño / Descanso nocturno
        if (contieneAlguna(texto, "dormir", "insomnio", "sueno", "descans", "noche", "madrugar", "pantallas", "acostar")) {
            acumulador.add(new RutinaPropuesta(
                    "SUENO", "Higiene del sueño sin pantallas",
                    "Desconectar dispositivos 30 minutos antes de dormir para un descanso profundo.",
                    "noche", "#E2D4F0", "Descanso", 30, "min", "Diario", "10:00 PM"
            ));
            intentsDetectados.add("SUENO");
        }

        // Productividad / Foco
        if (contieneAlguna(texto, "productiv", "foco", "enfoque", "concentra", "procrastin", "tiempo", "organiz", "trabajo", "tareas")) {
            acumulador.add(new RutinaPropuesta(
                    "PRODUCTIVIDAD", "Bloque Pomodoro sereno",
                    "25 minutos de trabajo focalizado libre de notificaciones.",
                    "lectura", "#FCE2CE", "Foco", 25, "min", "Diario", "09:30 AM"
            ));
            intentsDetectados.add("PRODUCTIVIDAD");
        }

        // Gratitud
        if (contieneAlguna(texto, "gratitud", "agradecer", "agradecid", "diario", "apreciar")) {
            acumulador.add(new RutinaPropuesta(
                    "GRATITUD", "3 agradecimientos diarios",
                    "Anotar mentalmente o en tu libreta tres pequeños destellos de alegría de tu jornada.",
                    "corazon", "#F8C8DC", "Mente", 3, "agradecimientos", "Diario", "09:30 PM"
            ));
            intentsDetectados.add("GRATITUD");
        }

        // 2. RESPUESTA CONVERSACIONAL INTELIGENTE (CHATS, SALUDOS, PREGUNTAS, CLIMA)
        String respuestaConversacional = responderConversacionNatural(texto);

        if (respuestaConversacional != null) {
            String sugerencia = null;
            if (!acumulador.isEmpty()) {
                sugerencia = generarSugerenciaContextual(intentsDetectados);
            }
            return new ResultadoAsistente(acumulador, respuestaConversacional, sugerencia);
        }

        // 3. RESPUESTAS PARA ENTRADAS EXCLUSIVAS DE RUTINAS O CONSULTAS LIBRES
        if (acumulador.isEmpty()) {
            return new ResultadoAsistente(
                    acumulador,
                    "Te escucho con cariño 🐼. Estoy aquí para acompañarte sin prisas. ¿Te gustaría trabajar en alguna rutina como tomar más agua, leer, meditar o hacer ejercicio, o simplemente prefieres charlar?",
                    "💡 Recuerda: Puedes pedirme 'quiero tomar agua y leer' para crear múltiples hábitos a la vez."
            );
        }

        String sugerenciaContextual = generarSugerenciaContextual(intentsDetectados);
        String mensajePanda = construirMensajePanda(acumulador);

        return new ResultadoAsistente(acumulador, mensajePanda, sugerenciaContextual);
    }

    /**
     * Módulo de conversación natural, clima, empatía y diálogo interactivo.
     */
    private static String responderConversacionNatural(String texto) {
        // --- Saludos y Bienvenida (incluyendo "ola", "hola", etc.) ---
        if (esSaludo(texto)) {
            return "¡Hola! Me alegra muchísimo saludarte 🐼✨ ¿Cómo va tu día? Cuéntame cómo te sientes o si te gustaría que organicemos juntos algún hábito o rutina amable.";
        }
        if (contieneAlguna(texto, "buenos dias", "buen dia", "lindo dia")) {
            return "¡Muy buenos días! 🌅 Que este nuevo amanecer te traiga ligereza y paz. Recuerda dar un pasito amable a la vez sin exigirte de más.";
        }
        if (contieneAlguna(texto, "buenas tardes", "buena tarde")) {
            return "¡Buenas tardes! ☀️ Espero que tu jornada esté fluyendo con serenidad. ¿Es momento de una pausa para respirar hondo o un sorbo de agua fresca?";
        }
        if (contieneAlguna(texto, "buenas noches", "buena noche", "hasta manana", "a dormir")) {
            return "¡Buenas noches! 🌙 Cierra los ojos con la certeza de que diste lo mejor de ti hoy. Suelta las preocupaciones; mañana habrá un nuevo amanecer esperándote a tu propio compás.";
        }
        if (contieneAlguna(texto, "como estas", "como te va", "que haces", "como andas", "todo bien")) {
            return "¡Me encuentro en profunda calma y muy feliz de conversar contigo! 🌿 Gracias por preguntar. ¿Y tú, cómo te sientes en este instante?";
        }

        // --- Clima y Naturaleza ---
        if (contieneAlguna(texto, "soleado", "hace sol", "dia soleado", "sol brillante", "hermoso dia")) {
            return "¡Qué hermoso día soleado! ☀️ La luz natural recarga el ánimo y despierta la vitalidad. Es una oportunidad perfecta para estirar los brazos, salir a caminar un momento o tomar agua fresca bajo el sol 🌿.";
        }
        if (contieneAlguna(texto, "lluvia", "lloviendo", "tormenta", "chubasco")) {
            return "Los días lluviosos tienen una magia especial para reconectar hacia adentro 🌧️. El sonido del agua es un arrullo natural: ideal para bajar revoluciones, preparar una infusión tibia y acompañarla con una lectura amable.";
        }
        if (contieneAlguna(texto, "frio", "helado", "invernal")) {
            return "¡Qué día tan fresco! 🧣 Abrígate bien y regálate momentos de confort: estiramientos suaves en casa o una pausa con tu bebida tibia favorita te reconfortarán el alma.";
        }
        if (contieneAlguna(texto, "calor", "bochorno", "caluroso")) {
            return "En días calurosos tu cuerpo pide ligereza y frescura 🍃💧. Mantén cerca tu botella de agua, busca sombra y recuerda que no hay prisa.";
        }
        if (contieneAlguna(texto, "nublado", "gris")) {
            return "Los cielos nublados nos invitan a la calma reflexiva ☁️. No todos los días tienen que ser vibrantes; la tranquilidad también es un hermoso logro.";
        }

        // --- Estados Emocionales y Empatía ---
        if (contieneAlguna(texto, "triste", "llorar", "desanimado", "bajon", "pena", "deprimid", "mal")) {
            return "Siento mucho que te sientas así 🤍. Abraza esa emoción sin juzgarla; está bien no estar bien. En Rutinas Pastel no hay espacio para la culpa. Regálate ternura hoy: descansar o beber un vaso de agua es más que suficiente.";
        }
        if (contieneAlguna(texto, "feliz", "contento", "alegre", "genial", "motivado", "emocionado", "excelente", "super")) {
            return "¡Qué hermosa energía tienes hoy! 🌟 La alegría compartida florece aún más. Aprovecha esta vitalidad para disfrutar tus hábitos favoritos o agradecer los pequeños momentos del día.";
        }
        if (contieneAlguna(texto, "solo", "soledad")) {
            return "Aunque a veces parezca que estamos solos, este refugio y yo estamos aquí para ti 🤍. La soledad también puede ser un espacio amable para aprender a ser tu propio mejor amigo.";
        }
        if (contieneAlguna(texto, "estres", "ansiedad", "abrumado", "agobiado", "colapso")) {
            return "Detén todo un instante conmigo 🍃. Inhala hondo sintiendo cómo se expande tu pecho... 1, 2, 3, 4... y exhala lento soltando los hombros. No tienes que resolverlo todo ahora. Una respiración a la vez.";
        }
        if (contieneAlguna(texto, "enojado", "molesto", "rabia", "bronca", "furioso")) {
            return "Es completamente válido sentir frustración a veces 🌿. No te castigues por sentirla; respira, date un espacio para caminar y deja que el agua se aclare por sí sola.";
        }
        if (contieneAlguna(texto, "flojera", "pereza", "desgano", "no tengo ganas", "sin ganas")) {
            return "Es totalmente normal sentir pereza o cansancio a veces 🐼💤. No te obligues a grandes hazañas: haz algo ridículamente pequeño, como sentarte erguido o beber medio vaso de agua. Ese pequeño paso reactiva tu energía con amor.";
        }
        if (contieneAlguna(texto, "aburrido", "aburrimiento")) {
            return "¡El aburrimiento puede ser el inicio de una linda idea! 💡 ¿Qué tal leer unas 5 páginas de algo nuevo, salir a mirar el cielo o estirarte un par de minutos?";
        }

        // --- Filosofía, Dudas y Orientación ---
        if (contieneAlguna(texto, "quien eres", "que eres", "como te llamas")) {
            return "Soy tu Acompañante Panda 🐼 en Rutinas Pastel. Mi propósito es recordarte que el autocuidado se cultiva con amor y pasos diminutos, libre de juicios y sin culpas.";
        }
        if (contieneAlguna(texto, "que puedes hacer", "que haces", "como me ayudas", "ayuda")) {
            return "¡Puedo ayudarte en mucho! 🌸\n• Conversar contigo cuando necesites calma o desahogarte.\n• Crear y sugerirte rutinas (agua, lectura, ejercicio, meditación).\n• Cuidar tu ritmo sin culpa para que seas constante a tu propio tiempo.";
        }
        if (contieneAlguna(texto, "consejo", "que me recomiendas", "como empiezo", "no se que hacer")) {
            return "Mi mejor sugerencia es 'hacerlo ridículamente fácil' 🌱. Elige una meta tan pequeña que sea imposible fallar (como tomar 1 vaso de agua al despertar o leer 2 minutos). Al eliminar la resistencia, la constancia florece sin esfuerzo.";
        }
        if (contieneAlguna(texto, "constancia", "disciplina", "fuerza de voluntad")) {
            return "La constancia no se trata de no fallar nunca, sino de regresar con amabilidad cuando te desvías 🎋. Por eso nuestro Modo Sin Culpa te cuida: si hoy no pudiste, mañana retomas con calma y sin penalizaciones.";
        }
        if (contieneAlguna(texto, "rutina pastel", "que es esta app", "de que trata")) {
            return "Rutinas Pastel es tu refugio de bienestar y hábitos amables 🌸🎨. Cuidamos tu salud mental, tu cuerpo y tu tiempo a través de colores suaves y acompañamiento empático.";
        }
        if (contieneAlguna(texto, "gracias", "muchas gracias", "te quiero", "lindo panda", "buen trabajo", "te amo", "genial")) {
            return "¡Muchísimas gracias a ti! 🐼💕 Es un inmenso honor acompañarte en tus días. Recuerda que siempre estaré aquí cuando necesites serenidad.";
        }

        return null;
    }

    private static boolean esSaludo(String texto) {
        if (texto.equals("ola") || texto.equals("hola") || texto.equals("holis") || texto.equals("holi") ||
            texto.equals("hey") || texto.equals("buenas") || texto.equals("hello") || texto.equals("hi") ||
            texto.equals("que tal") || texto.equals("olaa") || texto.equals("holaa") || texto.equals("saludos")) {
            return true;
        }
        return contieneAlguna(texto, "hola", "ola ", "ola!", "ola,", "buenas", "que tal", "que onda", "saludos");
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
        // Cruce: Meditación + Sueño
        if (intents.contains("MEDITACION") && intents.contains("SUENO")) {
            return "💡 Sugerencia: Haz tu pausa de respiración justo al acostarte; calmará tu pulso y disipará las tensiones del día.";
        }
        // Cruce: Ejercicio + Meditación
        if (intents.contains("EJERCICIO") && intents.contains("MEDITACION")) {
            return "💡 Sugerencia: Dedica los últimos 3 minutos de tu caminata a respirar con atención plena sintiendo el aire fresco.";
        }
        // Cruce: Productividad + Meditación/Agua
        if (intents.contains("PRODUCTIVIDAD") && intents.contains("AGUA")) {
            return "💡 Sugerencia: Bebe un vaso de agua al terminar cada bloque de trabajo para resetear tu concentración.";
        }
        return "💡 Recuerda: Pequeños pasos sostenibles valen más que grandes esfuerzos aislados.";
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
            sb.append("\nEstas rutinas se complementan muy bien para tu bienestar diario.");
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
