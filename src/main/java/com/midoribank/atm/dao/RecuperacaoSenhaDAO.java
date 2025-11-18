package com.midoribank.atm.dao;

import com.midoribank.atm.utils.CriptografiaUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;


public class RecuperacaoSenhaDAO {

    /**
     * Salva um código de recuperação de senha no banco de dados.

     */
    public boolean salvarCodigo(int usuarioId, String codigo, LocalDateTime dataExpiracao) {
        String sql = "INSERT INTO recuperacao_senha (usuario_id, codigo, data_expiracao, utilizado) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            stmt.setString(2, codigo);
            stmt.setTimestamp(3, Timestamp.valueOf(dataExpiracao));
            stmt.setBoolean(4, false);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao salvar código de recuperação: " + e.getMessage());
            return false;
        }
    }

    /**
     * Valida um código de recuperação de senha.

     */
    public boolean validarCodigo(int usuarioId, String codigo) {
        String sql = "SELECT 1 FROM recuperacao_senha WHERE usuario_id = ? AND codigo = ? AND utilizado = 0 AND data_expiracao > NOW()";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            stmt.setString(2, codigo);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Erro ao validar código: " + e.getMessage());
            return false;
        }
    }

    /**
     * Invalida todos os códigos de recuperação antigos de um usuário.

     */
    public void invalidarCodigosAntigos(int usuarioId) {
        String sql = "UPDATE recuperacao_senha SET utilizado = 1 WHERE usuario_id = ? AND utilizado = 0";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro ao invalidar códigos antigos: " + e.getMessage());
        }
    }

    /**
     * Atualiza a senha de um usuário no banco de dados.

     */
    public boolean atualizarSenha(int usuarioId, String novaSenha) {
        String sql = "UPDATE usuario SET senha = ? WHERE id = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String senhaHash = CriptografiaUtils.hashPassword(novaSenha);
            stmt.setString(1, senhaHash);
            stmt.setInt(2, usuarioId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar senha do usuário: " + e.getMessage());
            return false;
        }
    }
}
