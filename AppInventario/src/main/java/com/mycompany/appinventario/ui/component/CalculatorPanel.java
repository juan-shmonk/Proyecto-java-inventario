package com.mycompany.appinventario.ui.component;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CalculatorPanel extends JPanel {

    private final JTextField txtNumero1;
    private final JTextField txtNumero2;
    private final JLabel lblResultado;

    public CalculatorPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        inputPanel.add(new JLabel("Numero 1:"));
        txtNumero1 = new JTextField();
        inputPanel.add(txtNumero1);

        inputPanel.add(new JLabel("Numero 2:"));
        txtNumero2 = new JTextField();
        inputPanel.add(txtNumero2);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btnSumar = new JButton("+");
        JButton btnRestar = new JButton("-");
        JButton btnMultiplicar = new JButton("*");
        JButton btnDividir = new JButton("/");

        btnSumar.addActionListener(e -> calcular('+'));
        btnRestar.addActionListener(e -> calcular('-'));
        btnMultiplicar.addActionListener(e -> calcular('*'));
        btnDividir.addActionListener(e -> calcular('/'));

        buttonPanel.add(btnSumar);
        buttonPanel.add(btnRestar);
        buttonPanel.add(btnMultiplicar);
        buttonPanel.add(btnDividir);

        lblResultado = new JLabel("Resultado: 0");

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(lblResultado, BorderLayout.SOUTH);
    }

    private void calcular(char operacion) {
        double numero1;
        double numero2;

        try {
            numero1 = Double.parseDouble(txtNumero1.getText().trim());
            numero2 = Double.parseDouble(txtNumero2.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingresa dos numeros validos.", "Calculadora", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double resultado;
        switch (operacion) {
            case '+':
                resultado = numero1 + numero2;
                break;
            case '-':
                resultado = numero1 - numero2;
                break;
            case '*':
                resultado = numero1 * numero2;
                break;
            case '/':
                if (numero2 == 0) {
                    JOptionPane.showMessageDialog(this, "No se puede dividir entre cero.", "Calculadora", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                resultado = numero1 / numero2;
                break;
            default:
                return;
        }

        lblResultado.setText("Resultado: " + resultado);
    }
}
