package com.midoribank.atm.services;

import com.midoribank.atm.App;
import java.io.InputStream;
import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {

    private String EMAIL_REMETENTE;
    private String SENHA_REMETENTE;

    private final String SMTP_HOST = "smtp.gmail.com";
    private final String SMTP_PORT = "587";

    public EmailService() {
        carregarCredenciais();
    }

    private void carregarCredenciais() {
        Properties props = new Properties();
        try (InputStream input = App.class.getResourceAsStream("/config.properties")) {

            if (input == null) {
                System.err.println("Erro fatal: Não foi possível encontrar o config.properties.");
                System.err.println("Verifique se o arquivo está em 'src/main/resources/config.properties'");
                throw new RuntimeException("config.properties não encontrado.");
            }

            props.load(input);
            this.EMAIL_REMETENTE = props.getProperty("GMAIL_USER");
            this.SENHA_REMETENTE = props.getProperty("GMAIL_PASSWORD");

            if(this.EMAIL_REMETENTE == null || this.SENHA_REMETENTE == null ||
                    this.EMAIL_REMETENTE.isEmpty() || this.SENHA_REMETENTE.isEmpty() ||
                    this.SENHA_REMETENTE.equals("sua-senha-de-app-16-letras-aqui")) {

                System.err.println("Erro fatal: GMAIL_USER ou GMAIL_PASSWORD não estão definidos corretamente no config.properties.");
                throw new RuntimeException("Credenciais de e-mail não configuradas no config.properties.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erro ao carregar credenciais do config.properties.", e);
        }
    }

    public boolean enviarEmail(String destinatario, String assunto, String corpoEmail) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_REMETENTE, SENHA_REMETENTE);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_REMETENTE));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(assunto);
            message.setContent(corpoEmail, "text/html; charset=utf-8");

            Transport.send(message);

            System.out.println("Email enviado com sucesso para: " + destinatario);
            return true;

        } catch (MessagingException e) {
            System.err.println("Erro ao enviar e-mail: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}