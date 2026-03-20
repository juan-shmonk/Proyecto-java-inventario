
package com.mycompany.appinventario;

import com.mycompany.appinventario.ui.VentanaLogin;
import javax.swing.SwingUtilities;

/**
 *
 * @author Leanj
 */
public class AppInventario {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaLogin ventanaLogin = new VentanaLogin();
            ventanaLogin.setVisible(true);
        });
    }
}
