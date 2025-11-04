package com.midoribank.atm.services;

import com.midoribank.atm.dao.RecuperacaoSenhaDAO;
import com.midoribank.atm.dao.UserDAO;
import com.midoribank.atm.models.UserProfile;
import java.time.LocalDateTime;
import java.util.Random;

public class RecuperacaoSenhaService {

    private final UserDAO userDAO;
    private final RecuperacaoSenhaDAO recuperacaoDAO;
    private final EmailService emailService;
    private final Random random;

    public RecuperacaoSenhaService() {
        this.userDAO = new UserDAO();
        this.recuperacaoDAO = new RecuperacaoSenhaDAO();
        this.emailService = new EmailService();
        this.random = new Random();
    }

    private String gerarCodigoAleatorio() {
        int codigo = 100000 + random.nextInt(900000);
        return String.valueOf(codigo);
    }

    private String criarCorpoEmail(String nomeUsuario, String codigo) {
        // Usa String.format para inserir o nome e o código no template HTML
        return String.format(
                "<html lang=\"pt-BR\">" +
                        "<head><meta charset=\"UTF-8\"></head>" +
                        "<body style=\"font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4;\">" +
                        "  <div style=\"width: 90%%; max-width: 600px; margin: 20px auto; padding: 30px; background-color: #ffffff; border: 1px solid #ddd; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.05);\">" +
                        "    " +
                        "    " +
                        "    <h1 style=\"font-size: 24px; color: #292530; margin: 0 0 25px 0;\">" +
                        "      <span style=\"font-weight: bold;\">Midori</span><span style=\"font-weight: bold; color: #14FF00;\">Bank</span> - Recuperação de Senha" +
                        "    </h1>" +
                        "    " +
                        "    " +
                        "    <p style=\"font-size: 16px; color: #333; line-height: 1.6;\">Olá, <strong>%s</strong>!</p>" +
                        "    <p style=\"font-size: 16px; color: #333; line-height: 1.6;\">Recebemos uma solicitação para redefinir a senha da sua conta no MidoriBank.</p>" +
                        "    <p style=\"font-size: 16px; color: #333; line-height: 1.6;\">Seu código de verificação é:</p>" +
                        "    " +
                        "    " +
                        "    <div style=\"background-color: #292530; border-radius: 5px; padding: 20px 25px; text-align: center; margin: 25px 0;\">" +
                        "      <strong style=\"font-size: 36px; color: #14FF00; letter-spacing: 8px; font-family: 'Courier New', Courier, monospace;\">%s</strong>" +
                        "    </div>" +
                        "    " +
                        "    <p style=\"font-size: 16px; color: #333; line-height: 1.6;\">Este código expira em 15 minutos.</p>" +
                        "    " +
                        "    " +
                        "    <p style=\"font-size: 14px; color: #777; line-height: 1.6; margin-top: 20px;\">Se você não solicitou isso, por favor, ignore este e-mail.</p>" +
                        "    <hr style=\"border: 0; border-top: 1px solid #eee; margin: 30px 0;\">" +
                        "    <p style=\"font-size: 14px; color: #555;\">Atenciosamente,<br><strong>Equipe MidoriBank</strong></p>" +
                        "  </div>" +
                        "</body>" +
                        "</html>",
                nomeUsuario, codigo
        );
    }

    public java.util.concurrent.CompletableFuture<Boolean> iniciarRecuperacao(String email) {
        return com.midoribank.atm.utils.LoadingUtils.runWithLoading("Enviando código...", () -> {
            UserProfile user = userDAO.getProfileBasico(email);

            if (user == null) {
                System.err.println("Tentativa de recuperação para e-mail não cadastrado: " + email);
                return false;
            }

            int usuarioId = user.getId();
            String nome = user.getNome();
            String codigo = gerarCodigoAleatorio();
            LocalDateTime expiracao = LocalDateTime.now().plusMinutes(15);
            String corpoEmail = criarCorpoEmail(nome, codigo);
            String assunto = "MidoriBank - Código de Recuperação de Senha";

            recuperacaoDAO.invalidarCodigosAntigos(usuarioId);

            boolean salvoNoDb = recuperacaoDAO.salvarCodigo(usuarioId, codigo, expiracao);

            if (salvoNoDb) {
                return emailService.enviarEmail(email, assunto, corpoEmail).join(); // .join() para esperar o resultado
            } else {
                System.err.println("Falha ao salvar o código no banco para o usuário: " + usuarioId);
                return false;
            }
        });
    }

    public java.util.concurrent.CompletableFuture<Boolean> validarCodigo(String email, String codigo) {
        return com.midoribank.atm.utils.LoadingUtils.runWithLoading("Validando código...", () -> {
            UserProfile user = userDAO.getProfileBasico(email);
            if (user == null) {
                return false;
            }
            return recuperacaoDAO.validarCodigo(user.getId(), codigo);
        });
    }

    public java.util.concurrent.CompletableFuture<Boolean> redefinirSenha(String email, String novaSenha) {
        return com.midoribank.atm.utils.LoadingUtils.runWithLoading("Redefinindo senha...", () -> {
            UserProfile user = userDAO.getProfileBasico(email);
            if (user == null) {
                return false;
            }

            int usuarioId = user.getId();
            boolean sucesso = recuperacaoDAO.atualizarSenha(usuarioId, novaSenha);

            if (sucesso) {
                recuperacaoDAO.invalidarCodigosAntigos(usuarioId);
            }
            return sucesso;
        });
    }
}