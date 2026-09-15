package com.lucas.Gestor_Biblioteca.ui;

import tools.jackson.databind.JsonNode;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class PrestamoPanel extends JPanel {

    private final ApiClient apiClient = new ApiClient();
    private final DefaultTableModel tableModel;
    private final JTable table;

    public PrestamoPanel() {
        setLayout(new BorderLayout());

        String[] columnas = {"ID", "Libro", "Usuario", "F. Préstamo", "F. Prevista", "F. Real"};
        tableModel = new DefaultTableModel(columnas, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnNuevo = new JButton("Nuevo préstamo");
        JButton btnDevolver = new JButton("Devolver seleccionado");

        panelBotones.add(btnRefrescar);
        panelBotones.add(btnNuevo);
        panelBotones.add(btnDevolver);
        add(panelBotones, BorderLayout.SOUTH);

        btnRefrescar.addActionListener(e -> cargarPrestamos());
        btnNuevo.addActionListener(e -> mostrarFormularioNuevo());
        btnDevolver.addActionListener(e -> devolverSeleccionado());

        cargarPrestamos();
    }

    private void cargarPrestamos() {
        try {
            String json = apiClient.get("/prestamos");
            JsonNode prestamos = apiClient.getObjectMapper().readTree(json);

            tableModel.setRowCount(0);
            for (JsonNode prestamo : prestamos) {
                String tituloLibro = prestamo.path("libro").path("titulo").asString("(sin libro)");
                String nombreUsuario = prestamo.path("usuario").path("nombre").asString("(sin usuario)");

                tableModel.addRow(new Object[]{
                        prestamo.path("id").asLong(0),
                        tituloLibro,
                        nombreUsuario,
                        prestamo.path("fechaPrestamo").asString(""),
                        prestamo.path("fechaDevolucionPrevista").asString(""),
                        prestamo.path("fechaDevolucionReal").asString("(sin devolver)")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar préstamos: " + ex.getMessage());
        }
    }

    private void mostrarFormularioNuevo() {
        try {
            Map<String, Long> libros = cargarOpciones("/libros", "titulo");
            Map<String, Long> usuarios = cargarOpciones("/usuarios", "nombre");

            JComboBox<String> comboLibros = new JComboBox<>(libros.keySet().toArray(new String[0]));
            JComboBox<String> comboUsuarios = new JComboBox<>(usuarios.keySet().toArray(new String[0]));
            JTextField fechaPrevista = new JTextField("2026-09-30");

            JPanel formulario = new JPanel(new GridLayout(3, 2));
            formulario.add(new JLabel("Libro:"));
            formulario.add(comboLibros);
            formulario.add(new JLabel("Usuario:"));
            formulario.add(comboUsuarios);
            formulario.add(new JLabel("Fecha prevista devolución (AAAA-MM-DD):"));
            formulario.add(fechaPrevista);

            int resultado = JOptionPane.showConfirmDialog(this, formulario, "Nuevo préstamo",
                    JOptionPane.OK_CANCEL_OPTION);

            if (resultado == JOptionPane.OK_OPTION) {
                Long idLibro = libros.get(comboLibros.getSelectedItem());
                Long idUsuario = usuarios.get(comboUsuarios.getSelectedItem());

                String json = String.format(
                        "{\"libro\":{\"id\":%d},\"usuario\":{\"id\":%d},\"fechaDevolucionPrevista\":\"%s\"}",
                        idLibro, idUsuario, fechaPrevista.getText());

                apiClient.post("/prestamos", json);
                cargarPrestamos();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al crear préstamo: " + ex.getMessage());
        }
    }

    private Map<String, Long> cargarOpciones(String path, String campoNombre) throws Exception {
        String json = apiClient.get(path);
        JsonNode elementos = apiClient.getObjectMapper().readTree(json);

        Map<String, Long> opciones = new LinkedHashMap<>();
        for (JsonNode elemento : elementos) {
            String etiqueta = elemento.path(campoNombre).asString("(sin nombre)") + " (id " + elemento.path("id").asLong(0) + ")";
            opciones.put(etiqueta, elemento.path("id").asLong(0));
        }
        return opciones;
    }

    private void devolverSeleccionado() {
        int fila = table.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona primero un préstamo de la tabla");
            return;
        }
        long id = (long) tableModel.getValueAt(fila, 0);
        try {
            apiClient.put("/prestamos/" + id + "/devolver", "");
            cargarPrestamos();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al devolver: " + ex.getMessage());
        }
    }
}