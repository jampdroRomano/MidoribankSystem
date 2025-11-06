package com.midoribank.atm.services;

import com.midoribank.atm.dao.ConnectionFactory;
import com.midoribank.atm.dao.ContaDAO;
import com.midoribank.atm.dao.MovimentacaoDAO;
import com.midoribank.atm.models.UserProfile;
import java.sql.Connection;
import java.sql.SQLException;

public class OperacaoService {

    private final ContaDAO contaDAO;
    private final MovimentacaoDAO movimentacaoDAO;

    public OperacaoService() {
        this.contaDAO = new ContaDAO();
        this.movimentacaoDAO = new MovimentacaoDAO();
    }

    public java.util.concurrent.CompletableFuture<Boolean> executarSaque(UserProfile user, double valor) {
        return com.midoribank.atm.utils.LoadingUtils.runWithLoading("Realizando saque...", () -> {
            Connection conn = null;
            try {
                conn = ConnectionFactory.getConnection();
                conn.setAutoCommit(false);

                double novoSaldo = user.getSaldo() - valor;

                boolean saldoAtualizado = contaDAO.atualizarSaldo(user.getNumeroConta(), novoSaldo, conn);

                boolean movimentacaoRegistrada = movimentacaoDAO.registrarMovimentacao(
                        conn,
                        user.getContaId(),
                        MovimentacaoDAO.TipoMovimentacao.SAQUE,
                        valor,
                        null
                );

                if (saldoAtualizado && movimentacaoRegistrada) {
                    conn.commit();
                    return true;
                } else {
                    throw new SQLException("Falha ao registrar saque, revertendo.");
                }

            } catch (SQLException e) {
                System.err.println("Erro na transação de saque: " + e.getMessage());
                try {
                    if (conn != null) conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erro ao reverter transação: " + ex.getMessage());
                }
                return false;
            } finally {
                try {
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar conexão: " + e.getMessage());
                }
            }
        });
    }

    public java.util.concurrent.CompletableFuture<Boolean> executarDeposito(UserProfile user, double valor) {
        return com.midoribank.atm.utils.LoadingUtils.runWithLoading("Realizando depósito...", () -> {
            Connection conn = null;
            try {
                conn = ConnectionFactory.getConnection();
                conn.setAutoCommit(false);

                double novoSaldo = user.getSaldo() + valor;

                boolean saldoAtualizado = contaDAO.atualizarSaldo(user.getNumeroConta(), novoSaldo, conn);

                boolean movimentacaoRegistrada = movimentacaoDAO.registrarMovimentacao(
                        conn,
                        user.getContaId(),
                        MovimentacaoDAO.TipoMovimentacao.DEPOSITO,
                        valor,
                        null
                );

                if (saldoAtualizado && movimentacaoRegistrada) {
                    conn.commit();
                    return true;
                } else {
                    throw new SQLException("Falha ao registrar depósito, revertendo.");
                }

            } catch (SQLException e) {
                System.err.println("Erro na transação de depósito: " + e.getMessage());
                try {
                    if (conn != null) conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Erro ao reverter transação: " + ex.getMessage());
                }
                return false;
            } finally {
                try {
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar conexão: " + e.getMessage());
                }
            }
        });
    }

    public java.util.concurrent.CompletableFuture<Boolean> executarTransferencia(UserProfile userOrigem, UserProfile userDestino, double valor) {
        return com.midoribank.atm.utils.LoadingUtils.runWithLoading("Realizando transferência...", () -> {
            Connection conn = null;
            try {
                conn = ConnectionFactory.getConnection();
                conn.setAutoCommit(false); 

                double novoSaldoOrigem = userOrigem.getSaldo() - valor;
                boolean saldoOrigemAtualizado = contaDAO.atualizarSaldo(userOrigem.getNumeroConta(), novoSaldoOrigem, conn);

                // 2. Creditar na conta de destino
                double novoSaldoDestino = userDestino.getSaldo() + valor;
                boolean saldoDestinoAtualizado = contaDAO.atualizarSaldo(userDestino.getNumeroConta(), novoSaldoDestino, conn);

                // 3. Registrar movimentação de SAÍDA (Enviada)
                boolean movRegistradaOrigem = movimentacaoDAO.registrarMovimentacao(
                        conn,
                        userOrigem.getContaId(),
                        MovimentacaoDAO.TipoMovimentacao.TRANSFERENCIA_ENVIADA,
                        valor,
                        userDestino.getContaId() // ID da conta de destino
                );

                // 4. Registrar movimentação de ENTRADA (Recebida)
                boolean movRegistradaDestino = movimentacaoDAO.registrarMovimentacao(
                        conn,
                        userDestino.getContaId(),
                        MovimentacaoDAO.TipoMovimentacao.TRANSFERENCIA_RECEBIDA,
                        valor,
                        userOrigem.getContaId() // ID da conta de origem
                );

                // Se tudo deu certo, commita
                if (saldoOrigemAtualizado && saldoDestinoAtualizado && movRegistradaOrigem && movRegistradaDestino) {
                    conn.commit();
                    return true;
                } else {
                    // Se algo falhou, reverte
                    throw new SQLException("Falha ao registrar transferência em todas as partes, revertendo.");
                }

            } catch (SQLException e) {
                System.err.println("Erro na transação de transferência: " + e.getMessage());
                try {
                    if (conn != null) conn.rollback(); // Garante o rollback
                } catch (SQLException ex) {
                    System.err.println("Erro ao reverter transação: " + ex.getMessage());
                }
                return false;
            } finally {
                try {
                    if (conn != null) {
                        conn.setAutoCommit(true);
                        conn.close();
                    }
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar conexão: " + e.getMessage());
                }
            }
        });
    }
}