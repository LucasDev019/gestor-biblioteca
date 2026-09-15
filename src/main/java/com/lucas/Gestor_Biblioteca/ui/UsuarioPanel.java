package com.lucas.Gestor_Biblioteca.ui;

import tools.jackson.databind.JsonNode;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class UsuarioPanel extends JPanel {

    private final ApiClient apiClient = new ApiClient();
    private final DefaultTableModel tableModel;
    private final JTable table;

    public UsuarioPanel() {
        setLayout(new BorderLayout());

        String[] columnas = {"ID", "Nombre", "Email"};
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

        btnRefrescar.addActionListener(e -> cargarUsuarios());
        btnAnadir.addActionListener(e -> mostrarFormularioAnadir());
        btnBorrar.addActionListener(e -> borrarSeleccionado());

        cargarUsuarios();
    }

    private void cargarUsuarios() {
        try {
            String json = apiClient.get("/usuarios");
            JsonNode usuarios = apiClient.getObjectMapper().readTree(json);

            tableModel.setRowCount(0);
            for (JsonNode usuario : usuarios) {
                tableModel.addRow(new Object[]{
                        usuario.path("id").asLong(0),
                        usuario.path("nombre").asString(""),
                        usuario.path("email").asString("")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + ex.getMessage());
        }
    }

    private void mostrarFormularioAnadir() {
        JTextField nombre = new JTextField();
        JTextField email = new JTextField();

        JPanel formulario = new JPanel(new GridLayout(2, 2));
        formulario.add(new JLabel("Nombre:"));
        formulario.add(nombre);
        formulario.add(new JLabel("Email:"));
        formulario.add(email);

        int resultado = JOptionPane.showConfirmDialog(this, formulario, "Nuevo usuario",
                JOptionPane.OK_CANCEL_OPTION);

        if (resultado == JOptionPane.OK_OPTION) {
            try {
                String json = String.format(
                        "{\"nombre\":\"%s\",\"email\":\"%s\"}",
                        nombre.getText(), email.getText());
                apiClient.post("/usuarios", json);
                cargarUsuarios();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al crear usuario: " + ex.getMessage());
            }
        }
    }

    private void borrarSeleccionado() {
        int fila = table.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona primero un usuario de la tabla");
            return;
        }
        long id = (long) tableModel.getValueAt(fila, 0);
        try {
            apiClient.delete("/usuarios/" + id);
            cargarUsuarios();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al borrar: " + ex.getMessage());
        }
    }
}