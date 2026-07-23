import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class Transicion {

    private final Estado origen;
    private final Estado destino;
    private final String etiqueta;

    public Transicion(Estado origen, Estado destino, String etiqueta) {
        this.origen = origen;
        this.destino = destino;
        this.etiqueta = etiqueta;
    }

    public void dibujado(Graphics2D g2d, boolean visitada, boolean esValida) {
        int x1 = origen.getX();
        int y1 = origen.getY();
        int x2 = destino.getX();
        int y2 = destino.getY();

        double dx = x2 - x1;
        double dy = y2 - y1;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist == 0) {
            return;
        }

        double ux = dx / dist;
        double uy = dy / dist;
        int sx = (int) (x1 + ux * Estado.RADIO_NODO);
        int sy = (int) (y1 + uy * Estado.RADIO_NODO);
        int ex = (int) (x2 - ux * Estado.RADIO_NODO);
        int ey = (int) (y2 - uy * Estado.RADIO_NODO);

        Color colorLinea = new Color(180, 180, 180);
        int grosor = 1;
        if (visitada) {
            colorLinea = esValida ? new Color(46, 125, 50) : new Color(198, 40, 40);
            grosor = 3;
        }

        g2d.setColor(colorLinea);
        g2d.setStroke(new BasicStroke(grosor));
        g2d.drawLine(sx, sy, ex, ey);

        double angulo = Math.atan2(ey - sy, ex - sx);
        int largoFlecha = 10;
        double anguloFlecha = 0.4;
        int ax1 = (int) (ex - largoFlecha * Math.cos(angulo - anguloFlecha));
        int ay1 = (int) (ey - largoFlecha * Math.sin(angulo - anguloFlecha));
        int ax2 = (int) (ex - largoFlecha * Math.cos(angulo + anguloFlecha));
        int ay2 = (int) (ey - largoFlecha * Math.sin(angulo + anguloFlecha));
        g2d.fillPolygon(new int[] { ex, ax1, ax2 }, new int[] { ey, ay1, ay2 }, 3);

        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        FontMetrics fm = g2d.getFontMetrics();
        int tx = (sx + ex) / 2 - fm.stringWidth(etiqueta) / 2;
        int ty = (sy + ey) / 2 - 6;

        g2d.setColor(Color.WHITE);
        g2d.fillRect(tx - 2, ty - fm.getAscent() + 2, fm.stringWidth(etiqueta) + 4, fm.getHeight());

        g2d.setColor(visitada ? colorLinea : new Color(80, 80, 80));
        g2d.drawString(etiqueta, tx, ty);
    }

    public Estado getOrigen() {
        return origen;
    }

    public Estado getDestino() {
        return destino;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return origen.getNombre() + " --[" + etiqueta + "]--> " + destino.getNombre();
    }
}
