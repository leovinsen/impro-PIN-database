/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package impro;


import static impro.Methods.addTextLimiter;
import static impro.Methods.isNumeric;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import static model.Database.checkMember;
import static model.Database.deleteMember;
import static model.Database.getResultSetData;

/**
 * FXML Controller class
 *
 * @author asus
 */
public class EditMemberController implements Initializable {

    @FXML private Label labelWarning;
    @FXML private TextField fieldInput;
    @FXML private Label labelInfo;
    @FXML private Button btnSubmitQuery;
    @FXML private Button btnDelete;
   
    //variable to pass on userInput to EditMemberInfoController
    static SimpleStringProperty userInput;
    
    
    /**
     * Method for <tt>btnSubmitQueryr</tt> which finds the record for the entered IP in the database.
     * If the corresponding record is found, take the user to the next window where he/she can 
     * enter new values for the record. Else, notify the user that the record cannot be found /
     * an SQL error has occured.
     */
    @FXML private void findRecord(){
        String userInput = fieldInput.getText();
            if (isInvalidIP(userInput)) {
                labelWarning.setText("IP yang dimasukkan tidak valid" + "(" + userInput + ")");
            } else {
                try {
                    if (checkMember(userInput)) {
                        crtEditDataWindow(userInput);
                    } else {
                        labelWarning.setText(userInput + " tidak ditemukan di database.");
                    }
                } catch (SQLException ex) {
                    //Logger.getLogger(AppLogic.class.getName()).log(Level.SEVERE, null, ex);
                    labelWarning.setText("An error in the database has occured.");
                } catch (IOException ex) {
                Logger.getLogger(EditMemberController.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
    }
    
    /**
     * Method for <tt>btnDelete</tt> which deletes a specified record from the database.
     * If the IP entered is valid, the corresponding record is deleted.
     * Otherwise, an error message will be shown.
     */
    @FXML private void deleteRecord(){
        String userInput = fieldInput.getText();
        if(isInvalidIP(userInput)){
            labelWarning.setText("IP yang dimasukkan tidak valid" + "(" + userInput + ")");
        } else {
            try {
                if (checkMember(userInput)){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("Delete " + userInput + "?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        deleteMember(userInput);
                        
                    }
                } else {
                    labelWarning.setText(userInput + " tidak ditemukan di database.");
                }
            } catch (SQLException ex){
                Logger.getLogger(EditMemberController.class.getName()).log(Level.SEVERE, null, ex);
                labelWarning.setText("Gagal menghapus member (Database Error).");
            }
        }
    }

    @FXML private void removeWarning(KeyEvent event){
        labelWarning.setText("");
        event.consume();
    }
    
    /**
     * Loads the controller class for "Edit Member Info" window where
     * user types in the new IP, PIN and Name for the member.
     * @param input old IP value of the member.
     * @throws IOException 
     */
    private void crtEditDataWindow(String input) throws IOException{
        userInput.setValue(input);
        Parent root = FXMLLoader.load(getClass().getResource("editmemberinfo.fxml"));
        Stage stage = new Stage();
        stage.setTitle("edit member");
        stage.setScene(new Scene(root));
        stage.show();
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userInput = new SimpleStringProperty();
        configureTextField();
    }    
    
    /**
     * add text limiter of length 10 to <tt>TextField</tt>.
     */
    private void configureTextField(){
        if(fieldInput!= null) addTextLimiter(fieldInput, 10);
    }

    /**
     * 
     * @param userInput IP entered by user.
     * @return True if the IP is invalid, false otherwise.
     */
    private boolean isInvalidIP(String userInput){
        return (!userInput.startsWith("IP") || !isNumeric(userInput.substring(2)) || userInput.length() != 10);
    }
    
    
}
