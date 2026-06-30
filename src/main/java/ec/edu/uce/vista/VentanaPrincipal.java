package ec.edu.uce.vista;
import ec.edu.uce.controlador.*;
import ec.edu.uce.modelo.*;
import ec.edu.uce.util.Dijkstra;


import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;


public class VentanaPrincipal extends JFrame {

    // =========================================================================
    // COMPONENTES DE LA INTERFAZ
    // =========================================================================

    private PanelMapa panelMapa;
    private JComboBox<String> comboEmergencia;
    private JTextArea textoResultados;
    private JLabel labelReloj;
    private JLabel labelHoraPico;
    private JButton btnMostrarRutasAlternas; // ⚠️ NUEVO

    // =========================================================================
    // DATOS DEL SISTEMA
    // =========================================================================

    private Grafo grafo;
    private List<Ambulancia> ambulancias;
    private Nodo nodoEmergenciaActual;
    private Dijkstra.ResultadoOptimizacion resultadoActual;

    // ⚠️ NUEVO: Variables para rutas alternas
    private List<Dijkstra.ResultadoHospitalAlternativo> rutasHospitalesAlternativos;
    private boolean rutasAlternasVisibles = false;

    // =========================================================================
    // PALETA DE COLORES ECU 911
    // =========================================================================

    private static final Color COLOR_ECU911_AZUL = new Color(0, 51, 102);
    private static final Color COLOR_ECU911_ROJO = new Color(193, 39, 45);
    private static final Color COLOR_ECU911_AMARILLO = new Color(255, 215, 0);
    private static final Color COLOR_MSP = new Color(40, 167, 69);
    private static final Color COLOR_IESS = new Color(0, 123, 255);
    private static final Color COLOR_BOMBEROS = new Color(220, 53, 69);
    private static final Color COLOR_EXITO = new Color(25, 135, 84);
    private static final Color COLOR_FONDO = new Color(248, 249, 250);
    private static final Color COLOR_NARANJA = new Color(255, 152, 0); // ⚠️ NUEVO

    // =========================================================================
    // CONSTRUCTOR
    // =========================================================================

    public VentanaPrincipal() {
        setTitle("Sistema ECU 911 - Optimización de Rutas de Ambulancias | Universidad Central del Ecuador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500, 950);
        setLocationRelativeTo(null);

        // Inicializar datos
        grafo = DatosRedVial.crearRedVialUCE();
        ambulancias = DatosRedVial.crearAmbulancia(grafo);

        // Construir interfaz
        construirInterfaz();

        // Iniciar reloj
        iniciarReloj();

        setVisible(true);
    }

    // =========================================================================
    // CONSTRUCCIÓN DE LA INTERFAZ
    // =========================================================================

    private void construirInterfaz() {
        setLayout(new BorderLayout());

        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearPanelPrincipal(), BorderLayout.CENTER);
        add(crearPieDePagina(), BorderLayout.SOUTH);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_ECU911_AZUL);
        panel.setBorder(new EmptyBorder(15, 25, 15, 25));

        // Panel izquierdo: Título
        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBackground(COLOR_ECU911_AZUL);

        JLabel titulo = new JLabel("SISTEMA ECU 911 - OPTIMIZACIÓN DE RUTAS");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.WHITE);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitulo = new JLabel("Universidad Central del Ecuador | Teoría de Grafos | Algoritmo de Dijkstra V3.1");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitulo.setForeground(new Color(200, 220, 255));
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        panelTitulo.add(titulo);
        panelTitulo.add(Box.createVerticalStrut(5));
        panelTitulo.add(subtitulo);

        // Panel derecho: Logos institucionales
        JPanel panelLogos = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelLogos.setBackground(COLOR_ECU911_AZUL);

        String[] instituciones = {"ECU 911", "MSP", "IESS", "BOMBEROS", "UCE"};
        Color[] colores = {Color.WHITE, COLOR_MSP, COLOR_IESS, COLOR_BOMBEROS, COLOR_ECU911_AMARILLO};

        for (int i = 0; i < instituciones.length; i++) {
            JLabel logo = new JLabel(instituciones[i]);
            logo.setFont(new Font("Arial", Font.BOLD, 10));
            logo.setForeground(colores[i]);
            logo.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(colores[i], 2),
                    new EmptyBorder(3, 8, 3, 8)
            ));
            panelLogos.add(logo);
        }

        panel.add(panelTitulo, BorderLayout.WEST);
        panel.add(panelLogos, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(crearPanelControles(), BorderLayout.WEST);
        panel.add(crearPanelMapa(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelControles() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        panel.setPreferredSize(new Dimension(480, 0));

        // Título
        JLabel titulo = new JLabel("PANEL DE CONTROL ECU 911");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(COLOR_ECU911_AZUL);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(15));

        // Indicador de hora pico
        panel.add(crearIndicadorHoraPico());
        panel.add(Box.createVerticalStrut(15));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(15));

        // Secciones
        panel.add(crearSeccionEmergencia());
        panel.add(Box.createVerticalStrut(15));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(15));
        panel.add(crearSeccionCalculo());
        panel.add(Box.createVerticalStrut(15));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(15));

        // ⚠️ NUEVO: Sección de rutas alternas
        panel.add(crearSeccionRutasAlternas());
        panel.add(Box.createVerticalStrut(15));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(15));

        panel.add(crearSeccionResultados());

        return panel;
    }

    private JPanel crearIndicadorHoraPico() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO);

        labelHoraPico = new JLabel();
        labelHoraPico.setFont(new Font("Segoe UI", Font.BOLD, 12));
        labelHoraPico.setAlignmentX(Component.CENTER_ALIGNMENT);

        actualizarIndicadorHoraPico();

        panel.add(labelHoraPico);

        return panel;
    }

    private void actualizarIndicadorHoraPico() {
        boolean esHoraPico = GestorHorasPico.esHoraPico();
        String estado = GestorHorasPico.obtenerEstadoTrafico();

        if (esHoraPico) {
            labelHoraPico.setText("  " + estado + " - RUTAS ALTERNATIVAS ACTIVADAS");
            labelHoraPico.setForeground(COLOR_ECU911_ROJO);
        } else {
            labelHoraPico.setText("  " + estado);
            labelHoraPico.setForeground(COLOR_EXITO);
        }
    }

    private JPanel crearSeccionEmergencia() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO);

        JLabel etiqueta = new JLabel("1. SELECCIONAR PUNTO DE EMERGENCIA");
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        etiqueta.setForeground(COLOR_ECU911_AZUL);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(etiqueta);
        panel.add(Box.createVerticalStrut(10));

        List<Nodo> puntosEmergencia = DatosRedVial.obtenerPuntosEmergencia(grafo);
        String[] nombres = puntosEmergencia.stream()
                .map(Nodo::getNombre)
                .toArray(String[]::new);

        comboEmergencia = new JComboBox<>(nombres);
        comboEmergencia.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        comboEmergencia.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(comboEmergencia);
        panel.add(Box.createVerticalStrut(12));

        JButton btnMarcar = crearBoton("   MARCAR EMERGENCIA", COLOR_ECU911_AMARILLO, Color.BLACK);
        btnMarcar.addActionListener(e -> marcarEmergencia());
        panel.add(btnMarcar);

        return panel;
    }

    private JPanel crearSeccionCalculo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO);

        JLabel etiqueta = new JLabel("2. EJECUTAR ALGORITMO DE DIJKSTRA");
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        etiqueta.setForeground(COLOR_ECU911_AZUL);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(etiqueta);
        panel.add(Box.createVerticalStrut(10));

        JButton btnCalcular = crearBoton("  CALCULAR RUTA ÓPTIMA", COLOR_EXITO, Color.WHITE);
        btnCalcular.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCalcular.addActionListener(e -> calcularRutaOptima());
        panel.add(btnCalcular);

        return panel;
    }

    // ⚠️ NUEVO MÉTODO: Crear sección de rutas alternas
    private JPanel crearSeccionRutasAlternas() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO);

        JLabel etiqueta = new JLabel("2.5. RUTAS ALTERNATIVAS");
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        etiqueta.setForeground(COLOR_ECU911_AZUL);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(etiqueta);
        panel.add(Box.createVerticalStrut(10));

        btnMostrarRutasAlternas = crearBoton("   MOSTRAR RUTAS ALTERNAS", COLOR_NARANJA, Color.WHITE);
        btnMostrarRutasAlternas.setEnabled(false); // Deshabilitado hasta calcular ruta
        btnMostrarRutasAlternas.addActionListener(e -> toggleRutasAlternas());
        panel.add(btnMostrarRutasAlternas);

        return panel;
    }

    private JPanel crearSeccionResultados() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO);

        JLabel etiqueta = new JLabel("3. ANÁLISIS Y RESULTADOS");
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        etiqueta.setForeground(COLOR_ECU911_AZUL);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(etiqueta);
        panel.add(Box.createVerticalStrut(10));

        textoResultados = new JTextArea(20, 40);
        textoResultados.setEditable(false);
        textoResultados.setFont(new Font("Consolas", Font.PLAIN, 10));
        textoResultados.setBackground(Color.WHITE);
        textoResultados.setBorder(new EmptyBorder(12, 12, 12, 12));
        textoResultados.setLineWrap(false);
        textoResultados.setText("⏳ Esperando selección de emergencia y cálculo...\n\n" +
                "📋 INSTRUCCIONES:\n" +
                "1. Seleccione un punto de emergencia\n" +
                "2. Marque la emergencia en el mapa\n" +
                "3. Ejecute el algoritmo de Dijkstra\n" +
                "4. Revise el análisis detallado aquí\n\n" +
                "⚠️ EN HORAS PICO se calcularán automáticamente\n" +
                "   rutas alternativas a 2°, 3° y 4° hospital.");

        JScrollPane scroll = new JScrollPane(textoResultados);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));
        scroll.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        panel.add(scroll);
        panel.add(Box.createVerticalStrut(12));

        JButton btnReiniciar = crearBoton("🔄 REINICIAR SISTEMA", COLOR_ECU911_AZUL, Color.WHITE);
        btnReiniciar.addActionListener(e -> reiniciar());
        panel.add(btnReiniciar);

        return panel;
    }

    private JPanel crearPanelMapa() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("MAPA DE RED VIAL - QUITO, ECUADOR");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(COLOR_ECU911_AZUL);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titulo, BorderLayout.NORTH);

        panelMapa = new PanelMapa(grafo);
        JScrollPane scrollPaneMapa = new JScrollPane(panelMapa);
        scrollPaneMapa.setBorder(new LineBorder(new Color(200, 200, 200), 2));
        scrollPaneMapa.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPaneMapa.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        panelMapa.setScrollPane(scrollPaneMapa);
        panel.add(scrollPaneMapa, BorderLayout.CENTER);
        panel.add(crearLeyenda(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearLeyenda() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 12));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1),
                new EmptyBorder(8, 15, 8, 15)
        ));

        panel.add(crearItemLeyenda(COLOR_MSP, "Hospital MSP"));
        panel.add(crearItemLeyenda(COLOR_IESS, "Hospital IESS"));
        panel.add(crearItemLeyenda(COLOR_BOMBEROS, "Hospital Bomberos/Policía"));
        panel.add(crearItemLeyenda(new Color(108, 117, 125), "Intersección"));
        panel.add(crearItemLeyenda(COLOR_ECU911_AMARILLO, "Emergencia"));
        panel.add(crearItemLeyenda(new Color(66, 133, 244), "Ruta Óptima"));
        panel.add(crearItemLeyenda(COLOR_NARANJA, "Ruta Alterna 1"));
        panel.add(crearItemLeyenda(new Color(156, 39, 176), "Ruta Alterna 2"));

        return panel;
    }

    private JPanel crearItemLeyenda(Color color, String texto) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panel.setBackground(COLOR_FONDO);

        JPanel circulo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.fillOval(0, 0, 14, 14);
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawOval(0, 0, 14, 14);
            }
        };
        circulo.setPreferredSize(new Dimension(14, 14));
        circulo.setBackground(COLOR_FONDO);

        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("Segoe UI", Font.PLAIN, 10));

        panel.add(circulo);
        panel.add(etiqueta);

        return panel;
    }

    private JPanel crearPieDePagina() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_ECU911_AZUL);
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel textoIzq = new JLabel("© 2025 Universidad Central del Ecuador | Sistema Integrado de Emergencias ECU 911");
        textoIzq.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        textoIzq.setForeground(new Color(200, 220, 255));

        labelReloj = new JLabel();
        labelReloj.setFont(new Font("Segoe UI", Font.BOLD, 11));
        labelReloj.setForeground(Color.WHITE);

        panel.add(textoIzq, BorderLayout.WEST);
        panel.add(labelReloj, BorderLayout.EAST);

        return panel;
    }

    private JButton crearBoton(String texto, Color colorFondo, Color colorTexto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBackground(colorFondo);
        boton.setForeground(colorTexto);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (boton.isEnabled()) {
                    boton.setBackground(colorFondo.darker());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorFondo);
            }
        });

        return boton;
    }

    private void iniciarReloj() {
        Timer timer = new Timer(1000, e -> {
            LocalTime ahora = LocalTime.now();
            labelReloj.setText("🕐 " + ahora.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            actualizarIndicadorHoraPico();
        });
        timer.start();
    }

    // =========================================================================
    // LÓGICA DE NEGOCIO
    // =========================================================================

    private void marcarEmergencia() {
        String nombreNodo = (String) comboEmergencia.getSelectedItem();
        nodoEmergenciaActual = grafo.obtenerNodo(nombreNodo);

        if (nodoEmergenciaActual != null) {
            panelMapa.marcarEmergencia(nodoEmergenciaActual);

            String mensaje = "  Punto de emergencia marcado exitosamente:\n\n" +
                    "" + nombreNodo + "\n\n" +
                    "  Ahora puede ejecutar el algoritmo de Dijkstra.";

            if (GestorHorasPico.esHoraPico()) {
                mensaje += "\n\n   HORA PICO DETECTADA\n" +
                        "Se calcularán rutas alternativas automáticamente.";
            }

            JOptionPane.showMessageDialog(this, mensaje,
                    "Emergencia Marcada - ECU 911",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void calcularRutaOptima() {
        if (nodoEmergenciaActual == null) {
            JOptionPane.showMessageDialog(this,
                    "  ADVERTENCIA\n\n" +
                            "Primero debe marcar un punto de emergencia\n" +
                            "antes de calcular la ruta óptima.",
                    "Advertencia - ECU 911",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        textoResultados.setText("🔄 Ejecutando algoritmo de Dijkstra...\n" +
                "   Analizando " + ambulancias.size() + " ambulancias disponibles...\n" +
                "   Calculando rutas óptimas...");

        long tiempoInicio = System.currentTimeMillis();

        // Calcular ruta de ambulancia
        resultadoActual = Dijkstra.encontrarAmbulanciaOptima(
                grafo, ambulancias, nodoEmergenciaActual
        );

        // ⚠️ Calcular rutas alternativas a hospitales (siempre, no solo en hora pico)
        List<Nodo> hospitales = DatosRedVial.obtenerHospitales(grafo);
        rutasHospitalesAlternativos = Dijkstra.calcularRutasAHospitalesAlternativos(
                grafo, nodoEmergenciaActual, hospitales
        );

        long tiempoFin = System.currentTimeMillis();
        long tiempoProcesamiento = tiempoFin - tiempoInicio;

        if (resultadoActual != null && resultadoActual.getRutaOptima() != null) {
            panelMapa.mostrarRuta(
                    resultadoActual.getRutaOptima(),
                    resultadoActual.getAmbulanciaOptima()
            );

            // ⚠️ Habilitar botón de rutas alternas si hay resultados
            if (rutasHospitalesAlternativos != null && rutasHospitalesAlternativos.size() >= 2) {
                btnMostrarRutasAlternas.setEnabled(true);
            }

            mostrarResultadosDetallados(resultadoActual, rutasHospitalesAlternativos, tiempoProcesamiento);

            String tipoAmb = resultadoActual.getAmbulanciaOptima().getIdentificador().contains("MSP") ? "MSP" :
                    resultadoActual.getAmbulanciaOptima().getIdentificador().contains("IESS") ? "IESS" : "BOMBEROS";

            String mensajeExtra = "";
            if (rutasHospitalesAlternativos != null && !rutasHospitalesAlternativos.isEmpty()) {
                mensajeExtra = "\n\n🗺️ Se calcularon " + rutasHospitalesAlternativos.size() +
                        " rutas alternativas a hospitales.\n" +
                        "Use el botón 'MOSTRAR RUTAS ALTERNAS' para visualizarlas.";
            }

            JOptionPane.showMessageDialog(this,
                    String.format(
                            "✅ CÁLCULO COMPLETADO EXITOSAMENTE\n\n" +
                                    "🚑 Ambulancia seleccionada: %s (%s)\n" +
                                    "⏱️  Tiempo estimado de llegada: %.2f minutos\n" +
                                    "🏥 Hospital de origen: %s\n\n" +
                                    "📄 Revise el análisis detallado en el panel de resultados.%s",
                            resultadoActual.getAmbulanciaOptima().getIdentificador(),
                            tipoAmb,
                            resultadoActual.getTiempoOptimo(),
                            resultadoActual.getAmbulanciaOptima().getHospital().getNombre(),
                            mensajeExtra
                    ),
                    "Cálculo Completado - ECU 911",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ⚠️ NUEVO MÉTODO: Toggle rutas alternas
    private void toggleRutasAlternas() {
        if (rutasHospitalesAlternativos == null || rutasHospitalesAlternativos.size() < 2) {
            JOptionPane.showMessageDialog(this,
                    "⚠️ No hay suficientes rutas alternativas disponibles.\n" +
                            "Se requieren al menos 2 hospitales alternativos.",
                    "Advertencia - ECU 911",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        rutasAlternasVisibles = !rutasAlternasVisibles;

        if (rutasAlternasVisibles) {
            // Mostrar rutas alternas (2da y 3ra opción)
            List<List<Nodo>> rutasParaMostrar = new ArrayList<>();
            List<Ambulancia> ambulanciasVirtuales = new ArrayList<>();

            // Tomar la 2da y 3ra mejor ruta a hospitales (índices 1 y 2)
            for (int i = 1; i < Math.min(3, rutasHospitalesAlternativos.size()); i++) {
                rutasParaMostrar.add(rutasHospitalesAlternativos.get(i).getRuta());

                // Crear ambulancia virtual para mostrar en el hospital
                Nodo hospitalAlternativo = rutasHospitalesAlternativos.get(i).getHospital();
                String idVirtual = "ALT-" + (i) + "-" + hospitalAlternativo.getNombre().substring(3, 6);
                Ambulancia ambVirtual = new Ambulancia(idVirtual, hospitalAlternativo, hospitalAlternativo);
                ambulanciasVirtuales.add(ambVirtual);
            }

            panelMapa.mostrarRutasAlternas(rutasParaMostrar, ambulanciasVirtuales);
            btnMostrarRutasAlternas.setText("🔄 OCULTAR RUTAS ALTERNAS");
            btnMostrarRutasAlternas.setBackground(new Color(156, 39, 176)); // Púrpura

        } else {
            // Ocultar rutas alternas
            panelMapa.limpiarRutasAlternas();
            btnMostrarRutasAlternas.setText("🗺️ MOSTRAR RUTAS ALTERNAS");
            btnMostrarRutasAlternas.setBackground(COLOR_NARANJA);
        }
    }

    private void mostrarResultadosDetallados(
            Dijkstra.ResultadoOptimizacion resultado,
            List<Dijkstra.ResultadoHospitalAlternativo> rutasHospitales,
            long tiempoProcesamiento) {

        StringBuilder sb = new StringBuilder();

        // Encabezado
        sb.append("╔═══════════════════════════════════════════════════════════════════╗\n");
        sb.append("║          SISTEMA ECU 911 - RESULTADO DEL ANÁLISIS                 ║\n");
        sb.append("║      ALGORITMO DE DIJKSTRA PARA CAMINOS MÍNIMOS EN GRAFOS        ║\n");
        if (rutasHospitales != null && !rutasHospitales.isEmpty()) {
            sb.append("║         🗺️ RUTAS ALTERNATIVAS CALCULADAS - V3.1                 ║\n");
        }
        sb.append("╚═══════════════════════════════════════════════════════════════════╝\n\n");

        // Información del tráfico
        sb.append("🚦 ESTADO DEL TRÁFICO:\n");
        sb.append("════════════════════════════════════════════════════════════════════\n");
        sb.append(String.format("   %s\n", GestorHorasPico.obtenerEstadoTrafico()));
        sb.append(String.format("   Factor aplicado: %.1fx\n", GestorHorasPico.obtenerFactorTrafico()));
        sb.append(String.format("   Incremento: +%d%%\n", GestorHorasPico.obtenerPorcentajeIncremento()));
        sb.append(String.format("   %s\n\n", GestorHorasPico.obtenerDescripcionHoraria()));

        // Sección 1: Información de la Emergencia
        sb.append("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n");
        sb.append("┃  📋 SECCIÓN 1: ANÁLISIS DE LA EMERGENCIA                        ┃\n");
        sb.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛\n\n");
        sb.append(String.format("      Punto de emergencia: %s\n", nodoEmergenciaActual.getNombre()));
        sb.append(String.format("      Hora de registro: %s\n",
                LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
        sb.append(String.format("      Ambulancias analizadas: %d unidades\n", ambulancias.size()));
        sb.append("\n");

        // Sección 2: Ambulancia Seleccionada
        sb.append("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n");
        sb.append("┃  🚑 SECCIÓN 2: AMBULANCIA SELECCIONADA (RUTA MÁS RÁPIDA)        ┃\n");
        sb.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛\n\n");

        String idAmb = resultado.getAmbulanciaOptima().getIdentificador();
        String tipoAmb = idAmb.contains("MSP") ? "🏥 MSP (Ministerio de Salud Pública)" :
                idAmb.contains("IESS") ? "🏥 IESS (Instituto Ecuatoriano de Seguridad Social)" :
                        "🚒 BOMBEROS (Cuerpo de Bomberos de Quito)";

        sb.append(String.format("    Identificación: %s\n", idAmb));
        sb.append(String.format("   %s\n", tipoAmb));
        sb.append(String.format("    Hospital de origen: %s\n",
                resultado.getAmbulanciaOptima().getHospital().getNombre()));
        sb.append(String.format("     Tiempo estimado de llegada (ETA): %.2f minutos\n",
                resultado.getTiempoOptimo()));
        sb.append(String.format("    Número de intersecciones en ruta: %d\n\n",
                resultado.getRutaOptima().size() - 1));

        // Mostrar rutas alternativas a hospitales
        if (rutasHospitales != null && !rutasHospitales.isEmpty()) {
            sb.append("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n");
            sb.append("┃  🗺️ SECCIÓN 2.5: RUTAS ALTERNATIVAS A HOSPITALES              ┃\n");
            sb.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛\n\n");

            sb.append("   💡 Las siguientes rutas muestran el tiempo desde la emergencia\n");
            sb.append("      hacia cada hospital. Use el botón 'MOSTRAR RUTAS ALTERNAS'\n");
            sb.append("      para visualizar las rutas 2° y 3° en el mapa.\n\n");

            for (Dijkstra.ResultadoHospitalAlternativo ruta : rutasHospitales) {
                sb.append(String.format("   %s OPCIÓN %d: %s\n",
                        ruta.getMedalla(), ruta.getPosicion(), ruta.getHospital().getNombre()));
                sb.append(String.format("      ⏱️  Tiempo estimado: %.2f minutos\n", ruta.getTiempo()));
                sb.append(String.format("      📍 Distancia aproximada: %.2f km\n", ruta.getTiempo() * 0.7));
                sb.append(String.format("      🛣️  Intersecciones: %d\n", ruta.getRuta().size() - 1));

                // Mostrar ruta resumida
                sb.append("      🗺️  Ruta: ");
                List<Nodo> rutaDetalle = ruta.getRuta();
                if (rutaDetalle.size() <= 4) {
                    for (int i = 0; i < rutaDetalle.size(); i++) {
                        sb.append(rutaDetalle.get(i).getNombre());
                        if (i < rutaDetalle.size() - 1) sb.append(" → ");
                    }
                } else {
                    sb.append(rutaDetalle.get(0).getNombre());
                    sb.append(" → ... → ");
                    sb.append(rutaDetalle.get(rutaDetalle.size() - 1).getNombre());
                    sb.append(String.format(" (%d pasos)", rutaDetalle.size()));
                }
                sb.append("\n\n");
            }

            // Comparativa de tiempos
            if (rutasHospitales.size() >= 2) {
                double diferencia = rutasHospitales.get(1).getTiempo() - rutasHospitales.get(0).getTiempo();
                sb.append(String.format("   📊 Diferencia entre 1° y 2° hospital: +%.2f min (%.1f%% más)\n\n",
                        diferencia, (diferencia / rutasHospitales.get(0).getTiempo()) * 100));
            }
        }

        // Sección 3: Ruta Detallada de la Ambulancia
        sb.append("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n");
        sb.append("┃  🛣️  SECCIÓN 3: RUTA DE AMBULANCIA PASO A PASO                  ┃\n");
        sb.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛\n\n");

        List<Nodo> ruta = resultado.getRutaOptima();
        double tiempoAcumulado = 0.0;

        for (int i = 0; i < ruta.size(); i++) {
            Nodo nodoActual = ruta.get(i);

            if (i == 0) {
                sb.append(String.format("   🚑 INICIO: %s\n", nodoActual.getNombre()));
                sb.append("     └─ Hospital de origen\n\n");
            } else if (i == ruta.size() - 1) {
                Nodo nodoAnterior = ruta.get(i - 1);
                double tiempoSegmento = obtenerTiempoEntre(nodoAnterior, nodoActual);
                tiempoAcumulado += tiempoSegmento;

                sb.append(String.format("   🎯 DESTINO: %s\n", nodoActual.getNombre()));
                sb.append(String.format("     └─ Tiempo segmento: %.2f min | Tiempo total: %.2f min\n",
                        tiempoSegmento, tiempoAcumulado));
                sb.append("     └─ ⚠️  PUNTO DE EMERGENCIA - PACIENTE AQUÍ\n");
            } else {
                Nodo nodoAnterior = ruta.get(i - 1);
                double tiempoSegmento = obtenerTiempoEntre(nodoAnterior, nodoActual);
                tiempoAcumulado += tiempoSegmento;

                sb.append(String.format("   📍 PASO %d: %s\n", i, nodoActual.getNombre()));
                sb.append(String.format("     └─ Tiempo segmento: %.2f min | Tiempo acumulado: %.2f min\n\n",
                        tiempoSegmento, tiempoAcumulado));
            }
        }

        sb.append("\n");
        sb.append("  ════════════════════════════════════════════════════════════════\n");
        sb.append(String.format("    📏 Distancia total estimada: %.2f km\n",
                resultado.getTiempoOptimo() * 0.7));
        sb.append(String.format("   ⏱️  Tiempo total: %.2f minutos\n", resultado.getTiempoOptimo()));
        sb.append(String.format("   🔢 Total de segmentos: %d\n", ruta.size() - 1));
        sb.append("  ════════════════════════════════════════════════════════════════\n\n");

        // Sección 4: Comparativa de Ambulancias
        sb.append("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n");
        sb.append("┃  📊 SECCIÓN 4: COMPARATIVA DE TODAS LAS AMBULANCIAS            ┃\n");
        sb.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛\n\n");

        List<Dijkstra.ResultadoAmbulancia> todos = resultado.getTodosLosResultados();
        todos.sort((a, b) -> Double.compare(a.getTiempo(), b.getTiempo()));

        String[] medallas = {"🥇", "🥈", "🥉"};

        for (int i = 0; i < todos.size(); i++) {
            Dijkstra.ResultadoAmbulancia res = todos.get(i);
            boolean esMejor = res.getAmbulancia().equals(resultado.getAmbulanciaOptima());

            String medalla = i < 3 ? medallas[i] : "  ";
            String marca = esMejor ? " ⚡ SELECCIONADA" : "";
            String tipo = res.getAmbulancia().getIdentificador().contains("MSP") ? "MSP" :
                    res.getAmbulancia().getIdentificador().contains("IESS") ? "IESS" : "BOMB";

            sb.append(String.format("  %s Posición %d: %s (%s)%s\n",
                    medalla, i + 1, res.getAmbulancia().getIdentificador(), tipo, marca));
            sb.append(String.format("     ├─ Hospital: %s\n",
                    res.getAmbulancia().getHospital().getNombre()));
            sb.append(String.format("     ├─ Tiempo: %.2f minutos\n", res.getTiempo()));

            if (i > 0) {
                double diferencia = res.getTiempo() - todos.get(0).getTiempo();
                sb.append(String.format("     └─ Diferencia vs 1°: +%.2f minutos (%.1f%% más lento)\n\n",
                        diferencia, (diferencia / todos.get(0).getTiempo()) * 100));
            } else {
                sb.append("     └─ ⭐ TIEMPO MÁS RÁPIDO\n\n");
            }
        }

        // Sección 5: Estadísticas
        sb.append("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n");
        sb.append("┃  📈 SECCIÓN 5: ESTADÍSTICAS Y ANÁLISIS DEL ALGORITMO           ┃\n");
        sb.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛\n\n");

        double tiempoPromedio = todos.stream().mapToDouble(r -> r.getTiempo()).average().orElse(0);
        double tiempoMax = todos.stream().mapToDouble(r -> r.getTiempo()).max().orElse(0);
        double tiempoMin = resultado.getTiempoOptimo();
        double diferencia = tiempoMax - tiempoMin;

        sb.append(String.format("   📊 Tiempo promedio de todas las ambulancias: %.2f min\n", tiempoPromedio));
        sb.append(String.format("   ⚡ Tiempo mínimo (ruta óptima): %.2f min\n", tiempoMin));
        sb.append(String.format("   🐌 Tiempo máximo (peor ruta): %.2f min\n", tiempoMax));
        sb.append(String.format("   📉 Diferencia entre mejor y peor ruta: %.2f min (%.1f%%)\n",
                diferencia, (diferencia / tiempoMax) * 100));
        sb.append("\n");
        sb.append(String.format("   🧮 Algoritmo utilizado: Dijkstra (Caminos Mínimos)\n"));
        sb.append(String.format("   ⚙️  Complejidad computacional: O((V+E) log V)\n"));
        sb.append(String.format("   📍 Nodos en el grafo: %d\n", grafo.obtenerNumeroNodos()));
        sb.append(String.format("   🔗 Aristas en el grafo: %d\n", grafo.obtenerNumeroAristas()));
        sb.append(String.format("   ⏱️  Tiempo de procesamiento: %d ms\n", tiempoProcesamiento));
        sb.append("\n");

        // Recomendación
        sb.append("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓\n");
        sb.append("┃  💡 RECOMENDACIÓN DEL SISTEMA                                   ┃\n");
        sb.append("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛\n\n");

        if (GestorHorasPico.esHoraPico() && rutasHospitales != null && !rutasHospitales.isEmpty()) {
            sb.append("   ⚠️ HORA PICO DETECTADA - PROTOCOLO ESPECIAL:\n\n");
            sb.append("   OPCIÓN A (Recomendada): DESPACHO DE AMBULANCIA\n");
            sb.append(String.format("   └─ Despachar ambulancia %s\n", idAmb));
            sb.append(String.format("   └─ Tiempo estimado de llegada: %.2f minutos\n\n", resultado.getTiempoOptimo()));

            sb.append("   OPCIÓN B (Alternativa): TRASLADO DIRECTO AL HOSPITAL\n");
            sb.append(String.format("   └─ Hospital más cercano: %s\n", rutasHospitales.get(0).getHospital().getNombre()));
            sb.append(String.format("   └─ Tiempo de traslado: %.2f minutos\n", rutasHospitales.get(0).getTiempo()));
            sb.append("   └─ Considerar si hay transporte particular disponible\n\n");

            double tiempoEspera = resultado.getTiempoOptimo();
            double tiempoTraslado = rutasHospitales.get(0).getTiempo();

            if (tiempoTraslado < tiempoEspera) {
                sb.append(String.format("   📊 ANÁLISIS: El traslado directo es %.2f min MÁS RÁPIDO\n",
                        tiempoEspera - tiempoTraslado));
                sb.append("   ✅ Se recomienda OPCIÓN B si hay transporte disponible\n");
            } else {
                sb.append(String.format("   📊 ANÁLISIS: Esperar ambulancia es %.2f min MÁS RÁPIDO\n",
                        tiempoTraslado - tiempoEspera));
                sb.append("   ✅ Se recomienda OPCIÓN A (despacho de ambulancia)\n");
            }
        } else {
            sb.append(String.format("   🚑 Despachar inmediatamente la ambulancia %s\n", idAmb));
            sb.append(String.format("   ⏱️  Tiempo estimado de llegada: %.2f minutos\n", resultado.getTiempoOptimo()));
            sb.append("   🗺️  Seguir la ruta indicada en la Sección 3\n");
            sb.append("   📞 Coordinar con el hospital de origen para preparación\n");

            if (rutasHospitales != null && rutasHospitales.size() >= 2) {
                sb.append("\n   💡 Puede visualizar rutas alternativas con el botón correspondiente\n");
            }
        }

        sb.append("\n");
        sb.append("═══════════════════════════════════════════════════════════════════\n");
        sb.append("                   FIN DEL ANÁLISIS - ECU 911                      \n");
        sb.append("═══════════════════════════════════════════════════════════════════\n");

        textoResultados.setText(sb.toString());
        textoResultados.setCaretPosition(0);
    }

    private double obtenerTiempoEntre(Nodo origen, Nodo destino) {
        for (Grafo.ParNodoPeso par : grafo.obtenerVecinos(origen)) {
            if (par.getNodo().equals(destino)) {
                return par.getPeso();
            }
        }
        return 0.0;
    }

    private void reiniciar() {
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea reiniciar el sistema?\n\n" +
                        "Se perderán todos los datos de la emergencia actual.",
                "Confirmar Reinicio - ECU 911",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            nodoEmergenciaActual = null;
            resultadoActual = null;
            rutasHospitalesAlternativos = null;
            rutasAlternasVisibles = false;

            panelMapa.limpiar();
            btnMostrarRutasAlternas.setEnabled(false);
            btnMostrarRutasAlternas.setText("🗺️ MOSTRAR RUTAS ALTERNAS");
            btnMostrarRutasAlternas.setBackground(COLOR_NARANJA);

            textoResultados.setText("⏳ Sistema reiniciado. Esperando nueva emergencia...\n\n" +
                    "📋 INSTRUCCIONES:\n" +
                    "1. Seleccione un punto de emergencia\n" +
                    "2. Marque la emergencia en el mapa\n" +
                    "3. Ejecute el algoritmo de Dijkstra\n" +
                    "4. Use el botón 'MOSTRAR RUTAS ALTERNAS' para ver opciones\n" +
                    "5. Revise el análisis detallado aquí\n\n" +
                    "🗺️ Las rutas alternativas se calculan automáticamente\n" +
                    "   y pueden visualizarse en el mapa.");
            comboEmergencia.setSelectedIndex(0);
        }
    }


}
