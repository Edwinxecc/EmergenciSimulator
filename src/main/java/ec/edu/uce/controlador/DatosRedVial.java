package ec.edu.uce.controlador;

import ec.edu.uce.modelo.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DatosRedVial {

    // =========================================================================
    // CONSTANTES DEL SISTEMA
    // =========================================================================

    /**
     * Velocidad promedio de ambulancia con sirena en Quito.
     * Valor real: 45-50 km/h en ciudad → 750 m/min
     */
    private static final double VELOCIDAD_AMBULANCIA_MMIN = 750.0;

    /**
     * Tiempo promedio de espera en semáforo con sirena (30 segundos).
     */
    private static final double TIEMPO_SEMAFORO = 0.5;

    /**
     * Calcula el tiempo de recorrido entre dos puntos.
     *
     * @param distanciaMetros Distancia en metros
     * @param numSemaforos Número de semáforos en el trayecto
     * @return Tiempo en minutos
     */
    private static double calcularTiempo(double distanciaMetros, int numSemaforos) {
        return (distanciaMetros / VELOCIDAD_AMBULANCIA_MMIN) + (numSemaforos * TIEMPO_SEMAFORO);
    }

    // =========================================================================
    // MÉTODO PRINCIPAL: CREACIÓN DEL GRAFO
    // =========================================================================

    /**
     * Crea el grafo con UBICACIONES GEOGRÁFICAS CORREGIDAS V4.1.
     *
     * CORRECCIONES V4.1:
     * - H. Carlos Andrade Marín: Movido más a la izquierda (X=450)
     * - H. Policía Nacional: Reubicado en Mariscal Sucre altura Mariana de Jesús (X=300, Y=450)
     * - Todas las conexiones actualizadas
     *
     * @return Grafo con la red vial de Quito
     */
    public static Grafo crearRedVialUCE() {
        Grafo grafo = new Grafo();

        // =====================================================================
        // HOSPITALES (5) - Ubicaciones CORREGIDAS V4.1
        // =====================================================================

        // H. Carlos Andrade Marín (IESS)
        // Ubicación real: Av. 18 de Septiembre y Av. Universitaria (Centro-Oeste)
        // ✅ CORREGIDO: Movido MÁS A LA IZQUIERDA
        Nodo hCarlosAndrade = new Nodo("H. Carlos Andrade Marín", 450, 650, true);

        // H. Eugenio Espejo (MSP)
        // Ubicación real: Centro Histórico, Av. Gran Colombia
        // Coordenadas: Sur del centro, cerca de Av. 10 de Agosto
        Nodo hEugenioEspejo = new Nodo("H. Eugenio Espejo", 720, 1100, true);

        // H. Baca Ortiz (MSP)
        // Ubicación real: Av. 6 de Diciembre y Villacreces (Norte)
        // ✅ CONFIRMADO: Sobre Av. 6 de Diciembre, zona norte
        Nodo hBacaOrtiz = new Nodo("H. Baca Ortiz", 900, 400, true);

        // H. Pablo Arturo Suárez (IESS)
        // Ubicación real: Av. Occidental y Mariana de Jesús (Norte-Oeste)
        // Coordenadas: Zona norte-oeste, cerca Av. América
        Nodo hPabloArturo = new Nodo("H. Pablo Arturo Suárez", 400, 350, true);

        // H. Policía Nacional
        // ✅ CORREGIDO: Mariscal Sucre a la altura de Mariana de Jesús (Norte-Oeste)
        // Sobre la Av. Mariscal Sucre, zona norte
        Nodo hPolicia = new Nodo("H. Policía Nacional", 300, 450, true);

        // =====================================================================
        // AVENIDAS PRINCIPALES NORTE-SUR (Correctamente alineadas)
        // =====================================================================

        // ZONA NORTE (Y ≈ 200-500)
        // -----------------------------------------------------------------

        // Av. Mariscal Sucre - NORTE (Occidente)
        Nodo mariscalSucreNorte = new Nodo("Mariscal Sucre Norte", 300, 350);

        // Av. América - NORTE
        Nodo americaNorte = new Nodo("Av. América Norte", 500, 300);

        // Av. 10 de Agosto - NORTE (cruce con Av. Mariana de Jesús)
        Nodo agosto10Norte = new Nodo("10 de Agosto Norte", 700, 400);

        // Av. 6 de Diciembre - NORTE (La Carolina)
        Nodo diciembre6Norte = new Nodo("6 de Diciembre Norte", 900, 350);

        // Av. de los Shyris - NORTE
        Nodo shyrisNorte = new Nodo("Av. Shyris Norte", 1100, 380);

        // ZONA CENTRO-NORTE (Y ≈ 500-800)
        // -----------------------------------------------------------------

        // Av. Mariscal Sucre - CENTRO-NORTE
        Nodo mariscalSucreCentroNorte = new Nodo("Mariscal Sucre Centro-Norte", 300, 650);

        // Av. América - CENTRO (cruce con Av. Universitaria)
        Nodo americaCentro = new Nodo("Av. América Centro", 500, 600);

        // Av. 10 de Agosto - CENTRO (Parque El Ejido)
        Nodo agosto10Centro = new Nodo("10 de Agosto Centro", 700, 750);

        // Av. 6 de Diciembre - CENTRO (Universidad Central)
        Nodo diciembre6Centro = new Nodo("6 de Diciembre Centro", 900, 700);

        // Av. de los Shyris - CENTRO
        Nodo shyrisCentro = new Nodo("Av. Shyris Centro", 1100, 680);

        // ZONA CENTRO-SUR (Y ≈ 800-1100)
        // -----------------------------------------------------------------

        // Av. Mariscal Sucre - CENTRO-SUR
        Nodo mariscalSucreCentroSur = new Nodo("Mariscal Sucre Centro-Sur", 300, 950);

        // Av. América - CENTRO-SUR
        Nodo americaSur = new Nodo("Av. América Sur", 500, 900);

        // Av. 10 de Agosto - CENTRO-SUR
        Nodo agosto10CentroSur = new Nodo("10 de Agosto Centro-Sur", 700, 1000);

        // Av. 6 de Diciembre - CENTRO-SUR
        Nodo diciembre6Sur = new Nodo("6 de Diciembre Sur", 900, 950);

        // ZONA SUR (Y ≈ 1100-1400)
        // -----------------------------------------------------------------

        // Av. Mariscal Sucre - SUR (El Recreo)
        Nodo mariscalSucreSur = new Nodo("Mariscal Sucre Sur", 300, 1250);

        // Av. 10 de Agosto - SUR (Villaflora)
        Nodo agosto10Sur = new Nodo("10 de Agosto Sur", 700, 1250);

        // Av. Maldonado (conexión sur)
        Nodo maldonado = new Nodo("Av. Maldonado", 550, 1350);

        // =====================================================================
        // AVENIDAS TRANSVERSALES ESTE-OESTE
        // =====================================================================

        // NORTE:
        Nodo marianaJesus = new Nodo("Av. Mariana de Jesús", 650, 450);
        Nodo laCarolina = new Nodo("Parque La Carolina", 1000, 350);

        // CENTRO:
        Nodo avPatria = new Nodo("Av. Patria", 750, 600);
        Nodo avUniversitaria = new Nodo("Av. Universitaria", 550, 650);
        Nodo parqueEjido = new Nodo("Parque El Ejido", 800, 800);

        // SUR:
        Nodo avNacionesUnidas = new Nodo("Av. Naciones Unidas", 850, 1050);
        Nodo elRecreo = new Nodo("El Recreo", 400, 1300);

        // =====================================================================
        // AGREGAR TODOS LOS NODOS AL GRAFO
        // =====================================================================

        // Hospitales (5)
        grafo.agregarNodo(hCarlosAndrade);
        grafo.agregarNodo(hEugenioEspejo);
        grafo.agregarNodo(hBacaOrtiz);
        grafo.agregarNodo(hPabloArturo);
        grafo.agregarNodo(hPolicia);

        // Avenidas Norte-Sur (5 ejes principales × 4 segmentos)
        grafo.agregarNodo(mariscalSucreNorte);
        grafo.agregarNodo(mariscalSucreCentroNorte);
        grafo.agregarNodo(mariscalSucreCentroSur);
        grafo.agregarNodo(mariscalSucreSur);

        grafo.agregarNodo(americaNorte);
        grafo.agregarNodo(americaCentro);
        grafo.agregarNodo(americaSur);

        grafo.agregarNodo(agosto10Norte);
        grafo.agregarNodo(agosto10Centro);
        grafo.agregarNodo(agosto10CentroSur);
        grafo.agregarNodo(agosto10Sur);

        grafo.agregarNodo(diciembre6Norte);
        grafo.agregarNodo(diciembre6Centro);
        grafo.agregarNodo(diciembre6Sur);

        grafo.agregarNodo(shyrisNorte);
        grafo.agregarNodo(shyrisCentro);

        // Avenidas transversales
        grafo.agregarNodo(marianaJesus);
        grafo.agregarNodo(laCarolina);
        grafo.agregarNodo(avPatria);
        grafo.agregarNodo(avUniversitaria);
        grafo.agregarNodo(parqueEjido);
        grafo.agregarNodo(avNacionesUnidas);
        grafo.agregarNodo(maldonado);
        grafo.agregarNodo(elRecreo);

        // =====================================================================
        // CREAR ARISTAS - EJES NORTE-SUR (Verticales)
        // =====================================================================

        // AV. MARISCAL SUCRE (Occidente) - Eje completo Norte → Sur
        agregarAristaConCalculo(grafo, mariscalSucreNorte, mariscalSucreCentroNorte, 2200, 3);
        agregarAristaConCalculo(grafo, mariscalSucreCentroNorte, mariscalSucreCentroSur, 2400, 3);
        agregarAristaConCalculo(grafo, mariscalSucreCentroSur, mariscalSucreSur, 2200, 3);

        // AV. AMÉRICA - Eje completo Norte → Sur
        agregarAristaConCalculo(grafo, americaNorte, americaCentro, 2200, 3);
        agregarAristaConCalculo(grafo, americaCentro, americaSur, 2300, 3);

        // AV. 10 DE AGOSTO (Eje Central) - Eje completo Norte → Sur
        agregarAristaConCalculo(grafo, agosto10Norte, agosto10Centro, 2600, 4);
        agregarAristaConCalculo(grafo, agosto10Centro, agosto10CentroSur, 1900, 3);
        agregarAristaConCalculo(grafo, agosto10CentroSur, agosto10Sur, 1900, 2);

        // AV. 6 DE DICIEMBRE - Eje completo Norte → Sur
        agregarAristaConCalculo(grafo, diciembre6Norte, diciembre6Centro, 2600, 4);
        agregarAristaConCalculo(grafo, diciembre6Centro, diciembre6Sur, 1900, 3);

        // AV. DE LOS SHYRIS (Oriente) - Eje Norte → Centro
        agregarAristaConCalculo(grafo, shyrisNorte, shyrisCentro, 2200, 3);

        // =====================================================================
        // CREAR ARISTAS - CONEXIONES ESTE-OESTE (Horizontales)
        // =====================================================================

        // ZONA NORTE (conexiones transversales)
        agregarAristaConCalculo(grafo, mariscalSucreNorte, americaNorte, 1500, 2);
        agregarAristaConCalculo(grafo, americaNorte, marianaJesus, 1200, 2);
        agregarAristaConCalculo(grafo, marianaJesus, agosto10Norte, 900, 1);
        agregarAristaConCalculo(grafo, agosto10Norte, diciembre6Norte, 1500, 2);
        agregarAristaConCalculo(grafo, diciembre6Norte, laCarolina, 800, 1);
        agregarAristaConCalculo(grafo, laCarolina, shyrisNorte, 800, 1);

        // ZONA CENTRO-NORTE (Av. Patria)
        agregarAristaConCalculo(grafo, mariscalSucreCentroNorte, americaCentro, 1600, 2);
        agregarAristaConCalculo(grafo, americaCentro, avUniversitaria, 900, 1);
        agregarAristaConCalculo(grafo, avUniversitaria, avPatria, 1400, 2);
        agregarAristaConCalculo(grafo, avPatria, agosto10Centro, 900, 1);
        agregarAristaConCalculo(grafo, agosto10Centro, diciembre6Centro, 1500, 2);
        agregarAristaConCalculo(grafo, diciembre6Centro, shyrisCentro, 1500, 2);

        // ZONA CENTRO (Parque El Ejido)
        agregarAristaConCalculo(grafo, agosto10Centro, parqueEjido, 700, 1);
        agregarAristaConCalculo(grafo, parqueEjido, diciembre6Centro, 800, 1);

        // ZONA CENTRO-SUR
        agregarAristaConCalculo(grafo, mariscalSucreCentroSur, americaSur, 1600, 2);
        agregarAristaConCalculo(grafo, americaSur, agosto10CentroSur, 1500, 2);
        agregarAristaConCalculo(grafo, agosto10CentroSur, avNacionesUnidas, 1100, 2);
        agregarAristaConCalculo(grafo, avNacionesUnidas, diciembre6Sur, 900, 1);

        // ZONA SUR (El Recreo - Maldonado)
        agregarAristaConCalculo(grafo, mariscalSucreSur, elRecreo, 800, 1);
        agregarAristaConCalculo(grafo, elRecreo, maldonado, 1200, 2);
        agregarAristaConCalculo(grafo, maldonado, agosto10Sur, 1100, 2);

        // =====================================================================
        // CONEXIONES DE HOSPITALES A LA RED - ✅ CORREGIDAS V4.1
        // =====================================================================

        // H. CARLOS ANDRADE MARÍN (Centro-Oeste) - ✅ CORREGIDO
        // Más a la izquierda, cerca de Av. Universitaria y América
        agregarAristaConCalculo(grafo, hCarlosAndrade, avUniversitaria, 800, 1);
        agregarAristaConCalculo(grafo, hCarlosAndrade, americaCentro, 900, 1);
        agregarAristaConCalculo(grafo, hCarlosAndrade, mariscalSucreCentroNorte, 1100, 2);

        // H. EUGENIO ESPEJO (Centro-Sur)
        agregarAristaConCalculo(grafo, hEugenioEspejo, agosto10CentroSur, 1400, 2);
        agregarAristaConCalculo(grafo, hEugenioEspejo, agosto10Sur, 1200, 2);
        agregarAristaConCalculo(grafo, hEugenioEspejo, parqueEjido, 2200, 3);

        // H. BACA ORTIZ (Norte, sobre 6 de Diciembre) - ✅ CONFIRMADO
        agregarAristaConCalculo(grafo, hBacaOrtiz, diciembre6Norte, 600, 1);
        agregarAristaConCalculo(grafo, hBacaOrtiz, laCarolina, 700, 1);
        agregarAristaConCalculo(grafo, hBacaOrtiz, diciembre6Centro, 2200, 3);

        // H. PABLO ARTURO SUÁREZ (Norte-Oeste)
        agregarAristaConCalculo(grafo, hPabloArturo, mariscalSucreNorte, 700, 1);
        agregarAristaConCalculo(grafo, hPabloArturo, americaNorte, 900, 1);
        agregarAristaConCalculo(grafo, hPabloArturo, americaCentro, 2200, 3);

        // H. POLICÍA NACIONAL (Norte-Oeste, Mariscal Sucre altura Mariana de Jesús) - ✅ CORREGIDO
        // Sobre la Av. Mariscal Sucre, cerca de Mariana de Jesús
        agregarAristaConCalculo(grafo, hPolicia, mariscalSucreNorte, 800, 1);
        agregarAristaConCalculo(grafo, hPolicia, mariscalSucreCentroNorte, 1500, 2);
        agregarAristaConCalculo(grafo, hPolicia, marianaJesus, 2500, 3);
        agregarAristaConCalculo(grafo, hPolicia, americaNorte, 1800, 2);

        return grafo;
    }

    /**
     * Agrega una arista bidireccional con cálculo automático de tiempo.
     *
     * @param grafo Grafo al que agregar la arista
     * @param origen Nodo de origen
     * @param destino Nodo de destino
     * @param distanciaMetros Distancia en metros
     * @param numSemaforos Número de semáforos
     */
    private static void agregarAristaConCalculo(Grafo grafo, Nodo origen, Nodo destino,
                                                double distanciaMetros, int numSemaforos) {
        double tiempo = calcularTiempo(distanciaMetros, numSemaforos);
        grafo.agregarArista(origen, destino, tiempo);
    }

    // =========================================================================
    // MÉTODOS AUXILIARES
    // =========================================================================

    /**
     * Crea las ambulancias distribuidas en los hospitales.
     *
     * @param grafo Grafo con la red vial
     * @return Lista de ambulancias
     */
    public static List<Ambulancia> crearAmbulancia(Grafo grafo) {
        List<Ambulancia> ambulancias = new ArrayList<>();

        Nodo hCarlos = grafo.obtenerNodo("H. Carlos Andrade Marín");
        Nodo hEugenio = grafo.obtenerNodo("H. Eugenio Espejo");
        Nodo hBaca = grafo.obtenerNodo("H. Baca Ortiz");
        Nodo hPablo = grafo.obtenerNodo("H. Pablo Arturo Suárez");
        Nodo hPolicia = grafo.obtenerNodo("H. Policía Nacional");

        // Ambulancias IESS
        if (hCarlos != null) {
            ambulancias.add(new Ambulancia("AMB-03-IESS", hCarlos, hCarlos));
        }
        if (hPablo != null) {
            ambulancias.add(new Ambulancia("AMB-04-IESS", hPablo, hPablo));
        }

        // Ambulancias MSP
        if (hEugenio != null) {
            ambulancias.add(new Ambulancia("AMB-01-MSP", hEugenio, hEugenio));
        }
        if (hBaca != null) {
            ambulancias.add(new Ambulancia("AMB-02-MSP", hBaca, hBaca));
        }

        // Ambulancia BOMBEROS
        if (hPolicia != null) {
            ambulancias.add(new Ambulancia("AMB-06-BOMB", hPolicia, hPolicia));
        }

        return ambulancias;
    }

    /**
     * Obtiene todos los puntos que NO son hospitales.
     *
     * @param grafo Grafo con la red vial
     * @return Lista de nodos de emergencia potenciales
     */
    public static List<Nodo> obtenerPuntosEmergencia(Grafo grafo) {
        return grafo.obtenerTodosLosNodos()
                .stream()
                .filter(nodo -> !nodo.isEsHospital())
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los hospitales del sistema.
     *
     * @param grafo Grafo con la red vial
     * @return Lista de hospitales
     */
    public static List<Nodo> obtenerHospitales(Grafo grafo) {
        return grafo.obtenerTodosLosNodos()
                .stream()
                .filter(Nodo::isEsHospital)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // MÉTODO MAIN - VERIFICACIÓN DEL SISTEMA
    // =========================================================================

    /**
     * Método main para verificar la correcta creación del grafo.
     */
    public static void main(String[] args) {
        System.out.println("╔" + "═".repeat(78) + "╗");
        System.out.println("║" + centrar("SISTEMA ECU 911 - VERSIÓN 4.1 CORREGIDA", 78) + "║");
        System.out.println("║" + centrar("Red Vial de Quito con Ubicaciones Geográficas Reales", 78) + "║");
        System.out.println("╚" + "═".repeat(78) + "╝");
        System.out.println();

        Grafo grafo = crearRedVialUCE();

        System.out.println("📊 ESTADÍSTICAS DEL GRAFO:");
        System.out.println("   • Nodos totales: " + grafo.obtenerNumeroNodos());
        System.out.println("   • Aristas totales: " + grafo.obtenerNumeroAristas());
        System.out.println("   • Hospitales: " + obtenerHospitales(grafo).size());
        System.out.println("   • Puntos de emergencia: " + obtenerPuntosEmergencia(grafo).size());
        System.out.println();

        System.out.println("🏥 HOSPITALES DISPONIBLES (UBICACIONES CORREGIDAS V4.1):");
        for (Nodo hospital : obtenerHospitales(grafo)) {
            System.out.println("   • " + hospital.getNombre() +
                    " (X: " + hospital.getX() + ", Y: " + hospital.getY() + ")");
        }
        System.out.println();

        System.out.println("✅ CORRECCIONES V4.1 APLICADAS:");
        System.out.println("   • H. Carlos Andrade Marín → Movido más a la izquierda (X=450)");
        System.out.println("   • H. Policía Nacional → Reubicado en Mariscal Sucre altura Mariana de Jesús");
        System.out.println("   • H. Baca Ortiz → Confirmado en 6 de Diciembre (correcto)");
        System.out.println("   • Todas las conexiones actualizadas según nuevas ubicaciones");
        System.out.println();

        System.out.println("🛣️  AVENIDAS PRINCIPALES NORTE-SUR:");
        System.out.println("   1. Av. Mariscal Sucre (X = 300) - OCCIDENTE");
        System.out.println("   2. Av. América (X = 500)");
        System.out.println("   3. Av. 10 de Agosto (X = 700) - EJE CENTRAL");
        System.out.println("   4. Av. 6 de Diciembre (X = 900)");
        System.out.println("   5. Av. de los Shyris (X = 1100) - ORIENTE");
        System.out.println();

        System.out.println("═".repeat(80));
        System.out.println("Sistema listo para su uso. ✅");
        System.out.println("═".repeat(80));
    }

    /**
     * Método auxiliar para centrar texto.
     */
    private static String centrar(String texto, int ancho) {
        int espacios = (ancho - texto.length()) / 2;
        return " ".repeat(Math.max(0, espacios)) + texto +
                " ".repeat(Math.max(0, ancho - texto.length() - espacios));
    }
}
