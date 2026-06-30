package ec.edu.uce.util;

import ec.edu.uce.controlador.GestorHorasPico;
import ec.edu.uce.modelo.*;

import java.util.*;

public class Dijkstra {

    // =========================================================================
    // CLASE INTERNA: ResultadoRuta
    // =========================================================================

    public static class ResultadoRuta {
        private List<Nodo> ruta;
        private double distanciaTotal;

        public ResultadoRuta(List<Nodo> ruta, double distanciaTotal) {
            this.ruta = ruta;
            this.distanciaTotal = distanciaTotal;
        }

        public List<Nodo> getRuta() {
            return ruta;
        }

        public double getDistanciaTotal() {
            return distanciaTotal;
        }

        public boolean existeCamino() {
            return ruta != null && !ruta.isEmpty();
        }

        @Override
        public String toString() {
            if (!existeCamino()) {
                return "No existe camino";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Distancia: ").append(String.format("%.2f min", distanciaTotal));
            sb.append("\nRuta: ");
            for (int i = 0; i < ruta.size(); i++) {
                sb.append(ruta.get(i).getNombre());
                if (i < ruta.size() - 1) {
                    sb.append(" → ");
                }
            }
            return sb.toString();
        }
    }

    // =========================================================================
    // CLASE INTERNA: NodoDistancia (para PriorityQueue)
    // =========================================================================

    private static class NodoDistancia implements Comparable<NodoDistancia> {
        Nodo nodo;
        double distancia;

        public NodoDistancia(Nodo nodo, double distancia) {
            this.nodo = nodo;
            this.distancia = distancia;
        }

        @Override
        public int compareTo(NodoDistancia otro) {
            return Double.compare(this.distancia, otro.distancia);
        }
    }

    // =========================================================================
    // ⚠️ NUEVA CLASE: ResultadoHospitalAlternativo
    // =========================================================================

    /**
     * Almacena la información de una ruta desde la emergencia hacia un hospital.
     */
    public static class ResultadoHospitalAlternativo {
        private Nodo hospital;
        private List<Nodo> ruta;
        private double tiempo;
        private int posicion; // 1°, 2°, 3°, 4°

        public ResultadoHospitalAlternativo(Nodo hospital, List<Nodo> ruta, double tiempo, int posicion) {
            this.hospital = hospital;
            this.ruta = ruta;
            this.tiempo = tiempo;
            this.posicion = posicion;
        }

        public Nodo getHospital() {
            return hospital;
        }

        public List<Nodo> getRuta() {
            return ruta;
        }

        public double getTiempo() {
            return tiempo;
        }

        public int getPosicion() {
            return posicion;
        }

        public String getMedalla() {
            switch (posicion) {
                case 1: return "🥇";
                case 2: return "🥈";
                case 3: return "🥉";
                case 4: return "🏅";
                default: return "⭐";
            }
        }
    }

    // =========================================================================
    // MÉTODO PRINCIPAL: DIJKSTRA CON FACTOR DE TRÁFICO
    // =========================================================================

    public static ResultadoRuta calcularRutaMinima(Grafo grafo, Nodo origen, Nodo destino) {
        // Validaciones
        if (grafo == null || origen == null || destino == null) {
            throw new IllegalArgumentException("Grafo y nodos no pueden ser null");
        }

        // Si origen y destino son el mismo
        if (origen.equals(destino)) {
            List<Nodo> ruta = new ArrayList<>();
            ruta.add(origen);
            return new ResultadoRuta(ruta, 0.0);
        }

        // OBTENER FACTOR DE TRÁFICO ACTUAL
        double factorTrafico = GestorHorasPico.obtenerFactorTrafico();

        // FASE 1: INICIALIZACIÓN
        Map<Nodo, Double> distancias = new HashMap<>();
        for (Nodo nodo : grafo.obtenerTodosLosNodos()) {
            distancias.put(nodo, Double.POSITIVE_INFINITY);
        }
        distancias.put(origen, 0.0);

        Map<Nodo, Nodo> predecesores = new HashMap<>();
        PriorityQueue<NodoDistancia> colaPrioridad = new PriorityQueue<>();
        colaPrioridad.offer(new NodoDistancia(origen, 0.0));
        Set<Nodo> visitados = new HashSet<>();

        // FASE 2: ALGORITMO PRINCIPAL DE DIJKSTRA
        while (!colaPrioridad.isEmpty()) {
            NodoDistancia actual = colaPrioridad.poll();
            Nodo nodoActual = actual.nodo;
            double distanciaActual = actual.distancia;

            if (visitados.contains(nodoActual)) {
                continue;
            }

            visitados.add(nodoActual);

            if (nodoActual.equals(destino)) {
                break;
            }

            // RELAJACIÓN DE ARISTAS CON FACTOR DE TRÁFICO
            for (Grafo.ParNodoPeso par : grafo.obtenerVecinos(nodoActual)) {
                Nodo vecino = par.getNodo();
                double peso = par.getPeso();

                // APLICAR FACTOR DE TRÁFICO
                double pesoConTrafico = peso * factorTrafico;
                double distanciaTentativa = distanciaActual + pesoConTrafico;

                if (distanciaTentativa < distancias.get(vecino)) {
                    distancias.put(vecino, distanciaTentativa);
                    predecesores.put(vecino, nodoActual);
                    colaPrioridad.offer(new NodoDistancia(vecino, distanciaTentativa));
                }
            }
        }

        // FASE 3: RECONSTRUCCIÓN DEL CAMINO ÓPTIMO
        if (distancias.get(destino).equals(Double.POSITIVE_INFINITY)) {
            return new ResultadoRuta(null, Double.POSITIVE_INFINITY);
        }

        List<Nodo> ruta = new ArrayList<>();
        Nodo nodo = destino;

        while (nodo != null) {
            ruta.add(nodo);
            nodo = predecesores.get(nodo);
        }

        Collections.reverse(ruta);

        return new ResultadoRuta(ruta, distancias.get(destino));
    }

    // =========================================================================
    // ⚠️ NUEVO MÉTODO: CALCULAR RUTAS A HOSPITALES MÁS CERCANOS
    // =========================================================================

    /**
     * Calcula las rutas desde el punto de emergencia hacia los 4 hospitales
     * más cercanos. Útil para horas pico cuando se necesitan alternativas.
     *
     * @param grafo Red vial
     * @param nodoEmergencia Punto de emergencia
     * @param todosLosHospitales Lista de todos los hospitales disponibles
     * @return Lista con los 4 hospitales más cercanos y sus rutas
     */
    public static List<ResultadoHospitalAlternativo> calcularRutasAHospitalesAlternativos(
            Grafo grafo,
            Nodo nodoEmergencia,
            List<Nodo> todosLosHospitales) {

        if (todosLosHospitales == null || todosLosHospitales.isEmpty()) {
            return new ArrayList<>();
        }

        List<ResultadoHospitalAlternativo> resultados = new ArrayList<>();

        // Calcular ruta desde emergencia hacia cada hospital
        for (Nodo hospital : todosLosHospitales) {
            ResultadoRuta resultado = calcularRutaMinima(grafo, nodoEmergencia, hospital);

            if (resultado.existeCamino()) {
                resultados.add(new ResultadoHospitalAlternativo(
                        hospital,
                        resultado.getRuta(),
                        resultado.getDistanciaTotal(),
                        0 // Se asignará después del ordenamiento
                ));
            }
        }

        // Ordenar por tiempo (más cercano primero)
        resultados.sort(Comparator.comparingDouble(ResultadoHospitalAlternativo::getTiempo));

        // Asignar posiciones
        for (int i = 0; i < resultados.size() && i < 4; i++) {
            resultados.get(i).posicion = i + 1;
        }

        // Retornar solo los 4 más cercanos
        return resultados.subList(0, Math.min(4, resultados.size()));
    }

    // =========================================================================
    // MÉTODO PARA ENCONTRAR AMBULANCIA ÓPTIMA CON ALTERNATIVA
    // =========================================================================

    public static ResultadoOptimizacion encontrarAmbulanciaOptima(
            Grafo grafo,
            List<Ambulancia> ambulancias,
            Nodo nodoEmergencia,
            boolean usarSegundaMejor) {

        if (ambulancias == null || ambulancias.isEmpty()) {
            throw new IllegalArgumentException("Debe haber al menos una ambulancia");
        }

        List<ResultadoAmbulancia> resultados = new ArrayList<>();

        // Calcular ruta desde cada ambulancia
        for (Ambulancia ambulancia : ambulancias) {
            ResultadoRuta resultado = calcularRutaMinima(
                    grafo,
                    ambulancia.getNodoActual(),
                    nodoEmergencia
            );

            resultados.add(new ResultadoAmbulancia(
                    ambulancia,
                    resultado.getRuta(),
                    resultado.getDistanciaTotal()
            ));
        }

        // Ordenar por tiempo (mejor a peor)
        resultados.sort(Comparator.comparingDouble(ResultadoAmbulancia::getTiempo));

        // Seleccionar primera o segunda mejor
        ResultadoAmbulancia mejor;
        if (usarSegundaMejor && resultados.size() > 1) {
            mejor = resultados.get(1); // Segunda mejor opción
        } else {
            mejor = resultados.get(0); // Primera mejor opción
        }

        return new ResultadoOptimizacion(mejor, resultados);
    }

    public static ResultadoOptimizacion encontrarAmbulanciaOptima(
            Grafo grafo,
            List<Ambulancia> ambulancias,
            Nodo nodoEmergencia) {
        return encontrarAmbulanciaOptima(grafo, ambulancias, nodoEmergencia, false);
    }

    // =========================================================================
    // CLASES AUXILIARES PARA RESULTADOS
    // =========================================================================

    public static class ResultadoAmbulancia {
        private Ambulancia ambulancia;
        private List<Nodo> ruta;
        private double tiempo;

        public ResultadoAmbulancia(Ambulancia ambulancia, List<Nodo> ruta, double tiempo) {
            this.ambulancia = ambulancia;
            this.ruta = ruta;
            this.tiempo = tiempo;
        }

        public Ambulancia getAmbulancia() {
            return ambulancia;
        }

        public List<Nodo> getRuta() {
            return ruta;
        }

        public double getTiempo() {
            return tiempo;
        }
    }

    public static class ResultadoOptimizacion {
        private ResultadoAmbulancia mejorResultado;
        private List<ResultadoAmbulancia> todosLosResultados;

        public ResultadoOptimizacion(ResultadoAmbulancia mejor, List<ResultadoAmbulancia> todos) {
            this.mejorResultado = mejor;
            this.todosLosResultados = todos;
        }

        public ResultadoAmbulancia getMejorResultado() {
            return mejorResultado;
        }

        public List<ResultadoAmbulancia> getTodosLosResultados() {
            return todosLosResultados;
        }

        public Ambulancia getAmbulanciaOptima() {
            return mejorResultado.getAmbulancia();
        }

        public List<Nodo> getRutaOptima() {
            return mejorResultado.getRuta();
        }

        public double getTiempoOptimo() {
            return mejorResultado.getTiempo();
        }
    }
}
