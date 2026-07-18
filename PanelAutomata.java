import java.awt.*;
import java.util.List;
import javax.swing.*;

public class PanelAutomata extends JPanel {
    private Automata automata;
    private final int RADIO_NODO = 22;

    public PanelAutomata() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            "Representación Gráfica del Autómata",
            0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(100, 100, 100)
        ));
    }

    public void setAutomata(Automata automata) {
        this.automata = automata;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (automata == null) {
            g2.setColor(new Color(150, 150, 150));
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            FontMetrics fm = g2.getFontMetrics();
            String msg = "Escribe pseudocódigo, haz clic en 'Analizar' y selecciona una línea.";
            int w = fm.stringWidth(msg);
            g2.drawString(msg, (getWidth() - w) / 2, getHeight() / 2);
            return;
        }

        List<Estado> estados = automata.getEstados();
        List<Transicion> transiciones = automata.getTransiciones();
        List<Estado> caminoEstados = automata.getCaminoEstados();
        List<Transicion> caminoTransiciones = automata.getCaminoTransiciones();
        boolean esValida = automata.isEsValida();

        // 1. Dibujar Transiciones
        for (Transicion t : transiciones) {
            boolean visitada = caminoTransiciones.contains(t);
            dibujarTransicion(g2, t, visitada, esValida);
        }

        // Si la línea falló, podemos tener transiciones implícitas de error al estado 'qe' o 'se'/'ee'/'de'
        // Vamos a dibujarlas en color rojo si se recorrieron
        for (Transicion t : caminoTransiciones) {
            if (!transiciones.contains(t)) {
                // Es una transición implícita al estado de error
                dibujarTransicion(g2, t, true, false);
            }
        }

        // 2. Dibujar Estados
        for (Estado e : estados) {
            boolean visitado = caminoEstados.contains(e);
            dibujarEstado(g2, e, visitado, esValida);
        }
    }

    private void dibujarEstado(Graphics2D g2, Estado e, boolean visitado, boolean esValida) {
        int x = e.getX();
        int y = e.getY();

        // Determinar colores según estado de validación
        Color colorFondo = Color.WHITE;
        Color colorBorde = new Color(120, 120, 120);
        int grosorBorde = 2;

        if (visitado) {
            if (esValida) {
                // Camino exitoso (verde)
                colorFondo = new Color(220, 245, 220);
                colorBorde = new Color(46, 125, 50);
                grosorBorde = 3;
            } else {
                // Camino con error (rojo)
                colorFondo = new Color(255, 230, 230);
                colorBorde = new Color(198, 40, 40);
                grosorBorde = 3;
            }
        } else if (e.isEsError()) {
            // El nodo de error no visitado
            colorFondo = new Color(250, 250, 250);
            colorBorde = new Color(200, 150, 150);
        }

        // Dibujar círculo de fondo
        g2.setColor(colorFondo);
        g2.fillOval(x - RADIO_NODO, y - RADIO_NODO, RADIO_NODO * 2, RADIO_NODO * 2);

        // Dibujar borde
        g2.setColor(colorBorde);
        g2.setStroke(new BasicStroke(grosorBorde));
        g2.drawOval(x - RADIO_NODO, y - RADIO_NODO, RADIO_NODO * 2, RADIO_NODO * 2);

        // Si es de aceptación, dibujar doble círculo
        if (e.isEsAceptacion()) {
            g2.drawOval(x - RADIO_NODO + 4, y - RADIO_NODO + 4, (RADIO_NODO - 4) * 2, (RADIO_NODO - 4) * 2);
        }

        // Si es inicial, dibujar una flecha indicadora
        if (e.isEsInicial()) {
            g2.setColor(colorBorde);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(x - RADIO_NODO - 25, y, x - RADIO_NODO - 2, y);
            // Cabeza de flecha
            int[] xArrow = {x - RADIO_NODO - 8, x - RADIO_NODO - 2, x - RADIO_NODO - 8};
            int[] yArrow = {y - 5, y, y + 5};
            g2.fillPolygon(xArrow, yArrow, 3);
        }

        // Dibujar el nombre del estado
        g2.setColor(new Color(33, 33, 33));
        g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        FontMetrics fm = g2.getFontMetrics();
        String nombre = e.getNombre();
        int tx = x - fm.stringWidth(nombre) / 2;
        int ty = y + fm.getAscent() / 2 - 2;
        g2.drawString(nombre, tx, ty);
    }

    private void dibujarTransicion(Graphics2D g2, Transicion t, boolean visitada, boolean esValida) {
        Estado origen = t.getOrigen();
        Estado destino = t.getDestino();

        int x1 = origen.getX();
        int y1 = origen.getY();
        int x2 = destino.getX();
        int y2 = destino.getY();

        double dx = x2 - x1;
        double dy = y2 - y1;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist == 0) return;

        // Calcular puntos de inicio y fin en la superficie del círculo
        double ux = dx / dist;
        double uy = dy / dist;

        int sx = (int) (x1 + ux * RADIO_NODO);
        int sy = (int) (y1 + uy * RADIO_NODO);
        int ex = (int) (x2 - ux * RADIO_NODO);
        int ey = (int) (y2 - uy * RADIO_NODO);

        // Determinar colores y estilos
        Color colorLinea = new Color(180, 180, 180);
        int grosor = 1;

        if (visitada) {
            if (esValida) {
                colorLinea = new Color(46, 125, 50); // Verde
                grosor = 3;
            } else {
                colorLinea = new Color(198, 40, 40); // Rojo
                grosor = 3;
            }
        }

        g2.setColor(colorLinea);
        g2.setStroke(new BasicStroke(grosor));

        // Dibujar línea principal
        g2.drawLine(sx, sy, ex, ey);

        // Dibujar punta de la flecha
        double angulo = Math.atan2(ey - sy, ex - sx);
        int largoFlecha = 10;
        double anguloFlecha = 0.4; // radianes (aprox 23 grados)

        int ax1 = (int) (ex - largoFlecha * Math.cos(angulo - anguloFlecha));
        int ay1 = (int) (ey - largoFlecha * Math.sin(angulo - anguloFlecha));
        int ax2 = (int) (ex - largoFlecha * Math.cos(angulo + anguloFlecha));
        int ay2 = (int) (ey - largoFlecha * Math.sin(angulo + anguloFlecha));

        int[] xPoints = {ex, ax1, ax2};
        int[] yPoints = {ey, ay1, ay2};
        g2.fillPolygon(xPoints, yPoints, 3);

        // Dibujar texto de la transición
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();
        String etiqueta = t.getEtiqueta();
        int tx = (sx + ex) / 2 - fm.stringWidth(etiqueta) / 2;
        int ty = (sy + ey) / 2 - 6;

        // Dibujar fondo blanco detrás del texto para que no se superponga con la línea
        g2.setColor(Color.WHITE);
        g2.fillRect(tx - 2, ty - fm.getAscent() + 2, fm.stringWidth(etiqueta) + 4, fm.getHeight());

        // Dibujar texto
        g2.setColor(visitada ? colorLinea : new Color(80, 80, 80));
        g2.drawString(etiqueta, tx, ty);
    }
}
