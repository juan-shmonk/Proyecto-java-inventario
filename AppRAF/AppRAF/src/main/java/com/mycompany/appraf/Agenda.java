package com.mycompany.appraf;

public class Agenda {
    private String nombre, email;
    private long tel;
    Agenda(){nombre=""; email=""; tel=0;}
    Agenda(String n, long t, String em){ 
        nombre=n; tel=t; email=em; 
    }

    //Métodos para consultar datos de un objeto de esta clase
    public String getNombre (){return nombre;}
    public String getEmail (){return email;}
    public long getTel(){return tel;}

    //Métodos para modificar datos de un objeto de esta clase
    public void setNombre (String n){nombre=n;}
    public void setEmail (String e){email=e;}
    public void setTel(long t){tel=t;}

    //Método para consultar todos los datos de un objeto de esta clase, concatenados y separados por dos espacios
    public String getDatos(){
        return nombre+"  "+email+"  "+tel;
    }
    
    /*
    Como ya saben, el método length() regresa el número de 
    caracteres en un string; sin embargo, al usar el método writeUTF
    para la escritura de un "texto" en un archivo, este agrega 
    dos bytes adicionales que representan la longitud en bytes del "texto".
    De igual forma, ya saben que un dato de tipo long ocupa 8 bytes.
    por lo anterior, al escribir en un archivo 2 datos de tipo String,
    se agregan 4 bytes; más los 8 del tipo long la suma da 12. Por ello
    la expresión en el método tamDatos.
    */
    public int tamDatos(){
        return nombre.length()+email.length()+12;
    }
}