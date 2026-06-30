package ec.edu.uce.modelo;

public class Ambulancia {

    // =========================================================================
    // ATRIBUTOS
    // =========================================================================

    /**
     * Identificador único de la ambulancia.
     * Formato sugerido: "AMB-XX" donde XX es un número secuencial
     */
    private String identificador;

    /**
     * Nodo donde se encuentra actualmente la ambulancia.
     * En el escenario inicial, coincide con el hospital
     */
    private Nodo nodoActual;

    /**
     * Hospital al que pertenece la ambulancia.
     * Representa el nodo de origen para el algoritmo de Dijkstra
     */
    private Nodo hospital;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    /**
     * Constructor de una ambulancia.
     *
     * @param identificador Código único de la ambulancia
     * @param nodoActual Posición actual
     * @param hospital Hospital de origen
     * @throws IllegalArgumentException si algún parámetro es null
     */
    public Ambulancia(String identificador, Nodo nodoActual, Nodo hospital) {
        if (identificador == null || identificador.trim().isEmpty()) {
            throw new IllegalArgumentException("El identificador no puede ser vacío");
        }
        if (nodoActual == null) {
            throw new IllegalArgumentException("El nodo actual no puede ser null");
        }
        if (hospital == null) {
            throw new IllegalArgumentException("El hospital no puede ser null");
        }

        this.identificador = identificador;
        this.nodoActual = nodoActual;
        this.hospital = hospital;
    }

    // =========================================================================
    // GETTERS Y SETTERS
    // =========================================================================

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public Nodo getNodoActual() {
        return nodoActual;
    }

    public void setNodoActual(Nodo nodoActual) {
        this.nodoActual = nodoActual;
    }

    public Nodo getHospital() {
        return hospital;
    }

    public void setHospital(Nodo hospital) {
        this.hospital = hospital;
    }

    // =========================================================================
    // MÉTODOS AUXILIARES
    // =========================================================================

    /**
     * Verifica si la ambulancia está en su hospital de origen.
     *
     * @return true si está en el hospital
     */
    public boolean estaEnHospital() {
        return nodoActual.equals(hospital);
    }

    /**
     * Mueve la ambulancia a un nuevo nodo.
     * En una simulación real, esto representaría el desplazamiento.
     *
     * @param nuevoNodo Nodo destino
     */
    public void moverA(Nodo nuevoNodo) {
        if (nuevoNodo == null) {
            throw new IllegalArgumentException("El nodo destino no puede ser null");
        }
        this.nodoActual = nuevoNodo;
    }

    /**
     * Retorna la ambulancia a su hospital de origen.
     */
    public void regresarAlHospital() {
        this.nodoActual = hospital;
    }

    // =========================================================================
    // MÉTODOS SOBRESCRITOS
    // =========================================================================

    /**
     * Representación en String de la ambulancia.
     *
     * @return Descripción legible
     */
    @Override
    public String toString() {
        return String.format("Ambulancia %s en %s (Hospital: %s)",
                identificador,
                nodoActual.getNombre(),
                hospital.getNombre()
        );
    }

    /**
     * Representación compacta para UI.
     *
     * @return Identificador y posición
     */
    public String toStringCorto() {
        return String.format("%s @ %s", identificador, nodoActual.getNombre());
    }
}
