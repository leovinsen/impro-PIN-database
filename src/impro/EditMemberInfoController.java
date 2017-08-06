/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package impro;

import helperclass.AccountFocusListener;
import helperclass.ChildFocusListener;
import helperclass.IPFocusListener;
import static impro.EditMemberController.closeEditDataWindow;
import static impro.EditMemberController.userInput;
import static impro.ImproController.d;
import static impro.Methods.addTextLimiter;
import static impro.Methods.isValidIP;
import static impro.Methods.isValidPIN;
import static impro.Methods.isValidRekening;
import static impro.Methods.makeRed;
import static impro.Methods.showInvalidMessage;
import static impro.Methods.showValidMessage;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.Database.SearchMethod;
import static model.Database.checkMember;
import static model.Database.getMemberInfo;

/**
 * FXML Controller class
 *
 * @author asus
 */
public class EditMemberInfoController implements Initializable {

    @FXML private Label labelOldInfo;
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

    //[0] = IP
    //[1] = PIN
    //[2] = Name
    //[3] = Rekening
    //[4] = Child #1
    //[5] = Child #2
    //Contents retrieved through Database.getMemberInfo()
    private String[] memberInfo; 
    private String[] accountInfo;
    private ArrayList<Label> warningLabels;
    private ArrayList<TextField> textFields;
        
    IPFocusListener fieldIPListener;
    ChildFocusListener firstChildListener;
    ChildFocusListener secondChildListener;
    AccountFocusListener accountNoListener;
    AccountFocusListener accountNameListener;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {  
        fillTextFields();
        configureTextFields();
    }

    private void configureTextFields() {
        fieldIP.setEditable(false);
        addTextLimiter(fieldIP, 10);
        addTextLimiter(fieldPIN, 10);
        addTextLimiter(fieldAccountNo, 10);
        addTextLimiter(fieldFirstChild, 10);
        addTextLimiter(fieldSecondChild, 10);

        fieldIPListener = new IPFocusListener(fieldIP, warningIP, memberInfo[0]);
        fieldIP.focusedProperty().addListener(fieldIPListener);

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

    private void fillTextFields() {
        if (labelOldInfo != null) {
            try {
                memberInfo = getMemberInfo();
                labelOldInfo.setText(String.format("%s - %s - %s", memberInfo[0], memberInfo[1], memberInfo[2]));
                fieldIP.setText(memberInfo[0]);
                fieldPIN.setText(memberInfo[1]);
                fieldName.setText(memberInfo[2]);
                if (memberInfo[3] != null) {
                    accountInfo = d.getAccountInfo(memberInfo[3], SearchMethod.ACCOUNT_NUMBER);
                    fieldAccountNo.setText(memberInfo[3]);
                    fieldAccountName.setText(accountInfo[0]);
                    fieldBankName.setText(accountInfo[1]);
                    fieldBranchName.setText(accountInfo[2]);
                }
                //if child 1 is null do nothing
                if (memberInfo[4] != null) {
                    fieldFirstChild.setText(memberInfo[4]);
                }
                //if child 2 is null do nothing
                if (memberInfo[5] != null) {
                    fieldSecondChild.setText(memberInfo[5]);
                }
            } catch (SQLException ex) {
                Logger.getLogger(EditMemberController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Method for btnSubmit's onAction which does a validity check on all fields in the window
     * and performs modification of a member's info if the validity check is passed.
     * @param event
     * @throws SQLException 
     */
    @FXML
    private void editMember(ActionEvent event) throws SQLException {
        
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
        
         //If the IP is already in the database and it is not the original IP shown
        //Then prevent the use of that IP to prevent duplicate record in the database.
        if (checkMember(inputIP) && !inputIP.equals(memberInfo[0])) {
            correctInput = false;
            warningIP.setText(inputIP + " sudah digunakan.");
        } else {
            warningIP.setText("");
        }
        
        //One bank account can only be used by three members at once.
        if(d.countAccountUsage(inputAccountNo) > 3){
            correctInput = false;
            warningAccountNo.setText("Sudah digunakan 3 member.");
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
            } else {
                inputName = inputName.trim();
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
            } else {
                inputAccountName = inputAccountName.trim();
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
        
        //One bank account can only be used by three members simulatenously.
        if(d.countAccountUsage(inputAccountNo) > 3){
            correctInput = false;
            warningAccountNo.setText("Sudah digunakan 3 member.");
        } 
        
        //Checks for kaki (children)
        
        ////////CHILD #1/////////
        
        //If TextField for Kaki 1 is empty, do nothing because it is optional
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
        
        ////////CHILD #2///////
        
        //If TextField for Kaki 2 is empty, do nothing because it is optional
        //but if it is not, do validity checks
        if (inputChild2 != null) {
            if (!inputChild2.isEmpty()) {
            //If IP is valid, then check for the IP in the database
                //If cannot be found, then notify user
                //If it can be found, continue with the process
                if (isValidIP(inputChild2)) {

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
        }
        //Checks if the IP entered in both fields are the same
        if( inputChild1 != null && inputChild2 != null){
            if (inputChild1.equals(inputChild2)) {
                warningFirstChild.setText("Kaki 1 dan 2 duplikat");
                warningSecondChild.setText("Kaki 1 dan 2 duplikat");

                correctInput = false;
                induk = false;
            }
        }
        
       
        try {
            if (correctInput) {             
                //Edit the member's record. True if it is successful.
                boolean editMemberSuccess = 
                        d.editMemberRecord(inputIP, inputPIN, inputName, inputAccountNo, induk, userInput.getValue());
                
                if (editMemberSuccess) {                    
                    //If all 4 fields are not empty = true
                    if (bankAccIsFilled) {
                        //If it is not in the database
                        if (!d.checkAccount(inputAccountNo, SearchMethod.ACCOUNT_NUMBER)) {
                            d.addAccountDetails(inputAccountNo, inputAccountName, inputBankName, inputBranchName);
                        } 
                        d.insertRekeningHistory(inputIP, memberInfo[3], inputAccountNo, inputAccountName);
                    } else {
                        if (memberInfo[3] != null) {
                            d.insertRekeningHistory(inputIP, memberInfo[3], null, "-");
                        }
                    }
                    //If one or both of the two child fields is filled                   
                    if (induk) {
                        doChildModification(inputChild1, inputIP, 0);
                        doChildModification(inputChild2, inputIP, 1);
                        if(inputChild1 == null && inputChild2 == null){
                            //make induk false;
                        }
                    } 
                    
                    //Message that notifies the user that member was successfully edited.
                    String contentMessage ="New data: \n"
                            + "IP: %s, PIN: %s, Name: %s , Rekening: %s \n";
                    
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setHeaderText("Successfully changed user info for " + userInput.getValue());
                    alert.setContentText(String.format(contentMessage, inputIP, inputPIN, inputName, inputAccountNo));
                    alert.show();
                    closeEditDataWindow();
                } else {
                    result.setText("Gagal merubah info member.");
                    result.setStyle("-fx-font-fill: red");
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(EditMemberInfoController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    
    private void doChildModification(String childIP, String newIP, int index) throws SQLException {
        //index is incremented by 4 to get either memberInfo[4](child #1) or memberInfo[5] (child#2)
        index = index + 4;
        //If TextField for Child #1 is not empty
        if (childIP != null) {
            //If previously child #1 was empty, add a new record in table INDUK
            //Condition // Before: no child1, after: has child1
            if (memberInfo[index] == null) {
                d.addChild(newIP, childIP);
            } 
            //If child #1 was not empty, edit the record for child1
            //Condition // Before: has child1, after: has new child1.
            //Note: the record is edited even if the child stays unchanged,
            //to accomodate for change in IP_INDUK (if changed)
            else {
                d.editChild(memberInfo[0], newIP, memberInfo[index], childIP);
            }
        } 
        //If TextField for Child #1 is empty 
        else {                       
            //If previously the parent has a child, delete the record from table INDUK
            //Note: use old IP in case the IP_INDUK is changed.
            if (memberInfo[index] != null) {
                //Delete 
                if (d.deleteChild(memberInfo[0], memberInfo[index])) {
                    System.out.println("deleted");
                } else {
                    System.out.println("failed to delete");
                }
            } //If previously the parent has no child, do nothing
            else {
                //do nothing
            }
        }
    }
}
