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
 * 1. Procesamiento Conversacional Inteligente: Responde inteligentemente y de manera
 *    empática a cualquier comentario (ej: clima "hoy hace un buen día soleado", emociones,
 *    saludos o reflexiones de vida) sin requerir conexión a internet.
 * 2. Procesamiento Múltiple Acumulativo de Hábitos: Escanea toda la frase del usuario,
 *    detecta todas las intenciones presentes (agua, lectura, meditación, etc.) y las acumula.
 * 3. Sugerencias Contextuales Sinérgicas: Evalúa las relaciones cruzadas entre los hábitos
 *    detectados para entregar consejos de alto valor práctico y armonioso.
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
     * Procesa cualquier entrada del usuario: comentarios del día a día, clima,
     * emociones, dudas o peticiones de hábitos.
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

        // 2. RESPUESTA CONVERSACIONAL INTELIGENTE (CLIMA, CHARLA, EMOCIONES, PREGUNTAS)
        String respuestaConversacional = responderConversacionNatural(texto);

        if (respuestaConversacional != null) {
            // Si el usuario comentó sobre el clima y no pidió una rutina explícita, podemos
            // ofrecer una propuesta complementaria suave:
            if (acumulador.isEmpty()) {
                if (contieneAlguna(texto, "soleado", "sol", "buen dia", "lindo dia")) {
                    acumulador.add(new RutinaPropuesta(
                            "CAMINATA_SOL", "Paseo al aire libre y sol",
                            "15 minutos caminando bajo la luz natural para recargar energía.",
                            "ejercicio", "#B8E0D2", "Bienestar", 15, "min", "Diario", "11:00 AM"
                    ));
                    intentsDetectados.add("EJERCICIO");
                } else if (contieneAlguna(texto, "lluvia", "lloviendo", "frio", "nublado")) {
                    acumulador.add(new RutinaPropuesta(
                            "PAUSA_CALIDA", "Momento de infusión y calma",
                            "10 minutos disfrutando un té o bebida tibia en serenidad.",
                            "meditacion", "#F8C8DC", "Calma", 10, "min", "Diario", "05:00 PM"
                    ));
                    intentsDetectados.add("MEDITACION");
                }
            }

            String sugerencia = !acumulador.isEmpty()
                    ? generarSugerenciaContextual(intentsDetectados)
                    : "🌱 Consejo Panda: Camina a tu propio compás hoy; cada instante presente es un regalo.";

            return new ResultadoAsistente(acumulador, respuestaConversacional, sugerencia);
        }

        // 3. RESPUESTAS PARA ENTRADAS EXCLUSIVAS DE RUTINAS
        if (acumulador.isEmpty()) {
            acumulador.add(new RutinaPropuesta(
                    "PAUSA_BASE", "Momento de pausa consciente",
                    "5 minutos para reconectar contigo mismo y tomar aire.",
                    "corazon", "#B8E0D2", "Bienestar", 5, "min", "Diario", "10:00 AM"
            ));
            intentsDetectados.add("PAUSA_BASE");
        }

        String sugerenciaContextual = generarSugerenciaContextual(intentsDetectados);
        String mensajePanda = construirMensajePanda(acumulador);

        return new ResultadoAsistente(acumulador, mensajePanda, sugerenciaContextual);
    }

    /**
     * Módulo de conversación natural, clima, empatía y filosofía Zen.
     */
    private static String responderConversacionNatural(String texto) {
        // --- Clima y Naturaleza ---
        if (contieneAlguna(texto, "soleado", "sol", "buen dia", "lindo dia", "hermoso dia", "dia lindo")) {
            return "¡Qué hermoso día soleado! ☀️ La luz natural eleva la serotonina y revitaliza tus ritmos biológicos. Es una oportunidad perfecta para estirar las piernas, sentir el aire fresco o disfrutar un buen vaso de agua bajo el sol 🌿.";
        }
        if (contieneAlguna(texto, "lluvia", "lloviendo", "tormenta", "chubasco")) {
            return "Los días lluviosos tienen una magia especial para reconectar hacia adentro 🌧️. El sonido del agua es un arrullo natural: ideal para bajar revoluciones, preparar una infusión tibia y acompañarla con una lectura amable.";
        }
        if (contieneAlguna(texto, "frio", "helado", "invernal")) {
            return "¡Qué día tan fresco! 🧣 Abrígate bien y regálate pequeños momentos de confort: estiramientos suaves en casa o una pausa con tu bebida caliente favorita te reconfortarán el alma.";
        }
        if (contieneAlguna(texto, "calor", "bochorno", "caluroso")) {
            return "En días calurosos, tu cuerpo pide ligereza y atención 🍃💧. Mantén cerca tu botella de agua, busca espacios frescos y recuerda que no hay prisa para cumplir tus metas.";
        }
        if (contieneAlguna(texto, "nublado", "gris")) {
            return "Los cielos nublados nos invitan a la calma reflexiva ☁️. No todos los días tienen que ser vibrantes; los tonos suaves del cielo nos recuerdan que la tranquilidad también es un hermoso logro.";
        }

        // --- Saludos y Bienvenida ---
        if (contieneAlguna(texto, "hola", "buenas", "que tal", "que onda", "saludos")) {
            return "¡Hola! Me alegra mucho verte por aquí 🐼✨ Me siento sereno y listo para acompañarte. ¿Cómo te sientes hoy? Cuéntame si deseas cultivar una rutina o simplemente charlar un momento.";
        }
        if (contieneAlguna(texto, "buenos dias")) {
            return "¡Muy buenos días! 🌅 Que este nuevo amanecer te traiga ligereza y calma. Recuerda que no necesitas conquistar el mundo hoy, solo dar un pasito amable a la vez.";
        }
        if (contieneAlguna(texto, "buenas tardes")) {
            return "¡Buenas tardes! ☀️ Espero que tu jornada esté fluyendo con serenidad. ¿Es momento de una pausa para respirar hondo o un sorbo de agua fresca?";
        }
        if (contieneAlguna(texto, "buenas noches", "hasta manana", "a dormir")) {
            return "¡Buenas noches! 🌙 Cierra los ojos con la certeza de que diste lo mejor de ti hoy. Suelta las preocupaciones; mañana habrá un nuevo comienzo esperándote a tu propio compás.";
        }
        if (contieneAlguna(texto, "como estas", "como te va", "que haces")) {
            return "¡Me encuentro en profunda calma y muy feliz de conversar contigo! 🎋 Gracias por preguntar. ¿Y tú, cómo late tu corazón hoy?";
        }

        // --- Estados Emocionales y Empatía ---
        if (contieneAlguna(texto, "triste", "llorar", "desanimado", "bajon", "pena", "deprimid", "mal")) {
            return "Siento mucho que te sientas así 🤍. Abraza esa emoción sin juzgarla; está bien no estar bien. En Rutina Pastel no hay espacio para la culpa. Regálate ternura hoy: cobijarte, descansar o beber un vaso de agua es más que suficiente.";
        }
        if (contieneAlguna(texto, "feliz", "contento", "alegre", "genial", "motivado", "emocionado", "excelente")) {
            return "¡Qué hermosa energía! 🌟 La alegría compartida florece aún más. Aprovecha esta vitalidad para disfrutar tus rutinas favoritas o agradecer los pequeños milagros cotidianos.";
        }
        if (contieneAlguna(texto, "solo", "soledad")) {
            return "Aunque a veces parezca que estamos solos, este espacio y yo estamos aquí para ti 🤍. La soledad también puede ser un refugio amable para aprender a ser tu propio mejor amigo.";
        }
        if (contieneAlguna(texto, "estres", "ansiedad", "abrumado", "agobiado", "colapso")) {
            return "Detén todo un instante conmigo 🍃. Inhala hondo sintiendo cómo se expande tu pecho... 1, 2, 3, 4... y exhala lento soltando los hombros. No tienes que resolverlo todo en este minuto. Una respiración a la vez.";
        }
        if (contieneAlguna(texto, "enojado", "molesto", "rabia", "bronca", "furioso")) {
            return "Es completamente válido sentir frustración a veces 🌿. No te castigues por sentirla; respira, date un espacio para caminar y deja que el agua se aclare por sí sola.";
        }

        // --- Filosofía, Dudas y Orientación ---
        if (contieneAlguna(texto, "quien eres", "que eres", "como te llamas")) {
            return "Soy tu Acompañante Panda Zen 🐼 en Rutina Pastel. Mi propósito es recordarte que el autocuidado se cultiva con amor y pasos diminutos, libre de juicios y sin culpas.";
        }
        if (contieneAlguna(texto, "consejo", "que me recomiendas", "como empiezo", "no se que hacer")) {
            return "Mi mejor sugerencia es 'hacerlo ridículamente fácil' 🌱. Elige una meta tan pequeña que sea imposible fallar (por ejemplo, tomar 1 vaso de agua al despertar o leer 2 minutos). Al eliminar la resistencia, la constancia florece sin esfuerzo.";
        }
        if (contieneAlguna(texto, "constancia", "disciplina", "fuerza de voluntad")) {
            return "La constancia no se trata de no fallar nunca, sino de regresar con amabilidad cuando te desvías 🎋. Por eso nuestro Modo Sin Culpa te cuida: si hoy no pudiste, mañana retomas con calma y sin penalizaciones.";
        }
        if (contieneAlguna(texto, "rutina pastel", "que es esta app")) {
            return "Rutina Pastel es tu refugio de bienestar y hábitos amables 🌸🎨. Cuidamos tu salud mental, tu cuerpo y tu tiempo a través de colores suaves y acompañamiento empático.";
        }
        if (contieneAlguna(texto, "gracias", "muchas gracias", "te quiero", "lindo panda", "buen trabajo")) {
            return "¡Muchísimas gracias a ti! 🐼💕 Es un inmenso honor acompañarte en tus días. Recuerda que siempre estaré aquí cuando necesites serenidad.";
        }

        return null;
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
        if (intents.contains("GRATITUD")) {
            return "💡 Sugerencia: Puedes agradecer incluso cosas diminutas, como el aroma del café o un rayo de sol entrando por la ventana.";
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
