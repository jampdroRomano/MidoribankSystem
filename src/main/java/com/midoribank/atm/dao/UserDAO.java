package com.midoribank.atm.dao;

import com.midoribank.atm.models.UserProfile;
import com.midoribank.atm.utils.CriptografiaUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {

    public boolean autenticar(String email, String senha) {
        String sql = "SELECT senha FROM usuario WHERE email = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String senhaHashBanco = rs.getString("senha");
                    return CriptografiaUtils.checkPassword(senha, senhaHashBanco);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao autenticar usuário: " + e.getMessage());
        }
        return false;
    }

    public UserProfile getProfile(String email) {
        String sql = "SELECT " +
                "  u.id AS usuario_id, u.nome, u.email, " +
                "  c.id AS conta_id, c.agencia, c.numero_conta, c.saldo, " +
                "  ca.numero_cartao, ca.senha AS pin_cartao, ca.cvv, " +
                "  (SELECT senha FROM usuario WHERE email = ?) AS senha_conta " +
                "FROM " +
                "  usuario u " +
                "JOIN conta c ON u.id = c.usuario_id " +
                "JOIN cartao ca ON c.id = ca.conta_id " +
                "WHERE " +
                "  u.email = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int usuarioId = rs.getInt("usuario_id");
                    int contaId = rs.getInt("conta_id");
                    String nome = rs.getString("nome");
                    String senhaConta = rs.getString("senha_conta");
                    String agencia = rs.getString("agencia");
                    String numeroConta = rs.getString("numero_conta");
                    double saldo = rs.getDouble("saldo");
                    String cartao = rs.getString("numero_cartao");
                    String pin = rs.getString("pin_cartao");
                    String cvv = rs.getString("cvv");
                    return new UserProfile(usuarioId, contaId, nome, email, numeroConta, agencia, senhaConta, saldo, cartao, pin, cvv);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar perfil do usuário: " + e.getMessage());
        }
        return null;
    }

    public UserProfile getProfileBasico(String email) {
        String sql = "SELECT id, nome, email FROM usuario WHERE email = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    String emailDb = rs.getString("email");

                    return new UserProfile(id, 0, nome, emailDb, null, null, null, 0, null, null, null);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar perfil básico do usuário: " + e.getMessage());
        }
        return null;
    }

    public int cadastrarUsuario(String nome, String email, String senha, Connection conn) {
        String sql = "INSERT INTO usuario (nome, email, senha) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String senhaHash = CriptografiaUtils.hashPassword(senha);
            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, senhaHash);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar usuário: " + e.getMessage());
        }
        return -1;
    }

    public boolean verificarEmailExistente(String email) {
        String sql = "SELECT 1 FROM usuario WHERE email = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar e-mail: " + e.getMessage());
            return false;
        }
    }
}