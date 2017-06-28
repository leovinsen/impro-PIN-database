/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package impro;

import static impro.ImproController.d;
import static impro.Methods.addTextLimiter;
import static impro.Methods.isNumeric;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import static model.Database.checkMember;

/**
 * FXML Controller class
 *
 * @author asus
 */
public class AddMemberController implements Initializable {

    @FXML private Label labelIP;
    @FXML private Label labelPIN;
    @FXML private Label labelName;
    @FXML private TextField fieldIP;
    @FXML private TextField fieldPIN;
    @FXML private TextField fieldName;
    @FXML private Label warningIP;
    @FXML private Label warningPIN;
    @FXML private Label result;
    
    @FXML
    private void addMember(){
        boolean correctInput = true;
            String inputIP = fieldIP.getText();
            String inputPIN = fieldPIN.getText();
            String inputName = fieldName.getText();

            //Has to start with capital IP
            //Characters 3 - 10 have to be digits
            //Has to be 10 characters long
            if (!inputIP.startsWith("IP") || !isNumeric(inputIP.substring(2)) || inputIP.length() != 10) {
                correctInput = false;
                warningIP.setText("IP yang dimasukkan tidak valid" + "(" + inputIP + ")");
            } else {
                warningIP.setText("");
            }

            //PIN has to be 10 characters long
            //PIN must not be empty
            if (inputPIN.length() != 10) {
                correctInput = false;
                warningPIN.setText("PIN tidak valid");
            } else {
                warningPIN.setText("");
            }

            if (inputName.isEmpty()) {
                inputName = "-";
            }

            if (correctInput) {  
                String[] newMember = new String[]{inputIP, inputPIN, inputName};
                try {
                    if (d.addMember(newMember)) {
                        result.setText(String.format("New member: %s -- %s -- %s", newMember[0], newMember[1], newMember[2]));
                        warningIP.setText("");
                        warningPIN.setText("");
                        fieldIP.setText(inputIP.substring(0, 7));
                        fieldIP.requestFocus();
                        fieldIP.forward();
                        fieldPIN.clear();
                        fieldName.clear();
                    } else {
                        result.setText(inputIP + " sudah ada di dalam database.");
                    }
                } catch (SQLException ex) {
                    //Logger.getLogger(AppLogic.class.getName()).log(Level.SEVERE, null, ex);
                    result.setText("Gagal menambah member.");
                }
            }     
    }
    
    
    private static class FocusListener implements ChangeListener<Boolean>{

        private TextField field;
        private Label result;
        
        public FocusListener(TextField field, Label label){
            this.field = field;
            this.result = label;
        }
      
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            try {
                String input = field.getText();
                    if (newValue) {
                        if (checkMember(input)) {
                            result.setText(input + " sudah ada di dalam database.");
                        } else {
                            if (input.length()!= 10){
                                result.setText("IP harus 8 angka.");
                            } else {
                                result.setText("");
                            }
                        }
                    }
                } catch (SQLException ex) {
                    //Logger.getLogger(AppLogic.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
  
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTextField();
    }    
    
    /**
     * Adds <tt>FocusListener</tt> to fieldPIN and fieldName, which
     * tells the program to check the IP entered in fieldIP when the focus 
     * is moved to one of the two fields.
     */
    private void configureTextField(){
        addTextLimiter(fieldIP, 10);
        addTextLimiter(fieldPIN, 10);
        FocusListener fl = new FocusListener(fieldIP, result);
        fieldPIN.focusedProperty().addListener(fl);
        fieldName.focusedProperty().addListener(fl);
    }
}
