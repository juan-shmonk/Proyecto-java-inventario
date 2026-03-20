package com.mycompany.appinventario.ui;

import com.mycompany.appinventario.config.AppConfig;
import com.mycompany.appinventario.dao.InventarioDAO;
import com.mycompany.appinventario.dao.RegistroMaterialTareaDAO;
import com.mycompany.appinventario.dao.TareaDAO;
import com.mycompany.appinventario.dao.UsuarioDAO;
import com.mycompany.appinventario.model.EstadoTarea;
import com.mycompany.appinventario.model.MaterialInventario;
import com.mycompany.appinventario.model.RegistroMaterialTarea;
import com.mycompany.appinventario.model.Usuario;
import com.mycompany.appinventario.service.EstadoUsuarioService;
import com.mycompany.appinventario.service.ReporteService;
import com.mycompany.appinventario.ui.component.CalculatorPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class AdminDashboard extends JFrame {

    private final Usuario adminActual;
    private final UsuarioDAO usuarioDAO;
    private final TareaDAO tareaDAO;
    private final InventarioDAO inventarioDAO;
    private final RegistroMaterialTareaDAO registroMaterialTareaDAO;

    private final DefaultTableModel modeloColaboradores;
    private final DefaultTableModel modeloTareas;
    private final DefaultTableModel modeloNotificaciones;
    private final DefaultTableModel modeloInventario;

    private final JTable tablaColaboradores;
    private final JTable tablaTareas;
    private final JTable tablaNotificaciones;
    private final JTable tablaInventario;

    private final JLabel lblEstadoHorario;
    private final JLabel lblTotalesInventario;

    private JTextField txtTituloTarea;
    private JTextArea txtDescripcionTarea;
    private JTextField txtFechaEntregaTarea;
    private JComboBox<Usuario> cmbAsignado;

    private JTextField txtInvCodigo;
    private JTextField txtInvNombre;
    private JTextField txtInvFechaIngreso;
    private JTextField txtInvCantidad;
    private JTextField txtInvCostoUnitario;
    private JTextField txtInvProveedor;
    private JTextField txtInvDefectos;

    public AdminDashboard(Usuario adminActual) {
        this.adminActual = adminActual;
        this.usuarioDAO = new UsuarioDAO();
        this.tareaDAO = new TareaDAO();
        this.inventarioDAO = new InventarioDAO();
        this.registroMaterialTareaDAO = new RegistroMaterialTareaDAO();

        this.modeloColaboradores = new DefaultTableModel(new String[]{"ID", "Nombre", "Correo", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.modeloTareas = new DefaultTableModel(new String[]{"ID", "Titulo", "Estado", "Asignado", "Entrega", "Descripcion"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.modeloNotificaciones = new DefaultTableModel(new String[]{"ID", "Titulo", "Colaborador", "Fecha Entrega"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.modeloInventario = new DefaultTableModel(
                new String[]{"ID", "Codigo", "Nombre", "Fecha", "Cantidad", "Costo Unit. MXN", "Subtotal", "IVA", "Total MXN", "Total USD", "Proveedor", "Defectos"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        this.tablaColaboradores = new JTable(modeloColaboradores);
        this.tablaColaboradores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.tablaTareas = new JTable(modeloTareas);
        this.tablaTareas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.tablaNotificaciones = new JTable(modeloNotificaciones);
        this.tablaNotificaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.tablaInventario = new JTable(modeloInventario);
        this.tablaInventario.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        this.lblEstadoHorario = new JLabel();
        this.lblTotalesInventario = new JLabel("Totales -> MXN: 0.00 | USD: 0.00");

        construirUI();
        recargarTodo();
    }

    private void construirUI() {
        setTitle("Panel Admin - " + adminActual.getNombre());
        setSize(1280, 780);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Colaboradores", construirTabColaboradores());
        tabs.addTab("Tareas", construirTabTareas());
        tabs.addTab("Notificaciones", construirTabNotificaciones());
        tabs.addTab("Inventario", construirTabInventario());
        tabs.addTab("Calculadora", new CalculatorPanel());

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel construirTabColaboradores() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new BorderLayout());
        top.add(new JLabel("Gestion de colaboradores"), BorderLayout.WEST);
        top.add(lblEstadoHorario, BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaColaboradores), BorderLayout.CENTER);

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> agregarColaborador());

        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarColaborador());

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarColaborador());

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarColaboradores());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(btnAgregar);
        acciones.add(btnEditar);
        acciones.add(btnEliminar);
        acciones.add(btnRefrescar);

        panel.add(acciones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel construirTabTareas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        txtTituloTarea = new JTextField();
        txtDescripcionTarea = new JTextArea(3, 20);
        txtFechaEntregaTarea = new JTextField(LocalDate.now().plusDays(1).toString());
        cmbAsignado = new JComboBox<>();

        form.add(new JLabel("Titulo de tarea:"));
        form.add(txtTituloTarea);
        form.add(new JLabel("Descripcion:"));
        form.add(new JScrollPane(txtDescripcionTarea));
        form.add(new JLabel("Fecha entrega (yyyy-MM-dd):"));
        form.add(txtFechaEntregaTarea);
        form.add(new JLabel("Asignar a colaborador:"));
        form.add(cmbAsignado);

        JButton btnCrear = new JButton("Crear y asignar tarea");
        btnCrear.addActionListener(e -> crearTarea());

        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.add(form, BorderLayout.CENTER);
        top.add(btnCrear, BorderLayout.SOUTH);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaTareas), BorderLayout.CENTER);

        JButton btnEnProceso = new JButton("Marcar en proceso");
        btnEnProceso.addActionListener(e -> actualizarEstadoTareaSeleccionada(EstadoTarea.EN_PROCESO));

        JButton btnTerminar = new JButton("Marcar terminada");
        btnTerminar.addActionListener(e -> actualizarEstadoTareaSeleccionada(EstadoTarea.TERMINADA));

        JButton btnReporteEliminar = new JButton("Imprimir reporte y eliminar");
        btnReporteEliminar.addActionListener(e -> imprimirReporteYEliminar());

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> {
            cargarTareas();
            cargarNotificaciones();
        });

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(btnEnProceso);
        acciones.add(btnTerminar);
        acciones.add(btnReporteEliminar);
        acciones.add(btnRefrescar);

        panel.add(acciones, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel construirTabNotificaciones() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Tareas terminadas (notificaciones)"), BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaNotificaciones), BorderLayout.CENTER);

        JButton btnRefrescar = new JButton("Refrescar notificaciones");
        btnRefrescar.addActionListener(e -> cargarNotificaciones());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(btnRefrescar);
        panel.add(acciones, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel construirTabInventario() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel form = new JPanel(new GridLayout(4, 4, 8, 8));
        txtInvCodigo = new JTextField();
        txtInvNombre = new JTextField();
        txtInvFechaIngreso = new JTextField(LocalDate.now().toString());
        txtInvCantidad = new JTextField();
        txtInvCostoUnitario = new JTextField();
        txtInvProveedor = new JTextField();
        txtInvDefectos = new JTextField();

        form.add(new JLabel("Codigo:"));
        form.add(txtInvCodigo);
        form.add(new JLabel("Nombre:"));
        form.add(txtInvNombre);

        form.add(new JLabel("Fecha ingreso (yyyy-MM-dd):"));
        form.add(txtInvFechaIngreso);
        form.add(new JLabel("Cantidad:"));
        form.add(txtInvCantidad);

        form.add(new JLabel("Costo unitario MXN:"));
        form.add(txtInvCostoUnitario);
        form.add(new JLabel("Proveedor:"));
        form.add(txtInvProveedor);

        form.add(new JLabel("Defectos visibles:"));
        form.add(txtInvDefectos);
        form.add(new JLabel(""));
        form.add(new JLabel(""));

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaInventario), BorderLayout.CENTER);

        tablaInventario.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarMaterialSeleccionadoEnFormulario();
            }
        });

        JButton btnAgregar = new JButton("Agregar material");
        btnAgregar.addActionListener(e -> agregarMaterialInventario());

        JButton btnActualizar = new JButton("Actualizar seleccionado");
        btnActualizar.addActionListener(e -> actualizarMaterialInventario());

        JButton btnEliminar = new JButton("Eliminar seleccionado");
        btnEliminar.addActionListener(e -> eliminarMaterialInventario());

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarInventario());

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.add(btnAgregar);
        acciones.add(btnActualizar);
        acciones.add(btnEliminar);
        acciones.add(btnRefrescar);
        acciones.add(lblTotalesInventario);

        panel.add(acciones, BorderLayout.SOUTH);
        return panel;
    }

    private void recargarTodo() {
        cargarColaboradores();
        cargarColaboradoresCombo();
        cargarTareas();
        cargarNotificaciones();
        cargarInventario();
    }

    private void cargarColaboradores() {
        try {
            modeloColaboradores.setRowCount(0);
            boolean activoPorHorario = EstadoUsuarioService.estaActivoPorHorarioSistema();
            String estado = activoPorHorario ? "Activo" : "Fuera de horario";

            for (Usuario colaborador : usuarioDAO.obtenerColaboradores()) {
                modeloColaboradores.addRow(new Object[]{
                    colaborador.getIdUsuario(),
                    colaborador.getNombre(),
                    colaborador.getCorreo(),
                    estado
                });
            }

            lblEstadoHorario.setText(String.format("Horario sistema: %s (%s - %s)", estado, AppConfig.HORARIO_INICIO, AppConfig.HORARIO_FIN));
        } catch (SQLException ex) {
            mostrarError("No se pudieron cargar colaboradores", ex);
        }
    }

    private void cargarColaboradoresCombo() {
        try {
            DefaultComboBoxModel<Usuario> comboModel = new DefaultComboBoxModel<>();
            for (Usuario colaborador : usuarioDAO.obtenerColaboradores()) {
                comboModel.addElement(colaborador);
            }
            cmbAsignado.setModel(comboModel);
        } catch (SQLException ex) {
            mostrarError("No se pudo cargar listado de colaboradores", ex);
        }
    }

    private void agregarColaborador() {
        ColaboradorFormData formData = mostrarDialogoColaborador(null);
        if (formData == null) {
            return;
        }

        try {
            usuarioDAO.crearColaborador(formData.nombre(), formData.correo(), formData.password());
            cargarColaboradores();
            cargarColaboradoresCombo();
        } catch (SQLException ex) {
            mostrarError("No se pudo agregar colaborador", ex);
        }
    }

    private void editarColaborador() {
        int fila = tablaColaboradores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un colaborador.", "Colaboradores", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idUsuario = (int) modeloColaboradores.getValueAt(fila, 0);

        try {
            Optional<Usuario> usuarioOpt = usuarioDAO.obtenerPorId(idUsuario);
            if (usuarioOpt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontro el colaborador.", "Colaboradores", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Usuario usuario = usuarioOpt.get();
            ColaboradorFormData formData = mostrarDialogoColaborador(usuario);
            if (formData == null) {
                return;
            }

            usuarioDAO.actualizarColaborador(idUsuario, formData.nombre(), formData.correo(), formData.password());
            cargarColaboradores();
            cargarColaboradoresCombo();
        } catch (SQLException ex) {
            mostrarError("No se pudo editar colaborador", ex);
        }
    }

    private void eliminarColaborador() {
        int fila = tablaColaboradores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un colaborador.", "Colaboradores", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idUsuario = (int) modeloColaboradores.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Se eliminara el colaborador seleccionado. Continuar?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            usuarioDAO.eliminarUsuario(idUsuario);
            cargarColaboradores();
            cargarColaboradoresCombo();
        } catch (SQLException ex) {
            mostrarError("No se pudo eliminar colaborador", ex);
        }
    }

    private ColaboradorFormData mostrarDialogoColaborador(Usuario usuarioBase) {
        JTextField txtNombre = new JTextField(usuarioBase != null ? usuarioBase.getNombre() : "");
        JTextField txtCorreo = new JTextField(usuarioBase != null ? usuarioBase.getCorreo() : "");
        JTextField txtPassword = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2, 8, 8));
        panel.add(new JLabel("Nombre:"));
        panel.add(txtNombre);
        panel.add(new JLabel("Correo:"));
        panel.add(txtCorreo);
        panel.add(new JLabel(usuarioBase == null ? "Contrasena:" : "Contrasena (vacío para conservar):"));
        panel.add(txtPassword);

        String titulo = usuarioBase == null ? "Nuevo colaborador" : "Editar colaborador";
        int option = JOptionPane.showConfirmDialog(this, panel, titulo, JOptionPane.OK_CANCEL_OPTION);

        if (option != JOptionPane.OK_OPTION) {
            return null;
        }

        String nombre = txtNombre.getText().trim();
        String correo = txtCorreo.getText().trim();
        String password = txtPassword.getText().trim();

        if (nombre.isBlank() || correo.isBlank() || (usuarioBase == null && password.isBlank())) {
            JOptionPane.showMessageDialog(this, "Nombre, correo y contrasena son obligatorios.", "Colaboradores", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return new ColaboradorFormData(nombre, correo, password);
    }

    private void crearTarea() {
        String titulo = txtTituloTarea.getText().trim();
        String descripcion = txtDescripcionTarea.getText().trim();
        String fechaEntregaTexto = txtFechaEntregaTarea.getText().trim();

        if (titulo.isBlank()) {
            JOptionPane.showMessageDialog(this, "El titulo es obligatorio.", "Tareas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario asignado = (Usuario) cmbAsignado.getSelectedItem();
        if (asignado == null) {
            JOptionPane.showMessageDialog(this, "Debes seleccionar un colaborador para asignar la tarea.", "Tareas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate fechaEntrega;
        try {
            fechaEntrega = LocalDate.parse(fechaEntregaTexto);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Fecha invalida. Usa formato yyyy-MM-dd.", "Tareas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            com.mycompany.appinventario.model.Tarea tarea = new com.mycompany.appinventario.model.Tarea();
            tarea.setTitulo(titulo);
            tarea.setDescripcion(descripcion);
            tarea.setEstado(EstadoTarea.PENDIENTE);
            tarea.setFechaEntrega(fechaEntrega);
            tarea.setCreadoPor(adminActual.getIdUsuario());
            tarea.setAsignadoA(asignado.getIdUsuario());

            tareaDAO.crearTarea(tarea);

            txtTituloTarea.setText("");
            txtDescripcionTarea.setText("");
            txtFechaEntregaTarea.setText(LocalDate.now().plusDays(1).toString());

            cargarTareas();
            cargarNotificaciones();
        } catch (SQLException ex) {
            mostrarError("No se pudo crear la tarea", ex);
        }
    }

    private void cargarTareas() {
        try {
            modeloTareas.setRowCount(0);
            for (com.mycompany.appinventario.model.Tarea tarea : tareaDAO.listarTodas()) {
                modeloTareas.addRow(new Object[]{
                    tarea.getIdTarea(),
                    tarea.getTitulo(),
                    tarea.getEstado().getDbValue(),
                    tarea.getNombreAsignado(),
                    tarea.getFechaEntrega(),
                    tarea.getDescripcion()
                });
            }
        } catch (SQLException ex) {
            mostrarError("No se pudieron cargar las tareas", ex);
        }
    }

    private void actualizarEstadoTareaSeleccionada(EstadoTarea estado) {
        int fila = tablaTareas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una tarea.", "Tareas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTarea = (int) modeloTareas.getValueAt(fila, 0);

        try {
            tareaDAO.actualizarEstado(idTarea, estado);
            cargarTareas();
            cargarNotificaciones();
        } catch (SQLException ex) {
            mostrarError("No se pudo actualizar estado de tarea", ex);
        }
    }

    private void imprimirReporteYEliminar() {
        int fila = tablaTareas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una tarea.", "Tareas", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idTarea = (int) modeloTareas.getValueAt(fila, 0);

        try {
            Optional<com.mycompany.appinventario.model.Tarea> tareaOpt = tareaDAO.obtenerPorId(idTarea);
            if (tareaOpt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontro la tarea seleccionada.", "Tareas", JOptionPane.WARNING_MESSAGE);
                return;
            }

            com.mycompany.appinventario.model.Tarea tarea = tareaOpt.get();
            Optional<Usuario> colaboradorOpt = tarea.getAsignadoA() == null
                    ? Optional.empty()
                    : usuarioDAO.obtenerPorId(tarea.getAsignadoA());

            List<RegistroMaterialTarea> registros = registroMaterialTareaDAO.listarPorTarea(idTarea);
            String reporte = ReporteService.generarReporteTarea(tarea, colaboradorOpt.orElse(null), registros);
            Path archivo = ReporteService.guardarReporteEnArchivo(reporte, idTarea);

            int printOption = JOptionPane.showConfirmDialog(
                    this,
                    "Reporte guardado en: " + archivo + "\nDeseas enviarlo a impresion?",
                    "Reporte generado",
                    JOptionPane.YES_NO_OPTION
            );
            if (printOption == JOptionPane.YES_OPTION) {
                ReporteService.imprimirReporte(reporte);
            }

            int deleteOption = JOptionPane.showConfirmDialog(
                    this,
                    "Eliminar tarea despues del reporte?",
                    "Confirmar eliminacion",
                    JOptionPane.YES_NO_OPTION
            );
            if (deleteOption == JOptionPane.YES_OPTION) {
                tareaDAO.eliminarTarea(idTarea);
            }

            cargarTareas();
            cargarNotificaciones();
        } catch (SQLException | IOException | RuntimeException | java.awt.print.PrinterException ex) {
            mostrarError("No se pudo generar/imprimir/eliminar reporte de tarea", ex);
        }
    }

    private void cargarNotificaciones() {
        try {
            modeloNotificaciones.setRowCount(0);
            for (com.mycompany.appinventario.model.Tarea tarea : tareaDAO.listarTerminadas()) {
                modeloNotificaciones.addRow(new Object[]{
                    tarea.getIdTarea(),
                    tarea.getTitulo(),
                    tarea.getNombreAsignado(),
                    tarea.getFechaEntrega()
                });
            }
        } catch (SQLException ex) {
            mostrarError("No se pudieron cargar notificaciones", ex);
        }
    }

    private void agregarMaterialInventario() {
        try {
            MaterialInventario material = leerMaterialFormulario();
            inventarioDAO.crear(material);
            limpiarFormularioInventario();
            cargarInventario();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Inventario", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            mostrarError("No se pudo agregar material", ex);
        }
    }

    private void actualizarMaterialInventario() {
        int fila = tablaInventario.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un material para actualizar.", "Inventario", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idMaterial = (int) modeloInventario.getValueAt(fila, 0);

        try {
            MaterialInventario material = leerMaterialFormulario();
            material.setIdMaterial(idMaterial);
            inventarioDAO.actualizar(material);
            limpiarFormularioInventario();
            cargarInventario();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Inventario", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            mostrarError("No se pudo actualizar material", ex);
        }
    }

    private void eliminarMaterialInventario() {
        int fila = tablaInventario.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un material.", "Inventario", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idMaterial = (int) modeloInventario.getValueAt(fila, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Eliminar material seleccionado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            inventarioDAO.eliminar(idMaterial);
            limpiarFormularioInventario();
            cargarInventario();
        } catch (SQLException ex) {
            mostrarError("No se pudo eliminar material", ex);
        }
    }

    private void cargarInventario() {
        try {
            modeloInventario.setRowCount(0);
            double totalMxn = 0;
            double totalUsd = 0;

            List<MaterialInventario> materiales = inventarioDAO.listarTodo();
            for (MaterialInventario material : materiales) {
                double subtotal = material.getSubtotalMxn();
                double iva = material.getIvaMxn();
                double total = material.getTotalMxn();
                double usd = material.getTotalUsd();

                totalMxn += total;
                totalUsd += usd;

                modeloInventario.addRow(new Object[]{
                    material.getIdMaterial(),
                    material.getCodigo(),
                    material.getNombre(),
                    material.getFechaIngreso(),
                    material.getCantidad(),
                    String.format("%.2f", material.getCostoUnitarioMxn()),
                    String.format("%.2f", subtotal),
                    String.format("%.2f", iva),
                    String.format("%.2f", total),
                    String.format("%.2f", usd),
                    material.getProveedor(),
                    material.getDefectosVisibles()
                });
            }

            lblTotalesInventario.setText(String.format("Totales -> MXN: %.2f | USD: %.2f | IVA: 16%%", totalMxn, totalUsd));
        } catch (SQLException ex) {
            mostrarError("No se pudo cargar inventario", ex);
        }
    }

    private void cargarMaterialSeleccionadoEnFormulario() {
        int fila = tablaInventario.getSelectedRow();
        if (fila == -1) {
            return;
        }

        txtInvCodigo.setText(String.valueOf(modeloInventario.getValueAt(fila, 1)));
        txtInvNombre.setText(String.valueOf(modeloInventario.getValueAt(fila, 2)));
        txtInvFechaIngreso.setText(String.valueOf(modeloInventario.getValueAt(fila, 3)));
        txtInvCantidad.setText(String.valueOf(modeloInventario.getValueAt(fila, 4)));
        txtInvCostoUnitario.setText(String.valueOf(modeloInventario.getValueAt(fila, 5)));
        txtInvProveedor.setText(String.valueOf(modeloInventario.getValueAt(fila, 10)));
        txtInvDefectos.setText(String.valueOf(modeloInventario.getValueAt(fila, 11)));
    }

    private MaterialInventario leerMaterialFormulario() {
        String codigo = txtInvCodigo.getText().trim();
        String nombre = txtInvNombre.getText().trim();
        String fechaTexto = txtInvFechaIngreso.getText().trim();
        String cantidadTexto = txtInvCantidad.getText().trim();
        String costoTexto = txtInvCostoUnitario.getText().trim();
        String proveedor = txtInvProveedor.getText().trim();
        String defectos = txtInvDefectos.getText().trim();

        if (codigo.isBlank() || nombre.isBlank() || fechaTexto.isBlank() || cantidadTexto.isBlank() || costoTexto.isBlank()) {
            throw new IllegalArgumentException("Codigo, nombre, fecha, cantidad y costo unitario son obligatorios.");
        }

        LocalDate fecha;
        int cantidad;
        double costo;

        try {
            fecha = LocalDate.parse(fechaTexto);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Fecha invalida. Usa yyyy-MM-dd.");
        }

        try {
            cantidad = Integer.parseInt(cantidadTexto);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Cantidad invalida.");
        }

        try {
            costo = Double.parseDouble(costoTexto);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Costo unitario invalido.");
        }

        MaterialInventario material = new MaterialInventario();
        material.setCodigo(codigo);
        material.setNombre(nombre);
        material.setFechaIngreso(fecha);
        material.setCantidad(cantidad);
        material.setCostoUnitarioMxn(costo);
        material.setProveedor(proveedor);
        material.setDefectosVisibles(defectos);
        return material;
    }

    private void limpiarFormularioInventario() {
        txtInvCodigo.setText("");
        txtInvNombre.setText("");
        txtInvFechaIngreso.setText(LocalDate.now().toString());
        txtInvCantidad.setText("");
        txtInvCostoUnitario.setText("");
        txtInvProveedor.setText("");
        txtInvDefectos.setText("");
    }

    private void mostrarError(String mensaje, Exception ex) {
        JOptionPane.showMessageDialog(this, mensaje + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private record ColaboradorFormData(String nombre, String correo, String password) {
    }
}
