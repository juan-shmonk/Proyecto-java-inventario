package com.mycompany.appinventario.model;

import com.mycompany.appinventario.config.AppConfig;
import java.time.LocalDate;

public class MaterialInventario {
    private int idMaterial;
    private String codigo;
    private String nombre;
    private LocalDate fechaIngreso;
    private String defectosVisibles;
    private String proveedor;
    private int cantidad;
    private double costoUnitarioMxn;

    public int getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(int idMaterial) {
        this.idMaterial = idMaterial;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getDefectosVisibles() {
        return defectosVisibles;
    }

    public void setDefectosVisibles(String defectosVisibles) {
        this.defectosVisibles = defectosVisibles;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getCostoUnitarioMxn() {
        return costoUnitarioMxn;
    }

    public void setCostoUnitarioMxn(double costoUnitarioMxn) {
        this.costoUnitarioMxn = costoUnitarioMxn;
    }

    public double getSubtotalMxn() {
        return cantidad * costoUnitarioMxn;
    }

    public double getIvaMxn() {
        return getSubtotalMxn() * AppConfig.IVA_PORCENTAJE;
    }

    public double getTotalMxn() {
        return getSubtotalMxn() + getIvaMxn();
    }

    public double getTotalUsd() {
        return getTotalMxn() / AppConfig.TIPO_CAMBIO_USD;
    }
}
