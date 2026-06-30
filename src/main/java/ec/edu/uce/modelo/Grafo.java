package ec.edu.uce.modelo;

import java.util.*;

public class Grafo {

    // =========================================================================
    // ATRIBUTOS
    // =========================================================================

    /**
     * Mapa de nodos: nombre → Nodo
     * Permite acceso rápido a nodos por su identificador
     * Complejidad: O(1) para búsqueda
     */
    private Map<String, Nodo> nodos;

    /**
     * Lista de todas las aristas del grafo
     * Útil para visualización y análisis completo
     */
    private List<Arista> aristas;

    /**
     * Lista de adyacencia: Nodo → Lista de (vecino, peso)
     * Estructura fundamental para algoritmos de grafos
     *
     * TEORÍA: Para cada vértice v, almacena todos los vértices u
     * tales que existe una arista (v, u) con su peso correspondiente
     */
    private Map<Nodo, List<ParNodoPeso>> listaAdyacencia;

    // =========================================================================
    // CLASE INTERNA: ParNodoPeso
    // =========================================================================

    /**
     * Clase auxiliar que representa un par (nodo, peso).
     * Usado en la lista de adyacencia para almacenar vecinos con sus pesos.
     */
    public static class ParNodoPeso {
        private Nodo nodo;
        private double peso;

        public ParNodoPeso(Nodo nodo, double peso) {
            this.nodo = nodo;
            this.peso = peso;
        }

        public Nodo getNodo() {
            return nodo;
        }

        public double getPeso() {
            return peso;
        }

        @Override
        public String toString() {
            return String.format("(%s, %.1f)", nodo.getNombre(), peso);
        }
    }

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    /**
     * Constructor del grafo vacío.
     * Inicializa las estructuras de datos.
     */
    public Grafo() {
        this.nodos = new HashMap<>();
        this.aristas = new ArrayList<>();
        this.listaAdyacencia = new HashMap<>();
    }

    // =========================================================================
    // MÉTODOS PRINCIPALES
    // =========================================================================

    /**
     * Agrega un nodo al grafo.
     *
     * TEORÍA: V = V ∪ {v}
     *
     * @param nodo Nodo a agregar
     * @return true si se agregó exitosamente, false si ya existía
     *
     * Complejidad: O(1)
     */
    public boolean agregarNodo(Nodo nodo) {
        if (nodo == null) {
            throw new IllegalArgumentException("El nodo no puede ser null");
        }

        if (nodos.containsKey(nodo.getNombre())) {
            return false; // Nodo ya existe
        }

        nodos.put(nodo.getNombre(), nodo);
        listaAdyacencia.put(nodo, new ArrayList<>());
        return true;
    }

    /**
     * Agrega una arista bidireccional al grafo (grafo no dirigido).
     *
     * TEORÍA: En un grafo no dirigido, cada arista (u,v) permite
     * movimiento en ambas direcciones: u → v y v → u
     *
     * @param origen Nodo de origen
     * @param destino Nodo de destino
     * @param peso Peso de la arista (tiempo en minutos)
     * @return true si se agregó exitosamente
     *
     * Complejidad: O(1)
     */
    public boolean agregarArista(Nodo origen, Nodo destino, double peso) {
        if (origen == null || destino == null) {
            throw new IllegalArgumentException("Los nodos no pueden ser null");
        }

        if (!nodos.containsKey(origen.getNombre()) ||
                !nodos.containsKey(destino.getNombre())) {
            throw new IllegalArgumentException(
                    "Ambos nodos deben existir en el grafo antes de agregar la arista"
            );
        }

        // Crear y almacenar la arista
        Arista arista = new Arista(origen, destino, peso);
        aristas.add(arista);

        // Agregar a lista de adyacencia (bidireccional)
        listaAdyacencia.get(origen).add(new ParNodoPeso(destino, peso));
        listaAdyacencia.get(destino).add(new ParNodoPeso(origen, peso));

        return true;
    }

    /**
     * Obtiene un nodo por su nombre.
     *
     * @param nombre Nombre del nodo
     * @return Nodo correspondiente, o null si no existe
     *
     * Complejidad: O(1)
     */
    public Nodo obtenerNodo(String nombre) {
        return nodos.get(nombre);
    }

    /**
     * Obtiene los vecinos de un nodo con sus pesos.
     *
     * TEORÍA: Para un vértice v, retorna N(v) = {u : (v,u) ∈ E}
     * junto con los pesos w(v,u)
     *
     * @param nodo Nodo del cual obtener vecinos
     * @return Lista de pares (vecino, peso)
     *
     * Complejidad: O(1) para acceder, O(k) para iterar donde k = grado del nodo
     */
    public List<ParNodoPeso> obtenerVecinos(Nodo nodo) {
        return listaAdyacencia.getOrDefault(nodo, new ArrayList<>());
    }

    /**
     * Obtiene todos los nodos del grafo.
     *
     * @return Colección de todos los nodos
     *
     * Complejidad: O(1)
     */
    public Collection<Nodo> obtenerTodosLosNodos() {
        return nodos.values();
    }

    /**
     * Obtiene todas las aristas del grafo.
     *
     * @return Lista de todas las aristas
     */
    public List<Arista> obtenerTodasLasAristas() {
        return new ArrayList<>(aristas);
    }

    /**
     * Obtiene el número de nodos (orden del grafo).
     *
     * TEORÍA: |V|
     *
     * @return Cantidad de nodos
     */
    public int obtenerNumeroNodos() {
        return nodos.size();
    }

    /**
     * Obtiene el número de aristas (tamaño del grafo).
     *
     * TEORÍA: |E|
     *
     * @return Cantidad de aristas
     */
    public int obtenerNumeroAristas() {
        return aristas.size();
    }

    /**
     * Calcula el grado de un nodo.
     *
     * TEORÍA: deg(v) = |{u : (v,u) ∈ E}|
     *
     * @param nodo Nodo del cual calcular el grado
     * @return Grado del nodo (número de vecinos)
     */
    public int obtenerGrado(Nodo nodo) {
        return listaAdyacencia.getOrDefault(nodo, new ArrayList<>()).size();
    }

    /**
     * Verifica si existe una arista entre dos nodos.
     *
     * @param nodo1 Primer nodo
     * @param nodo2 Segundo nodo
     * @return true si existe la arista
     *
     * Complejidad: O(k) donde k = grado del nodo1
     */
    public boolean existeArista(Nodo nodo1, Nodo nodo2) {
        List<ParNodoPeso> vecinos = obtenerVecinos(nodo1);
        for (ParNodoPeso par : vecinos) {
            if (par.getNodo().equals(nodo2)) {
                return true;
            }
        }
        return false;
    }

    // =========================================================================
    // MÉTODOS DE UTILIDAD
    // =========================================================================

    /**
     * Representación en String del grafo.
     * Muestra información general y lista de aristas.
     *
     * @return Descripción del grafo
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════╗\n");
        sb.append("║       GRAFO DE RED VIAL - UCE          ║\n");
        sb.append("╚════════════════════════════════════════╝\n");
        sb.append(String.format("Nodos (|V|): %d\n", obtenerNumeroNodos()));
        sb.append(String.format("Aristas (|E|): %d\n", obtenerNumeroAristas()));
        sb.append("\nARISTAS:\n");

        for (Arista arista : aristas) {
            sb.append("  • ").append(arista.toString()).append("\n");
        }

        return sb.toString();
    }
}