package com.mycompany.appraf;

import java.io.*;
import javax.swing.*;
public class AccesoRandom {
    private RandomAccessFile raf;
    private final int tamMaxReg;
    private int totalRegistros;
    private int registroActual;
    Agenda ag;
    AccesoRandom(){ 
        tamMaxReg=120; 
        totalRegistros=0;  
        registroActual=0; 
        ag=new Agenda(); 
        raf=null;
    }
    public void abrir(File nomArch) {
        try{
            raf = new RandomAccessFile(nomArch,"rw" );
            registroActual=0;
            totalRegistros=(int)Math.ceil(raf.length()/(double)tamMaxReg);
        } catch (IOException e){
            System.out.println("Error 1: "+e.toString());
        }
    }

    public void cerrar() {
        if (raf!=null) try{
                raf.close(); 
                totalRegistros=0;  
                registroActual=0;
            } catch(IOException e){
                System.out.println("Error 2: "+e.toString());
            }
    }
   
    public void agregar (Agenda agenda){
        if (raf!=null) 
            try{
              if (agenda.tamDatos()<=tamMaxReg){
                raf.seek(totalRegistros*tamMaxReg);
                raf.writeUTF(agenda.getNombre());
                raf.writeLong(agenda.getTel());
                raf.writeUTF(agenda.getEmail());
                registroActual=totalRegistros;
                totalRegistros++;
              }
              else JOptionPane.showMessageDialog(null,"El tamaño de los datos excede el maximo permitido!");
            } catch(IOException e){
                System.out.println("Error 3: "+e.toString());
            }
    }

    public void modificar (Agenda agenda, int posRegistro){
        if (raf!=null)
        try{
            if (agenda.tamDatos()<=tamMaxReg){
                raf.seek(posRegistro*tamMaxReg);
                //registroActual=posRegistro;
                raf.writeUTF(agenda.getNombre());
                raf.writeLong(agenda.getTel());
                raf.writeUTF(agenda.getEmail());
            }
            else JOptionPane.showMessageDialog(null,"El tamaño de los datos excede el maximo permitido!");
        } catch(IOException e){
            System.out.println("Error 4: "+e.toString());
        }
    }

    public Agenda leer(int pos) {
        ag = null;
        if (raf!=null) {
            try {
                if(pos>=0 && pos<totalRegistros){
                    raf.seek(tamMaxReg*pos);
                    registroActual=pos;
                    ag = new Agenda(raf.readUTF(), raf.readLong(), raf.readUTF());
                }
            } catch (IOException e){
                System.out.println("Error 5: "+e.toString());
            }
        }
        return ag;
    }
    
    public Agenda buscar(String name) {
        ag = null;
        boolean encontrado=false;
        if (raf!=null) {
            try {
                for (int i=0; i<totalRegistros; i++){
                    raf.seek(tamMaxReg*i);
                    registroActual=i;
                    ag = new Agenda(raf.readUTF(), raf.readLong(), raf.readUTF());
                    if (ag.getNombre().equalsIgnoreCase(name)) 
                        {encontrado=true; break;}
                }
            }
            catch (IOException e){
                System.out.println("Error 6: "+e.toString());
            }
        }
        if (!encontrado) {ag=null; registroActual=totalRegistros;}
        return ag;
    }
    public int getTamReg(){ return tamMaxReg;}
    public int getNumRegs(){ return totalRegistros;}
    public int getRegActual(){return registroActual;}
    public void setRegActual(int ra){registroActual=ra;}
    public void setNumRegs(int nr){totalRegistros=nr;}
}