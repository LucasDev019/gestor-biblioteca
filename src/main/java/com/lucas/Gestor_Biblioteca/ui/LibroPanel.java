
package com.lucas.Gestor_Biblioteca.ui;


import tools.jackson.databind.JsonNode;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LibroPanel extends JPanel {

    private final ApiClient apiClient = new ApiClient();
    private final DefaultTableModel tableModel;
    private final JTable table;

    public LibroPanel() {
        setLayout(new BorderLayout());

        String[] columnas = {"ID", "Título", "Autor", "ISBN", "Ejemplares"};
        tableModel = new DefaultTableModel(columnas, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnAnadir = new JButton("Añadir");
        JButton btnBorrar = new JButton("Borrar seleccionado");

        panelBotones.add(btnRefrescar);
        panelBotones.add(btnAnadir);
        panelBotones.add(btnBorrar);
        add(panelBotones, BorderLayout.SOUTH);

        btnRefrescar.addActionListener(e -> cargarLibros());
        btnAnadir.addActionListener(e -> mostrarFormularioAnadir());
        btnBorrar.addActionListener(e -> borrarSeleccionado());

        cargarLibros();
    }

    private void cargarLibros() {
        try {
            String json = apiClient.get("/libros");
            JsonNode libros = apiClient.getObjectMapper().readTree(json);

            tableModel.setRowCount(0);
            for (JsonNode libro : libros) {
                tableModel.addRow(new Object[]{
                        libro.path("id").asLong(),
                        libro.path("titulo").asString(""),
                        libro.path("autor").asString(""),
                        libro.path("ISBN").asString(""),
                        libro.path("ejemplaresDisponibles").asInt()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar libros: " + ex.getMessage());
        }
    }

    private void mostrarFormularioAnadir() {
        JTextField titulo = new JTextField();
        JTextField autor = new JTextField();
        JTextField isbn = new JTextField();
        JTextField ejemplares = new JTextField();

        JPanel formulario = new JPanel(new GridLayout(4, 2));
        formulario.add(new JLabel("Título:"));
        formulario.add(titulo);
        formulario.add(new JLabel("Autor:"));
        formulario.add(autor);
        formulario.add(new JLabel("ISBN:"));
        formulario.add(isbn);
        formulario.add(new JLabel("Ejemplares:"));
        formulario.add(ejemplares);

        int resultado = JOptionPane.showConfirmDialog(this, formulario, "Nuevo libro",
                JOptionPane.OK_CANCEL_OPTION);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                String json = String.format(
                        "{\"titulo\":\"%s\",\"autor\":\"%s\",\"ISBN\":\"%s\",\"ejemplaresDisponibles\":%s}",
                        titulo.getText(), autor.getText(), isbn.getText(), ejemplares.getText());
                apiClient.post("/libros", json);
                cargarLibros();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al crear libro: " + ex.getMessage());
            }
        }
    }

    private void borrarSeleccionado() {
        int fila = table.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona primero un libro de la tabla");
            return;
        }
        long id = (long) tableModel.getValueAt(fila, 0);
        try {
            apiClient.delete("/libros/" + id);
            cargarLibros();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al borrar: " + ex.getMessage());
        }
    }
}