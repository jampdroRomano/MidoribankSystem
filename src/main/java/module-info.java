/**
 * Define o módulo da aplicação MidoriBank ATM.
 * Especifica as dependências e os pacotes que são expostos e abertos para reflexão.
 */
module com.midoribank.atm {
    // Dependências do JavaFX para a interface gráfica
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;

    // Dependência para acesso ao banco de dados
    requires java.sql;

    // Dependência para hashing de senhas
    requires jbcrypt;

    // Dependência para envio de e-mails
    requires jakarta.mail;

    // Dependência para geração de PDF (extrato)
    requires com.github.librepdf.openpdf;
    
    // Dependência para AWT, usado pela biblioteca de PDF para manipulação de cores
    requires java.desktop;     

    // Abre o pacote principal para o JavaFX FXML, permitindo o carregamento de recursos
    opens com.midoribank.atm to javafx.fxml;

    // Abre o pacote de controladores para o JavaFX FXML, permitindo a injeção de dependências
    opens com.midoribank.atm.controllers to javafx.fxml;

    // Exporta o pacote principal da aplicação
    exports com.midoribank.atm;
}

