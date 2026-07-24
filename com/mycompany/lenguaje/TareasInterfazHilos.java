package mycompany.lenguaje;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

public class TareasInterfazHilos {

    // ... (Otras tareas como Ejecutar o Guardar si existen) ...

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

                // PASO 4: Instanciar el Autómata
                Automata auto = new Automata(linea);

                // PASO 5: Crear el hilo y asignarle la tarea del autómata
                Thread hiloAutomata = new Thread(auto);

                // PASO 6: Iniciar el hilo (ejecuta el método run() de Automata en segundo plano)
                hiloAutomata.start();

                try {
                    // PASO 7: Esperar a que el hilo del autómata termine de analizar esta línea
                    hiloAutomata.join();
                } catch (InterruptedException e) {
                    // Manejo de interrupción del hilo si es necesario
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }

                // PASO 8: Procesar el resultado una vez que el hilo terminó
                if (auto.isEsValida()) {
                    nuevasLineas.add(linea);
                    elementosLista.add("Línea " + (i + 1) + " [Válida]: " + linea);
                } else {
                    elementosLista.add("Línea " + (i + 1) + " [Sintaxis Incorrecta]: " + linea);
                }
            }

            final List<String> lineasResultado = nuevasLineas;
            final List<String> elementosResultado = elementosLista;
            final boolean sinLineasValidas = nuevasLineas.isEmpty();

            // PASO 9: Enviar resultados a la GUI de forma segura (SwingUtilities.invokeLater)
            SwingUtilities.invokeLater(() -> {
                ventana.finalizarAnalisis(lineasResultado, elementosResultado, sinLineasValidas);
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
