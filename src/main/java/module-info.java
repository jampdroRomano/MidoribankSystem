module com.midoribank.atm {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires java.sql;
    requires jbcrypt;
    requires jakarta.mail;

    requires com.github.librepdf.openpdf;
    requires java.desktop;     

    opens com.midoribank.atm to javafx.fxml;

    opens com.midoribank.atm.controllers to javafx.fxml;

    exports com.midoribank.atm;
}
