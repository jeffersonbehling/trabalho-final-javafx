/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.Controller.Reports;

import com.mysql.jdbc.Connection;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import src.Controller.Helpers.GenerateReports;
import src.Main;
import src.Model.Database;
import src.Model.Projector;

/**
 * FXML Controller class
 *
 * @author jefferson
 */


public class ProjectorsController extends AppController {

    @FXML
    private TableColumn<Projector, String> columnPurchaseDate;

    @FXML
    private TableColumn<Projector, String> columnBrand;

    @FXML
    private TableColumn<Projector, String> columnStatus;

    @FXML
    private TableView<Projector> tableView;

    @FXML
    private TableColumn<Projector, String> columnSerialNumber;

    @FXML
    private TableColumn<Projector, String> columnModel;
    
    @FXML
    private ProgressIndicator loading;
    
    private ObservableList<Projector> tableDatas;
    
    private String dir;
    private String reportName;
	
    private GeradorRelatorio<Projector> report = new GeradorRelatorio<Projector>("Projectors");

    private Database database = new Database();
    private Connection connection;
    
    public ProjectorsController() {
        if (!database.connect()) {
            System.err.println("Failed connected to database!");
            System.exit(0);
        } else {
            this.connection = database.getConnection();
        }
    }
    
     @FXML
    public void initialize() {
        loading.setVisible(false);
        tableDatas = FXCollections.observableArrayList();
        drawTable();
        mostrar();
   }
    
	public void mostrar() {
		loading.setVisible(true);		
		Task<Void> carregaBD = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				search();
				return null;
			}

			@Override
			protected void succeeded() {				
				loading.setVisible(false);
				tableView.setItems((ObservableList<Projector>) tableDatas);
			}
			
			@Override
			protected void failed(){
				Alert dialogoInfo = new Alert(Alert.AlertType.ERROR);
		        dialogoInfo.setTitle("Informação do Sistema");
		        dialogoInfo.setHeaderText("Conexão com o Banco de Dados");
		        dialogoInfo.setContentText("Erro ao se conectar com o Banco de Dados!");
		        dialogoInfo.showAndWait();
			}
		};
		
		Thread t = new Thread(carregaBD);
		t.setDaemon(true);
		t.start();						
	}
    
    @FXML
    void printerReport() {
        loading.setVisible(true);
		
        Task<Void> printerReport = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    LinkedList<Projector> datas = new LinkedList<Projector>();
                    datas.addAll(tableDatas);				
                    report.gerarRelatorio(datas);
                    report.imprimir();
                    return null;
                }

                @Override
                protected void succeeded() {				
                    loading.setVisible(false);
                    Alert dialogoInfo = new Alert(Alert.AlertType.INFORMATION);
                    dialogoInfo.setTitle("Informação do Sistema");
                    dialogoInfo.setHeaderText("Impressão de Relatório");
                    dialogoInfo.setContentText("O relatório foi impresso com sucesso!");
                    dialogoInfo.showAndWait();
                }

                @Override
                protected void failed(){
                    Alert dialogoInfo = new Alert(Alert.AlertType.ERROR);
                    dialogoInfo.setTitle("Informação do Sistema");
                    dialogoInfo.setHeaderText("Impressão de Relatório");
                    dialogoInfo.setContentText("Erro ao imprimir o relatório!");
                    dialogoInfo.showAndWait();
                }
        };
		
        Thread t = new Thread(printerReport);
        t.setDaemon(true);
        t.start();
    }

    @FXML
    void generatePdf() {
	
		FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Relatorio"); 
        
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Arquivos PDF (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(Main.primaryStage);
        if (file != null) {
        	dir = file.getAbsolutePath().toString();        	
        	 reportName = file.getName().toString();    
        	dir = dir.replaceAll(reportName, " ");
        	dir = dir.trim();
        }
        
        System.out.println("Diretorio = " + dir);
        System.out.println("Nome do Relatorio = " + reportName);
        
		loading.setVisible(true);
		
		Task<Void> gerandoRelatorio = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
                            LinkedList<Projector> dados = new LinkedList<Projector>();
                            dados.addAll(tableDatas);

                            report.setDiretorioDoRelatorio(dir);
                            report.setNomeRelatorio(reportName);
                            report.gerarRelatorio(dados);
                        try {
                            report.exportarParaPdf();
                            } catch(Exception e) {
                                System.out.println("Erro: " + e);
                            }
                            return null;
			}

			@Override
			protected void succeeded() {				
                            loading.setVisible(false);
                            Alert dialogoInfo = new Alert(Alert.AlertType.INFORMATION);
                            dialogoInfo.setTitle("Informacao do Sistema");
                            dialogoInfo.setHeaderText("Geracao de Relatorio");
                            dialogoInfo.setContentText("O relatorio foi gerado com sucesso!");
                            dialogoInfo.showAndWait();
			}
			
			@Override
			protected void failed(){
                            loading.setVisible(false);
                            Alert dialogoInfo = new Alert(Alert.AlertType.ERROR);
                            dialogoInfo.setTitle("Informacao do Sistema");
                            dialogoInfo.setHeaderText("Geracao de Relatorio");
                            dialogoInfo.setContentText("Erro ao gerar o relatorio!");
                            dialogoInfo.showAndWait();
			}
		};
		
		Thread t = new Thread(gerandoRelatorio);
		t.setDaemon(true);
		t.start();
			
    }
    
    @FXML
	public void printer() {
		        
		loading.setVisible(true);
		
		Task<Void> imprimindoRelatorio = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				LinkedList<Projector> dados = new LinkedList<Projector>();
				dados.addAll(tableDatas);				
				report.gerarRelatorio(dados);
				report.imprimir();
				return null;
			}

			@Override
			protected void succeeded() {				
				loading.setVisible(false);
				Alert dialogoInfo = new Alert(Alert.AlertType.INFORMATION);
		        dialogoInfo.setTitle("Informação do Sistema");
		        dialogoInfo.setHeaderText("Impressão de Relatório");
		        dialogoInfo.setContentText("O relatório foi impresso com sucesso!");
		        dialogoInfo.showAndWait();
			}
			
			@Override
			protected void failed(){
				Alert dialogoInfo = new Alert(Alert.AlertType.ERROR);
		        dialogoInfo.setTitle("Informação do Sistema");
		        dialogoInfo.setHeaderText("Impressão de Relatório");
		        dialogoInfo.setContentText("Erro ao imprimir o relatório!");
		        dialogoInfo.showAndWait();
			}
		};
		
		Thread t = new Thread(imprimindoRelatorio);
		t.setDaemon(true);
		t.start();
										
	}
    
    public void gerarRelatorio(LinkedList<Projector> dados){
		try {
			this.report.gerarPDF(dados);
		} catch (Exception e) {
			System.out.println("Erro ao gerar relatório: " + e.getMessage());
		}
	}
    
    @FXML
    void searchClient() {
        drawTable();
        getDatas();
    }
    
    private void drawTable() {
        columnBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        columnModel.setCellValueFactory(new PropertyValueFactory<>("model"));
        columnPurchaseDate.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));
        columnSerialNumber.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        columnStatus.setCellValueFactory(new PropertyValueFactory<>("projectorState"));
    }
    
    private void getDatas() {
        tableView.getItems().setAll((ObservableList<Projector>) tableDatas);
    }
    
    private void search() {
        try {
            String SQL = "SELECT * FROM projectors;";
            PreparedStatement pstmt = (PreparedStatement) connection.prepareStatement(SQL);
            ResultSet rs = pstmt.executeQuery();
            
            try {
                
                while(rs.next()) {
                    Projector projector = new Projector();
                    projector.setId(rs.getInt("id"));
                    projector.setBrand(rs.getString("brand"));
                    projector.setModel(rs.getString("model"));
                    projector.setPurchaseDate(rs.getString("purchase_date"));
                    projector.setDateLastLampChange(rs.getString("date_last_lamp_change"));
                    projector.setAnsiLumens(rs.getInt("ansi_lumens"));
                    projector.setSerialNumber(rs.getString("serial_number"));
                    projector.setProjectorState(rs.getString("projector_state"));
                    
                    tableDatas.add(projector);
                }
            } catch (SQLException ex) {
                    System.out.println("Error: " + ex.getMessage());
            }
            pstmt.close();

            
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
}
