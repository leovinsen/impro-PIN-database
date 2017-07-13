/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package impro;

import static impro.ImproController.d;
import static impro.Methods.addTextLimiter;
import static impro.Methods.isValidIP;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Record;

/**
 * FXML Controller class
 *
 * @author asus
 */
public class MemberHistoryController implements Initializable {

    @FXML private TextField fieldIP;
    @FXML private Button btnSubmit;
    @FXML private Label warningIP;
    @FXML private TableView tableHistory;
    @FXML private TableColumn colAccountNo;
    @FXML private TableColumn colAccountOwner;
    @FXML private TableColumn colDate;
    @FXML private TableColumn colHour;
    
    private ObservableList<Record> records;

    @FXML private void showHistory() throws SQLException{
        
        String input = fieldIP.getText();
        //If IP entered is valid, proceed. Else, notify user it is invalid.
        if(isValidIP(input)){
            //Check if the member exists in the database.
            if(!d.checkMember(input)){
                warningIP.setText("IP tidak ditemukan.");
            }
            else {
                
                //False = no record found.
                //True = records found; clear the warning message if any.
                if (d.findHistory(input, records)){
                    warningIP.setText("");
                }
                else {
                    warningIP.setText("Tidak ada riwayat untuk " + input);
                }   
                
                
            }
        } 
        else {
            warningIP.setText("IP tidak valid. Cek kembali.");
        }
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTable();
        configureTextField();
    }    
    
    private void configureTable() {
        colAccountNo.setCellValueFactory(new PropertyValueFactory<>("accountNo"));
        colAccountOwner.setCellValueFactory(new PropertyValueFactory<>("accountOwner"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colHour.setCellValueFactory(new PropertyValueFactory<>("hour"));
        
        records = FXCollections.observableArrayList();
        tableHistory.setItems(records);
    }
    
    private void configureTextField() {
        addTextLimiter(fieldIP, 10);
    }
    
}
