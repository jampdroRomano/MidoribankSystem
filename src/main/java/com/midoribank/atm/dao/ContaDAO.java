package com.midoribank.atm.dao;

import com.midoribank.atm.models.UserProfile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ContaDAO {

    public int cadastrarConta(int usuarioId, String agencia, String numeroConta, double saldoInicial, Connection conn) {
        String sql = "INSERT INTO conta (usuario_id, agencia, numero_conta, saldo) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, usuarioId);
            stmt.setString(2, agencia);
            stmt.setString(3, numeroConta);
            stmt.setDouble(4, saldoInicial);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar conta: " + e.getMessage());
        }
        return -1;
    }

    public boolean atualizarSaldo(String numeroConta, double novoSaldo, Connection conn) {
        String sql = "UPDATE conta SET saldo = ? WHERE numero_conta = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, novoSaldo);
            stmt.setString(2, numeroConta);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar saldo no banco: " + e.getMessage());
            return false;
        }
    }

    public int findContaIdByAgenciaAndNumero(String agencia, String numeroConta) {
        String sql = "SELECT id FROM conta WHERE agencia = ? AND numero_conta = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, agencia);
            stmt.setString(2, numeroConta);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar conta por agência e número: " + e.getMessage());
        }
        return -1;
    }

    public UserProfile getProfileByConta(String agencia, String numeroConta) {
        String sql = "SELECT " +
                "  u.id, c.id AS conta_id, u.nome, u.email, " +
                "  c.agencia, c.numero_conta, c.saldo, " +
                "  ca.numero_cartao, ca.senha AS pin_cartao, ca.cvv, " +
                "  u.senha AS senha_conta " +
                "FROM " +
                "  usuario u " +
                "JOIN conta c ON u.id = c.usuario_id " +
                "LEFT JOIN cartao ca ON c.id = ca.conta_id " +
                "WHERE " +
                "  c.agencia = ? AND c.numero_conta = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, agencia);
            stmt.setString(2, numeroConta);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    int contaId = rs.getInt("conta_id");
                    String nome = rs.getString("nome");
                    String email = rs.getString("email");
                    String senhaConta = rs.getString("senha_conta");
                    String agenciaDb = rs.getString("agencia");
                    String numeroContaDb = rs.getString("numero_conta");
                    double saldo = rs.getDouble("saldo");
                    String cartao = rs.getString("numero_cartao");
                    String pin = rs.getString("pin_cartao");
                    String cvv = rs.getString("cvv");

                    return new UserProfile(id, contaId, nome, email, numeroContaDb, agenciaDb, senhaConta, saldo, cartao, pin, cvv);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar perfil por conta: " + e.getMessage());
        }
        return null;
    }
}