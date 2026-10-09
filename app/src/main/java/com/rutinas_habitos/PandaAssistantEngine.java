package com.rutinas_habitos;

import java.text.Normalizer;
import java.util.Locale;

public class PandaAssistantEngine {

    public static class PropuestaPanda {
        public String mensajePanda;
        public Habito habitoSugerido;

        public PropuestaPanda(String mensajePanda, Habito habitoSugerido) {
            this.mensajePanda = mensajePanda;
            this.habitoSugerido = habitoSugerido;
        }
    }

    public static PropuestaPanda analizarYProponer(String entradaUsuario, long usuarioId) {
        String texto = normalizar(entradaUsuario);

        Habito habito = new Habito();
        habito.setUsuarioId(usuarioId);
        habito.setTipoFrecuencia("Diario");
        habito.setDiasSemana("L,M,M,J,V,S,D");
        habito.setRecordatorioActivo(true);
        habito.setHoraRecordatorio("08:30 AM");

        // 1. Detección de Estrés / Cansancio / Ansiedad
        if (contieneAlguna(texto, "estres", "cansad", "agobiad", "abrumad", "ansiedad", "paz", "calma", "relaj", "agotad", "respirar", "presion")) {
            habito.setNombre("Pausa de respiración y calma");
            habito.setIcono("meditacion");
            habito.setColor("#B8E0D2");
            habito.setAreaVida("Salud");
            habito.setTipoMeta("cantidad");
            habito.setMetaCantidad(10);
            habito.setUnidad("min");

            String msg = "Noto que has tenido días con mucha carga 🌿. Tu cuerpo te pide una pausa amable. Te propongo empezar con solo 10 minutos de respiración y calma para renovar tu paz interior.";
            return new PropuestaPanda(msg, habito);
        }

        // 2. Detección de Lectura / Libros
        if (contieneAlguna(texto, "leer", "lectura", "libro", "paginas", "novela")) {
            habito.setNombre("Lectura consciente");
            habito.setIcono("lectura");
            habito.setColor("#F8C8DC");
            habito.setAreaVida("Mente");
            habito.setTipoMeta("cantidad");
            habito.setMetaCantidad(5);
            habito.setUnidad("páginas");
            habito.setHoraRecordatorio("09:00 PM");

            String msg = "¡Me encanta esa meta! 📖 Para que nunca sea una carga pesada, empezaremos con solo 5 páginas al día. Al ritmo constante habrás leído varios libros sin darte cuenta.";
            return new PropuestaPanda(msg, habito);
        }

        // 3. Detección de Productividad / Enfoque / Procrastinación
        if (contieneAlguna(texto, "productiv", "foco", "enfoque", "concentra", "procrastin", "tiempo", "organiz", "trabajo", "estudio")) {
            habito.setNombre("Bloque de enfoque profundo");
            habito.setIcono("lectura");
            habito.setColor("#F8C8DC");
            habito.setAreaVida("Foco");
            habito.setTipoMeta("cantidad");
            habito.setMetaCantidad(25);
            habito.setUnidad("min");
            habito.setHoraRecordatorio("09:30 AM");

            String msg = "La productividad serena no se trata de hacer todo a la vez, sino de avanzar con intención ⚡. Te propongo una sesión Pomodoro de 25 minutos de enfoque profundo sin distracciones.";
            return new PropuestaPanda(msg, habito);
        }

        // 4. Detección de Agua e Hidratación
        if (contieneAlguna(texto, "agua", "hidrat", "vaso", "sed", "beber")) {
            habito.setNombre("Beber 2L de agua");
            habito.setIcono("agua");
            habito.setColor("#B8D8E8");
            habito.setAreaVida("Salud");
            habito.setTipoMeta("cantidad");
            habito.setMetaCantidad(8);
            habito.setUnidad("vasos");
            habito.setHoraRecordatorio("08:00 AM");

            String msg = "El agua es vida y vitalidad 💧. Con 8 vasitos distribuidos a lo largo del día mantendrás tu mente despierta y tu cuerpo agradecido. ¡Lo registraremos juntos!";
            return new PropuestaPanda(msg, habito);
        }

        // 5. Detección de Movimiento / Ejercicio / Caminar
        if (contieneAlguna(texto, "ejercicio", "caminar", "correr", "gym", "moverse", "sedentari", "entrenar", "deporte", "yoga")) {
            habito.setNombre("Caminata al aire libre");
            habito.setIcono("ejercicio");
            habito.setColor("#D7D9F8");
            habito.setAreaVida("Salud");
            habito.setTipoMeta("cantidad");
            habito.setMetaCantidad(20);
            habito.setUnidad("min");
            habito.setHoraRecordatorio("06:00 PM");

            String msg = "Dar pasos bajo el cielo despeja la mente y llena el pecho de energía 👟. Con 20 minutos de caminata amable empezarás a sentir la diferencia.";
            return new PropuestaPanda(msg, habito);
        }

        // 6. Detección de Sueño / Descanso nocturno
        if (contieneAlguna(texto, "dormir", "sueno", "insomnio", "noche", "descans", "despertar", "pantalla")) {
            habito.setNombre("Desconexión antes de dormir");
            habito.setIcono("noche");
            habito.setColor("#D7D9F8");
            habito.setAreaVida("Mente");
            habito.setTipoMeta("cantidad");
            habito.setMetaCantidad(30);
            habito.setUnidad("min");
            habito.setHoraRecordatorio("10:00 PM");

            String msg = "Para despertar con alegría, el descanso de la noche es sagrado 🌙. Te propongo dejar las pantallas 30 minutos antes de dormir para permitir que tu mente repose.";
            return new PropuestaPanda(msg, habito);
        }

        // 7. Por defecto: Hábito amable de Gratitud / Meditación
        habito.setNombre("Momento de gratitud matutina");
        habito.setIcono("corazon");
        habito.setColor("#B8E0D2");
        habito.setAreaVida("Mente");
        habito.setTipoMeta("simple");
        habito.setMetaCantidad(1);
        habito.setUnidad("veces");
        habito.setHoraRecordatorio("08:00 AM");

        String msg = "Todo gran cambio florece con un pequeño paso 🌱. Te sugiero comenzar con 1 momento de gratitud al despertar para orientar tu día con serenidad y propósito.";
        return new PropuestaPanda(msg, habito);
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
        String normalized = Normalizer.normalize(input.toLowerCase(Locale.ROOT), Normalizer.Form.NFD);
        return normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    }
}
