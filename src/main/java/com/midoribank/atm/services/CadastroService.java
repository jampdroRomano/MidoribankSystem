package com.midoribank.atm.services;

import com.midoribank.atm.dao.CartaoDAO;
import com.midoribank.atm.dao.ConnectionFactory;
import com.midoribank.atm.dao.ContaDAO;
import com.midoribank.atm.dao.UserDAO;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;


public class CadastroService {

    /**
     * Realiza o cadastro completo de um novo usuário, incluindo conta e cartão, de forma assíncrona e transacional.

     */
    public CompletableFuture<Boolean> realizarCadastroCompleto(String nome, String email, String senhaConta,
                                            String agencia, String numeroConta,
                                            String numeroCartao, String cvv, String senhaCartao) {

        // Executa a operação de cadastro com uma tela de loading
        return com.midoribank.atm.utils.LoadingUtils.runWithLoading("Realizando cadastro...", () -> {
            Connection conn = null;
            try {
                conn = ConnectionFactory.getConnection();
                conn.setAutoCommit(false); // Inicia a transação

                UserDAO userDAO = new UserDAO();
                ContaDAO contaDAO = new ContaDAO();
                CartaoDAO cartaoDAO = new CartaoDAO();

                // 1. Cadastra o usuário
                int usuarioId = userDAO.cadastrarUsuario(nome, email, senhaConta, conn);
                if (usuarioId == -1) {
                    throw new SQLException("Falha ao cadastrar usuário.");
                }

                // 2. Cadastra a conta
                int contaId = contaDAO.cadastrarConta(usuarioId, agencia, numeroConta, 0.0, conn);
                if (contaId == -1) {
                    throw new SQLException("Falha ao cadastrar conta.");
                }

                // 3. Cadastra o cartão
                int cartaoId = cartaoDAO.cadastrarCartao(numeroCartao, cvv, senhaCartao, contaId, conn);
                if (cartaoId == -1) {
                    throw new SQLException("Falha ao cadastrar cartão.");
                }

                conn.commit(); // Confirma a transação se tudo ocorreu bem
                return true;

            } catch (SQLException e) {
                System.err.println("Erro na transação de cadastro: " + e.getMessage());
                try {
                    if (conn != null) {
                        conn.rollback(); // Reverte a transação em caso de erro
                    }
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
