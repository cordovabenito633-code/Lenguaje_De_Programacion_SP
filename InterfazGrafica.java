import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class InterfazGrafica extends JFrame implements ActionListener {
    private JList<String> lista;
    private DefaultListModel<String> modeloLista;
    private JTextArea areaTexto;
    private JButton btnAnalizar, btnEjecutar, btnLimpiar, btnGuardar;

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
    }

    private void accionEjecutar() {
    }

    private void accionLimpiar() {
        areaTexto.setText("");
        modeloLista.clear();
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

    public static void main(String[] args) {
        InterfazGrafica v = new InterfazGrafica();
        v.setVisible(true);
    }
}