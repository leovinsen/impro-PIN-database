/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package impro;

import static impro.EditMemberController.userInput;
import static impro.ImproController.d;
import static impro.Methods.isNumeric;
import static impro.Methods.addTextLimiter;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import static model.Database.checkMember;
import static model.Database.getResultSetData;

/**
 * FXML Controller class
 *
 * @author asus
 */
public class EditMemberInfoController implements Initializable {

    @FXML
    private Label labelOldInfo;
    @FXML
    private Label labelIP;
    @FXML
    private TextField fieldIP;
    @FXML
    private Label warningIP;
    @FXML
    private Label labelPIN;
    @FXML
    private TextField fieldPIN;
    @FXML
    private Label warningPIN;
    @FXML
    private Label labelName;
    @FXML
    private TextField fieldName;
    @FXML
    private Label result;
    @FXML
    private Button btnSubmitChanges;

    private String[] memberInfo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (labelOldInfo != null) {
            try {
                memberInfo = getResultSetData();
                labelOldInfo.setText(String.format("%s -- %s -- %s", memberInfo[0], memberInfo[1], memberInfo[2]));
                fieldIP.setText(memberInfo[0]);
                fieldPIN.setText(memberInfo[1]);
                fieldName.setText(memberInfo[2]);
            } catch (SQLException ex) {
                Logger.getLogger(EditMemberController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (fieldIP != null) {
            addTextLimiter(fieldIP, 10);
        }
        if (fieldPIN != null) {
            addTextLimiter(fieldPIN, 10);
        }
    }

    @FXML
    private void editMember(ActionEvent event) {
        boolean correctInput = true;
        String inputIP = fieldIP.getText();
        String inputPIN = fieldPIN.getText();
        String inputName = fieldName.getText();

        if (!inputIP.startsWith("IP") || !isNumeric(inputIP.substring(2)) || inputIP.length() != 10) {
            correctInput = false;
            warningIP.setText("IP yang dimasukkan tidak valid" + "(" + inputIP + ")");
        }

        if (inputPIN.length() != 10 || inputPIN.isEmpty()) {
            correctInput = false;
            warningPIN.setText("PIN tidak valid");
        }

        if (inputName.isEmpty()) {
            inputName = "-";
        }

        try {
            if (checkMember(inputIP) && !inputIP.equals(memberInfo[0])) {
                correctInput = false;
                warningIP.setText(inputIP + " sudah digunakan.");
            }

            if (correctInput) {
                warningIP.setText("");
                warningPIN.setText("");

                if (d.editRecord(inputIP, inputPIN, inputName, userInput.getValue())) {
                    result.setText("PERUBAHAN BERHASIL!\n" + inputIP + " -- " + inputPIN + " -- " + inputName);
                } else {
                    result.setText("GAGAL MERUBAH INFO MEMBER");
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(EditMemberInfoController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
