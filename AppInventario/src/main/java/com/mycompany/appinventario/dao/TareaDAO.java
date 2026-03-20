package com.mycompany.appinventario.dao;

import com.mycompany.appinventario.db.ConexionBD;
import com.mycompany.appinventario.model.EstadoTarea;
import com.mycompany.appinventario.model.Tarea;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TareaDAO {

    public void crearTarea(Tarea tarea) throws SQLException {
        final String query = """
                INSERT INTO tareas(titulo, descripcion, estado, fecha_entrega, creado_por, asignado_a)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, tarea.getTitulo());
            statement.setString(2, tarea.getDescripcion());
            statement.setString(3, tarea.getEstado().getDbValue());
            statement.setDate(4, tarea.getFechaEntrega() != null ? Date.valueOf(tarea.getFechaEntrega()) : null);
            statement.setObject(5, tarea.getCreadoPor());
            statement.setObject(6, tarea.getAsignadoA());
            statement.executeUpdate();
        }
    }

    public List<Tarea> listarTodas() throws SQLException {
        final String query = """
                SELECT t.id_tarea, t.titulo, t.descripcion, t.estado, t.fecha_creacion, t.fecha_entrega,
                       t.creado_por, t.asignado_a, u.nombre AS nombre_asignado
                FROM tareas t
                LEFT JOIN usuarios u ON u.id_usuario = t.asignado_a
                ORDER BY t.fecha_creacion DESC
                """;
        return ejecutarListado(query, null);
    }

    public List<Tarea> listarPorAsignado(int idUsuario) throws SQLException {
        final String query = """
                SELECT t.id_tarea, t.titulo, t.descripcion, t.estado, t.fecha_creacion, t.fecha_entrega,
                       t.creado_por, t.asignado_a, u.nombre AS nombre_asignado
                FROM tareas t
                LEFT JOIN usuarios u ON u.id_usuario = t.asignado_a
                WHERE t.asignado_a = ?
                ORDER BY t.fecha_creacion DESC
                """;
        return ejecutarListado(query, statement -> statement.setInt(1, idUsuario));
    }

    public List<Tarea> listarTerminadas() throws SQLException {
        final String query = """
                SELECT t.id_tarea, t.titulo, t.descripcion, t.estado, t.fecha_creacion, t.fecha_entrega,
                       t.creado_por, t.asignado_a, u.nombre AS nombre_asignado
                FROM tareas t
                LEFT JOIN usuarios u ON u.id_usuario = t.asignado_a
                WHERE t.estado = 'terminada'
                ORDER BY t.fecha_creacion DESC
                """;
        return ejecutarListado(query, null);
    }

    public void actualizarEstadoYDescripcion(int idTarea, EstadoTarea estado, String descripcion) throws SQLException {
        final String query = "UPDATE tareas SET estado = ?, descripcion = ? WHERE id_tarea = ?";

        try (Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, estado.getDbValue());
            statement.setString(2, descripcion);
            statement.setInt(3, idTarea);
            statement.executeUpdate();
        }
    }

    public void actualizarEstado(int idTarea, EstadoTarea estado) throws SQLException {
        final String query = "UPDATE tareas SET estado = ? WHERE id_tarea = ?";

        try (Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, estado.getDbValue());
            statement.setInt(2, idTarea);
            statement.executeUpdate();
        }
    }

    public void eliminarTarea(int idTarea) throws SQLException {
        final String query = "DELETE FROM tareas WHERE id_tarea = ?";

        try (Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idTarea);
            statement.executeUpdate();
        }
    }

    public Optional<Tarea> obtenerPorId(int idTarea) throws SQLException {
        final String query = """
                SELECT t.id_tarea, t.titulo, t.descripcion, t.estado, t.fecha_creacion, t.fecha_entrega,
                       t.creado_por, t.asignado_a, u.nombre AS nombre_asignado
                FROM tareas t
                LEFT JOIN usuarios u ON u.id_usuario = t.asignado_a
                WHERE t.id_tarea = ?
                """;

        try (Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idTarea);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapTarea(resultSet));
            }
        }
    }

    private List<Tarea> ejecutarListado(String query, StatementConfigurer configurer) throws SQLException {
        List<Tarea> tareas = new ArrayList<>();

        try (Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            if (configurer != null) {
                configurer.configure(statement);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    tareas.add(mapTarea(resultSet));
                }
            }
        }

        return tareas;
    }

    private Tarea mapTarea(ResultSet resultSet) throws SQLException {
        Tarea tarea = new Tarea();
        tarea.setIdTarea(resultSet.getInt("id_tarea"));
        tarea.setTitulo(resultSet.getString("titulo"));
        tarea.setDescripcion(resultSet.getString("descripcion"));
        tarea.setEstado(EstadoTarea.fromDbValue(resultSet.getString("estado")));

        Timestamp fechaCreacion = resultSet.getTimestamp("fecha_creacion");
        if (fechaCreacion != null) {
            tarea.setFechaCreacion(fechaCreacion.toLocalDateTime());
        }

        Date fechaEntrega = resultSet.getDate("fecha_entrega");
        if (fechaEntrega != null) {
            tarea.setFechaEntrega(fechaEntrega.toLocalDate());
        }

        int creadoPor = resultSet.getInt("creado_por");
        tarea.setCreadoPor(resultSet.wasNull() ? null : creadoPor);

        int asignadoA = resultSet.getInt("asignado_a");
        tarea.setAsignadoA(resultSet.wasNull() ? null : asignadoA);

        tarea.setNombreAsignado(resultSet.getString("nombre_asignado"));

        return tarea;
    }

    @FunctionalInterface
    private interface StatementConfigurer {
        void configure(PreparedStatement statement) throws SQLException;
    }
}
