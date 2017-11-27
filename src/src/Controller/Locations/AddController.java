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
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javax.swing.JOptionPane;
import src.Controller.Helpers.Date;
import src.Controller.Helpers.MaskFieldUtil;
import src.Main;
import src.Model.Database;
import src.Model.Location;

public class AddController extends AppController {

    @FXML
    private ComboBox<String> comboClientTypes;

    String clientsTypes[] = {"Pessoa Física", "Pessoa Jurídica"};
    
    @FXML
    private Label lblProjector;

    @FXML
    private Label lblCpfCnpj;

    @FXML
    private Button btnSave;

    @FXML
    private Label lblName;

    @FXML
    private TextField txtSerialNumber;

    @FXML
    private Label lblBrandModel;

    @FXML
    private TextField txtCpf;

    @FXML
    private Label lblClient;

    @FXML
    private Label lblError;
    
    @FXML
    private TextField txtCnpj;
    
    @FXML
    private DatePicker txtDevolutionDate;
    
    @FXML
    private Label lblLocationDate;

    @FXML
    private Label lblDevolutionDate;
    
    @FXML
    private Label lblPrice;

    @FXML
    private TextField txtPrice;
    
    @FXML
    private DatePicker txtLocationDate;
    
    private Location location = new Location();

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
        comboClientTypes.getItems().addAll(clientsTypes);
        comboClientTypes.getSelectionModel().select(0);
        MaskFieldUtil.cpfField(txtCpf);
        MaskFieldUtil.cnpjField(txtCnpj);
        configureBindings();
    }
    
    @FXML
    void searchProjectorBySerialNumber() {
        findProjector();
    }
    
    private void findProjector() {
        try {
            String SQL = "SELECT * FROM projectors WHERE projector_state = 'Disponível' AND serial_number = ?;";
            
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setString(1, txtSerialNumber.getText());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                location.setProjectorId(rs.getInt("id"));
                lblError.setVisible(false);
                lblProjector.setVisible(true);
                lblBrandModel.setVisible(true);
                lblBrandModel.setText(rs.getString("brand") + " - " + rs.getString("model"));
            } else {
                lblProjector.setVisible(false);
                lblBrandModel.setVisible(false);
                lblError.setVisible(true);
                lblError.setText("Nenhum Projetor encontrado ou não está disponível");
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar Projetor!\n Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @FXML
    void searchClientByCpf() {
        if (txtCpf.getText().length() == 14) {
            findPhysicalPerson();
        }
    }
    
    private void findPhysicalPerson() {
        try {
            String SQL = "SELECT * FROM physical_persons p, clients c WHERE p.id = c.id AND p.cpf = ?;";
            
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setString(1, MaskFieldUtil.removeCharCpf(txtCpf.getText()));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                location.setClientId(rs.getInt("c.id"));
                lblError.setVisible(false);
                lblClient.setVisible(true);
                lblName.setVisible(true);
                lblName.setText(rs.getString("c.name"));
            } else {
                lblClient.setVisible(false);
                lblName.setVisible(false);
                lblError.setVisible(true);
                lblError.setText("Nenhum Cliente encontrado");
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar Cliente!\n Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configureBindings() {
        
        BooleanBinding aux = lblClient.visibleProperty().and(lblProjector.visibleProperty());
        lblDevolutionDate.visibleProperty().bind(aux);
        lblLocationDate.visibleProperty().bind(aux);
        txtDevolutionDate.visibleProperty().bind(aux);
        txtLocationDate.visibleProperty().bind(aux);
        lblPrice.visibleProperty().bind(aux);
        txtPrice.visibleProperty().bind(aux);
        
        txtLocationDate.setValue(Date.formatterDate(Date.getCurrentDate()));
        
        BooleanBinding isFill = txtLocationDate.valueProperty().isNotNull().and(txtDevolutionDate.valueProperty().isNotNull()).and(txtPrice.textProperty().isNotEmpty());
        btnSave.disableProperty().bind(isFill.not());
        
        comboClientTypes.valueProperty().addListener((obs, oldValue, newValue) -> {
            lblClient.setVisible(false);
            lblName.setVisible(false);
            if (newValue.equals("Pessoa Física")) {
                lblCpfCnpj.setText("CPF");
                txtCpf.setVisible(true);
                txtCnpj.setVisible(false);
                txtCnpj.setText("");
            } else {
                lblCpfCnpj.setText("CNPJ");
                txtCnpj.setVisible(true);
                txtCpf.setVisible(false);
                txtCpf.setText("");
            }
        });
    }
    
    @FXML
    void saveLocation() {
        recordLocation();
        
        if (add()) {
            updateProjectorToLeased();
            JOptionPane.showMessageDialog(null, "Locação Registrada com Sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            Main.loadScene("View/Locations/search.fxml", "Locações");
        } else {
            JOptionPane.showMessageDialog(null, "Falha ao registrar locação", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void recordLocation() {
        location.setLocationDate(txtLocationDate.getValue().toString());
        location.setDevolutionDate(txtDevolutionDate.getValue().toString());
        location.setPrice(Float.parseFloat(txtPrice.getText()));
        location.setReturned(false);
    }
    
    private boolean add() {
        try {
            String SQL = "INSERT INTO locations (location_date, devolution_date, price, returned, client_id, projector_id) "
                    + "VALUES(?, ?, ?, ?, ?, ?)";
            
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setString(1, location.getLocationDate());
            pstmt.setString(2, location.getDevolutionDate());
            pstmt.setFloat(3, location.getPrice());
            pstmt.setBoolean(4, location.isReturned());
            pstmt.setInt(5, location.getClientId());
            pstmt.setInt(6, location.getProjectorId());
            
            pstmt.execute();
            return true;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao adicionar Locação!\n Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    private void updateProjectorToLeased() {
        try {
            String SQL = "UPDATE projectors SET projector_state = 'Locado' WHERE id = ?";
            
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setInt(1, location.getProjectorId());
            
            pstmt.execute();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao atualizar estado do projetor!\n Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    @FXML
    void searchClientByCnpj() {
        if (txtCnpj.getText().length() == 18) {
            findJuridicalPerson();
        }
    }
    
    private void findJuridicalPerson() {
        try {
            String SQL = "SELECT * FROM juridical_persons p, clients c WHERE p.id = c.id AND p.cnpj = ?;";
            
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setString(1, MaskFieldUtil.removeCharCnpj(txtCnpj.getText()));
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                location.setClientId(rs.getInt("c.id"));
                lblError.setVisible(false);
                lblName.setVisible(true);
                lblClient.setVisible(true);
                lblName.setText(rs.getString("c.name"));
            } else {
                lblClient.setVisible(false);
                lblName.setVisible(false);
                lblError.setVisible(true);
                lblError.setText("Nenhum Cliente encontrado");
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar Cliente!\n Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

}
