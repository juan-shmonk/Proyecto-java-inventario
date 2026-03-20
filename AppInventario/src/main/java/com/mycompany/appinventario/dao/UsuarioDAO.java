package com.mycompany.appinventario.dao;

import com.mycompany.appinventario.db.ConexionBD;
import com.mycompany.appinventario.model.RolUsuario;
import com.mycompany.appinventario.model.Usuario;
import com.mycompany.appinventario.service.PasswordService;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioDAO {

    public Optional<Usuario> autenticar(String correo, String password) throws SQLException {
        final String query = "SELECT id_usuario, nombre, correo, password, rol FROM usuarios WHERE correo = ?";

        try (Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, correo);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                String storedPassword = resultSet.getString("password");
                if (!PasswordService.matches(password, storedPassword)) {
                    return Optional.empty();
                }

                return Optional.of(mapUsuario(resultSet));
            }
        }
    }

    public List<Usuario> obtenerColaboradores() throws SQLException {
        return obtenerPorRol(RolUsuario.COLABORADOR);
    }

    public List<Usuario> obtenerAdministradores() throws SQLException {
        return obtenerPorRol(RolUsuario.ADMIN);
    }

    public List<Usuario> obtenerTodos() throws SQLException {
        final String query = "SELECT id_usuario, nombre, correo, password, rol FROM usuarios ORDER BY nombre";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                usuarios.add(mapUsuario(resultSet));
            }
        }

        return usuarios;
    }

    public void crearColaborador(String nombre, String correo, String plainPassword) throws SQLException {
        final String query = "INSERT INTO usuarios(nombre, correo, password, rol) VALUES (?, ?, ?, 'colaborador')";

        try (Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nombre);
            statement.setString(2, correo);
            statement.setString(3, PasswordService.hash(plainPassword));
            statement.executeUpdate();
        }
    }

    public void actualizarColaborador(int idUsuario, String nombre, String correo, String plainPassword) throws SQLException {
        final boolean actualizarPassword = plainPassword != null && !plainPassword.isBlank();
        final String query = actualizarPassword
                ? "UPDATE usuarios SET nombre = ?, correo = ?, password = ? WHERE id_usuario = ? AND rol = 'colaborador'"
                : "UPDATE usuarios SET nombre = ?, correo = ? WHERE id_usuario = ? AND rol = 'colaborador'";

        try (Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, nombre);
            statement.setString(2, correo);

            if (actualizarPassword) {
                statement.setString(3, PasswordService.hash(plainPassword));
                statement.setInt(4, idUsuario);
            } else {
                statement.setInt(3, idUsuario);
            }

            statement.executeUpdate();
        }
    }

    public void eliminarUsuario(int idUsuario) throws SQLException {
        final String query = "DELETE FROM usuarios WHERE id_usuario = ? AND rol = 'colaborador'";

        try (Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idUsuario);
            statement.executeUpdate();
        }
    }

    public Optional<Usuario> obtenerPorId(int idUsuario) throws SQLException {
        final String query = "SELECT id_usuario, nombre, correo, password, rol FROM usuarios WHERE id_usuario = ?";

        try (Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, idUsuario);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapUsuario(resultSet));
            }
        }
    }

    private List<Usuario> obtenerPorRol(RolUsuario rol) throws SQLException {
        final String query = "SELECT id_usuario, nombre, correo, password, rol FROM usuarios WHERE rol = ? ORDER BY nombre";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection connection = ConexionBD.getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, rol.getDbValue());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    usuarios.add(mapUsuario(resultSet));
                }
            }
        }

        return usuarios;
    }

    private Usuario mapUsuario(ResultSet resultSet) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(resultSet.getInt("id_usuario"));
        usuario.setNombre(resultSet.getString("nombre"));
        usuario.setCorreo(resultSet.getString("correo"));
        usuario.setPassword(resultSet.getString("password"));
        usuario.setRol(RolUsuario.fromDbValue(resultSet.getString("rol")));
        return usuario;
    }
}
