package ec.edu.uce.vista;

import ec.edu.uce.modelo.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.ArrayList;

public class PanelMapa extends JPanel {

    private Grafo grafo;
    private List<Nodo> rutaOptima;
    private Nodo nodoEmergencia;
    private Ambulancia ambulanciaSeleccionada;

    // ⚠️ NUEVO: Lista de rutas alternas
    private List<List<Nodo>> rutasAlternas;
    private List<Ambulancia> ambulanciasAlternas;

    private BufferedImage imagenFondo;
    private boolean mostrarFondo = true;

    private Point dragStart;
    private JScrollPane scrollPane;

    private static final int RADIO_HOSPITAL = 18;
    private static final int RADIO_INTERSECCION = 10;
    private static final int RADIO_EMERGENCIA = 20;

    // Colores ECU 911
    private static final Color COLOR_ECU911_AZUL = new Color(0, 51, 102);
    private static final Color COLOR_ECU911_ROJO = new Color(220, 53, 69);
    private static final Color COLOR_ECU911_AMARILLO = new Color(230, 140, 0);

    private static final Color COLOR_MSP = new Color(40, 167, 69);
    private static final Color COLOR_IESS = new Color(0, 123, 255);
    private static final Color COLOR_BOMBEROS = new Color(220, 53, 69);

    private static final Color COLOR_INTERSECCION = new Color(108, 117, 125);
    private static final Color COLOR_ARISTA_NORMAL = new Color(200, 200, 200);
    private static final Color COLOR_ARISTA_RUTA = COLOR_ECU911_ROJO;
    private static final Color COLOR_FONDO = new Color(250, 250, 252);

    // ⚠️ NUEVO: Colores para rutas alternas
    private static final Color COLOR_RUTA_ALTERNA_1 = new Color(255, 152, 0);  // Naranja
    private static final Color COLOR_RUTA_ALTERNA_2 = new Color(156, 39, 176); // Púrpura

    private static final Color COLOR_AVENIDA = new Color(0, 51, 102, 200);

    private static final int ANCHO_MAPA = 1600;
    private static final int ALTO_MAPA = 1600;

    public PanelMapa(Grafo grafo) {
        this.grafo = grafo;
        this.rutaOptima = null;
        this.nodoEmergencia = null;
        this.ambulanciaSeleccionada = null;

        // ⚠️ NUEVO: Inicializar listas de rutas alternas
        this.rutasAlternas = new ArrayList<>();
        this.ambulanciasAlternas = new ArrayList<>();

        setBackground(COLOR_FONDO);
        setPreferredSize(new Dimension(ANCHO_MAPA, ALTO_MAPA));

        cargarImagenFondo();
        configurarScrollConMouse();
    }

    private void cargarImagenFondo() {
        try {
            File archivoImagen = new File("recursos/mapa_quito.png");

            if (archivoImagen.exists()) {
                imagenFondo = ImageIO.read(archivoImagen);
                System.out.println("✅ Imagen de fondo cargada correctamente: " + archivoImagen.getAbsolutePath());
            } else {
                System.err.println("⚠️ No se encontró la imagen en: " + archivoImagen.getAbsolutePath());
                System.err.println("   Por favor, coloca 'mapa_quito.png' en la carpeta 'recursos'");
                imagenFondo = null;
            }
        } catch (Exception e) {
            System.err.println("❌ Error al cargar la imagen de fondo: " + e.getMessage());
            e.printStackTrace();
            imagenFondo = null;
        }
    }

    public void setMostrarFondo(boolean mostrar) {
        this.mostrarFondo = mostrar;
        repaint();
    }

    private void configurarScrollConMouse() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragStart = null;
                setCursor(Cursor.getDefaultCursor());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart != null && scrollPane != null) {
                    JViewport viewport = scrollPane.getViewport();
                    Point viewPosition = viewport.getViewPosition();

                    int deltaX = dragStart.x - e.getX();
                    int deltaY = dragStart.y - e.getY();

                    viewPosition.translate(deltaX, deltaY);

                    viewPosition.x = Math.max(0, viewPosition.x);
                    viewPosition.y = Math.max(0, viewPosition.y);

                    Dimension viewSize = viewport.getExtentSize();
                    Dimension viewportSize = viewport.getViewSize();

                    viewPosition.x = Math.min(viewPosition.x, viewportSize.width - viewSize.width);
                    viewPosition.y = Math.min(viewPosition.y, viewportSize.height - viewSize.height);

                    viewport.setViewPosition(viewPosition);
                }
            }
        });
    }

    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

    public void marcarEmergencia(Nodo nodo) {
        this.nodoEmergencia = nodo;
        repaint();
    }

    public void mostrarRuta(List<Nodo> ruta, Ambulancia ambulancia) {
        this.rutaOptima = ruta;
        this.ambulanciaSeleccionada = ambulancia;
        repaint();
    }

    // ⚠️ NUEVO MÉTODO: Mostrar rutas alternas
    public void mostrarRutasAlternas(List<List<Nodo>> rutas, List<Ambulancia> ambulancias) {
        this.rutasAlternas = rutas != null ? rutas : new ArrayList<>();
        this.ambulanciasAlternas = ambulancias != null ? ambulancias : new ArrayList<>();
        repaint();
    }

    // ⚠️ NUEVO MÉTODO: Limpiar solo rutas alternas
    public void limpiarRutasAlternas() {
        this.rutasAlternas.clear();
        this.ambulanciasAlternas.clear();
        repaint();
    }

    public void limpiar() {
        this.rutaOptima = null;
        this.nodoEmergencia = null;
        this.ambulanciaSeleccionada = null;
        this.rutasAlternas.clear();
        this.ambulanciasAlternas.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Dibujar imagen de fondo
        if (imagenFondo != null && mostrarFondo) {
            g2d.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        dibujarNombresAvenidas(g2d);
        dibujarTitulo(g2d);
        dibujarBrujula(g2d);
        dibujarIndicadorEstado(g2d);
        dibujarAristas(g2d);

        // ⚠️ NUEVO: Dibujar rutas alternas ANTES de la ruta óptima
        dibujarRutasAlternas(g2d);

        dibujarNodos(g2d);
        dibujarEmergencia(g2d);
        dibujarAmbulanciaSeleccionada(g2d);

        // ⚠️ NUEVO: Dibujar ambulancias alternas
        dibujarAmbulanciasAlternas(g2d);
    }

    private void dibujarNombresAvenidas(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(COLOR_AVENIDA);

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        dibujarTextoConSombra(g2d, "La Carolina", 920, 350);
        dibujarTextoConSombra(g2d, "Av. Naciones Unidas", 1070, 390);
        dibujarTextoConSombra(g2d, "Av. de los Shyris", 1170, 420);
        dibujarTextoConSombra(g2d, "Av. Patria", 770, 570);
        dibujarTextoConSombra(g2d, "Av. 10 de Agosto", 870, 670);
        dibujarTextoConSombra(g2d, "Av. 6 de Diciembre", 970, 520);
        dibujarTextoConSombra(g2d, "Parque El Ejido", 820, 770);
        dibujarTextoConSombra(g2d, "Av. Universitaria", 620, 670);
        dibujarTextoConSombra(g2d, "UCE", 520, 720);
        dibujarTextoConSombra(g2d, "Av. América", 470, 570);
        dibujarTextoConSombra(g2d, "Av. Mariscal Sucre", 670, 1070);
        dibujarTextoConSombra(g2d, "Av. Maldonado", 570, 1220);
        dibujarTextoConSombra(g2d, "El Recreo", 620, 1370);

        g2d.setFont(new Font("Arial", Font.PLAIN, 13));
        g2d.setColor(new Color(0, 51, 102, 130));

        dibujarTextoConSombra(g2d, "Av. Occidental", 370, 220);
        dibujarTextoConSombra(g2d, "Av. Mariana de Jesús", 820, 320);
        dibujarTextoConSombra(g2d, "Av. 18 de Septiembre", 570, 620);
        dibujarTextoConSombra(g2d, "Av. Gran Colombia", 720, 920);
    }

    private void dibujarTextoConSombra(Graphics2D g2d, String texto, int x, int y) {
        g2d.setColor(Color.WHITE);
        g2d.drawString(texto, x + 1, y + 1);
        g2d.drawString(texto, x - 1, y - 1);
        g2d.drawString(texto, x + 1, y - 1);
        g2d.drawString(texto, x - 1, y + 1);

        g2d.setColor(COLOR_AVENIDA);
        g2d.drawString(texto, x, y);
    }

    private void dibujarTitulo(Graphics2D g2d) {
        g2d.setFont(new Font("Arial", Font.BOLD, 15));
        g2d.setColor(COLOR_ECU911_AZUL);
        String titulo = "MAPA DE RED VIAL - QUITO, ECUADOR";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(titulo)) / 2;
        g2d.drawString(titulo, x, 22);
    }

    private void dibujarBrujula(Graphics2D g2d) {
        int x = getWidth() - 65;
        int y = 60;
        int radio = 32;

        g2d.setColor(new Color(255, 255, 255, 250));
        g2d.fillOval(x - radio, y - radio, 2 * radio, 2 * radio);

        g2d.setColor(COLOR_ECU911_AZUL);
        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawOval(x - radio, y - radio, 2 * radio, 2 * radio);

        int[] xN = {x, x - 7, x + 7};
        int[] yN = {y - radio + 10, y - 5, y - 5};
        g2d.setColor(COLOR_ECU911_ROJO);
        g2d.fillPolygon(xN, yN, 3);

        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(COLOR_ECU911_ROJO);
        g2d.drawString("N", x - 5, y - radio - 6);

        g2d.setColor(new Color(80, 80, 80));
        g2d.drawString("S", x - 4, y + radio + 16);
        g2d.drawString("E", x + radio + 8, y + 5);
        g2d.drawString("O", x - radio - 16, y + 5);
    }

    private void dibujarIndicadorEstado(Graphics2D g2d) {
        g2d.setColor(new Color(40, 167, 69));
        g2d.fillOval(18, 18, 12, 12);

        g2d.setColor(new Color(144, 238, 144, 180));
        g2d.fillOval(20, 20, 8, 8);

        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        g2d.setColor(COLOR_ECU911_AZUL);
        g2d.drawString("SISTEMA ACTIVO - ECU 911", 36, 27);
    }

    private void dibujarAristas(Graphics2D g2d) {
        for (Arista arista : grafo.obtenerTodasLasAristas()) {
            Point2D p1 = coordenadasCanvas(arista.getOrigen());
            Point2D p2 = coordenadasCanvas(arista.getDestino());

            boolean esRutaOptima = esParteDeRutaOptima(arista);

            if (esRutaOptima) {
                g2d.setColor(new Color(66, 133, 244, 100));
                g2d.setStroke(new BasicStroke(12.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.draw(new Line2D.Double(p1.getX() + 2, p1.getY() + 2, p2.getX() + 2, p2.getY() + 2));

                g2d.setColor(new Color(66, 133, 244));
                g2d.setStroke(new BasicStroke(8.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.draw(new Line2D.Double(p1, p2));

                dibujarTiempoRutaOptima(g2d, arista, p1, p2);
            } else {
                g2d.setColor(new Color(224, 224, 224));
                g2d.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.draw(new Line2D.Double(p1, p2));

                dibujarTiempoArista(g2d, arista, p1, p2);
            }
        }
    }

    // ⚠️ NUEVO MÉTODO: Dibujar rutas alternas
    private void dibujarRutasAlternas(Graphics2D g2d) {
        if (rutasAlternas == null || rutasAlternas.isEmpty()) {
            return;
        }

        Color[] coloresRutas = {COLOR_RUTA_ALTERNA_1, COLOR_RUTA_ALTERNA_2};
        float[] anchos = {6.0f, 6.0f};

        for (int rutaIndex = 0; rutaIndex < rutasAlternas.size() && rutaIndex < 2; rutaIndex++) {
            List<Nodo> ruta = rutasAlternas.get(rutaIndex);
            Color colorRuta = coloresRutas[rutaIndex];
            float ancho = anchos[rutaIndex];

            if (ruta == null || ruta.size() < 2) {
                continue;
            }

            for (int i = 0; i < ruta.size() - 1; i++) {
                Nodo nodo1 = ruta.get(i);
                Nodo nodo2 = ruta.get(i + 1);

                Point2D p1 = coordenadasCanvas(nodo1);
                Point2D p2 = coordenadasCanvas(nodo2);

                // Sombra
                g2d.setColor(new Color(colorRuta.getRed(), colorRuta.getGreen(), colorRuta.getBlue(), 80));
                g2d.setStroke(new BasicStroke(ancho + 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.draw(new Line2D.Double(p1.getX() + 2, p1.getY() + 2, p2.getX() + 2, p2.getY() + 2));

                // Línea principal
                g2d.setColor(colorRuta);
                g2d.setStroke(new BasicStroke(ancho, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.draw(new Line2D.Double(p1, p2));

                // Dibujar tiempo en el segmento
                dibujarTiempoRutaAlterna(g2d, nodo1, nodo2, p1, p2, colorRuta);
            }
        }
    }

    private void dibujarTiempoRutaAlterna(Graphics2D g2d, Nodo nodo1, Nodo nodo2, Point2D p1, Point2D p2, Color colorRuta) {
        double mx = (p1.getX() + p2.getX()) / 2;
        double my = (p1.getY() + p2.getY()) / 2;

        double tiempo = obtenerTiempoEntre(nodo1, nodo2);
        String tiempoStr = String.format("%.1f'", tiempo);

        g2d.setFont(new Font("Arial", Font.BOLD, 10));

        FontMetrics fm = g2d.getFontMetrics();
        int w = fm.stringWidth(tiempoStr);
        int h = fm.getHeight();

        g2d.setColor(colorRuta);
        g2d.fillRoundRect((int) (mx - w / 2 - 4), (int) (my - h / 2), w + 8, h + 2, 5, 5);

        g2d.setColor(Color.WHITE);
        g2d.drawString(tiempoStr, (int) (mx - w / 2), (int) (my + 4));
    }

    private double obtenerTiempoEntre(Nodo origen, Nodo destino) {
        for (Grafo.ParNodoPeso par : grafo.obtenerVecinos(origen)) {
            if (par.getNodo().equals(destino)) {
                return par.getPeso();
            }
        }
        return 0.0;
    }

    private void dibujarTiempoArista(Graphics2D g2d, Arista arista, Point2D p1, Point2D p2) {
        double mx = (p1.getX() + p2.getX()) / 2;
        double my = (p1.getY() + p2.getY()) / 2;

        String tiempo = String.format("%.1f'", arista.getPeso());
        g2d.setFont(new Font("Arial", Font.BOLD, 10));

        FontMetrics fm = g2d.getFontMetrics();
        int w = fm.stringWidth(tiempo);
        int h = fm.getHeight();

        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect((int) (mx - w / 2 - 3), (int) (my - h / 2), w + 6, h, 5, 5);

        g2d.setColor(new Color(180, 180, 180));
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.drawRoundRect((int) (mx - w / 2 - 3), (int) (my - h / 2), w + 6, h, 5, 5);

        g2d.setColor(new Color(100, 100, 100));
        g2d.drawString(tiempo, (int) (mx - w / 2), (int) (my + 4));
    }

    private void dibujarTiempoRutaOptima(Graphics2D g2d, Arista arista, Point2D p1, Point2D p2) {
        double mx = (p1.getX() + p2.getX()) / 2;
        double my = (p1.getY() + p2.getY()) / 2;

        String tiempo = String.format("%.1f min", arista.getPeso());
        g2d.setFont(new Font("Arial", Font.BOLD, 11));

        FontMetrics fm = g2d.getFontMetrics();
        int w = fm.stringWidth(tiempo);
        int h = fm.getHeight();

        g2d.setColor(COLOR_ECU911_ROJO);
        g2d.fillRoundRect((int) (mx - w / 2 - 5), (int) (my - h / 2), w + 10, h + 2, 7, 7);

        g2d.setColor(Color.WHITE);
        g2d.drawString(tiempo, (int) (mx - w / 2), (int) (my + 4));
    }

    private void dibujarNodos(Graphics2D g2d) {
        for (Nodo nodo : grafo.obtenerTodosLosNodos()) {
            if (!nodo.isEsHospital() && !nodo.equals(nodoEmergencia)) {
                dibujarInterseccion(g2d, nodo);
            }
        }

        for (Nodo nodo : grafo.obtenerTodosLosNodos()) {
            if (nodo.isEsHospital()) {
                dibujarHospital(g2d, nodo);
            }
        }
    }

    private void dibujarHospital(Graphics2D g2d, Nodo nodo) {
        Point2D p = coordenadasCanvas(nodo);
        Color color = determinarColorHospital(nodo.getNombre());

        int x = (int) p.getX();
        int y = (int) p.getY();

        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillOval(x - RADIO_HOSPITAL + 2, y - RADIO_HOSPITAL + 2,
                2 * RADIO_HOSPITAL, 2 * RADIO_HOSPITAL);

        g2d.setColor(color);
        g2d.fillOval(x - RADIO_HOSPITAL, y - RADIO_HOSPITAL,
                2 * RADIO_HOSPITAL, 2 * RADIO_HOSPITAL);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3.0f));
        g2d.drawOval(x - RADIO_HOSPITAL, y - RADIO_HOSPITAL,
                2 * RADIO_HOSPITAL, 2 * RADIO_HOSPITAL);

        g2d.setStroke(new BasicStroke(2.5f));
        g2d.drawLine(x - 8, y, x + 8, y);
        g2d.drawLine(x, y - 8, x, y + 8);

        dibujarEtiquetaHospital(g2d, nodo, p);
    }

    private Color determinarColorHospital(String nombre) {
        if (nombre.contains("Eugenio") || nombre.contains("Baca")) return COLOR_MSP;
        if (nombre.contains("Carlos") || nombre.contains("Pablo")) return COLOR_IESS;
        return COLOR_BOMBEROS;
    }

    private void dibujarInterseccion(Graphics2D g2d, Nodo nodo) {
        Point2D p = coordenadasCanvas(nodo);

        int x = (int) p.getX();
        int y = (int) p.getY();

        g2d.setColor(new Color(0, 0, 0, 40));
        g2d.fillOval(x - RADIO_INTERSECCION + 2, y - RADIO_INTERSECCION + 2,
                2 * RADIO_INTERSECCION, 2 * RADIO_INTERSECCION);

        g2d.setColor(COLOR_INTERSECCION);
        g2d.fillOval(x - RADIO_INTERSECCION, y - RADIO_INTERSECCION,
                2 * RADIO_INTERSECCION, 2 * RADIO_INTERSECCION);

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawOval(x - RADIO_INTERSECCION, y - RADIO_INTERSECCION,
                2 * RADIO_INTERSECCION, 2 * RADIO_INTERSECCION);

        dibujarEtiquetaNodo(g2d, nodo, p);
    }

    private void dibujarEmergencia(Graphics2D g2d) {
        if (nodoEmergencia == null) return;

        Point2D p = coordenadasCanvas(nodoEmergencia);
        int x = (int) p.getX();
        int y = (int) p.getY();

        for (int i = 3; i > 0; i--) {
            g2d.setColor(new Color(230, 140, 0, 15 * i));
            int r = RADIO_EMERGENCIA + (4 - i) * 8;
            g2d.fillOval(x - r, y - r, 2 * r, 2 * r);
        }

        g2d.setColor(COLOR_ECU911_AMARILLO);
        g2d.fillOval(x - RADIO_EMERGENCIA, y - RADIO_EMERGENCIA,
                2 * RADIO_EMERGENCIA, 2 * RADIO_EMERGENCIA);

        g2d.setColor(COLOR_ECU911_ROJO);
        g2d.setStroke(new BasicStroke(3.5f));
        g2d.drawOval(x - RADIO_EMERGENCIA, y - RADIO_EMERGENCIA,
                2 * RADIO_EMERGENCIA, 2 * RADIO_EMERGENCIA);

        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        g2d.drawString("!", x - 4, y + 7);

        dibujarEtiquetaEmergencia(g2d, p);
    }

    private void dibujarEtiquetaEmergencia(Graphics2D g2d, Point2D p) {
        int x = (int) p.getX();
        int y = (int) p.getY();

        g2d.setFont(new Font("Arial", Font.BOLD, 11));
        String texto = "EMERGENCIA";
        FontMetrics fm = g2d.getFontMetrics();
        int w = fm.stringWidth(texto);

        g2d.setColor(COLOR_ECU911_ROJO);
        g2d.fillRoundRect(x - w / 2 - 7, y - RADIO_EMERGENCIA - 34, w + 14, 18, 9, 9);

        g2d.setColor(Color.WHITE);
        g2d.drawString(texto, x - w / 2, y - RADIO_EMERGENCIA - 20);
    }

    private void dibujarAmbulanciaSeleccionada(Graphics2D g2d) {
        if (ambulanciaSeleccionada == null) return;

        Point2D p = coordenadasCanvas(ambulanciaSeleccionada.getHospital());

        int x = (int) p.getX();
        int y = (int) p.getY();

        String id = ambulanciaSeleccionada.getIdentificador();
        String emoji = id.contains("BOMB") ? "🚒" : "🚑";

        g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        g2d.drawString(emoji, x - 14, y + RADIO_HOSPITAL + 40);

        g2d.setFont(new Font("Arial", Font.BOLD, 9));
        FontMetrics fm = g2d.getFontMetrics();
        int w = fm.stringWidth(id);

        Color colorAmb = id.contains("MSP") ? COLOR_MSP :
                id.contains("IESS") ? COLOR_IESS : COLOR_BOMBEROS;

        g2d.setColor(colorAmb);
        g2d.fillRoundRect(x - w / 2 - 5, y + RADIO_HOSPITAL + 45, w + 10, 14, 7, 7);

        g2d.setColor(Color.WHITE);
        g2d.drawString(id, x - w / 2, y + RADIO_HOSPITAL + 56);
    }

    // ⚠️ NUEVO MÉTODO: Dibujar ambulancias alternas
    private void dibujarAmbulanciasAlternas(Graphics2D g2d) {
        if (ambulanciasAlternas == null || ambulanciasAlternas.isEmpty()) {
            return;
        }

        String[] etiquetas = {"2ª", "3ª"};
        Color[] colores = {COLOR_RUTA_ALTERNA_1, COLOR_RUTA_ALTERNA_2};

        for (int i = 0; i < ambulanciasAlternas.size() && i < 2; i++) {
            Ambulancia ambulancia = ambulanciasAlternas.get(i);
            Point2D p = coordenadasCanvas(ambulancia.getHospital());

            int x = (int) p.getX();
            int y = (int) p.getY();

            String id = ambulancia.getIdentificador();
            String emoji = id.contains("BOMB") ? "🚒" : "🚑";

            // Dibujar emoji de ambulancia
            g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            g2d.drawString(emoji, x + 20 + (i * 15), y + RADIO_HOSPITAL + 40);

            // Etiqueta de la ambulancia alterna
            g2d.setFont(new Font("Arial", Font.BOLD, 9));
            FontMetrics fm = g2d.getFontMetrics();
            String etiqueta = etiquetas[i] + " " + id;
            int w = fm.stringWidth(etiqueta);

            g2d.setColor(colores[i]);
            g2d.fillRoundRect(x + 15 + (i * 15) - w / 2, y + RADIO_HOSPITAL + 60, w + 10, 14, 7, 7);

            g2d.setColor(Color.WHITE);
            g2d.drawString(etiqueta, x + 20 + (i * 15) - w / 2, y + RADIO_HOSPITAL + 71);
        }
    }

    private void dibujarEtiquetaHospital(Graphics2D g2d, Nodo nodo, Point2D p) {
        int x = (int) p.getX();
        int y = (int) p.getY();

        g2d.setFont(new Font("Arial", Font.BOLD, 10));
        FontMetrics fm = g2d.getFontMetrics();
        int w = fm.stringWidth(nodo.getNombre());
        int h = fm.getHeight();

        g2d.setColor(Color.WHITE);
        g2d.fillRoundRect(x - w / 2 - 5, y - RADIO_HOSPITAL - h - 3, w + 10, h + 2, 7, 7);

        g2d.setColor(determinarColorHospital(nodo.getNombre()));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(x - w / 2 - 5, y - RADIO_HOSPITAL - h - 3, w + 10, h + 2, 7, 7);

        g2d.setColor(COLOR_ECU911_AZUL);
        g2d.drawString(nodo.getNombre(), x - w / 2, y - RADIO_HOSPITAL - 6);
    }

    private void dibujarEtiquetaNodo(Graphics2D g2d, Nodo nodo, Point2D p) {
        int x = (int) p.getX();
        int y = (int) p.getY();

        g2d.setFont(new Font("Arial", Font.PLAIN, 8));
        FontMetrics fm = g2d.getFontMetrics();
        int w = fm.stringWidth(nodo.getNombre());

        g2d.setColor(new Color(255, 255, 255, 200));
        g2d.fillRoundRect(x - w / 2 - 2, y - RADIO_INTERSECCION - 16, w + 4, 10, 4, 4);

        g2d.setColor(new Color(80, 80, 80));
        g2d.drawString(nodo.getNombre(), x - w / 2, y - RADIO_INTERSECCION - 8);
    }

    private Point2D coordenadasCanvas(Nodo nodo) {
        return new Point2D.Double(nodo.getX(), nodo.getY());
    }

    private boolean esParteDeRutaOptima(Arista arista) {
        if (rutaOptima == null || rutaOptima.size() < 2) return false;

        for (int i = 0; i < rutaOptima.size() - 1; i++) {
            if (arista.conecta(rutaOptima.get(i), rutaOptima.get(i + 1))) return true;
        }
        return false;
    }
}
