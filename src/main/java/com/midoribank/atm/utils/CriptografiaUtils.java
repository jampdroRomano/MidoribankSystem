package com.midoribank.atm.utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Classe utilitária para operações de criptografia, como hashing de senhas.

 */
public class CriptografiaUtils {

    /**
     * Gera um hash de uma senha em texto plano usando o algoritmo BCrypt.

     */
    public static String hashPassword(String plainTextPassword) {
        // O segundo argumento (workload) do gensalt define a força do hash. 12 é um bom valor padrão.
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    /**
     * Verifica se uma senha em texto plano corresponde a um hash existente.

     */
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        // Verifica se o hash é nulo ou não tem o formato esperado do BCrypt.
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            return false;
        }

        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Trata o caso de um hash inválido.
            System.err.println("Erro ao verificar a senha: " + e.getMessage());
            return false;
        }
    }
}
