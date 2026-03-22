package com.mycompany.appinventario.ui;

import com.mycompany.appinventario.dao.RegistroMaterialTareaDAO;
import com.mycompany.appinventario.dao.TareaDAO;
import com.mycompany.appinventario.model.EstadoTarea;
import com.mycompany.appinventario.model.RegistroMaterialTarea;
import com.mycompany.appinventario.model.Tarea;
import com.mycompany.appinventario.model.Usuario;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTabbedPane;

public class ColaboradorDashboard extends JFrame {

    private final Usuario colaboradorActual;
    private final TareaDAO tareaDAO;
    private final RegistroMaterialTareaDAO registroMaterialTareaDAO;

    private final DefaultTableModel modeloTareas;
    private final DefaultTableModel modeloMateriales;

    private final JTable tablaTareas;
    private final JTable tablaMateriales;

    private final JTextArea txtDescripcionTrabajo;
    private final JComboBox<EstadoTarea> cmbEstado;

    private final JTextField txtCodigoMaterial;
    private final JTextField txtNombreMaterial;
    private final JTextField txtFechaIngresoMaterial;
    private final JTextField txtDefectosMaterial;
    private final JTextField txtProveedorMaterial;
    private final JTextField txtCantidadMaterial;

    public ColaboradorDashboard(Usuario colaboradorActual) {
        this.colaboradorActual = colaboradorActual;
        this.tareaDAO = new TareaDAO();
        this.registroMaterialTareaDAO = new RegistroMaterialTareaDAO();

        this.modeloTareas = new DefaultTableModel(new String[]{"ID", "Titulo", "Estado", "Fecha entrega", "Descripcion"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.modeloMateriales = new DefaultTableModel(new String[]{"ID", "Codigo", "Nombre", "Fecha", "Defectos", "Proveedor", "Cantidad"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.tablaTareas = new JTable(modeloTareas);
        this.tablaTareas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.tablaMateriales = new JTable(modeloMateriales);
        this.tablaMateriales.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.txtDescripcionTrabajo = new JTextArea(4, 30);
        this.cmbEstado = new JComboBox<>(EstadoTarea.values());

        this.txtCodigoMaterial = new JTextField();
        this.txtNombreMaterial = new JTextField();
        this.txtFechaIngresoMaterial = new JTextField(LocalDate.now().toString());
        this.txtDefectosMaterial = new JTextField();
        this.txtProveedorMaterial = new JTextField();
        this.txtCantidadMaterial = new JTextField();

        construirUI();
        cargarTareas();
    }

    private void construirUI() {
        setTitle("Panel Colaborador - " + colaboradorActual.getNombre());
        setSize(1200, 760);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane pestañas = new JTabbedPane();
        pestañas.addTab("Mis tareas", construirPestanaTareas());
        pestañas.addTab("Materiales", construirPanelMateriales());

        tablaTareas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarDetalleTareaSeleccionada();
            }
        });

        add(pestañas);
    }
    
    private JPanel construirPestanaTareas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitulo = new JLabel("Mis tareas asignadas");
        panel.add(lblTitulo, BorderLayout.NORTH);

        JScrollPane scrollTareas = new JScrollPane(tablaTareas);
        scrollTareas.setBorder(BorderFactory.createTitledBorder("Lista de tareas"));

        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.add(scrollTareas, BorderLayout.CENTER);

        JPanel panelDerecho = construirPanelActualizacionTarea();

        JPanel contenido = new JPanel(new GridLayout(1, 2, 10, 10));
        contenido.add(panelIzquierdo);
        contenido.add(panelDerecho);

        panel.add(contenido, BorderLayout.CENTER);

        return panel;
    }

    private JPanel construirPanelActualizacionTarea() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Actualizar tarea"));

        JPanel form = new JPanel(new GridLayout(3, 1, 8, 8));
        form.add(new JLabel("Descripcion de trabajo/imprevistos:"));
        form.add(new JScrollPane(txtDescripcionTrabajo));

        JPanel estadoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        estadoPanel.add(new JLabel("Estado:"));
        estadoPanel.add(cmbEstado);
        form.add(estadoPanel);

        panel.add(form, BorderLayout.CENTER);

        JButton btnGuardar = new JButton("Guardar estado + descripcion");
        btnGuardar.addActionListener(e -> guardarActualizacionTarea());

        JButton btnFinalizar = new JButton("Finalizar tarea");
        btnFinalizar.addActionListener(e -> finalizarTarea());

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarTareas());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(btnGuardar);
        acciones.add(btnFinalizar);
        acciones.add(btnRefrescar);

        panel.add(acciones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel construirPanelMateriales() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Materiales por tarea"));

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 8));
        form.add(new JLabel("Codigo:"));
        form.add(txtCodigoMaterial);

        form.add(new JLabel("Nombre producto:"));
        form.add(txtNombreMaterial);

        form.add(new JLabel("Fecha ingreso (yyyy-MM-dd):"));
        form.add(txtFechaIngresoMaterial);

        form.add(new JLabel("Defectos visibles:"));
        form.add(txtDefectosMaterial);

        form.add(new JLabel("Proveedor:"));
        form.add(txtProveedorMaterial);

        form.add(new JLabel("Cantidad:"));
        form.add(txtCantidadMaterial);

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaMateriales), BorderLayout.CENTER);

        JButton btnAgregar = new JButton("Agregar (+) material");
        btnAgregar.addActionListener(e -> agregarMaterialATarea());

        JButton btnRefrescar = new JButton("Refrescar materiales");
        btnRefrescar.addActionListener(e -> cargarMaterialesDeTareaSeleccionada());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(btnAgregar);
        acciones.add(btnRefrescar);

        panel.add(acciones, BorderLayout.SOUTH);
        return panel;
    }

    private void cargarTareas() {
        try {
            modeloTareas.setRowCount(0);
            List<Tarea> tareas = tareaDAO.listarPorAsignado(colaboradorActual.getIdUsuario());
            for (Tarea tarea : tareas) {
                modeloTareas.addRow(new Object[]{
                    tarea.getIdTarea(),
                    tarea.getTitulo(),
                    tarea.getEstado().getDbValue(),
                    tarea.getFechaEntrega(),
                    tarea.getDescripcion()
                });
            }

            if (tablaTareas.getRowCount() > 0) {
                tablaTareas.setRowSelectionInterval(0, 0);
                cargarDetalleTareaSeleccionada();
            } else {
                modeloMateriales.setRowCount(0);
                txtDescripcionTrabajo.setText("");
            }
        } catch (SQLException ex) {
            mostrarError("No se pudieron cargar tus tareas", ex);
        }
    }

    private void cargarDetalleTareaSeleccionada() {
        int fila = tablaTareas.getSelectedRow();
        if (fila == -1) {
            return;
        }

        String descripcion = String.valueOf(modeloTareas.getValueAt(fila, 4));
        String estadoTexto = String.valueOf(modeloTareas.getValueAt(fila, 2));

        txtDescripcionTrabajo.setText("null".equalsIgnoreCase(descripcion) ? "" : descripcion);

        try {
            cmbEstado.setSelectedItem(EstadoTarea.fromDbValue(estadoTexto));
        } catch (IllegalArgumentException ex) {
            cmbEstado.setSelectedItem(EstadoTarea.PENDIENTE);
        }

        cargarMaterialesDeTareaSeleccionada();
    }

    private void guardarActualizacionTarea() {
        int fila = tablaTareas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una tarea.", "Tareas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTarea = (int) modeloTareas.getValueAt(fila, 0);
        EstadoTarea estado = (EstadoTarea) cmbEstado.getSelectedItem();
        String descripcion = txtDescripcionTrabajo.getText().trim();

        if (estado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un estado.", "Tareas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            tareaDAO.actualizarEstadoYDescripcion(idTarea, estado, descripcion);
            cargarTareas();
        } catch (SQLException ex) {
            mostrarError("No se pudo guardar la actualizacion", ex);
        }
    }

    private void finalizarTarea() {
        int fila = tablaTareas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una tarea.", "Tareas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTarea = (int) modeloTareas.getValueAt(fila, 0);
        String descripcion = txtDescripcionTrabajo.getText().trim();

        try {
            tareaDAO.actualizarEstadoYDescripcion(idTarea, EstadoTarea.TERMINADA, descripcion);
            cargarTareas();
            JOptionPane.showMessageDialog(this, "Tarea finalizada. Se notificara al admin en su panel.", "Tareas", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            mostrarError("No se pudo finalizar la tarea", ex);
        }
    }

    private void agregarMaterialATarea() {
        int fila = tablaTareas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una tarea para registrar materiales.", "Materiales", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTarea = (int) modeloTareas.getValueAt(fila, 0);

        try {
            RegistroMaterialTarea registro = leerMaterialFormulario(idTarea);
            registroMaterialTareaDAO.crear(registro);
            limpiarFormularioMaterial();
            cargarMaterialesDeTareaSeleccionada();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Materiales", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            mostrarError("No se pudo registrar material", ex);
        }
    }

    private RegistroMaterialTarea leerMaterialFormulario(int idTarea) {
        String codigo = txtCodigoMaterial.getText().trim();
        String nombre = txtNombreMaterial.getText().trim();
        String fechaTexto = txtFechaIngresoMaterial.getText().trim();
        String defectos = txtDefectosMaterial.getText().trim();
        String proveedor = txtProveedorMaterial.getText().trim();
        String cantidadTexto = txtCantidadMaterial.getText().trim();

        if (codigo.isBlank() || nombre.isBlank() || fechaTexto.isBlank() || cantidadTexto.isBlank()) {
            throw new IllegalArgumentException("Codigo, nombre, fecha y cantidad son obligatorios.");
        }

        LocalDate fechaIngreso;
        int cantidad;

        try {
            fechaIngreso = LocalDate.parse(fechaTexto);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Fecha invalida. Usa yyyy-MM-dd.");
        }

        try {
            cantidad = Integer.parseInt(cantidadTexto);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Cantidad invalida.");
        }

        RegistroMaterialTarea registro = new RegistroMaterialTarea();
        registro.setIdTarea(idTarea);
        registro.setCodigoProducto(codigo);
        registro.setNombreProducto(nombre);
        registro.setFechaIngreso(fechaIngreso);
        registro.setDefectosVisibles(defectos);
        registro.setProveedor(proveedor);
        registro.setCantidad(cantidad);
        registro.setRegistradoPor(colaboradorActual.getIdUsuario());
        return registro;
    }

    private void cargarMaterialesDeTareaSeleccionada() {
        int fila = tablaTareas.getSelectedRow();
        if (fila == -1) {
            modeloMateriales.setRowCount(0);
            return;
        }

        int idTarea = (int) modeloTareas.getValueAt(fila, 0);

        try {
            modeloMateriales.setRowCount(0);
            List<RegistroMaterialTarea> registros = registroMaterialTareaDAO.listarPorTarea(idTarea);
            for (RegistroMaterialTarea registro : registros) {
                modeloMateriales.addRow(new Object[]{
                    registro.getIdRegistro(),
                    registro.getCodigoProducto(),
                    registro.getNombreProducto(),
                    registro.getFechaIngreso(),
                    registro.getDefectosVisibles(),
                    registro.getProveedor(),
                    registro.getCantidad()
                });
            }
        } catch (SQLException ex) {
            mostrarError("No se pudieron cargar los materiales de la tarea", ex);
        }
    }

    private void limpiarFormularioMaterial() {
        txtCodigoMaterial.setText("");
        txtNombreMaterial.setText("");
        txtFechaIngresoMaterial.setText(LocalDate.now().toString());
        txtDefectosMaterial.setText("");
        txtProveedorMaterial.setText("");
        txtCantidadMaterial.setText("");
    }

    private void mostrarError(String mensaje, Exception ex) {
        JOptionPane.showMessageDialog(this, mensaje + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
