/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.Controller.Clients;

import javafx.fxml.FXML;
import src.Controller.HomeController;
import src.Main;

/**
 *
 * @author jefferson
 */
public class AppController extends HomeController {
   
    @FXML
    void goBack() {
        Main.loadScene("View/Clients/search.fxml", "Clientes");
    }
    
}
