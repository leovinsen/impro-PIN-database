/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package impro;

import helperclass.AccountFocusListener;
import helperclass.ChildFocusListener;
import helperclass.IPFocusListener;
import static impro.ImproController.d;
import static impro.Methods.addTextLimiter;
import static impro.Methods.createAlert;
import static impro.Methods.isValidIP;
import static impro.Methods.isValidPIN;
import static impro.Methods.isValidRekening;
import static impro.Methods.makeGreen;
import static impro.Methods.makeRed;
import static impro.Methods.showInvalidMessage;
import static impro.Methods.showValidMessage;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Database;
import model.Database.SearchMethod;
import static model.Database.checkMember;

/**
 * FXML Controller class
 *
 * @author asus
 */
public class AddMemberController implements Initializable {

    @FXML private TextField fieldIP;
    @FXML private TextField fieldPIN;
    @FXML private TextField fieldName;
    @FXML private TextField fieldAccountNo;
    @FXML private TextField fieldAccountName;
    @FXML private TextField fieldBankName;
    @FXML private TextField fieldBranchName;
    @FXML private TextField fieldFirstChild;
    @FXML private TextField fieldSecondChild;
    
    @FXML private Label warningIP;
    @FXML private Label warningPIN;
    @FXML private Label warningAccountNo;
    @FXML private Label warningAccountName;
    @FXML private Label warningFirstChild;
    @FXML private Label warningSecondChild;
    @FXML private Label result; 
    @FXML private Label resultChild;
    
    private IPFocusListener fieldIPListener;
    private ChildFocusListener firstChildListener;
    private ChildFocusListener secondChildListener;
    private AccountFocusListener accountNameListener;
    private AccountFocusListener accountNoListener;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configureTextField();
    }

    private void configureTextField() {
        //Limit max character entered in these fields to 10 characters
        addTextLimiter(fieldIP, 10);
        addTextLimiter(fieldPIN, 10);
        addTextLimiter(fieldAccountNo, 10);
        addTextLimiter(fieldFirstChild, 10);
        addTextLimiter(fieldSecondChild, 10);

        fieldIPListener = new IPFocusListener(fieldIP, warningIP, "");
        fieldIP.focusedProperty().addListener(fieldIPListener);
//        fieldPIN.focusedProperty().addListener(fieldIPListener);
//        fieldName.focusedProperty().addListener(fieldIPListener);

        firstChildListener = new ChildFocusListener(fieldFirstChild, warningFirstChild);
        secondChildListener = new ChildFocusListener(fieldSecondChild, warningSecondChild);
        firstChildListener.setDependency(secondChildListener);
        secondChildListener.setDependency(firstChildListener);
        fieldSecondChild.focusedProperty().addListener(firstChildListener);
        fieldFirstChild.focusedProperty().addListener(secondChildListener);
        
        accountNoListener = new AccountFocusListener(fieldAccountNo, warningAccountNo, 
                fieldAccountName, fieldBankName, fieldBranchName, SearchMethod.ACCOUNT_NUMBER);
        accountNameListener = new AccountFocusListener(fieldAccountName, warningAccountName,
                fieldAccountNo, fieldBankName, fieldBranchName, SearchMethod.ACCOUNT_NAME);
        
        fieldAccountNo.focusedProperty().addListener(accountNoListener);
        fieldAccountName.focusedProperty().addListener(accountNameListener);
        
    }
    
    @FXML
    private void addMember() throws SQLException {
        boolean correctInput = true;
        String inputIP = fieldIP.getText();
        String inputPIN = fieldPIN.getText();
        String inputName = fieldName.getText();
        String inputAccountNo = fieldAccountNo.getText();
        String inputAccountName = fieldAccountName.getText();
        String inputBankName = fieldBankName.getText();
        String inputBranchName = fieldBranchName.getText();
        String inputChild1 = fieldFirstChild.getText();
        String inputChild2 = fieldSecondChild.getText();
        boolean induk = false;
        boolean bankAccIsFilled = true;
        boolean child1 = false;
        boolean child2 = false;

        //Has to start with capital IP
        //Characters 3 - 10 have to be digits
        //Has to be 10 characters long
        if (isValidIP(inputIP)) {
            showValidMessage(warningIP, "IP");
        } else {
            correctInput = false;
            showInvalidMessage(warningIP, "IP");
        }
        
        //If the IP is already in the database 
        //Then prevent the use of that IP to prevent duplicate record in the database.
        if (checkMember(inputIP)) {
            correctInput = false;
            warningIP.setText(inputIP + " sudah digunakan.");
            makeRed(warningIP);
        } else {
            warningIP.setText("");
            makeRed(warningIP);
        }

        
        //PIN has to be 10 characters long
        //PIN must not be empty
        if (isValidPIN(inputPIN)) {
            showValidMessage(warningPIN, "PIN");
        } else {
            correctInput = false;
            showInvalidMessage(warningPIN, "PIN");
        }

        //If name is empty then replace with NULL
        if (inputName != null) {
            if (inputName.isEmpty()) {
                inputName = null;
            }
        }
        
        //Checks for four fields associated with bank acc
        //If one of the fields is empty then set bankAccIsFilled = false
        //to ensure that either ALL fields are filled/empty.
        if(inputAccountNo != null){
            if(inputAccountNo.isEmpty()){
                bankAccIsFilled = false;
                inputAccountNo = null;
            } else if (!isValidRekening(inputAccountNo)){
                correctInput = false;
                warningAccountNo.setText("no. rekening invalid.");
            }
        }
        if(inputAccountName != null){
            if(inputAccountName.isEmpty()) {
                bankAccIsFilled = false;
                inputAccountName = null;
            }
        }
        
        if(inputBankName != null){
            if (inputBankName.isEmpty()){
                bankAccIsFilled = false;
                inputBankName = null;
            }
        }
        
        if(inputBranchName != null){
            if (inputBranchName.isEmpty()){
                bankAccIsFilled = false;
                inputBranchName = null;
            }
        }
        
        //Block input if not all fields are filled / empty
        if(!bankAccIsFilled
                && !(inputBranchName == null && inputAccountName == null && inputBankName == null && inputBranchName == null)){
            correctInput = false;
            warningAccountNo.setText("Isi semua data rekening & bank");
        }
        
        //One bank account can only be used by three members at once.
        if (d.countAccountUsage(inputAccountNo) > 3) {
            correctInput = false;
            warningAccountNo.setText("Sudah digunakan 3 member.");
        }
        
        //Checks for kaki (children)
        

        //Child #1
        //If TextField for Child#1 is empty, do nothing because it is optional
        //but if it is not, do validity checks
        if (!inputChild1.isEmpty()){
            //If IP is valid, then check for the IP in the database
            //If cannot be found, then notify user
            //If it can be found, continue with the process
            if (isValidIP(inputChild1)){
                
                //Find member
                if (checkMember(inputChild1)) {
                    induk = true;
                    child1 = true;
                    showValidMessage(warningFirstChild, "IP Kaki 1");
                } else {
                    correctInput = false;
                    warningFirstChild.setText("IP tidak ditemukan.");
                    makeRed(warningFirstChild);
                }
                
            } else {
                //IP entered is in invalid form.
                correctInput = false;
                showInvalidMessage(warningFirstChild, "IP Kaki 2");
            }
        } else {
            inputChild1 = null;
        }
        
        //Child #2
        //If TextField for Child #2 is empty, do nothing because it is optional
        //but if it is not, do validity checks
        if (!inputChild2.isEmpty()){
            //If IP is valid, then check for the IP in the database
            //If cannot be found, then notify user
            //If it can be found, continue with the process
            if (isValidIP(inputChild2)){
                
                //Find member
                if (checkMember(inputChild2)) {
                    induk = true;
                    child2 = true;
                    showValidMessage(warningSecondChild, " IP Kaki 2");
                } else {
                    correctInput = false;
                    warningSecondChild.setText("IP tidak ditemukan.");
                    makeRed(warningSecondChild);
                }
                
            } else {
                //IP entered is in invalid form.
                correctInput = false;
                showInvalidMessage(warningSecondChild, "IP Kaki 2");
            }
        } else {
            inputChild2 = null;
        }
        
        if( inputChild1 != null && inputChild2 != null){
            if (inputChild1.equals(inputChild2)) {
                warningFirstChild.setText("Kaki 1 dan 2 duplikat");
                warningSecondChild.setText("Kaki 1 dan 2 duplikat");
                correctInput = false;
                induk = false;
            }
        }

        //FROM THIS POINT ONWARDS, ALL EMPTY FIELDS ARE NULL
        
        if (correctInput) {
            String[] newMember = new String[]{inputIP, inputPIN, inputName, inputAccountNo};

            try {               
                //if member added successfully (true returned), clear all fields 
                //and set a success message in Label result
                if (d.addMember(newMember, induk)) {
                    if (bankAccIsFilled) {
                        //If it is not in the database
                        if (!d.checkAccount(inputAccountNo, SearchMethod.ACCOUNT_NUMBER)) {
                            d.addAccountDetails(inputAccountNo, inputAccountName, inputBankName, inputBranchName);
                        }
                        d.insertRekeningHistory(inputIP, null, inputAccountNo, inputAccountName);
                    }             
                
                    result.setText(String.format("New Member: %s - %s", inputIP, inputPIN));
                    warningIP.setText("");
                    warningPIN.setText("");
                    warningAccountNo.setText("");
                    warningAccountName.setText("");
                    warningFirstChild.setText("");
                    warningSecondChild.setText("");
                    fieldIP.setText(inputIP.substring(0, 7));
                    fieldIP.requestFocus();
                    fieldIP.forward();
                    fieldPIN.clear();
                    fieldName.clear();
                    fieldAccountNo.clear();
                    fieldAccountName.clear();
                    fieldBankName.clear();
                    fieldBranchName.clear();
                   
                    //If induk = true, then one of the fields is filled by the user
                    if (induk) {
                        StringBuilder resultChildMessage = new StringBuilder("Kaki : ");

                        //If field Child#1 is not empty, get the field's input and add to database
                        if (inputChild1 != null) {
                            d.addChild(inputIP, inputChild1);
                            fieldFirstChild.clear();
                            resultChildMessage.append(inputChild1 + " ");
                        }
                        //Same for Child #2
                        if (inputChild2 != null) {
                            d.addChild(inputIP, inputChild2);
                            fieldSecondChild.clear();
                            resultChildMessage.append(inputChild2 + " ");
                        }
                        resultChild.setText(resultChildMessage.toString());
                    }
                //False means that the member already exists in the database.
                } else {
                    System.out.println("Should never happen");
                }             
                
            //Theoritically, the catch part should never be thrown under normal circumstances. 
            //A common cause is database being opened by another program.
            } catch (SQLException ex) {
                createAlert(ex);
            }           
            
            //If correctInput = false, clear Label result
            //If the Label is not cleared, in the case where a user just finished entering 
            //a valid member and then entering an invalid member, the success message of the 
            //previous entry will remain.
        } else {
            result.setText("");
            resultChild.setText("");
        }
    }
}
