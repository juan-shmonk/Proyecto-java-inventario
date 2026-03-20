package com.mycompany.appinventario.db;

import com.mycompany.appinventario.config.AppConfig;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionBD {

    private ConexionBD() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(AppConfig.getDbUrl(), AppConfig.DB_USER, AppConfig.DB_PASSWORD);
    }
}
