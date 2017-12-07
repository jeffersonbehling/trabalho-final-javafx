/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.Controller.Projectors;

import src.Model.Database;
import src.Model.Projector;
import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.JOptionPane;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import src.Main;

/**
 *
 * @author jefferson
 */
public class AddController extends AppController {
    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private DatePicker txtPurchaseDateField = new DatePicker(LocalDate.now());

    @FXML
    private DatePicker txtLastDateField;

    @FXML
    private TextField txtAnsiLumensField;

    @FXML
    private TextField txtModelField;

    @FXML
    private ComboBox<String> comboProjectorState;
    
    String states[] = {"Disponível", "Manutenção"};

    @FXML
    private TextField txtSerialNumberField;

    @FXML
    private TextField txtBrandField;
    
    @FXML
    private Label lblError;
    
    @FXML
    private Pane pane;
    
    private Database database = new Database();
    private Connection connection;
    
    public AddController() {
        if (!database.connect()) {
            System.err.println("Failed connected to database!");
            System.exit(0);
        } else {
            this.connection = database.getConnection();
        }
    }
    
    @FXML
    public void initialize() {
        comboProjectorState.getItems().addAll(states);
        comboProjectorState.getSelectionModel().select(0);
    }
    
    @FXML
    void recordProjector() {
        Projector projector = new Projector();
        
        if (txtBrandField.getText().isEmpty() || txtModelField.getText().isEmpty() ||
                txtSerialNumberField.getText().isEmpty() || txtPurchaseDateField.getValue() == null) {
            
            lblError.setVisible(true);

            lblError.setText("Preencha todos os campos Obrigatórios, por favor.");
        } else {
            lblError.setVisible(false);
            projector.setBrand(txtBrandField.getText());
            projector.setModel(txtModelField.getText());
            projector.setSerialNumber(txtSerialNumberField.getText());
            projector.setProjectorState(comboProjectorState.getValue().toString());
            projector.setPurchaseDate(txtPurchaseDateField.getValue().toString());
            
            if (txtLastDateField.getValue() != null) {
                projector.setDateLastLampChange(txtLastDateField.getValue().toString());
            }
            
            if (!txtAnsiLumensField.getText().isEmpty()) {
                projector.setAnsiLumens(Integer.parseInt(txtAnsiLumensField.getText()));
            }
            
            if (add(projector)) {
                JOptionPane.showMessageDialog(null, "Projetor Cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                Main.loadScene("View/Projectors/search.fxml", "Projetores");
            }
        }
    }
    
    private void clearFields() {
        txtBrandField.setText("");
        txtModelField.setText("");
        txtLastDateField.setValue(null);
        txtAnsiLumensField.setText("");
        txtSerialNumberField.setText("");
        txtPurchaseDateField.setValue(null);
    }
    
    public boolean add(Projector projector) {
        try {
            String SQL = "INSERT INTO projectors (brand, model, ansi_lumens, serial_number, purchase_date, date_last_lamp_change, projector_state) VALUES (?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setString(1, projector.getBrand());
            pstmt.setString(2, projector.getModel());
            pstmt.setInt(3, projector.getAnsiLumens());
            pstmt.setString(4, projector.getSerialNumber());
            pstmt.setString(5, projector.getPurchaseDate());
            pstmt.setString(6, projector.getDateLastLampChange());
            pstmt.setString(7, projector.getProjectorState());
            pstmt.execute();
            pstmt.close();

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Falha ao cadastras Projetor!\nErro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            System.out.println("Error: " + e.getMessage());
        }
        
        return false;
    }
}
