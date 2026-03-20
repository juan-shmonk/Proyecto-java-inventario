package com.mycompany.appinventario.model;

public enum RolUsuario {
    ADMIN("admin"),
    COLABORADOR("colaborador");

    private final String dbValue;

    RolUsuario(String dbValue) {
        this.dbValue = dbValue;
    }

    public String getDbValue() {
        return dbValue;
    }

    public static RolUsuario fromDbValue(String value) {
        for (RolUsuario rol : values()) {
            if (rol.dbValue.equalsIgnoreCase(value)) {
                return rol;
            }
        }
        throw new IllegalArgumentException("Rol no valido: " + value);
    }
}
