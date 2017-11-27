package src.Controller.Clients;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javax.swing.JOptionPane;
import src.Controller.Helpers.MaskFieldUtil;
import src.Main;
import src.Model.Database;
import src.Model.JuridicalPerson;
import src.Model.PhysicalPerson;

public class AddController extends AppController {

    @FXML
    private ComboBox<String> comboCities;

    @FXML
    private Button btnSave;

    @FXML
    private TextField txtName;

    @FXML
    private DatePicker txtBirthday;

    @FXML
    private ComboBox<String> comboStates;

    @FXML
    private ComboBox<String> comboClientsTypes;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtCpf;

    @FXML
    private TextField txtCnpj;

    @FXML
    private TextField txtTelephone;

    @FXML
    private TextField txtRg;
    
    @FXML
    private Label lblError;
    
    @FXML
    private Label lblRgRequired;
    
    @FXML
    private Label lblBirthday;
    
    @FXML
    private Label lblCpfCnpj;
     
      @FXML
    private Label lblRg;
    
    private Database database = new Database();
    
    private Connection connection;

    String clientsTypes[] = {"Pessoa Física", "Pessoa Jurídica"};
    
    @FXML
    public void initialize() {
        comboClientsTypes.getItems().addAll(clientsTypes);
        comboClientsTypes.getSelectionModel().select(0);
        getStates();
        comboStates.getSelectionModel().select(0);
        getCities(comboStates.getSelectionModel().getSelectedItem());
        MaskFieldUtil.cpfField(txtCpf);
        configureBindings();
    }
        
    public AddController() {
        if (!database.connect()) {
            System.err.println("Failed connected to database!");
            System.exit(0);
        } else {
            this.connection = database.getConnection();
        }
    }
    
    private void getStates() {
        try {
            String SQL = "SELECT * FROM states ORDER BY name ASC;";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs != null) {
                while (rs.next()) {
                    comboStates.getItems().add(rs.getString("name"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Failed add States in ComboBox!");
        }
    }
    
    private void getCities(String stateName) {
        try {
            comboCities.getItems().clear();
            String SQL = "SELECT * FROM cities c, states s WHERE c.state_id = s.id AND s.name = ? ORDER BY c.name ASC;";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setString(1, stateName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs != null) {
                while (rs.next()) {
                    comboCities.getItems().add(rs.getString("c.name"));
                }
            }
            comboCities.getSelectionModel().select(0);
        } catch (SQLException e) {
            System.err.println("Failed add States in ComboBox!");
        }
    }
    
    private void configureBindings() {
        comboStates.valueProperty().addListener((obs, oldValue, newValue) -> {
            getCities(newValue);
        });
        
        comboClientsTypes.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.equals("Pessoa Física")) {
                lblRg.setVisible(true);
                txtRg.setVisible(true);
                txtCpf.setVisible(true);
                txtCnpj.setVisible(false);
                txtBirthday.setVisible(true);
                lblBirthday.setVisible(true);
                lblRgRequired.setVisible(true);
                lblCpfCnpj.setText("CPF");
                MaskFieldUtil.cpfField(txtCpf);
            } else {
                lblRg.setVisible(false);
                txtRg.setVisible(false);
                txtCpf.setVisible(false);
                txtCnpj.setVisible(true);
                txtBirthday.setVisible(false);
                lblBirthday.setVisible(false);
                lblRgRequired.setVisible(false);
                txtBirthday.setVisible(false);
                lblCpfCnpj.setText("CNPJ");
                MaskFieldUtil.cnpjField(txtCnpj);
            }
        });
    }
    
    @FXML
    void recordClient() {
        if (txtName.getText().isEmpty()) {
            lblError.setVisible(true);
            lblError.setText("Preencha todos os Campos");
        } else {
            if (comboClientsTypes.getSelectionModel().getSelectedItem().equals("Pessoa Física")) {
                if (txtCpf.getText().isEmpty() || txtRg.getText().isEmpty()) {
                    lblError.setVisible(true);
                    lblError.setText("Preencha todos os Campos");
                } else {
                    if (txtCpf.getText().length() != 14) {
                        lblError.setText("Preencha o CPF corretamente.");
                    } else if (txtRg.getText().length() != 10) {
                        lblError.setText("Preencha o RG corretamente.");
                    } else {
                        String state = comboStates.getSelectionModel().getSelectedItem();
                        String city = comboCities.getSelectionModel().getSelectedItem();
                        int cityId = getCityId(state, city);
                        PhysicalPerson physicalPerson = new PhysicalPerson();
                        physicalPerson.setName(txtName.getText());
                        
                        if (!txtEmail.getText().isEmpty()) {
                            physicalPerson.setEmail(txtEmail.getText());
                        }
                        if (!txtTelephone.getText().isEmpty()) {
                            physicalPerson.setTelephone(txtTelephone.getText());
                        }
                        if (txtBirthday.getValue() != null) {
                            physicalPerson.setBirthday(String.valueOf(txtBirthday.getValue()));
                        }
                        
                        physicalPerson.setCpf(MaskFieldUtil.removeCharCpf(txtCpf.getText()));
                        physicalPerson.setRg(txtRg.getText());
                        physicalPerson.setCityId(cityId);

                        if (addPhysicalPerson(physicalPerson)) {
                            JOptionPane.showMessageDialog(null, "Cliente cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                            Main.loadScene("View/Clients/search.fxml", "Clientes");
                        } else {
                            JOptionPane.showMessageDialog(null, "Falha ao cadastrar Cliente!", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else {
                if (txtCnpj.getText().isEmpty()) {
                    lblError.setVisible(true);
                    lblError.setText("Preencha todos os Campos");
                } else {
                    String state = comboStates.getSelectionModel().getSelectedItem();
                    String city = comboCities.getSelectionModel().getSelectedItem();
                    int cityId = getCityId(state, city);
                    JuridicalPerson juridicalPerson = new JuridicalPerson();
                    juridicalPerson.setName(txtName.getText());

                    if (!txtEmail.getText().isEmpty()) {
                        juridicalPerson.setEmail(txtEmail.getText());
                    }
                    if (!txtTelephone.getText().isEmpty()) {
                        juridicalPerson.setTelephone(txtTelephone.getText());
                    }

                    juridicalPerson.setCnpj(MaskFieldUtil.removeCharCnpj(txtCnpj.getText()));
                    juridicalPerson.setCityId(cityId);

                    if (addJuridicalPerson(juridicalPerson)) {
                        JOptionPane.showMessageDialog(null, "Cliente cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        Main.loadScene("View/Clients/search.fxml", "Clientes");
                    } else {
                        JOptionPane.showMessageDialog(null, "Falha ao cadastrar Cliente!", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
    
    private int getCityId(String state, String city) {
        try {
            String SQL = "SELECT * FROM states s, cities c WHERE c.state_id = s.id AND s.name = ? AND c.name = ?;";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setString(1, state);
            pstmt.setString(2, city);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs != null) {
                while (rs.next()) {
                    return rs.getInt("c.id");
                }
                
            }
            
        } catch (SQLException e) {
            System.err.println("Failed get City Id!");
        }
        return 0;
    }
    
    private boolean addPhysicalPerson(PhysicalPerson physicalPerson) {
        try {
            String SQL = "INSERT INTO clients (name, email, telephone, city_id) VALUES (?, ?, ?, ?);";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setString(1, physicalPerson.getName());
            pstmt.setString(2, physicalPerson.getEmail());
            pstmt.setString(3, physicalPerson.getTelephone());
            pstmt.setInt(4, physicalPerson.getCityId());
            
            pstmt.execute();
            physicalPerson.setId(getClientId());

            SQL = "INSERT INTO physical_persons (id, cpf, rg, birthday) VALUES (?, ?, ?, ?);";
            pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setInt(1, physicalPerson.getId());
            pstmt.setString(2, physicalPerson.getCpf());
            pstmt.setString(3, physicalPerson.getRg());
            pstmt.setString(4, physicalPerson.getBirthday());

            pstmt.execute();

            return true;
            
        } catch (SQLException e) {
            System.out.println("Erro: "+ e.getMessage());
            System.err.println("Failed add Client!");
        }
        return false;
    }
    
    private boolean addJuridicalPerson(JuridicalPerson juridicalPerson) {
        try {
            String SQL = "INSERT INTO clients (name, email, telephone, city_id) VALUES (?, ?, ?, ?);";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setString(1, juridicalPerson.getName());
            pstmt.setString(2, juridicalPerson.getEmail());
            pstmt.setString(3, juridicalPerson.getTelephone());
            pstmt.setInt(4, juridicalPerson.getCityId());
            
            pstmt.execute();
            juridicalPerson.setId(getClientId());

            SQL = "INSERT INTO juridical_persons (id, cnpj) VALUES (?, ?);";
            pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setInt(1, juridicalPerson.getId());
            pstmt.setString(2, juridicalPerson.getCnpj());

            pstmt.execute();

            return true;
            
        } catch (SQLException e) {
            System.out.println("Erro: "+ e.getMessage());
            System.err.println("Failed add Client!");
        }
        return false;
    }
    
    private int getClientId() {
        try {
            String SQL = "SELECT MAX(id) AS id FROM clients;";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs != null) {
                while(rs.next()) {
                    return rs.getInt("id");
                }
            }
           
        } catch (SQLException e) {
            System.out.println("Erro: "+ e.getMessage());
            System.err.println("Failed add Client!");
        }
        return 0;
    }
}
