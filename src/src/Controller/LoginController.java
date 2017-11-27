/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.Controller;

/**
 *
 * @author jefferson
 */
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javax.swing.JOptionPane;
import src.Main;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    void verifyLogin() {
        if (txtUsername.getText().equals("admin") && txtPassword.getText().equals("admin")) {
            Main.loadScene("View/Home.fxml", "Página Inicial");
        } else {
            JOptionPane.showMessageDialog(null, "Usuário ou Senha incorreta", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

     @FXML
    void showHelp() {
         System.out.println("Help");
    }

    @FXML
    void goExit() {

    }

}
