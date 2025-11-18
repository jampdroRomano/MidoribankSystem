package com.midoribank.atm.dao;

import com.midoribank.atm.models.Movimentacao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class MovimentacaoDAO {

    /*     * Enum para representar os tipos de movimentação possíveis.
     */
    public enum TipoMovimentacao {
        SAQUE, DEPOSITO, TRANSFERENCIA_ENVIADA, TRANSFERENCIA_RECEBIDA
    }

    /*     * Registra uma nova movimentação no banco de dados.

     */
    public boolean registrarMovimentacao(Connection conn, int contaId, TipoMovimentacao tipo, double valor, Integer contaDestinoId) {
        String sql = "INSERT INTO movimentacao (conta_id, tipo_movimentacao, valor, conta_destino_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, contaId);
            stmt.setString(2, tipo.name());
            stmt.setDouble(3, valor);
            if (contaDestinoId != null) {
                stmt.setInt(4, contaDestinoId);
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao registrar movimentação: " + e.getMessage());
            return false;
        }
    }

    /*     * Lista todas as movimentações de uma conta.

     */
    public List<Movimentacao> listarMovimentacoesPorContaId(int contaId) {
        String sql = "SELECT * FROM movimentacao WHERE conta_id = ? ORDER BY data_hora DESC";
        List<Movimentacao> movimentacoes = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, contaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Movimentacao mov = new Movimentacao();
                    mov.setId(rs.getInt("id"));
                    mov.setContaId(rs.getInt("conta_id"));
                    mov.setTipoMovimentacao(rs.getString("tipo_movimentacao"));
                    mov.setValor(rs.getBigDecimal("valor"));
                    mov.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
                    mov.setContaDestinoId(rs.getObject("conta_destino_id", Integer.class));
                    movimentacoes.add(mov);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar movimentações: " + e.getMessage());
        }
        return movimentacoes;
    }
}

