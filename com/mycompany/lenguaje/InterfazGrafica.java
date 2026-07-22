package mycompany.lenguaje;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class InterfazGrafica extends JFrame implements ActionListener {
    private JList<String> lista;
    private DefaultListModel<String> modeloLista;
    private JTextArea areaTexto;
    private Map<String, Object> memoriaVariables = new HashMap<>();
    private JButton btnAnalizar, btnEjecutar, btnLimpiar, btnGuardar;
    private PanelAutomata panelAutomata; // Panel para mostrar el automata graficado
    private List<String> lineasAnalizadas; // Líneas que se muestran en el JList
    private Automata automataActual; // Automata construido por la línea seleccionada

    public InterfazGrafica() {
        setTitle("Lenguaje en español - Analizador de Pseudocódigo");
        setSize(850, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        modeloLista = new DefaultListModel<>();

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(245, 245, 245));

        // ------------------- TÍTULO -----------------------
        JLabel titulo = new JLabel("Procesamiento de Datos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setBounds(25, 15, 350, 35);
        panel.add(titulo);

        // ------------------- JLIST ------------------------
        lista = new JList<>(modeloLista);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Cuando el usuario cambia de selección se actualiza el panel del autómata
        lista.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarAutomataSeleccionado();
            }
        });

        JScrollPane scrollLista = new JScrollPane(lista);
        scrollLista.setBounds(30, 80, 160, 220);
        panel.add(scrollLista);

        // ------------------- BOTONES ----------------------
        btnAnalizar = new JButton("Analizar");
        btnAnalizar.setBounds(210, 80, 140, 40);
        btnAnalizar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAnalizar.setFocusPainted(false);
        btnAnalizar.addActionListener(this);
        panel.add(btnAnalizar);

        btnEjecutar = new JButton("Ejecutar");
        btnEjecutar.setBounds(210, 140, 140, 40);
        btnEjecutar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEjecutar.setFocusPainted(false);
        btnEjecutar.addActionListener(this);
        panel.add(btnEjecutar);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(210, 200, 140, 40);
        btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.addActionListener(this);
        panel.add(btnLimpiar);

        btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(210, 260, 140, 40);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnGuardar.setFocusPainted(false);
        btnGuardar.addActionListener(this);
        panel.add(btnGuardar);

        // ------------------ TEXT AREA ---------------------
        areaTexto = new JTextArea();
        areaTexto.setEditable(true);
        areaTexto.setFont(new Font("Consolas", Font.PLAIN, 15));

        JScrollPane scrollArea = new JScrollPane(areaTexto);
        scrollArea.setBounds(410, 20, 400, 280);
        panel.add(scrollArea);

        // Se crea el panel del automata y se agrega a la interfaz
        panelAutomata = new PanelAutomata();
        panelAutomata.setBounds(30, 320, 780, 330);
        panel.add(panelAutomata);

        lineasAnalizadas = new ArrayList<>();
        automataActual = null;

        add(panel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAnalizar) {
            accionAnalizar();
        } else if (e.getSource() == btnEjecutar) {
            accionEjecutar();
        } else if (e.getSource() == btnLimpiar) {
            accionLimpiar();
        } else if (e.getSource() == btnGuardar) {
            accionGuardar();
        }
    }

    // ----------------------------------------------------
    //              ACCIONES DE LOS BOTONES
    // ----------------------------------------------------

    private void accionAnalizar() {
        String texto = areaTexto.getText().trim();
        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escribe al menos una línea antes de analizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Limpiamos las lineas del texto anterior para analizar el nuevo
        modeloLista.clear();
        lineasAnalizadas.clear();
        automataActual = null;

        String[] lineas = texto.split("\\r?\\n");
        for (int i = 0; i < lineas.length; i++) {
            String linea = lineas[i].trim();
            if (linea.isEmpty()) {
                continue;
            }
            // Guardamos solo la línea; el automata se crea cuando se selecciona
            lineasAnalizadas.add(linea);
            modeloLista.addElement("Línea " + (i + 1) + ": " + linea);
        }

        if (lineasAnalizadas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay líneas válidas para analizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            panelAutomata.setAutomata(null);
            return;
        }

        // Seleccionamos la primera línea para que se construya y muestre su automata
        lista.setSelectedIndex(0);
    }

    private void accionLimpiar() {
        areaTexto.setText("");
        modeloLista.clear();
        panelAutomata.setAutomata(null);
    }

    private void actualizarAutomataSeleccionado() {
        // Construye y muestra el autómata
        int index = lista.getSelectedIndex();
        if (index >= 0 && index < lineasAnalizadas.size()) {
            String linea = lineasAnalizadas.get(index);
            automataActual = new Automata(linea); // crear aquí el objeto Automata
            panelAutomata.setAutomata(automataActual);
        } else {
            panelAutomata.setAutomata(null);
        }
    }
    // ----------------------------------------------------
    //    EJECUCIÓN LÍNEA POR LÍNEA DEL ÁREA DE TEXTO
    // ----------------------------------------------------

    private void accionEjecutar() {
        String contenidoCompleto = areaTexto.getText().trim();
        if (contenidoCompleto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El área de texto está vacía.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        memoriaVariables.clear(); // Limpiamos la memoria antes de ejecutar el script
        StringBuilder consola = new StringBuilder();

        // 1. DIVIDIMOS EL TEXTO DEL JTEXTAREA POR LÍNEAS CON .split()
        String[] lineas = contenidoCompleto.split("\\r?\\n");
        int numeroLinea = 1;

        // 2. RECORREMOS Y PROCESAMOS LÍNEA POR LÍNEA
        for (String linea : lineas) {
            linea = linea.trim();

            // Ignorar líneas vacías o comentarios que empiecen por //
            if (linea.isEmpty() || linea.startsWith("//")) {
                numeroLinea++;
                continue;
            }

            try {
                procesarLinea(linea, consola);
            } catch (Exception ex) {
                consola.append("[Error en Línea ").append(numeroLinea).append("]: ").append(ex.getMessage()).append("\n");
            }
            numeroLinea++;
        }

        if (consola.length() == 0) {
            consola.append("Ejecución finalizada con éxito.");
        }

        // Muestra el resultado de los 'imprimir' en una ventana emergente de consola
        JTextArea areaConsola = new JTextArea(consola.toString());
        areaConsola.setEditable(false);
        areaConsola.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane scroll = new JScrollPane(areaConsola);
        scroll.setPreferredSize(new Dimension(450, 250));

        JOptionPane.showMessageDialog(this, scroll, "Resultado de la Ejecución", JOptionPane.PLAIN_MESSAGE);
    }

    private void procesarLinea(String linea, StringBuilder consola) throws Exception {
        // CASO A: Comando IMPRIMIR o MOSTRAR
        if (linea.toLowerCase().startsWith("imprimir") || linea.toLowerCase().startsWith("mostrar")) {
            String contenido = linea.replaceFirst("(?i)^(imprimir|mostrar)\\s*", "").trim();

            if (contenido.startsWith("(") && contenido.endsWith(")")) {
                contenido = contenido.substring(1, contenido.length() - 1).trim();
            }

            Object resultado = evaluarExpresion(contenido);
            consola.append(formatearResultado(resultado)).append("\n");
        }
        // CASO B: ASIGNACIÓN DE VARIABLES CON OPERACIONES (Ej: x = 10 + 5  o  Cadena s = "hola")
        else if (linea.contains("=")) {
            String[] partes = linea.split("=", 2);
            String izq = partes[0].trim();
            String der = partes[1].replace(";", "").trim();

            // Extraer el nombre de la variable (soporta 'Numero x', 'Cadena s' o solo 'x')
            String[] palabrasIzq = izq.split("\\s+");
            String nombreVar = palabrasIzq[palabrasIzq.length - 1];

            // Evaluamos lo que hay a la derecha del '='
            Object valorEvaluado = evaluarExpresion(der);
            memoriaVariables.put(nombreVar, valorEvaluado);
        } else {
            throw new Exception("Línea no reconocida: " + linea);
        }
    }

    // Evalúa si lo que se recibe es un texto entre comillas, una variable o una operación matemática
    private Object evaluarExpresion(String expr) throws Exception {
        expr = expr.trim();

        // Si es una cadena literal entre comillas "hola"
        if ((expr.startsWith("\"") && expr.endsWith("\"")) || (expr.startsWith("'") && expr.endsWith("'"))) {
            return expr.substring(1, expr.length() - 1);
        }

        // Reemplazamos los nombres de variables por sus valores actuales en la memoria
        for (Map.Entry<String, Object> entry : memoriaVariables.entrySet()) {
            String var = entry.getKey();
            Object val = entry.getValue();
            expr = expr.replaceAll("\\b" + var + "\\b", val.toString());
        }

        // Si después de reemplazar variables queda una cadena entre comillas
        if ((expr.startsWith("\"") && expr.endsWith("\"")) || (expr.startsWith("'") && expr.endsWith("'"))) {
            return expr.substring(1, expr.length() - 1);
        }

        // Intentar calcular si es una operación aritmética (ej: 10 + 20)
        try {
            return evaluarAritmetica(expr);
        } catch (Exception e) {
            // Si la variable simple existe pero no es numérica
            if (memoriaVariables.containsKey(expr)) {
                return memoriaVariables.get(expr);
            }
            return expr;
        }
    }

    // Evaluador de operaciones aritméticas (+, -, *, /) con prioridades
    private double evaluarAritmetica(String expr) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expr.length()) ? expr.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expr.length()) throw new RuntimeException("Carácter inesperado: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expr.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Factor no válido");
                }
                return x;
            }
        }.parse();
    }

    private String formatearResultado(Object obj) {
        if (obj instanceof Double) {
            double d = (Double) obj;
            if (d == (long) d) {
                return String.format("%d", (long) d); // Muestra enteros sin decimales feos (ej: 15 en vez de 15.0)
            }
        }
        return obj.toString();
    }

    private void accionGuardar() {
        JFileChooser chooser = new JFileChooser();
        int r = chooser.showSaveDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try (BufferedWriter w = new BufferedWriter(new FileWriter(f))) {
                w.write(areaTexto.getText());
                JOptionPane.showMessageDialog(this, "Pseudocódigo guardado correctamente.", "Guardar",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args){
        InterfazGrafica v = new InterfazGrafica();
        v.setVisible(true);
    }
}