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
import src.Controller.Helpers.Date;
import src.Controller.Helpers.MaskFieldUtil;
import src.Main;
import src.Model.Client;
import src.Model.Database;
import src.Model.JuridicalPerson;
import src.Model.PhysicalPerson;

public class EditController extends AppController {

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
    private Label lblCpfCnpj;
    
    @FXML
    private Label lblRgRequired;
     
      @FXML
    private Label lblRg;
      
    @FXML
    private Label lblDataNasc;
    
    private Database database = new Database();
    
    private Connection connection;
    
    private int id;
    
    private Client client;

    @FXML
    public void initialize() {
       this.id = Context.getInstance().client().getId();
       
       this.client = getClient(id);
       
        if (isPhysicalPerson(id)) {
            txtRg.setVisible(true);
            lblRg.setVisible(true);
            txtCpf.setVisible(true);
            MaskFieldUtil.cpfField(txtCpf);
            loadPhysicalPersonDatas(id);
        } else {
            txtRg.setVisible(false);
            lblRg.setVisible(false);
            txtCpf.setVisible(false);
            lblCpfCnpj.setText("CNPJ");
            txtCnpj.setVisible(true);
            MaskFieldUtil.cnpjField(txtCnpj);
            loadJuridicalPersonDatas(id);
        }
        
        loadStatesCities();        
        configureBindings();
    }
        
    public EditController() {
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
    }
    
    @FXML
    void recordClient() {
        if (txtName.getText().isEmpty()) {
            lblError.setVisible(true);
            lblError.setText("Preencha todos os Campos");
        } else {
            if (isPhysicalPerson(id)) {
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
                        if (txtTelephone.getText() != null && !txtTelephone.getText().isEmpty()) {
                            physicalPerson.setTelephone(txtTelephone.getText());
                        }
                        if (txtBirthday.getValue() != null) {
                            physicalPerson.setBirthday(String.valueOf(txtBirthday.getValue()));
                        }
                        
                        physicalPerson.setCpf(MaskFieldUtil.removeCharCpf(txtCpf.getText()));
                        physicalPerson.setRg(txtRg.getText());
                        physicalPerson.setCityId(cityId);

                        if (editPhysicalPerson(physicalPerson)) {
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
                } else if (txtCnpj.getText().length() != 18) {
                    lblError.setVisible(true);
                    lblError.setText("Preencha o CNPJ corretamente");
                } else {
                    JuridicalPerson juridicalPerson = new JuridicalPerson();
                    juridicalPerson.setName(txtName.getText());
                    if (!txtEmail.getText().isEmpty()) {
                            juridicalPerson.setEmail(txtEmail.getText());
                    }
                    if (txtTelephone.getText() != null && !txtTelephone.getText().isEmpty()) {
                        juridicalPerson.setTelephone(txtTelephone.getText());
                    }
                    juridicalPerson.setCnpj(MaskFieldUtil.removeCharCnpj(txtCnpj.getText()));
                    juridicalPerson.setCityId(getCityId(comboStates.getSelectionModel().getSelectedItem(), comboCities.getSelectionModel().getSelectedItem()));
                    
                    if (editJuridicalPerson(juridicalPerson)) {
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
    
    private boolean editPhysicalPerson(PhysicalPerson physicalPerson) {
        try {
            physicalPerson.setId(id);
            String SQL = "UPDATE clients SET name = ?, email = ?, telephone = ?, city_id = ? WHERE id = ?;";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setString(1, physicalPerson.getName());
            pstmt.setString(2, physicalPerson.getEmail());
            pstmt.setString(3, physicalPerson.getTelephone());
            pstmt.setInt(4, physicalPerson.getCityId());
            pstmt.setInt(5, physicalPerson.getId());
            
            pstmt.execute();

            SQL = "UPDATE physical_persons SET cpf = ?, rg = ?, birthday = ? WHERE id = ?;";
            pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setString(1, physicalPerson.getCpf());
            pstmt.setString(2, physicalPerson.getRg());
            pstmt.setString(3, physicalPerson.getBirthday());
            pstmt.setInt(4, physicalPerson.getId());

            pstmt.execute();

            return true;
            
        } catch (SQLException e) {
            System.out.println("Erro: "+ e.getMessage());
            System.err.println("Failed add Client!");
        }
        return false;
    }
    
    private boolean editJuridicalPerson(JuridicalPerson juridicalPerson) {
        try {
            juridicalPerson.setId(id);
            String SQL = "UPDATE clients SET name = ?, email = ?, telephone = ?, city_id = ? WHERE id = ?;";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setString(1, juridicalPerson.getName());
            pstmt.setString(2, juridicalPerson.getEmail());
            pstmt.setString(3, juridicalPerson.getTelephone());
            pstmt.setInt(4, juridicalPerson.getCityId());
            pstmt.setInt(5, juridicalPerson.getId());
            
            pstmt.execute();

            SQL = "UPDATE juridical_persons SET cnpj = ? WHERE id = ?;";
            pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setString(1, juridicalPerson.getCnpj());
            pstmt.setInt(2, juridicalPerson.getId());

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
    
    private boolean isPhysicalPerson(int id) {
        try {
            String SQL = "SELECT * FROM physical_persons WHERE id = ?";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                
                return true;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao verificar tipo de cliente!\n Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    private void loadPhysicalPersonDatas(int id) {
        PhysicalPerson physicalPerson = getPhysicalPerson(id);
        txtName.setText(physicalPerson.getName());
        txtCpf.setText(physicalPerson.getCpf());
        txtRg.setText(physicalPerson.getRg());
        txtTelephone.setText(physicalPerson.getTelephone());
        txtEmail.setText(physicalPerson.getEmail());
        txtBirthday.setValue(Date.formatterDate(physicalPerson.getBirthday()));
    }
    
    private void loadJuridicalPersonDatas(int id) {
        lblRgRequired.setVisible(false);
        txtBirthday.setVisible(false);
        lblDataNasc.setVisible(false);
        JuridicalPerson juridicalPerson = getJuridicalPerson(id);
        txtName.setText(juridicalPerson.getName());
        txtCnpj.setText(juridicalPerson.getCnpj());
        txtTelephone.setText(juridicalPerson.getTelephone());
        txtEmail.setText(juridicalPerson.getEmail());
    }
    
    
    private void loadStatesCities() {
        getStates();
        comboStates.getSelectionModel().select(getStateName());
        getCities(comboStates.getSelectionModel().getSelectedItem());
        comboCities.getSelectionModel().select(getCityName());
    }
    
    private String getStateName() {
        
        try {
            String SQL = "SELECT s.name FROM states s, cities c WHERE c.state_id = s.id AND c.id = ?";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setInt(1, client.getCityId());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs != null) {
                rs.next();
                return rs.getString("s.name");
            }
            
        } catch (SQLException e) {
            
        }
        return null;
    }
    
    private String getCityName() {
        
        try {
            String SQL = "SELECT name FROM cities WHERE id = ?";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setInt(1, client.getCityId());
            ResultSet rs = pstmt.executeQuery();
            
            if (rs != null) {
                rs.next();
                
                return rs.getString("name");
            }
            
        } catch (SQLException e) {
            
        }
        return null;
    }
    
    private PhysicalPerson getPhysicalPerson(int id) {
        try {
            String SQL = "SELECT * FROM physical_persons p, clients c WHERE c.id = p.id AND p.id = ?";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs != null) {
                rs.next();
                PhysicalPerson physicalPerson = new PhysicalPerson();
                physicalPerson.setId(rs.getInt("p.id"));
                physicalPerson.setName(rs.getString("c.name"));
                physicalPerson.setEmail(rs.getString("email"));
                physicalPerson.setTelephone(rs.getString("telephone"));
                physicalPerson.setCpf(rs.getString("cpf"));
                physicalPerson.setRg(rs.getString("rg"));
                physicalPerson.setBirthday(rs.getString("birthday"));
                
                return physicalPerson;
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar dados do Cliente!\n Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    private JuridicalPerson getJuridicalPerson(int id) {
        try {
            String SQL = "SELECT * FROM juridical_persons j, clients c WHERE c.id = j.id AND j.id = ?";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs != null) {
                rs.next();
                JuridicalPerson juridicalPerson = new JuridicalPerson();
                juridicalPerson.setId(rs.getInt("j.id"));
                juridicalPerson.setName(rs.getString("c.name"));
                juridicalPerson.setEmail(rs.getString("email"));
                juridicalPerson.setTelephone(rs.getString("telephone"));
                juridicalPerson.setCnpj(rs.getString("cnpj"));
                
                return juridicalPerson;
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar dados do Cliente!\n Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }
    
    private Client getClient(int id) {
        try {
            String SQL = "SELECT * FROM clients WHERE id = ?";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs != null) {
                rs.next();
                Client client = new Client();
                client.setId(rs.getInt("id"));
                client.setName(rs.getString("name"));
                client.setEmail(rs.getString("email"));
                client.setTelephone(rs.getString("telephone"));
                client.setCityId(rs.getInt("city_id"));
                
                return client;
            }
        } catch (SQLException e) {
            
        }
        return null;
    }
}
