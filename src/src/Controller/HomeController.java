/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.Controller;

import com.mysql.jdbc.Connection;
import src.Main;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import src.Model.Client;
import src.Model.Database;
import src.Model.Location;

public class HomeController {
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    
    @FXML
    private TableColumn<Location, String> columnProjector;

    @FXML
    private TableColumn<Location, String> columnLocationDate;

    @FXML
    private TableColumn<Location, String> columnDevolutionDate;

    @FXML
    private TableColumn<Location, String> columnClient;

    @FXML
    private TableColumn<Location, String> columnPrice;

    @FXML
    private TableView<Location> table;
    
    private Database database = new Database();
    
    private Connection connection;
    
    @FXML
    private Menu menuHome;

    @FXML
    void initialize() {
        drawTable();
        getDatas();
    }
    
    private void drawTable() {
        columnClient.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        columnProjector.setCellValueFactory(new PropertyValueFactory<>("projectorBrandModel"));
        columnLocationDate.setCellValueFactory(new PropertyValueFactory<>("locationDate"));
        columnDevolutionDate.setCellValueFactory(new PropertyValueFactory<>("devolutionDate"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
    }
    
    private void getDatas() {
        table.getItems().setAll(search());
    }

    private ArrayList<Location> search()
    {
        try {
            String SQL = "SELECT * FROM locations l, clients c, projectors p WHERE l.client_id = c.id AND p.id = l.projector_id AND l.returned = false;";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            ResultSet rs = pstmt.executeQuery();
            
            ArrayList<Location> arrayList = new ArrayList<>();
            try {
                
                while(rs.next()) {
                    Location location = new Location();
                    location.setId(rs.getInt("id"));
                    location.setClientName(rs.getString("c.name"));
                    location.setProjectorBrandModel(rs.getString("p.brand") + " - " + rs.getString("p.model"));
                    location.setDevolutionDate(rs.getString("devolution_date"));
                    location.setLocationDate(rs.getString("location_date"));
                    location.setPrice(Float.parseFloat(rs.getString("price")));
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
    void goHome() {
        Main.loadScene("View/Home.fxml", "Página Inicial");
    }

    @FXML
    void goAddClients() {
        Main.loadScene("View/Clients/add.fxml", "Adicionar Cliente");
    }

    @FXML
    void goAddProjector() {
        Main.loadScene("View/Projectors/add.fxml", "Adicionar Projetor");
    }

    @FXML
    void goAddLocation() {
        Main.loadScene("View/Locations/add.fxml", "Adicionar Locação");
    }

    @FXML
    void goViewClient() {
        Main.loadScene("View/Clients/search.fxml", "Clientes");
    }

    @FXML
    void goViewProjectors() {
        Main.loadScene("View/Projectors/search.fxml", "Projetores");
    }

    @FXML
    void goViewLocations() {
        Main.loadScene("View/Locations/search.fxml", "Locações");
    }
    
    @FXML
    void reportClients() {
        
    }
    
    @FXML
    void reportProjectors() {
        Main.loadScene("View/Reports/projectors.fxml", "Relatório de Projetores");
    }
    
    @FXML
    void reportLocations() {
        
    }

    @FXML
    void goExit() {
        System.exit(0);
    }
    
    public HomeController() {
        if (!database.connect()) {
            System.err.println("Failed connected to database!");
            System.exit(0);
        } else {
            this.connection = database.getConnection();
        }   
    }    
}
