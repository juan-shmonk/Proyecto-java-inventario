package com.mycompany.appinventario.dao;

import com.mycompany.appinventario.db.ConexionBD;
import com.mycompany.appinventario.model.RegistroMaterialTarea;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RegistroMaterialTareaDAO {

    public void crear(RegistroMaterialTarea registro) throws SQLException {
        final String query = """
                INSERT INTO tarea_materiales(id_tarea, codigo_producto, nombre_producto, fecha_ingreso, defectos_visibles, proveedor, cantidad, registrado_por)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, registro.getIdTarea());
            statement.setString(2, registro.getCodigoProducto());
            statement.setString(3, registro.getNombreProducto());
            statement.setDate(4, Date.valueOf(registro.getFechaIngreso()));
            statement.setString(5, registro.getDefectosVisibles());
            statement.setString(6, registro.getProveedor());
            statement.setInt(7, registro.getCantidad());
            statement.setObject(8, registro.getRegistradoPor());
            statement.executeUpdate();
        }
    }

    public List<RegistroMaterialTarea> listarPorTarea(int idTarea) throws SQLException {
        final String query = """
                SELECT id_registro, id_tarea, codigo_producto, nombre_producto, fecha_ingreso, defectos_visibles, proveedor, cantidad, registrado_por
                FROM tarea_materiales
                WHERE id_tarea = ?
                ORDER BY id_registro DESC
                """;

        List<RegistroMaterialTarea> registros = new ArrayList<>();

        try (Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idTarea);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    RegistroMaterialTarea registro = new RegistroMaterialTarea();
                    registro.setIdRegistro(resultSet.getInt("id_registro"));
                    registro.setIdTarea(resultSet.getInt("id_tarea"));
                    registro.setCodigoProducto(resultSet.getString("codigo_producto"));
                    registro.setNombreProducto(resultSet.getString("nombre_producto"));
                    registro.setFechaIngreso(resultSet.getDate("fecha_ingreso").toLocalDate());
                    registro.setDefectosVisibles(resultSet.getString("defectos_visibles"));
                    registro.setProveedor(resultSet.getString("proveedor"));
                    registro.setCantidad(resultSet.getInt("cantidad"));

                    int registradoPor = resultSet.getInt("registrado_por");
                    registro.setRegistradoPor(resultSet.wasNull() ? null : registradoPor);

                    registros.add(registro);
                }
            }
        }

        return registros;
    }
}
