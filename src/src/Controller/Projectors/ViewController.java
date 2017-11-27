/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.Controller.Projectors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import src.Main;
import src.Model.Projector;

public class ViewController extends AppController {

    @FXML
    private Label lblPurchaseDate;

    @FXML
    private Label lblBrand;

    @FXML
    private Label lblAnsiLumens;

    @FXML
    private Label lblLastChange;

    @FXML
    private Label lblSerialNumber;

    @FXML
    private Label lblModel;

    @FXML
    private Label lblStatus;

    @FXML
    void goBack() {
       Main.loadScene("View/Projectors/search.fxml", "Projetores");
    }
    
    @FXML
    public void initialize() {
        Projector projector = Context.getInstance().projector();
        lblBrand.setText(projector.getBrand());
        lblModel.setText(projector.getModel());
        lblSerialNumber.setText(projector.getSerialNumber());
        lblAnsiLumens.setText(String.valueOf(projector.getAnsiLumens()));
        lblPurchaseDate.setText(projector.getPurchaseDate());
        lblStatus.setText(projector.getProjectorState());
        if (projector.getDateLastLampChange() != null) {
            lblLastChange.setText(projector.getDateLastLampChange());
        } else {
            lblLastChange.setText("----------");
        }
    }
}

