/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package impro;

import model.Database;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 *
 * @author asus
 */
public class ImproController implements Initializable {
    public static Database d;
    private Stage stage1;
    private Stage stage2;
    private Stage stage3;
   
    @FXML private Button btnSearch;
    @FXML private Button btnAddMember;
    @FXML private Button btnEditMember;
    @FXML private Button btnHistory;
    
    @FXML
    void searchMember() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("searchmember.fxml"));
        stage1 = new Stage();
        stage1.setTitle("Search for member's PIN");
        stage1.setScene(new Scene(root));
        stage1.show();
    }
    
    @FXML
    void addMember() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("addmember.fxml"));
        stage2 = new Stage();
        stage2.setTitle("Add a new member");
        stage2.setScene(new Scene(root));
        stage2.show();
        
    }
    
    @FXML   
    void editMember() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("editmember.fxml"));
        stage3 = new Stage();
        stage3.setTitle("Edit member");
        stage3.setScene(new Scene(root));
        stage3.show();
    }
    
    @FXML
    void findHistory() throws IOException{
        Parent root = FXMLLoader.load(getClass().getResource("memberhistory.fxml"));
        Stage stage = new Stage();
        stage.setTitle("History");
        stage.setScene(new Scene(root));
        stage.show();
    }
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        d = new Database();
    }    
    
}
