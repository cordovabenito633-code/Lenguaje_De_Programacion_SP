package mycompany.lenguaje;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class Estado {
    public static final int RADIO_NODO = 22;
    private final String nombre;
    private final boolean esInicial;
    private final boolean esAceptacion;
    private final boolean esError;
    private int x;
    private int y;

    public Estado(String nombre, boolean esInicial, boolean esAceptacion, boolean esError, int x, int y) {
        this.nombre = nombre;
        this.esInicial = esInicial;
        this.esAceptacion = esAceptacion;
        this.esError = esError;
        this.x = x;
        this.y = y;
    }
    public void dibujado(Graphics2D g2d, boolean visitado, boolean esValida) {
        Color colorFondo = Color.WHITE;
        Color colorBorde = new Color(120, 120, 120);
        int grosorBorde = 2;

        if (visitado) {
            if (esValida) {
                colorFondo = new Color(220, 245, 220);
                colorBorde = new Color(46, 125, 50);
                grosorBorde = 3;
            } else {
                colorFondo = new Color(255, 230, 230);
                colorBorde = new Color(198, 40, 40);
                grosorBorde = 3;
            }
        } else if (esError) {
            colorFondo = new Color(250, 250, 250);
            colorBorde = new Color(200, 150, 150);
        }

        g2d.setColor(colorFondo);
        g2d.fillOval(x - RADIO_NODO, y - RADIO_NODO, RADIO_NODO * 2, RADIO_NODO * 2);

        g2d.setColor(colorBorde);
        g2d.setStroke(new BasicStroke(grosorBorde));
        g2d.drawOval(x - RADIO_NODO, y - RADIO_NODO, RADIO_NODO * 2, RADIO_NODO * 2);

        if (esAceptacion) {
            g2d.drawOval(x - RADIO_NODO + 4, y - RADIO_NODO + 4, (RADIO_NODO - 4) * 2, (RADIO_NODO - 4) * 2);
        }

        if (esInicial) {
            g2d.setColor(colorBorde);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x - RADIO_NODO - 25, y, x - RADIO_NODO - 2, y);
            int[] xArrow = {x - RADIO_NODO - 8, x - RADIO_NODO - 2, x - RADIO_NODO - 8};
            int[] yArrow = {y - 5, y, y + 5};
            g2d.fillPolygon(xArrow, yArrow, 3);
        }

        g2d.setColor(new Color(33, 33, 33));
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
        FontMetrics fm = g2d.getFontMetrics();
        int tx = x - fm.stringWidth(nombre) / 2;
        int ty = y + fm.getAscent() / 2 - 2;
        g2d.drawString(nombre, tx, ty);
    }
    public String getNombre() {
        return nombre;
    }
    public boolean isEsInicial() {
        return esInicial;
    }
    public boolean isEsError() { 
        return esError; 
    }
    public boolean isEsAceptacion() {
        return esAceptacion;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
    @Override
    public String toString() {
        return nombre + (esAceptacion ? " (Aceptación)" : "") + (esError ? " (Error)" : "");
    }
    
}
