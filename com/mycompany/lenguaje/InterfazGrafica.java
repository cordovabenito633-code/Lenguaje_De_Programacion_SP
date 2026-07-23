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

        memoriaVariables.clear();
        StringBuilder consola = new StringBuilder();

        String[] lineas = contenidoCompleto.split("\\r?\\n");

        try {
            ejecutarBloque(lineas, 0, lineas.length, consola);
        } catch (Exception ex) {
            consola.append("[Error de Ejecución]: ").append(ex.getMessage()).append("\n");
        }

        if (consola.length() == 0) {
            consola.append("Ejecución finalizada con éxito.");
        }

        // LLAMADA A LA VENTANA ESTÉTICA DE RESULTADOS
        mostrarConsolaEstetico(consola.toString());
    }

    // Ventana Emergente Personalizada y Estética
    private void mostrarConsolaEstetico(String textoResultado) {
        JDialog dialog = new JDialog(this, "Resultado de la Ejecución", true);
        dialog.setSize(480, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        // -----------------------------------------------------------
        // A. ENCABEZADO MODERNO
        // -----------------------------------------------------------
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(new Color(30, 32, 44)); // Tema oscuro elegante
        panelHeader.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        JLabel lblTitulo = new JLabel("Salida de Ejecución");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Ejecución completada con éxito");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(160, 170, 200));

        JPanel panelTitulos = new JPanel(new GridLayout(2, 1, 0, 4));
        panelTitulos.setOpaque(false);
        panelTitulos.add(lblTitulo);
        panelTitulos.add(lblSubtitulo);

        panelHeader.add(panelTitulos, BorderLayout.CENTER);

        // -----------------------------------------------------------
        // B. CUERPO PRINCIPAL (TARJETA CENTRADA)
        // -----------------------------------------------------------
        JPanel panelCentro = new JPanel(new GridBagLayout());
        panelCentro.setBackground(new Color(243, 244, 246)); // Fondo gris suave

        String textoLimpio = textoResultado.trim();
        boolean esResultadoUnico = textoLimpio.split("\n").length == 1;

        if (esResultadoUnico) {
            // TARJETA TIPO "DASHBOARD" PARA UN SOLO VALOR (ej. 5, "Hola", etc.)
            JPanel tarjeta = new JPanel(new BorderLayout());
            tarjeta.setBackground(Color.WHITE);
            tarjeta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(225, 228, 232), 1, true),
                    BorderFactory.createEmptyBorder(20, 40, 20, 40)
            ));

            JLabel lblValor = new JLabel(textoLimpio, SwingConstants.CENTER);
            lblValor.setFont(new Font("Segoe UI", Font.BOLD, 42)); // Número/Texto Gigante
            lblValor.setForeground(new Color(79, 70, 229)); // Color Índigo Moderno

            JLabel lblTag = new JLabel("VALOR RETORNADO", SwingConstants.CENTER);
            lblTag.setFont(new Font("Segoe UI", Font.BOLD, 10));
            lblTag.setForeground(new Color(156, 163, 175));

            tarjeta.add(lblTag, BorderLayout.NORTH);
            tarjeta.add(lblValor, BorderLayout.CENTER);

            panelCentro.add(tarjeta);
        } else {
            // VISTA TIPO CONSOLA PARA MÚLTIPLES LÍNEAS DE SALIDA
            JTextArea areaConsola = new JTextArea(textoResultado);
            areaConsola.setEditable(false);
            areaConsola.setFont(new Font("Consolas", Font.PLAIN, 14));
            areaConsola.setBackground(Color.WHITE);
            areaConsola.setForeground(new Color(31, 41, 55));
            areaConsola.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JScrollPane scroll = new JScrollPane(areaConsola);
            scroll.setPreferredSize(new Dimension(400, 180));
            scroll.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));

            panelCentro.add(scroll);
        }

        // -----------------------------------------------------------
        // C. PIE DE PÁGINA Y BOTÓN
        // -----------------------------------------------------------
        JPanel panelFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 14));
        panelFooter.setBackground(Color.WHITE);
        panelFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(229, 231, 235)));

        JButton btnCerrar = new JButton("Aceptar");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCerrar.setBackground(new Color(79, 70, 229));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        btnCerrar.addActionListener(e -> dialog.dispose());

        panelFooter.add(btnCerrar);

        dialog.add(panelHeader, BorderLayout.NORTH);
        dialog.add(panelCentro, BorderLayout.CENTER);
        dialog.add(panelFooter, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
    // SOLUCIÓN AL ERROR 1: Método maestro para bloques recursivos
    private int ejecutarBloque(String[] lineas, int inicio, int fin, StringBuilder consola) throws Exception {
        int i = inicio;
        while (i < fin) {
            String linea = lineas[i].trim();

            if (linea.isEmpty() || linea.startsWith("//") || linea.equals("}")) {
                i++;
                continue;
            }

            if (linea.toLowerCase().startsWith("si")) {
                i = ejecutarSiSino(lineas, i, consola);
            } else if (linea.toLowerCase().startsWith("mientras")) {
                i = ejecutarMientras(lineas, i, consola);
            } else {
                procesarInstruccionSimple(linea, consola);
                i++;
            }
        }
        return i;
    }

    // EVALUACIÓN DE EXPRESIONES (Cadenas, Variables, Operaciones)
    private Object evaluarExpresion(String expr) throws Exception {
        expr = expr.trim();

        if ((expr.startsWith("\"") && expr.endsWith("\"")) || (expr.startsWith("'") && expr.endsWith("'"))) {
            return expr.substring(1, expr.length() - 1);
        }

        for (Map.Entry<String, Object> entry : memoriaVariables.entrySet()) {
            String var = entry.getKey();
            Object val = entry.getValue();
            expr = expr.replaceAll("\\b" + var + "\\b", val.toString());
        }

        if ((expr.startsWith("\"") && expr.endsWith("\"")) || (expr.startsWith("'") && expr.endsWith("'"))) {
            return expr.substring(1, expr.length() - 1);
        }

        try {
            return evaluarAritmetica(expr);
        } catch (Exception e) {
            if (memoriaVariables.containsKey(expr)) {
                return memoriaVariables.get(expr);
            }
            return expr;
        }
    }

    // ESTRUCTURA Si / Sino
    private int ejecutarSiSino(String[] lineas, int indiceActual, StringBuilder consola) throws Exception {
        String lineaHead = lineas[indiceActual].trim();
        String condicionStr = extraeEntreParentesis(lineaHead);
        boolean condicion = evaluarCondicionBooleana(condicionStr);

        int finBloqueSi = buscarFinBloque(lineas, indiceActual);
        int siguienteLinea = finBloqueSi + 1;

        boolean tieneSino = (siguienteLinea < lineas.length) && lineas[siguienteLinea].trim().toLowerCase().startsWith("sino");
        int finBloqueSino = tieneSino ? buscarFinBloque(lineas, siguienteLinea) : finBloqueSi;

        if (condicion) {
            ejecutarBloque(lineas, indiceActual + 1, finBloqueSi, consola);
        } else if (tieneSino) {
            ejecutarBloque(lineas, siguienteLinea + 1, finBloqueSino, consola);
        }

        return tieneSino ? finBloqueSino + 1 : finBloqueSi + 1;
    }

    // ESTRUCTURA Mientras
    private int ejecutarMientras(String[] lineas, int indiceActual, StringBuilder consola) throws Exception {
        String lineaHead = lineas[indiceActual].trim();
        String condicionStr = extraeEntreParentesis(lineaHead);
        int finBloque = buscarFinBloque(lineas, indiceActual);

        while (evaluarCondicionBooleana(condicionStr)) {
            ejecutarBloque(lineas, indiceActual + 1, finBloque, consola);
        }
        return finBloque + 1;
    }

    // ASIGNACIONES E IMPRESIONES
    private void procesarInstruccionSimple(String linea, StringBuilder consola) throws Exception {
        if (linea.toLowerCase().startsWith("imprimir") || linea.toLowerCase().startsWith("mostrar")) {
            String contenido = linea.replaceFirst("(?i)^(imprimir|mostrar)\\s*", "").trim();
            if (contenido.startsWith("(") && contenido.endsWith(")")) {
                contenido = contenido.substring(1, contenido.length() - 1).trim();
            }
            Object res = evaluarExpresion(contenido);
            consola.append(formatearResultado(res)).append("\n"); // SOLUCIÓN AL ERROR 3
        } else if (linea.contains("=")) {
            String[] partes = linea.split("=", 2);
            String izq = partes[0].trim();
            String der = partes[1].replace(";", "").trim();

            String[] palabrasIzq = izq.split("\\s+");
            String nombreVar = palabrasIzq[palabrasIzq.length - 1];

            Object valorEvaluado = evaluarExpresion(der);
            memoriaVariables.put(nombreVar, valorEvaluado);
        }
    }

    // CONDICIONES BOOLEANAS
    private boolean evaluarCondicionBooleana(String expr) throws Exception {
        for (Map.Entry<String, Object> entry : memoriaVariables.entrySet()) {
            expr = expr.replaceAll("\\b" + entry.getKey() + "\\b", entry.getValue().toString());
        }

        expr = expr.replaceAll("(?i)\\bverdadero\\b", "true").replaceAll("(?i)\\bfalso\\b", "false");

        if (expr.contains(">=")) {
            String[] p = expr.split(">=");
            return Double.parseDouble(evaluarExpresion(p[0]).toString()) >= Double.parseDouble(evaluarExpresion(p[1]).toString());
        } else if (expr.contains("<=")) {
            String[] p = expr.split("<=");
            return Double.parseDouble(evaluarExpresion(p[0]).toString()) <= Double.parseDouble(evaluarExpresion(p[1]).toString());
        } else if (expr.contains(">")) {
            String[] p = expr.split(">");
            return Double.parseDouble(evaluarExpresion(p[0]).toString()) > Double.parseDouble(evaluarExpresion(p[1]).toString());
        } else if (expr.contains("<")) {
            String[] p = expr.split("<");
            return Double.parseDouble(evaluarExpresion(p[0]).toString()) < Double.parseDouble(evaluarExpresion(p[1]).toString());
        } else if (expr.contains("==")) {
            String[] p = expr.split("==");
            return evaluarExpresion(p[0]).toString().equals(evaluarExpresion(p[1]).toString());
        } else if (expr.contains("!=")) {
            String[] p = expr.split("!=");
            return !evaluarExpresion(p[0]).toString().equals(evaluarExpresion(p[1]).toString());
        }

        return Boolean.parseBoolean(expr.trim());
    }

    private String extraeEntreParentesis(String texto) {
        int i = texto.indexOf("(");
        int f = texto.lastIndexOf(")");
        return (i != -1 && f != -1) ? texto.substring(i + 1, f) : texto;
    }

    private int buscarFinBloque(String[] lineas, int inicio) {
        for (int i = inicio + 1; i < lineas.length; i++) {
            if (lineas[i].trim().equals("}")) {
                return i;
            }
        }
        return lineas.length;
    }

    // PARSER MATEMÁTICO
    private double evaluarAritmetica(String expr) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expr.length()) ? expr.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') {
                    nextChar();
                }
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expr.length()) {
                    throw new RuntimeException("Carácter inesperado: " + (char) ch);
                }
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) {
                        x += parseTerm();
                    } else if (eat('-')) {
                        x -= parseTerm();
                    } else {
                        return x;
                    }
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) {
                        x *= parseFactor();
                    } else if (eat('/')) {
                        x /= parseFactor();
                    } else {
                        return x;
                    }
                }
            }

            double parseFactor() {
                if (eat('+')) {
                    return +parseFactor();
                }
                if (eat('-')) {
                    return -parseFactor();
                }

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') {
                        nextChar();
                    }
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
                return String.format("%d", (long) d);
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