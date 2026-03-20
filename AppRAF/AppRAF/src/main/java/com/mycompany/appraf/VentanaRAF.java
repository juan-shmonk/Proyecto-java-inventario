package com.mycompany.appraf;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;

public class VentanaRAF extends JFrame implements ActionListener {
    AccesoRandom accesoRAF;
    AccesoRandom accesoRafTmp;
    Agenda ag;
    File archivo, archiTmp;
    
    JMenuBar barraMenu;
    JMenu menuArch, menuDatos;
    JMenuItem abrir, cerrar, agregar, buscar, limpiar;
    
    JLabel etNombre, etEmail, etTelefono, etRegistro;
    JTextField nombre, email, tel, registro;
    JButton ini, fin, atras, adelante;
    JPanel panelCaptura, panelWest, panelCentro, panelNav;
    VentanaRAF(){
        getContentPane(); setLayout(new BorderLayout());
        
        etNombre=new JLabel("Nombre"); 
        etEmail=new JLabel("Email"); 
        etTelefono=new JLabel("Telefono"); 
        etRegistro=new JLabel ("Registro:");
        nombre=new JTextField(30); 
        email=new JTextField(30);  
        tel=new JTextField("983",10); 
        registro=new JTextField(7); registro.setEnabled(false);
        ini=new JButton(" |< "); fin=new JButton(" >| "); 
        atras=new JButton(" < "); adelante=new JButton(" > ");
        
        panelCaptura=new JPanel(); panelCaptura.setLayout(new BorderLayout());
        panelWest=new JPanel(); panelWest.setLayout(new GridLayout(3,1));
        panelCentro=new JPanel(); panelCentro.setLayout(new GridLayout(3,1));
        
        panelNav=new JPanel(); 
        panelNav.setLayout(new FlowLayout());
        
        panelWest.add(etNombre); panelCentro.add(nombre); 
        panelWest.add(etEmail); panelCentro.add(email);
        panelWest.add(etTelefono); panelCentro.add(tel);
        panelCaptura.add(panelWest, BorderLayout.WEST);
        panelCaptura.add(panelCentro, BorderLayout.CENTER);
        
        panelNav.add(ini); panelNav.add(atras);
        panelNav.add(adelante); panelNav.add(fin);
        panelNav.add(etRegistro); panelNav.add(registro);       
        
        barraMenu=new JMenuBar();
        menuArch=new JMenu("Archivo");
        menuDatos=new JMenu("Datos");
        abrir=new JMenuItem  ("Nuevo/Abrir");
        cerrar=new JMenuItem  ("Cerrar");
        agregar=new JMenuItem("Agregar"); 
        buscar=new JMenuItem("Buscar");         
        limpiar=new JMenuItem("Limpiar campos");         
        menuArch.add(abrir);
        menuArch.add(cerrar);
        menuDatos.add(agregar);
        menuDatos.add(buscar);
        menuDatos.add(limpiar);
        barraMenu.add(menuArch);
        barraMenu.add(menuDatos);
        
        abrir.addActionListener(this);
        cerrar.addActionListener(this);    
        limpiar.addActionListener(this);
        agregar.addActionListener(this);
        buscar.addActionListener(this);
        ini.addActionListener(this);
        fin.addActionListener(this);
        adelante.addActionListener(this);
        atras.addActionListener(this);
        
        setJMenuBar(barraMenu);                
        add(panelCaptura,BorderLayout.CENTER);
        add(panelNav,BorderLayout.SOUTH);

        inhabilitarItems();
    
        setVisible(true);
        setTitle("Acceso a Datos - RAF");
        setSize(400,280);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
    
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == abrir){         
            JFileChooser chooser=new JFileChooser();
            int seleccionado=chooser.showOpenDialog(this);
            if (seleccionado == JFileChooser.APPROVE_OPTION){
                archivo = chooser.getSelectedFile();
                accesoRAF=new AccesoRandom();
                accesoRAF.abrir(archivo);
                if (accesoRAF.getNumRegs()>0) {
                    ag=accesoRAF.leer(0);
                    setDatos();
                }
                habilitarItems();
           }
        } 
        else if(e.getSource() == agregar) {
            ag = new Agenda(nombre.getText(),Long.parseLong(tel.getText()), email.getText());
            accesoRAF.agregar(ag);
        } 
        else if(e.getSource() == buscar) {
            ag=null; String nombreABuscar="";
            nombreABuscar=JOptionPane.showInputDialog(null,"Escribe el nombre");
            if(nombreABuscar!=null) ag = accesoRAF.buscar(nombreABuscar);
            if(ag!=null) setDatos();
            else {
                JOptionPane.showMessageDialog(null, nombreABuscar+" No se encuentra almacenado en el archivo!!!");
                if(ag!=null){
                    ag=accesoRAF.leer(accesoRAF.getNumRegs()-1);
                    setDatos();
                }
            }
        } 
        else if(e.getSource() == cerrar) {
            accesoRAF.cerrar();
            registro.setText("");
            inhabilitarItems();
            cleanDatos();
        } 
        else if(e.getSource() == ini) {
            if (accesoRAF.getRegActual()!=0){          
                ag=accesoRAF.leer(0);
                setDatos();
            }
        } 
        else if (e.getSource() == atras) {
            if (accesoRAF.getRegActual()>0){
                ag=accesoRAF.leer(accesoRAF.getRegActual()-1);
                setDatos();
            }
        } 
        else if (e.getSource() == adelante) {
            if (accesoRAF.getRegActual()<(accesoRAF.getNumRegs()-1)){
                ag=accesoRAF.leer(accesoRAF.getRegActual()+1);
                setDatos();
            }
        } 
        else if (e.getSource() == fin){
            if (accesoRAF.getNumRegs()>0 && accesoRAF.getRegActual()!=(accesoRAF.getNumRegs()-1)){
                ag=accesoRAF.leer(accesoRAF.getNumRegs()-1);
                setDatos();
            }
        }
        else if(e.getSource()==limpiar){ cleanDatos(); } 
        
        if (accesoRAF!=null && accesoRAF.getNumRegs()>0) registro.setText(""+(accesoRAF.getRegActual()+1)+"/"+accesoRAF.getNumRegs());
    }
    
    private void inhabilitarItems(){
        abrir.setEnabled(true);
        cerrar.setEnabled(false);
        agregar.setEnabled(false); 
        buscar.setEnabled(false);         
    }
    
    private void habilitarItems(){
        abrir.setEnabled(false);
        cerrar.setEnabled(true);
        agregar.setEnabled(true); 
        buscar.setEnabled(true);         
    }
    
    private void setDatos(){
        nombre.setText(""+ag.getNombre());
        email.setText(""+ag.getEmail());
        tel.setText(""+ag.getTel());
    }
    
    private void cleanDatos(){
        nombre.setText("");
        email.setText("");
        tel.setText("983");
    }
}