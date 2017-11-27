/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.Controller.Locations;

/**
 *
 * @author jefferson
 */

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javax.swing.JOptionPane;
import src.Main;
import src.Model.Database;
import src.Model.Location;

public class RenewController extends AppController {

    @FXML
    private Button btnSave;

    @FXML
    private DatePicker txtDevolutionDate;

    @FXML
    private TextField txtPrice;

    @FXML
    private Button btnExit;
    
    private Database database = new Database();
    private Connection connection;
    
    public RenewController() {
        if (!database.connect()) {
            System.err.println("Failed connected to database!");
            System.exit(0);
        } else {
            this.connection = database.getConnection();
        }
    }
    
    @FXML
    public void initialize() {
        configureBindings();
    }
    
    private void configureBindings() {
        BooleanBinding enableButtons = txtDevolutionDate.valueProperty().isNotNull().and(txtPrice.textProperty().isNotEmpty());
        
        btnSave.disableProperty().bind(enableButtons.not());
    }

    @FXML
    void saveLocation() {
        Location location = Context.getInstance().getLocation();
        location.setDevolutionDate(txtDevolutionDate.getValue().toString());
        location.setPrice(Float.parseFloat(txtPrice.getText()));
        try {
            String SQL = "UPDATE locations SET devolution_date = ?, price = ? WHERE id = ?;";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setString(1, location.getDevolutionDate());
            pstmt.setFloat(2, location.getPrice());
            pstmt.setInt(3, location.getId());
            
            pstmt.execute();
            JOptionPane.showMessageDialog(null, "Renovação realizada com sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            Main.loadScene("View/Locations/search.fxml", "Locações");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @FXML
    void goSearchLocation() {
        Main.loadScene("View/Locations/search.fxml", "Locações");
    }
}
