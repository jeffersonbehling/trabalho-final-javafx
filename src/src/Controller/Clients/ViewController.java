/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.Controller.Clients;

/**
 *
 * @author jefferson
 */
import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javax.swing.JOptionPane;
import src.Controller.Helpers.Date;
import src.Model.Database;

public class ViewController extends AppController {

    @FXML
    private Label lblCpfCnpj;

    @FXML
    private Label labelRg;

    @FXML
    private Label lblDataNasc;

    @FXML
    private Label lblName;

    @FXML
    private Label lblTelephone;

    @FXML
    private Label lblEmail;

    @FXML
    private Label lblRg;

    @FXML
    private Label lblCity;

    @FXML
    private Label lblBirthday;

    @FXML
    private Label lblState;
    
    private int id;
    
    private Database database = new Database();
    
    private Connection connection;
    
    public ViewController() {
        if (!database.connect()) {
            System.err.println("Failed connected to database!");
            System.exit(0);
        } else {
            this.connection = database.getConnection();
        }
    }
    
    @FXML
    public void initialize() {
        this.id = Context.getInstance().client().getId();
        loadClient(id);
    }
    
    private void loadClient(int id) {
        if (isPhysicalPerson(id)) {
            loadPhysicalPersonData(id);
        } else {
            lblRg.setVisible(false);
            labelRg.setVisible(false);
            lblBirthday.setVisible(false);
            lblDataNasc.setVisible(false);
            loadJuridicalPersonData(id);
        }
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
    
    private void loadPhysicalPersonData(int id) {
        try {
            String SQL = "SELECT * FROM physical_persons p, clients c, cities ci, states s WHERE ci.state_id = s.id AND"
                    + " ci.id = c.city_id AND c.id = p.id AND p.id = ?";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs != null) {
                while(rs.next()) {
                    lblName.setText(rs.getString("c.name"));
                    lblCpfCnpj.setText(rs.getString("p.cpf"));
                    lblEmail.setText(rs.getString("c.email"));
                    lblRg.setText(rs.getString("p.rg"));
                    lblState.setText(rs.getString("s.name"));
                    lblCity.setText(rs.getString("ci.name"));
                    lblTelephone.setText(rs.getString("c.telephone"));
                    lblBirthday.setText(""+Date.formatterDate(rs.getString("p.birthday")));
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar informações do cliente!\n Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadJuridicalPersonData(int id) {
        try {
            String SQL = "SELECT * FROM juridical_persons p, clients c, cities ci, states s WHERE ci.state_id = s.id AND"
                    + " ci.id = c.city_id AND c.id = p.id AND p.id = ?";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs != null) {
                while(rs.next()) {
                    lblName.setText(rs.getString("c.name"));
                    lblCpfCnpj.setText(rs.getString("p.cnpj"));
                    lblEmail.setText(rs.getString("c.email"));
                    lblState.setText(rs.getString("s.name"));
                    lblCity.setText(rs.getString("ci.name"));
                    lblTelephone.setText(rs.getString("c.telephone"));
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao carregar informações do cliente!\n Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

}

