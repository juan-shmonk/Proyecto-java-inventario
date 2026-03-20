package com.mycompany.appinventario.dao;

import com.mycompany.appinventario.db.ConexionBD;
import com.mycompany.appinventario.model.MaterialInventario;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventarioDAO {

    public void crear(MaterialInventario material) throws SQLException {
        final String query = """
                INSERT INTO inventario_materiales(codigo, nombre, fecha_ingreso, defectos_visibles, proveedor, cantidad, costo_unitario_mxn)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, material.getCodigo());
            statement.setString(2, material.getNombre());
            statement.setDate(3, Date.valueOf(material.getFechaIngreso()));
            statement.setString(4, material.getDefectosVisibles());
            statement.setString(5, material.getProveedor());
            statement.setInt(6, material.getCantidad());
            statement.setDouble(7, material.getCostoUnitarioMxn());
            statement.executeUpdate();
        }
    }

    public void actualizar(MaterialInventario material) throws SQLException {
        final String query = """
                UPDATE inventario_materiales
                SET codigo = ?, nombre = ?, fecha_ingreso = ?, defectos_visibles = ?, proveedor = ?, cantidad = ?, costo_unitario_mxn = ?
                WHERE id_material = ?
                """;

        try (Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, material.getCodigo());
            statement.setString(2, material.getNombre());
            statement.setDate(3, Date.valueOf(material.getFechaIngreso()));
            statement.setString(4, material.getDefectosVisibles());
            statement.setString(5, material.getProveedor());
            statement.setInt(6, material.getCantidad());
            statement.setDouble(7, material.getCostoUnitarioMxn());
            statement.setInt(8, material.getIdMaterial());
            statement.executeUpdate();
        }
    }

    public void eliminar(int idMaterial) throws SQLException {
        final String query = "DELETE FROM inventario_materiales WHERE id_material = ?";

        try (Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idMaterial);
            statement.executeUpdate();
        }
    }

    public List<MaterialInventario> listarTodo() throws SQLException {
        final String query = "SELECT id_material, codigo, nombre, fecha_ingreso, defectos_visibles, proveedor, cantidad, costo_unitario_mxn FROM inventario_materiales ORDER BY fecha_ingreso DESC";
        List<MaterialInventario> materiales = new ArrayList<>();

        try (Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                MaterialInventario material = new MaterialInventario();
                material.setIdMaterial(resultSet.getInt("id_material"));
                material.setCodigo(resultSet.getString("codigo"));
                material.setNombre(resultSet.getString("nombre"));
                material.setFechaIngreso(resultSet.getDate("fecha_ingreso").toLocalDate());
                material.setDefectosVisibles(resultSet.getString("defectos_visibles"));
                material.setProveedor(resultSet.getString("proveedor"));
                material.setCantidad(resultSet.getInt("cantidad"));
                material.setCostoUnitarioMxn(resultSet.getDouble("costo_unitario_mxn"));
                materiales.add(material);
            }
        }

        return materiales;
    }
}
