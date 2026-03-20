package com.mycompany.appinventario.model;

public enum EstadoTarea {
    PENDIENTE("pendiente"),
    EN_PROCESO("en proceso"),
    TERMINADA("terminada");

    private final String dbValue;

    EstadoTarea(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static EstadoTarea fromDbValue(String value) {
        for (EstadoTarea estado : values()) {
            if (estado.dbValue.equalsIgnoreCase(value)) {
                return estado;
            }
        }
        throw new IllegalArgumentException("Estado no valido: " + value);
    }

    @Override
    public String toString() {
        return dbValue;
    }
}
