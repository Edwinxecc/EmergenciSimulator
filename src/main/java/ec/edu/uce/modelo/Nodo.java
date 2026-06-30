package ec.edu.uce.modelo;

import java.util.Objects;
public class Nodo {

    // =========================================================================
    // ATRIBUTOS
    // =========================================================================

    /**
     * Identificador único del nodo.
     * En teoría de grafos: v ∈ V donde V es el conjunto de vértices
     */
    private String nombre;

    /**
     * Coordenada X para representación visual en el plano cartesiano
     */
    private double x;

    /**
     * Coordenada Y para representación visual en el plano cartesiano
     */
    private double y;

    /**
     * Indica si el nodo representa un hospital (nodo especial)
     * Útil para diferenciación visual y lógica de negocio
     */
    private boolean esHospital;

    // =========================================================================
    // CONSTRUCTORES
    // =========================================================================

    /**
     * Constructor principal de un Nodo.
     *
     * @param nombre Identificador único del nodo
     * @param x Coordenada X
     * @param y Coordenada Y
     * @param esHospital true si es un hospital, false si es intersección común
     */
    public Nodo(String nombre, double x, double y, boolean esHospital) {
        this.nombre = nombre;
        this.x = x;
        this.y = y;
        this.esHospital = esHospital;
    }

    /**
     * Constructor alternativo para nodos comunes (no hospitales)
     *
     * @param nombre Identificador único
     * @param x Coordenada X
     * @param y Coordenada Y
     */
    public Nodo(String nombre, double x, double y) {
        this(nombre, x, y, false);
    }

    // =========================================================================
    // GETTERS Y SETTERS
    // =========================================================================

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isEsHospital() {
        return esHospital;
    }

    public void setEsHospital(boolean esHospital) {
        this.esHospital = esHospital;
    }

    // =========================================================================
    // MÉTODOS SOBRESCRITOS DE OBJECT
    // =========================================================================

    /**
     * Dos nodos son iguales si tienen el mismo nombre.
     *
     * TEORÍA: En un grafo, los vértices se identifican únicamente.
     * No pueden existir dos vértices con el mismo identificador.
     *
     * @param obj Objeto a comparar
     * @return true si son el mismo nodo
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Nodo nodo = (Nodo) obj;
        return Objects.equals(nombre, nodo.nombre);
    }

    /**
     * Código hash basado en el nombre.
     * Necesario para usar Nodos como claves en HashMap.
     *
     * IMPORTANTE: Si equals() usa nombre, hashCode() también debe usarlo.
     *
     * @return Código hash del nodo
     */
    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }

    /**
     * Representación en String del nodo.
     *
     * @return Descripción legible del nodo
     */
    @Override
    public String toString() {
        String tipo = esHospital ? "Hospital" : "Intersección";
        return String.format("%s: %s (%.1f, %.1f)", tipo, nombre, x, y);
    }

    /**
     * Representación compacta para debugging.
     *
     * @return Nombre del nodo
     */
    public String toStringCorto() {
        return nombre;
    }
}
