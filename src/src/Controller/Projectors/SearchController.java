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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javafx.beans.binding.BooleanBinding;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import src.Main;

/**
 *
 * @author jefferson
 */
public class SearchController extends AppController {
    
    @FXML
    private Pane pane;
    
    @FXML
    private TableColumn<Projector, String> columnPurchaseDate;

    @FXML
    private TableColumn<Projector, String> columnBrand;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnEdit;

    @FXML
    private TableColumn<Projector, String> columnStatus;

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<Projector> tableView;

    @FXML
    private Button btnView;

    @FXML
    private TableColumn<Projector, String> columnSerialNumber;

    @FXML
    private TableColumn<Projector, String> columnModel;
    
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
    
    private void getDatas(String search) {
        tableView.getItems().setAll(search(search));
    }
    
    @FXML
    void searchProjector() {
        drawTable();
        getDatas(txtSearch.getText());
    }
    
    @FXML
    void goEdit() {
        Projector projector = tableView.getSelectionModel().getSelectedItem();
        Context.getInstance().projector().setId(projector.getId());
        Context.getInstance().projector().setBrand(projector.getBrand());
        Context.getInstance().projector().setModel(projector.getModel());
        Context.getInstance().projector().setSerialNumber(projector.getSerialNumber());
        Context.getInstance().projector().setAnsiLumens(projector.getAnsiLumens());
        Context.getInstance().projector().setPurchaseDate(projector.getPurchaseDate());
        Context.getInstance().projector().setDateLastLampChange(projector.getDateLastLampChange());
        Context.getInstance().projector().setProjectorState(projector.getProjectorState());
        Main.loadScene("View/Projectors/edit.fxml", "Editar Projetor");
    }
    
    @FXML
    void goView() {
        Projector projector = tableView.getSelectionModel().getSelectedItem();
        Context.getInstance().projector().setId(projector.getId());
        Context.getInstance().projector().setBrand(projector.getBrand());
        Context.getInstance().projector().setModel(projector.getModel());
        Context.getInstance().projector().setSerialNumber(projector.getSerialNumber());
        Context.getInstance().projector().setAnsiLumens(projector.getAnsiLumens());
        Context.getInstance().projector().setPurchaseDate(projector.getPurchaseDate());
        Context.getInstance().projector().setDateLastLampChange(projector.getDateLastLampChange());
        Context.getInstance().projector().setProjectorState(projector.getProjectorState());
        Main.loadScene("View/Projectors/view.fxml", "Visualizar Projetor");
    }
    
    @FXML
    void goDelete() {
        Projector projector = tableView.getSelectionModel().getSelectedItem();
        Object[] options = { "Confirmar", "Cancelar" };
        int wantDelete = JOptionPane.showConfirmDialog(null, "Realmente quer excluír o Projetor da marca " + projector.getBrand() + "?", "Informação", JOptionPane.YES_NO_OPTION);
        
        if (wantDelete == JOptionPane.YES_OPTION) {
            if (delete(projector)) {
                JOptionPane.showMessageDialog(null, "Projetor excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                Main.loadScene("View/Projectors/search.fxml", "Projetores");
            }
        }
    }
    
    public boolean delete(Projector projector) {
        try {
            String SQL = "DELETE FROM projectors WHERE id = ?";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setInt(1, projector.getId());
            pstmt.execute();
            pstmt.close();

            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao excluír Projetor!\n Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }
    
    private void configureBindings() {
        BooleanBinding notSelected = tableView.getSelectionModel().selectedItemProperty().isNull();
        
        btnEdit.disableProperty().bind(notSelected);
        btnDelete.disableProperty().bind(notSelected);
        btnView.disableProperty().bind(notSelected);
    }
    
    private void drawTable() {
        columnBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        columnModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        columnSerialNumber.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        columnPurchaseDate.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));
        columnStatus.setCellValueFactory(new PropertyValueFactory<>("projectorState"));
    }
    
    private ArrayList<Projector> search(String search)
    {
        try {
            String SQL = "SELECT * FROM projectors WHERE brand LIKE ? || model LIKE ?;";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            pstmt.setString(1, '%'+search+'%');
            pstmt.setString(2, '%'+search+'%');
            ResultSet rs = pstmt.executeQuery();
            
            ArrayList<Projector> arrayList = new ArrayList<>();
            try {
                
                while(rs.next()) {
                    Projector projector = new Projector();
                    projector.setId(rs.getInt("id"));
                    projector.setBrand(rs.getString("brand"));
                    projector.setModel(rs.getString("model"));
                    projector.setSerialNumber(rs.getString("serial_number"));
                    projector.setAnsiLumens(rs.getInt("ansi_lumens"));
                    projector.setPurchaseDate(rs.getString("purchase_date"));
                    projector.setDateLastLampChange(rs.getString("date_last_lamp_change"));
                    projector.setProjectorState(rs.getString("projector_state"));
                   arrayList.add(projector);
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
}
