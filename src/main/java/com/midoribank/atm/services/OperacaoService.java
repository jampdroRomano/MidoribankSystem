package com.midoribank.atm.services;

import com.midoribank.atm.dao.ConnectionFactory;
import com.midoribank.atm.dao.ContaDAO;
import com.midoribank.atm.dao.MovimentacaoDAO;
import com.midoribank.atm.models.UserProfile;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;


public class OperacaoService {

    private final ContaDAO contaDAO;
    private final MovimentacaoDAO movimentacaoDAO;

    public OperacaoService() {
        this.contaDAO = new ContaDAO();
        this.movimentacaoDAO = new MovimentacaoDAO();
    }

    /**
     * Executa uma operação de saque de forma assíncrona e transacional.

     */
    public CompletableFuture<Boolean> executarSaque(UserProfile user, double valor) {
        return com.midoribank.atm.utils.LoadingUtils.runWithLoading("Realizando saque...", () -> {
            Connection conn = null;
            try {
                conn = ConnectionFactory.getConnection();
                conn.setAutoCommit(false); // Inicia a transação

                double novoSaldo = user.getSaldo() - valor;

                // Atualiza o saldo da conta
                boolean saldoAtualizado = contaDAO.atualizarSaldo(user.getNumeroConta(), novoSaldo, conn);

                // Registra a movimentação de saque
                boolean movimentacaoRegistrada = movimentacaoDAO.registrarMovimentacao(
                        conn,
                        user.getContaId(),
                        MovimentacaoDAO.TipoMovimentacao.SAQUE,
                        valor,
                        null
                );

                if (saldoAtualizado && movimentacaoRegistrada) {
                    conn.commit(); // Confirma a transação
                    return true;
                } else {
                    throw new SQLException("Falha ao registrar saque, revertendo.");
                }

            } catch (SQLException e) {
                System.err.println("Erro na transação de saque: " + e.getMessage());
                try {
                    if (conn != null) conn.rollback(); // Reverte a transação em caso de erro
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

    /**
     * Executa uma operação de depósito de forma assíncrona e transacional.

     */
    public CompletableFuture<Boolean> executarDeposito(UserProfile user, double valor) {
        return com.midoribank.atm.utils.LoadingUtils.runWithLoading("Realizando depósito...", () -> {
            Connection conn = null;
            try {
                conn = ConnectionFactory.getConnection();
                conn.setAutoCommit(false); // Inicia a transação

                double novoSaldo = user.getSaldo() + valor;

                // Atualiza o saldo da conta
                boolean saldoAtualizado = contaDAO.atualizarSaldo(user.getNumeroConta(), novoSaldo, conn);

                // Registra a movimentação de depósito
                boolean movimentacaoRegistrada = movimentacaoDAO.registrarMovimentacao(
                        conn,
                        user.getContaId(),
                        MovimentacaoDAO.TipoMovimentacao.DEPOSITO,
                        valor,
                        null
                );

                if (saldoAtualizado && movimentacaoRegistrada) {
                    conn.commit(); // Confirma a transação
                    return true;
                } else {
                    throw new SQLException("Falha ao registrar depósito, revertendo.");
                }

            } catch (SQLException e) {
                System.err.println("Erro na transação de depósito: " + e.getMessage());
                try {
                    if (conn != null) conn.rollback(); // Reverte a transação em caso de erro
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

    /**
     * Executa uma operação de transferência entre duas contas de forma assíncrona e transacional.

     */
    public CompletableFuture<Boolean> executarTransferencia(UserProfile userOrigem, UserProfile userDestino, double valor) {
        return com.midoribank.atm.utils.LoadingUtils.runWithLoading("Realizando transferência...", () -> {
            Connection conn = null;
            try {
                Thread.sleep(2000); // Simula um tempo de processamento
                conn = ConnectionFactory.getConnection();
                conn.setAutoCommit(false); // Inicia a transação

                // Atualiza o saldo da conta de origem
                double novoSaldoOrigem = userOrigem.getSaldo() - valor;
                boolean saldoOrigemAtualizado = contaDAO.atualizarSaldo(userOrigem.getNumeroConta(), novoSaldoOrigem, conn);

                // Atualiza o saldo da conta de destino
                double novoSaldoDestino = userDestino.getSaldo() + valor;
                boolean saldoDestinoAtualizado = contaDAO.atualizarSaldo(userDestino.getNumeroConta(), novoSaldoDestino, conn);

                // Registra a movimentação de envio na conta de origem
                boolean movRegistradaOrigem = movimentacaoDAO.registrarMovimentacao(
                        conn,
                        userOrigem.getContaId(),
                        MovimentacaoDAO.TipoMovimentacao.TRANSFERENCIA_ENVIADA,
                        valor,
                        userDestino.getContaId() 
                );

                // Registra a movimentação de recebimento na conta de destino
                boolean movRegistradaDestino = movimentacaoDAO.registrarMovimentacao(
                        conn,
                        userDestino.getContaId(),
                        MovimentacaoDAO.TipoMovimentacao.TRANSFERENCIA_RECEBIDA,
                        valor,
                        userOrigem.getContaId() 
                );

                if (saldoOrigemAtualizado && saldoDestinoAtualizado && movRegistradaOrigem && movRegistradaDestino) {
                    conn.commit(); // Confirma a transação
                    return true;
                } else {
                    throw new SQLException("Falha ao registrar transferência em todas as partes, revertendo.");
                }

            } catch (SQLException | InterruptedException e) {
                System.err.println("Erro na transação de transferência: " + e.getMessage());
                try {
                    if (conn != null) conn.rollback(); // Garante o rollback em caso de erro
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
