package com.mycompany.appinventario.config;

import java.time.LocalTime;

public final class AppConfig {

    private AppConfig() {
    }

    private static final String DB_HOST = System.getenv().getOrDefault("APP_DB_HOST", "localhost");
    private static final String DB_PORT = System.getenv().getOrDefault("APP_DB_PORT", "3306");
    private static final String DB_NAME = System.getenv().getOrDefault("APP_DB_NAME", "tiendastorage");

    public static final String DB_USER = System.getenv().getOrDefault("APP_DB_USER", "root");
    public static final String DB_PASSWORD = System.getenv().getOrDefault("APP_DB_PASSWORD", "");

    public static final LocalTime HORARIO_INICIO = LocalTime.of(8, 0);
    public static final LocalTime HORARIO_FIN = LocalTime.of(18, 0);
    public static final double IVA_PORCENTAJE = 0.16;
    public static final double TIPO_CAMBIO_USD = 17.00;

    public static String getDbUrl() {
        return String.format(
                "jdbc:mysql://%s:%s/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Cancun",
                DB_HOST,
                DB_PORT,
                DB_NAME
        );
    }
}
