package ec.edu.uce.controlador;

/*
* Clase controlador de horas pico
* Simula el trafico real de quito en diferentes horas
*
* HORAS PICO EN QUITO:
* - 07:00 - 09:00 (Mañana): Factor 1.5x (+50% tiempo)
* - 12:00 - 14:00 (Mediodía): Factor 1.3x (+30% tiempo)
* - 17:00 - 19:00 (Tarde): Factor 1.6x (+60% tiempo)
* - Resto del día: Factor 1.0x (tiempo normal)
 */

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GestorHorasPico {

    public static double obtenerFactorTrafico(){
        LocalTime ahora = LocalTime.now();
        int hora = ahora.getHour();
        if (hora >= 7 && hora <= 9){
            return 1.50;
        }else if (hora >= 12 && hora <= 14){
            return 1.30;
        } else if (hora >= 17 && hora <= 19) {
            return 1.60;
        }
        return 1.0;
    }

    public static boolean esHoraPico() {
        LocalTime ahora = LocalTime.now();
        int hora = ahora.getHour();
        // Hora pico de la mañana (7:00 - 9:00)
        if (hora >= 7 && hora < 9) {
            return true;
        }
        // Hora pico del mediodía (12:00 - 14:00)
        else if (hora >= 12 && hora < 14) {
            return true;
        }
        // Hora pico de la tarde (17:00 - 19:00)
        else if (hora >= 17 && hora < 19) {
            return true;
        }
        return false;
    }

    public static String obtenerEstadoTrafico() {
        double factor = obtenerFactorTrafico();

        if (factor >= 1.5) {
            return "🔴 TRÁFICO ALTO";
        } else if (factor >= 1.3) {
            return "🟡 TRÁFICO MODERADO";
        } else {
            return "🟢 TRÁFICO NORMAL";
        }
    }

    public static String obtenerDescripcionHoraria() {
        LocalTime ahora = LocalTime.now();
        int hora = ahora.getHour();

        if (hora >= 7 && hora < 9) {
            return "Hora pico matutina - Incremento +50% en tiempos";
        } else if (hora >= 12 && hora < 14) {
            return "Hora pico mediodía - Incremento +30% en tiempos";
        } else if (hora >= 17 && hora < 19) {
            return "Hora pico vespertina - Incremento +60% en tiempos";
        } else {
            return "Tráfico fluido - Tiempos normales";
        }
    }

    public static String obtenerHoraActual() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    public static int obtenerPorcentajeIncremento() {
        double factor = obtenerFactorTrafico();
        return (int)((factor - 1.0) * 100);
    }

    public static String obtenerReporteCompleto() {
        StringBuilder sb = new StringBuilder();

        sb.append("╔═══════════════════════════════════════════════════════════════╗\n");
        sb.append("║      REPORTE DE HORAS PICO - QUITO, ECUADOR                   ║\n");
        sb.append("║                 Sistema ECU 911 - UCE                         ║\n");
        sb.append("╚═══════════════════════════════════════════════════════════════╝\n\n");

        sb.append("📊 FRANJAS HORARIAS Y FACTORES DE TRÁFICO:\n\n");

        // Hora pico matutina
        sb.append("┌───────────────────────────────────────────────────────────────┐\n");
        sb.append("│ 🌅 HORA PICO MATUTINA                                         │\n");
        sb.append("├───────────────────────────────────────────────────────────────┤\n");
        sb.append("│ ⏰ Horario: 07:00 - 09:00                                     │\n");
        sb.append("│ 🔴 Estado: TRÁFICO ALTO                                       │\n");
        sb.append("│ 📈 Factor: 1.5x (Incremento del 50%)                          │\n");
        sb.append("│ 🚗 Descripción: Hora pico de ingreso laboral y escolar        │\n");
        sb.append("│ 📍 Zonas más afectadas: Norte y Centro de Quito               │\n");
        sb.append("└───────────────────────────────────────────────────────────────┘\n\n");

        // Hora pico mediodía
        sb.append("┌───────────────────────────────────────────────────────────────┐\n");
        sb.append("│ ☀️ HORA PICO MEDIODÍA                                         │\n");
        sb.append("├───────────────────────────────────────────────────────────────┤\n");
        sb.append("│ ⏰ Horario: 12:00 - 14:00                                     │\n");
        sb.append("│ 🟡 Estado: TRÁFICO MODERADO                                   │\n");
        sb.append("│ 📈 Factor: 1.3x (Incremento del 30%)                          │\n");
        sb.append("│ 🚗 Descripción: Hora de almuerzo y movilización media         │\n");
        sb.append("│ 📍 Zonas más afectadas: Centro de Quito                       │\n");
        sb.append("└───────────────────────────────────────────────────────────────┘\n\n");

        // Hora pico vespertina
        sb.append("┌───────────────────────────────────────────────────────────────┐\n");
        sb.append("│ 🌆 HORA PICO VESPERTINA                                       │\n");
        sb.append("├───────────────────────────────────────────────────────────────┤\n");
        sb.append("│ ⏰ Horario: 17:00 - 19:00                                     │\n");
        sb.append("│ 🔴 Estado: TRÁFICO MUY ALTO                                   │\n");
        sb.append("│ 📈 Factor: 1.6x (Incremento del 60%)                          │\n");
        sb.append("│ 🚗 Descripción: Hora pico de salida laboral (LA MÁS CRÍTICA)  │\n");
        sb.append("│ 📍 Zonas más afectadas: Norte, Centro y Sur de Quito          │\n");
        sb.append("└───────────────────────────────────────────────────────────────┘\n\n");

        // Horario normal
        sb.append("┌───────────────────────────────────────────────────────────────┐\n");
        sb.append("│ 🟢 HORARIO NORMAL                                             │\n");
        sb.append("├───────────────────────────────────────────────────────────────┤\n");
        sb.append("│ ⏰ Horario: Resto del día                                     │\n");
        sb.append("│ 🟢 Estado: TRÁFICO FLUIDO                                     │\n");
        sb.append("│ 📈 Factor: 1.0x (Sin incremento)                              │\n");
        sb.append("│ 🚗 Descripción: Condiciones normales de circulación           │\n");
        sb.append("│ 📍 Zonas: Todas las zonas de Quito                            │\n");
        sb.append("└───────────────────────────────────────────────────────────────┘\n\n");

        // Estado actual
        LocalTime ahora = LocalTime.now();
        sb.append("╔═══════════════════════════════════════════════════════════════╗\n");
        sb.append("║               ESTADO ACTUAL DEL TRÁFICO                       ║\n");
        sb.append("╚═══════════════════════════════════════════════════════════════╝\n\n");
        sb.append(String.format("🕐 Hora actual: %s\n", obtenerHoraActual()));
        sb.append(String.format("🚦 Estado: %s\n", obtenerEstadoTrafico()));
        sb.append(String.format("📊 Factor aplicado: %.1fx\n", obtenerFactorTrafico()));
        sb.append(String.format("➕ Incremento: +%d%%\n", obtenerPorcentajeIncremento()));
        sb.append(String.format("ℹ️  %s\n\n", obtenerDescripcionHoraria()));

        // Estado de hora pico
        sb.append(String.format("⚠️  ¿Es hora pico?: %s\n\n", esHoraPico() ? "SÍ - Rutas alternativas activas" : "NO"));

        // Recomendaciones
        sb.append("╔═══════════════════════════════════════════════════════════════╗\n");
        sb.append("║                    RECOMENDACIONES                            ║\n");
        sb.append("╚═══════════════════════════════════════════════════════════════╝\n\n");
        sb.append("💡 Para emergencias en horas pico:\n");
        sb.append("   • Considere rutas alternativas menos congestionadas\n");
        sb.append("   • El tiempo de llegada puede incrementarse significativamente\n");
        sb.append("   • Coordine con autoridades de tránsito si es necesario\n");
        sb.append("   • Active protocolos de emergencia prioritaria\n");
        sb.append("   • Evalúe traslado directo al hospital más cercano\n\n");

        sb.append("═══════════════════════════════════════════════════════════════\n");

        return sb.toString();
    }

    public static String obtenerInformacionPorZona(String nombreHospital) {
        StringBuilder sb = new StringBuilder();

        sb.append("╔═══════════════════════════════════════════════════════════════╗\n");
        sb.append(String.format("║  INFORMACIÓN DE TRÁFICO - %-35s║\n", nombreHospital));
        sb.append("╚═══════════════════════════════════════════════════════════════╝\n\n");

        // Determinar zona según hospital
        String zona = "";
        String descripcion = "";

        if (nombreHospital.contains("Pablo Arturo") || nombreHospital.contains("Policía")) {
            zona = "ZONA NORTE-OESTE";
            descripcion = "Zona con alto tráfico en horas pico matutinas y vespertinas\n" +
                    "debido a concentración de instituciones educativas y\n" +
                    "oficinas gubernamentales.";
        } else if (nombreHospital.contains("Carlos Andrade")) {
            zona = "ZONA CENTRO-OESTE";
            descripcion = "Zona céntrica con tráfico moderado-alto durante todo el día.\n" +
                    "Especialmente congestionada en hora de almuerzo.";
        } else if (nombreHospital.contains("Eugenio Espejo")) {
            zona = "ZONA CENTRO";
            descripcion = "Zona con mayor afluencia vehicular de Quito.\n" +
                    "Tráfico alto en todas las horas pico.";
        } else if (nombreHospital.contains("Baca Ortiz")) {
            zona = "ZONA CENTRO-SUR";
            descripcion = "Zona residencial con tráfico moderado.\n" +
                    "Afectación principalmente en horas pico vespertinas.";
        }

        sb.append(String.format("📍 Zona: %s\n\n", zona));
        sb.append(String.format("ℹ️  %s\n\n", descripcion));

        sb.append("⏰ IMPACTO POR FRANJA HORARIA:\n\n");

        sb.append("  🌅 07:00-09:00 (Matutina):\n");
        if (nombreHospital.contains("Pablo") || nombreHospital.contains("Policía") ||
                nombreHospital.contains("Eugenio")) {
            sb.append("     🔴 ALTO - Factor 1.5x (+50%)\n");
        } else {
            sb.append("     🟡 MODERADO - Factor 1.3x (+30%)\n");
        }
        sb.append("\n");

        sb.append("  ☀️ 12:00-14:00 (Mediodía):\n");
        if (nombreHospital.contains("Carlos") || nombreHospital.contains("Eugenio")) {
            sb.append("     🟡 MODERADO-ALTO - Factor 1.4x (+40%)\n");
        } else {
            sb.append("     🟡 MODERADO - Factor 1.3x (+30%)\n");
        }
        sb.append("\n");

        sb.append("  🌆 17:00-19:00 (Vespertina):\n");
        sb.append("     🔴 MUY ALTO - Factor 1.6x (+60%)\n");
        sb.append("     ⚠️  LA HORA MÁS CRÍTICA EN TODAS LAS ZONAS\n\n");

        sb.append("═══════════════════════════════════════════════════════════════\n");

        return sb.toString();
    }

    public static List<String> listarHorasPico() {
        List<String> horas = new ArrayList<>();
        horas.add("🌅 07:00-09:00 | Matutina | 🔴 ALTO (+50%)");
        horas.add("☀️ 12:00-14:00 | Mediodía | 🟡 MODERADO (+30%)");
        horas.add("🌆 17:00-19:00 | Vespertina | 🔴 MUY ALTO (+60%)");
        return horas;
    }

}
