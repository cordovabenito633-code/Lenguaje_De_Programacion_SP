import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

public class TareasInterfazHilos {

    public static class Analizar implements Runnable {
        private final InterfazGrafica ventana;
        private final String texto;

        public Analizar(InterfazGrafica ventana, String texto) {
            this.ventana = ventana;
            this.texto = texto;
        }

        @Override
        public void run() {
            List<String> nuevasLineas = new ArrayList<>();
            List<String> elementosLista = new ArrayList<>();

            String[] lineas = texto.split("\\r?\\n");
            for (int i = 0; i < lineas.length; i++) {
                String linea = lineas[i].trim();
                if (linea.isEmpty()) {
                    continue;
                }
                nuevasLineas.add(linea);
                elementosLista.add("Línea " + (i + 1) + ": " + linea);
            }

            final List<String> lineasResultado = nuevasLineas;
            final List<String> elementosResultado = elementosLista;
            final boolean sinLineasValidas = nuevasLineas.isEmpty();

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ventana.finalizarAnalisis(lineasResultado, elementosResultado, sinLineasValidas);
                }
            });
        }
    }

    public static class Ejecutar implements Runnable {
        private final InterfazGrafica ventana;
        private final String contenido;

        public Ejecutar(InterfazGrafica ventana, String contenido) {
            this.ventana = ventana;
            this.contenido = contenido;
        }

        @Override
        public void run() {
            final String resultado = ventana.ejecutarPseudocodigo(contenido);

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ventana.finalizarEjecucion(resultado);
                }
            });
        }
    }

    public static class Guardar implements Runnable {
        private final InterfazGrafica ventana;
        private final File archivo;
        private final String contenido;

        public Guardar(InterfazGrafica ventana, File archivo, String contenido) {
            this.ventana = ventana;
            this.archivo = archivo;
            this.contenido = contenido;
        }

        @Override
        public void run() {
            String error = null;
            try (BufferedWriter w = new BufferedWriter(new FileWriter(archivo))) {
                w.write(contenido);
            } catch (IOException ex) {
                error = ex.getMessage();
            }

            final String errorFinal = error;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ventana.finalizarGuardado(errorFinal);
                }
            });
        }
    }
}
