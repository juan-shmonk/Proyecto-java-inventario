package com.mycompany.appinventario.ui;

import com.mycompany.appinventario.dao.UsuarioDAO;
import com.mycompany.appinventario.model.RolUsuario;
import com.mycompany.appinventario.model.Usuario;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class VentanaLogin extends JFrame {

    private final JTextField txtCorreo;
    private final JPasswordField txtPassword;
    private final UsuarioDAO usuarioDAO;

    public VentanaLogin() {
        this.usuarioDAO = new UsuarioDAO();

        setTitle("App Inventario - Login");
        setSize(420, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        formPanel.add(new JLabel("Correo:"));
        txtCorreo = new JTextField();
        formPanel.add(txtCorreo);

        formPanel.add(new JLabel("Contrasena:"));
        txtPassword = new JPasswordField();
        formPanel.add(txtPassword);

        JButton btnLogin = new JButton("Iniciar sesion");
        btnLogin.addActionListener(e -> iniciarSesion());

        add(formPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(btnLogin);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void iniciarSesion() {
        String correo = txtCorreo.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (correo.isBlank() || password.isBlank()) {
            JOptionPane.showMessageDialog(this, "Completa correo y contrasena.", "Login", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Optional<Usuario> usuarioOpt = usuarioDAO.autenticar(correo, password);
            if (usuarioOpt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Credenciales invalidas.", "Login", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Usuario usuario = usuarioOpt.get();
            abrirDashboard(usuario);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(), "Login", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirDashboard(Usuario usuario) {
        dispose();

        if (usuario.getRol() == RolUsuario.ADMIN) {
            new AdminDashboard(usuario).setVisible(true);
            return;
        }

        new ColaboradorDashboard(usuario).setVisible(true);
    }
}
