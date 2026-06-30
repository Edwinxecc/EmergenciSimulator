package ec.edu.uce.modelo;

public class Arista {

    // =========================================================================
    // ATRIBUTOS
    // =========================================================================

    /**
     * Nodo origen de la arista.
     * En teoría: u en e = (u, v, w)
     */
    private Nodo origen;

    /**
     * Nodo destino de la arista.
     * En teoría: v en e = (u, v, w)
     */
    private Nodo destino;

    /**
     * Peso de la arista (tiempo de recorrido en minutos).
     * En teoría: w en e = (u, v, w)
     *
     * RESTRICCIÓN: w > 0 (el algoritmo de Dijkstra requiere pesos no negativos)
     */
    private double peso;

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    /**
     * Constructor de una arista ponderada.
     *
     * @param origen Nodo de inicio
     * @param destino Nodo final
     * @param peso Peso de la arista (tiempo en minutos)
     * @throws IllegalArgumentException si el peso es negativo
     */
    public Arista(Nodo origen, Nodo destino, double peso) {
        if (peso < 0) {
            throw new IllegalArgumentException(
                    "El peso de una arista no puede ser negativo. " +
                            "El algoritmo de Dijkstra requiere pesos no negativos."
            );
        }

        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
    }

    // =========================================================================
    // GETTERS Y SETTERS
    // =========================================================================

    public Nodo getOrigen() {
        return origen;
    }

    public void setOrigen(Nodo origen) {
        this.origen = origen;
    }

    public Nodo getDestino() {
        return destino;
    }

    public void setDestino(Nodo destino) {
        this.destino = destino;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        if (peso < 0) {
            throw new IllegalArgumentException("El peso no puede ser negativo");
        }
        this.peso = peso;
    }

    // =========================================================================
    // MÉTODOS AUXILIARES
    // =========================================================================

    /**
     * Verifica si esta arista conecta dos nodos específicos.
     * Útil para determinar si una arista pertenece a una ruta.
     *
     * @param nodo1 Primer nodo
     * @param nodo2 Segundo nodo
     * @return true si la arista conecta estos nodos (en cualquier dirección)
     */
    public boolean conecta(Nodo nodo1, Nodo nodo2) {
        return (origen.equals(nodo1) && destino.equals(nodo2)) ||
                (origen.equals(nodo2) && destino.equals(nodo1));
    }

    /**
     * Obtiene el nodo opuesto dado uno de los nodos.
     * Útil para traversar el grafo.
     *
     * @param nodo Nodo conocido
     * @return El otro nodo de la arista, o null si el nodo no pertenece
     */
    public Nodo obtenerOpuesto(Nodo nodo) {
        if (origen.equals(nodo)) {
            return destino;
        } else if (destino.equals(nodo)) {
            return origen;
        }
        return null;
    }

    // =========================================================================
    // MÉTODOS SOBRESCRITOS
    // =========================================================================

    /**
     * Representación en String de la arista.
     * Formato: "Origen → Destino [peso min]"
     *
     * @return Descripción legible de la arista
     */
    @Override
    public String toString() {
        return String.format("%s → %s [%.1f min]",
                origen.getNombre(),
                destino.getNombre(),
                peso
        );
    }
}