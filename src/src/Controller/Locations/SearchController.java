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
import src.Model.Database;
import src.Model.Location;

public class SearchController extends AppController {

    @FXML
    private TableColumn<Location, String> columnProjector;

    @FXML
    private TableColumn<Location, String> columnLocationDate;

    @FXML
    private TableColumn<Location, String> columnDevolutionDate;

    @FXML
    private TextField txtSearchClient;

    @FXML
    private TableColumn<Location, String> columnClient;

    @FXML
    private Button btnReturn;

    @FXML
    private TableColumn<Location, String> columnPrice;

    @FXML
    private TableColumn<Location, String> columnReturned;

    @FXML
    private TableView<Location> table;

    @FXML
    private Button btnRenew;

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
    public void initialize() {
        drawTable();
        getDatas("");
        configureBindings();
    }
    
    @FXML
    void searchClient() {
        drawTable();
        getDatas(txtSearchClient.getText());
    }
    
    private void getDatas(String search) {
        table.getItems().setAll(search(search));
    }
    
    private void configureBindings() {
        BooleanBinding notSelected = table.getSelectionModel().selectedItemProperty().isNull();

        btnRenew.disableProperty().bind(notSelected);
        btnReturn.disableProperty().bind(notSelected);
    }
    
    private void drawTable() {
        columnClient.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        columnProjector.setCellValueFactory(new PropertyValueFactory<>("projectorBrandModel"));
        columnLocationDate.setCellValueFactory(new PropertyValueFactory<>("locationDate"));
        columnDevolutionDate.setCellValueFactory(new PropertyValueFactory<>("devolutionDate"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        columnReturned.setCellValueFactory(new PropertyValueFactory<>("returned"));
    }
    
    private ArrayList<Location> search(String search)
    {
        try {
            String SQL = "SELECT * FROM locations l, projectors p, clients c WHERE l.projector_id = p.id AND l.client_id = c.id AND c.name LIKE ?;";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setString(1, '%'+search+'%');
            ResultSet rs = pstmt.executeQuery();
            
            ArrayList<Location> arrayList = new ArrayList<>();
            try {
                
                while(rs.next()) {
                    Location location = new Location();
                    location.setId(rs.getInt("l.id"));
                    location.setDevolutionDate(rs.getString("l.devolution_date"));
                    location.setLocationDate(rs.getString("l.location_date"));
                    location.setPrice(rs.getFloat("l.price"));
                    location.setReturned(rs.getBoolean("l.returned"));
                    location.setClientId(rs.getInt("l.client_id"));
                    location.setProjectorId(rs.getInt("l.projector_id"));
                    location.setClientName(rs.getString("c.name"));
                    location.setProjectorBrandModel(rs.getString("p.brand") + " - " + rs.getString("p.model"));

                    arrayList.add(location);
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
    
    
    @FXML
    void goReturn() {
        Location location = table.getSelectionModel().getSelectedItem();
        if (location.isReturned()) {
            JOptionPane.showMessageDialog(null, "Projetor já foi devolvido", "Informação", JOptionPane.DEFAULT_OPTION);
        } else {
            int wantReturn = JOptionPane.showConfirmDialog(null, "Realmente quer devolver o projetor "+location.getProjectorBrandModel() + "?", "Informação", JOptionPane.YES_NO_OPTION);
        
            if (wantReturn == JOptionPane.YES_OPTION) {
                try {
                    String SQL = "UPDATE locations SET returned = TRUE WHERE id = ?;";
                    PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
                    pstmt.setInt(1, location.getId());
                    pstmt.execute();

                    SQL = "UPDATE projectors SET projector_state = 'Disponível' WHERE id = ?";
                    pstmt = (PreparedStatement) connection.prepareStatement(SQL);
                    pstmt.setInt(1, location.getProjectorId());
                    pstmt.execute();
                    pstmt.close();
                    JOptionPane.showMessageDialog(null, "Projetor develvido com Sucesso", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Falha ao devolver projetor\nErro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
                drawTable();
                getDatas("");
            }
        }
    }

    @FXML
    void goRenew() {
        Location location = table.getSelectionModel().getSelectedItem();
        if (location.isReturned()) {
            JOptionPane.showMessageDialog(null, "Projetor já foi devolvido, impossível renovar", "Informação", JOptionPane.DEFAULT_OPTION);
        } else {
            Context.getInstance().setLocation(location);
            Main.loadScene("View/Locations/renew.fxml", "Renovar Locação");
        }
    }

}
