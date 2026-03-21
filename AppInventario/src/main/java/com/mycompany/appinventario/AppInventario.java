package com.mycompany.appinventario;

import com.mycompany.appinventario.ui.LoginForm;
import javax.swing.SwingUtilities;

public class AppInventario {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
        });
    }
}