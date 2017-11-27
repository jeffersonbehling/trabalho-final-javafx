/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.Controller.Clients;

import com.mysql.jdbc.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import src.Main;
import src.Model.Client;
import src.Model.Database;

public class SearchController extends AppController {

    @FXML
    private Button btnEdit;

    @FXML
    private Button btnView;
    
    @FXML
    private Button btnDelete;
    
    @FXML
    private TextField txtSearch;

    @FXML
    private TableColumn<Client, String> columnTelephone;

    @FXML
    private TableColumn<Client, String> columnEmail;

    @FXML
    private TableView<Client> table;

    @FXML
    private TableColumn<Client, String> columnName;
    
    private Database database = new Database();
    private Connection connection;
    
    public SearchController() {
        if (!database.connect()) {
            System.err.println("Failed connected to database!");
            System.exit(0);
        } else {
            this.connection = database.getConnection();
        }
    }

    @FXML
    void goEditClient() {
        Client client = table.getSelectionModel().getSelectedItem();
        Context.getInstance().client().setId(client.getId());
        Main.loadScene("View/Clients/edit.fxml", "Editar Cliente");
    }

    @FXML
    void goDeleteClient() {
        Client client = table.getSelectionModel().getSelectedItem();
        Object[] options = { "Confirmar", "Cancelar" };
        int wantDelete = JOptionPane.showConfirmDialog(null, "Realmente quer excluír o Cliente " + client.getName()+ "?", "Informação", JOptionPane.YES_NO_OPTION);
        
        if (wantDelete == JOptionPane.YES_OPTION) {
            if (delete(client)) {
                JOptionPane.showMessageDialog(null, "Cliente excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                Main.loadScene("View/Clients/search.fxml", "Procurar Cliente");
            }
        }
    }

    @FXML
    void goViewClient() {
        Client client = table.getSelectionModel().getSelectedItem();
        Context.getInstance().client().setId(client.getId());
        Main.loadScene("View/Clients/view.fxml", "Visualizar Cliente");
    }
    
    @FXML
    public void initialize() {
        drawTable();
        getDatas("");
        configureBindings();
    }
    
    @FXML
    void searchClient() {
        drawTable();
        getDatas(txtSearch.getText());
    }
    
    private void drawTable() {
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        columnTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
    }
    
    private void getDatas(String search) {
        table.getItems().setAll(search(search));
    }

    private ArrayList<Client> search(String search)
    {
        try {
            String SQL = "SELECT * FROM clients WHERE name LIKE ?;";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setString(1, '%'+search+'%');
            ResultSet rs = pstmt.executeQuery();
            
            ArrayList<Client> arrayList = new ArrayList<>();
            try {
                
                while(rs.next()) {
                    Client client = new Client();
                    client.setId(rs.getInt("id"));
                    client.setName(rs.getString("name"));
                    client.setEmail(rs.getString("email"));
                    client.setTelephone(rs.getString("telephone"));
                   arrayList.add(client);
                }
            } catch (SQLException ex) {
                    System.out.println("Error: " + ex.getMessage());
            }
            pstmt.close();

            return arrayList;
            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;
    }
    
    private void configureBindings() {
        BooleanBinding notSelected = table.getSelectionModel().selectedItemProperty().isNull();
        
        btnEdit.disableProperty().bind(notSelected);
        btnDelete.disableProperty().bind(notSelected);
        btnView.disableProperty().bind(notSelected);
    }
       
    public boolean delete(Client client) {
        try {
            String SQL = "";
            if (isPhysicalPerson(client.getId())) {
                SQL = "DELETE FROM physical_persons WHERE id = ?;";
            } else {
                SQL = "DELETE FROM juridical_persons WHERE id = ?;";
            }
            
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setInt(1, client.getId());
            pstmt.execute();
            
            SQL = "DELETE FROM clients WHERE id = ?;";
            pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setInt(1, client.getId());
            pstmt.execute();

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluír Cliente!\n Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return false;
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
}
